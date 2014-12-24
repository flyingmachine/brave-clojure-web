---
title: Independent Concurrent Processes with core.async
link_title: Independent Concurrent Processes with core.async
kind: documentation
---

One day, while you are walking down the street, you will be surprised,
intrigued, and a little disgusted to discover a hot dog vending
machine. Your scalp tingling with guilty curiosity, you won't be able
to help yourself from pulling out three wrinkled dollar bills and
seeing if this contraption actually works. After accepting your money,
it will emit a click and a whirr, and out will pop a fresh hot dog,
bun and all.

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
independent processes within a single program. This chapter covers a
useful mindset for thinking about this style of programming as well as
the practical details you need to actually write stuff. You'll learn
how to use channels, alts, and go blocks to create synchronous and
asynchronous independent processes and communicate between them, and
you'll learn a bit about how Clojure uses threads and something called
"parking" to allow this all to happen efficiently.

## Processes

At the heart of core.async is the idea of the process. A process, in
this context, is a concurrently running unit of logic that responds to
and produces events. The process concept is meant to capture our
mental model of the real world, with entities interacting with and
responding to each other completely independently, without some kind
of central control mechanism.

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
  (:require [clojure.core.async :as a :refer [>! <! >!! <!! go chan close! thread]]))
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
for conveying events; when you put three dollars in the hot dog
machine, you don't think about the "channel" that communicates the
"sufficient money" event or about the channel that communicates the
"hot dog dispensed" event. Still, these events *are* communicated
through the physical media of light, sound, and grease odor
particles. With core.async, the channel is your virtual medium for
communicating events.

Next, you used `go` to create a new process. Everything within the *go
block* - everything in the go expression - will run concurrently on a
separate thread. (Go blocks actually use a thread pool, a detail that
I'll cover in a little bit.) In this case, the process `(println (<!
echo-chan))` expresses, "*when* an event happens on `echo-chan`, print
that event."

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
on `echo-chan`, and it's how you express "hey an event has happened."
(By the way, if you don't think that ketchup is an event, then stop
reading this immediately and go to Veggie Galaxy in Cambridge,
Massachusetts, because holy cow their ketchup is *delicious*. I think
it's seasoned with hippie love.)
