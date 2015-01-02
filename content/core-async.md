---
title: Concurrent Processes with core.async
link_title: Concurrent Processes with core.async
kind: documentation
draft: true
---

One day, while you are walking down the street, you will be surprised,
intrigued, and a little disgusted to discover a hot dog vending
machine. Your scalp tingling with guilty curiosity, you won't be able
to help yourself from pulling out three Sacagawea dollars and seeing
if this contraption actually works. After accepting your money, it
will emit a click and a whirr, and out will pop a fresh hot dog, bun
and all.

The vending machine exhibits simple behavior: when it receives money,
it releases a hot dog, then readies itself for the next purchase. When
it's out of hot dogs, it stops. All around us are hot dog vending
machines in different guises, independent entities concurrently
responding to events in the world according to their nature. The
espresso machine at your favorite coffee shop, the pet hamster you
loved as a child - everything can be modeled in terms of behavior
using the general form, "when *x* happens, do *y*." Even the programs
we write are just glorified hot dog vending machines, each one an
independent process waiting for the next event, whether it's a
keystroke, a timeout, or the arrival of data on a socket.

Clojure's core.async library allows you to create independent
processes within a single program. This chapter describes a useful
model for thinking about this style of programming as well as the
practical details you need to actually write stuff. You'll learn how
to use channels, alts, and go blocks to create independent processes
and communicate between them, and you'll learn a bit about how Clojure
uses threads and something called "parking" to allow this all to
happen efficiently.

## Processes

At the heart of core.async is the *process*, a concurrently running
unit of logic that responds to events. The process concept is meant to
capture our mental model of the real world, with entities interacting
with and responding to each other completely independently, without
some kind of central control mechanism. You put your money in the
machine and out comes a hotdog, all without the Illuminati or Big
Brother orchestrating the whole thing. This differs from the view of
concurrency you've been exploring so far, where you've defined tasks
that are either mere extensions of the main thread of control (for
example, achieving data parallelism with `pmap`) or that you have no
interest in communicating with (`pmap` again and one-off tasks created
with `future`).

It might be strange to consider a vending machine to be a process:
vending machines are noun-y and thing-y, and processes are verb-y and
do-y. To get yourself in the right mindset, try defining real-world
objects as the sum of their event-driven behavior. When a seed gets
watered, it sprouts; when a mother looks at her new-born child, she is
flooded with oxytocin and feels love; when you watch Star Wars Episode
I, you are filled with a deep anger and despair. If you want to get
super philosophical, consider whether it's possible to define every
thing's essence as the set of the events it recognizes and its
responses. Is reality just the composition of hot dog vending
machines?

Anyway, enough of my yakking! Let's make these ideas real by creating
some simple processes. First, create a new Leiningen project called
"playsync" with `lein new playsync`. Then, open the file `project.clj`
and add core.async to the `:dependencies` vector so that it reads:

```clojure
[[org.clojure/clojure "1.6.0"]
 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
```

