--- 
title: Do Things
link_title: Do Things
kind: documentation
---

# Do Things

While you've undoubtedly heard of Clojure's awesome concurrency
support and other stupendous features, Clojure's most salient
characteristic is that it is a Lisp. We're going to explore this Lisp
core. And this core is comprised of two parts: functions and data.

Functions and data are intertwined: functions are understood in terms
of the data they operate on, and data is understood in terms of the
functions that operate on it. We'll handle this pedagogical
chicken-and-egg problem by briefly introducing the most common Clojure
data structures. Then, we'll dive deep into functions.

All of this groundwork will allow us to write some super important
code. In the last section, we'll create a model of a hobbit so that we
can create a function to hit it in a random spot. Super! Important!

As you go through these examples, it's really important that you type
them out and run them. Programming in a new language is a skill, and,
just like yodeling or synchronized swimming, you have to practice it
to learn it.

One final note before we start: in Clojure, the core data structures
aren't mutable. For example, we'll be looking at the vector data
structure, which looks a lot like arrays in other languages:

```clojure
(def failed-protagonist-names
  ["Larry Potter"
   "Doreen the Explorer"
   "The Incredible Bulk"])
```

Most other languages would allow you to manipulate this array. In
Ruby or Javascript, for example:

```ruby
failed_protagonist_names = [
  "Larry Potter",
  "Doreen the Explorer",
  "The Incredible Bulk"
]
failed_protagonist_names[0] = "Gary Potter"
failed_protagonist_names # => [
  "Gary Potter",
  "Doreen the Explorer",
  "The Incredible Bulk"
]
```

In Clojure, there is no equivalent. We'll cover the implications of
immutability in more detail later on, but for now keep in mind that
immutability distinguishes these data structures from the ones you're
used to in other programming languages.

## Just Enough Data Structures

