---
title: Functional Programming
link_title: Functional Programming
kind: documentation
---

# Functional Programming

So far, you've focused on familiarizing yourself with the tools that
Clojure provides: immutable data structures, functions, abstractions,
and so on. In this chapter, you'll begin learning how to think about
your programming tasks in a way that makes the best use of those
tools. You'll begin integrating your experience into a new mindset,
the functional programming mindset.

The core concepts you'll learn include: what pure functions are and
why they're useful; how to work with immutable data structures and why
they're superior to their mutable cousins; how disentangling data and
functions gives you more power and flexibility, and why it's powerful
to program to a small set of data abstractions. The result of shoving
all this knowledge into your brain matter is that you'll have an
entirely new approach to problem solving!

After going over these topics you'll put everything you've learned to
use in order to write a terminal-based game based on an ancient mystic
mind-training device which can now be found in Cracker Barrel
restaurants across America: Peg Thing!

## Pure Functions, What and Why

With the exception of `println` and `rand`, all the functions you've
used up till now have been pure functions:

```clojure
(get [:chumbawumba] 0)
; => :chumbawumba

(reduce + [1 10 5])
; => 16

(str "wax on " "wax off")
; => "wax on wax off"
```

What makes them pure functions, and why does it matter? A function is
pure if it meets two qualifications:

1.  It always returns the same result given the same arguments. This is
    called "referential transparency" and you can add it to your list of
    five-dollar programming terms.
2.  It doesn't cause any side effects, e.g. it doesn't "change the
    external world" by changing external mutable objects or outputting
    to i/o.

These qualities matter because they make it easier for us to reason
about our programs. Pure functions are easier to reason about because
they're completely isolated, unable to impact other parts
of your system. When you use them, you don't have to ask yourself,
"What could I break by calling this function?" They're also
consistent; you'll never find yourself trying to figure out why the
passing a function the exact same arguments results in different
return values.

For example, when was the last time you fretted over adding two
numbers? Pure functions are as stable and problem-free as arithmetic.
They're these stupendous, stable little bricks of functionality
that you can confidently use as the foundation of your program.
Let's look at referential transparency and lack-of-side-effects in
more detail so that we'll know exactly what they are how they're
helpful.

### Pure Functions Are Referentially Transparent

Referentially transparent functions always returns the same result
when called with the same argument. In order to achieve this, they
only rely on 1) their own arguments and 2) immutable values to
determine their return value. Mathematical functions are referentially
transparent:


```clojure
(+ 1 2)
; => 3
```

If a function relies on an immutable value, it's referentially
transparent. The string ", Daniel-san" is immutable, so the following
function is referentially transparent:

```clojure
(defn wisdom
  [words]
  (str words ", Daniel-san"))
(wisdom "Always bathe on Fridays")
; => "Always bathe on Fridays, Daniel-san"
```

By contrast, the following functions do not yield the same result with
the same arguments and, therefore, are not referentially transparent.
Any function which relies on a random number generator cannot be
referentially transparent:

```clojure
(defn year-end-evaluation
  []
  (if (> (rand) 0.5)
    "You get a raise!"
    "Better luck next year!"))
```

If your function reads from a file, it's not referentially
transparent because the file's contents can change. The function
`analyze-file` below is not referentially transparent, but the
function `analysis` is:

```clojure
(defn analysis
  [text]
  (str "Character count: " (count text)))

(defn analyze-file
  [filename]
  (analysis (slurp filename)))
```

When using a referentially transparent function, you never have to
consider what possible external conditions could affect the return
value of the function. This is especially important if your function
is used multiple places or if it's nested deeply in a chain of
function calls. In both cases, you can rest easy knowing that changes
to external conditions won't cause your code to break.

Another way to think about this is that reality is largely
referentially transparent. This is what lets you form habits. If
reality weren't referentially transparent, you wouldn't be able to
mindlessly plug your iPod into your bathroom speakers and play "The
Final Countdown" by Europe every morning when you take a shower. Each
of these actions will have the same result pretty much every time you
perform them, which lets you put them on autopilot.

### Pure Functions Have No Side Effects

To perform a side effect is to change the association between a name
and its value within a given scope. For example, in Javscript:

```javascript
var haplessObject = {
  emotion: "Carefree!"
};

var evilMutator = function(object){
  object.emotion = "So emo :'(";
}

evilMutator(haplessObject);
haplessObject.emotion;
// => "So emo :'("
```

