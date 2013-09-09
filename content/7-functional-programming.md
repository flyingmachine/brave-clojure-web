--- 
title: Functional Programming
link_title: Functional Programming
kind: documentation
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
   external world" by changing external mutable objects or outputting
   to i/o.

These qualities matter because they make it easier for us to reason
about our programs. Pure functions are easier to reason about because
they're completely isolated, concealing no dependencies on other parts
of your system. When you use them, you don't have to ask yourself,
"Ok, what could I break by calling this function?" You don't have to
spend time hunting around your codebase and cramming additional
information into your limited-capacity short-term memory. For example,
w hen was the last time you fretted over adding two numbers?

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
function.

This is especially important if your function is used multiple places
or if it's nested deeply in a chain of function calls. In both cases,
you can rest easy knowing that changes to external conditions won't
cause your code to break.

### Pure Functions Have No Side Effects

To perform a side effect is to change the association between a name
and its value within a given scope.

For example, in Javscript:

```ruby
var haplessObject = {
  emotion: "Carefree!"
};

var evilMutator = function(object){
  object.emotion = "So emo :(";
}

evilMutator(haplessObject);
haplessObject.emotion
// => "So emo :("
```

Of course, your program has to have some side effects; it writes to a
disk, which is changing the association between a filename and a
collection of disk sectors; it changes the rgb values of your
monitor's pixels, etc. Otherwise, there'd be no point in running it.

The reason why side effects are potentially harmful is that they
prevent us from being certain what the names in our code are referring
to. This makes it difficult or impossible to know what our code is
doing. It's very easy to end up wondering how a name came to be
associated with a value and it's usually difficult to figure out why.

When you call a function which doesn't have side effects, you only
have to consider the relationship between the input and the output.

Functions which have side effects, however, place more of a burden on
your short term memory: now you have to worry about how the world is
affected when you call the function. Not only that, every function
which calls a side-effecting function gets "infected". It's another
component which requires extra care and thought as you build your
program.

If you have any significant experience with a language like Ruby or
Javascript, you've probably run into this problem. As an object gets
passed around, its attributes somehow get changed and you can't figure
out why. Then you have to buy a new computer because you've chucked
yours out the window. If you've read anything about object-oriented
design, you'll notice that a lot of words have been devoted to
strategies for managing state and reducing side effects for just this
reason.

Therefore, it's a good idea to look for ways to limit the use of side
effects in your code. Clojure goes to great lengths to limit side
effects &mdash; all of its core data structures are immutable. You
cannot change them in place no matter how hard you try! We'll explore
these mighty, indestructible data structures in greater detail later
in this chapter.

To sum things up, pure functions are idempotent and side-effect free.
This makes them easy to reason about. Try to keep your dirty, impure
functions to a minimum.

## Pure Functions Give You Power

Because you only need to worry about the input/output relationship in
pure functions, it's safe to compose them. Indeed, you will often see
code that looks something like this:

```clojure
(defn dirty-html->clean-md
  [dirty-html]
  (html->md (tidy (clean-chars dirty-html))))
```

This practice is so common, in fact, that there's a function for
composing functions, `comp`:

```clojure
((comp clojure.string/lower-case clojure.string/trim) " Unclean string ")
; => "unclean string"
```

The Clojure implementation of this function can compose any number of
functions. Here's an implementation which composes just two functions:

```clojure
(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))
```

I encourage you to try this out! Also, try re-implementing Clojure's
`comp` so that you can compose any number of functions.

Another cool thing you can do with pure functions is memoize them.
Pure functions are *referentially transparent*, which means that, for
any given set of arguments, you can replace a function call with its
return value. Example:

```clojure
;; + is referentially transparent. You can replace this...
(+ 3 (+ 5 8))

;; ...with this...
(+ 3 13)

;; ...or this...
16

;; and the program will have the same behavior
```

Memoization lets you take advantage of referential transparency by
storing the arguments passed to a function and the return value of the
function. Every subsequent call to the memoized function returns the
stored value:

```clojure
(defn sleepy-identity
  "Returns the given value after 1 second"
  [x]
  (Thread/sleep 1000)
  x)
(sleepy-identity "Mr. Fantastico")
; => "Mr. Fantastico" after 1 second
(sleepy-identity "Mr. Fantastico")
; => "Mr. Fantastico" after 1 second

;; Only sleeps once and returns the given value immediately on every
;; subsequent call
(def memo-sleep-identity (memoize sleepy-identity))
(memo-sleepy-identity "Mr. Fantastico")
; => "Mr. Fantastico" after 1 second
(memo-sleepy-identity "Mr. Fantastico")
; => "Mr. Fantastico" immediately
```

Pretty cool!

## Living with Immutable Data Structures

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
