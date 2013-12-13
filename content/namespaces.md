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

To get there, you'll explore the idea of your project as a library
using the REPL. This will give you a clear mental model of Clojure's
organizational system. You'll also join me in a tale of suspense and
mystery as we solve the heist of a lifetime. This will give you an
increased heartrate and a slight pain in your bum as you sit on the
edge of your seat. Finally, I'll show you how you'll organize projects
in real life using the filesystem. Put a pillow on your seat's edge
and read on!

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

You can also get the map the namespace uses for looking up a Var when
given a symbol:

```clojure
(ns-map *ns*)
; => very large map which we won't print here

;; The symbol 'great-books is mapped to the Var we created above
(get (ns-map *ns*) 'great-books)
; => #'user/great-books
```

`#'user/great-books` probably looks unfamiliar to you at this point.
That's the *reader form* of a Var. I explain reader forms in the
chapter
[Clojure Alchemy: Reading, Evaluation, and Macros](/read-and-eval).
For now, just know that `#'` can be used to grab hold of the Var
corresponding to the symbol that follows; `#'user/great-books` lets
you use the Var associated with the symbol `great-books` within the
`user` namespace. We can `deref` vars to get the objects they point
to:

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
cheese.analysis> (clojure.core/refer 'cheese.taxonomy)
cheese.analysis> bries
; => ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]
cheese.analysis> cheddars
; => ["mild" "medium" "strong" "sharp" "extra sharp"]
```

Calling `refer` with a symbol allows us to refer to the corresponding
namespace's objects without having to use their fully-qualified names.
It updates the current namespace's symbol/object map. You can see the
new entries:

```clojure
cheese.analysis> ('bries (clojure.core/ns-map clojure.core/*ns*))
#'cheese.taxonomy/bries

cheese.analysis> ('cheddars (clojure.core/ns-map clojure.core/*ns*))
#'cheese.taxonomy/cheddars
```

It's as if Clojure does something like:

1. Call `ns-interns` on the `cheese.taxonomy` namespace
2. Merge that with the `ns-map` of the current namespace
3. Make the result the new `ns-map` of the current namespace

You can also pass `refer` the filters `:only`, `:exclude`, and
`:rename`. `:only` and `:exclude` limit the symbol/Var mappings which
get merged into the current namespace's `ns-map`. `:rename` lets you
use different symbols for the vars being merged in. Here's what would
happen if we had used these options in the example above:

```clojure
;; :only example
cheese.analysis> (clojure.core/refer 'cheese.taxonomy :only ['bries])
cheese.analysis> bries
; => ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]
cheese.analysis> cheddars 
; => RuntimeException: Unable to resolve symbol: cheddars

;; :exclude example
cheese.analysis> (clojure.core/refer 'cheese.taxonomy :exclude ['bries])
cheese.analysis> bries
; => RuntimeException: Unable to resolve symbol: bries
cheese.analysis> cheddars 
; => ["mild" "medium" "strong" "sharp" "extra sharp"]

