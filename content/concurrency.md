---
title: Concurrency & Parallelism
link_title: Concurrency & Parallelism
kind: documentation
draft: true
---

One of Clojure's big selling points is its support for concurrent
programming. In this chapter, you'll learn the following:

* Intro to concurrency & parallelism
    * Definitions
    * Why should you care?
* Easy concurrent ops
* What are the dangers?
* The Clojure model of state
* Tool 1: Statelessness
* Tool 2: Atomic updates
* Tool 3: STM
* Tool 4: core.async

## Who Cares?

Why should you bother with any of this? The reason is that parallel
programming is essential for writing performant applications on modern
hardware. In recent years CPU designers have focused more on enabling
parallel execution than on increasing clock speed. This is mostly
because increasing clock speed requires exponentially more power,
making it impractical. You can google "cpu power wall", "cpu memory
wall" and "instruction-level parallelism wall" to find out more. As
those creepy Intel bunnies continue smooshing more and more cores
together, it's up to you and me to squueze performance out of them.

Performance can be measured as both **latency**, or the total amount of
time it takes to complete a task, and **throughput**, or the number of
tasks a program completes per second. Parallelism can improve both, as
this diagram shows:

TODO: diagram serial vs parallel execution of tasks

## Concurrency and Parallelism Defined

Concurrent and parallel programming involves a lot of messy details at
all levels of program execution, from the hardware to the operating
system to programming language libraries to the code that you're
typing out with your very own fingers. In this section I'll ignore
those details and instead focus on the implementation-independent
high-level concepts.

### Managing Tasks vs. Executing Tasks Simultaneously

**Concurrency** refers to *managing* more than one task at the same
time. "Task" just means "something that needs to get done." We can
illustrate concurrency with the song "Telephone" by Lady Gaga. Gaga
sings,

    I cannot text you with a drink in my hand, eh

Here, she's explaining that she can only manage one task (drinking).
She flat out rejects the suggestion that she manage more than one
task. However, if she decided to process tasks concurrently, she would
sing,

    I will put down this drink to text you,
    then put my phone away and continue drinking, eh

In this hypothetical universe, Lady Gaga is *managing* two tasks:
drinking and texting. However, she is not *executing* both tasks at
the same time. Instead, she's switching between the two. This is
called **interleaving**. Note that, when talking about interleaving,
you don't have to fully complete a task before switching; Gaga could
type one word, put her phone down, pick up her drink and have a sip,
then switch back to her phone and type another word.

**Parallelism** refers to *executing* more than one task at the same
time. If Madame Gaga were to execute her two tasks in parallel, she
would sing:

    I can text you with one hand
    while I use the other to drink, eh

Clojure has many features which allow you to easily achieve
parallelism. Whereas the Lady Gaga system achieves parallelism by
simultaneously executing tasks on multiple hands, computer systems
generally achieve parallelism by simultaneously executing tasks on
multiple cores or processors.

You can see from this definition that parallelism is a subclass of
concurrency: in order to execute multiple tasks simultaneously, you
first have to manage multiple tasks. Concurrency can be seen as
potential paralellism.

It's important to distinguish parallelism from **distribution**.
Distributed computing is a specialization of parallel computing where
the processors don't reside in the same computer and where tasks are
distributed to computers over a network. It'd be like Lady
Gaga asking Beyoncé, "Please text this guy while I drink."

I'm going to use "parallel" only to refer to cohabitating processors.
While there are Clojure libraries that aid distributed programming,
this book only covers parallel programming. That said, the concepts
you'll learn apply directly to distributed programming.

### Blocking and Async

One of the big use cases for concurrent programming is for
**blocking** operations. "Blocking" really just means waiting and
you'll most often hear it used in relation to I/O operations. Let's
examine this using the Concurrent Lady Gaga example.

