---
title: "Clojure Alchemy: Reading, Evaluation, and Macros"
link_title: "Clojure Alchemy: Reading, Evaluation and Macros"
kind: documentation
---

# Clojure Alchemy: Reading, Evaluation, and Macros

This chapter will give you a systematic understanding of Clojure by
explaining how it evaluates code. This is similar to the way that the
periodic table of elements helps young chemistry students everywhere
understand the properties of atoms and why they react with each other
as they do: elements in the same column behave similarly because they
have the same nuclear charge. Without the periodic table and its
underlying theory, high schoolers would be in the same position as the
alchemists of yore, mixing stuff together randomly to see what blows
up. With a deeper understanding of the elements, today's youth can
know *why* stuff blows up. Right now, your alchemical elements are
"form", "special form", "evaluates", and the like. This chapter will
serve as your periodic table, and with it you'll be able to blow stuff
up on purpose.

Understanding Clojure's evaluation model will allow you to understand
how macros work. Macros are one of the most powerful tools available
to programmers, and many programmers decide to learn a Lisp so they
can finally see what all the hoopla is about. I think that the
programmer, author, and open source advocate Eric S. Raymond had
macros in mind when he
[wrote](http://www.catb.org/esr/faqs/hacker-howto.html#skills1), "LISP
is worth learning for a different reason — the profound enlightenment
experience you will have when you finally get it. That experience will
make you a better programmer for the rest of your days, even if you
never actually use LISP itself a lot."

## The Philosopher's Stone

The philosopher's stone, along with the elixir of life and viagra, is
one of the most well-known examples of alchemical lore, pursued for
its ability to transmute lead into gold. Less known, however, is that
alchemists also believed it would allow them to erase the apparent
separation between themselves and God, transcending duality and
reaching enlightenment. I don't think that's what Eric S. Raymond was
talking about in the quote above, but when it comes to Clojure (and
Lisps in general), reaching enlightenment also involves transcending
duality. The key to Clojure enlightenment is that *Clojure evaluates
data structures*. You can see this for yourself right now in a REPL:

```clojure
(def addition-list (list + 1 2))
(eval addition-list)
; => 3
```

That's right, baby! Clojure just evaluated a list. `(list + 1 2)`
returned a list, which was then passed to `eval`. When Clojure
evaluated the list, it looked up the function corresponding to the `+`
symbol, then called that function with `1` and `2` as arguments,
returning `3`. And guess what: all the Clojure code you write that
actually does anything consists of representations of lists!

```clojure
(+ 1 2) ; <= that's a representation of a list
(map first '([0 1] [3 4])) ; <= that's a representation of a list, too!
```

These representations are parsed by the **reader**, which produces
data structures. The data structures are then **evaluated**. This
makes Clojure **homoiconic**: you write Clojure programs using
representations of Clojure data structures. You can visualize the
evaluation process like this:

![read-eval](/images/read-eval/simple-read-eval.png)

To fully show how what's going on here, I'll explain the reader
and how it parses text to produce data structures. Then I'll reveal
the rules employed when evaluating data structures. Finally, we'll tie
everything together with an introduction to macros.

## The Reader

You now know that Clojure evaluates data structures. Where do these
data structures come from, though? For example, when you type the
following in the REPL

```clojure
(inc 1)
```

you have not, in fact, written a data structure. You've written a
sequence of unicode characters which *represents* a data structure.
The **reader** is Clojure's bridge between the *textual
representation* of a data structure and the data structure itself. In
Clojure parlance, we call these representations **reader forms**. For
example, remember that "REPL" stands for "read-eval-print-loop". The
REPL is actually a running program which presents you with a prompt.
You type characters into the prompt and hit enter. Then, Clojure
*reads* the stream of characters, producing a data structure which it
evaluates.

First, the REPL prompts you for text:

```clojure
user>
```

You enter reader forms, or textual representations of data structures.
In the example below, you're entering a form which consists of a list
data structure that contains three more forms: the `str` symbol and
two strings.

```
user> (str "To understand what recursion is," " you must first understand recursion")
```

After you hit enter, Clojure reads the forms and internally produces
the corresponding data structures. It then evaluates the data
structures. The textual representation of the result is then printed
and you get:

```
"To understand what recursion is, you must first understand recursion"
```

The key takeaways here are:

1.  Clojure code consists of textual representations of data structures
    called reader forms. You'll notice that all your code that actually
    does stuff &mdash; function calls, if's, def's, etc &mdash;
    consists of *list* reader forms.
2.  The reader transforms these reader forms into the actual internal
    data structures.

You can get a feel for the reader more directly by using `read-string`
in the REPL. `read-string` will show you the data structures that your
text gets converted to before they're evaluated. For example, the
result of reading this reader form is a list with 3 members:

```clojure
(read-string "(+ 8 3)")
; => (+ 8 3)
```

## Reader Macros

So far, you've seen a one-to-one mapping between reader forms and their
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
transform `#(+ 1 %)`. Reader macros are not to be confused with
macros, which you'll read about later in this chapter. Rather, reader
macros are sets of rules for transforming text into data structures.
Reader macros are designated by **macro characters**. Reader macros
often allow us to represent data structures in more compact ways. The
example below shows the quote reader macro, which expands to the quote
special form:

```clojure
(read-string "'(a b c)")
; => (quote (a b c))
```

When the reader encounters the single quote, `'`, it expands it to a
list whose first member is the symbol `quote` and whose second member
is the data structure following the single quote. The deref reader
macro, `@` works similarly:

```clojure
(read-string "@var")
; => (clojure.core/deref var)
```

Reader macros can also do crazy stuff like cause text to be ignored.
The semicolon designates the single-line comment reader macro:

```clojure
(read-string "; ignore!\n(+ 1 2)")
; => (+ 1 2)
```

And that's the reader! Your humble companion, toiling away at
transforming text into data structures.

## Evaluation

You already know that Clojure evaluates data structures:

```clojure
(def addition-list (list + 1 2))
(eval addition-list)
; => 3
```

In this section, I'll go over the rules which govern evaluation. Once
you understand these rules, you'll finally be ready for macros! Huzzah!

### These Things Evaluate to Themselves

Strings, numbers, characters, `true`, `false`, `nil` and keywords evaluate
to themselves

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
`"t"`. For the time being, however, we're going to keep using
`read-string` to more clearly show that `eval` works on data
structures.

```clojure
(eval (read-string "true"))
; => true

(eval (read-string "false"))
; => false

(eval (read-string ":huzzah"))
; => :huzzah
```

Whenever Clojure evaluates these data structures, the result is the
data structure itself.

### Symbols

In the last chapter, you learned about how to associate symbols with
values using `def`. In Clojure, symbols are data structures; if they
weren't, Clojure wouldn't be able to evaluate them. Clojure evaluates
symbols by **resolving** them, which you learned about in the last
chapter as well. Ultimately, a symbol resolves to either a *special
form* or a *value*. I'll cover special forms in the next section.
Let's look at some examples of symbols resolving to values.

In this example, The symbol `x` is bound to 5. When the evaluator
resolves x, it resolves it to the value 5:

```clojure
(let [x 5]
  (+ x 3))
; => 8
```

Here, `x` is mapped to 15. Clojure resolves the *symbol* `x` to the
*value* 15:

```clojure
(def x 15)
(+ x 3)
; => 18
```

Now `x` is mapped to 15, but we introduce a *local binding* of `x`
to 5. `x` is resolved to 5:

```clojure
(def x 15)
(let [x 5]
  (+ x 3))
; => 8
```

The "closest" binding takes precedence:

```clojure
(let [x 5]
  (let [x 6]
    (+ x 3)))
; => 9
```

In this next example, `exclaim` is mapped to a function. Within the
function body, `exclamation` is bound to the argument passed to the
function

```clojure
(defn exclaim
  [exclamation]
  (str exclamation "!"))
(exclaim "Hadoken")
; => "Hadoken!"
```

Finally, in this last example, `map` and `inc` are both mapped to
functions:

```clojure
(map inc [1 2 3])
; => (2 3 4)
```

In general, Clojure resolves a symbol by:

1.  Looking up whether the symbol names a special form. If it doesn't&#x2026;
2.  Trying to find a local binding. If it doesn't&#x2026;
3.  Trying to find a mapping introduced by `def`. If it doesn't&#x2026;
4.  Throwing an exception

### Lists

If the data structure is an empty list, it evaluates to an empty list:

```clojure
(eval (read-string "()"))
; => ()
```

Otherwise, it is a *call* to the first element of the list:

### Function Calls

You're familiar with function calls. In this example, the + symbol
resolves to a function. The function is *called* with the arguments 1
and 2:

```clojure
(+ 1 2)
; => 3
```

When performing a function call, each operand is fully evaluated
and then passed to the function as an argument.

### Special Forms

You can also call *special forms*. For example:

```clojure
(eval (read-string "(if true 1 2)"))
; => 1
```

In the above example, Clojure evaluated a data structure which
consisted of the following:

1.  The `if` symbol
2.  The value `true`
3.  The value `1`
4.  The value `2`

`if` got resolved to the if special form. For the sake of brevity, I'm
going to say "the `if` special form" or even just `if` instead of "the
special form whose symbol is `if`".

In resolving the list data structure, we *called* `if` with the
*operands* `true`, `1`, and `2`.

In general, special forms are special because they implement core
behavior that can't be implemented with functions. For example, when
you call a function, each operand gets evaluated. With `if`, however,
you don't want each operand to be evaluated, whereas functions always
evaluate each argument.

Another important special form is `quote`. You've seen lists
represented like this:

```clojure
'(a b c)
```

As we saw in the Reader section, this invokes a reader macro so that we
end up with:

```clojure
(quote (a b c))
```

Normally, Clojure would try to resolve the `a` symbol and then *call*
it because it's the first element of a list. The `quote` special form
tells the evaluator "instead of evaluating my next data structure like
normal, just return the data structure itself." In this case, you end
up with a list consisting of the symbols `a`, `b`, and `c`, rather
than the result of the value of `a` applied to the values of `b` and
`c`. `def`, `let`, `loop`, `fn`, `do`, and `recur` are all special
forms as well. You can see why - they don't get evaluated in the same
way as functions.

When Clojure evaluates a list data structure, it calls a function or a
special form. It can also call *macros*.

## Macros

Hmm... Clojure programs are comprised of data structures... the same
data structures that Clojure is capable of manipulating... wouldn't it
be awesome if you could use Clojure to manipulate the data structures
that comprise a Clojure program? Yes, yes it would! And guess what:
you can do this with macros! Did your head just explode? Mine did!

To get an idea of what macros do, let's look at some code. This
example is *not* a macro. Rather, it merely shows that you can write
code using infix notation and then use Clojure to transform it so that
it will actually execute. First, create a list which represents infix
addition:

```clojure
(read-string "(1 + 1)")
; => (1 + 1)
```

Clojure will throw an exception if you try to make it evaluate this
list:

```clojure
(eval (read-string "(1 + 1)"))
; => ClassCastException java.lang.Long cannot be cast to clojure.lang.IFn
```

`read-string` returns a list, however, and you can just use Clojure to
create a reorganized list that it *can* successfully evaluate:

```clojure
(let [infix (read-string "(1 + 1)")]
  (list (second infix) (first infix) (last infix)))
; => (+ 1 1)
```

If you `eval` this, then it returns 2, just like you'd expect:

```clojure
(eval
 (let [infix (read-string "(1 + 1)")]
   (list (second infix) (first infix) (last infix))))
; => 2
```

This is cool, but it's also inconvenient. That's where macros come in.
Macros allow you to manipulate lists before Clojure evaluates them,
except more conveniently. They work very similarly to functions. They
take arguments and return a value, just like a function would. Macro
bodies behave exactly like function bodies, and you have your full
program at your disposal within them. What makes them interesting and
powerful is the way they fit in to the evaluation process. Let's look
at an example:

```clojure
(defmacro ignore-last-operand
  [function-call]
  (butlast function-call))

(ignore-last-operand (+ 1 2 10))
; => 3

;; This will not print anything
(ignore-last-operand (+ 1 2 (println "look at me!!!")))
; => 3
```

Clearly, this isn't a function call. There is no way possible for a
function to "reach into" one of its operands and alter it. The
difference is all in the way functions and macros are evaluated.

First, when you call a function, each of its operands is evaluated
before being passed to the function as an argument. By contrast, when
you call a macro, the operands are *not* evaluated. In particular,
symbols are not resolved &mdash; they are passed as symbols. Lists are
not evaluated by calling a function, special form, or macro &mdash;
the unevaluated list data structure itself is passed in. In the above
example, the macro `ignore-last-operand` receives the list `(+ 1 2
10)` as its argument, *not* the value `13`.

Second, the data structure returned by a function is *not* evaluated,
but the data structure returned by a macro *is*. The process of
determining the return value of a macro is called **macro expansion**,
and you can use the function `macroexpand` to see what data structure
a macro returns before that data structure is evaluated. Note that you
have to quote the form that you pass to `macroexpand`:

```clojure
(macroexpand '(ignore-last-operand (+ 1 2 10)))
; => (+ 1 2)

(macroexpand '(ignore-last-operand (+ 1 2 (println "look at me!!!"))))
; => (+ 1 2)
```

As you can see, both expansions result in the list `(+ 1 2)`. When
this list is evaluated, as in the previous example, the result is `3`.

The best way to think about this whole process is to picture a phase
between reading and evaluation: the *macro expansion* phase. Below is
an example of macro expansion with a new form, and below that is how
you can visualize it:

```clojure
(when :in-doubt "something")
; => "something"

(macroexpand '(when :in-doubt "something"))
; => (if :in-doubt (do "something"))

(eval (macroexpand '(when :in-doubt "something")))
; => "something"
```

![read-eval](/images/read-eval/read-eval.png)

And that's how macros fit into the evaluation process. But why would
you want to do this? The reason is that macros allow you to transform
an arbitrary data structure like `(1 + 1)` into one that can
be evaluated by Clojure, `(+ 1 1)`. Thus, *you can use Clojure to
extend itself* so that you write your program however you please.
Macros thus enable syntax abstraction. "Syntax abstraction" sounds a
little too abstract (ha ha!), so let's explore that a little.

## A Syntax Abstraction Example: The -> Macro

Often, our Clojure code consists of a bunch of nested function calls.
For example, I use the following function in one of my projects:

```clojure
(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (clojure.java.io/resource path))))
```

In order to understand the function body, you have to find the
innermost form, in this case `(clojure.java.io/resource path)`, and
then work your way outward from right to left to see how the result of
each function gets passed to another function. This right-to-left flow
is opposite to what Western programmers are used to. As you get used
to writing in Clojure, this kind of code gets easier and easier to
understand. Thankfully, though, we have the `->` macro, also known as
the "threading" macro and the "stabby" macro. It lets you rewrite the
above function like this:

```clojure
(defn read-resource
  [path]
  (-> path
      clojure.java.io/resource
      slurp
      read-string))
```

You can read this as `path` gets passed to `io/resource`. The result
gets passed to `slurp`. The result of that gets passed to
`read-string`.

These two ways of defining `read-resource` are entirely equivalent.
However, the second one can be easier understand because we can
approach it from top to bottom, a direction we're used to. The `->`
also has the benefit that we can leave out parentheses, which means
there's less visual noise to contend with. This is a *syntactical
abstraction* because it lets you write code in a syntax that's
different from Clojure's built-in syntax, but which is preferable for
human consumption.

Here's another syntax abstraction that lets you write code backwards:

```clojure
(defmacro backwards
  [form]
  (reverse form))
(backwards (" cowboys" "mamas don't let your babies grow up to be" str))
; => "mamas don't let your babies grow up to be cowboys"
```

It's just a toy example, but you get the idea: macros give you
complete freedom to express programs however we want to. In the next
chapter, you'll learn how to write macros. Onward!