This section will briefly introduce you to core Clojure data
structures. If you're curious about the functions used,
[ClojureDocs](http://clojuredocs.org/) is a great reference for
finding out more. You can also use `(doc functionname)` and `(source
functionname)` in the REPL to see the documentation or source code for
a function.

We won't go over all the functions that can operate on each data
structure, so I recommend you check out the
[Clojure Cheatsheet](http://clojure.org/cheatsheet).

### Numbers

We're only going to work with integers and floats for awhile, though
[Clojure's treatment of numbers](http://clojure.org/data_structures#Data
Structures-Numbers) is more sophisticated than that. Examples:

```clojure
93
1.2
```

### Strings

Here are some string examples:

```clojure
(println "Lord Voldemort")
(println "\"He who must not be named\"")
(println "\"Great cow of Moscow!\" - Hermes Conrad")
```

Notice that Clojure only allows double quotes to delineate strings.
`'Lord Voldemort'`, for example, is not a valid string. Also notice
that Clojure doesn't have string interpolation. It only allows
concatenation via the `str` function:

```clojure
(def name "Chewbacca")
(println (str "\"Uggllglglglglglglglll\" - " name))
; => "Uggllglglglglglglglll" - Chewbacca
```

### Maps

Maps are similar to dictionaries or hashes in other languages. They're
a way of associating some value with some other value. Examples:

```clojure
;; ":a" is a keyword and we'll cover it in the next section
{:a 1
 :b "boring example"
 :c []}

;; Associate "string-key" with the "plus" function
{"string-key" +}

;; Maps can be nested
{:name {:first "John" :middle "Jacob" :last "Jingleheimerschmidt"}}
```

You can look up values in maps:

```clojure
(get {:a 0} :a)
; => 0

(get {:a 0 :b 1} :b)
; => 1

(get {:a 0 :b {:c "ho hum"}} :b)
; => {:c "ho hum"}
```

Notice that we didn't need to use commas. In Clojure, commas are
considered whitespace.

### Keywords

Clojure keywords are best understood by the way they're used. They're
primarily used as keys in maps, as you can see above. Examples of
keywords:

```clojure
:a
:rumplestiltsken
:34
:_?
```

Keywords can be used as functions. For example:

```clojure
;; Look up :a in map
(:a {:a 1 :b 2 :c 3})
; => 1

;; This is equivalent to:
(get {:a 1 :b 2 :c 3} :a)
; => 1
```

I think this is super cool and I do it all the time. You should do it,
too!

### Vectors

A vector is similar to an array in that it's a 0-indexed collection:

```clojure
;; Here's a vector
[3 2 1]

;; Here we're returning elements of vectors
(get [3 2 1] 0)
; => 3

;; Another example of getting by index. Notice as well that vector
;; elements can be of any type and you can mix types.
(get ["a" {:name "Pugsley Winterbottom"} "c"] 1)
; => {:name "Pugsley Winterbottom"}
```

Vectors do differ from arrays in important ways, and we'll go over
those differences later.

### Lists

Lists are similar to vectors in that they're linear collections of
values. You can't access their elements in the same way, though:

```clojure
;; Here's a list - note the preceding single quote
'(1 2 3 4)
; => (1 2 3 4)
;; Notice that the REPL prints the list without a quote. This is OK,
;; and it'll be explained later.


;; Doesn't work for lists
(get '(100 200 300 400) 0)

;; This works but has different performance characteristics which we
;; don't care about right now
(nth '(100 200 300 400) 3)
; => 400
```

### Sets

Sets are collections of unique values:

```clojure
;; Literal notation
#{"hannah montanna" "miley cyrus" 20 45}

;; If you try to add :b to a set which already contains :b,
;; the set still only has one :b
(conj #{:a :b} :b)
; => #{:a :b}

;; You can check whether a value exists in a set
(get #{:a :b} :a)
; => :a

(get #{:a :b} "hannah montanna")
; => nil
```

### Symbols and Naming

As [clojure.org](http://clojure.org/data_structures#Data
Structures-Symbols) states, symbols are identifiers that are normally
used to refer to something. Let's associate a value with a symbol:

```clojure
(def failed-protagonist-names
  ["Larry Potter"
   "Doreen the Explorer"
   "The Incredible Bulk"])
```

In this case, `def` associates the value
`["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"]` with
the symbol `failed-protagonist-names`.

You might be thinking, "So what? Every other programming language lets
me associate a name with a value. Big whoop!" Lisps, however, allow
you to manipulate symbols as data, something we'll see a lot of when
we start working with macros. Functions can return symbols and take
them as arguments:

```
(identity 'test)
; => test
```

For now, though, it's OK to think "Big whoop!" and not be very
impressed.

Thus concludes our Clojure data structures primer. Now it's time to
dig in to functions and see how these data structures can be used!

## Functions

One of the reasons people go nuts over Lisps is that they allow you to
build programs which behave in complex ways, yet the primary building
block &mdash; the function &mdash; is so simple. This section will
initiate you in the beauty and elegance of Lisp functions by
explaining:

* Calling functions
* Defining functions
* Anonymous functions
* Returning functions

### Calling Functions

By now you've evaluated many function calls in the REPL:

```clojure
(+ 1 2 3 4)
(* 1 2 3 4)
(first [1 2 3 4])
```

You've probably been able to deduce that a function call returns a
value. You've also probably deduced that function calls take the
general form of:

```
<function-call> ::= (<function-expression> [<arg>*])
```

(If you're not familiar with
[Backus-Naur Form](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form),
this basically says "A function call is denoted by an opening
parenthesis, followed by a function expression, followed by one or
more optional arguments.)

What you might not know, however, is how flexible this structure is.
For example, a `function expression` can be any expression which
evaluates to a function. The following are all valid function calls
which evaluate to `6`:

```clojure
;; Return value of "or" is first truthy value, and + is truthy
((or + -) 1 2 3)

;; Return value of "and" is first falsey value or last truthy value
((and (= 1 1) +) 1 2 3)

;; Return value of "first" is the first element in a sequence
((first [+ 0]) 1 2 3)
```

However, these aren't valid function calls:

```clojure
;; Numbers aren't functions
(1 2 3 4)

;; Neither are strings
("test" 1 2 3)
```

If you run these in your REPL you'll get something like

```
ClassCastException java.lang.String cannot be cast to clojure.lang.IFn
user/eval728 (NO_SOURCE_FILE:1)
```

You're likely to see this error many times as you continue with
Clojure. "x cannot be cast to clojure.lang.IFn" just means that you're
trying something as a function when it's not.

Function flexibility doesn't end with the function expression!
Syntactically, functions can take any expressions as arguments &mdash;
including *other functions*.

Take the function `map` (not to be confused with the map data
structure), which can be understood by example:

```clojure
;; The "inc" function increments a number by 1
(inc 1)
; => 2

(inc 3.3)
; => 4.3

;; "map" creates a new list by applying a function to each member of
;; a collection.
(map inc [0 1 2 3])
; => (1 2 3 4)

;; Note that "map" doesn't return a vector even though we supplied a
;; vector as an argument. You'll learn why later. For now, just trust
;; that this is OK and expected.

;; The "dec" function is like "inc" except it subtracts 1
(dec 3)
; => 2

(map dec [0 1 2 3])
; => (-1 0 1 2)
```

Indeed, Clojure's ability to receive functions as arguments allows you
to build more powerful abstractions. Those unfamiliar with this kind
of programming think of functions as allowing you to generalize over
data instances. For example, the `+` function abstracts addition over
any specific numbers.

By contrast, Clojure (and all Lisps) allows you to create functions
which generalize over processes. `map` allows you to generalize the
process of transforming a collection by applying a function &mdash; any
function &mdash; over any collection.

### Clarifying Terminology

In the above discussion, we make use of terms which might not be
clear:

* expression
* evaluate
* function call
* apply a function

An **expression** is anything which Clojure can **evaluate** to a
value. I have to be honest here, though &mdash; I've been using the
term "expression" because it's more familiar, but in Lisp we call them
**forms**. For example, the following are all forms:

```clojure
2
[1 2 3]
(inc 1)
(map inc [1 3 (inc 5)])
((or + -) 1 2 3)
```

But these are not valid forms:

```clojure
;; No closing paren
(+ 1 2

;; No opening paren
+ 1 2)
```

We'll go into more detail about how Clojure evaluates forms later in
the chapter, but for now you can just think of it as a black box.
Clojure encounters a form and magically evaluates it! Yay, magic!

A **function call** is a form which is enclosed in parentheses where
the first element in the parentheses is a function. Example:

```clojure
;; Not a function call. If "if" isn't a function, then what is it?
;; Find out in section 3.2 below
(if true 1 2)
; => 1

;; A proper function call
(+ 1 2)
3
```

When Clojure encounters a function call, it continues the evaluation
process by first evaluating all sub-forms recursively. Once the
sub-forms are evaluated, the function is applied and it returns a
value. For example:

```clojure
;; Here's the function call. It kicks off the evaluation process
(+ (inc 12) (/ (- 20 2) 100))

;; All sub-forms are evaluated before applying the "+" function
(+ 13 (/ (- 20 2) 100)) ; evaluated "(inc 12)"
(+ 13 (/ 18 100)) ; evaluated (-20 2)
(+ 13 0.18) ; evaluated (/ 18 100)
13.18 ; final evaluation
```

And that's how you call a function! Now let's learn how to define
these crazy puppies!

### Defining Functions

Function definitions are comprised of five main parts:

* `defn`
* A name
* (Optional) a docstring
* Parameters
* The function body

Here's an example of a function definition and calling the function:

```clojure
(defn too-enthusiastic
  "Return a cheer that might be a bit too enthusiastic"
  [name]
  (str "OH. MY. GOD! " name " YOU ARE MOST DEFINITELY LIKE THE BEST "
  "MAN SLASH WOMAN EVER I LOVE YOU AND WE SHOULD RUN AWAY TO SOMEWHERE"))
  
(too-enthusiastic "Zelda")
; => "OH. MY. GOD! Zelda YOU ARE MOST DEFINITELY LIKE THE BEST MAN SLASH WOMAN EVER I LOVE YOU AND WE SHOULD RUN AWAY TO SOMEWHERE"
```

Let's dive deeper into the docstring, parameters, and function
body.

#### The Docstring

The docstring is really cool. You can view the docstring for a
function in the REPL with `(doc fn-name)`, e.g. `(doc map)`.

#### Parameters

Clojure functions can be defined with zero or more parameters:

```clojure
(defn no-params
  []
  "I take no parameters!")
  
(defn one-param
  [x]
  (str "I take one param: " x " It'd better be a string!")
  
(defn two-params
  [x y]
  (str "Two parameters! That's nothing! Pah! I will smoosh them "
  "together to spite you! " x y))
```

Functions can also be overloaded by arity. This is one way to provide
default values for arguments:

```clojure
(defn x-chop
  "Describe the kind of chop you're inflicting on someone"
  ([name chop-type]
     (str "I " chop-type " chop " name "! Take that!"))
  ([name]
     (x-chop name "karate")))
;; In this case, "karate" is the default argument for the chop-type
;; param

(x-chop "Kanye West")
; => "I karate chop Kanye West! Take that!"

(x-chop "Kanye East" "slap")
; => "I slap chop Kanye East! Take that!"
```

You can also make each arity do something completely unrelated:

```clojure
(defn weird-arity
  ([]
     "Destiny dressed you this morning my friend, and now Fear is
     trying to pull off your pants. If you give up, if you give in,
     you're gonna end up naked with Fear just standing there laughing
     at your dangling unmentionables! - the Tick")
  ([number]
     (inc number)))
```

But most likely, you don't want to do that.

Clojure also allows you to define variable-arity functions by
including a "rest-param", as in "put the rest of these arguments in a
list with the following name":

```clojure
(defn codger-communication
  [whippersnapper]
  (str "Get off my lawn, " whippersnapper "!!!"))

(defn codger
  [& whippersnappers] ;; the ampersand indicates the "rest-param"
  (map codger-communication whippersnappers))

(codger "Billy" "Henry" "Anne-Marie" "The Incredible Bulk")
; =>
; ("Get off my lawn, Billy!!!"
;  "Get off my lawn, Henry!!!"
;  "Get off my lawn, Anne-Marie!!!"
;  "Get off my lawn, The Incredible Bulk!!!")
```

As you can see, when you provide arguments to a variable-arity
functions, the arguments get treated as a list.

You can mix rest-params with normal params, but the rest-param has to
come last:

```clojure
(defn favorite-things
  [name & things]
  (str "Hi, " name ", here are my favorite things: "
       (clojure.string/join ", " things)))

(favorite-things "Doreen" "gum" "shoes" "berries")
; => "Hi, Doreen, here are my favorite things: gum, shoes, berries"
```

Finally, Clojure has a more sophisticated way of defining parameters
called "destructuring", which deserves its own subsection:

#### Destructuring

You don't have to use destructuring now. If you find it too
complicated, feel free to skip ahead and come back to this section
later. It will always be here for you!

The basic idea behind destructuring is that it lets you concisely bind
*symbols* to *values* within a *collection*. Let's look at a basic
example:

```clojure
;; Return the first element of a collection
(defn my-first
  [[first-thing]] ; Notice that first-thing is within a vector
  first-thing)

(my-first ["oven" "bike" "waraxe"])
; => "oven"

;; Here's how you would accomplish the same thing without destructuring:
(defn my-other-first
  [collection]
  (first collection))
(my-other-first ["nickel" "hair"])
; => "nickel"
```

As you can see, the `my-first` associates the symbol `first-thing`
with the first element of the vector that was passed in as an
argument. It does this by placing the symbol within a vector.

That vector is like a huge sign held up to Clojure which says, "Hey!
This function is going to receive a list or a vector as an argument.
Make my life easier by taking apart the argument's structure for me
and associating meaningful names with different parts of the
argument!"

When destructuring a vector or list, you can name as many elements as
you want and also use rest params:

```clojure
(defn chooser
  [[first-choice second-choice & unimportant-choices]]
  (println (str "Your first choice is: " first-choice))
  (println (str "Your second choice is: " second-choice))
  (println (str "We're ignoring the rest of your choices. "
                "Here they are in case you need to cry over them: "
                (clojure.string/join ", " unimportant-choices))))
(chooser ["Marmalade", "Handsome Jack", "Pigpen", "Aquaman"])
; => 
; Your first choice is: Marmalade
; Your second choice is: Handsome Jack
; We're ignoring the rest of your choices. Here they are in case \
; you need to cry over them: Pigpen, Aquaman
```

You can also destructure maps. In the same way that you tell Clojure
to destructure a vector or list by providing a vector as a parameter,
you destucture maps by providing a map as a parameter:

```clojure
(defn announce-treasure-location
  [{lat :lat lng :lng}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))
(announce-treasure-location {:lat 28.22 :lng 81.33})
; =>
; Treasure lat: 100
; Treasure lng: 50
```

Let's look more at this line:

```
[{lat :lat lng :lng}]
```

This is like telling Clojure, "Yo! Clojure! Do me a flava and
associate the symbol `lat` with the value corresponding to the key
`:lat`. Do the same thing with `lng` and `:lng`, ok?."

We often want to just take keywords and "break them out" of a map, so
there's a shorter syntax for that:

```clojure
;; Works the same as above.
(defn announce-treasure-location
  [{:keys [lat lng]}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))
```

If you want to have access to the original map argument, you can
indicate that:

```clojure
;; Works the same as above.
(defn announce-treasure-location
  [{:keys [lat lng] :as treasure-location}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng))
  
  ;; One would assume that this would put in new coordinates for your ship
  (steer-ship! treasure-location))
```

In general, you can think of destructuring as instructing Clojure how
to associate symbols with values in a list, map, or vector.

Now, on to the part of the function that actually does something: the
function body!

#### Function body

Your function body can contain any forms. Clojure automatically
returns the last form evaluated:

```clojure
(defn illustrative-function
  []
  (+ 1 304)
  30
  "joe")
(illustrative-function)
; => "joe"

(defn number-comment
  [x]
  (if (> x 6)
    "Oh my gosh! What a big number!"
    "That number's OK, I guess"))

(number-comment 5)
; => "That number's OK, I guess"

(number-comment 7)
; => "Oh my gosh! What a big number!"
```

#### All Functions are Created Equal

One final note: in Clojure, there are no privileged functions. `+` is
just a function, `-` is just a function, `inc` and `map` are just
functions. They're no better than your functions! So don't let them
give you any lip.

More importantly, this fact helps to demonstrate Clojure's underlying
simplicity. In a way, Clojure is very dumb. When you make a function
call, Clojure just says, "map? Sure, whatever! I'll just apply this
and move on." It doesn't care what the function is or where it came
from, it treats all functions the same. At its core, Clojure doesn't
give two burger flips about addition, multiplication, or mapping. It
just cares about applying functions.

As you program in with Clojure more, you'll see that this simplicity
is great. You don't have to worry about special rules or syntax for
working with functions. They all work the same!

### Anonymous Functions

In Clojure, your functions don't have to have names. In fact, you'll
find yourself using anonymous functions all the time.

There are two ways to create anonymous functions. The first is to use
the `fn` form:

```clojure
;; This looks a lot like defn, doesn't it?
(fn [param-list]
  function body)
  
;; Example
(map (fn [name]
       (str "Hi, " name))
     ["Darth Vader" "Mr. Magoo"])
; => ("Hi, Darth Vader" "Hi, Mr. Magoo")

;; Another example
((fn [x] (* x 3)) 8)
; => 24
```

You can treat `fn` nearly identically to the way you treat `defn`.
The parameter lists and function bodies work exactly the same. You can
use argument destructuring, rest-params, and so on.

You could even associate your anonymous function with a name, which
shouldn't come as a surprise:

```clojure
(def my-special-multiplier (fn [x] (* x 3)))
(my-special-multiplier 12)
; => 36
```

(If it does come as a surprise, then... Surprise!)

There's another, more compact way to create anonymous functions:

```clojure
;; Whoa this looks weird.
#(* % 3)

;; Example
(#(* % 3) 8)
; => 24

;; Another example
(map #(str "Hi, " %)
     ["Darth Vader" "Mr. Magoo"])
; => ("Hi, Darth Vader" "Hi, Mr. Magoo")
```

You can see that it's definitely more compact, but it's probably also
confusing. Let's break it down.

This kind of anonymous function looks a lot like a function call,
except that it's preceded by a pound sign, `#`:

```clojure
;; Function expression
(* 8 3)

;; Anonymous function
#(* % 3)
```

This similarity allows you to more quickly see what will happen when
this anonymous function gets applied. "Oh," you can say to yourself,
"this is going to multiply its argument by 3".

As you may have guessed by now, the percent sign, `%`, indicates the
argument passed to the function. If your anonymous function takes
multiple arguments, you can distinguish them like this: `%1`, `%2`,
`%3`, etc. `%` is equivalent to `%1`:

```clojure
(#(str %1 " and " %2) "corn bread" "butter beans")
; => "corn bread and butter beans"
```

You can also pass a rest param:

```clojure
(#(identity %&) 1 "blarg" :yip)
; => (1 "blarg" :yip)
```

The main difference between this form and `fn` is that this form can
easily become unreadable and is best used for very short functions.

### Returning Functions

Functions can return other functions. The returned functions are
closures, which means that they can access all the variables that were
in scope when the function was created.

Here's a standard example:

```clojure
;; inc-by is in scope, so the returned function has access to it even
;; when the returned function is used outside inc-maker
(defn inc-maker
  "Create a custom incrementor"
  [inc-by]
  #(+ % inc-by))

(def inc3 (inc-maker 3))

(inc3 7)
; => 10
```

Woohoo!

## Pulling It All Together

OK! Let's pull all this together and use our knowledge for a noble
purpose: smacking around hobbits!

In order to hit a hobbit, we'll first model its body parts. Each body
part will include its relative size to help us determine how likely it
is that that part will be hit.

In order to avoid repetition, this hobbit model will only include
entries for "left foot", "left ear", etc. Therefore, we'll need a
function to fully symmetrize the model.

Finally, we'll create a function which iterates over our body parts
and randomly chooses the one hit.

Fun!

### The Shire's Next Top Model

For our hobbit model, we'll eschew such characteristics as "joviality"
and "mischievousness" and focus only on the hobbit's tiny body. Here's
our hobbit model:

```clojure
(def asym-hobbit-body-parts [{:name "head" :size 3}
                             {:name "left-eye" :size 1}
                             {:name "left-ear" :size 1}
                             {:name "mouth" :size 1}
                             {:name "nose" :size 1}
                             {:name "neck" :size 2}
                             {:name "left-shoulder" :size 3}
                             {:name "left-upper-arm" :size 3}
                             {:name "chest" :size 10}
                             {:name "back" :size 10}
                             {:name "left-forearm" :size 3}
                             {:name "abdomen" :size 6}
                             {:name "left-kidney" :size 1}
                             {:name "left-hand" :size 2}
                             {:name "left-knee" :size 2}
                             {:name "left-thigh" :size 4}
                             {:name "left-lower-leg" :size 3}
                             {:name "left-achilles" :size 1}
                             {:name "left-foot" :size 2}])
```

This is a vector of maps. Each map has the name of the body part and
relative size of the body part. Look, I know that only anime
characters have eyes 1/3 the size of their head, but just go with it,
OK?

Conspicuously missing is the hobbit's right side. Let's fix that:

```clojure
(defn has-matching-part?
  [part]
  (re-find #"^left-" (:name part)))

(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})

(defn symmetrize-body-parts
  "Expects a seq of maps which have a :name and :size"
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts
            final-body-parts (conj final-body-parts part)]
        (if (has-matching-part? part)
          (recur remaining (conj final-body-parts (matching-part part)))
          (recur remaining final-body-parts))))))

(symmetrize-body-parts asym-hobbit-body-parts)
; => the following is the return value
[{:name "head", :size 3}
 {:name "left-eye", :size 1}
 {:name "right-eye", :size 1}
 {:name "left-ear", :size 1}
 {:name "right-ear", :size 1}
 {:name "mouth", :size 1}
 {:name "nose", :size 1}
 {:name "neck", :size 2}
 {:name "left-shoulder", :size 3}
 {:name "right-shoulder", :size 3}
 {:name "left-upper-arm", :size 3}
 {:name "right-upper-arm", :size 3}
 {:name "chest", :size 10}
 {:name "back", :size 10}
 {:name "left-forearm", :size 3}
 {:name "right-forearm", :size 3}
 {:name "abdomen", :size 6}
 {:name "left-kidney", :size 1}
 {:name "right-kidney", :size 1}
 {:name "left-hand", :size 2}
 {:name "right-hand", :size 2}
 {:name "left-knee", :size 2}
 {:name "right-knee", :size 2}
 {:name "left-thigh", :size 4}
 {:name "right-thigh", :size 4}
 {:name "left-lower-leg", :size 3}
 {:name "right-lower-leg", :size 3}
 {:name "left-achilles", :size 1}
 {:name "right-achilles", :size 1}
 {:name "left-foot", :size 2}
 {:name "right-foot", :size 2}]
```

Holy shipmates! This has a lot going on that we haven't discussed yet.
So let's discuss it!

### if

Clojure's `if` form is very simple

```clojure
(if boolean-form
  then-form
  optional-else-form)
```
  
You could describe this as, if the boolean form returns true then
evaluate and return `then-form`. Otherwise evaluate and return
`optional-else-form` if it's there.

If you have eagle eyes, you may have noticed something *different*
about `if`: the `then` and `else` forms *are never both evaluated*.
The rationale is clear. Imagine you had an `if` statement like this:

```clojure
(if good-mood
  (adjust-salary! 10000 all-employees)
  (adjust-salary! -20000 all-employees))
```

If we evaluated both forms, like we do with function calls, then those
poor employees would lose no matter what.

Contrast this with a function call:

```clojure
(oh-my-god-a-function (+ 1 3) (+ 3 4))
```

As we discussed in section 2.2 above, when you call a function, all of
the sub-forms are evaluated before the function is applied. `(+ 1 3)`
and `(+ 3 4)` are both evaluated before `oh-my-god-a-function` is
applied.

This means that `if` is not a function. So what is it?

In Lisp, there are a handful of **special forms** which do not follow
the default evaluation rules. `if` is a special form, as are `def` and
`defn`. We'll talk about special forms a bit in the next chapter. For
now, you can feel special every time you use `if`.

### let

In our symmetrizer above, we saw the following:

```clojure
(let [[part & remaining] remaining-asym-parts
      final-body-parts (conj final-body-parts part)]
  some-stuff)
```

All this does is bind the names on the left to the values on the
right. You can think of `let` as short for "let it be", which is also
a beautiful Beatles song (in case you didn't know (in which case, wtf
man)). For example, "Let `final-body-parts` be `(conj final-body-parts
part)`."

Here are some simpler examples:

```clojure
(let [x 3]
  x)