If Lady Gaga texts her interlocutor and then stands there with her
phone in her hand, staring at the screen for a response, then you
could say that the "read next text message" operation is blocking. If,
instead, she tucks her phone away so that she can drink until it
alerts her by beeping or vibrating, then you could say she's handling
the "read next text message" operation **asynchronously**.


### Concurrent Programmin, Parallel Programming

"Concurrent programming" and "parallel programming" refer to how you
decompose a task into parallelizable sub-tasks and the techniques you
use to manage the kinds of risks that arise when your program executes
more than one task at the same time.

To better understand those risks and how Clojure help you avoid them,
let's examine how concurrency and parallelism are implemented in
Clojure.

## Clojure Implementation: JVM Threads

I've been using the term "task" to refer to *logical* sequences of
related operations. Texting consists of a series of related operations
separate from those involved in pouring a drink into your face, for
example. So, "task" is an abstract term which says nothing about
implementation.

In Clojure, you can think of your normal, serial code as a sequence of
tasks. You indicate that tasks can be performed concurrently by
placing them on **threads**.

### So What's a Thread?

I'm glad you asked! Rather than giving a technical definition of a
thread, though, I'm going to describe a useful mental model of how
threads work.

I think of a thread as an actual, physical piece of thread that's been
threaded through a sequence of instructions. In my mind, the
instructions are marshmallows, because marshmallows are delicious. The
processor executes these instructions in order. I envision this as an
alligator consuming the instructions, because alligators love
marshmallows (true fact!). So executing a program looks like a bunch
of marshmallows strung out on a line with a pacifist alligator
traveling down the line and eating them one by one.

Here's a single-core processor executing a single-threaded program:

TODO: add image here

A thread can "spawn" a new thread. In a single-processor system, the
processor switches back and forth between the threads (interleaving).
Here's where we things start to get tricky. While the processor will
execute the instructions on each thread in order, it makes no
guarantees about when it will switch back and forth between threads.

Here's an illustration of two threads, "A" and "B", along with a
timeline of how their instructions could be executed. I've shaded the
instructions on thread B to help distinguish them from the
instructions on thread A.

TODO: add image here

Note that this is just a *possible* ordering of instruction execution.
The processor could also have executed the instructions in the order,
"A1, A2, A3, B1, A4, B2, B3", for example. The main idea is that you
can't know what order the instructions will actually take. This makes
the program **nondeterministic**. You can't know beforehand what the
result will be because you can't know the execution order.

Whereas the above example showed concurrent execution on a single
processor through interleaving, a multi-core system will assign a
thread to each core. This allows the computer to execute more than one
thread simultaneously. Each core executes its thread's instructions in
order:

TODO: image

As with interleaving on a single core, there are no order guarantees
so the program is nondeterministic. To make things even more fun, your
programs will typically have more threads than cores, so each core
will likely perform interleaving on multiple threads.

### The Three Goblins: Reference Cells, Mutual Exclusion, Dwarven Berserkers

To drive this point home, imagine the program in the image above
includes the following pseudo instructions:

| ID | Instruction |
|----+-------------|
| A1 | WRITE X = 0 |
| A2 | READ X      |
| A3 | PRINT X     |
| B1 | WRITE X = 5 |

If the processor follows the order "A1, A2, A3, B1" then the program
will print the number `0`. If it follows the order "A1, B1, A2, A3",
then the program will print the number `5`. Even weirder, if the order
is "A1, A2, B1, A3" then the program will print `0` even though `X`'s
value is `5`.

This little thought experiment demonstrates one of the three central
challenges in concurrent programming, a.k.a "The Three Concurrency
Goblins." We'll call this the "reference cell" problem. There are two
other problems involved in concurrent programming: mutual exclusion
and the dwarven berserkers problem.

For mutual exclusion, imagine two threads are each trying to write the
text of a different spell to a file. Without any way to claim
exclusive write access to the file, the spell will end up garbled as
the write instructions get interleaved. These two spells:

    By the power invested in me
    by the state of California
    I now pronounce you man and wife

    Thunder, lightning, wind and rain,
    a delicious sandwich, I summon again

