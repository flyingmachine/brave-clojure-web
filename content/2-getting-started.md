--- 
title: Running, Building, and The REPL
link_title: Building, Running, and The REPL
kind: documentation
---

# Building, Running, and The REPL

There's something powerful and motivating about getting a real program
running. Once you can do that, you're free to experiment and you can
actually share your work!

In this chapter, you'll invest a small amount of time up front to get
familiar with a quick, problem-free way to build and run Clojure
programs. You'll also learn how to instantly experiment with code
within a running Clojure process using a REPL. This will tighten your
feedback loop, allowing you to learn more efficiently.

Below, we'll briefly introduce Clojure. Next, we'll cover Leiningen,
the de-facto standard build tool for Clojure. By the end, you'll know
how to do the following:

* Create a new Clojure project with Leiningen
* Build the project to create an executable jar file
* Execute the jar file
* Execute code in a Clojure REPL

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
and rely on the JVM for core features like threading and garbage
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

Leiningen is a Clojure program which has become the de-facto standard
build tool for Clojure. You can read a
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

There's nothing inherently special or Clojure-y about this project
skeleton. It's just a convention used by Leiningen. You'll be using
Leiningen to build and run Clojure apps, and Leiningen expects your
app to be laid out this way. Here's the function of each part of the
skeleton:

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

You should see the output, "I'm a little teapot!" Congratulations,
little teapot!

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

Before we move on to the next chapter on the Wonder and Glory of
Emacs, let's go over one last important tool: the REPL.

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
`src/clojure_noob/core.clj` file. Try executing your `-main` function:

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

Awesome!

Conceptually, the REPL is similar to SSH. In the same way that you can
use SSH to interact with a remote server, the Clojure REPL allows you
to monkey around with a running Clojure process. This can be very
powerful, as you can even attach a REPL to a live, production app and
modify your program as it runs. For now, though, we'll be using the
REPL to build our knowledge of Clojure syntax and semantics.

At this point you should have the basic knowledge you need to begin
learning the Clojure language without having to fuss with tools. To
learn how to interaact with Clojure with even more proficiency,
however, we'll cover Emacs in depth.

If Emacs isn't your cup of tea, here are some resources for setting
up other text editors for Clojure development:

* [Clojure development with Sublime Text 2 (youtube)](http://www.youtube.com/watch?v=wBl0rYXQdGg)
* [Writing Clojure with Vim in 2013](http://mybuddymichael.com/writings/writing-clojure-with-vim-in-2013.html)
* [Counterclockwise](https://code.google.com/p/counterclockwise/) is a
  highly-recommended Eclipse plugin
* [Getting Started with La Clojure, a plugin for IntelliJ](http://wiki.jetbrains.net/intellij/Getting_started_with_La_Clojure)

## Chapter Summary

I'm so proud of you, little teapot. You've run your first Clojure
program! Not only that, you've become acquainted with the REPL, one of
the most important tools for developing Clojure software. Amazing!
It brings to mind the immortal lines from Taylor Swift's master
songwriting:

```
You held your head like a hero
On a history book page
It was the end of a decade
But the start of an age

- Taylor Swift, Voice of Our Age
```

Bravo!