; => 3


(def dalmatian-list
  ["Pongo" "Missis" "Puppy 1" "Puppy 2"]) ; and 97 more...
(let [dalmatians (take 2 dalmatian-list)]
  dalmatians)
; => ("Pongo" "Missis")

;; Notice the rest-param - it works just like rest-params
;; in functions
(let [[pongo & dalmatians] dalmatian-list]
  [pongo dalmatians])
; => ["Pongo" ("Missis" "Puppy 1" "Puppy 2")]  
```

Notice that the value of a `let` form is the last form in its body
which gets evaluated. Also, `let` forms are special forms, just like
`if`. Special!

`let` forms can also follow all the destructuring rules which we
introduced in "Calling a Function" above.

One way to think about `let` forms is that they provide parameters and
their arguments side-by-side. `let` forms have two main uses:

* They provide clarity by allowing you to name things
* They allow you to evaluate an expression only once and re-use the
  result. This is especially important when you need to re-use the
  result of an expensive function call, like a network API call. It's
  also important when the expression has side effects.

Let's have another look at the `let` form in our symmetrizing function
so we can understand exactly what's going on:

```clojure
;; Associate "part" with the first element of "remaining-asym-parts"
;; Associate "remaining" with the rest of the elements in "remaining-asym-parts"
;; Associate "final-body-parts" with the result of (conj final-body-parts part)
(let [[part & remaining] remaining-asym-parts
      final-body-parts (conj final-body-parts part)]
  (if (has-matching-part? part)
    (recur remaining (conj final-body-parts (matching-part part)))
    (recur remaining final-body-parts)))
