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

### Who Cares?

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

### Reference Cells, Mutual Exclusion, Dwarven Berserkers

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

This little thought experiment demonstrates one of the central
challenges in concurrent programming. We'll call this the "reference
cell" problem. There are two other problems involved in concurrent
programming: mutual exclusion and the dwarven berserkers.

For the mutual exclusion problem, imagine two threads are each trying
to write the text of a different spell to a file. Without any way to claim
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
3. Comfort your neighbor with your "comfort sticks"
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
evaluated any other statements until the REPL was done sleeping; your
REPL would be blocked. However, `future` throws your call to
`Thread/sleep` into another thread, allowing the REPL's thread to
continue, unblocked.

Futures differ from the values you're used to, like hashes and maps,
in that you have to *dereference* them to obtain their value. You can
dereference a future with either the `deref` function or the short `@`
reader macro.

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

Sometimes you want to place aa time limit on how long to wait for a
future. To do that, you can pass the number of milliseconds to wait to
`deref` along with the value to return if the deref does time out:

```clojure
(deref (future (Thread/sleep 1000) 0)
       10
       5)
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

### Dereferencing

Dereferencing is easy to understand, but it implies some nuances that
aren't immediately obvious. It allows you more flexibility than is
possible with serial code. When you write serial code, you create
dependencies between:

* When a task is *defined*
* When a task is *evaluated*
* When you *require the result* of a task

Take this example code:

```clojure
(/ (web-api/get :total-sales)
   (web-api/get :total-customers))
```

In this code, you're communicating that the task `(web-api/get
:total-sales)` must be evaluated now and that you require the result
before evaluating the next task, `(web-api/get :total-customers)`.
Part of learning concurrent programming is learning to identify when
you've created these chronological dependencies that aren't actually
necessary.

Futures allow you to require the result of a task independently of
when it's defined and evaluated. Calling `future` defines the task and
indicates that it should start being evaluated immediately. It also
indicates that you don't need the result immediately.

When you dereference a future, you indicate that the result is
required *right now* and that evaluation should stop until the result
is obtained. You'll see how this can help you deal with the mutual
exclusion problem in just a little bit.

Alternatively, you can ignore the result. For example, you can use
futures to write to a log file asynchronously.

Clojure also allows you to treat "task definition" and "requiring the
result" independently with `delays` and `promises`.

### Delays

Delays allow you to decouple a task's definition from when it's
evaluated and when you require the result. You can create a delay with
`delay`. In this example, nothing is printed:

```clojure
(def print-delay (delay (println "I'll be here when you need me")))
```

You can evaluate the delay and get its result by dereferencing or
using `force`:

```clojure
(force print-delay)
; => "I'll be here when you need me"
; => nil
```

Like futures, a delay is only run once and its result is cached. The
return value of `println` is `nil`, so subsequent dereferencing will
return `nil` without printing anything:

```clojure
@print-delay
; => nil
```

TODO - use example of futures and delays where you are receiving data
in a nondeterministic order and want to handle the results in a
deterministic order

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

Because you are a modern practitioner of the freaking awesome arts,
you know that yak butter retailers now offer their catalogs online.
Rather than tediously navigate each site, you create a script to give
you the URL of the first yak butter offering that meets your needs.

Here's a simulation of what that script might look like. We'll
generate yak butter products to simulate the results you'd get from
making API calls:

```clojure
(def sites ["http://yak-butter-international.com"
            "http://butter-than-nothing.com"
            "http://yak-attack.com"
            "http://baby-got-yak.com"])

(defn generate-yak-butter
  "Yak butter with random smoothness and price for site"
  [id site]
  {:id id
   :smoothness (inc (int (rand 100)))
   :price (inc (int (rand 1000)))
   :url (str site "/products/" id)})

(defn yak-butter-generator
  "Create lazy list of random yak butters for site"
  [site]
  (iterate (fn [x] (generate-yak-butter (inc (:id x)) site))
           (generate-yak-butter 0 site)))

(defn yak-butters
  "Return a lazy list of yak butters for this site. Sleep 1 seconds to
  simulate network connection"
  [site n]
  (Thread/sleep 1000)
  (take n (yak-butter-generator site)))
```

`yak-butters` returns a lazy list of yak butters with random
smoothness ratings and prices. Here's an example of what it could
return:

```clojure
(yak-butters "http://yak-attack.com" 2)
; => returns:
({:id 0, :smoothness 93, :price 78, :url "http://yak-attack.com/products/0"}
 {:id 1, :smoothness 14, :price 263, :url "http://yak-attack.com/products/1"})
```

With a promise and futures, you can
perform a parallel search for a satisfactory yak butter and continue
execution as soon it's found:

```clojure
(defn satisfactory?
  [butter]
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))

(let [butter-promise (promise)]
  (doseq [site sites]
    (future (if-let [satisfactory-butter (some satisfactory? (yak-butters site 250000))]
              (deliver butter-promise satisfactory-butter))))
  (println "And the winner is:" @butter-promise))
```

The actual time this take takes to execute will vary since it
generates random data.


### Solving Problems

