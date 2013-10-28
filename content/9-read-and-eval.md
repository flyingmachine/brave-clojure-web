--- 
title: "Clojure Alchemy: Reading, Evaluation, and Macros"
link_title: "Clojure Alchemy: Reading, Evaluation and Macros"
kind: documentation
---

# Clojure Alchemy: Reading, Evaluation, and Macros

By this point, you probably have a magical understanding of Clojure.
In the same way that alchemists thought of each chemical substance as
a force of nature with no rational relationship to other chemicals,
your alchemical elements are "form", "special form", "evaluates", and
the like.

This chapter will serve as your periodic table. By the end, you'll be
able to clearly see the relationships among Clojure's component parts.
We'll get you to this understanding by explaining Clojure's
fundamental mechanisms: reading and evaluation.

Once you understand these fundamentals, you'll be ready to go full
circle and obtain Clojure enlightenment, transcending code/data
duality. With your own Clojure philosopher's stone in hand, you'll be
able to work with one of the most powerful tools available to
programmers: the macro. And thus you will become the most feared and
respected programmers aliiiiiiiive!

## The Philosopher's Stone

The philosopher's stone &mdash; the supreme obsession of alchemsists
&mdash; was not just a wonder tool for transmuting base metals into
gold; it was also a metaphor for transcending duality and reaching
enlightenment.

The key to Clojure enlightnment is that *Clojure evaluates data
structures*. You can try this right now in a REPL:

```clojure
(def addition-list (list + 1 2))
(eval addition-list)
; => 3
```

That's right, baby! Clojure just evaluated a list. `(list + 1 2)`
returned a list, which was then passed to eval. When Clojure evaluated
the list, it called the `+` function with `1` and `2` as arguments,
returning `3`. And guess what: all the Clojure code you write that
actually does anything consists of representations of lists!

```clojure
(+ 1 2) ; <= that's a representation of a list
(map first '([0 1] [3 4])) ; <= that's a representation of a list, too!
```

Clojure code consists of representations of lists. These
representations are parsed by the *reader*, which produces the data
structure that's then *evaluated*. This makes Clojure *homoiconic*:
Clojure programs are represented by Clojure data structures. Overall,
the read/eval process looks something like this:

![read-eval](/images/read-eval/read-eval.png)

To fully understand how what's going on here, we'll explore the
reader and how it parses text to produce data structures. Then we'll
learn about the rules employed when evaluating data structures.
Finally, we'll tie everything together with an introduction to macros.

## The Reader

We now know that Clojure evaluates data structures. Where do these
data structures come from, though? For example, when you type the
following in the REPL

```clojure
(inc 1)
```

you have not, in fact, written a data structure. You've written a
sequence of unicode characters which *represents* a data structure.

The reader is Clojure's bridge between the *textual representation* of
a data structure and the data structure itself. In Clojure parlance,
we call these representations **reader forms**.

For example, Remember that "REPL" stands for "read-eval-print-loop".
The REPL is actually a process which presents you with a prompt. You
type characters into the prompt and hit enter. Then, Clojure *reads*
the stream of characters, producing a data structure which it then
evaluates:

```clojure
;; The REPL prompts you for text
user>

;; You enter reader forms, or textual representations of data
;; structures. In this case, you're entering a form which consists of
;; a list data structure that contains three more forms: the "str"
;; symbol and two strings.
user> (str "Hit me baby " "one more time")

;; After you hit enter, Clojure reads the forms and internally
;; produces the corresponding data structures. It then evaluates the
;; data structures. The textual representation of the result is then
;; printed and you get:
"Hit me baby one more time"
```

The key takeaways here are:

1. Clojure code consists of textual representations of data structures
   called reader forms. You'll notice that all your code that actually
   does stuff &mdash; function calls, if's, def's, etc &mdash;
   consists of *list* reader forms.
2. The reader transforms these reader forms into the actual internal
   data structures.

You can get a feel for the reader more directly by using `read-string`
in the REPL:

```clojure
;; The result of reading this reader form is a list with 3 members
(read-string "(+ 8 3)")
; => (+ 8 3)
```

### Reader Macros

So far, we've seen a one-to-one mapping between reader forms and their
corresponding data structures:

```clojure
() ; <= a list reader form
str ; <= a symbol reader form
[1 2] ; <= a vector reader form containing two number reader forms
{:sound "hoot"} ; <= a map reader form with a keyword reader form and
                ; string reader form
```

However, the reader can employ more complex behavior when converting
text to data structures. For example, remember anonymous functions?

```clojure
(#(+ 1 %) 3)
; => 4
```