Could get written as this:

    By the power invested in me
    by Thunder, lightning, wind and rain,
    the state of California
    I now pronounce you a delicious man sandwich, and wife
    I summon again

Finally, the dwarven berserker problem can be stated as follows.
Imagine four berserkers are sitting around a rough-hewn, circular
wooden table and comforting each other. "I know I'm distant toward my
children, but I just don't know how to communicate with them," one
growls. The rest sip their coffees and nod knowingly, care lines
creasing their eyeplaces.

Now, as everyone knows, the dwarven berserker ritual for ending a
comforting coffee klatch is to pick up their "comfort sticks"
("double-bladed waraxes") and scratch each others' backs. One waraxe
is placed between each pair of dwarves, like so:

Their rituaal proceeds thusly:

1. Pick up the *left* waraxe, if available
2. Pick up the *right* waraxe, if available
3. Comfort your neighbor with vigorous swings of your "comfort sticks"
4. Release both waraxes
5. Go to 1

Following this ritual, it's entirely possible that all dwarven
berserkers will pick up their left comfort stick and then block
indefinitely waiting for the comfort stick to their right to become
available, resulting in **deadlock**.

The takeaway here is that concurrent programming has the potential to
be confusing and terrifying. But! With the right tools, it's
manageable and even fun. Let's start looking at the right tools.

## Futures, Delays, and Promises

Futures, delays, and promises are easy, lightweight tools for
concurrent programming. In this section, you'll learn how each works
and how to use them together to defend against the Reference Cell
Concurrency Goblin and the Mutual Exclusion Concurrency Goblin. You'll
discover that, while simple, these tools can go a long way toward
meeting your concurrency needs.

### Futures

In Clojure, you can use **futures** to place a task on another thread.
You can create a future with the `future` macro. Try this in a REPL:

```clojure
(future (Thread/sleep 4000)
        (println "I'll print after 4 seconds"))
(println "I'll print immediately")
```

`Thread/sleep` tells the current thread to just sit on its ass and do
nothing for the specified number of milliseconds. Normally, if you
evaluated `Thread/sleep` in your REPL you wouldn't be able to
evaluate any other statements until the REPL was done sleeping; your
REPL would be blocked. However, `future` throws your call to
`Thread/sleep` into another thread, allowing the REPL's thread to
continue, unblocked.

Futures differ from the values you're used to, like hashes and maps,
in that you have to *dereference* them to obtain their value. The
value of a future is the value of the last expression evaluated. You
can dereference a future with either the `deref` function or the `@`
reader macro. Try this out to use them both:

```clojure
(let [result (future (println "this prints once")
                     (+ 1 1))]
  (println "deref: " (deref result))
  (println "@: " @result))
; =>
; "this prints once"
; deref: 2
; @: 2
```

Notice that the the string `"this prints once"` indeed only prints
once, showing that the future's body runs only once and the result
gets cached.

Dereferencing a future will block if the future hasn't finished
running:

```clojure
(let [result (future (Thread/sleep 3000)
                     (+ 1 1))]
  (println "The result is: " @result)
  (println "It will be at least 3 seconds before I print"))
; =>
; The result is:  2
; It will be at least 3 seconds before I print
```

Sometimes you want to place a time limit on how long to wait for a
future. To do that, you can pass the number of milliseconds to wait to
`deref` along with the value to return if the deref does time out. In
this example, you tell `deref` to return the value `5` if the future
doesn't return a value within 10 milliseconds.

```clojure
(deref (future (Thread/sleep 1000) 0) 10 5)
; => 5
```

Finally, you can interrogate a future to see if it's done running with
`realized?`:

```clojure
(realized? (future (Thread/sleep 1000)))
; => false

(let [f (future)]
  @f
  (realized? f))
; => true
```