```


Notice that `part`, `remaining`, and `final-body-parts` each gets used
multiple times in the body of the `let`. If, instead of using the
names `part`, `remaining`, and `final-body-parts` we used the original
expressions, it would be a mess! For example:

```clojure
(let [[part & remaining] remaining-asym-parts
      final-body-parts (conj final-body-parts part)]
  (if (has-matching-part? (first remaining-asym-parts))
    (recur (rest remaining-asym-parts)
           (conj (conj final-body-parts (first remaining-asym-parts))
                 (matching-part (first remaining-asym-parts))))
    (recur (rest remaining-asym-parts)
           (conj final-body-parts (first remaining-asym-parts)))))
```

So, `let` is a handy way to introduce names for values.

### loop

`loop` provides an efficient way to do recursion in Clojure. Let's
look at a simple example:

```clojure
(loop [iteration 0]
  (println (str "Iteration " iteration))
  (if (> iteration 3)
    (println "Goodbye!")
    (recur (inc iteration))))
; =>
Iteration 0
Iteration 1
Iteration 2
Iteration 3
Iteration 4
Goodbye!
```

The first line, `loop [iteration 0]` begins the loop and introduces a
binding with an initial value. This is almost like calling an
anonymous function with a default value. On the first pass through the
loop, `iteration` has a value of 0.

Next, we print a super interesting little message.

Then, we check the value of `iteration` - if it's greater than 3 then
it's time to say goodbye. Otherwise, we `recur`. This is like calling
the anonymous function created by `loop`, but this time we pass it an
argument, `(inc iteration)`.

We could in fact accomplish the same thing just using functions:

```clojure
(defn recursive-printer
  ([]
     (recursive-printer 0))
  ([iteration]
     (println iteration)
     (if (> iteration 3)
       (println "Goodbye!")
       (recursive-printer (inc iteration)))))
