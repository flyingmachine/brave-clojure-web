---
title: Abstraction Tools: Multimethods, Records, Protocols, Reify
link_title: Abstraction Tools: Multimethods, Records, Protocols, Reify
kind: documentation
draft: true
---

As a programmer, abstraction is your second-best friend. It is the
glorious mechanism that allows you to circumvent your cognitive
limits, allowing you to tie together disparate details into a neat
conceptual package that you can easily hold in your working
memory. Instead of having to hold "squeezable red ball honks nose
adornment" in your mind, you only need the concept "clown nose." The
more a programming language lets you think and write in terms of
abstractions, the more productive you will be.

In the chapter "Core Functions in Depth", you saw how Clojure is
written in terms of abstractions. In this chapter, you'll learn how to
create your own abstractions with protocols and multimethods, how to
create performant implementions of abstractions with records, and how
to extend existing implementations so that they'll belong to an
abstraction.

## Abstractions, Implementations, and Polymorphism

An *abstraction* is a named collection of *operations*, and data types
implement abstractions. The Seq abstraction consists of operations
like `first` and `rest`, and the vector data type is an implementation
of that abstraction - it responds to all of the Seq operations. A
specific vector, like `[:seltzer :water]` is an instance of that data
type.

The main way that we achieve abstraction in Clojure is through
*polymorphism*, or the association of an operation name with more than
one algorithm. The algorithm for performing `conj` on a list is
different from the one for vectors, but we give them the same name to
indicate that they implement the same concept of "add an element to
the business end of this data structure."

## Multimethods

Multimethods give you a direct, flexible way to introduce
polymorphism. Using multimethods, you associate a name with multiple
implementations by first defining a *dispatching function* which
produce *dispatching values* that are used to determine which *method*
to use. The dispatching function is like the host at a restaurant. The
host will ask you questions like, "do you have a reservation?" and
"party size?" and sit you at the right table based on the
answer. Similarly, when you call a multimethod, the dispatching
function will interrogate the arguments and send them to the right
method. Let's look at an example:

```clojure
(ns were-creatures)
(defmulti full-moon-behavior (fn [were-creature] (:were-type were-creature)))
(defmethod full-moon-behavior :wolf
  [were-creature]
  (str (:name were-creature) " will howl and murder"))
(defmethod full-moon-behavior :simmons
  [were-creature]
  (str (:name were-creature) " will encourage people and sweat to the oldies"))

(full-moon-behavior {:were-type :wolf
                     :name "Rachel from next door"})
; => "Rachel from next door will howl and murder"

(full-moon-behavior {:name "Andy the baker"
                     :were-type :simmons})
; => "Andy the baker will encourage people and sweat to the oldies"
```

This multimethod shows how you might define the full moon behavior of
different kinds of were-creatures. Everyone knows that a werewolf
turns into a wolf and runs around howling all creepy-like and
murdering people. A lesser-known species of were-creature, the
were-Simmons, turns into Richard Simmons, power perm and all, and runs
around encouraging people to be their best and sweat it to the
oldies. You do not want to get bitten by either, lest you turn into
one yourself.

The first line, `(defmulti full-moon-behavior (fn [were-creature]
(:were-type were-creature)))`, created the multimethod. This tells
Clojure, "Hey, create a new multimethod!  Its name is
`full-moon-behavior`. Whenever someone calls `full-moon-behavior`, run
the dispatching function `(fn [were-creature] (:were-type
were-creature))` on the arguments. Use the result of that function to
decide which specific method to use!" By the way, you could have
written the first line as `(defmulti full-moon-behavior :were-type)`,
since keywords can act as functions.

Next, you define two methods, one for when the value returned by
dispatching function is `:wolf` and one for when it's
`:simmons`. `:wolf` and `:simmons` are both referred to as a
*dispatch-value*. This is distinct from a *dispatching value*, which
is what the dispatching function returns. This method definition looks
a lot like a function definition, but the major difference is that the
method name is immediately followed by the dispatch-value.

After that, you call the method twice. In the first example,
`(full-moon-behavior {:were-type :wolf :name "Rachel from next
door"})`, the dispatching function returns the value `:wolf` and the
corresponding method is used, informing you that `"Rachel from next
door will howl and murder"`. The next example behaves similarly,
except with `:simmons` as the dispatching value.

One cool thing about multimethods is that you can always continue
adding new methods. If you publish a library that includes the
`were-creatures` namespace, other people can continue extending the
multimethod to handle new dispatch values:

```clojure
(ns random-namespace)
(defmethod were-creatures/full-moon-behavior :murray
  [were-creature]
  (str (:name were-creature) " will show up and dance at random parties"))

(were-creatures/full-moon-behavior {:name "Laura the intern" :were-type :murray})
; => "Laura the intern will show up and dance at random parties"
```

Multimethods also allow hierarchical dispatching. In fact, Clojure
actually uses the `isa?` function to compare dispatching values to
methods' dispatch-values. `isa?` tests for equality first, which is
why method matches for `:simmons`, `:were`, and `:murray` have worked.
It also tests for various kinds of hierarchical relationships, and you
can read them about them all in the
[clojure.org documentation](http://clojure.org/multimethods).

