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
    * gensym
    * autogensym
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
;; macroexpand takes a macro application and returns the list which
;; ends up being evaluated by Clojure
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

One key difference between functions and macros is that function
arguments are fully evaluated before they're passed to the function,
whereas macros receive arguments as unevaluated data structures.

We can see this in the above example. If you tried evaluating `(1 1
+)`, you would get an exception. However, since you're making a macro
call, the unevaluated list `(1 1 +)` is passed to `postfix-notation`.
We can thus use `conj`, `butlast`, and `last` functions to rearrange
the list so that it's something Clojure can evaluate:

```clojure
(macroexpand '(postfix-notation (1 1 +)))
; => (+ 1 1)
```

Continuing with our anatomical adventures, macro definitions can use
argument destructuring, just like you can with functions:

```clojure
(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))

(code-critic {:good (+ 1 1) :bad (1 + 1)})
; =>
Great squid of Madrid, this is bad code: (1 + 1)
Sweet gorilla of Manila, this is good code: (+ 1 1)
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

## Building Lists for Evaluation

Macro-writing is all about building a list to be evaluated by Clojure
and it requires a kind of inversion to your thinking normal way of
thinking. In particular, you'll need to be extra careful about the
difference between a *symbol* and its *value*.

### Be Careful about Distinguishing Symbols and Values

Let's take the `postfix-notation` example:

```clojure
(defmacro postfix-notation
  [expression]
  (conj (butlast expression) (last expression)))

(macroexpand '(postfix-notation (1 1 +)))
; => (+ 1 2)

(postfix-notation (1 1 +))
; => 2
```

When you pass the argument `(1 1 +)` to the `postfix-notation` macro,
the value of `expression` within the macro body is a list comprised of
three elements: the number one, the number one, and the symbol `+`.
Note that the *symbol* `+` is distinct from its *value*, which is the
addition function.

The `postfix-notation` macro returns a new list comprised of the `+`
symbol, `1`, and `1`. This list is then evaluated and the result is
returned.

###  Simple Quoting

You'll almost always use quoting within your macros. This is so that
you can obtain an unevaluated symbol. Here's a brief refresher on
quoting:

```clojure
;; No quoting
(+ 1 2)
; => 3

;; Quoting returns unevaluated data structure
;; + is a symbol in the returned list
(quote (+ 1 2))
; => (+ 1 2) 

;; again, a symbol
(quote +)
; => + 

;; quoting returns a symbol regardless of whether the symbol
;; has a value associated with it
(quote a)
; => a 

;; The single quote character is a shorthand for (quote x)
;; This example works just like (quote (+ 1 2))
'(+ 1 2)
; => (+ 1 2)
```

We can see quoting at work in the `when` macro:

```clojure
;; This is when's actual source
(defmacro when
  "Evaluates test. If logical true, evaluates body in an implicit do."
  {:added "1.0"}
  [test & body]
  (list 'if test (cons 'do body)))

(macroexpand '(when (the-cows-come :home)
                (call me :pappy)
                (slap me :silly)))
; =>
(if (the-cows-come :home)
  (do (call me :pappy)
      (slap me :silly)))
```

Notice that both `if` and `do` are quoted. That's because we want
these symbols to be in the final list returned for evaluation by
`when`.

Here's another example:

```clojure
(defmacro unless
  "Inverted 'if'"
  [test & branches]
  (conj (reverse branches) test 'if))

(macroexpand '(unless (done-been slapped? me)
                      (slap me :silly)
                      (say "I reckon that'll learn me")))
; =>
(if (done-been slapped? me)
  (say "I reckon that'll learn me")
  (slap me :silly))
```

Again, we have to quote `if` because we want the unevaluated symbol to
be placed in the resulting list.

### Syntax Quoting

So far we've built up our lists by using `'` (quote) and functions
which operate on lists (`conj`, `butlast`, `first`, etc), and by using
the `list` function to create a list. Indeed, you could write your
macros that way until the cows come home. Sometimes, though, it leads
to tedious and verbose code. Take the `code-critic` example we
introduced above:

```clojure
(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))
```

How tedious! I feel like I'm falling asleep from the tedium. Somebody
slap me awake already! Luckily, Clojure has a handy mechanism for
solving this problem: the syntax quote! Here's how we would rewrite
`code-critic` using syntax quote:

```clojure
(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))
```

There's a lot going on here, though, so let's take a step back and
break down the syntax quote.

First, syntax quoting can return unevaluated data structure similarly
to quoting. There's one important difference, though: syntax quoting
will return the *fully qualified* symbols so that the symbol includes
its namespace:

```clojure
;; Quoting does not include a namespace unless your code includes a namespace
'+
; => +

;; Write out the namespace and it'll be returned
'clojure.core/+
; => clojure.core/+

;; Syntax quoting will always include the symbol's full namespace
`+
; => clojure.core/+

;; Quoting a list
'(+ 1 2)
; => (+ 1 2)

;; Syntax-quoting a list
`(+ 1 2)
; => (clojure.core/+ 1 2)
```

We'll dive into the implications of this later. For now, just don't be
surprised when you see fully qualified symbols.

The other difference between quoting and syntax-quoting is that the
latter allows you to *unquote* forms with the tilde, `~`. Unquoting a
form evaluates it. Compare the following:

```clojure
;; The tilde (~) unquotes the form "(inc 1)"
`(+ 1 ~(inc 1))
; => (clojure.core/+ 1 2)

;; Without the unquote, syntax quote returns the unevaluated form with
;; fully qualified symbols
; => (clojure.core/+ 1 (clojure.core/inc 1))
```

If you're familiar with string interpolation, then you can think of
syntax-quote/unquote similarly. For example, in Ruby you can write:

```ruby
name = "Jebediah"

# You can create a new string through concatenation:
"Churn your butter, " + name + "!"

# Or through interpolation:
"Churn your butter, #{name}!"
```

In the same way that string interpolation leads to clearer and more
concise code, syntax-quoting and unquoting allow us to create lists
more clearly and concisely. Consider:

```clojure
;; Building a list with the list function
(list '+ 1 (inc 1))
; => (+ 1 2)

;; Building a list from a quoted list - super awkward
(concat '(+ 1) (list (inc 1)))

;; Building a list with unquoting
`(+ 1 ~(inc 1))
; => (clojure.core/+ 1 2)
```

As you can see, the syntax-quote version is the most concise. Also,
its visual form is closest to the final form of the list, making it
the easiest to understand.

## Applying Your Knowledge to a Macro

Now that we have a good handle on how syntax quoting works, let's take
a closer look at how it's employed in the `code-critic` macro. Here's
the macro again:

```clojure
(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  ;; Notice the backtick - that's the syntax quote
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))
```

To make things easier on ourselves, let's focus on a subset of the
macro. In order to spare our tender feelings, we'll look at the part
of the macro that praises our code:

```clojure

```

## Refactoring a Macro