;; :rename example
cheese.analysis> (clojure.core/refer 'cheese.taxonomy :rename {'bries 'yummy-bries})
cheese.analysis> bries
; => RuntimeException: Unable to resolve symbol: bries
cheese.analysis> yummy-bries
; => ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]
```

By the way, notice that we're having to use the fully-qualified names
of all the functions in `clojure.core`, whereas we didn't have to do
that in the `user` namespace. That's because the REPL automatically
refers `clojure.core` within the `user` namespace.

So that's `refer`! `alias` is relatively simple by comparison. All it
does it let you use a shorter namespace name when using a
fully-qualified name:

```clojure
cheese.analysis> (clojure.core/alias 'taxonomy 'cheese.taxonomy)
cheese.analysis> taxonomy/bries
["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]
```

And that's it! Those are all your basic tools for referring to objects
outside of your current namespace! They're great aids to REPL
development.

However, you're unlikely to create your entire program in the REPL. In
the next section I'll cover everything you need to know to organize
your a real project.

## Real Project Organization

Up until now I've covered the building blocks of Clojure's
organization system. Now I'll show how to use them in real
projects. I'll cover:

* The relationship between file paths and namespace names
* Loading a file with `require` and `use`
* Using `ns` to set up a namespace

### The relationship between file paths and namespace names

To kill to birds with one stone (or feed two birds with one seed,
depending on how much of a hippie you are), our example will be used
to catch the pesky international cheese thief by mapping the locations
of his heists. Run the following:

```shell
lein new app the-divine-cheese-code
```

This should create a directory structure that looks like this:

```
| .gitignore
| doc
| | intro.md
| project.clj
| README.md
| resources
| src
| | the_divine_cheese_code
| | | core.clj
| test
| | the_divine_cheese_code
| | | core_test.clj
```

Now, open `src/the_divine_cheese_code/core.clj`. You should see this
on the first line:

```clojure
(ns the-divine-cheese-code.core
```

`ns` is the primary way you that create and manage namespaces within
Clojure. I'm going to explain it fully shortly. For now, though, this
line is very similar to the `in-ns` function we used above. It creates
a namespace if it doesn't exist and then switches to it.

The name of the namespace is `the-divine-cheese-code.core`. In
Clojure, there's a one-to-one mapping between a namespace name and the
path of the file where the namespace is declared, relative to the
source code's root:

* The source code's root is `src`
* Dashes in namespace names correspond with underscores in the
  filesystem. `the-divine-cheese-code` is mapped to
  `the_divine_cheese_code` on the filesystem
* The namespace component preceding every period (`.`) in a namespace
  name corresponds with a directory. `the_divine_cheese_code` is a
  directory.
* The final component of a namespace corersponds with a file with the
  `.clj` extension; `core` is mapped to `core.clj`.

Your project is going to have one more namespaces,
`the-divine-cheese-code.crimes.visualization`. Go ahead and create the
file for it now:

```shell
mkdir src/the_divine_cheese_code/visualization
touch src/the_divine_cheese_code/visualization/svg.clj
```

Notice that the filesystem path follows the rules outlined above. With
the relationship between namespaces and the filesystem down, let's
look at `require` and `use`.

### Loading a file with `require` and `use`

`src/the_divine_cheese_code/core.clj` is going to coordinate the work
of your project. It's going to use the functions in `svg.clj` to
create SVG markup, save it to a file, and open the file in a browser.
To use `svg.clj`'s functions, `core.clj` will have to *load* it. But
first, it makes sense to add some code to `svg.clj`. Make it look like
this (you'll add in more later):

```clojure
(ns the-divine-cheese-code.visualization.svg)

(defn- latlng->point
  [latlng]
  (str (:lat latlng) "," (:lng latlng)))

(defn points
  [locations]
  (clojure.string/join " " (map latlng->point locations)))
```

This just converts a vector of latitude/longitude coordinates into a
string of points.

In order to use this code, though, we have to `require` it. `require`
takes a symbol and *loads* code if it hasn't already been loaded.
Go ahead and require `the-divine-cheese-code.visualization.svg` by
changing `core.clj` so that it looks like this:

```clojure
(ns the-divine-cheese-code.core)
;; Load our SVG code
(require 'the-divine-cheese-code.visualization.svg)
;; Refer it so that we don't have to use the fully-qualified name to
;; reference svg functions
(refer 'the-divine-cheese-code.visualization.svg)

(def heists [{:location "Cologne, Germany"
              :cheese-name "x"
              :lat 50.95
              :lng 6.97}
             {:location "Zurich, Switzerland"
              :cheese-name "x"
              :lat 47.37
              :lng 8.55}
             {:location "Marseilles, France"
              :cheese-name "x"
              :lat 43.30
              :lng 5.37}
             {:location "Zurich, Switzerland"
              :cheese-name "x"
              :lat 47.37
              :lng 8.55}
             {:location "Vatican City"
              :lat 41.90
              :lng 12.45}])

(defn -main
  [& args]
  (println (points heists)))
```

If you run the project with `lein run` you should see

```shell
50.95,6.97 47.37,8.55 43.3,5.37 47.37,8.55 41.9,12.45
```

Hooray! You're one step closer to catching that purloiner of the
fermented curd! Using `require` successfully loaded
`the-divine-cheese-code.visualization.svg` for use.

The details of `require` are actually a bit complicated, and I'll
cover them in a later chapter, but for practical purposes you can
think of `require` as telling Clojure:

* Do nothing if you've already called `require` with this symbol
  (`the-divine-cheese-code.visualization.svg`). Otherwise...
* Find the file which corresponds with this symbol using the rules we
  described in "The relationship between file paths and namespaces"
  above. In this case, Clojure finds
  `src/the_divine_cheese_code/visualization/svg.clj`.
* Read and evaluate the contents of that file. Clojure expects the
  file to declare a namespace corresponding to its path (which ours
  does).

`require` also lets you alias a namespace when you require it:

```clojure
;; This:
(require '[the-divine-cheese-code.visualization.svg :as svg])

;; ...is equivalent to this:
(require 'the-divine-cheese-code.visualization.svg)
(alias 'svg 'the-divine-cheese-code.visualization.svg)

;; You can now use the aliased namespace
(svg/points heists)
; => "50.95,6.97 47.37,8.55 43.3,5.37 47.37,8.55 41.9,12.45"
```

Clojure provides another "shortcut". Instead of calling `require` and
`refer` separately, the function `use` does both:

```clojure
;; This:
(require 'the-divine-cheese-code.visualization.svg)
(refer 'the-divine-cheese-code.visualization.svg)

;; ...is equivalent to this:
(use 'the-divine-cheese-code.visualization.svg)
```

You can alias a namespace with `use` just like you can with `require`:

```clojure
;; This:
(use '[the-divine-cheese-code.visualization.svg :as svg :only (points)])

;; ...is equivalent to this:
(require 'the-divine-cheese-code.visualization.svg)
(refer 'the-divine-cheese-code.visualization.svg)
(alias 'svg 'the-divine-cheese-code.visualization.svg)

;; You can now access "points" with or without the aliased namespace
(= svg/points points)
; => true

;; You can access "latlng->point" as well
(= svg/latlng->point latlng->point)
; => true
```

The reason you might want to alias a namespace when using it is
because `use` takes the same options as `refer` above:

```clojure
;; This:
(use '[the-divine-cheese-code.visualization.svg :as svg :only points])

;; ...is equivalent to this:
(require 'the-divine-cheese-code.visualization.svg)
(refer 'the-divine-cheese-code.visualization.svg :only 'points)
(alias 'svg 'the-divine-cheese-code.visualization.svg)

;; This works as expected
(= svg/points points)
; => true

;; We can use the alias the reach latlng->point
svg/latlng->point
; => no exception

;: But we can't use the bare name
latlng->point
; => exception!
```

The takeaway here is that `require` and `use` load files and
optionally `alias` or `refer` their namespaces. This covers the main
use cases of `require` and `use`. As you write Clojure programs and
read code written by others, you might encounter even more ways of
writing `require` and `use`, at which point it'll make sense to read
Clojure's API docs to understand what's going on. However, what you've
learned so far should cover 95.3% of your needs.

Now it's time to look at the `ns` macro.

### Using `ns`

Everything we've covered so far &ndash; `in-ns`, `refer`, `alias`,
`require`, and `use` are actually hardly ever used in your source code
files. Normally, you would only use them when playing around in the
REPL. Instead, you'll use the `ns` macro. In the last section I showed
how `use` calls can also `refer` and `alias` namespaces. `ns` is
conceptually similar. In this section, you'll learn about how one `ns`
call can incorporate `require`, `use`, `in-ns`, `alias`, and `refer`.

We mentioned already that `ns` is like calling `in-ns`. `ns` also
defaults to referring the `clojure.core` namespace. That's why we can
call `println` from within `the-divine-cheese-code.core` without using
the fully qualified name, `clojure.core/println`.

You can control what gets referred from `clojure-core` with
`:refer-clojure`. `:refer-clojure` takes the same options as `refer`:

```clojure
;; This will break our code, causing us to have to use
;; clojure.core/println within the -main function:
(ns the-divine-cheese-code.core
  (:refer-clojure :exclude [println]))
```

Within `ns`, the form `(:refer-clojure)` is called a *reference*. This
might look weird to you. Is this reference a function call? A macro?
What is it? You'll learn fully how to make sense of it in the chapter
[Clojure Alchemy: Reading, Evaluation, and Macros](/read-and-eval).
For now, though, it should be sufficient to understand how each
reference maps to functions calls. For example, the above code is
equivalent to:

```clojure
(in-ns 'the-divine-cheese-code.core)
(refer 'clojure.core :exclude ['println])
```

There are six possible kinds of references within `ns`:

* `(:refer-clojure)`
* `(:require)`
* `(:use)`
* `(:import)`
* `(:load)`
* `(:gen-class)`

For now, I'm going to ignore `(:import)`, `(:load)`, and
`(:gen-class)`. They'll be covered in a later chapter.

`(:require)` works similarly to the require function. For example:

```clojure
;; This:
(ns the-divine-cheese-code.core
  (:require the-divine-cheese-code.visualization.svg))

;; ...is equivalent to this:
(in-ns 'the-divine-cheese-code.core)
(require 'the-divine-cheese-code.visualization.svg)
```

Notice that in the `ns` form you don't have to quote your symbol with
`'` You'll never have to quote symbols within `ns`.

You can also `alias` a lib that you `require` within `ns`, just like
when you call the function:

```clojure
;; This:
(ns the-divine-cheese-code.core
  (:require [the-divine-cheese-code.visualization.svg :as svg]))

;; ...is equivalent to this:
(in-ns 'the-divine-cheese-code.core)
(require ['the-divine-cheese-code.visualization.svg :as 'svg])

;; ... which is equivalent to this:
(in-ns 'the-divine-cheese-code.core)
(require 'the-divine-cheese-code.visualization.svg)
(alias 'svg 'the-divine-cheese-code.visualization.svg)
```

You can require multiple libs in a `(:require)` reference:

```clojure
;; This:
(ns the-divine-cheese-code.core
  (:require [the-divine-cheese-code.visualization.svg :as svg]
            [clojure.java.browse :as browse]))

;; ...is equivalent to this:
(in-ns 'the-divine-cheese-code.core)
(require ['the-divine-cheese-code.visualization.svg :as 'svg])
(require ['clojure.java.browse :as 'browse])
```

One difference between the `(:require)` reference and the `require`
function, however, is that the reference allows you to refer names:

```clojure
;; This:
(ns the-divine-cheese-code.core
  (:require [the-divine-cheese-code.visualization.svg :refer [points]]))

;; ...is equivalent to this:
(in-ns 'the-divine-cheese-code.core)
(require 'the-divine-cheese-code.visualization.svg)
(refer 'the-divine-cheese-code.visualization.svg :only ['points])

;; You can also refer all symbols (notice the :all keyword):
(ns the-divine-cheese-code.core
  (:require [the-divine-cheese-code.visualization.svg :refer :all]))

;; ...same as doing this:
(in-ns 'the-divine-cheese-code.core)
(require 'the-divine-cheese-code.visualization.svg)
(refer 'the-divine-cheese-code.visualization.svg)
```

This is the preferred way to require code, alias namespaces, and refer
symbols. It's recommended that you not use `(:use)`, but since it's
possible that you'll come across it, it's good to know how it works.
Here's an example:

```clojure
;; You know the drill. This:
(ns the-divine-cheese-code.core
  (:use clojure.java.browse))

;; Does this:
(in-ns 'the-divine-cheese-code.core)
(use 'clojure.java.browse)

;; Whereas this:
(ns the-divine-cheese-code.core
  (:use [clojure.java browse io]))

;; Does this:
(in-ns 'the-divine-cheese-code.core)
(use 'clojure.java.browse)
(use 'clojure.java.io)
```

Notice that when you follow `:use` with a vector, it takes the first
symbol as the "base" and then calls use with each symbol that follows.

Oh my god that's it! Now you can use `ns` like a pro! And you're going
to need to, dammit, because that "voleur des fromages" (as they
probably say in French) is still running amok! Remember him/her!?!?

## To Catch a Burglar

