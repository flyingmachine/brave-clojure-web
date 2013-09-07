--- 
title: Using Emacs with Clojure
link_title: Using Emacs with Clojure
kind: documentation
---

# Using Emacs with Clojure

In this chapter you'll learn how to use Emacs to efficiently develop a
Clojure application. You'll learn:

* How to start a REPL process that's connected to Emacs
* How to work with Emacs windows
* A cornucopia of useful key bindings
    * How to evaluate an expression
    * How to compile the file you're editing
    * How to switch to the namespace of your current file
* How to handle errors
* Intro to Paredit

If you want to start digging in to Clojure code, please do skip ahead!
You can always return later.

## Fire up your REPL!

REPL stands for "Read-Eval-Print Loop", and a REPL is a great tool for
quickly developing code.

To connect Emacs to a REPL, we're going to use the Emacs package
[nrepl.el](https://github.com/clojure-emacs/nrepl.el). If you followed
the instructions in the previous chapter you should already have it
installed, but you can also install it by running `M-x
package-install`, then entering `nrepl` and hitting `enter`.

nrepl is a Clojure library which you can think of as similar to an SSH
daemon. It allows clients to connect to a running Clojure process and
execute code. nrepl.el is an Emacs client for nrepl.

Let's go ahead and use it to start an Emacs REPL session. After we've
got a REPL session running, we'll briefly go over what Emacs is doing.

Using Emacs, open the file `clojure-noob/src/clojure_noob/core.clj`
which you created in Chapter 1. Next, do `M-x nrepl-jack-in`. This
starts a Clojure process with nrepl running and connects Emacs to it.
After a short wait (it should be less than a minute), you should see
something like this:

![nrepl-jack-in](/images/using-emacs-with-clojure/nrepl-jack-in.png)

If you've never seen Emacs split into two halves like this, don't
worry! We'll cover that in a second.

In the mean time, go ahead and try evaluating some code in the REPL.
Try typing in the following. When you hit `enter` after each line, you
should see a result printed.

```clojure
(+ 1 2 3 4)
(println "I'm cuckoo for Cocoa Puffs")
(map inc [1 2 3 4])
(reduce + [5 6 100])
```

Pretty nifty! You can use this REPL just as used `lein repl` from the
first chapter.

You can also do a whole lot more, but before we go into that let's
go over how to work with split-screen Emacs.

## Interlude: Emacs Windows and Frames

Feel free to skip this section if you're already familiar with Emacs
windows!

Emacs was invented in like, 1802 or something, so it uses terminology
slightly different from what you're used to. When you ran
`nrepl-jack-in` above, Emacs split its **frame** into two **windows**:

![Frame and windows](/images/using-emacs-with-clojure/emacs-windows.png)

Here are a bunch of key bindings for working with windows:

| Keys | Description |
|------|-------------|
| C-x o | Switch cursor to another window. Go ahead and try this now to switch between your Clojure file and the REPL |
| C-x 1 | Delete all other windows. This doesn't close your buffers and it won't cause you to lose any work. It just un-splits your frame. |
| C-x 2 | Split window, above and below |
| C-x 3 | Split window, side by side |
| C-x 0 | Delete current window |


I encourage you to try these out. For example, put your cursor in the
left window, the one with the Clojure file, and do `C-x 1`. This
should un-split your frame and you should only see the Clojure code.
Then do:

* `C-x 3` to split the window side by side again
* `C-x o` to switch to the right window
* `C-x b *nrepl*` to switch to the nrepl buffer in the right window

Once you've tried things out a bit, set up Emacs so that it contains
two side-by-side windows with Clojure code on the left and nrepl on
the right, as in the above images. If you're interested in learning
more about windows and frames,
[the Emacs manual](http://www.gnu.org/software/emacs/manual/html_node/elisp/Windows.html#Windows)
has a ton of info.

Now that you can navigate Emacs windows, let's learn some Clojure
development key bindings!

## A Cornucopia of Useful Key Bindings

At the bottom of `core.clj`, add the following:

```clojure
(println "Cleanliness is next to godliness")
```

Then do the following:

1. `C-e` to navigate to the end of the line
2. `C-x C-e`

Once you do this, you should see the text `Cleanliness is next to
godliness` appear in the nrepl buffer:

![keep it clean](/images/using-emacs-with-clojure/nrepl-eval-last-expression.png)

The key binding `C-x C-e` runs the command
`nrepl-eval-last-expression`. As the command suggests, this makes
Emacs send the expression immediately preceding point to nrepl, which
then evaluates it.

Now let's try to run the `-main` function so that we can let the world
know that we're little tea pots:

1. In the core.clj buffer, do `C-c M-n`. The nrepl prompt in the right
   window should now read `clojure-noob.core>`. `C-c M-n` sets the
   nrepl namespace to the namespace listed at the top of your current
   file, in this case `clojure-noob.core`. We haven't gone into detail
   about namespaces yet, but for now it's enough to know that
   namespaces are an organizational mechanism which allows us to avoid
   naming conflicts.
2. Enter `(-main)` at the prompt

You should see `I'm a little teapot!`. How exciting!

Now let's create a new function and run it. At the bottom of
`core.clj`, add the following:

```clojure
(defn train
  []
  (println "Choo choo!"))
```

When you're done, save your file and do `C-c C-k`. This compiles your
current file within the nrepl session. Now if you run `(train)` in
nrepl it will echo back `Choo choo!`.

While still in nrepl, try `C-↑`, which is Control +
the up key. `C-↑` and `C-↓` cycle
through your nrepl history.

Finally, try this:

1. Write `(-main` at the nrepl prompt. Note the lack of a closing
   parenthesis.
2. Press `C-↵`.

nrepl should close the parenthis and evaluate the expression.

The [nrepl.el README](https://github.com/clojure-emacs/nrepl.el) has a
comprehensive list of key bindings which you can learn over time, but
for now here's a summary of the key bindings we just went over:

### Clojure Buffer Key Bindings

| Keys | Description |
|------|-------------|
| C-c M-n | Switch to namespace of the current buffer |
| C-x C-e | Evaluate the expression immediately preceding point |
| C-c C-k | Compile current buffer |

### nrepl Buffer Key Bindings

| Keys | Description |
|------|-------------|
| C-&uarr;, C-&darr; | Cycle through nrepl history |
| C-&crarr; | Close parentheses and evaluate |

## How to Handle Errors

Let's write some buggy code so that we'll know how Emacs responds to
it. We'll do this in both the nrepl buffer and in the `core.clj`
buffer.

At the prompt, type this and hit enter: 

```clojure
(map)
```

You should see something like this:

![nrepl error](/images/using-emacs-with-clojure/nrepl-error.png)

To get rid of the stack trace in the left window, do

1. `C-x o` to swith to the window
2. `q` to close the stack trace and go back to nrepl

If you want to view the error again, you can switch to the buffer `*nrepl-error*`.

Now try going to the `core.clj` buffer and do almost the same thing:

1. Add `(map)` to the end
2. `C-c C-k` to compile
3. Follow steps 1 and 2 above to close the stack trace

## Paredit

While writing code in the Clojure buffer, you may have noticed some
unexpected things happening. For example, every time you type `(`, `)`
immediately gets inserted.

This is thanks to paredit-mode, a minor mode which turns Lisp's
profusion of parentheses from a liability into an asset. Paredit
ensures that all parentheses, double quotes, and brackets are closed,
relieving you of that odious burden.

Paredit also offers key bindings to easily navigate and alter the
structure created by all those parenthess. Below we'll go over the
most useful key bindings, but you can also check out a
[comprehensive cheat sheet](https://github.com/georgek/paredit-cheatsheet/blob/master/paredit-cheatsheet.pdf?raw=true)
(in the cheat sheet, the red pipe represents point).

If you're not used to it, though, paredit can sometimes be annoying.
You can always disable it with `M-x paredit-mode`, which toggles the
mode on and off. However, I think it's more than worth your while to
take some time to learn it.

The following shows you the most useful key bindings. Point will be
represented as a vertical pipe, `|`.

### Wrapping and Slurping

```clojure
;; Start with this
(+ 1 2 3 4)

;; We want to get to this
(+ 1 (* 2 3) 4)

;; Place point
(+ 1 |2 3 4)

;; Type "M-(", the binding for paredit-wrap-round
(+ 1 (|2) 3 4)

;; Add the asterisk and a space
(+ 1 (* |2) 3 4)

;; Now slurp in the "3":
;; press C-→

(+ 1 (* |2 3) 4)
```

So, wrapping surrounds the expression after point with parentheses.
Slurping moves a closing parenthesis to include the next expression to
the right.

### Barfing

Suppose, in the above example, you accidentally slurped the `4`.
Here's how you'd un-slurp it:

```clojure
;; Start with this
(+ 1 (* 2 3 4))

;; We want to get to this
(+ 1 (* 2 3) 4)

;; Place your cursor anywhere in inner parens
(+ 1 (|* 2 3 4))

;; Do C-←
(+ 1 (|* 2 3) 4)
```

Ta-da!

### Navigation

Often when writing lisp you'll work with expressions like

```clojure
(map (comp record first)
     (d/q '[:find ?post
            :in $ ?search
            :where
            [(fulltext $ :post/content ?search)
             [[?post ?content]]]]
          (db/db)
          (:q params)))
```

It's useful to quickly jump from one sub-expression to the next. If
you put point right before an opening paren, `C-M-f` will take you to
the closing paren. Similarly, if you're right after a closing paren,
`C-M-b` will take you to the opening paren.

### Summary

| Keys | Description |
|------|-------------|
| M-x paredit-mode | Toggle paredit mode |
| M-( | paredit-wrap-round, surround expression after point in parentheses |
| C-&rarr; | Slurp; move closing parenthesis to the right to include next expression |
| C-&larr; | Barf; move closing parenthesis to the left to exclude last expression |
| C-M-f, C-M-b | Move to the opening/closing parenthesis |

## Chapter Summary

Oh my god, you're using Emacs!

Now that you've gotten your environment set up, let's start learning
Clojure in earnest!
