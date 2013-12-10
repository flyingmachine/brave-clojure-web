---
title: "Namespaces: a Librarians's Tale"
link_title: "Namespaces: a Librarian's Tale"
kind: documentation
draft: true
---

Within each of us is lives a librarian, a fantastical creature who
delights in the organizational arts. Day and night, he yearns to to
bring order to your codebase. Happily, Clojure provides a suite of
tools designed specifically for aiding your inner librarian in its
constant struggle against the forces of chaos.

These tools allow you to keep your code organized by grouping related
functions and data together. They also prevent name collisions,
ensuring that you don't accidentally overwrite someone else's code or
vice versa.

This chapter will instruct you in the proper usage of these tools. The
librarian inside you quivers with excitement! By the end, you will
understand:

* How to think about `def`
* What namespaces are and how to use them
* The relationship between namespaces and the filesystem
* How to use `require`, `use`, and `import`
* What dynamic binding is and why you'd use it

To get there, we'll explore the idea of your project as a library. Try
the examples in your REPL as we go!

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
(def actually-great-books ["East of Eden" "The Glass Bead Game"])
```

Then, when you hand Clojure the symbol `actually-great-books`, it's
like saying "find the object with this name and give it to me."

```clojure
actually-great-books
; => ["East of Eden" "The Glass Bead Game"]
```

So far, so good, right? Well, brace yourself, because this idyllic
paradise is about to be turned upside down!

What happens when you call `def` again with the same symbol?

```clojure
(def actually-great-books ["The Power of Bees" "Journey to Upstairs"]
actually-great-books
; => ["The Power of Bees" "Journey to Upstairs"]
```

It's like Clojure peeled the label off the first vector and placed it on
the second. The result is that you can no longer ask Clojure to find
the first vector. This is referred to as a *name collision*.

You may have experienced this in other programming languages.
Javacript is notorious for it, and it happens in Ruby as well. It's a
problem because you can unintentionally overwrite your own code, and
if you use someone else's libraries you have no guarantee that they
won't overwrite your code. Clojure solves this problem with
**namespaces**.

## Introducing Namespaces

I think of namespaces as actual spaces, as storage rooms for my
objects. In Clojure, you are always "in" a namespace. When you start
the REPL, for example, you're in the `user` namespace. The prompt will
show the current namespace with something like `user>`.

Namespaces are data structures of type `clojure.lang.Namespace`. You
can pass them as arguments to functions, for example. The current
namespace is always accessible with `*ns*` &ndash; try typing that in
the REPL. Namespaces have names, and you can get the name of the
current namespace with `(ns-name *ns*)`. In the next section we'll go
over how to create and switch to name spaces.

By using namespaces, we can use the same names in different contexts.
For example, Clojure stores string-related functions in the namespace
`clojure.string` and set-related functions in `clojure.set`. Each
namespace has a function stored under the name `join`.

So when I said above that using `def` is like telling Clojure to
attach a symbol to an object and store it, it's more accurate to say
that using `def` it like telling Clojure:

* Look in the *current namespace* for an object with the given symbol.
  If you find one, remove the symbol. Using the examples above, we
  would remove `actually-great-books` from the vector
  `["East of Eden" "The Glass Bead Game"]`.
* Attach the symbol to the given object. In the above examples, we'd
  attach `actually-great-books` to
  `["The Power of Bees" "Journey to Upstairs"]`.
* Store the object in the current namespace.

Thus, using namespaces allows you to avoid the name collisions other
languages are prone to.

### Creating and Switching to Namespaces

Clojure has three tools for creating namespaces:

* The function `create-ns`
* The function `in-ns`
* The macro `ns`

Let's look at each in detail.

`create-ns` takes a symbol, creates a namespace with that name if it
doesn't exist already, and returns the namespace. Remember from the
chapter [Do Things]("/do-things") to quote a symbol when passing it as
an argument to a function:

```clojure
;; Creates the namespace if it doesn't exist and return
user> (create-ns 'secret-lair)
; => #<Namespace secret-lair>

;; Returns the namespace if it already exists
user> (create-ns 'secret-lair)
; => #<Namespace secret-lair>

;; Pass the returned namespace as an argument
; (ns-name (create-ns 'secret-lair))
; => secret-lair
```

You'll rarely use `create-ns` in your code. Instead, you'll use
`in-ns` which creates the namespace if it doesn't exist and then
switches to it:

```clojure
user> (in-ns 'secret-living-room)
; => #<Namespace secret-living-room>
```

Notice that your REPL prompt is now `secret-living-room>`.

### Creating Namespaces

### Changing Namespaces

### Referring to Other Objects

## More efficient
