---
title: "Organizing Your Project: a Librarians's Tale"
link_title: "Organizing Your Project: a Librarian's Tale"
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

* What `def` does
* What namespaces are and how to use them
* The relationship between namespaces and the filesystem
* How to use `refer` and `ns`
* What dynamic binding is and why you'd use it

To get there, you'll explore the idea of your project as a library.
You'll also join me in a tale of suspense and mystery as we solve the
heist of a lifetime. You'll start in the REPL and then progress to
trying out examples in a filesystem-based project. Be sure to try out
all the examples!

## Your Project as a Library

Real-world libraries store collections of objects like books,
magazines, and DVDs. They use addressing systems so that, given an
object's address, you can navigate the physical space and retrieve the
object you're looking for.

No human being is going to know off-hand what a book's address is, so
libraries provide tools for finding the address. For example, if you
searched for "The Da Vinci Code" your local library's database, it
would return the address "813.54" to you (if it's using the Dewey
Decimal System). Now you can efficiently navigate your library and
locate the physical space where _The Da Vinci Code_ resides. Then you
can engage in the literary and/or hate-reading adventure of your
lifetime.

It's useful to imagine a similar physical setup in Clojure. I think of
Clojure as storing objects like data structures and functions in a
vast set of numbered lockers. No human being is going to know off-hand
which locker an object is stored in. Instead, we want to give Clojure
an identifier which it can use to retrieve the object.

In order for this to happen, Clojure has to maintain the associations
between our identifiers and locker addresses. It does this using
*namespaces*. Namespaces contain maps between human-friendly *symbols*
and references to locker addresses (known as *Vars*).

Technically, namespaces are objects of type `clojure.lang.Namespace`.
The current namespace is accessible with `*ns*` &ndash; try evaluating
that in the REPL. Namespaces have names, and you can get the name of
the current namespace with `(ns-name *ns*)`.

```clojure
(ns-name *ns*)
; => user
```

In Clojure programs, you are always "in" a namespace. When you start
the REPL, for example, you're in the `user` namespace (as you can see
above). The prompt will show the current namespace with something like
`user>`. In a later section we'll go over how to create and switch to
name spaces.

This terminology makes me think of namespaces as little cubicles lined
with cubbies. The cubbies have names like `str`, `ns-name`, and so on,
and within each is the corresponding var. This might sound like a
completely boring analogy, but believe me, Melvil loves it.

Now that we know how Clojure's organization system works, let's look
at how we use it!

## Storing Objects with def

Our primary tool for storing objects is `def`. Other tools like `defn`
and `defmacro` use `def` under the hood. Here's an example of def in
action:

```clojure
(def great-books ["East of Eden" "The Glass Bead Game"])
; => #'user/great-books
great-books
["East of Eden" "The Glass Bead Game"]
```

This is like telling Clojure:

1. Find a Var named `great-books`. If it doesn't exist, create it and
  update the current namespace's map with the association between
  `great-books` and the Var that was just created
2. Find a free storage locker
3. Shove `["East of Eden" "The Glass Bead Game"]` in it
4. Write the address of the locker on the Var
5. Return the Var (in this case, `#'user/great-books`)

This process is called *interning* a Var. You can interact with a
namespace's map of symbols-to-interned-vars:

```clojure
(ns-interns *ns*)
; => {great-books #'user/great-books}

(get (ns-interns *ns*) 'great-books)
; => #'user/great-books
```

`#'user/great-books` probably looks unfamiliar to you at this point.
That's the *reader form* of a Var. I explain reader forms in the
chapter [Clojure Alchemy: Reading, Evaluation, and Macros](Clojure
Alchemy: Reading, Evaluation, and Macros). For now, just know that
`#'` can be used to grab hold of the Var corresponding to the symbol
that follows; `#'user/great-books` lets you use the Var associated
with the symbol `great-books` within the `user` namespace. We can
`deref` vars to get the objects they point to:

```clojure
(deref #'user/great-books)
; => ["East of Eden" "The Glass Bead Game"]
```

This is like telling Clojure:

* Get the locker number from the Var
* Go to that locker number and grab what's inside
* Give it to me!

Normally, though, you'll just use the symbol:

```clojure
great-books
; => ["East of Eden" "The Glass Bead Game"]
```

This is like telling Clojure:

* Retrieve the Var associated with `great-books`
* `deref` that bad Jackson

So far, so good, right? Well, brace yourself, because this idyllic
paradise is about to be turned upside down!

Call `def` again with the same symbol:

```clojure
(def great-books ["The Power of Bees" "Journey to Upstairs"])
great-books
; => ["The Power of Bees" "Journey to Upstairs"]
```

The Var has been updated with the address of the new vector. The
result is that you can no longer ask Clojure to find the first vector.
This is referred to as a *name collision*. Chaos! Anarchy!

You may have experienced this in other programming languages.
Javacript is notorious for it, and it happens in Ruby as well. It's a
problem because you can unintentionally overwrite your own code, and
if you use someone else's libraries you have no guarantee that they
won't overwrite your code. Melvil recoils in horror! Thankfully,
Clojure allows to create as many namespaces as we like so that we can
avoid these collisions.

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
namespace to namespace, I shout at Clojure to hand me one thing after
another.

But Clojure is kind of dumb. From within the `user` namespace, I belt
out "`join`! Give me `join`!", specks of spittle flying out my mouth.
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
I think of this as

## Using other libraries


### Creating Namespaces

### Changing Namespaces

### Referring to Other Objects

## More efficient