(recursive-printer)
; =>
Iteration 0
Iteration 1
Iteration 2
Iteration 3
Iteration 4
Goodbye!
```

As you can see, this is a little more verbose. Also, `loop` has much
better performance.

### Regular Expressions

I won't go into how regular expressions work, but here's their literal
notation:

```clojure
;; pound, open quote, close quote
#"regular-expression"
```

And here's how regexes are used in our symmetrizer:

```clojure
;; re-find returns true or false based on whether the
;; the part's name starts with the string "left-"
(defn has-matching-part?
  [part]
  (re-find #"^left-" (:name part)))
(has-matching-part? "left-eye")
; => true
(has-matching-part? "neckbeard")
; => false

;; Use a regex tp replace "left-" with "right-"
(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})
(matching-part {:name "left-eye" :size 1})
; => {:name "right-eye" :size 1}]
```

### conj

`conj` adds elements to a sequence:

```clojure
(conj [] 1)
; => [1]

;; Conj adds elements to the *end* of a vector
(conj [1] 2 3)
; => [1 2 3]

;; But it adds elements to *beginning* of a list
(conj '(1) 2 3)
; => (3 2 1)
```

### Symmetrizer

Now let's analyze the symmetrizer fully. Note points are floating in
the ocean, like `~~~1~~~`:

```clojure
(def asym-hobbit-body-parts [{:name "head" :size 3}
                             {:name "left-eye" :size 1}
                             {:name "left-ear" :size 1}
                             {:name "mouth" :size 1}
                             {:name "nose" :size 1}
                             {:name "neck" :size 2}
                             {:name "left-shoulder" :size 3}
                             {:name "left-upper-arm" :size 3}
                             {:name "chest" :size 10}
                             {:name "back" :size 10}
                             {:name "left-forearm" :size 3}
                             {:name "abdomen" :size 6}
                             {:name "left-kidney" :size 1}
                             {:name "left-hand" :size 2}
                             {:name "left-knee" :size 2}
                             {:name "left-thigh" :size 4}
                             {:name "left-lower-leg" :size 3}
                             {:name "left-achilles" :size 1}
                             {:name "left-foot" :size 2}])

(defn has-matching-part?
  [part]
  (re-find #"^left-" (:name part)))

(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})

; ~~~1~~~
(defn symmetrize-body-parts
  "Expects a seq of maps which have a :name and :size"
  [asym-body-parts] ; 
  (loop [remaining-asym-parts asym-body-parts ; ~~~2~~~
         final-body-parts []]
    (if (empty? remaining-asym-parts) ; ~~~3~~~
      final-body-parts
      (let [[part & remaining] remaining-asym-parts ; ~~~4~~~
            final-body-parts (conj final-body-parts part)]
        (if (has-matching-part? part) ; ~~~5~~~
          (recur remaining (conj final-body-parts (matching-part part))) ; ~~~6~~~
          (recur remaining final-body-parts))))))
```

1. This function employs a general strategy which is common in functional
   programming. Given a sequence (in this case, a vector of body parts
   and their sizes), continuously split the sequence into a "head" and
   a "tail". Process the head, add it to some result, and then
   use recursion to continue the process with the tail.
2. Begin looping over the body parts. The "tail" of the sequence will be
   bound to `remaining-asym-parts`. Initially, it's bound to the full
   sequence passed to the function, `asym-body-parts`. Create a result
   sequence, `final-body-parts`; its initial value is an empty vector.
3. If `remaining-asym-parts` is empty, that means we've processed the
   entire sequence and can return the result, `final-body-parts`.
4. Otherwise, split the list into a head, `part`, and tail,
   `remaining`. Also, add `part` to `final-body-parts` and re-bind the
   result to the name `final-body-parts`. This might seem weird, and
   it's worthwhile to figure out why it works.
5. Our growing sequence of `final-body-parts` already includes the
   body part we're currently examining, `part`. Here, we decide
   whether we need to add the matching body part to the list.
6. If so, then add the `matching-part` to `final-body-parts` and
   recur. Otherwise, just recur.

If you're new to this kind of programming, this might take some time
to puzzle out. Stick with it! Once you understand what's happening,
you'll feel like a million bucks!

### Shorter Symmetrizer with Reduce

The pattern of "process each element in a sequence and build a result"
is so common that there's a function for it: `reduce`.

Here's a simple example:

```clojure
;; sum with reduce
(reduce + [1 2 3 4])
; => 10
```

Reduce could be implemented like this:

```clojure
(defn my-reduce
  ([f initial coll]
     (loop [result initial
            remaining coll]
       (let [[current & rest] remaining]
         (if (empty? remaining)
           result
           (recur (f result current) rest)))))
  ([f [head & tail]]
     (my-reduce f (f head (first tail)) (rest tail))))
```

We could re-implement symmetrize as follows:

```clojure
(defn better-symmetrize-body-parts
  "Expects a seq of maps which have a :name and :size"
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
            (let [final-body-parts (conj final-body-parts part)]
              (if (has-matching-part? part)
                (conj final-body-parts (matching-part part))
                final-body-parts)))
          []
          asym-body-parts))
