--- 
title: Getting Started
link_title: Getting Started
kind: documentation
---

# Tools and Workflow

In order to learn Clojure it makes sense to invest some time up front
to get familiar with a set of tools and a workflow. This way you'll be
able to focus just on the code and concepts you're trying to learn
rather than struggling with problems with your Clojure development
setup.

Below, we'll briefly introduce Clojure. Next, we'll cover Leiningen,
the de-facto standard build tool for Clojure. Finally, we'll dive in
to installing and setting up Emacs for Clojure development. By the
end, you'll know how to do the following:

* Create a new Clojure project with Leiningen
* Build the project to create an executable jar file
* Execute the jar file
* Execute code in a Clojure REPL
* Integrate a Clojure REPL with Emacs
* Use Emacs productively to write Clojure

But before we jump into all that fun stuff, a note on Emacs. You can,
of course, use any editor you want. The primary reason I recommend
Emacs, however, is that it offers tight integration with a Clojure
REPL. This allows you to instantly try out your code as you write.
That kind of tight feedback loop will be useful both when learning
Clojure and, later, when writing real Clojure programs. If you don't
follow the thorough Emacs instructions below, then it's still
worthwhile to invest time in setting up your editor to work with a
REPL. A secondary reason for using Emacs is that it's really great for
working with any Lisp dialect; Emacs itself is written in a Lisp
dialect called Emacs Lisp.

OK, now that I've totally convinced you to use Emacs, let's get a
brief overview of Clojure!

## First things first: What is Clojure?

Clojure is actually two things! They are:

* Clojure the Language
    * Lisp dialect
    * Great support for concurrency and async programming
    * Path to enlightenment, etc.
* Clojure the Compiler
    * An executable JAR, `clojure.jar`
    * Takes code written in Clojure the Language and compiles it to
      Java Virtual Machine (JVM) bytecode
    * Source of confusion

This distinction is necessary because, unlike most programming
languages - Ruby, Python, C, a bazillion others - Clojure is a *hosted
language*. Clojure programs are executed within a Java Virtual Machine
and rely on the JVM for core features like threads and garbage
collection. We'll explore the relationship between Clojure and the JVM
more later on, but for now the main thing you need to understand is
this:

* The JVM executes Java bytecode. For example, you can view the Java
  programming language as a DSL for emitting Java bytecode.
* JAR files contain Java bytecode. You can refer to JAR files as Java
  programs.
* There is a Java program, `clojure.jar`, which, when executed, reads
  Clojure source code and produces Java bytecode
* That Java bytecode then gets executed by the JVM process which is
  already running `clojure.jar`

Now that we've got our heads straight regarding what Clojure is, let's
actually build a freakin Clojure program!

## Leiningen

Leiningen is the de-facto build tool for Clojure. You can read a
[full description of Leiningen](http://www.flyingmachinestudios.com/programming/how-clojure-babies-are-made-what-leiningen-is/),
but for now we only care about using it for four things:

1. Creating a new Clojure project
2. Running the Clojure project
3. Building the Clojure project
4. Using the REPL

Before going further, install Leiningen using the
[instructions from the Leiningen home page](http://leiningen.org/). 

### Creating a new Clojure Project

Let's create our first Clojure project:

```
lein new app clojure-noob
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
| | clojure_noob
| | | core.clj
| test
| | clojure_noob
| | | core_test.clj
```

* `project.clj` is a configuration file for Leiningen. It helps
  Leiningen answer questions like, "What dependencies does this
  project have?" and "When this Clojure program runs, what function
  should get executed first?"
* `src/clojure_noob/core.clj` is where we'll be doing our
  Clojure coding for awhile. In general, your source code will fall
  under `src/{project_name}`
* The `test` directory obviously contains tests.
* `resources` is a place for you to store assets like images; we won't
be using it for awhile.

So, `lein new` is a generator which 

### Running the Clojure project

Now let's actually run the project. First, open
`src/clojure_noob/core.clj` in your favorite editor. You should see
this:

```clojure
(ns clojure-noob.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!"))
```

Change line 9 so that it says this:

```clojure
(ns clojure-noob.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "I'm a little teapot!"))
```

Now, in your terminal, run this:

```
lein run
```

You should see the output, "I'm a little tea pot!"

### Building the Clojure Project

Now let's create a JAR file which you can distribute for big $$$. Run
this:

```
lein uberjar
java -jar target/clojure-noob-0.1.0-SNAPSHOT-standalone.jar
```

Look at that! `target/clojure-noob-0.1.0-SNAPSHOT-standalone.jar` is
your new, award-winning Clojure program which you can distribute and
run on almost any platform! Awesome!

We won't go into the details of how exactly Leiningen works but if
you're interested in the lower-level details of compiling and running
a Clojure program, you can check out
[my article series on the topic](http://www.flyingmachinestudios.com/programming/how-clojure-babies-are-made-the-java-cycle/).

For now, though, you have all the basic details you need to build,
run, and distribute (very) basic Clojure programs.

Before we move on to the Wonder and Glory of Emacs, let's go over one
last important tool: the REPL.

### Using the REPL

Run this:

```
lein repl
```

You should see output that looks like this:

```
nREPL server started on port 28925
REPL-y 0.1.10
Clojure 1.5.1
    Exit: Control+D or (exit) or (quit)
Commands: (user/help)
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
          (user/sourcery function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
Examples from clojuredocs.org: [clojuredocs or cdoc]
          (user/clojuredocs name-here)
          (user/clojuredocs "ns-here" "name-here")
clojure-noob.core=>
```

The last line, `clojure-noob.core=>`, tells you that you're in the
`clojure-noob.core` namespace. We won't get into namespaces now, but
you might recognize that namespace from your
`src/clojure_noob/core.clj` file. Try running your `-main` function:

```
clojure-noob.core=> (-main)
I'm a little tea pot!
nil
```

Cool! Try a few more basic Clojure functions:

```
clojure-noob.core=> (+ 1 2 3 4)
10
clojure-noob.core=> (* 1 2 3 4)
24
clojure-noob.core=> (first [1 2 3 4])
1
```

OK that's enough for now. Once we actually start learning Clojure the
Language, the REPL will be an invaluable tool. It will allow you to
instantly try out Clojure code.

At this point you should have the basic knowledge you need to begin
learning the Clojure language without having to fuss with tools.