Well, try this out:

```clojure
(read-string "#(+ 1 %)")
; => (fn* [p1__423#] (+ 1 p1__423#))
```

Whoa! What just happened? This is not the one-to-one mapping that
we're used to. Reading `#(+ 1 %)` somehow resulted in a list
consisting of the `fn*` symbol, a vector containing a symbol, and a
list containing three elements.

To answer my own question: the reader used a **reader macro** to
transform  `#(+ 1 %)`.

Reader macros are not to be confused with macros, which you'll read
about later in this chapter. Rather, reader macros are sets of rules
for transforming text into data structures. Reader macros are
designated by **macro characters**.

Reader macros often allow us to represent data structures in more
compact ways. For example:

```clojure
;; The quote reader macro is designated by the single quote, '
(read-string "'(a b c)")
; => (quote (a b c))

;; The deref reader macro is designated by @
(read-string "@var")
; => (clojure.core/deref var)
```

Reader macros can also do crazy stuff like cause text to be ignored:

```clojure
;; The humble semicolon designates the single-line comment reader macro
(read-string "; ignore!\n(+ 1 2)")
; => (+ 1 2)
```

So that's the reader! Your humble companion, toiling away at
transforming text into data structures.

## Evaluation

We already know that Clojure evaluates data structures:

```clojure
(def addition-list (list + 1 2))
(eval addition-list)
; => 3
```

In this section, we'll go over the rules which govern evaluation. Once
you understand these rules, you'll finally be ready for macros! Yay!

### These Things Evaluate to Themselves

Strings, numbers, characters, `true`, `false`, `nil` and keywords evaluate
to themselves:

```clojure
;; A string evaluates to itself
(eval (read-string "\"t\""))
; => "t"
```

You might notice that the following accomplishes the same thing
without `read-string`:

```clojure
(eval "t")
```

This is a consequence of using the REPL. Once the data structure
represented by `(eval "t")` gets evaluated, `"t"` itself has already
gone through the read/eval process, yielding the string represented by
`"t"`. For the time being, we're going to keep using `read-string`,
however, to more clearly show that `eval` works on data structures.

```clojure
(eval (read-string "true"))
; => true

(eval (read-string "false"))
; => false

(eval (read-string ":huzzah"))
; => :huzzah
```

So, whenever Clojure evaluates these data structures, the result is
the data structure itself.

### Symbols

When we introduced symbols in the last chapter, we said it was OK to
think "big whoop!" about them. Now it should be clearer why symbols
are interesting: they're data structures, just the same as vectors,
lists, strings, etc. Clojure wouldn't be able to evaluate symbols if
they weren't data structures.

Clojure evaluates symbols by **resolving** them. We haven't gone over
namespaces or packages, so we'll ignore those resolution rules for now
&mdash; they're not at all complicated and you'll learn them easily
when the time is right, young padawan.

For now, though, it's enough to say that a symbol resolves to either a
*special form* or a *value*.

We'll discuss special forms in the next section. Let's look at some
examples of symbols resolving to values:

```clojure
;; The symbol x is *bound* to 5. When the evaluator resolves x, it
;; resolves it to the value 5
(let [x 5]
  (+ x 3))
; => 8

;; x is *mapped* to 15. Clojure resolves the symbol x to the value 15
(def x 15)
(+ x 3)
; => 18

;; x is *mapped* 15, but we introduce a *local binding* of x to 5.
;; x is resolved 5
(def x 15)
(let [x 5]
  (+ x 3))
; => 8

;; The "closest" binding takes precedence
(let [x 5]
  (let [x 6]
    (+ x 3)))
; => 9

;; exclaim is *mapped* to a function. Within the function body,
;; exclamation is *bound* to the argument passed to the function
(defn exclaim
  [exclamation]
  (str exclamation "!"))
(exclaim "Hadoken")
; => "Hadoken!"
```

So in general, Clojure resolves a symbol by:

1. Looking up whether the symbol names a special form. If it doesn't...
2. Trying to find a local binding. If it doesn't...
3. Trying to find a mapping introduced by `def`. If it doesn't...
4. Throwing an exception

### Lists

If the data structure is an empty list, it evaluates to an empty list:

```clojure
(eval (read-string "()"))
; => ()
```

Otherwise, it is a *call* to the first element of the list.

#### Function Calls

We're familiar with function calls:

```clojure
;; The + symbol resolves to a function. The function is *called* with
;; the arguments 1 and 2
(+ 1 2)
; => 3
```

When performing a function call, each operand is fully evaluated
and then passed to the function as an argument.

