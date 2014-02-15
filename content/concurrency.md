---
title: Concurrency & Parallelism
link_title: Concurrency & Parallelism
kind: documentation
draft: true
---

One of Clojure's big selling points is its support for concurrent
programming. In this chapter, you'll learn the following:

* Intro to concurrency & parallelism
    * Definitions
    * Why should you care?
* Easy concurrent ops
* What are the dangers?
* The Clojure model of state
* Tool 1: Statelessness
* Tool 2: Atomic updates
* Tool 3: STM
* Tool 4: core.async

## Concurrent and Parallel Programming Concepts

Concurrent and parallel programming involves a lot of messy details at
all level of program execution, from the hardware to the operating
system to the programming language to the code that you're typing out
with your very own fingers. In this section we'll ignore most of those
details and instead focus on the concepts which will allow you to
reason correctly about your code 90% of the time.

### Definitions

To kick things off, let's define our terms.

*Concurrency* refers to *managing* more than one task at the same
time. We can illustrate concurrency with the song "Telephone" by Lady
Gaga. Gaga sings,

    I cannot text you with a drink in my hand, eh

Here, she's explaining that she can only manage one task (drinking).
She flat out rejects the suggestion that she manage more than one
task. However, if she decided to process tasks concurrently, she would
sing,

    I will put down this drink to text you,
    then put my phone away and continue drinking, eh

In this hypothetical universe, Lady Gaga is *managing* two tasks:
drinking and texting. However, she is not *executing* both tasks at
the same time. Instead, she's switching between the two. This is
called *interleaving*. Note that, when talking about interleaving, you
don't have to fully complete a task between switching; Gaga could type
one word, put her phone down, pick up her drink and have a sip, then
switch back to her phone and type another word.

*Parallelism* refers to *executing* more than one task at the same
time. If Madame Gaga were to execute her two tasks in parallel, she
would sing:

    I can text you with one hand
    while I use the other to drink, eh

Clojure has many features which allow you to easily achieve
parallelism. Whereas the Lady Gaga system achieves parallelism by
simultaneously executing tasks on multiple hands, computer systems
generally achieve parallelism by simultaneously executing tasks on
multiple cores or processors.

It's important to distinguish *parallelism* from *distribution*.
Distributed computing is a special kind of parallel computing where
the processors don't reside in the same computer. It'd be like Lady
Gaga asking Beyoncé, "Please text this guy while I drink."

I'm only going to use "parallel" to refer to cohabitating processors.
While Clojure does have libraries that aid distributed programming,
this book only covers parallel programming. That said, the concepts
you'll learn apply directly to distributed programming.

"Concurrent programming" and "parallel programming" refer to the
techniques used to manage the kinds of risks that arise when your
program executes more than one task at the same time.

Finally, I've been using the term "task" to refer to *logical*
sequences of operations which can be performed independently of each
other. Texting can be performed independently of pouring a drink into
your face, for example. So, "task" is an abstract term which says
nothing about implementation.

NOTE: Clarify this. Describe what a thread is? Create a visualization?

In Clojure, tasks are implemented as JVM *threads*. When you're
programming in Clojure, though, you will rarely need to manage threads
yourself.

Still, we're going to explore the nature of threads
throughout the chapter because there are risks involved in
multithreaded programming.

### Who Cares?

Why should you bother with any of this? The reason is that parallel
programming is essential for writing performant applications on modern
hardware. In recent years CPU designers have focused more on enabling
parallel execution than on increasing clock speed. This is mostly
because increasing clock speed requires exponentially more power,
making it impractical. You can google "cpu power wall", "cpu memory
wall" and "instruction-level parallelism wall" to find out more. As
those creepy Intel bunnies continue smooshing more and more cores
together, it's up to you and me to squueze performance out of them.

Performance can be measured as both *latency*, or the total amount of
time it takes to complete a task, and *throughput*, or the number of
tasks a program completes per second. Parallelism can improve both, as
this diagram shows:

TODO: diagram serial vs parallel execution of tasks

## Easy Concurrent Ops



* Easy concurrent ops
    * control when a task runs, its effect on the current thread, and
      how to get its val
    * future
    * delay
    * promise - show example of searching multiple sources, delivering
      first result
    * combine delay/future
    * show when blocking happens
    * explain that concurrent tasks are composed of sequential ops
* What are the dangers?
    * Commonly understood as shared access to mutable state
        * reference cell
        * mutual exclusion
        * dining philosophers
    * Clojure facilities understood as tools for safety and liveness
* Shared access to mutable state
    * How other languages define state and how that causes trouble
    * We need to define state
        * chain of related values associated with an identity
        * regardless of how you think the world actually works, you
          can agree that in computers we're dealing with information,
          and information is immutable
* Tactic one: statelessness
    * pure functions are parallelizable
    * immutable objects are parallelizable
        * explain how keys and indices can be seen as names
    * pmap
    * ppmap
    * reducers
* Tactic two: atomic updates
    * using `atom`
* Tactic three: STM
* Tactic 4: core.async