Of course, your program has to have some side effects; it writes to a
disk, which is changing the association between a filename and a
collection of disk sectors; it changes the rgb values of your
monitor's pixels, and so on. Otherwise, there'd be no point in running
it.

The reason why side effects are potentially harmful, though, is that
they prevent us from being certain what the names in our code are
referring to. This makes it difficult or impossible to know what our
code is doing. It's very easy to end up wondering how a name came to
be associated with a value and it's usually difficult to figure out
why. When you call a function which doesn't have side effects, you
only have to consider the relationship between the input and the
output, not the changes that could be rippling through your system.

Functions which have side effects, however, place more of a burden on
your mind grapes: now you have to worry about how the world is
affected when you call the function. Not only that, every function
which depends on a side-effecting function gets "infected". It's
another component which requires extra care and thought as you build
your program.

If you have any significant experience with a language like Ruby or
Javascript, you've probably run into this problem. As an object gets
passed around, its attributes somehow get changed and you can't figure
out why. Then you have to buy a new computer because you've chucked
yours out the window. If you've read anything about object-oriented
design, you'll notice that a lot of writing has been devoted to
strategies for managing state and reducing side effects for just this
reason.

Therefore, it's a good idea to look for ways to limit the use of side
effects in your code. Think of yourself as an overeager bureaucrat,
&mdash; let's call you Kafka Human &mdash; scrutinizing each side effect
with your trusty BureauCorp clipboard in hand. Not only will this lead
to better code, it's also sexy and dramatic!

Luckily for you, Clojure makes your job easier by going to great
lengths to limit side effects &mdash; all of its core data structures
are immutable. You cannot change them in place no matter how hard you
try! If you're unfamiliar with immutable data structures, however, you
might feel like your favorite tool has been taken from you. How can
you *do* anything without side effects? Well, guess what! That's What
the next sections all about! How about this segue, eh? Eh?

## Living with Immutable Data Structures

Immutable data structures ensure that your code won't have side
effects. As you now know with all your heart, this is a good thing.
But how do you get anything done without side effects?

### Recursion instead of for/while

Raise your hand if you've ever written something like this
(javascript):

```javascript
var wrestlers = getAlligatorWrestlers();
var totalBites = 0;
var l = wrestlers.length;
// Side effect on i! Bad!
for(var i=0; i < l; i++){
  // Side effect on totalBites! Bad!
  totalBites += wrestlers[i].timesBitten;
}
```

or this:

```javascript
var allPatients = getArkhamPatients();
var analyzedPatients = [];
var l = allPatients.length;
// Side effect on i! Bad!
for(var i=0; i < l; i++){
  if(allPatients[i].analyzed){
    // Side effect on analyzedPatients! Bad!
    analyzedPatients.push(allPatients[i]);
  }
}
```

Using side effects in this way &mdash; mutating "internal" variables
&mdash; is pretty much harmless. You're creating some value to be used
elsewhere, as opposed to changing an object you've received.

But Clojure's core data structures don't even allow these harmless
mutations. So what can you do?

Let's ignore the fact that you can easily use `map` and `reduce` to
accomplish the work done above. In these situations &mdash; iterating
over some collection to build a result &mdash; the functional
alternative to mutation is recursion.

Let's look at the first example, building a sum. In Clojure, there is
no assignment operator. You can't associate a new value with a name
within the same scope:

```clojure
(defn no-mutation
  [x]
  (println x)

  ;; let creates a new scope
  (let [x "Kafka Human"]
    (println x))

  ;; Exiting the let scope, x is the same
  (println x))
(no-mutation "Existential Angst Person")
; => 
; Existential Angst Person
; Kafka Human
; Existential Angst Person
```

In Clojure, we can get around this apparent limitation through
recursion. The following example shows the general approach to
recursive problem-solving.

```clojure
(defn sum
  ([vals] (sum vals 0)) ;; ~~~1~~~
  ([vals accumulating-total]
     (if (empty? vals) ;; ~~~2~~~
       accumulating-total
       (sum (rest vals) (+ (first vals) accumulating-total)))))
```

This function takes two arguments, a collection to process (`vals`)
and an accumulator (`accumulating-total`), and we use arity
overloading (covered in "[Do Things](/do-things)") to provide a
default value of 0 for `accumulating-total`. (1)

Like all recursive solution, this function checks the argument it's
processing against a base condition. In this case, we check whether
`vals` is empty. If it is, we know that we've processed all the
elements in the collection and so we return `accumulating-total`.

