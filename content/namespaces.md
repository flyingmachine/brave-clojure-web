---
title: "Namespaces: a Librarians's Tale"
link_title: "Namespaces: a Librarian's Tale"
kind: documentation
draft: true
---

Within each of us is lives a librarian named Melvil, a fantastical
creature who delights in the organizational arts. Day and night, Melvil
yearns to bring order to your codebase. Happily, Clojure provides a
suite of tools designed specifically for aiding this homunculus in its
constant struggle against the forces of chaos.

These tools allow you to keep your code organized by grouping related
functions and data together. They also prevent name collisions,
ensuring that you don't accidentally overwrite someone else's code or
vice versa.

This chapter will instruct you in the proper usage of these tools.
Melvil quivers with excitement! By the end, you will understand:

* How to think about `def`
* What namespaces are and how to use them
* The relationship between namespaces and the filesystem
* How to use `require`, `use`, and `import`
* What dynamic binding is and why you'd use it

To get there, you'll explore the idea of your project as a library.
You'll also join me in a tale of suspense and mystery as we solve the
heist of a lifetime. You'll start in the REPL and then progress to
trying out examples in a filesystem-based project. Be sure to try out
all the examples!

## Your Project as a Library

Real-world libraries store collections of objects like books,
magazines, and DVDs. Each object is placed systematically within this
physical space and affixed with a name which corresponds with its
location. That way you can easily find an object if you know its name.

For example, if I picked up _The Da Vinci Code_ (a modern masterpiece)
at your local library, I would likely find a piece of paper with
"813.54" printed on it glued to the book's spine. "813.54" is the
book's name within the context of the library's organizational system.
And now that you know that _The Da Vinci Code_ is named "813.54", you
can efficiently navigate your library and locate the physical space
where the book resides. Then you can engage in the literary and/or
hate-reading adventure of your lifetime.

It's useful to imagine the same physical process in Clojure. In
Clojure, you use **symbols** to represent names. Using the special
form `def` is like telling Clojure to glue a symbol to a programmatic
"object", like a data structure or function, and then store it:

```clojure
(def great-books ["East of Eden" "The Glass Bead Game"])
```

Then, when you hand Clojure the symbol `great-books`, it's
like saying "find the object with this name and give it to me."

```clojure
great-books
; => ["East of Eden" "The Glass Bead Game"]
```

So far, so good, right? Well, brace yourself, because this idyllic
paradise is about to be turned upside down!

Call `def` again with the same symbol:

```clojure
(def great-books ["The Power of Bees" "Journey to Upstairs"])
great-books
; => ["The Power of Bees" "Journey to Upstairs"]
```

It's like Clojure peeled the label off the first vector and placed it
on the second. Chaos! Anarchy! The result is that you can no longer
ask Clojure to find the first vector. This is referred to as a *name
collision*.

You may have experienced this in other programming languages.
Javacript is notorious for it, and it happens in Ruby as well. It's a
problem because you can unintentionally overwrite your own code, and
if you use someone else's libraries you have no guarantee that they
won't overwrite your code. Melvil recoils in horror! Thankfully,
Clojure solves this problem with **namespaces**.

## Introducing Namespaces

I think of namespaces as actual spaces, as storage rooms for my
objects. In Clojure, you are always "in" a namespace. When you start
the REPL, for example, you're in the `user` namespace. The prompt will
show the current namespace with something like `user>`.

Namespaces are objects of type `clojure.lang.Namespace`. You can pass
them as arguments to functions, for example. The current namespace is
always accessible with `*ns*` &ndash; try typing that in the REPL.
Namespaces have names, and you can get the name of the current
namespace with `(ns-name *ns*)`. In the next section we'll go over how
to create and switch to name spaces.

By using namespaces, we can use the same names in different contexts.
For example, Clojure stores string-related functions in the namespace
`clojure.string` and set-related functions in `clojure.set`. Each
namespace has a function stored under the name `join`. No conflicts!
Melvil lets out an audible sigh.

So when I said above that using `def` is like telling Clojure to
attach a symbol to an object and store it, it's more accurate to say
that using `def` it like telling Clojure:

* Look in the *current namespace* for an object with the given symbol.
  If you find one, remove the symbol. Using the examples above, we
  would remove `great-books` from the vector
  `["East of Eden" "The Glass Bead Game"]`.
