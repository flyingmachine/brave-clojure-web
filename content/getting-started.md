---
title: Building, Running, and The REPL
link_title: Building, Running, and The REPL
kind: documentation
---

# Building, Running, and The REPL

In this chapter you'll invest a small amount of time up front to get
familiar with a quick, problem-free way to build and run Clojure
programs. There's something powerful and motivating about getting a
real program running. Once you can do that, you're free to experiment
and you can actually share your work!

You'll also learn how to instantly try out code within a running
Clojure process using a REPL. This will allow you to quickly test your
understanding of the language, allowing you to learn more efficiently.

In order to get there, I'll first briefly introduce Clojure. I think
you'll agree that that's a useful topic in a Clojure book! Next, I'll
cover Leiningen, the de-facto standard build tool for Clojure. By the
end, you'll know how to do the following:

- Create a new Clojure project with Leiningen
- Build the project to create an executable jar file
- Execute the jar file
- Execute code in a Clojure REPL

## First things first: What is Clojure?

Clojure was forged in a mythic volcano by Rich Hickey. Using an alloy
of lisp, functional programming, and a lock of his own epic hair, he
crafted a language which has proven to be delightful yet powerful. Its
lisp heritage gives you the power to write code more expressively than
is possible in most non-lisp languages. Its distinct take on
functional programming will sharpen your thinking as a programmer
*and* give you better tools for tackling complex domains – like
concurrent programming – which are traditionally known to drive
developers into years of therapy. All of which you'll learn about in
this book.

Clojure continues to evolve. As of this writing, it's at
version 1.6.alpha3 and development is going strong.

When talking about Clojure, it's important to keep in mind that Clojure
is actually two things! They are:

- Clojure the Language
    - Lisp dialect with functional emphasis
    - Great support for concurrency and asynchronous programming
    - Path to enlightenment, etc.
- Clojure the Compiler
    - An executable JAR, `clojure.jar`
    - Takes code written in Clojure the Language and compiles it to
      Java Virtual Machine (JVM) bytecode
    - Source of confusion

This distinction is necessary because, unlike most programming
languages - Ruby, Python, C, a bazillion others - Clojure is a *hosted
language*. Clojure programs are executed within a Java Virtual Machine
and rely on the JVM for core features like threading and garbage
collection. We'll explore the relationship between Clojure and the JVM
more later on, but for now the main thing you need to understand is
this:

- The JVM executes Java bytecode. For example, you can view the Java
  programming language as a DSL for emitting Java bytecode.
- JAR files contain Java bytecode. You can refer to JAR files as Java
  programs.
- There is a Java program, `clojure.jar`, which, when executed, reads
  Clojure source code and produces Java bytecode.
- That Java bytecode then gets executed by the JVM process which is
  already running `clojure.jar`.

Now that we've got our heads straight regarding what Clojure is, let's
actually build a freakin' Clojure program!

## Leiningen

Leiningen is a Clojure program which has become the de-facto standard
build tool for Clojure. You can read a [full description of Leiningen](http://www.flyingmachinestudios.com/programming/how-clojure-babies-are-made-what-leiningen-is/),
but for now we only care about using it for four things:

1.  Creating a new Clojure project
2.  Running the Clojure project
3.  Building the Clojure project
4.  Using the REPL

Before going further, install Leiningen using the
[instructions from the Leiningen home page](http://leiningen.org/).

### Creating a new Clojure Project

Let's create our first Clojure project:

```sh
lein new app clojure-noob
```

This should create a directory structure that looks like this:

```
| .gitignore
| doc
| | intro.md
| project.clj
| resources
| README.md
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

- `project.clj` is a configuration file for Leiningen. It helps
  Leiningen answer questions like, "What dependencies does this
  project have?" and "When this Clojure program runs, what function
  should get executed first?"
- `src/clojure_noob/core.clj` is where we'll be doing our
  Clojure coding for awhile. In general, your source code will fall
  under `src/{project_name}`
- The `test` directory obviously contains tests.
- `resources` is a place for you to store assets like images; we won't
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
  (println "Hello, World!"))
```

Change line 9 so that it says this:

```clojure
(ns clojure-noob.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "I'm a little teapot!"))
```

Now, in your terminal, make sure you're in the `clojure-noob`
directory:

```sh
cd clojure-noob
```

From here, you can run your Clojure program:

```sh
lein run
```

You should see the output, "I'm a little teapot!" Congratulations,
little teapot!

As you go through the book you'll learn what's actually happening
here, but for now all you need to know is that you created a function,
`-main`, and that function gets run when you execute `lein run` at the
command line. You can modify `-main` and execute `lein run` to
experiment with Clojure.

### Building the Clojure Project

Now let's create a JAR file which you can distribute for big $$$. Run
this:

```sh
lein uberjar
java -jar target/clojure-noob-0.1.0-SNAPSHOT-standalone.jar
```

Look at that! `target/clojure-noob-0.1.0-SNAPSHOT-standalone.jar` is
your new, award-winning Clojure program which you can distribute and
run on almost any platform! Awesome!

We won't go into the details of how exactly Leiningen works but if
you're interested in the lower-level details of compiling and running
a Clojure program, you can check out [my article series on the topic](http://www.flyingmachinestudios.com/programming/how-clojure-babies-are-made-the-java-cycle/).

For now, though, you have all the basic details you need to build,
run, and distribute (very) basic Clojure programs.

Before we move on to the next chapter on the Wonder and Glory of
Emacs, let's go over one last important tool: the REPL.

### Using the REPL

REPL stands for "Read-Eval-Print Loop" and it's a tool for
experimenting with code. It presents you with a prompt and you type
code into it. It then *reads* your input, *evaluates* it, *prints* the
result, and *loops*, presenting you with a prompt again.

The REPL is considered an essential tool for lisp developpment,
development, and I strongly recommend you make use of it. It will let
you quickly check your understanding of the language and explore
ideas.

To start a REPL, run this:

```sh
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

```clojure
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

-   [Clojure development with Sublime Text 2 (youtube)](http://www.youtube.com/watch?v=wBl0rYXQdGg)
-   [Writing Clojure with Vim in 2013](http://mybuddymichael.com/writings/writing-clojure-with-vim-in-2013.html)
-   [Counterclockwise](https://code.google.com/p/counterclockwise/) is a highly-recommended Eclipse plugin
-   [Getting Started with La Clojure, a plugin for IntelliJ](http://wiki.jetbrains.net/intellij/Getting_started_with_La_Clojure)

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
