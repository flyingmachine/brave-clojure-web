---
title: All the Java You Need
link_title: All the Java You Need
kind: documentation
draft: true
---

# All the Java You Need

There comes a day in every Clojurist's life when she must venture
forth from the sanctuary of pure functions and immutable data
structures into the wild and barbaric Land of Java. This treacherous
journey is necessary because Clojure is hosted on the Java Virtual
Machine (JVM), granting it two fundamental characteristics. First, you
run Clojure applications the same way you run Java
applications. Second, you need to use Java objects for core
functionality like reading files and working with dates. In this way,
Clojure is a bit like a utopian community plunked down in the middle
of a non-utopian country. It's preferable to interact with other
utopians, but every once in a while you need to talk to the locals in
order to get things done.

If this chapter were a guide book for the Land of Java, it'd be
somewhere between a phrasebook ("Where is the bathroom?") and a
full-blown course on the local language and customs. It will give you
an overview of what the JVM is, how it runs programs, and how to
compile programs for it. It will also give you a tour of
frequently-used Java classes and methods and explain how to interact
with them from Clojure. More than that, it will show you how to think
about and understand Java so that you can incorporate any Java library
into your Clojure program.

# The JVM

Developers use the term "JVM" to refer to a few different
things. You'll hear them say, "Clojure runs on *the* JVM", and you'll
also hear "Clojure programs run in *a* JVM". In the first case, "JVM"
refers to an abstraction - the general movel of the Java Virtual
Machine. In the second, it refers to a process, an instance of a
running program.  Right now, we're only concerned with the JVM model;
I'll point out when we're talking about running JVM processes.

## Java Bytecode

To understand the Java Virtual Machine, let's first take a step back
and review how plain-ol' machines (also known as computers) work. Deep
in the cockles of a computer's heart is its CPU, and the CPU's job is
to execute operations like "add" and "unsigned multiply". These
operations are represented in assembly language by mnemonics like
"ADD" and "MUL". What operations are availabe depends on the CPU
architecture (x86, ARMv7 and what have you) as part of the
architecture's *instruction set*.

Because it's no fun to program in assembly language, we humans have
invented higher-level languages like C and C++, which get compiled
into the instructions that a CPU will understand. Here's a diagram of
the process:

1. Compiler reads source code.
2. Compiler outputs a file containing CPU instructions
3. The computer executes those instructions

The most important thing here is that, ultimately, you have to
translate programs into the instructions that a CPU will understand,
and the CPU doesn't care what programming language was used to produce
the instructions.

The JVM is analagous to a CPU in that it also executes low-level
instructions, called Java bytecode. As a *virtual* machine, though,
it's implemented as software rather than hardware. A running JVM
executes bytecode by translating it on the fly into the machine code
that its host will understand, a process called just-in-time
compilation.

For a program to run on the JVM, then, it has to get compiled to Java
bytecode. Usually, when you compile programs you store the result as a
JAR (Java archive) file. Just as a CPU doesn't care how machine
instructions are generated, the JVM doesn't care how bytecode gets
created. It doesn't care if you used Scala, JRuby, Clojure, or even
Java to create Java bytecode. Here's a diagram:

1. Compile to bytecode
2. JVM reads bytecode
3. JVM sends machine instructions

So when someone says that Clojure runs on the JVM, one of the things
they mean is that Clojure programs get compiled to Java bytecode and
JVM processes execute them. This matters because you treat Clojure
programs the same as Java programs from an operations perspective. You
compile them to JAR files and run them using the `java` command. If a
client needs a program that runs on the JVM, you could secretly write
it in Clojure instead of Java and they would be none the wiser. Seen
from the outside, you can't tell the difference between a Java and a
Clojure program any more than you can tell the difference between a C
and a C++ program.

Let's go ahead and actually create a simple Java program and then take
a peek at Clojure's Java implementation. This will help you feel much
more comfortable with the JVM, and it will prepare you for the
upcoming section on Java interop.

# Compiling and Running a Java Program



# Notes

* You use local building materials (protocols, interfaces, file and
  date objs, compiling, exec jar file)
* Two perspectives:
    * the java objects perspective
        * Quick Java intro
        * instantiating
        * calling methods 
            * dot op
            * doto
        * class methods
        * navigating documentation
        * importing
        * commonly used classes
            * File
            * Date
            * Readers
            * Writers
            * System: getenv, exit, etc
    * the ops perspective
        * what it means to execute a java program (bytecode, jvm)
        * JVM as a platform (lots of library, analogous to GNU on
          linux)
        * compiling to bytecode
        * classpath
        * the ecosystem around java programs