Futures are a dead simple way to sprikle some concurrency on your
program. On their own, they give you the power to chuck tasks onto
other threads, allowing you to increase performance. They don't
protect you against The Three Concurrency Goblins, but you'll learn
some ways to protect yourself with delays and promises in just a
minute. First, though, let's take a closer look at dereferencing.

### Dereferencing

Dereferencing is easy to understand, but it implies some nuances that
aren't immediately obvious. It allows you more flexibility than is
possible with serial code. When you write serial code, you bind
together these three events:

* Task definition
* Task execution
* Requiring the task's result

As an example, take this hypothetical code, which defines a simple api
call task:

```clojure
(web-api/get :dwarven-beard-waxes)
```

As soon as Clojure encounters this task definition, it executes it. It
also requires the result *right now*, blocking until the api call
finishes.

Futures, however, allow you to separate the "require the result" event
from the other two. Calling `future` defines the task and indicates
that it should start being evaluated immediately. *But*, it also
indicates that you don't need the result immediately. Part of learning
concurrent programming is learning to identify when you've created
these chronological couplings when they aren't actually necessary.

When you dereference a future, you indicate that the result is
required *right now* and that evaluation should stop until the result
is obtained. You'll see how this can help you deal with the mutual
exclusion problem in just a little bit.

Alternatively, you can ignore the result. For example, you can use
futures to write to a log file asynchronously.

This kind of flexibility is pretty cool. Clojure also allows you to
treat "task definition" and "requiring the result" independently with
`delays` and `promises`. Onward!

### Delays

Delays allow you to define a task definition without having to execute
it or require the result immediately. You can create a delay with
`delay`. In this example, nothing is printed:

```clojure
(def jackson-5-delay
  (delay (let [message "Just call my name and I'll be there"]
           (println "First deref:" message)
           message)))
```

You can evaluate the delay and get its result by dereferencing or
using `force`. `force` has the same effect as `deref`; it just
communicates more clearly that you're causing a task to start
as opposed to waiting for a task to finish.

```clojure
(force jackson-5-delay)
; => First deref: Just call my name and I'll be there
; => "Just call my name and I'll be there"
```

Like futures, a delay is only run once and its result is cached.
Subsequent dereferencing will return the Jackson 5 message without
printing anything:

```clojure
@jackson-5-delay
; => "Just call my name and I'll be there"
```

One way you can use a delay is to fire off a statement the first time
one future out of a group of related futures finishes. For example,
pretend your app uploads a set of headshots to a headshot-sharing site
and notifies the owner as soon as the first one is up, as in the
following:

```clojure
(let [notify (delay (email-user "and-my-axe@gmail.com"))]
  (doseq [headshot gimli-headshots]
    (future (upload-document headshot)
            @notify)))
```

In this example, Gimli will be grateful to know when the first
headshot is available so that he can begin tweaking it and sharing it.
He'll also appreciate not being spammed, and you'll appreciate not
facing his reaction to being spammed.

This technique can help protect you from the Mutual Exclusion
Concurrency Goblin. You can view a delay as a resource in the same way
that a printer is a resource. Since the body of a delay is guaranteed
to only ever fire once, you can be sure that only one thread will ever
have "access" to the "resource" at a time. Of course, no thread will
ever be able to access the resource ever again. That might be too
drastic a constraint for most situations, but in cases like the
example above it works perfectly.

### Promises

Promises allow you to express the expectation of a result indepedently
of the task that should produce it and when that task should run. You
create promises with `promise` and deliver a result to them with
`deliver`. You obtain the result by dereferencing:

```clojure
(def my-promise (promise))
(deliver my-promise (+ 1 2))
@my-promise

; => 3
```

You can only deliver a result to a promise once. Just like with
futures and delays, dereferencing a promise will block until a result
is available.

