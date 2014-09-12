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
Machine (JVM), granting it two fundamental characteristics. First, a
Clojure application is a Java application when you're compiling and
running it. Second, you need to use Java objects for core
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
Machine. In the second, it refers an instance that's actually running.
Right now, we're only concerned with the JVM model; I'll point out
when we're talking about running JVM processes.

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
translate programs into the instructions that a CPU will
understand. The CPU doesn't care what programming language was used to
produce the instructions, all it cares about is machine instructions.

As a *virtual* machine, the JVM provides an isolated environment that
acts like a computer but that runs as a process on top of a host
machine. The JVM provides its own instruction set, Java bytecode. Just
as a CPU doesn't care how machine instructions are generated, the JVM
doesn't care how bytecode gets created. A running JVM process reads
bytecode and translates it on the fly into the machine code that its
host will understand, a process called just-in-time compilation.

So, what is the Java Virtual Machine?  One hint is in "virtual
machine" part of the
name. [Here's what wikipedia says about virtual machines](http://en.wikipedia.org/wiki/Virtual_machine):

    A virtual machine (VM) is a software implementation of a machine
    (for example, a computer) that executes programs like a physical
    machine.

    ...A *process* virtual machine (also, language virtual machine) is
    designed to run a single program, which means that it supports a
    single process. Such virtual machines are usually closely suited
    to one or more programming languages and built with the purpose of
    providing program portability and flexibility (amongst other
    things). An essential characteristic of a virtual machine is that
    the software running inside is limited to the resources and
    abstractions provided by the virtual machine—it cannot break out
    of its virtual environment.

All of this applies to the JVM: it's a software implementation

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
        * compiling to bytecode
        * classpath
        * the ecosystem around java programs