#### Special Forms

You can also call *special forms*. For example:

```clojure
(eval (read-string "(if true 1 2)"))
; => 1
```

In the above example, we evaluated a data structure which consisted of
the following:

1. The `if` symbol
2. The value `true`
3. The value `1`
4. The value `2`

`if` got resolved to the `if special form`. For the sake of brevity,
we're going to say "the `if` special form" or even just `if` instead
of "the special form whose symbol is `if`".

In resolving the list data structure, we *called* `if` with the
*operands* `true`, `1`, and `2`.

In general, special forms are special because they implement core
behavior that can't be implemented with functions. For example, when
you call a function, each operand gets evaluated. With `if`, however,
you don't want each operand to be evaluated.

Another important special form is `quote`. When we introduced lists in
the last chapter, we represented them like this:

```clojure
'(a b c)
```

As we saw in the Reader section, this invoke a reader macro so that we
end up with:

```
(quote (a b c))
; => (a b c)
```

Normally, Clojure would try to resolve the `a` symbol and then *call*
it because it's the first element of a list. The `quote` special form
tells the evaluator "instead of evaluating my next data structure like
normal, just return the data structure itself."

`def`, `let`, `loop`, `fn`, and `recur` are all special forms as well.
You can see why - they don't get evaluated in the same way as
functions.

So, when Clojure evaluates a list data structure, it calls a function
or a special form. It can also call macros, which we're now ready to
learn about!

## Macros

Macros actually behave very similarly to functions. They take
arguments and return a value, just like a function would.

What makes them interesting and powerful is the way they fit in to the
evaluation process. Let's look at an example:

```clojure
(defmacro ignore-last-operand
  [function-call]
  (let [c (count function-call)]
    (take (dec c) function-call)))

(ignore-last-operand (+ 1 2 10))
; => 3

;; This will not print anything
(ignore-last-operand (+ 1 2 (println "look at me!!!")))
; => 3
```

Clearly, this isn't a function call! There is no way possible for a
function to "reach into" one of its operands and alter it. The
difference is all in the way functions and macros are evaluated:

1. When you call a function, each of its operands is evaluated before
   being passed to the function as an argument. By contrast, when you
   call a macro, the operands are *not* evaluated. In particular,
   symbols are not resolved &mdash; they are passed as symbols. Lists
   are not evaluated by calling a function, special form, or macro
   &mdash; the unevaluated list data structure itself is passed in.

   In the above example, the macro `ignore-last-operand` receives the
   list `(+ 1 2 10)` as its argument, *not* the value `13`.

2. The data structure returned by a function is *not* evaluated, but
   the data structure returned by a macro *is*. In the above example,
   `ignore-last-operand` returned the list `(+ 1 2)` both times, and
   both times that list was then evaluated, resulting in the `+`
   function being called.

Macros allow you to use the full power of Clojure to transform the
data structures used to represent your program into completely
different data structures which then get evaluated. They thus enable
syntax abstraction.

"Syntax abstraction" sounds a little too abstract (ha ha!), so let's
explore that a little.

### A Syntax Abstraction Example: The -> Macro

Often, our Clojure code consists of a bunch of nested function calls.
For example, I use the following function in one of my projects:

```clojure
(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (io/resource path))))
```

In order to understand the function body, you have to find the
innermost form, in this case `(io/resource path)`, and then work your
way outward from right to left to see how the result of each function
gets passed to another function. This right-to-left flow is opposite
to what Western programmers are used to.

The `->` macro lets you rewrite the function like this:

```clojure
(defn read-resource
  [path]
  (-> path
      io/resource
      slurp
      read-string))
```

You can read this as "`path` gets passed to `io/resource`. The result
gets passed to `slurp`. The result of that gets passed to
`read-string`.

So these two functions are entirely equivalent. However, the second
one can be easier understand because we can approach it from top to
bottom, a direction we're used to. The `->` also has the benefit that
we can leave out parentheses, which means there's less visual noise
for our poor, strained eyes to contend with.

This is a *syntactical abstraction* because it lets us write code in a
syntax that's different from Clojure's built-in syntax, but which is
preferable for human consumption.

Here's another syntax abstraction:

```clojure
(defmacro backwards
  [form]
  (reverse form))
(backwards (" cowboys" "mamas don't let your babies grow up to be" str))
; => "mamas don't let your babies grow up to be cowboys"
```

As you can see, this lets us write expressions backwards. It's just a
toy example, of course, but you get the idea: macros give us complete
freedom to express programs however we want to.

In a later chapter we'll fully explore how to write macros. Fun!
