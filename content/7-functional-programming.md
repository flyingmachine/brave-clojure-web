--- 
title: Functional Programming
link_title: Functional Programming
kind: documentation
---

# Functional Programming

<!---
If I were Mr. Miyagi and you were Daniel-san, the last chapter would
be the equivalent of "wax on, wax off." Also, why haven't you cleaned
my car!?!?
-->

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

## Pure Functions, What and Why

With the exception of `println`, all the functions we've used up till
now have been pure functions:

```clojure
;; Pure functions:
(get [:chumbawumba] 0)
; => :chumbawumba

(reduce + [1 10 5])
; => 16

(str "wax on " "wax off")
; => "wax on wax off"
```

What makes them pure functions, and why does it matter?

A function is pure if it meets two qualifications:

1. It always returns the same result given the same arguments. This is
   called "referential transparency" and you can add it to your list of
   five-dollar programming terms.
2. It doesn't cause any side effects, e.g. it doesn't "change the
   external world" by changing external mutable objects or outputting
   to i/o.

These qualities matter because they make it easier for us to reason
about our programs. Pure functions are easier to reason about because
they're completely isolated, concealing no dependencies on other parts
of your system. When you use them, you don't have to ask yourself,
"Ok, what could I break by calling this function?" You don't have to
spend time hunting around your codebase and cramming additional
information into your limited-capacity short-term memory.

For example, when was the last time you fretted over adding two
numbers? Pure functions are as stable and problem-free as arithmetic.
They're like these stupendous, stable little bricks of functionality
that you can confidently use as the foundation of your program.

Let's look at referential transparency and lack-of-side-effects in
more detail so that we'll know exactly what they are how they're
helpful.

### Pure Functions Are Referentially Transparent

Referentially transparent functions always returns the same result
when called with the same argument. In order to achieve this, they
only rely on 1) their own arguments and 2) immutable values to
determine their return value.

```clojure
;; Mathematical functions are referentially transparent
(+ 1 2)
; => 3

;; If a function relies on an immutable value, it's referentially
;; transparent. The string ", Daniel-san" is immutable, so the
;; function is referentially transparent
(defn wisdom
  [words]
  (str words ", Daniel-san"))
```

By contrast, these functions do not yield the same result with the
same arguments and, therefore, are not referentially transparent:

```clojure
;; Any function which relies on a random number generator
;; cannot be referentially transparent
(defn random-judgment
  [judgee]
  (if (> (rand) 0.5)
    (str judgee " is great!")
    (str judgee " is terrible :(")))

;; If your functions reads from a file, it's not referentially
;; transparent because the file's contents can change
(defn file-analyzer
  [filename]
  (let [contents (slurp filename)]
    (analyze-file contents))
;; Note, however, that "analyze-file" could very well be referentially
;; transparent - it could very well return the same result every time
;; it's passed the same string.
```

When using a referentially transparent function, you never have to
consider what possible external conditions could affect the return
value of the function.

This is especially important if your function is used multiple places
or if it's nested deeply in a chain of function calls. In both cases,
you can rest easy knowing that changes to external conditions won't
cause your code to break.

Another way to think about this is that reality is largely
referentially transparent. This is what lets you form habits. If
reality weren't referentially transparent, you wouldn't be able to
mindlessly plug your iPod into your bathroom speakers and play "The
Final Countdown" by Europe every morning when you take a shower. Each
of these actions will have the same result pretty much every time you
perform them, which lets you put them on autopilot.

### Pure Functions Have No Side Effects

To perform a side effect is to change the association between a name
and its value within a given scope.

For example, in Javscript:

```javascript
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
your mind grapes: now you have to worry about how the world is
affected when you call the function. Not only that, every function
which calls a side-effecting function gets "infected". It's another
component which requires extra care and thought as you build your
program.

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
&mdash; let's call you Kafka Man &mdash; scrutinizing each side effect
with your trusty BureauCorp clipboard in hand. Not only will this lead
to better code, it's also sexy and dramatic!

Luckily for you, Clojure makes your job easier by going to great
lengths to limit side effects &mdash; all of its core data structures
are immutable. You cannot change them in place no matter how hard you
try!

If you're unfamiliar with immutable data structures, you might feel
like your favorite tool has been taken from you. How can you *do*
anything without side effects? Well, guess what! That's What the next
sections all about! How about this segue, eh? Eh?

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
// Side effect on i! Boo!
for(var i=0; i < l; i++){
  // Side effect on sum! Boo!
  totalBites += wrestlers[i].timesBitten;
}
```

or this:

```javascript
var allPatients = getArkhamPatients();
var analyzedPatients = [];
var l = allPatients.length;
// Side effect on i! Boo!
for(var i=0; i < l; i++){
  if(allPatients[i].analyzed){
    // Side effect on analyzedPatients! Boo!
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
  ;; = is a boolean operation
  (= x 3)
  (println x)

  ;; let creates a new scope
  (let [x "Kafka Man"]
    (println x))

  ;; Exiting the let scope, x is the same
  (println x))
(no-mutation "Existential Angst Woman")
; => 
; Existential Angst Woman
; Kafka Man
; Existential Angst Woman

```

In Clojure, we can get around this apparent limitation through
recursion:

