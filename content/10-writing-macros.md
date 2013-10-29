--- 
title: "Writing Macros"
link_title: "Writing Macros"
kind: documentation
draft: true
---

# Writing Macros

When I was 18 I got a job as a night auditor at a hotel in Santa Fe,
New Mexico. Four nights a week from 11pm till 7am I would dutifully
check people in and out, fold laundry, and play my Playstation 2 in
the back room. All in all it was a decent job, providing 8 hours of
pay for 2 hours of work.

After a few months of this schedule, though, I had become a different
person. My emotions in particular had taken on a life of their own.
One night, around 3am, I was watching an infomercial for a product
claiming to restore men's hair. As I watched the story of one
formerly-bald individual, I became overwhelmed with joy. "At last!",
my brain said to itself, "This man has gotten the love and success he
deserves! What an incredible product this is, giving hope to the
hopeless!"

Throughout the intervening years I've found myself wondering if I
could somehow recreate the emotional abandon and appreciation for life
induced by chronic sleep deprivation and waging war against my
circadian rhythms. The ultimate solution would be some kind of potion
&mdash; a couple quaffs to unleash my inner Richard Simmons, but not
for too long.

Just as a potion would allow me to temporarily alter my fundamental
nature, macros allow you to modify Clojure in ways that just aren't
possible with other languages. With macros, you can extend Clojure to
suit your problem space, building up the language itself.

Which is exactly what we'll do in this chapter. After donning our
make-believe caps, we'll pretend that we run an online potion store.
We'll use macros to validate customer orders and perform data
transformations.

We'll thoroughly explore the art of writing macros in the process. By
the end, you'll understand:

* What macros are
* The tools used to write macros
    * quote
    * syntax quote
    * unqoute
    * unwrapping
    * macroexpand
* Gotchas
    * double eval
    * variable capture
* Why you'd want to use macros

## What Macros Are

In the last chapter we covered how
[Clojure evaluates data structures](/read-and-eval/#3__Evaluation).

Macros are a tool for allowing you to transform an arbitrary data
structure into one which can be evaluated by Clojure, effectively
allowing you to introduce new syntax. The result is that you can write
code which is more concise and meaningful.

Recall the `->` threading macro:

```clojure
;; vanilla Clojure
(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (io/resource path))))

;; using the threading macro
(defn read-resource
  [path]
  (-> path
      io/resource
      slurp
      read-string))
```

In this case, the `->` threading macro is a widely-applicable utility.
It makes your code more concise by allowing you to forego parentheses.
It makes your code more meaningful by communicating instantly that
you've created a function pipeline. And it's plain that it introduces
new syntax &mdash; you're expressing function application without all
those parentheses.

Macros allow Clojure to derive a lot of its "built-in" functionality
from a tiny core of functions and special forms. Take `when`, for
example. `when` has the general form:

```clojure
(when boolean-expression
  expression-1
  expression-2
  expression-3
  ...
  expression-x)
```

You might think that `when` is a special form like `if`. No one would
blame you for thinking this. In most other languages, conditional
expressions are built into the language itself and you can't add your
own. However, `when` is actually a macro:

```clojure
;; macroexpand takes a macro and returns the list which ends up being
;; evaluated by Clojure
(macroexpand '(when boolean-expression
                expression-1
                expression-2
                expression-3))
; =>
(if boolean-expression
  (do expression-1
      expression-2
      expression-3))

```

This shows that macros are an integral part of Clojure development
&mdash; they're even used to provide fundamental operations. Macros
are not some esoteric tool you pull out when you feel like being
awesome. There's no technical distinction between the "core" language
and operations which are provided by macros. As you learn to write
your own macros, you'll see how they allow you to extend the language
even further so that it fits the shape of your particular problem
domain.

## Anatomy of a Macro

Macro definitions look much like function definitions. They have a
name, an optional document string, an argument list, and a body. The
body will almost always return a list (remember that function calls,
special form calls, and macro calls are all represented as lists).
Here's a simple example:

```clojure
(defmacro postfix-notation
  "I'm too indie for prefix notation"
  [expression]
  (conj (butlast expression) (last expression)))
```

Notice that you have *full access to Clojure* within the macro's body.
You can use any function, macro, or special form within the macro
body. Let that simmer a bit: you have the full power of Clojure at
your disposal to extend Clojure. That's really cool!

You call macros just like you would a function or special form:

```clojure
(postfix-notation (1 1 +))
; => 2
```

You can use argument destructuring, just like you can with functions:

```clojure
(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  (list
   'do
   (list 'println "Great squid of Madrid, this is bad code:" (list 'quote bad))
   (list 'println "Sweet gorilla of Manila, this is good code:" (list 'quote good))))

(code-critic {:good (+ 1 1) :bad (1 + 1)})
```

You can also create multiple-arity macros, though I've never seen one
and most likely you shouldn't do it:

```clojure
(defmacro mutiple-arity
  ;; Notice that each arity's argument list and body is wrapped in parens
  ([single-arg]
     "Don't do this")
     
  ([arg1 arg2]
     "Seriously, don't do it :(")
     
  ([arg1 arg2 arg 3]
     "Nah, just kidding. Do whatever you want! Self-actualize!"))
```

Now that you're comfortable with the anatomy of macros and are well on
your way to self-actualization, let's strap ourselves to our thinking
masts Odysseus-style and look at how to write macro bodies. 

## Writing Tools

* It's about returning lists
* example: defnot
* example: bunch of infix
* writing "list" all the time is tedious
* examples
* when to use "do"