One potential use for promises is to find the first satisfactory
element in a collection of data. Suppose, for example, that you're
gathering ingredients for a spell to summon a parrot that repeats what
people say, but in James Earl Jones's voice. Because James Earl Jones
has the smoothest voice on earth, one of the ingredients is premium
yak butter with a smoothness rating of 97 or greater. Because you
haven't yet summoned a tree that grows money, you have a budget of
$100 for one pound (or 0.45kg if you're a European sorcerer).

Because you are a modern practitioner of the magico-ornithological
arts, you know that yak butter retailers now offer their catalogs
online. Rather than tediously navigate each site, you create a script
to give you the URL of the first yak butter offering that meets your
needs.

In this scenario, the collection of data is the yak butter options
provided by each store. You can model that by defining some yak butter
products, creating a function to mock out an API call, and creating
a function to test whether a product is satisfactory:

```clojure
(def yak-butter-international
  {:store "Yak Butter International"
    :price 90
    :smoothness 90})
(def butter-than-nothing
  {:store "Butter than Nothing"
   :price 150
   :smoothness 83})
;; This is the butter that meets our requirements
(def baby-got-yak
  {:store "Baby Got Yak"
   :price 94
   :smoothness 99})

(defn mock-api-call
  [result]
  (Thread/sleep 1000)
  result)

(defn satisfactory?
  [butter]
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))
```

The API call waits 1 second before returning a result to simulate the
time it would take to perform an actual call.

If you check each site serially, it could take more than 3 seconds to
obtain a result, as the following code shows. Note that `time` prints
the time taken to evaluate a form and `comp` composes functions:

```clojure
(time (some (comp satisfactory? mock-api-call)
            [yak-butter-international butter-than-nothing baby-got-yak]))
; => "Elapsed time: 3002.132 msecs"
; => {:store "Baby Got Yak", :smoothness 99, :price 94}
```

You can use a promise and futures to perform each check on a separate
thread. If your computer has multiple cores, you could cut down the
time taken to about 1 second:

```clojure
(time
 (let [butter-promise (promise)]
   (doseq [butter [yak-butter-international butter-than-nothing baby-got-yak]]
     (future (if-let [satisfactory-butter (satisfactory? (mock-api-call butter))]
               (deliver butter-promise satisfactory-butter))))
   (println "And the winner is:" @butter-promise)))
; => "Elapsed time: 1002.652 msecs"
; => And the winner is: {:store Baby Got Yak, :smoothness 99, :price 94}
```

By decoupling the requirement for a result from how the result is
actually computed, you can perform multiple computations in parallel
and save yourself some time.

You can view this as a way to protect yourself from the Reference Cell
Concurrency Goblin. Since promises can only be written to once, you're
can't create the kind of inconsistent state that arises from
nondeterministic reads and writes.

The last thing I should mention is that you can also use promises to
register callbacks, achieving the same functionality that you might be
used to in JavaScript. Here's how to do it:

```clojure
(let [ferengi-wisdom-promise (promise)]
  (future (println "Here's some Ferengi wisdom:" @ferengi-wisdom-promise))
  (Thread/sleep 100)
  (deliver ferengi-wisdom-promise "Whisper your way to success."))
; => Here's some Ferengi wisdom: Whisper your way to success.
```

In this example, you're creating a future that executes immediately.
However, it's blocking because it's waiting for a value to be
delivered to `ferengi-wisdom-promise`. After 100 milliseconds, you
deliver the value and the `println` statement in the future runs.

Futures, delays, and promises are great, simple ways to manage
concurrency in your application. In the next section, we'll look at
one more fun way to keep your concurrent applications under control.

### Simple Queueing

So far you've looked at some simple ways to combine futures, delays,
and promises to make your concurrent programs a little safer. In this
section, you'll use a macro to combine futures and promises in a
slightly more complex manner. You might not necessarily ever use this
code, but it'll show the power of these simple tools a bit more.

Sometimes the best way to handle concurrent tasks is to re-serialize
them. You can do that by placing your tasks onto a queue. In this
example, you'll make API calls to pull random quotes from
[I Heart Quotes](http://www.iheartquotes.com/) and write them to your
own quote library. For this process, you want to allow the API calls
to happen concurrently but you want to serialize the writes so that
none of the quotes get garbled, like the spell example above.

Here's the un-queued quote for retrieving a random quote and writing
it:

```clojure
(defn append-to-file
  [filename s]
  (spit filename s :append true))

(defn format-quote
  [quote]
  (str "=== BEGIN QUOTE ===\n" quote "=== END QUOTE ===\n\n"))

(defn snag-quotes
  [n filename]
  (dotimes [_ n]
    (->> (slurp "http://www.iheartquotes.com/api/v1/random")
         format-quote
         (append-to-file filename)
         (future))))
```

If you call `(snag-quotes 2 "quotes.txt")` you'll end up with a file
that contains something like:

```
=== BEGIN QUOTE ===
Leela: You buy one pound of underwear and you're on their list forever.

[futurama] http://iheartquotes.com/fortune/show/1463
=== END QUOTE ===

=== BEGIN QUOTE ===
It takes about 12 ears of corn to make a tablespoon of corn oil.

[codehappy] http://iheartquotes.com/fortune/show/39667
=== END QUOTE ===
```

Most likely you got lucky like I did and your quotes weren't
interleaved. To ensure that you'll always be this lucky, you can use
this macro:

```clojure
(defmacro queue
  [q concurrent-promise-name & work]
  (let [concurrent (butlast work)
        serialized (last work)]
    `(let [~concurrent-promise-name (promise)]
       (future (deliver ~concurrent-promise-name (do ~@concurrent)))
       (deref ~q)
       ~serialized
       ~concurrent-promise-name)))