* Attach the symbol to the given object. In the above examples, we'd
  attach `great-books` to
  `["The Power of Bees" "Journey to Upstairs"]`.
* Store the object in the current namespace.

Thus, using namespaces allows you to avoid the name collisions other
languages are prone to.

## Creating and Switching to Namespaces

Clojure has three tools for creating namespaces:

* The function `create-ns`
* The function `in-ns`
* The macro `ns`

Let's look at `create-ns` and `in-ns`. We'll examine `ns` in great
detail in an upcoming section.

`create-ns` takes a symbol, creates a namespace with that name if it
doesn't exist already, and returns the namespace. Remember from the
chapter [Do Things]("/do-things") to quote a symbol when passing it as
an argument to a function:

```clojure
;; Creates the namespace if it doesn't exist and return
user> (create-ns 'ch)
; => #<Namespace secret-lair>

;; Returns the namespace if it already exists
user> (create-ns 'secret-lair)
; => #<Namespace secret-lair>

;; Pass the returned namespace as an argument
; (ns-name (create-ns 'secret-lair))
; => secret-lair
```

This is pretty neat, but in practice you'll probably never use
`create-ns` in your code. It's not very useful to create a namespace
and not move into it. `in-ns` does just that, creating the namespace
if it doesn't exist and switching to it:

```clojure
user> (in-ns 'secret-living-room)
; => #<Namespace secret-living-room>
```

Notice that your REPL prompt is now `secret-living-room>`, indicating
that you are indeed in the new namespace you just created. Now when
you use `def` it will store the named object in the
`secret-living-room` namespace.

What if you want to use things from other namespaces, though? To do
that, you use what's called a "fully-qualified" symbol. The general
form is `namespace/name`:

```clojure
;; We get an exception if we try to refer to the user namespace's
;; great-books from within secret-living-room
secret-living-room> great-books
; => Exception: Unable to resolve symbol: great-books in this context

;; But using the fully-qualified symbol works:
secret-living-room> user/great-books
; => ["The Power of Bees" "Journey to Upstairs"]
```

The way I think about this is that I imagine I am an extremely
impatient academic specializing in semiotics-au-fromage, or the study
of symbols as they relate to cheese. Suddenly, I'm thrust into the
middle of an international plot. All across the world, sacred and
historically important cheeses have gone missing. Wisconsin's Standard
Cheddar: gone! The Great Cheese Jars of Tutankhamun: stolen!

And now, these daring cheese thieves have claimed the most famous
cheese of all: the Cheese of Turin, a crumb of cheese purported to
have fallen from the lips of a deity during his last dinner.

Because I'm an academic I attempt to solve the case the best way I
know how: by heading to the library and researching the shit out of
it. My trusty assistant, Clojure, accompanies me. As we bustle from
room to room, I shout at Clojure to hand me one thing after another.

But Clojure is kind of dumb. From within the `user` room, I belt out
"`join`! Give me `join`!", specks of spittle flying out my mouth.
"`RuntimeException: Unable to resolve symbol: join`", Clojure whines
in response. "For the love of brie, just hand me
`clojure.string/join`"! I retort, and Clojure dutifully hands me the
function I was looking for.

After awhile, though, my voice gets hoarse. I need some way to tell
Clojure what objects to get me without having to use the fully
qualified symbol every. damn. time.

Luckily, Clojure has some tools that allow me to yell at it more
succinctly.

## refer and alias

The first tool, `refer`, gives us fine-grained control over how we
refer to objects in other namespaces. Fire up a new REPL session and
try the following (and keep in mind that this is not at all indicative
of how you should actually structure a Clojure project):

```clojure
user> (in-ns 'cheese.taxonomy)
cheese.taxonomy> (def cheddars ["mild" "medium" "strong" "sharp" "extra sharp"])
cheese.taxonomy> (def bries ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"])
cheese.taxonomy> (in-ns 'cheese.analysis)
cheese.analysis> (clojure.core/refer 'cheese.taxonomy) ; ~~1~~
cheese.analysis> bries
; => ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]
cheese.analysis> cheddars
; => ["mild" "medium" "strong" "sharp" "extra sharp"]
```

Calling `refer` with a symbol allows us to refer to the corresponding
namespace's objects without having to use their fully-qualified names.

You ignorant clod!

## Using other libraries


### Creating Namespaces

### Changing Namespaces

### Referring to Other Objects

## More efficient