```

Groovy!

### Hobbit Violence

My word, this is truly Clojure for the Brave and True!

Now, let's create a function that will determine which part of the
hobbit gets hit:

```clojure
(defn hit
  [asym-body-parts]
  (let [sym-parts (better-symmetrize-body-parts asym-body-parts)
        body-part-size-sum (reduce + 0 (map :size sym-parts))
        target (inc (rand body-part-size-sum))]
    (loop [[part & rest] sym-parts
           accumulated-size (:size part)]
      (if (> accumulated-size target)
        part
        (recur rest (+ accumulated-size (:size part)))))))

(hit asym-hobbit-body-parts)
; => {:name "right-upper-arm", :size 3}

(hit asym-hobbit-body-parts)
; => {:name "chest", :size 10}

(hit asym-hobbit-body-parts)
; => {:name "left-eye", :size 1}
```

Oh my god, that poor hobbit! You monster!

## What Now?

By this point I *highly* recommend actually writing some code to
solidify your Clojure knowledge if you haven't started already. One
great place to start would be to refactor out the `loop` in the `hit`
function. Or, write out some project Euler challenges. Write
*anything*.

In the next update, I'll include some project ideas and guidance. In
the mean time, you can also check out
[4Clojure](http://www.4clojure.com/problems), an online set of Clojure
problems designed to test your knowledge.