```

`queue` works by splitting a task into a concurrent and serialized
portion. It creates a future, which is what allows the concurrent
portion to run concurrently. You can see this with `(future (deliver
~concurrent-promise-name (do ~@concurrent)))`. The next line, `(deref
~q)`, blocks the thread until `q` is done, preventing the serialized
portion from running until the previous job in the queue is done.
Finally, the macro returns a promise which can then be used in another
call to `queue`.

To demonstrate that this works, you're going to pay homage to the
British, since they invented queues. you'll use a queue to ensure that
the customary British greeting, "'Ello, gov'na! Pip pip! Cheerio!" is
delivered in the correct order. This demonstration is going to involve
an abundance of `sleep`ing, so here's a macro to do that more
concisely:

```clojure
(defmacro wait
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))
```

Now here's an example of the greeting being delivered in the wrong
order:

```clojure
(future (wait 200 (println "'Ello, gov'na!")))
(future (wait 400 (println "Pip pip!")))
(future (wait 100 (println "Cheerio!")))

; => Cheerio!
; => 'Ello, gov'na!
; => Pip pip!
```

This is the wrong greeting completely, though no British person would
be so impolite as to correct you. Here's how you can `queue` it so as
not to embarrass yourself:

```clojure
(time @(-> (future (wait 200 (println "'Ello, gov'na!")))
           (queue line (wait 400 "Pip pip!") (println @line))
           (queue line (wait 100 "Cheerio!") (println @line))))
; => 'Ello, gov'na!
; => Pip pip!
; => Cheerio!
; => "Elapsed time: 401.635 msecs"
```

Blimey! The greeting is delivered in the correct order, and you can
see by the elapsed time that the "work" of sleeping was shunted onto
separate cores.

Now let's use this to read from "I Heart Quotes" concurrently while
writing serially. Here's the final product. I'll walk through it below.

```clojure
(defn append-to-file
  [filename s]
  (spit filename s :append true))

(defn format-quote
  [quote]
  (str "=== BEGIN QUOTE ===\n" quote "=== END QUOTE ===\n\n"))

