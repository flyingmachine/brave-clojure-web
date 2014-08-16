--- 
title: "Writing Macros"
link_title: "Writing Macros"
kind: documentation
---

# Writing Macros

When I was 18 I got a job as a night auditor at a hotel in Santa Fe,
New Mexico. Four nights a week from 11pm till 7am I would dutifully
check people in and out, fold laundry, and play my Playstation 2 in
the back room. All in all it was a decent job, providing 8 hours of
pay for 2 hours of work.

After a few months of this sleepless schedule, though, I had become a
different person. My emotions in particular had taken on a life of
their own. Like one night, around 3am, when I was watching an
infomercial for a product claiming to restore men's hair. As I watched
the story of a formerly-bald individual, I became overwhelmed with
joy. "At last!", my brain said to itself with complete sincerity,
"This man has gotten the love and success he deserves! What an
incredible product this is, giving hope to the hopeless!"

Throughout the intervening years I've found myself wondering if I
could somehow recreate the emotional abandon and appreciation for life
induced by chronic sleep deprivation. The ultimate solution would be
some kind of potion &mdash; a couple quaffs to unleash my inner
Richard Simmons, but not for too long.

Just as a potion would allow me to temporarily alter my fundamental
nature, macros allow you to modify Clojure in ways that just aren't
possible with other languages. With macros, you can extend Clojure to
suit your problem space, building up the language itself.

Which is exactly what you'll do in this chapter. You'll thoroughly
examine how to write macros starting with basic examples and moving up
in complexity. You'll close by donning your make-believe cap,
pretending that you run an online potion store and using macros to
validate customer orders.

By the end of the chapter, you'll understand:

* What macros are
* The tools used to write macros
    * quote
    * syntax quote
    * unquote
    * unquote-splicing / the pinata tool
    * gensym
    * autogensym
    * macroexpand
* Things to watch out for
    * double evaluation
    * variable capture
    * macros all the way down

## What Macros Are

