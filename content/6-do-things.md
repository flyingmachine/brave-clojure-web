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
data structures. Then, we'll dive deep into functions. Finally, we'll
bring the two together to thoroughly explore the ways in which we can
manipulate data with functions.

One final note before we start: in Clojure, the core data structures
aren't mutable. For example, we'll be looking at the vector data
structure, which looks a lot like arrays in other languages:

```clojure
(def failed-protagonist-names
  ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"])
```

Most other languages would allow you to manipulate this array. In
Ruby, for example:

```ruby
failed_protagonist_names = ["Larry Potter", "Doreen the Explorer", "The Incredible Bulk"]
failed_protagonist_names[0] = "Gary Potter"
failed_protagonist_names # => ["Gary Potter", "Doreen the Explorer", "The Incredible Bulk"]
```

In Clojure, there is no equivalent. We'll cover the implications of
immutability in more detail later on, but for now keep in mind that
immutability distinguishes these data structures from the ones you're
used to in other programming languages.

## Just Enough Data Structures

This section will briefly introduce you to core Clojure data
structures. If you're curious about the functions used,
[ClojureDocs](http://clojuredocs.org/) is a great reference for
finding out more.

### Numbers

We're only going to work with integers and floats for awhile, though
[Clojure's treatment of numbers](http://clojure.org/data_structures#Data
Structures-Numbers) is more sophisticated than that. Examples:

```
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
(println (str "\"Uggllglglglglglglglll\ - " name))
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

### Vectors

A vector is similar to an array in that it's a 0-indexed collection:

```clojure
;; Here's a vector
[3 2 1]

;; Here we're returning elements of vectors
(get [3 2 1] 0)
; => 3

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

### Symbols and Naming

As [clojure.org](http://clojure.org/data_structures#Data
Structures-Symbols) states, symbols are identifiers that are normally
used to refer to something. Let's associate a value with a symbol:

```clojure
(def failed-protagonist-names
  ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"])
```

In this case, `def` associates the value
`["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"]` with
the symbol `failed-protagonist-names`.

You might be thinking, "So what? Every other programming language
lets me associate a name with a value. Big whoop!" Lisps, however,
allow you to manipulate symbols as data, something we'll see a lot of 
when we start working with macros. For now, though, it's OK to think
"Big whoop!" and not be very impressed.

Thus concludes our Clojure data structures primer. Now it's time to
dig in to functions and see how these data structures and be used!

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

```
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

Function flexibility doesn't end with the function expression,
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

A **function call** is a form which is enclosed in parentheses. When
Clojure encounters a function call, it kicks off the evaluation
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

Clojure functions can be defined with one or more parameters:

```clojure
(defn no-params
  []
  "I take no parameters!"
  
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
     (str "I " chop-type " " name "! Take that!"))
  ([name]
     (x-chop name "karate")))
;; In this case, "karate" is the default argument for the chop-type param
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

