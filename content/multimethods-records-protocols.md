---
title: Multimethods, Protocols, and Records
link_title: Multimethods, Protocols, and Records
kind: documentation
---

# Multimethods, Protocols, and Records

As a programmer, abstraction is your second-best friend. It is the
glorious mechanism that allows you to circumvent your cognitive
limits, allowing you to tie together disparate details into a neat
conceptual package that you can easily hold in your working
memory. Instead of having to hold "squeezable honking red ball nose
adornment" in your mind, you only need the concept "clown nose." The
more a programming language lets you think and write in terms of
abstractions, the more productive you will be.

In the chapter "Core Functions in Depth", you saw how Clojure is
written in terms of abstractions. This chapter serves as an
introduction to the world of creating and implementing your own
abstractions. You'll learn the basics of multimethods, protocols, and
records.

## Abstractions, Implementations, and Polymorphism

An *abstraction* is a named collection of operations, and *data types*
implement abstractions. The Seq abstraction consists of operations
like `first` and `rest`, and the vector data type is an implementation
of that abstraction - it responds to all of the Seq operations. A
specific vector, like `[:seltzer :water]` is an instance of that data
type.

Because this chapter deals with how data types implement abstractions,
and because Clojure relies on Java's standard library for many of its
data types, there's a little Java involved. For example, Clojure
strings are just Java strings, instances of the Java class
`java.lang.String`. "Wait a minute," you might be mentally imploring
me, "what's all this talk about classes all of a sudden?" Classes are
how you define your own data types in Java. Clojure provides
additional type constructs: *records* and *types*. This book only
covers records.

The main way that we achieve abstraction in Clojure is through
*polymorphism*, or the association of an operation name with more than
one algorithm. The algorithm for performing `conj` on a list is
different from the one for vectors, but we unify them under the same
name to indicate that they implement the same concept of "add an
element to the business end of this data structure." Let's look at
multimethods, our first tool for defining polymorphic behavior.

## Multimethods

Multimethods give you a direct, flexible way to introduce
polymorphism. Using multimethods, you associate a name with multiple
implementations by first defining a *dispatching function* which
produces *dispatching values* that are used to determine which
*method* to use. The dispatching function is like the host at a
restaurant. The host will ask you questions like, "do you have a
reservation?" and "party size?" and sit you at the right table based
on the answer. Similarly, when you call a multimethod, the dispatching
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
*dispatch value*. This is distinct from a dispatch*ing* value, which
is what the dispatching function returns. This method definition looks
a lot like a function definition, but the major difference is that the
method name is immediately followed by the dispatch value.

After that, you call the method twice. In the first example,
`(full-moon-behavior {:were-type :wolf :name "Rachel from next
door"})`, the dispatching function returns the value `:wolf` and the
corresponding method is used, informing you that `"Rachel from next
door will howl and murder"`. The next example behaves similarly,
except with `:simmons` as the dispatching value.

You can define a method for `nil`:

```clojure
(defmethod full-moon-behavior nil
  [were-creature]
  (str (:name were-creature) " will stay at home and eat ice cream"))
(full-moon-behavior {:were-type nil
                     :name "Martin the nurse"})
; => "Martin the nurse will stay at home and eat ice cream"
```

You can also define a default method to use if no other methods match
by specifying `:default` as the dispatch value:

```clojure
(defmethod full-moon-behavior :default
  [were-creature]
  (str (:name were-creature) " will stay up all night fantasy footballing"))
(full-moon-behavior {:were-type :office-worker
                     :name "Jimmy from sales"})
; => "Jimmy from sales will stay up all night fantasy footballing"
```

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

Your dispatching function can return any arbitrary value using any or
all of its arguments. The next example defines a multimethod that
takes two arguments and returns a vector containing the type of each
argument, and a method that matches when each argument is a string:

```clojure
(ns user)
(defmulti types (fn [x y] [(class x) (class y)]))
(defmethod types [java.lang.String java.lang.String]
  [x y]
  "Two strings!")
(types "String 1" "String 2")
; => "Two strings!"
```

Incidentally, this is why they're called *multi*methods - they allow
multiple dispatch, or dispatch on more than one argument.