The last chapter covered how
[Clojure evaluates data structures](/read-and-eval/#3__Evaluation).
Briefly:

* Strings, numbers, characters, `true`, `false`, `nil` and keywords evaluate
  to themselves
* Clojure resolves symbols by:
    1. Looking up whether the symbol names a special form. If it doesn't...
    2. Trying to find a local binding. If it doesn't...
    3. Trying to find a mapping introduced by `def`. If it doesn't...
    4. Throwing an exception
* Lists result in calls:
    * When performing a function call, each operand is fully evaluated
      and then passed to the function as an argument.
    * Special form calls, like `if` and `quote`, follow "special"
      evaluation rules which implement core Clojure behavior
    * Macros take unevaluated data structures as arguments and return
      a data structure which is then evaluated using the rules above

So, a macro is a tool for transforming an arbitrary data structure
into one which can be evaluated by Clojure. This allows you to
introduce new syntax so that you can write code which is more concise
and meaningful. For example, here are two versions of a function, one
that doesn't use the `->` threading macro and one that does:

```clojure
;; vanilla Clojure
(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (io/resource path))))

;; using the threading macro
(defn read-resource
  [path]
  (-> path io/resource slurp read-string))
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

You might think that `when` is a special form like `if`. Well guess
what: it's not! In most other languages, you can only create
conditional expressions using special keywords, and there's no way to
create your own conditional operators. However, `when` is actually a
macro. In this macro expansion, you can see that `when` is implemented
in terms of `if` and `do`:

```clojure
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
body will almost always return a list. This makes sense &mdash;
remember that Clojure uses lists to represent function calls, special
form calls, and macro calls, and macros are a way of transforming data
structures from a represenation that Clojure can't evaluate into one
that it can.

Here's a simple example of a macro definition:

```clojure
(defmacro postfix-notation
  "I'm too indie for prefix notation"
  [expression]
  (conj (butlast expression) (last expression)))
```

Notice that you have *full access to Clojure* within the macro's body.
You can use any function, macro, or special form within the macro
body. Let that simmer a bit: you have the full power of Clojure at
your disposal to extend Clojure. Can you feel your power as a
programmer growing? Can you? Can you? <small>Can you?</small>

You call macros just like you would a function or special form:

```clojure
(postfix-notation (1 1 +))
; => 2
```

One key difference between functions and macros is that function
arguments are fully evaluated before they're passed to the function,
whereas macros receive arguments as unevaluated data structures.

You can see this in the above example. If you tried evaluating `(1 1
+)`, you would get an exception. However, since you're making a macro
call, the unevaluated list `(1 1 +)` is passed to `postfix-notation`.
You can thus use `conj`, `butlast`, and `last` functions to rearrange
the list so that it's something Clojure can evaluate:

```clojure
(macroexpand '(postfix-notation (1 1 +)))
; => (+ 1 1)
```

Continuing with our anatomical adventures, you can use argument
destructuring in macro definitions argument destructuring, just like
you can with functions:

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

Here, `code-critic` takes a map as an argument and destructures the
`:good` and `:bad` keys so that the corresponding values are named by
`good` and `bad` within the macro.

You can also create multiple-arity macros, though I've never seen one
and most likely you shouldn't do it. Notice that each arity's argument
list and body is wrapped in parens:

```clojure
(defmacro mutiple-arity
  ([single-arg]
     "Don't do this")
     
  ([arg1 arg2]
     "Seriously, don't do it :(")
     
  ([arg1 arg2 arg 3]
     "Nah, just kidding. Do whatever you want! Self-actualize!"))
```

Now that you're comfortable with the anatomy of macros and are well on
your way to self-actualization, it's time to strap yourself to your
thinking masts Odysseus-style and look at how to write macro bodies.

## Building Lists for Evaluation

Macro-writing is all about building a list to be evaluated by Clojure
and it requires a kind of inversion to your normal way of
thinking. In particular, you'll need to be extra careful about the
difference between a *symbol* and its *value*.

### Be Careful about Distinguishing Symbols and Values

Let's take the `postfix-notation` example:

```clojure
(defmacro postfix-notation
  [expression]
  (conj (butlast expression) (last expression)))

(macroexpand '(postfix-notation (1 1 +)))
; => (+ 1 1)

(postfix-notation (1 1 +))
; => 2
```

When you pass the argument `(1 1 +)` to the `postfix-notation` macro,
the value of `expression` within the macro body is a list comprised of
three elements: the number one, the number one, and the symbol `+`.
Note that the *symbol* `+` is distinct from its *value*, which is the
addition function.

The `postfix-notation` macro returns a new list comprised of the `+`
symbol, `1`, and `1`. This list is then evaluated and the result, 2,
is returned.

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

;; Evaluating the plus symbol yields the plus function
+
; => #<core$_PLUS_ clojure.core$_PLUS_@47b36583>

;; Quoting the plus symbol yields the plus symbol
(quote +)
; => +

;; Evaluating an unbound symbol raises an exception
sweating-to-the-80s
; => Unable to resolve symbol: sweating-to-the-80s in this context

;; quoting returns a symbol regardless of whether the symbol
;; has a value associated with it
(quote sweating-to-the-80s)
; => sweating-to-the-80s

;; The single quote character is a shorthand for (quote x)
;; This example works just like (quote (+ 1 2))
'(+ 1 2)
; => (+ 1 2)

'dr-jekyll-and-richard-simmons
; => dr-jekyll-and-richard-simmons
```

You can see quoting at work in the `when` macro. This is `when`'s actual source:

```clojure
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

Notice that the macro definition quotes both `if` and `do`. That's
because you want these symbols to be in the final list that `when`
returns for evaluation.

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

Again, you have to quote `if` because you want the unevaluated symbol
to be placed in the resulting list.

There are many cases where you'll use simple quoting like this when
writing macros, but most often you'll use the more powerful syntax
quote.

### Syntax Quoting

So far, you've built up our lists by using `'` (quote) and functions
which operate on lists (`conj`, `butlast`, `first`, etc), and by using
the `list` function to create a list. Indeed, you could write your
macros that way until the cows come home. Sometimes, though, it leads
to tedious and verbose code. Take the `code-critic` example:

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

I feel like I'm falling asleep from the tedium. Somebody slap me awake
already! Luckily, Clojure has a handy mechanism for solving this
problem: the syntax quote! Here's how you would rewrite `code-critic`
using syntax quote:

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
its namespace. Below is a comparison between quoting and syntax
quoting:

```clojure
;; Quoting does not include a namespace unless your code includes a namespace
'+
; => +

;; Write out the namespace and it'll be returned by normal quote
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

The syntax quote does this in order to help you avoid name collisions,
a topic covered in Chapter 7: Organizing your project.

The other difference between quoting and syntax-quoting is that the
latter allows you to *unquote* forms with the tilde, `~`. Unquoting a
form evaluates it. It's kind of like kryptonite in that way: whenever
Superman is around kryptonite, his powers disappear. Whenever a tilde
appears within a syntax-quoted form, it's like you're suppressing the
syntax quote's power to return unevaluated, fully-namespaced
forms. Here's an example:

```clojure
`(+ 1 ~(inc 1))
; => (clojure.core/+ 1 2)
```

Without the unquote, syntax quote returns the unevaluated form with
fully qualified symbols:

```clojure
`(+ 1 (inc 1))
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
more clearly and concisely. Check out these three examples of building
a list:

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

Now that you have a good handle on how syntax quoting works, let's
take a closer look at how it's employed in the `code-critic`
macro. Here's the macro again:

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
of the macro that praises our code. Here's how a code-praiser would
look without syntax-quote and with it:

```clojure
;; Without syntax-quote
(defmacro code-praiser
  [code]
  (list 'println
        "Sweet gorilla of Manila, this is good code:"
        (list 'quote code)))

;; With syntax-quote
(defmacro code-praiser
  [code]
  `(println
    "Sweet gorilla of Manila, this is good code:"
    (quote ~code)))
```

These have identical macro expansions:

```clojure
(macroexpand '(code-praiser (+ 1 1)))
; =>
(println "Sweet gorilla of Manila, this is good code:" (quote (+ 1 1)))
```

Here are the differences:

1. Without syntax-quote, you need to use the `list` function. Remember
   that you want to return a list which will then be evaluated,
   resulting in the function `println` being applied.

   The `list` function isn't necessary when you use syntax-quote,
   however, because a syntax-quoted list evaluates to a list &mdash;
   not to a function call, special form call, or macro call.
2. Without syntax-quote, you need to quote the symbol `println`. This
   is because you want the resulting list to include the symbol
   `println`, not the function which `println` evaluates to.

   By comparison, Clojure doesn't evaluate symbols within a
   syntax-quoted list; it returns a fully-qualified symbol. You don't
   need to precede `println` with a single quote.
3. The string is treated the same in both versions.
4. Without syntax quote, you need to build up another list with the
   `list` function and the `quote` symbol quoted. This might make your
   head hurt. Look at the macro expansion &mdash; you want to call
   `quote` on the data structure which was passed to the macro.

   With syntax quote, you can continue to build a list more concisely.
5. Finally, in the syntax-quoted version you have to unquote `code` so
   that it will be evaluated. Otherwise, the symbol `code` would be
   included in the macro expansion instead of its value, `(+ 1 1)`:

To further illustrate, here's what happens if you don't unquote `code`
in the macro definition:

```clojure
(defmacro code-praiser
  [code]
  `(println
    "Sweet gorilla of Manila, this is good code:"
    (quote code)))

(macroexpand '(code-praiser (+ 1 1)))
; =>
(clojure.core/println
  "Sweet gorilla of Manila, this is good code:"
  (quote user/code))
```

Sweet gorilla of Manila, you've come a long way! With this smaller
portion of the `code-critic` macro thoroughly dissected, the full
macro should make more sense:

```clojure
(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))
```

The principles are exactly the same. You're using syntax-quote because
it lets you write things out more concisely and you're unquoting the
bits that we want evaluated. There are two differences, however.

First, you're now dealing with two variables within the syntax-quoted
list: `good` and `bad`. You introduce these variables introduced by
destructuring the argument passed to `code-critic`, a map containing
`:good` and `:bad` keys. This isn't macro-specific; as mentioned
above, functions and `let` bindings both allow destructuring.

Second, you have to wrap our two `println` expressions in a `do`
expression. Why are you *do*ing that? (Ha ha!) Consider the following:

```clojure
(defmacro code-makeover
  [code]
  `(println "Before: " (quote ~code))
  `(println "After: " (quote ~(reverse code))))

(code-makeover (1 2 +))
; => After:  (+ 2 1)
```

Why wasn't the "before" version printed? The macro only returned its
last expression, in this case

```clojure
`(println "After: " (quote ~(reverse code)))
```

`do` lets you wrap up multiple expressions into one expression in
situations like this.

And thus concludes our introduction to the mechanics of writing a
macro! Sweet sacred boa of Western and Eastern Samoa, that was a lot!

To sum up: Macros receive unevaluated, arbitrary data structures as
arguments and return data structures that Clojure evaluates. When
defining your macro, you can use argument destructuring just like you
can with functions and `let` bindings.

Most of time, your macros are going to return lists. You can build up
the list to be returned by using list functions or by using
syntax-quote. Syntax quoting usually leads to code that's clearer and
more concise, and can unquote forms when using syntax quoting. Whether
you use syntax quoting or plain quoting, it's important to be clear on
the distinction between a symbol and the value it evaluates to when
building up your list. And if you want your macro to return multiple
forms for Clojure to evaluate, make sure to wrap them in a `do`.

## Refactoring a Macro & Unquote Splicing

That `code-critic` macro isn't actually very good. Look at the
duplication! There are two `println` calls which are nearly identical.
Let's clean that up. First, let's create a function to generate those
`println` lists:

```clojure
(defn criticize-code
  [criticism code]
  `(println ~criticism (quote ~code)))

(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~(criticize-code "Cursed bacteria of Liberia, this is bad code:" bad)
       ~(criticize-code "Sweet sacred boa of Western and Eastern Samoa, this is good code:" good)))
```

Notice how the `criticize-code` function returns a syntax-quoted list.
This is how you build up the list that the macro will return.

There's still room for improvement, though. You still have multiple,
nearly-identical calls to a function. In a situation like this it
makes sense to use a seq function; `map` will do.

```clojure
(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~(map #(apply criticize-code %)
             [["Great squid of Madrid, this is bad code:" bad]
              ["Sweet gorilla of Manila, this is good code:" good]])))
```

This is looking a little better. You're mapping over each
criticism/code pair and applying the `criticize-code` function to the
pair. Let's try to run the code:

```clojure
(code-critic {:good (+ 1 1) :bad (1 + 1)})
; => NullPointerException
```

Oh no! That didn't work at all! What happened? Let's expand the macro
to see what Clojure is trying to evaluate:

```clojure
(clojure.pprint/pprint (macroexpand '(code-critic {:good (+ 1 1) :bad (1 + 1)})))
; =>
(do
 ((clojure.core/println
   "Great squid of Madrid, this is bad code:"
   '(1 + 1))
  (clojure.core/println
   "Sweet gorilla of Manila, this is good code:"
   '(+ 1 1))))
```

It looks like Clojure's trying to evaluate the result of a `println`
function call. You can see this more clearly if we simplify the macro
expansion a bit:

```clojure
(do
 ((clojure.core/println "criticism" '(1 + 1))
  (clojure.core/println "criticism" '(+ 1 1))))
```

Here's how this would evaluate:

```clojure
;; After evaluating first println call:
(do
 (nil
  (clojure.core/println "criticism" '(+ 1 1))))

;; After evaluating second println call:
(do
 (nil nil))
```

This is the cause of the exception. `println` evaluates to nil, so we
end up with something like `(nil nil)`. `nil` isn't callable, and we
get a NullPointerException.

We ended up with this code because `map` returns a list. In this case,
it returned a list of `println` expressions. What an inconvenience! As
it happens, though, unquote splicing was invented exactly to handle
this kind of situation. Unquote splicing is represented by `~@`. Here
are some examples:

```clojure
;; Without unquote splicing
`(+ ~(list 1 2 3))
; => (clojure.core/+ (1 2 3))

;; With unquote splicing
`(+ ~@(list 1 2 3))
; => (clojure.core/+ 1 2 3)
```

I think of unquote splicing as unwrapping a seqable data structure,
placing its contents directly within the enclosing syntax-quoted data
structure. It's like the `~@` is a sledgehammer and whatever follows
it is a pinata and the result is the most terrifying and awesome party
you've ever been to.

Anyway, if you use unquote splicing in your code critic, then
everything will work great:

```clojure
(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~@(map #(apply criticize-code %)
              [["Sweet lion of Zion, this is bad code:" bad]
               ["Great cow of Moscow, this is good code:" good]])))
(code-critic {:good (+ 1 1) :bad (1 + 1)})
; =>
Sweet lion of Zion, this is bad code: (1 + 1)
Great cow of Moscow, this is good code: (+ 1 1)
```

Woohoo!

## Things to Watch Out For

Macros have a couple unobvious "gotchas" that you should be aware of:

* variable capture
* double evaluation
* macros all the way down

You're about to learn about all of these, and how to avoid them. I
hope you haven't unstrapped yourself from your thinking mast.

### Variable Capture

Consider the following:

```clojure
(def message "Good job!")
(defmacro with-mischief
  [& stuff-to-do]
  (concat (list 'let ['message "Oh, big deal!"])
          stuff-to-do))
(with-mischief
  (println "Here's how I feel about that thing you did: " message))
; =>
Here's how I feel about that thing you did: Oh, big deal!
```

The `println` call references the symbol `message` which we think is
bound to the string `"Good job!"`. However, the `with-mischief` macro
has created a new binding for `message`.

Notice that we didn't use syntax-quote in our macro. Doing so would
actually result in an exception:

```clojure
(def message "Good job!")
(defmacro with-mischief
  [& stuff-to-do]
  `(let [message "Oh, big deal!"]
     ~@stuff-to-do))
(with-mischief
  (println "Here's how I feel about that thing you did: " message))
; =>
Exception: Can't let qualified name: user/message
```

This is good: syntax-quoting is designed to prevent you from making
this kind of mistake with macros. In the case where you do want to
introduce let bindings in your macro, you can use something called a
"gensym". The `gensym` function produces unique symbols on each
successive call:

```clojure
(gensym)
; => G__655

(gensym)
; => G__658
```

You can also pass a symbol prefix:

```clojure
(gensym 'message)
; => message4760

(gensym 'message)
; => message4763
```

Here's how you could re-write `with-mischief` to be less mischievous:

```clojure
(defmacro without-mischief
  [& stuff-to-do]
  (let [macro-message (gensym 'message)]
    `(let [~macro-message "Oh, big deal!"]
       ~@stuff-to-do
       (println "I still need to say: " ~macro-message))))
(without-mischief
  (println "Here's how I feel about that thing you did: " message))
; =>
Here's how I feel about that thing you did:  Good job!
I still need to say:  Oh, big deal!
```

Because this is such a common pattern, you can use something called an
auto-gensym. Here's an example:

```clojure
(defmacro gensym-example
  []
  `(let [name# "Larry Potter"] name#))
(gensym-example)
; => "Larry Potter"

(macroexpand '(gensym-example))
(let* [name__4947__auto__ "Larry Potter"]
  name__4947__auto__)
```

Notice how both instance of `name#` is replaced with the same gensym'd
symbol. `gensym` and auto-gensym are both used all the time when
writing macros and they allow you avoid variable capture.

### Double Evaluation

Consider the following:

```clojure
(defmacro report
  [to-try]
  `(if ~to-try
     (println (quote ~to-try) "was successful:" ~to-try)
     (println (quote ~to-try) "was not successful:" ~to-try)))
     
;; Thread/sleep takes a number of milliseconds to sleep for
(report (do (Thread/sleep 1000) (+ 1 1)))
```

In this case, you would actually sleep for 2 seconds because
`(Thread/sleep 1000)` actually gets evaluated twice. "Big deal!" your
inner example critic says. Well, if your code did something like
transfer money between bank accounts, this would be a very big deal.
Here's how you could avoid this problem:

```clojure
(defmacro report
  [to-try]
  `(let [result# ~to-try]
     (if result#
       (println (quote ~to-try) "was successful:" result#)
       (println (quote ~to-try) "was not successful:" result#))))
```

By binding the macro's argument to a gensym, you only need to evaluate
it once.

### Macros all the way down

One subtler pitfall of using macros is that you can end up having to
write more and more of them to get anything done. This happens because
macros don't exist at runtime and because their arguments are not
evaluated. For example, let's say you wanted to `doseq` using the
`report` macro:

```clojure
;; Instead of multiple calls to report...
(report (= 1 1))
; => (= 1 1) was successful: true

(report (= 1 2))
(= 1 2) was not successful: false

;; Let's iterate
(doseq [code ['(= 1 1) '(= 1 2)]]
  (report code))
; =>
code was successful: (= 1 1)
code was successful: (= 1 2)
```

This isn't what we want at all. Here's what a macroexpansion for one
of the `doseq` iterations would look like:

```clojure
(if
 code
 (clojure.core/println 'code "was successful:" code)
 (clojure.core/println 'code "was not successful:" code))
```

As you can see, `report` receives the unevaluated symbol `code` in each
iteration, whereas we want it to receive whatever `code` is bound to
at runtime. `report` just can't do that, though. It's like it has
t-rex arms, with runtime values forever out of its reach.

To resolve this situation we might write another macro, like:

```clojure
(defmacro doseq-macro
  [macroname & args]
  `(do
     ~@(map (fn [arg] (list macroname arg)) args)))

(doseq-macro report (= 1 1) (= 1 2))
; =>
(= 1 1) was successful: true
(= 1 2) was not successful: false
```

If you ever find yourself in this situation, take some time to
re-think your approach. There's likely a less confusing way to
accomplish what you want using functions.

We've now covered all the mechanics of writing a macro. Pat yourself
on the back! It's a pretty big deal!

To close things out, it's finally time to put on your pretending cap
and work on that online potion store I talked about at the very
beginning of the chapter.

## Brews for the Brave and True

When you began this chapter, I revealed a dream: to find some kind of
drinkable that, once ingested, would temporarily give me the power and
temperament of an 80's fitness guru, freeing me from the prison of
inhibition and self-awareness. To keep such a magical liquid to
myself, however, would be pure selfishness. I'm sure that someone,
somewhere will invent such a thing so we might as well get to work on
a system for selling this mythical potion. For the sake of this
example, let's call this hypothetical concoction the "Brave and True
Ale." The name just came to me for no reason whatsoever.

Before the orders come "pouring" in (pun! high five!), we'll need to
have some validation in place. These orders will get converted to
Clojure maps before we validate them. What I'm thinking is we want
something like this:

```clojure
;; The shipping details of an order will be represented as a map.
;; There are a couple invalid fields in this one.
(def shipping-details
  {:name "Mitchard Blimmons"
   :address "134 Wonderment Ln"
   :city ""
   :state "FL"
   :postal-code "32501"
   :email "mitchard.blimmonsgmail.com"})

;; Validations are comprise of a key which corresponds to the
;; map to be validated and a vector of error message / validating
;; function pairs. For example, :name has one validating function,
;; not-empty, and if that validation fails we should get the "Please
;; enter a name" error message
(def shipping-details-validation
  {:name
   ["Please enter a name" not-empty]

   :address
   ["Please enter an address" not-empty]

   :city
   ["Please enter a city" not-empty]

   :postal-code
   ["Please enter a postal code" not-empty
    
    "Please enter a postal code that looks like a US postal code"
    #(or (empty? %)
         (not (re-seq #"[^0-9-]" %)))]

   :email
   ["Please enter an email address" not-empty

    "Your email address doesn't look like an email address"
    (or (empty? %)
        #(re-seq #"@" %))]})

;; Here's a hypothetical validation function applied to our data
(validate shipping-details)
; =>
{:email ["Your email address doesn't look like an email address."]
 :city ["Please enter a city"]}
```

So far so good, right? Now we just need to actually write out the
`validate` function. After that we'll write a macro to aid with
validation.

The `validate` function can be decomposed into two functions, one to
apply validations to a single validation and return error messages
and another to accumulate those error messages into a final map of
error messages like the one we see above.

Here's a function for applying validations to a single value:

```clojure
(defn error-messages-for
  "return a seq of error messages
   validation-check-groups is a seq of alternating messages and
   validation checks"
  [value validation-check-groups]
  ;; Filter will return all validation check pairs that fail
  ;; Then we map first over the resulting list, getting a seq
  ;; of error messages
  (map first (filter #(not ((second %) value))
                     (partition 2 validation-check-groups))))

(error-messages-for "" ["Please enter a city" not-empty])
; => ("Please enter a city")

(error-messages-for "SHINE ON"
                    ["Please enter a postal code" not-empty
                     "Please enter a postal code that looks like a US postal code"
                     #(or (empty? %)
                          (not (re-seq #"[^0-9-]" %)))])
; => ("Please enter a postal code that looks like a US postal code")
```

Now we need to accumulate these error messages in a map:

```clojure
(defn validate
  "returns a map with a vec of errors for each key"
  [to-validate validations]
  (reduce (fn [errors validation]
            (let [[fieldname validation-check-groups] validation
                  value (get to-validate fieldname)
                  error-messages (error-messages-for value validation-check-groups)]
              (if (empty? error-messages)
                errors
                (assoc errors fieldname error-messages))))
          {}
          validations))

(validate shipping-details shipping-details-validation)
; =>
{:email ("Your email address doesn't look like an email address")
 :city ("Please enter a city")}
```

Success!

With our validation code in place, we can now validate records to our
heart's content! Most often, validation will look something like this:

```clojure
(let [errors (validate shipping-details shipping-details-validation)]
  (if (empty? errors)
    (render :success)
    (render :failure errors)))

(let [errors (validate shipping-details shipping-details-validation)]
  (if (empty? errors)
    (do (save-shipping-details shipping-details)
        (redirect-to (url-for :order-confirmation)))
    (render "shipping-details" {:errors errors})))
```

Here's where we can an introduce a macro to clean things up a bit.
Notice the pattern? It's:

1. Validate a record and bind result to `errors`
2. Check whether there were any errors
3. If there were, do the success thing
4. Otherwise do the failure thing

I think that we can clean this up by introducing a macro called
`if-valid`. It will meet the rationale for creating a macro in that it
will allow us to write code that's both more concise and more
meaningful. Here's how we'll use it:

```clojure
(if-valid shipping-details shipping-details-validation errors
 (render :success)
 (render :failure errors))

(if-valid shipping-details shipping-details-validation errors
 (do (save-shipping-details shipping-details)
     (redirect-to (url-for :order-confirmation)))
 (render "shipping-details" {:errors errors}))
```

Not a *huge* difference, but it expresses our intention more
succinctly. It's like asking someone to give you the bottle opener
instead of saying "please give me the manual device for removing the
temporary sealant from a glass container of liquid." Here's the
implementation. Note points are floating in the ocean, like `~~~1~~~`:

```clojure
(defmacro if-valid
  "Handle validation more concisely"
  ;; ~~~1~~~
  [to-validate validations errors-name & then-else]
  ;; ~~~2~~~
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ;; ~~~3~~~
       ~@then-else)))
```

That's actually pretty simple! After all this talk about macros and
going through their mechanics in such detail, I bet you were expecting
something more complicated. Sorry, friend. If you're having a hard
time coping with your disappointment, I know of a certain drink that
will help.

Let's break the macro down:

1. It takes four arguments: `to-validate`, `validations`,
   `errors-name`, and the rest-arg `then-else`. Using `errors-name`
   like this is a new strategy. We want to have access to the errors
   within the `then-else` statements, but we need to avoid variable
   capture. Giving the macro the name that the errors should be bound
   to allows us to get around this problem.

2. The syntax quote abstracts the general form of the let/validate/if
   pattern we saw above.

3. We use unquote-splicing to unpack the `if` branches which were
   packed into the `then-else` rest arg.

Woohoo!

## Some Final Advice

Macros are really fun tools that allow you to code with fewer
inhibitions. Using macros, you have a degree of freedom and
expressivity that other languages simply don't allow.

Throughout your Clojure journey you'll probably hear people cautioning
you against their use, saying things like "macros are evil" and "you
should never use macros." Don't listen to these prudes. At least, not
at first! Go out there and have a good time. That's the only way
you'll learn the situations when it's appropriate to use macros.
You'll come out the other side knowing how to use macros responsibly.