If `vals` isn't empty it means we're still working our way through the
sequence, so we recursively call `sum` with the "tail" of vals with
`(rest vals)` and the sum of the first element of vals and the
accumulating total with `(+ (first vals) accumulating-total)`. In this
way, we build up `accumulating-total` and at the same time reduce
`vals` until it reaches the base case of an empty collection.

Here's what the recursive function call might look like:

```clojure
(sum [39 5 1]) ; single-arity body calls 2-arity body
(sum [39 5 1] 0)
(sum [5 1] 39)
(sum [1] 44)
(sum [] 45) ; base case is reached, so return accumulating-total
; => 45
```

Each recursive call to `sum` creates a new scope where `vals` and
`accumulating-total` are bound to different values, all without
needing to alter the values originally passed to the function or
perform any internal mutation. As you can see, you can get along fine
without mutation.

One note: you should generally use `recur` when doing recursion for
performance reasons. This is because Clojure doesn't provide tail call
optimization, a topic we will never bring up again! So here's how
you'd do this with `recur`:

```clojure
(defn sum
  ([vals]
     (sum vals 0))
  ([vals accumulating-total]
     (if (empty? vals)
       accumulating-total
       (recur (rest vals) (+ (first vals) accumulating-total)))))
```

Using `recur` isn't too important if you're recursively operating on a
small collection, but if your collection contains thousands or
millions values then you will definitely need to whip out `recur`.

One last thing! You might be thinking, "Wait a minute, what if I end
up creating thousands of intermediate values? Doesn't this cause the
program to thrash because of garbage collection or whatever?

Very good question, eagle-eyed reader! The answer is no! This is
because, behind the scenes, Clojure's immutable data structures are
implemented using something called structural sharing, which is
totally beyond the scope of this book. Just, uh, it's kind of like
git! I don't know, google it!

### Functional Composition instead of Attribute Mutation

Another way you might be used to using mutation is to build up the
final state of an object. In the Ruby below example, the
GlamourShotCaption object uses mutation to clean input by removing
trailing spaces and capitalizing "lol":

```ruby
class GlamourShotCaption
  attr_reader :text
  def initialize(text)
    @text = text
    clean!
  end

  private
  def clean!
    text.trim!
    text.gsub!(/lol/, "LOL")
  end
end

best = GlamourShotCaption.new("My boa constrictor is so sassy lol!  ")
best.text
; => "My boa constrictor is so sassy LOL!"
```

`GlamourShotCaption` encapsulates the knowledge of how to clean a
glamour shot caption. On creating a `GlamourShotCaption` object, you
assign text to an instance variable and progressively mutate it. So
far so good, right? The example below shows how you might do this in
Clojure. It uses `require` in order to allow you to access the string
function library, a concept that will be covered in the next chapter.

```clojure
(require '[clojure.string :as s])
(defn clean
  [text]
  (s/replace (s/trim text) #"lol" "LOL"))

(clean "My boa constrictor is so sassy lol!  ")
; => "My boa constrictor is so sassy LOL!"
```

Easy peasy. No mutation required. Instead of progressively mutating an
object, you apply a chain of functions to an immutable value.

This example also starts to show the limitations of object-oriented
programming. In OOP, one of the main purposes of classes is to provide
data hiding &mdash; something that isn't necessary with immutable data
structures.

You also have to tightly couple methods with classes, thus limiting
the reusability of the methods. In the Ruby example, you have to do
extra work to reuse the `clean!` method. In Clojure, `clean` will work
on any string at all. By both a) decoupling functions and data and b)
programming to a small set of abstractions, you end up with more
reusable, composable code. You gain power and lose nothing.

If you think that this is a trivial example and not realistic, then
consider all the times you've created very simple Ruby classes which
essentially act as decorated hashes, but which aren't allowed to take
part in the hash abstraction without work. The takeaway here is that
you can just use function composition instead of a succession of
mutations.

Once you start using immutable data structures you'll quickly feel
confident in your ability to get stuff done. Then, you'll feel even
more confident because you won't have to worry about what dirty code
might be getting its greasy paws on your precious, mutable variables.
It'll be great!

## Cool Things to do With Pure Functions

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
You can do this because, as you learned above, pure functions are
referentially transparent:

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

## Chapter Summary

-   Pure functions are referentially transparent and side-effect free.
    This makes them easy to reason about.
-   Try to keep your dirty, impure functions to a minimum.
-   In an immutable world, you use recursion instead of for/while and
    function composition instead of successions of mutations
-   Pure functions allow powerful techniques like function composition
    functions and memoization
