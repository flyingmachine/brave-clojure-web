--- 
title: Functional Programming
link_title: Functional Programming

---

# Functional Programming

If I were Mr. Miyagi and you were Daniel-san, the last chapter would
be the equivalent of "wax on, wax off." Also, why haven't you cleaned
my car!?!?

In this chapter, you'll begin to take your concrete experience with
functions and data structures and integrate it in a new mindset, the
functional programming mindset. By the end of this chapter, you'll
have learned:

* What pure functions are and why they're important
* Why immutability matters
* How to transform immutable values
* How disentangling data and functions gives you more power and
  flexibility
* Why it's powerful to program to a small set of data abstractions

The result of shoving all this knowledge into your brain matter is
that you'll have an entirely new approach to problem solving!

## Pure Functions

With the exception of `println`, all the functions we've used up till
now have been pure functions:

```clojure
;; Pure functions:
(get [:chumbawumba] 0)
; => :chumbawumba

(reduce + [1 10 5])
; => 6

(str "wax on " "wax off")
; => "wax on wax off"
```

What makes them pure functions, and why does it matter?

A function if pure if it meets two qualifications:

1. It always returns the same result given the same argument. This is
   call "idempotence" and you can add it to your list of five-dollar
   programming words.
2. It doesn't cause any side effects, e.g. it doesn't "change the
   world" by changing mutable objects or outputting to i/o.

These qualities matter because they make it easier for us to reason
about our programs. Pure functions are easier to reason about because
they're completely isolated, concealing no dependencies on other parts
of your system. When you use them, you don't have to ask yourself,
"Ok, what could I break by calling this function?" You don't have to
spend time hunting around your codebase and cramming additional
information into your limited-capacity short-term memory. I mean, when
was the last time you fretted over adding two numbers?

Let's look at idempotence and lack-of-side-effects in more detail so
that know exactly what they are how they're helpful.

### Pure Functions Are Idempotent

Idempotent functions only rely on 1) their own arguments and 2)
immutable values to determine their return value. The result is that
calling the same function multiple times with the same arguments
always yields the same result:

```clojure
;; Mathematical functions are idempotent
(+ 1 2)
; => 3

;; If a function relies on an immutable value, it's idempotent.
;; The string ", Daniel-san" is immutable, so the function is idempotent
(defn wisdom
  [words]
  (str words ", Daniel-san"))
```

By contrast, these functions do not yield the same result with the
same arguments and, therefore, are not idempotent:

```clojure
;; Any function which relies on a random number generator
;; cannot be idempotent
(defn random-judgment
  [judgee]
  (if ((rand) > 0.5)
    (str judgee " is great!")
    (str judgee " is terrible :(")))

;; If your functions reads from a file, it's not idempotent because
;; the file's contents can change
(defn file-analyzer
  [filename]
  (let [contents (slurp filename)]
    (analyze-file contents))
;; Note, however, that "analyze-file" could very well be idempotent -
;; it could very well return the same result every time it's passed
;; the same string.
```

When using an idempotent function, you never have to consider what
possible external conditions could affect the return value of the
function. This is especially important if your function is used
multiple places or if it's nested deeply in a chain of function calls.

In both cases, you can rest easy knowing that changes to external
conditions won't cause your code to break.

### Pure Functions Have No Side Effects

<!---
pure functions ->
no side effects ->
how to do things?

data all the things ->
why? ->
isolation ->
composability ->
reusability ->
minimize knowledge ->
disentangling data and functions give you more power and flexibility ->
-->