It's likely that the core.async version has advanced since I wrote
this; you can check on
[the core.async github project page](https://github.com/clojure/core.async/)
for the latest version.

Now that that's done, open up `src/playsync/core.clj` and change it so
that it reads:

```clojure
(ns playsync.core
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))
```

Great! Now when you open this in a REPL, you'll have the most
frequently-used core.async functions at your disposal. Before creating
something as sophisticated and revolutionary as a hot dog vending
machine, let's create a process that simply prints the message it
receives:

```clojure
(def echo-chan (chan))
(go (println (<! echo-chan)))
(>!! echo-chan "ketchup")
; => true
; => ketchup
```

Here, you created a *channel* named `echo-chan` with the `chan`
function. Channels communicate *messages*. Communication over a
channel is an event, and these communication events are what processes
respond to. In the physical world, we don't normally think about
channels or any other mechanism for conveying messages, and we don't
think about that communication as an event. You don't think of the
mechanical hot dog maker as listening on a channel, waiting for the
communication event that includes the "sufficient money" message. You
don't think about a person listening on a channel, waiting for the
communication event that include the "hot dog dispensed"
message. Still, these messages *are* communicated through the physical
media of light, sound, and grease odor molecules, and that
communication is an event that things respond to.

With core.async, the channel is your virtual medium for communicating
messages. You can *put* messages on a channel and *take* messages off
of them. Processes wait for the completion of "put" and "take" -
they're the events that processes respond to.

Next, you used `go` to create a new process. Everything within the go
expression - called a *go block* - will run concurrently on a separate
thread. (Go blocks actually use a thread pool, a detail that I'll
cover in a little bit.) In this case, the process `(println (<!
echo-chan))` expresses, "when I take a message from `echo-chan`, print
it." The process is shunted to another thread, freeing up the current
thread and allowing you to continue interacting with the REPL.

The expression `(<! echo-chan)` is how you express "wait until I take
message from `echo-chan`." `<!` is the *take* function. It listens to
the channel you give it as an argument, causing the process to wait
until another process puts a message on the channel. You probably
noticed that `ketchup` didn't print in your REPL until you put it on
the channel. When `<!` does retrieve a value, the value is returned
and the `println` expression is executed.

The expression `(>!! echo-chan "ketchup")` *puts* the string
`"ketchup"` on `echo-chan` and returns `true` When you put a message
on a channel, the process will block until another process takes the
message. In this case, the REPL process didn't have to wait at all
because there was already a process listening to the channel, waiting
to take something off of it. However, if you do this:

```clojure
(>!! (chan) "mustard")
```

then your REPL will block indefinitely. You've created a new channel
and put something on it, but it's impossible for anything else to take
from it. So, processes don't just wait to receive messages, they also
wait for the messages they put on a channel to be taken.

(By the way, if you don't think that ketchup is event-worthy, then
stop reading this immediately and go to Veggie Galaxy in Cambridge,
Massachusetts, because holy cow their ketchup is *delicious*. I think
it's seasoned with hippie love. Wait... I'm not sure that sounds as
good as I thought it would.)

It's worth noting that there were *two* processes for this exercise:
the one you created with `go` and the REPL process. These processes
don't have explicit knowledge of each other, and they act
independently. The REPL process could refrain from ketchuping on
`echo-chan` and the go block could live on forever, waiting for
something to happen. Alternatively, the REPL process could have
ketchuped on `echo-chan` without knowing whether another process would
be there to handle the event.

I picture this all taking place in a diner. The REPL is the ketchup
chef, and when he's done with a batch he belts out, "Ketchup!" It's
entirely possible that the rest of the staff is outside admiring the
latest batch of "hippie oregano" in their "organic garden", and the
chef just sits there waiting until someone shows up to take his
ketchup. On the flip side, the go process is one of those staff
members, and he's waiting patiently for something to respond to. It
could be that nothing ever happens, and he just waits indefinitely
until the restaurant closes.

This situation seems a little pathological: what self-respecting
ketchup chef would just sit there waiting for someone to take his
latest batch when he could return to making more ketchup? To avoid
this tragic situation, you can create buffered channels:

```clojure
(def echo-buffer (chan 2))
(>!! echo-buffer "ketchup")
; => true
(>!! echo-buffer "ketchup")
; => true
(>!! echo-buffer "ketchup")
; blocks
```

In this case, you've created a channel with buffer size 2. That means
that you can put two values on the channel without waiting, but
putting a third one will wait until another process takes a value
from the channel. You can also create "sliding" buffers with
`sliding-buffer`, which drops values in a first-in-first-out fashion
as you continue adding values, and "dropping" buffers with
`dropping-buffer`, which discards values in a last-in-first-out
fashion. Neither of these buffers will ever cause a put to block.

Buffers, though, are just elaborations of the core model: processes
are independent, concurrently executing units of logic that respond to
events. You can create processes with go blocks and communicate events
over channels. It's time to expand on this model, starting with go
blocks.

## Go Blocks, thread, Blocking, and Parking

You may have noticed that the take function, `<!`, used only one
exclamation point, while the put function, `>!!` used two. In fact,
both put and take have a one-exclamation-point and
two-exclamation-point variety. When do you use which? The simple
answer is that you can use one exclamation point inside go blocks, but
you have to use two exclamation points outside:

|      | inside go block | outside |
|------|-----------------+---------|
| put  | `>!` or `>!!`   | `>!!`   |
| take | `<!` or `<!!`   | `<!!`   |

...but that doesn't really explain anything. What exactly is a go
block, and why can you use the one-exclamation varieties inside it?

It all comes down to efficiency. Go blocks allow you to run your
processes on a fixed-size thread pool (containing a number of threads
equal to two plus the number of cores on your machine) instead of
having to create a new thread for each process. This often results in
better performance because you avoid the overhead associated with
creating a new thread. For example, in the next example we create 1000
go processes, but only a handful of threads are used:

```clojure
(def hi-chan (chan))
(doseq [n (range 1000)]
  (go (>! hi-chan (str "hi " n))))
```

Clojure is able to accomplish this feat with threads by giving special
care to the way they *wait*. We've already established that "put"
waits until another process does a "take" on the same channel, and
vice versa. In the example above, there are 1000 processes all waiting
to take from `hi-chan`.

There are two varieties of waiting: *parking* and
*blocking*. *Blocking* is the kind of waiting you're familiar with: a
thread stops execution until a task is complete. Usually this happens
when you're performing some kind of I/O. This kind of waiting keeps
the thread alive, doing no work, so that if you want your program to
continue doing work you have to create a new thread. In the last
chapter, you learned how to do this with `future`.

*Parking* moves the waiting task off the thread, freeing up the thread
to do the work of processes that aren't waiting. Clojure's smart
enough to move the parked process back on to a thread as soon its put
or take is done. It's like parking allows interleaving on a single
thread, similar to the way that using multiple threads allows
interleaving on a single core:

![Parking and Blocking](/images/core-async/parking.png)

The implementation of parking isn't important; suffice to say that
it's only possible within go blocks, and it's only possible when you
use `>!` and `<!`, or *parking put* and *parking take*. As you've no
doubt guessed, `>!!` and `<!!` are *blocking put* and *blocking take*.

There are definitely times when you should prefer blocking over
parking, like when your process will take a long time before putting
or taking, and for those occasions you should use `thread`:

```clojure
(thread (println (<!! echo-chan)))
(>!! echo-chan "mustard")
; => true
; => mustard
```

`thread` acts almost exactly like `future`: it creates a new thread,
executing a process on that thread. However, instead of returning an
object which you can dereference, `thread` returns a channel. When
`thread`'s process stops, the process's return value is put on the
channel that `thread` returns:

```clojure
(let [t (thread "chili")]
  (<!! t))
; => "chili"
```

In this case, the process doesn't wait for any events, instead
stopping immediately. Its return value is `"chili"`, which gets put on
the channel that's bound to `t`. We take from `t`, returning
`"chili"`.

The reason you should use `thread` instead of a go block when you're
performing a long-running task is so that you don't "clog up" your
thread pool. Imagine you're running four processes which download
humongous files, saves them, and then puts the file paths on a
channel. While the processes are downloading files and saving them,
Clojure can't park their threads. It can only park the thread at the
last step, when the process puts the files' paths on a
channel. Therefore, if your thread pool only has four threads, then
all four threads will be used for downloading, and no other process
will be allowed to run until one of the downloads finishes.

`go`, `thread`, `chan`, `<!`, `<!!`, `>!`, and `>!!` are the core
tools you'll use for creating and communicating with processes. Both
put and take will cause a process to wait until its complement is
performed on the given channel. `go` allows you to use the parking
variants of put and take, which could improve your
performance. The blocking variants should be used, along with
`thread`, if you're performing some long-running task before the put
or take.

And that should give us everything we need to fulfill our hearts'
desire and create a machine that turns money into hot dogs.

## The Hot Dog Process You've Been Longing For

Behold, your dreams made real!

```clojure
(defn hotdog-machine
  []
  (let [in (chan)
        out (chan)]
    (go (<! in)
        (>! out "hotdog"))
    [in out]))
```

This function creates an `in` channel for receiving money and an `out`
channel for dispensing a hotdog. It then creates an asynchronous
process with `go` which waits for money, and then pops out a
hotdog. Finally, it returns the `in` and `out` channels. Time for a
hotdog!

```clojure
(let [[in out] (hotdog-machine)]
  (>!! in "pocket lint")
  (<!! out))
; => "hotdog"
```

Wait a minute... that's not right. I mean, yay, free hotdogs, but
someone's bound to get upset that the machine's getting paid in pocket
lint. Not only that, this machine will only dispense one hot dog
before shutting down. We can do better than that:

```clojure
(defn hotdog-machine-v2
  [hotdog-count]
  (let [in (chan)
        out (chan)]
    (go (loop [hc hotdog-count]
          (if (> hc 0)
            (let [input (<! in)]
              (if (= 3 input)
                (do (>! out "hotdog")
                    (recur (dec hc)))
                (do (>! out "wilted lettuce")
                    (recur hc))))
            (do (close! in)
                (close! out)))))
    [in out]))
```

There's a lot more code here, but the strategy is straightforward. The
new function `hotdog-machine-v2` allows you to specify the
`hotdog-count`. Within the go block, it only dispenses a hotdog if the
number 3 is placed on the `in` channel, otherwise it dispenses wilted
lettuce, which is the opposite of a hotdog. Once a process has taken
the "output", whether it's lettuce or a hotdog, the process loops back
with an updated hotdog count, ready to receive money again.

Once it's out of hotdogs, it *closes* the channels. When you close a
channel, you can no longer perform puts on it, and once you've taken
all values off a closed channel subsequent takes will return `nil`.

Let's give it a go:

```clojure
(let [[in out] (hotdog-machine-v2 2)]
  (>!! in "pocket lint")
  (println (<!! out))

  (>!! in 3)
  (println (<!! out))

  (>!! in 3)
  (println (<!! out))

  (>!! in 3)
  (<!! out))
; => wilted lettuce
; => hotdog
; => hotdog
; => nil
```

First, we try the ol' pocket lint trick and get wilted lettuce. Then
we put in 3 dollars twice and get a hot dog both times. Finally, we
try to put in another 3 dollars, but that's ignored because the the
channel is closed. When we try to take something out we get `nil`,
again because the channel is closed.

There are a couple interesting things about this hotdog
machine. First, it both does both a put and a take within the same go
block. This isn't that unusual, and it's one way that you can create a
pipeline of processes: just make the "in" channel of one process the
"out" channel of another. The next example does just that, passing a
string through a series of processes that perform transformations
until the string finally gets printed by the last process:

```clojure
(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  (go (>! c2 (clojure.string/upper-case (<! c1))))
  (go (>! c3 (clojure.string/reverse (<! c2))))
  (go (println (<! c3)))
  (>!! c1 "redrum"))
; => MURDER
```

I'll have more to say about process pipelines and how they can be used
instead of callbacks toward the end of the chapter. The second cool
thing about the hot dog machine, though, is that the machine doesn't
accept more money until you've dealt with whatever it's
dispensed. This allows you to model state-machine-like behavior, where
the completion of channel operations trigger state transitions. For
example, you can think of the vending machine as having two states,
"ready to receive money" and "dispensed item," with the inserting of
money and taking of the item triggering transitions between the two.

## Choice

The core.async function `alts!!` lets you use the result of the first
successful channel operation among a collection of operations. We did
something similar do this in the previous chapter with delays and
futures. In that example, we uploaded a set of headshots to a
headshot-sharing site and notified the headhot owner when the first
photo was uploaded. Here's how you'd do the same thing with `alts!!`:

```clojure
(defn upload
  [headshot c]
  (go (Thread/sleep (rand 100))
      (>! c headshot)))

(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  (upload "serious.jpg" c1)
  (upload "fun.jpg" c2)
  (upload "sassy.jpg" c3)
  (let [[headshot channel] (alts!! [c1 c2 c3])]
    (println "Sending headshot notification for" headshot)))
; => Sending headshot notification for sassy.jpg
```

Here, the `upload` function is pretty straightforward: it takes a
headshot and a channel and creates a new process which sleeps for a
random amount of time (simulating the upload) and then puts the
headshot on the channel. The next few lines should make sense: we
create three channels, then use them to perform the uploads.

The next part is where it gets interesting. The `alts!!` takes a
vector of channels as its argument. This is like saying, "Try to do a
blocking take on each of these channels simultaneously. As soon as a
take succeeds, return a vector whose first element is the value taken
and whose second element is the winning channel. Consign the remaining
channels to the dust heap of history." In this case, the channel
associated with "sassy.jpg" received a value first.

One cool thing about `alts!!` is that you can give it a *timeout
channel*. A timeout channel is a channel which waits the specified
number of milliseconds, then closes, and it's an elegant mechanism for
putting a time limit on concurrent operations. Here's how you could
use it with the upload service:

```clojure
(let [c1 (chan)]
  (upload "serious.jpg" c1)
  (let [[headshot channel] (alts!! [c1 (timeout 20)])]
    (if headshot
      (println "Sending headshot notification for" headshot)
      (println "Timed out!"))))
; => Timed out!
```

In this case, we set the timeout to 20 milliseconds. The "upload"
didn't finish in that timeframe and we got a timeout message.

You can also use `alts!!` to specify "put" operations. To do that, put
a vector inside the vector you pass to `alts`, like this:

```
(let [c1 (chan)
      c2 (chan)]
  (go (<! c2))
  (let [[value channel] (alts!! [c1 [c2 "put!"]])]
    (println value)
    (= channel c2)))
; => true
; => true
```

In this example, you're creating two channels and then creating a
process that's waiting to perform a take on `c2`. The vector that you
supply to `alts!!` tells it, "Try to do a take on `c1` and try to put
`"put!"` on `c2`. If the take on `c1` finishes first, return its value
and channel. If the put on `c2` finishes first, return `true` if the
put was successful (the channel was open) and `false` otherwise."
Finally, you print the result of `value` (`true`, because the `c2`
channel was open) and show that the channel returned was indeed `c2`.

Like `<!!` and `>!!`, `alts!!` has a parking alternative, `alts!`,
that you can use in go blocks.

## Queues

In the last chapter, we wrote a macro that let us queue our
futures. Processes let us do something similar in a more
straightforward manner. Here's how you'd snag quotes and write them to
a file without having to worry about quotes being interleaved:

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

(defn snag-quotes
  [filename num-quotes]
  (let [c (chan)]
    (go (while true (append-to-file filename (<! c))))
    (dotimes [n num-quotes] (go (>! c (random-quote))))))
```

The functions `append-to-file`, `format-quote`, and `random-quote` are
all lifted from the last chapter's example. `snag-quotes` is where the
interesting work is happening. First, it creates a channel to be
shared between the quote producing processes and the quote consuming
process. Then, it creates a process which uses `while true` to create
an infinite loop. On every iteration of the loop, it waits for a quote
to arrive on `c` and then appends it to a file. Finally, `snag-quotes`
creates an `num-quotes` number of processes that fetch a quote and
then put it on `c`. If you evaluate `(snag-quotes "/tmp/quotes" 2)`
and check `/tmp/quotes`, it should have two quotes:

```
=== BEGIN QUOTE ===
Nobody's gonna believe that computers are intelligent until they start
coming in late and lying about it.

[codehappy] http://iheartquotes.com/fortune/show/23605
=== END QUOTE ===

=== BEGIN QUOTE ===
Give your child mental blocks for Christmas.

[fortune] http://iheartquotes.com/fortune/show/47398
=== END QUOTE ===
```

This kind of queueing differs from the example in the last chapter in
that that example ensured that each task was handled in the order it
was *created*. Here, though, each quote-retrieving task is handled in
the order that it *finishes*. In both cases, you ensure that only one
quote at a time is being written to a file.

## Callbacks

In languages without channels, you end up needing to express the idea
"when X happens, do Y" with *callbacks*.  If you've worked with
JavaScript, you've probably spent some time wallowing in Callback Hell
with code that looks like this (extra relevant if you've also used
JavaScript to raise an army of the undead, and, well, who hasn't):

```javascript
$.get("/cemetaries", function(cemetaries) {
  $.each(cemetaries, function(cemetary){
    raiseMinions(cemetary);
    $.put("/cemetaries/" + cemetary.id, function(cemetary){
      updateCemetaryDom(cemetary);
    })
  })
});
```

In case you're unfamiliar with JavaScript and jQuery, `$.get` and
`$.put` perform asynchronous HTTP requests. Here, their first argument
is the URL and the second argument is the callback function - when the
HTTP request returns, the callback function gets called on the
resulting data. This is following the same event-driven behavior
pattern that we've been looking at for this whole chapter: *when* X
happens, do Y. *When* the request to "/cemetaries" returns, iterate over
each cemetary in the result, raising minions and so forth.

The reason this is called "Callback *Hell*" is that it's very easy to
create unobvious dependencies among the layers of callbacks. They end
up sharing state, making it difficult to reason about the state of the
overall system as the callbacks get triggered. You can avoid this
depressing outcome by creating a process pipeline. That way, each unit
of logic lives in its own isolated environment (a process), with
communication between units of logic occurring through
explicitly-defined and easy-to-reason about input and output
channels. This is analogous to the way that pure functions are easier
to reason about than non-pure functions, the difference being that
processes place their data on an output channel, creating a one-way
flow of data transformations rather than returning a value to a
calling function.

In this next example, we create three infinitely looping
processes connected through channels, passing the "out" channel of one
process as the "in" channel of the next process in the pipeline:

```clojure
(defn upper-caser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/upper-case (<! in)))))
    out))

