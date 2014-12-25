---
title: Independent Concurrent Processes with core.async
link_title: Independent Concurrent Processes with core.async
kind: documentation
---

One day, while you are walking down the street, you will be surprised,
intrigued, and a little disgusted to discover a hot dog vending
machine. Your scalp tingling with guilty curiosity, you won't be able
to help yourself from pulling out three Sacagawea dolalrs and seeing
if this contraption actually works. After accepting your money, it
will emit a click and a whirr, and out will pop a fresh hot dog, bun
and all.

The vending machine exhibits simple behavior: when it receives money,
it releases a hot dog, then readies itself for the next purchase. When
it's out of hot dogs, it stops. All around us are hot dog vending
machines in different guises, independent entities concurrently
responding to events in the world and producing further events
according to their nature. The espresso machine at your favorite
coffee shop, the pet hamster you loved as a child - everything's
behavior can be modeled using the general form, "when *x* happens, do
*y*." Even the programs we write are just glorified hot dog vending
machines, each one an independent process waiting for the next event,
whether it's a keystroke, a timeout, or the arrival of data on a
socket.

Clojure's core.async library allows you to model the execution of
independent processes within a single program. This chapter describes
a useful mindset for thinking about this style of programming as well
as the practical details you need to actually write stuff. You'll
learn how to use channels, alts, and go blocks to create independent
processes and communicate between them, and you'll learn a bit about
how Clojure uses threads and something called "parking" to allow this
all to happen efficiently.

## Processes

At the heart of core.async is the idea of the process. A process, in
this context, is a concurrently running unit of logic that responds to
and produces events. The process concept is meant to capture our
mental model of the real world, with entities interacting with and
responding to each other completely independently, without some kind
of central control mechanism. You put your money in the machine and
out comes a hotdog, all without the Illumati orchestrating the whole
thing.

This differs from the view of concurrency you've been exploring so
far, where you've defined tasks that are either tightly bound to the
main thread of control (for example, achieving data parallelism with
`pmap`) or that you have no interest in communicating with (`pmap`
again and one-off tasks created with `future`).

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
             :refer [>! <! >!! <!! go chan close! thread alts! alt! alts!! alt!!]]))
```

Great! Now when you open this in a REPL, you'll have the most
frequently-used core.async functions at your disposal. Before creating
something as sophisticated and revolutionary as a hot dog vending
machine, let's create a process that simply prints the events that
happen to it:

```clojure
(def echo-chan (chan))
(go (println (<! echo-chan)))
(>!! echo-chan "ketchup")
; => true
; => ketchup
```

Here, you created a *channel* named `echo-chan` with the `chan`
function. Channels are how you communicate events to processes. In the
physical world, we don't think about channels or any other mechanism
for conveying events; when you put your hard-earned cash in the
mechanical hot dog maker, you don't think about the "channel" that
communicates the "sufficient money" event or about the channel that
communicates the "hot dog dispensed" event. Still, these events *are*
communicated through the physical media of light, sound, and grease
odor molecules. With core.async, the channel is your virtual medium
for communicating events.

Next, you used `go` to create a new process. Everything within the go
expression - called a *go block* - will run concurrently on a separate
thread. (Go blocks actually use a thread pool, a detail that I'll
cover in a little bit.) In this case, the process `(println (<!
echo-chan))` expresses, "*when* an event happens on `echo-chan`, print
that event." The process is shunted to another thread, freeing up the
current thread and allowing you to continue interacting with the REPL.

The expression `(<! echo-chan)` is how you express "when an event
happens on `echo-chan`" as well as "that event." `<!` is the "take"
function. It listens to the channel you give it as an argument and
causes the go block it lives in to pause execution of its root
expression (the `println` expression here) until it's able to retrieve
a value from the channel; you probably noticed that `ketchup` didn't
print in your REPL until you put it on the channel. Metaphorically
speaking, the retrieval of the value signifies that the process is
responding to the event. When `<!` does retrieve a value, the value is
returned and the `println` expression is executed.

The expression `(>!! echo-chan "ketchup")` puts the string `"ketchup"`
on `echo-chan` and returns `true`, and it's how you express "hey an
event has happened." When you put an event on a channel, the process
will block until another process takes the event. For example, if you
do this:

```clojure
(>!! (chan) "mustard")
```

then your REPL will block indefinitely. You've created a new channel
and put something on it, but it's impossible for anything else to take
from it. So, processes don't just wait to receive events, they also
wait for the events they produce to be handled.

(By the way, if you don't think that ketchup is an event, then stop
reading this immediately and go to Veggie Galaxy in Cambridge,
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
ketchup. The go process is one of those staff members, and he's
waiting patiently for something to respond to. It could be that
nothing ever happens, and he just waits indefinitely until the
restaurant closes.

This situation seems a little pathological, but soon you'll see how to
use buffers and choice to allow your processes to behave a little more
intelligently. Those tools, though, are just elaborations of the core
model: processes are independent, concurrently executing units of
logic that respond to and produce events. You can create processes
with go blocks and communicate events over channels. It's time to
expand on this model, starting with go blocks.

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
for a process to take from `hi-chan`.

There are two varieties of waiting: *parking* and
*blocking*. *Blocking* is the kind of waiting you're familiar with: a
thread stops execution until a task is complete. Usually this happens
when you're performing some kind of I/O. This kind of waiting keeps
the thread alive, doing no work, so that if you want your program to
continue doing work you have to create a new thread. *Parking* moves
the waiting task off the thread, freeing up the thread to do the work
of processes that aren't waiting. Clojure's smart enough to move the
parked process back on to a thread as soon its put or take is
done. It's like parking allows interleaving on a single thread,
similar to the way that using multiple threads allows interleaving on
a single core:

![Parking and Blocking](/images/core-async/parking.png)

Parking is only possible within go blocks, and it's only possible when
you use `>!` and `<!`, or *parking put* and *parking take*. As you've
no doubt guessed, `>!!` and `<!!` are *blocking put* and *blocking
take*.

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
desires and create a machine that turns money into hotdogs.

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
channel, you can no longer perform puts on them, and once you've taken
all values off a channel subsequent takes will return nil.

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
machine. First, it both responds to events and produces them within
the same go block. This isn't that unusual, and it's one way that you
can create a pipeline of processes: just make the "in" channel of one
process the "out" channel of another. (In fact, the function
`clojure.core.async/pipe` helps you do just this.) Second, it's cool
that the machine doesn't accept more money until you've dealt with
whatever it's dispensed. This allows you to model state-machine-like
behavior, where the completion of channel operations trigger state
transitions. For example, you can think of the vending machine as
having two states, "ready to receive money" and "dispensed item," with
the inserting of money and taking of the item triggering transitions
between the two.

## Choice and Buffers



####
next

create an infinite loop
create a process that just emits events
create an infinite hot dog machine

#### patterns?
- rework the peg example
- rock paper scissors?