(defn random-quote
  []
  (format-quote (slurp "http://www.iheartquotes.com/api/v1/random")))

(defmacro snag-quotes-queued
  [n filename]
  (let [quote-gensym (gensym)
        queue-line `(queue ~quote-gensym
                           (random-quote)
                           (append-to-file ~filename @~quote-gensym))]
    `(-> (future)
         ~@(take n (repeat queue-line)))))

```

`append-to-file` and `format-quote` are the same as above, they're
just shown here again so you don't have to flip back  and forth.
`random-quote` simply grabs a quote and formats it.

The interesting bits are in `snag-quotes-queued`. The point of the
macro is to returns a form something along the lines of:

```clojure
(snag-quotes-queued 4 "quotes.txt")
; => expands to:
(-> (future)
    (queue G__627 (random-quote) (append-to-file "quotes.txt" @G__627))
    (queue G__627 (random-quote) (append-to-file "quotes.txt" @G__627))
    (queue G__627 (random-quote) (append-to-file "quotes.txt" @G__627))
    (queue G__627 (random-quote) (append-to-file "quotes.txt" @G__627)))
```

There are a couple things going on here. You have to "seed" the queue
with a no-op future because `queue` expects a dereferenceable object
as its first argument. Then, you just repeat instances of `(queue
G__627 (random-quote) (append-to-file "quotes.txt" @G__627))`. This
mirrors the way the British example above uses `queue`.

And that's it for futures, delays, and promises! This section has
shown how you can combine them together to make your concurrent
program safer.

In the rest of the chapter, you'll dive deeper into Clojure's
philosophy and discover how the language's design enables even more
power concurrency tools.

## Escaping The Pit of Evil

Literature on concurrency generally agrees that the Three Concurrency
Goblins are all spawned from the same pit of evil: shared access to
mutable state. You can see this in the Reference Cell discussion
above: when two threads make uncoordinated changes to the reference
cell the result is unpredictable.

Rich Hickey designed Clojure so that it would specifically address the
problems which arise from shared access to mutable state. In fact,
Clojure embodies a very clear conception of state that makes it
inherently safe for concurrency.

You'll learn this philosophy in this section. To fully clarify it,
I'll compare it to the philosophy embodied by object-oriented
languages. By learning this philosophy, you'll be fully equipped to
handle Clojure's remaining concurrency tools.

By the way, this section is heavily inspired by Rich Hickey's talks,
especially "Are We There Yet?"

### Metaphysics, Programming, and You: Comparing OOP and FP

The concept of "state" is inextricably related to the concepts of
"value", "identity", "time", and "behavior". In order to understand
state, you'll need to understand these concepts as well.

In object-oriented programming languages, these concepts aren't
well-defined, and the result is a fertile breeding ground for the
Three Concurrency Goblins. By contrast, Clojure was designed with
clear and explicit definitions of these concepts.

To better understand the differences, let's compare the models of
reality which underlie OOP and Clojure. It's this underlying
difference which gives rise to their different approach to the
concepts above.

When talking about metaphysics things tend to get a little fuzzy, but
hopefully this will all make sense.

As the ever-trusty
[Wikipedia](http://en.wikipedia.org/wiki/Metaphysics) explains,
metaphysics attempts to answer two basic questions in the broadest
possible terms:

* What is there?
* What is it like?

To draw out the differences, let's go over two different ways of
modeling a cuddle zombie. Unlike a regular zombie, a cuddle zombie
does not want to devour your brains. It wants only to spoon you and
maybe smell your neck. That makes its undead, decaying state all the
more tragic.

#### Object-Oriented Metaphysics

In OO metaphysics, a cuddle zombie is something which actually exists
in the world. I know, I know, I can hear what you're saying: "Uh...
yeah? So?" But believe me, the accuracy of that statement has caused
many a sleepless night for philosophers.

The wrinkle is that the cuddle zombie is always changing. Its
unceasing hunger for cuddles grows fiercer with time. Its body slowly
deteriorates. In OO terms, we would say that the cuddle zombie is an
object with mutable state, and that its state is ever fluctuating. No
matter how much the zombie changes, we still identify it as the same
zombie.

The fact that the state of the Cuddle Zombie Object and that Objects
in general are never stable doesn't stop us from nevertheless treating
them as the fundamental building blocks of programs. In fact, this is
seen as an advantage of OOP. It doesn't matter how the state changes,
you can still interact with a stable interface and all will work as it
should.

This conforms to our intuitive sense of the world. It doesn't matter
how many fingers Undead Fred has left; curling up on the couch with
him will still illicit the same soft coos of contentment.

Finally, in OOP, objects do things. They act on each other. Again,
this conforms to our intuitive sense of the world: change is the
result of objects acting upon each other. A Person object pushes on a
Door object and enters a House object.

You can visualize this as a box that only holds one thing at a time.
The box is an object, and you change its state by replacing its
contents.

#### Functional Programming

In FP metaphysics, we would say that we never encounter the same
cuddle zombie twice. What we see as a discrete _thing_ which actually
exists in the world independent of its mutations is in reality a
succession of discrete, unchanging things.

The zombie is not a thing in and of itself; it's a concept that we
superimpose on a succession of related phenomena. This concept is very
useful - I won't belabor that point - but it is just a concept.
Identity is not inherent; it is an endowment.

What really exists are atoms (in the sense of atomic, unchanging,
stable entities) and processes. The zombie is not an object which
exists independently of the states flowing through it. Rather, we
identify a succession of related atoms which are generated by some
kind of process as a zombie.

These atoms don't act upon each other and they can't be changed. They
can't _do_ anything. Change is not the result of one object acting on
another. What we call "change" results when a) a process generates a new
atom and b) we choose to associate the identity with the new atom.

With this broad characterization under our belts, let's look more
thoroughly at the concepts of value, identity, state, time, and
behavior.

### Value

It's obvious that numbers like 3 and 6 and 42 are values. Numbers are
stable, unchanging. They're immutable. Thus, programming with values
is inherently safe. There's no "shared access to mutable state" if
you're dealing with values.

Object-oriented languages have little support for values in this
sense. You can create a class whose instances are composed of
immutable components, but there is no high-level concept of immutable
value implemented as a first class construct within the class.

By contrast, Clojure emphasizes working with immutable values. All the
built-in data structures are immutable. In the metaphysics discussion
above, "values" correspond to "atoms".

### Identity

In Clojure, identity is essentially a name we give to a sequence of
related values. "Cuddle Zombie" refers to the sequence CZ1, CZ2, CZ3,
etc produced by the "" process.

> Identity is a putative entity we associate with a series of causally
> related values (states) over time. It's a label, a construct we use
> to collect a time series.

In OO, identity and state are mashed together. The name refers to
right now's value, and all previous values are discarded.

### State

In OO, there is no real clear definition of state. Maybe it's, "the
values of all the attributes within an object right now." And it has
to be "right now", because there's no language-supported way of
holding on to the past.

This becomes clearer if you contrast it with the notion of identity in
FP. In the Hickeysian universe, a State is a specific value for an
identity at a point in time. (For me, this definition really clarified
my own thoughts.)

### Time

There's no real notion of time in OO. Everything's just "right now".
This is part of what causes problems in concurrent programs.

By contrast, in the FP worldview we've been exploring, time is
well-defined. Time is a by-product of ordering states within an
identity.

(By the way, I'd really like to write more here, and would appreciate
any suggestions.)

### Behavior

Behavior is changing the value associated with a name. In FP, it's
adding a new brick and making the name point to the new brick. In OOP,
it's placing a new thing in the box.

### Why Does This Matter?

## Make it Stateless

## Atoms

## 