```clojure
(defn sum
  ;; Clojure allows overloading on arity. If you call sum with only
  ;; one argument, it just calls the two-argument body with 0 as the
  ;; second argument.
  ([vals]
     (sum vals 0))
  ([vals accumulating-total]
     ;; rest returns the "tail" of vals, e.g. (rest [0 1]) => [1]
     ;; thus "vals is eventually empty and we return the accumulating
     ;; total
     (if (empty? vals)
       accumulating-total
       ;; if vals isn't empty, i.e. we're still working our way
       ;; through the sequence, then recur with the tail of vals
       ;; and the sum of the first element of vals and the
       ;; accumulating total.
       (sum (rest vals) (+ (first vals) accumulating-total)))))

(sum [39 5 1])
; => 45

;; This is what gets called recursively:
(sum [39 5 1])
; single-arity body calls 2-arity body
(sum [39 5 1] 0)
(sum [5 1] 39)
(sum [1] 44)
(sum [] 45)
; => 45
```

Each recursive call to `sum` creates a new scope where `vals` and
`accumulating-total` are bound to different values, all without
needing to alter the values originally passed to the function or
perform any internal mutation.

Note, however, that you should generally use `loop` when doing
recursion for performance reasons. This is because Clojure doesn't
provide tail call optimization, a topic we will never bring up again!

So here's how you'd do this with `loop`:

```clojure
(defn sum
  ([vals]
     (sum vals 0))
  ([vals accumulating-total]
     (loop [vals vals
            accumulating-total accumulating-total]
       (if (empty? vals)
         accumulating-total
         (recur (rest vals) (+ (first vals) accumulating-total))))))
```

This isn't too important if you're recursively operating on a small
collection, but if your collection contains thousands or millions
values then you will definitely need to whip out `loop`.

Now let's try accumulation in Clojure. You'll notice that this is
really similar to our hobbit symmetrizing code:

```clojure
(defn analyzed-patients
  [patients]
  (loop [remaining-patients patients
         analyzed []]
    (let [current-patient (first remaining-patients)]
      (cond (empty? remaining-patients)
            analyzed

            ;; Note that conj produces a new value without mutating
            ;; anything, unlike Javascript's array.push which alters
            ;; the array
            (analyzed? current-patient)
            (recur (rest remaining-patients)
                   (conj analyzed current-patient))

            :else
            (recur (rest remaining-patients)
                   analyzed)))))
```

Hey check that out, we introduced a new form: `cond`. `cond` is like a
multi-if, where you give it a series of if/then's and end it with an
optional `:else`:

* If there are no more remaining patients, return the
  vector of analyzed patients.
* If the current patient has been analyzed, recur. Bind
  `remaining-patients` to a vector which consists of all patients
  except the current one. Bind `analyzed` to a new vector which
  includes the current vector of analyzed patients as well as the
  current patient.
* Otherwise recur. Bind `remaining-patients` same as above. Bind
  `analyzed` to the existing vector of analyzed patients.

As you can see, Clojure gets along fine without mutation.

One last thing! You might be thinking, "Wait a minute, what if I end
up creating thousands of intermediate values? Doesn't this cause the
program to thrash because of garbage collection or whatever?

Very good question, eagle-eyed reader! The answer is no! This is
because, behind the scenes, Clojure's immutable data structures are
implemented using something called structural sharing, which is
totally beyond the scope of this book. Just, uh, it's kind of like
git! I don't know, google it!

### Functional Composition instead of Attribute Mutation

Here's another way we might use mutation:

```ruby
class GlamourShotCaption
  attr_reader :text
  def initialize(text)
    @text = text
    clean!
  end

  def save!
    File.open("read_and_feel_giddy.txt", "w+"){ |f|
      f.puts text
    }
  end

  private
  def clean!
    text.trim!
    text.gsub!(/lol/, "LOL")
  end
end

best = GlamourShotCaption.new("My boa constrictor is so sassy lol!  ")
best.save!
```

GlamourShotCaption encapsulates the knowledge of how to clean and save
a glamour shot caption. On creating a GlamourShotCaption object, you
assign text to an instance variable and progressively mutate it. So
far so good, right? Here's how we might do this in Clojure:

```clojure
;; We'll go over require soon
(require '[clojure.string :as s])

;; This uses the -> macro which we'll cover more in
;; "Clojure Alchemy: Reading, Evaluation, and Macros"
(defn clean
  [text]
  (-> text
      s/trim
      (s/replace #"lol" "LOL")))

(spit "read_and_feel_giddy.txt"
      (clean "My boa constrictor is so sassy lol!  "))
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
on any string at all. By decoupling functions and data and
programaming to a small set of abstractions we end up with more
reusable, composable code. You gain power and lose nothing.

If you think that this is a trivial example and not realistic, then
consider all the times you've created very simple Ruby classes which
essentially act as decorated hashes, but which aren't allowed to take
part in the hash abstraction without work.

Anyway, the takeaway here is that you can just use function
composition instead of a succession of mutations.

Once you start using immutable data structures you'll quickly feel
confident in your ability to get stuff done. Then, you'll feel even
more confident because you won't have to worry about what dirty code
might be getting its greasy pause on your precious, mutable variables.
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

* Pure functions are referentially transparent and side-effect free.
  This makes them easy to reason about.
* Try to keep your dirty, impure functions to a minimum.
* In an immutable world, you use recursion instead of for/while and
  function composition instead of successions of mutations
* Pure functions allow powerful techniques like function composition
  functions and memoization