Multimethods also allow *hierarchical dispatching*. Clojure allows you
to build custom hierarchies, which I won't cover, but you can learn
about them by reading the
[clojure.org documentation](http://clojure.org/multimethods).

## Protocols

93.58% of the time, you'll want to dispatch to methods according to an
argument's type; `count` will need to work differently for vectors
than it does for maps or for lists. You could perform type dispatch
with multimethods, of course, but *protocols* are optimized for this
use case. They're more efficient than multimethods and they have nice
language support so that you can succinctly specify protocol
implementations.

Whereas a multimethod is one polymorphic operation, a protocol is a
named *collection* of one or more polymorphic operations. Protocol
operations are also called methods.  Whereas multimethods perform
dispatch on arbitrary values returned by a dispatching function that
can make use of any of its arguments, protocol methods are dispatched
based on the type of the first argument. Let's look at an example:

```clojure
(ns data-psychology)
(defprotocol Psychodynamics
  "Plumb the inner depths of your data types"
  (thoughts [x] "The data type's innermost thoughts")
  (feelings-about [x] [x y] "Feelings about self or other"))
```

First, there's `defprotocol`. This takes a name, `Psychodynamics`, and
an optional docstring, `"Plumb the inner depths"` yada yada. Next are
the *method signatures*. A method signature consists of a name, an
argument specification, and an optional doc string. In the first
method signature, `thoughts` is the name and it can only ever take one
argument. In the second, `feelings-about` is the name and it can take
one or two arguments. One limitation of protocols is that the methods
can't have rest arguments, so something like

```clojure
(feelings-about [x] [x & others])
```

isn't allowed.

In defining a protocol, you're defining an abstraction, so it makes
sense that there would be no implementation code within the protocol
itself. Instead, you *extend* data types to implement the protocol:

```clojure
(extend-type java.lang.String
  Psychodynamics
  (thoughts [x] "Truly, the character defines the data type")
  (feelings-about
    ([x] "longing for a simpler way of life")
    ([x y] (str "envious of " y "'s simpler way of life"))))

(thoughts "blorb")
; => "Truly, the character defines the data type"

(feelings-about "schmorb")
; => "longing for a simpler way of life"

(feelings-about "schmorb" 2)
; => "envious of 2's simpler way of life"
```

`extend-type` is followed by the name of the class or type you want to
extend and the protocol you want it to support. After that, you
provide an implementation for each method. If you want to provide a
default implementation, you can just extend `java.lang.Object`:

```clojure
(extend-type java.lang.Object
  Psychodynamics
  (thoughts [x] "Maybe the Internet is just a vector for toxoplasmosis")
  (feelings-about
    ([x] "meh")
    ([x y] (str "meh about " y))))
  
(thoughts 3)
; => "Maybe the Internet is just a vector for toxoplasmosis"

(feelings-about 3)
; => "meh"

(feelings-about 3 "blorb")
; => "meh about blorb"
```

Instead of using multiple calls to `extend-type` to extend multiple
types, you can use `extend-protocol`, which allows you to define
protocol implementations for multiple types at once. Here's how you'd
define the above protocol implementations:

```clojure
(extend-protocol Psychodynamics
  java.lang.String
  (thoughts [x] "Truly, the character defines the data type")
  (feelings-about
    ([x] "longing for a simpler way of life")
    ([x y] (str "envious of " y "'s simpler way of life")))
  
  java.lang.Object
  (thoughts [x] "Maybe the Internet is just a vector for toxoplasmosis")
  (feelings-about
    ([x] "meh")
    ([x y] (str "meh about" y))))
```

You might find this more convenient than using `extend-type`. Then
again, you might not. How does `extend-type` make you feel? How about
`extend-protocol`? Come lie down on this couch and tell me about it.

One thing to keep in mind is that a protocol's methods "belong" to the
namespace that they're defined in. In the examples above, the
fully-qualified names of the `Psychodynamics` methods are
`data-psychology/thoughts` and `data-psychology/feelings-about`. If
you come from an object-oriented background, this might seem weird
because, in OOP, methods belong to data types. Protocols' belonging to
namespaces is just another way that Clojure gives primacy to
abstractions. One consequence of this fact is that, if you want two
different protocols to include methods that have the same name, you'll
need to put the protocols in different namespaces.

## Records

Clojure allows you to create *records*, which are custom map-like data
types. They're map-like in that they associate keys with values and
you can look up their values the same as you can with maps, and
they're immutable just like maps are. They're different in that you
specify *fields* for records. Fields are named slots for data; it's
like you're specifying which keys this data structure should
have. Records are also different in that you can extend them to
implement protocols.

To create a record, you use `defrecord` to specify its name and
fields:

```clojure
(ns were-records)
(defrecord WereWolf [name title])
```

This record's name is `WereWolf` and it has two fields, `name` and
`title`. There are three different ways that you can create an
instance of of this record:

```clojure
(WereWolf. "David" "London Tourist")
; => #were_records.WereWolf{:name "David", :title "London Tourist"}

(->WereWolf "Jacob" "Lead Shirt Discarder")
; => #were_records.WereWolf{:name "Jacob", :title "Lead Shirt Discarder"}

(map->WereWolf {:name "Lucian" :title "CEO of Melodrama"})
; => #were_records.WereWolf{:name "Lucian", :title "CEO of Melodrama"}
```

In the first example, you create an instance the same way that you
create a Java object, using the class instantiation interop
call. Notice that the arguments must follow the same order as the
field definition. You can use the Java interop call because, under the
covers, records are actually Java classes. If you want to use them in
another namespace, you'll have to import them, just like you would any
other Java class.

The second example looks nearly identical to the first, but the key
difference is that `->WereWolf` is a function. When you create a
record, the factory functions `->RecordName` and `map->RecordName` are
created automatically. The last example, `map->WereWolf` should be
self-explanatory.

You can look up record values in the same way you look up map values,
and you can also use Java field access interop:

```clojure
(def jacob (->WereWolf "Jacob" "Lead Shirt Discarder"))
(.name jacob) ; => "Jacob"
(:name jacob) ; => "Jacob"
(get jacob :name) ; => "Jacob"
```

When testing for equality, Clojure will check that all fields are
equal and that the two comparands have the same type:

```clojure
(= jacob (->WereWolf "Jacob" "Lead Shirt Discarder"))
; => true

(= jacob (WereWolf. "David" "London Tourist"))
; => false

(= jacob {:name "Jacob" :title "Lead Shirt Discarder"})
; => false
```

If you can use a function on a map, you can use it on a record:

```clojure
(assoc jacob :title "Lead Third Wheel")
; => #were_records.WereWolf{:name "Jacob", :title "Lead Third Wheel"}
```

However, you `dissoc` a field, the result will be map rather than a
data structure of the record's type:

```clojure
(dissoc jacob :title)
; => {:name "Jacob"} <- that's not a were_records.WereWolf
```

When you create a new record type, you can extend a protocol:

```clojure
(defprotocol WereCreature
  (full-moon-behavior [x]))

(defrecord WereWolf [name title]
  WereCreature
  (full-moon-behavior [x]
    (str name " will howl and murder")))

(full-moon-behavior (map->WereWolf {:name "Lucian" :title "CEO of Melodrama"}))
; => "Lucian will howl and murder"
```

We've created a new protocol, WereCreature, with one method,
`full-moon-behaior`. Next, `defrecord` implements `WereCreature` for
`WereWolf`. The most interesting part of the `full-moon-behavior`
implementation is that you have access to `name`. You also have access
to `title` and any other fields that might be defined for your
record. You can also extend records using `extend-type` and
`extend-protocol`.

When should you use records, and when should you use maps? In general,
you should consider using records if you find yourself creating maps
with the same fields over and over. This tells you that that set of
data represents information in your application's domain, and it'd be
your code will communicate its purpose better if you give a name to
the concept you're trying to model. Not only that, but record access
is more performant than map access, so your program will become a
little more efficient. Finally, if you want to make use of protocols,
then you'll need to create a record.

## Further Study

Clojure offers other tools for working with abstractions and data
types, including `deftype`, `reify`, and `proxy`, which I consider
advanced tools. If you're interested in learning more, check out the
[clojure.org documentation on data types](http://clojure.org/datatypes).

## Summary

One of Clojure's design principles is to write to abstractions. In
this chapter, you learned how to define your own abstractions using
multimethods and prototypes. These constructs provide polymorphism,
allowing the same operation to behave differently based on the
arguments its given. You also learned how to create and use your own
associative data types with `defrecord`, and how to extend records to
implement protocols.
