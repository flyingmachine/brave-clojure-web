---
title: "Namespaces: a Librarians's Tale"
link_title: "Namespaces: a Librarian's Tale"
kind: documentation
draft: true
---

Within each of us is lives a librarian, a fantastical creature who
delights in the organizational arts. Happily, Clojure provides a suite
of tools designed specifically for aiding your inner librarian in its
constant struggle against the forces of chaos.

These tools allow you to keep your code organized by grouping related
functions and data together. They also prevent name collisions,
ensuring that you don't accidentally overwrite someone else's code or
vice versa.

This chapter will instruct you in the proper usage of these tools. The
librarian inside you quivers with excitement! By the end, you will
understand:

* What `def` does
* What namespaces are and how to use them
* How to use `require`, `use`, and `import`

To get there, we'll explore the idea of your project as a library.

## Your Project as a Library

Real-world libraries house collections of objects like books,
magazines, and DVDs. Each object is placed systematically within this
physical space and affixed with a label which corresponds with its
placement. 

For example, _The Da Vinci Code_ (a modern masterpiece) would have the
label "813.54" if your library uses the Dewey Decimal system. Once you
know that _The Da Vinci Code_ is labelled "813.54", you can then
efficiently navigate your library and locate the physical space where
the book resides. You can then engage in the literary and/or
hate-reading adventure of your lifetime.

In Clojure, we use the special form `def` to label our program's
"objects" like data structures, functions, and macros:

```clojure
(def actually-great-books ["East of Eden" "The Glass Bead Game"])
```

This associates the *symbol* `actually-great-books` with the vector
that follows. It's like telling Clojure, "Take this object and store
it within your vast labyrinth of bits and bytes. But before you do
that, smack this label on it."

Then, 

Instead, each
item is given a label which corresponds

Your project houses collections of "objects" like
data structures, functions, and macros.

In order to 

Whereas the librarians you're used to deal with books, magazines, dvds
and the like, your librarian will be working with functions, data
structures, and macros.
