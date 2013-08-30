--- 
title: "Clojure Alchemy: Reading and Evaluation"
link_title: "Clojure Alchemy: Reading and Evaluation"
kind: documentation
---

# Clojure Alchemy: Reading and Evaluation

Those who endeavor to be *brave* and *true* will find a fascinating
metaphor for their journey in the history of chemistry.

As alchemy, it lurched along in fits and starts, inextricably tied to
magic and mysticism. Each chemical substance was its own force of
nature with no rational relationship to other chemicals. Chemical
reactions were proof of the supernatural. The philosopher's stone
&nbsp; the supreme obsession of alchemsists &nbsp; was not just a
wonder tool for transmuting base metals into gold; it was also a
metaphor for transcending duality and reaching enlightenment.

Until, finally, Dmitri Mendeleev developed the periodic table. By
clearly revealing the underlying pattern which governed the
relationships among alements, Mendeleev ended millennia of confusion
among philosophers and scientists.

By this point, you probably have a magical understanding of Clojure.
Your alchemical elements are "form", "special form", "evaluates", and
so forth. By the end of this chapter you'll be able to clearly see the
relationships among Clojure's component parts. We'll get you to this
understanding by explaining Clojure's fundamental mechanisms: reading
and evaluation.

Once you understand these fundamentals, you'll be ready to go full
circle and obtain Clojure enlightenment, transcending code/data
duality. With your own Clojure philosopher's stone in hand, you'll be
able to work with one of the most powerful tools available to
programmers: the macro. And thus you will become the most feared and
respected programmers aliiiiiiiive!

## The Philosopher's Stone

The key to Clojure enlightnment is that *Clojure evaluates data
structures*. You can try this right now in a REPL:

```clojure
(eval (list + 1 2))
; => 3
```

That's right, baby! Clojure just evaluated a list. `(list + 1 2)`
returned a list, which was then passed to eval. The evaluation
resulted in a function call, adding 1 and 2 together to produce 3. And
guess what: all the Clojure code you write that actually does anything
consists of representations of lists!

```clojure
(+ 1 2) ; <= that's a representation of a list
(map first '([0 1] [3 4])) ; <= that's a representation of a list, too!
```

Clojure code consists of representations of lists. These lists are
then parsed by the *reader*, which produces the data structure that's
then *evaluated*. This makes Clojure *homoiconic*: Clojure programs
are represented by Clojure data structures. Overall, the read/eval
process looks something like this:

![read-eval](/assets/images/read-eval/read-eval.png)

To fully understand how what's going on here, we'll explore the
reader and how it parses text to produce data structures. Then we'll
learn about the rules employed when evaluating data structures.
Finally, we'll tie everything together with an introduction to macros.

## The Reader

The reader is Clojure's bridge between the *textual representation*
of a data structure and the data structure itself.



## Evaluation

## Macros
