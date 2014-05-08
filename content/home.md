---
title: Clojure for the Brave and True, an Online Book for Beginners
link_title: Introduction
kind: documentation
notoc: true
---

# Introduction

\**Clojure!*\*

For weeks, *months* &mdash; no! *from the very moment you were born*
&mdash; you've felt it calling to you. Every time you've held your
keyboard aloft, crying out in anguish over an incomprehensible class
hierarchy; every time you've lain awake at night, disturbing your
loved ones with sobs over a mutation-induced heisenbug; every time a
race condition has caused you to pull out more of your ever-dwindling
hair, some secret part of you has known that *there has to be a better
way*.

Now, at long last, the instructional material you have in front of
your face will unite you with the programming language you've been
longing for.

Are you ready, brave reader? Are you ready to meet your true destiny?
Grab your best pair of parentheses: you're about to embark on the
journey of a lifetime!

## Journey through the Four Labyrinths

To wield Clojure to its fullest, you will need to find your way
through the four labyrinths confronting every programmer learning a
new language:

- **The Cave of Artifacts.** In its depths you'll learn to build, run,
  and distribute your own programs and use the libraries of others.
  You'll learn Clojure's relationship to the JVM (Java Virtual
  Machine).
- **The Forest of Tooling.** It's paramount to set up your environment
  so that you can quickly try things out and learn from them.
- **The Mountain of Language.** As you ascend, you'll gain knowledge
  of Clojure's syntax, semantics, and data structures.
- **The Cloud Castle of Mindset.** In its rarified air you will come
  to know the why and how of lisp and functional programming.

Make no mistake: you *will* work. But this book will make the work
feel exhilarating, not exhausting. That's because this book does three
things:

- It takes the "dessert-first" approach, giving you the development
  tools and language details needed to start playing with real
  programs immediately
- It assumes 0 experience with the JVM, functional programming, or
  lisp. It covers these topics in detail so that you'll feel like you
  know what what you're doing when you write Clojure code
- It eschews "real-world" examples in favor of more interesting
  exercises like "assaulting hobbits" and "tracking glittery vampires"

By the end, you'll be able to use Clojure, one of the most exciting
and fun programming languages in existence!

## Part 1: Environment Setup

In order to stay motivated and learn efficiently, you need to be able
to actually try things out and build executables. No running code in a
browser for you!

### Building, Running and the REPL

There's something powerful and motivating about getting a real program
running. Once you can do that, you're free to experiment and you can
actually share your work!

In this short chapter, you'll invest a small amount of time up front
to get familiar with a quick, problem-free way to build and run
Clojure programs. You'll also learn how to instantly experiment with
code within a running Clojure process using a REPL. This will tighten
your feedback loop, allowing you to learn more efficiently.

[Read "Building, Running, and The REPL"](/getting-started/)

### Using Emacs for Clojure development

Having a quick feedback loop is so important for learning that we
cover Emacs from the ground up so that you're guaranteed to have an
efficient Emacs/Clojure workflow.

[Read "Start Using Emacs"](/basic-emacs/)
&bull;
[Read "Using Emacs with Clojure"](/using-emacs-with-clojure/)

## Part 2: Language Fundamentals

The goal of this section is to provide you with a solid foundation on
which to continue learning Clojure. You'll start by learning how to
actually do things, and then take a step back to understand the
underlying concepts.

### Do Things

While you've undoubtedly heard of Clojure's awesome concurrency
support and other stupendous features, Clojure's most salient
characteristic is that it is a Lisp. We're going to explore this Lisp
core. And this core is comprised of two parts: functions and data.

[Read "Do Things"](/do-things/)

### Core Functions in Depth

In this chapter, you'll learn about a couple of Clojure's underlying
concepts. This will give you the grounding you need to read the
documentation for functions you haven't used before and to understand
what's happening when you give them a try.

You'll also see usage examples of the functions you'll be reaching for
the most. This will give you a solid foundation for writing your own
code and reading and learning from others' projects.

[Read "Core Functions in Depth"](/core-functions-in-depth/)

### Functional Programming

In this chapter, you'll begin to take your concrete experience with
functions and data structures and integrate it in a new mindset, the
functional programming mindset.

[Read "Functional Programming"](/functional-programming/)

### Clojure Alchemy: Reading, Evaluation, and Macros

In this chapter, we'll take a step back and describe how Clojure runs
your code. This will give you the conceptual structure needed to truly
understand how Clojure works and how it's different from other,
non-lisp languages. With this structure in place, we'll introduce the
macro, one of the most powerful tools in existence.

[Read "Clojure Alchemy: Reading, Evaluation, and Macros"](/read-and-eval/)

### Writing Macros

This chapter thoroughly examines how to write macros starting with
basic examples and moving up in complexity. We'll close by donnig our
make-believe caps, pretending that we run an online potion store and
using macros to validate customer orders.

[Read "Writing Macros"](/writing-macros/)