(defn reverser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/reverse (<! in)))))
    out))

(defn printer
  [in]
  (go (while true (println (<! in)))))

(def in-chan (chan))
(def upper-caser-out (upper-caser in-chan))
(def reverser-out (reverser upper-caser-out))
(printer reverser-out)

(>!! in-chan "redrum")
; => MURDER

(>!! in-chan "repaid")
; => DIAPER
```

By handling events using processes like this, it's easier to reason
about the individual steps of the overall data transformation
system. You can look at each step and understand what it does without
having to refer to what might have happened before it or what might
happen after it; each process is as easy to reason about as a pure
function.

## Additional Resources

Clojure's core.async library was largely inspired by Go's concurrency
model, which is based on the work by Tony Hoare in
[Communicating Sequential Processes](http://www.usingcsp.com/).

Rob Pike, co-creator of Go, has a
[good talk](https://www.youtube.com/watch?v=f6kdp27TYZs) on
concurrency.

Finally, check out the
[api docs](http://clojure.github.io/core.async/).

## Summary

In this chapter, you learned about how core.async allows you to create
concurrent processes that respond to the put and take communication
events on channels. You learned about how to use `go` and `thread` to
create concurrent processes that wait for communication events by
parking and blocking. You also learned how to create process pipelines
by making the "out" channel of one process the "in" channel of
another, and how this allows you to create saner code than nested
callbacks. Finally, you meditated on whether or not you're just a
fancy hot dog vending machine.
