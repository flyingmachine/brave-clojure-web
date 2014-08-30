---
title: "Do Things: a Clojure Language Crash Course"
link_title: "Do Things: a Clojure Language Crash Course"
kind: documentation
---

# Do Things: a Clojure Language Crash Course

It's time to to learn how to actually *do things* with Clojure! Hot
damn!

While you've undoubtedly heard of Clojure's awesome concurrency
support and other stupendous features, Clojure's most salient
characteristic is that it is a Lisp. In this chapter, you're going to
explore the elements which comprise this Lisp core: syntax, functions,
and data. This will provide you with a solid foundation for
representing and solving problems in Clojure.

This groundwork will also allow you to write some super important
code. In the last section, you'll tie everything together by creating
a model of a hobbit and writing a function to hit it in a random spot.
Super! Important!

As you go through the chapter, I recommend that you type out the
examples in a REPL and run them. Programming in a new language is a
skill, and, just like yodeling or synchronized swimming, you have to
practice it to learn it. By the way, "Synchronized Swimming for
Yodelers for the Brave and True" is due to be published in August of
20never. Check it out!

## Syntax

Clojure's syntax is simple. Like all Lisps, it employs a uniform
structure, a handful of special operators, and a constant supply of
parentheses delivered from the parenthesis mines hidden beneath the
Massachusetts Institute of Technology, where Lisp was born.

### Forms

All Clojure code is written in a uniform structure. Clojure understands:

1. Literal representations of data structures like numbers, strings,
   maps, and vectors
2. Operations

We use the term **form** to refer to structurally valid code. These
literal representations are all valid forms:

```clojure
1
"a string"
["a" "vector" "of" "strings"]
```

Your code will rarely contain free-floating literals, of course, since
they don't actually *do* anything on their own. Instead, you'll use
literals in operations. Operations are how you *do things*. All
operations take the form, "opening parthensis, operator, operands,
closing parenthesis":

```clojure
(operator operand1 operand2 ... operandn)
```

Notice that there are no commas. Clojure uses whitespace to separate
operands and it treats commas as whitespace. Here are some example
operations:

```clojure
(+ 1 2 3)
; => 6

(str "It was the panda " "in the library " "with a dust buster")
; => "It was the panda in the library with a dust buster"
```

To recap, Clojure consists of *forms*. Forms have a uniform
structure. They consist of literals and operations. Operations consist
of forms enclosed within parentheses.

For good measure, here's something that is not a form because it
doesn't have a closing parenthesis:

```clojure
(+
```

Clojure's structural uniformity is probably different from what you're
used to. In other languages, different operations might have different
structures depending on the operator and the operands. For example,
JavaScript employs a smorgasbord of infix notation, dot operators, and
parentheses:

```javascript
1 + 2 + 3
"It was the panda ".concat("in the library ", "with a dust buster")
```

Clojure's structure is very simple and consistent by comparison. No
matter what operator you're using or what kind of data you're
operating on, the structure is the same.

One final note: I'll also use the term **expression** to refer to
Clojure forms. Don't get too hung up on the terminology, though.

### Control Flow

Here are some basic control flow operators. Throughout the book you'll
encounter more.

#### if

The general structure of `if` is:

```clojure
(if boolean-form
  then-form
  optional-else-form)
```

Here's an example:

```clojure
(if true
  "abra cadabra"
  "hocus pocus")
; => "abra cadabra"
```

Notice that each branch of the if can only have one form. This is
different from most languages. For example, in Ruby you can write:

```ruby
if true
  doer.do_thing(1)
  doer.do_thing(2)
else
  other_doer.do_thing(1)
  other_doer.do_thing(2)
end
```

To get around this apparent limitation, we have the `do` operator:

#### do

`do` lets you "wrap up" multiple forms. Try the following in your REPL:

```clojure
(if true
  (do (println "Success!")
      "abra cadabra")
  (do (println "Failure :(")
      "hocus pocus"))
; => Success!
; => "abra cadabra"
```

In this case, `Success!` is printed in the REPL and `"abra cadabra"`
is returned as the value of the entire `if` expression.

#### when

The `when` operator is like a combination of `if` and `do`, but with
no else form. Here's an example:

```clojure
(when true
  (println "Success!")
  "abra cadabra")
; => Success!
; => "abra cadabra"
```

Use `when` when you want to do multiple things when some condition is
true, and you don't want to do anything when the condition is false.

That covers the essential control flow operators!

### Naming Things with def

One final thing before we move on to data structures: you use `def` to
*bind* a *name* to a *value* in Clojure:

```clojure
(def failed-protagonist-names
  ["Larry Potter"
   "Doreen the Explorer"
   "The Incredible Bulk"])
```

In this case, you're binding the name `failed-protagonist-names` to a
vector containing three strings. Notice that I'm using the term
"bind", whereas in other langauges you'd say that you're *assigning* a
value to a *variable*. For example, in Ruby you might perform multiple
assignments to a variable to "build up" its value:

```ruby
severity = :mild
error_message = "OH GOD! IT'S A DISASTER! WE'RE "
if severity == :mild
  error_message = error_message + "MILDLY INCONVENIENCED!"
else
  error_message = error_message + "DOOOOOOOMED!"
end
```

The Clojure equivalent would be:

```clojure
(def severity :mild)
(def error-message "OH GOD! IT'S A DISASTER! WE'RE ")
(if (= severity :mild)
  (def error-message (str error-message "MILDLY INCONVENIENCED!"))
  (def error-message (str error-message "DOOOOOOOMED!")))
```

However, this is really bad Clojure. For now, you should treat `def`
as if it's defining constants. But fear not! Over the next few
chapters you'll learn how to work with this apparent limitation by
coding in the functional style.

## Data Structures

Clojure comes with a handful of data structures which you'll find
yourself using the majority of the time. If you're coming from an
object-oriented background, you'll be surprised at how much you can do
with the "basic" types presented here.

All of Clojure's data structures are immutable, meaning you can't
change them in place. There's no Clojure equivalent for the following
Ruby:

```ruby
failed_protagonist_names = [
  "Larry Potter",
  "Doreen the Explorer",
  "The Incredible Bulk"
]
failed_protagonist_names[0] = "Gary Potter"
failed_protagonist_names
# => [
#   "Gary Potter",
#   "Doreen the Explorer",
#   "The Incredible Bulk"
# ]
```

You'll learn more about why Clojure was implemented this way, but for
now it's fun to just learn how to do things without all that
philosophizing. Without further ado:

### nil, true, false, Truthiness, Equality

Clojure has `true` and `false` values. `nil` is used to indicate "no
value" in Clojure. You can check if a value is `nil` with the cleverly
named `nil?` function:

```clojure
(nil? 1)
; => false

(nil? nil)
; => true
```

Both `nil` and `false` are used to represent logical falsiness, while
all other values are logically truthy. `=` is the equality operator:

```clojure
(= 1 1)
; => true

(= nil nil)
; => true

(= 1 2)
; => false
```

Some other languages require you to use different operators when
comparing values of different types. For example, you might have to
use some kind of special "string equality" operator specially made
just for strings. You don't need anything weird or tedious like
that to test for equality when using Clojure's built-in data
structures.

### Numbers

Clojure has pretty sophisticated numerical support. I'm not going to
spend much time dwelling on the boring technical details (like
coercion and contagion), because that will get in the way of *doing
things*. If you're interested in said boring details, check out
[http://clojure.org/data_structures#Data Structures-Numbers](http://clojure.org/data_structures#Data
Structures-Numbers). Suffice to say that Clojure will merrily handle
pretty much anything you throw at it.

In the mean time, we'll be working with integers and floats. We'll
also be working with ratios, which Clojure can represent directly.
Here's an integer, a float, and a ratio:

```clojure
93
1.2
1/5
```

### Strings

Here are some string examples:

```clojure
"Lord Voldemort"
"\"He who must not be named\""
"\"Great cow of Moscow!\" - Hermes Conrad"
```

Notice that Clojure only allows double quotes to delineate strings.
`'Lord Voldemort'`, for example, is not a valid string. Also notice
that Clojure doesn't have string interpolation. It only allows
concatenation via the `str` function:

```clojure
(def name "Chewbacca")
(str "\"Uggllglglglglglglglll\" - " name)
; => "Uggllglglglglglglglll" - Chewbacca
```

### Maps

Maps are similar to dictionaries or hashes in other languages. They're
a way of associating some value with some other value. Here are
example map literals:

```clojure
;; An empty map
{}

;; ":a", ":b", ":c" are keywords and we'll cover them in the next section
{:a 1
 :b "boring example"
 :c []}

;; Associate "string-key" with the "plus" function
{"string-key" +}

;; Maps can be nested
{:name {:first "John" :middle "Jacob" :last "Jingleheimerschmidt"}}
```

Notice that map values can be of any type. String, number, map,
vector, even function! Clojure don't care!

You can look up values in maps with the `get` function:

```clojure
(get {:a 0 :b 1} :b)
; => 1

(get {:a 0 :b {:c "ho hum"}} :b)
; => {:c "ho hum"}
```

`get` will return `nil` if it doesn't find your key, but you can give
it a default value to return:

```clojure
(get {:a 0 :b 1} :c)
; => nil

(get {:a 0 :b 1} :c "UNICORNS")
; => "UNICORNS"
```

The `get-in` function lets you look up values in nested maps:

```clojure
(get-in {:a 0 :b {:c "ho hum"}} [:b :c])
; => "ho hum"
```

`[:b :c]` is a vector, which you'll read about in a minute.

Another way to look up a value in a map is to treat the map like a
function, with the key as its argument:

```clojure
({:name "The Human Coffee Pot"} :name)
; => "The Human Coffee Pot"
```

Real Clojurists hardly ever do this, though. However, Real Clojurists
*do* use keywords to look up values in maps:

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

Keywords can be used as functions which look up the corresponding
value in a data structure. For example:

```clojure
;; Look up :a in map
(:a {:a 1 :b 2 :c 3})
; => 1

;; This is equivalent to:
(get {:a 1 :b 2 :c 3} :a)
; => 1

;; Provide a default value, just like get:
(:d {:a 1 :b 2 :c 3} "FAERIES")
; => "FAERIES
```

I think this is super cool and Real Clojurists do it all the time. You
should do it, too!

Besides using map literals, you can use the `hash-map` function to
create a map:

```clojure
(hash-map :a 1 :b 2)
; => {:a 1 :b 2}
```

Clojure also lets you create sorted maps, but I won't be covering
that.

### Vectors

A vector is similar to an array in that it's a 0-indexed collection:

```clojure
;; Here's a vector literal
[3 2 1]

;; Here we're returning an element of a vector
(get [3 2 1] 0)
; => 3

;; Another example of getting by index. Notice as well that vector
;; elements can be of any type and you can mix types.
(get ["a" {:name "Pugsley Winterbottom"} "c"] 1)
; => {:name "Pugsley Winterbottom"}
```

Notice that we're using the same `get` function as we use when looking
up values in maps. The next chapter explains why we do this.

You can create vectors with the `vector` function:

```clojure
(vector "creepy" "full" "moon")
; => ["creepy" "full" "moon"]
```

Elements get added to the *end* of a vector:

```clojure
(conj [1 2 3] 4)
; => [1 2 3 4]
```

### Lists

Lists are similar to vectors in that they're linear collections of
values. There are some differences, though. You can't retrieve list
elements with `get`:

```clojure
;; Here's a list - note the preceding single quote
'(1 2 3 4)
; => (1 2 3 4)
;; Notice that the REPL prints the list without a quote. This is OK,
;; and it'll be explained later.


;; Doesn't work for lists
(get '(100 200 300 400) 0)

;; This works but has different performance characteristics which we
;; don't care about right now.
(nth '(100 200 300 400) 3)
; => 400
```

You can create lists with the `list` function:

```clojure
(list 1 2 3 4)
; => (1 2 3 4)
```

Elements get added to the *beginning* of a list:

```clojure
(conj '(1 2 3) 4)
; => (4 1 2 3)
```

When should you use a list and when should you use a vector? For now,
you're probably best off just using vectors. As you learn more, you'll
get a good feel for when to use which.

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

(:a #{:a :b})
; => :a

(get #{:a :b} "hannah montanna")
; => nil
```

You can create sets from existing vectors and lists by using the `set`
function. One unobvious use for this is to check whether an element
exists in a collection:

```clojure
(set [3 3 3 4 4])
; => #{3 4}

;; 3 exists in vector
(get (set [3 3 3 4 4]) 3)
; => 3

;; but 5 doesn't
(get (set [3 3 3 4 4]) 5)
; => nil
```

Just as you can create hash maps and sorted maps, you can create hash
sets and sorted sets:

```clojure
(hash-set 1 1 3 1 2)
; => #{1 2 3}

(sorted-set :b :a :c)
; => #{:a :b :c}
```

Clojure also lets you define how a set is sorted using the
`sorted-set-by` function, but this book doesn't cover that.

### Symbols and Naming

Symbols are identifiers that are normally used to refer to something.
Let's look at a `def` example:

```clojure
(def failed-movie-titles ["Gone With the Moving Air" "Swellfellas"])
```

In this case, `def` associates the value
`["Gone With the Moving Air" "Swellfellas"]` with the symbol
`failed-movie-titles`.

You might be thinking, "So what? Every other programming language lets
me associate a name with a value. Big whoop!" Lisps, however, allow
you to manipulate symbols as data, something we'll see a lot of when
we start working with macros. Functions can return symbols and take
them as arguments:

```clojure
;; Identity returns its argument
(identity 'test)
; => test
```

For now, though, it's OK to think "Big whoop!" and not be very
impressed.

### Quoting

You may have noticed the single quote, `'`, in the examples above.
This is called "quoting". You'll learn about this in detail in the
chapter "Clojure Alchemy: Reading, Evaluation, and Macros". Here's the
quick explanation for now.


Giving Clojure a symbol returns the "object" it refers to:

```clojure
failed-protagonist-names
; => ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"]

(first failed-protagonist-names)
; => "Larry Potter"
```

Quoting a symbol tells Clojure to use the symbol itself as a data
structure, not the object the symbol refers to:

```clojure
'failed-protagonist-names
; => failed-protagonist-names

(eval 'failed-protagonist-names)
; => ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"]

(first 'failed-protagonist-names)
; => Throws exception!

(first ['failed-protagonist-names 'failed-antagonist-names])
; => failed-protagonist-names
```

You can also quote collections like lists, maps, and vectors. All
symbols within the collection will be unevaluated:

```clojure
'(failed-protagonist-names 0 1)
; => (failed-protagonist-names 0 1)

(first '(failed-protagonist-names 0 1))
; => failed-protagonist-names

(second '(failed-protagonist-names 0 1))
; => 0
```

### Simplicity

You may have noticed that this treatment of data structures doesn't
include a description of how to create new types or classes. This is
because Clojure's emphasis on simplicity encourages you to reach for
the built-in, "basic" data structures first.

If you come from an object-oriented background, you might think that
this approach is weird and backwards. What you'll find, though, is
that your data does not have to be tightly bundled with a class for it
to be useful and intelligible. Here's an epigram loved by Clojurists
which hints at the Clojure philosophy:

    It is better to have 100 functions operate on one data structure
    than 10 functions on 10 data structures.
    
    -- Alan Perlis

You'll learn more about this aspect of Clojure's philosophy in the
coming chapters. For now, though, keep an eye out for the ways that
you gain code re-use by sticking to basic data structures.

Thus concludes our Clojure data structures primer. Now it's time to
dig in to functions and see how these data structures can be used!

## Functions

One of the reasons people go nuts over Lisps is that they allow you to
build programs which behave in complex ways, yet the primary building
block &mdash; the function &mdash; is so simple. This section will
initiate you in the beauty and elegance of Lisp functions by
explaining:

* Calling functions
* How functions differ from macros and special forms
* Defining functions
* Anonymous functions
* Returning functions

### Calling Functions

By now you've seen many examples of function calls:

```clojure
(+ 1 2 3 4)
(* 1 2 3 4)
(first [1 2 3 4])
```

I've already gone over how all Clojure expressions have the same
syntax: opening parenthesis, operator, operands, closing parenthesis.
"Function call" is just another term for an expression where the
operator is a *function expression*. A *function expression* is just
an expression which returns a function.

It might not be obvious, but this lets you write some pretty
interesting code. Here's a function expression which returns the `+`
(addition) function:

```clojure
;; Return value of "or" is first truthy value, and + is truthy
(or + -)
```

You can use that expression as the operator in another expression:

```clojure
((or + -) 1 2 3)
; => 6
```

Here are a couple more valid function calls which return 6:

```clojure
;; Return value of "and" is first falsey value or last truthy value.
;; + is the last truthy value
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

Take the `map` function (not to be confused with the map data
structure). `map` creates a new list by applying a function to each
member of a collection:

```clojure
;; The "inc" function increments a number by 1
(inc 1.1)
; => 2.1

(map inc [0 1 2 3])
; => (1 2 3 4)
```

(Note that `map` doesn't return a vector even though we supplied a
vector as an argument. You'll learn why later. For now, just trust
that this is OK and expected.)

Indeed, Clojure's ability to receive functions as arguments allows you
to build more powerful abstractions. Those unfamiliar with this kind
of programming think of functions as allowing you to generalize
operations over data instances. For example, the `+` function
abstracts addition over any specific numbers.

By contrast, Clojure (and all Lisps) allows you to create functions
which generalize over processes. `map` allows you to generalize the
process of transforming a collection by applying a function &mdash; any
function &mdash; over any collection.

The last thing that you need know about function calls is that Clojure
evaluates all function arguments recursively before passing them to
the function. Here's how Clojure would evaluate a function call whose
arguments are also function calls:

```clojure
;; Here's the function call. It kicks off the evaluation process
(+ (inc 199) (/ 100 (- 7 2)))

;; All sub-forms are evaluated before applying the "+" function
(+ 200 (/ 100 (- 7 2))) ; evaluated "(inc 199)"
(+ 200 (/ 100 5)) ; evaluated (- 7 2)
(+ 200 20) ; evaluated (/ 100 5)
220 ; final evaluation
```

### Function Calls, Macro Calls, and Special Forms

In the last section, you learned that function calls are expressions
which have a function expression as the operator. There are two other
kinds of expressions: **macro calls** and **special forms**. You've
already seen a couple special forms:

```clojure
(def failed-movie-titles ["Gone With the Moving Air" "Swellfellas"])
(if (= severity :mild)
  (def error-message (str error-message "MILDLY INCONVENIENCED!"))
  (def error-message (str error-message "DOOOOOOOMED!")))
```

You'll learn everything there is to know about macro calls and special
forms in the chapter "Clojure Alchemy: Reading, Evaluation, and
Macros". For now, though, the main feature which makes special forms
"special" is that *they don't always evaluate all of their operands*,
unlike function calls.

Take `if`, for example. Its general structure is:

```clojure
(if boolean-form
  then-form
  optional-else-form)
```

Now imagine you had an `if` statement like this:

```clojure
(if good-mood
  (tweet walking-on-sunshine-lyrics)
  (tweet mopey-country-song-lyrics))
```

If Clojure evaluated both `tweet` function calls, then your followers
would end up very confused.

Another feature which differentiates special forms is that you can't use
them as arguments to functions.

In general, special forms implement core Clojure functionality that
just can't be implemented with functions. There are only a handful of
Clojure special forms, and it's pretty amazing that such a rich
language is implemented with such a small set of building blocks.

Macros are similar to special forms in that they evaluate their
operands differently from function calls and they also can't be passed
as arguments to functions. But this detour has taken long enough; it's
time to learn how to define functions!

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
function in the REPL with `(doc fn-name)`, e.g. `(doc map)`. The
docstring is also utilized if you use a tool to generate documentation
for your code. In the above example, `"Return a cheer that might be a
bit too enthusiastic"` is the docstring.

#### Parameters

Clojure functions can be defined with zero or more parameters:

```clojure
(defn no-params
  []
  "I take no parameters!")

(defn one-param
  [x]
  (str "I take one param: " x " It'd better be a string!"))

(defn two-params
  [x y]
  (str "Two parameters! That's nothing! Pah! I will smoosh them "
  "together to spite you! " x y))
```

Functions can also be overloaded by arity. This means that a different
function body will run depending on the number of arguments passed to
a function.

Here's the general form of a multiple-arity function definition.
Notice that each arity definition is enclosed in parentheses and has
an argument list:

```clojure
(defn multi-arity
  ;; 3-arity arguments and body
  ([first-arg second-arg third-arg]
     (do-things first-arg second-arg third-arg))
  ;; 2-arity arguments and body
  ([first-arg second-arg]
     (do-things first-arg second-arg))
  ;; 1-arity arguments and body
  ([first-arg]
     (do-things first-arg)))
```

Overloading by arity is one way to provide default values for
arguments. In this case, `"karate"` is the default argument for the
`chop-type` param: 

```clojure
(defn x-chop
  "Describe the kind of chop you're inflicting on someone"
  ([name chop-type]
     (str "I " chop-type " chop " name "! Take that!"))
  ([name]
     (x-chop name "karate")))
```

If you call `x-chop` with two arguments, then the function works just
as it would if it weren't a multi-arity function:

```clojure
(x-chop "Kanye West" "slap")
; => "I slap chop Kanye West! Take that!"
```

If you call `x-chop` with only one argument, though, then `x-chop`
will actually call itself with the second argument `"karate"`
supplied:

```clojure
(x-chop "Kanye East")
; => "I karate chop Kanye East! Take that!"
```

It might seem unusual to define a function in terms of itself like
this. If so, great! You're learning a new way to do things!

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

(codger "Billy" "Anne-Marie" "The Incredible Bulk")
; =>
; ("Get off my lawn, Billy!!!"
;  "Get off my lawn, Anne-Marie!!!"
;  "Get off my lawn, The Incredible Bulk!!!")
```

As you can see, when you provide arguments to variable-arity
functions, the arguments get treated as a list.

You can mix rest-params with normal params, but the rest-param has to
come last:

```clojure
(defn favorite-things
  [name & things]
  (str "Hi, " name ", here are my favorite things: "
       (clojure.string/join ", " things)))

(favorite-things "Doreen" "gum" "shoes" "kara-te")
; => "Hi, Doreen, here are my favorite things: gum, shoes, kara-te"
```

Finally, Clojure has a more sophisticated way of defining parameters
called "destructuring", which deserves its own subsection:

#### Destructuring

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
```

Here's how you would accomplish the same thing without destructuring:

```clojure
(defn my-other-first
  [collection]
  (first collection))
(my-other-first ["nickel" "hair"])
; => "nickel"
```

As you can see, the `my-first` associates the symbol `first-thing`
with the first element of the vector that was passed in as an
argument. You tell `my-first` to do this by placing the symbol
`first-thing` within a vector.

That vector is like a huge sign held up to Clojure which says, "Hey!
This function is going to receive a list or a vector or a set as an
argument. Make my life easier by taking apart the argument's structure
for me and associating meaningful names with different parts of the
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
; Treasure lat: 28.22
; Treasure lng: 81.33
```

Let's look more at this line:

```clojure
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

You can retain access to the original map argument by using the `:as`
keyword. In the example below, the original map is accessed with
`treasure-location`:

```clojure
;; Works the same as above.
(defn receive-treasure-location
  [{:keys [lat lng] :as treasure-location}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng))

  ;; One would assume that this would put in new coordinates for your ship
  (steer-ship! treasure-location))
```

In general, you can think of destructuring as instructing Clojure how
to associate symbols with values in a list, map, set, or vector.

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
call, Clojure just says, "`map`? Sure, whatever! I'll just apply this
and move on." It doesn't care what the function is or where it came
from, it treats all functions the same. At its core, Clojure doesn't
give two burger flips about addition, multiplication, or mapping. It
just cares about applying functions.

As you program with Clojure more, you'll see that this simplicity
is great. You don't have to worry about special rules or syntax for
working with functions. They all work the same!

### Anonymous Functions

In Clojure, your functions don't have to have names. In fact, you'll
find yourself using anonymous functions all the time. How mysterious!

There are two ways to create anonymous functions. The first is to use
the `fn` form:

```clojure
;; This looks a lot like defn, doesn't it?
(fn [param-list]
  function body)

;; Example
(map (fn [name] (str "Hi, " name))
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

(If it does come as a surprise, then&#x2026; Surprise!)

There's another, more compact way to create anonymous functions:

```clojure
;; Whoa this looks weird.
#(* % 3)

;; Apply this weird looking thing
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
;; Function call
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
easily become unreadable and is best used for short functions.

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

Conspicuously missing is the hobbit's right side. Let's fix that.
The code below is the most complex code we've looked at so far. It
introduces some ideas we haven't covered yet. Don't worry though,
because we're going to examine it in great detail:

```clojure
(defn needs-matching-part?
  [part]
  (re-find #"^left-" (:name part)))

(defn make-matching-part
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
        (if (needs-matching-part? part)
          (recur remaining (conj final-body-parts (make-matching-part part)))
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

Let's break this down!

### let

In our symmetrizer above, we saw the following:

```clojure
(let [[part & remaining] remaining-asym-parts
      final-body-parts (conj final-body-parts part)]
  some-stuff)
```

All this does is bind the names on the left to the values on the
right. You can think of `let` as short for "let it be", which is also
a beautiful Beatles song (in case you didn't know (in which case,
wtf?)). For example, "Let `final-body-parts` be `(conj
final-body-parts part)`."

Here are some simpler examples:

```clojure
(let [x 3]
  x)
; => 3


(def dalmatian-list
  ["Pongo" "Perdita" "Puppy 1" "Puppy 2"]) ; and 97 more...
(let [dalmatians (take 2 dalmatian-list)]
  dalmatians)
; => ("Pongo" "Perdita")
```

`let` also introduces a new scope:

```clojure
(def x 0)
(let [x 1] x)
; => 1
```

However, you can reference existing bindings in your `let` binding:

```
(def x 0)
(let [x (inc x)] x)
; => 1
```

You can also use rest-params in `let`, just like you can in functions:

```clojure
(let [[pongo & dalmatians] dalmatian-list]
  [pongo dalmatians])
; => ["Pongo" ("Perdita" "Puppy 1" "Puppy 2")]
```

Notice that the value of a `let` form is the last form in its body
which gets evaluated.

`let` forms follow all the destructuring rules which we introduced in
"Calling a Function" above.

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
  (if (needs-matching-part? part)
    (recur remaining (conj final-body-parts (make-matching-part part)))
    (recur remaining final-body-parts)))
```

Notice that `part`, `remaining`, and `final-body-parts` each gets used
multiple times in the body of the `let`. If, instead of using the
names `part`, `remaining`, and `final-body-parts` we used the original
expressions, it would be a mess! For example:

```clojure
(if (needs-matching-part? (first remaining-asym-parts))
  (recur (rest remaining-asym-parts)
         (conj (conj (conj final-body-parts part) (first remaining-asym-parts))
               (make-matching-part (first remaining-asym-parts))))
  (recur (rest remaining-asym-parts)
         (conj (conj final-body-parts part) (first remaining-asym-parts))))
```

So, `let` is a handy way to introduce names for values.

### loop

`loop` provides another way to do recursion in Clojure. Let's look at
a simple example:

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

Next, it prints a super interesting little message.

Then, it checks the value of `iteration` - if it's greater than 3 then
it's time to say goodbye. Otherwise, we `recur`. This is like calling
the anonymous function created by `loop`, but this time we pass it an
argument, `(inc iteration)`.

You could in fact accomplish the same thing just using functions:

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

Regular expressions are tools for performing pattern matching on text.
I won't go into how they work, but here's their literal notation:

```clojure
;; pound, open quote, close quote
#"regular-expression"
```

In our symmetrizer, `re-find` returns true or false based on whether
the part's name starts with the string "left-":

```clojure
(defn needs-matching-part?
  [part]
  (re-find #"^left-" (:name part)))
(needs-matching-part? {:name "left-eye"})
; => true
(needs-matching-part? {:name "neckbeard"})
; => false
```

`make-matching-part` uses a regex to replace `"left-"` with `"right-"`:

```clojure
(defn make-matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})
(make-matching-part {:name "left-eye" :size 1})
; => {:name "right-eye" :size 1}]
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

(defn needs-matching-part?
  [part]
  (re-find #"^left-" (:name part)))

(defn make-matching-part
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
        (if (needs-matching-part? part) ; ~~~5~~~
          (recur remaining (conj final-body-parts (make-matching-part part))) ; ~~~6~~~
          (recur remaining final-body-parts))))))
```

1.  This function employs a general strategy which is common in functional
    programming. Given a sequence (in this case, a vector of body parts
    and their sizes), continuously split the sequence into a "head" and
    a "tail". Process the head, add it to some result, and then
    use recursion to continue the process with the tail.
2.  Begin looping over the body parts. The "tail" of the sequence will be
    bound to `remaining-asym-parts`. Initially, it's bound to the full
    sequence passed to the function, `asym-body-parts`. Create a result
    sequence, `final-body-parts`; its initial value is an empty vector.
3.  If `remaining-asym-parts` is empty, that means we've processed the
    entire sequence and can return the result, `final-body-parts`.
4.  Otherwise, split the list into a head, `part`, and tail,
    `remaining`. Also, add `part` to `final-body-parts` and re-bind the
    result to the name `final-body-parts`. This might seem weird, and
    it's worthwhile to figure out why it works.
5.  Our growing sequence of `final-body-parts` already includes the
    body part we're currently examining, `part`. Here, we decide
    whether we need to add the matching body part to the list.
6.  If so, then add the result of `make-matching-part` to
    `final-body-parts` and recur. Otherwise, just recur.

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

This is like telling Clojure to do this:

```clojure
(+ (+ (+ 1 2) 3) 4)
```

So, reduce works by doing this:

1. Apply the given function to the first two elements of a sequence.
   That's where `(+ 1 2)` comes from.
2. Apply the given function to the result and the next element of the
   sequence. In this case, the result of step 1 is `3`, and the next
   element of the sequence is `3` as well. So you end up with `(+ 3
   3)`.
3. Repeat step 2 for every remaining element in the sequence.

Reduce also takes an optional initial value. `15` is the initial value
here:

```clojure
(reduce + 15 [1 2 3 4])
```

If you provide an initial value, then reduce starts by applying the
given function to the initial value and the first element of the
sequence, rather than the first two elements of the sequence.

To further understand how reduce works, here's one way that it could
be implemented:

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
              (if (needs-matching-part? part)
                (conj final-body-parts (make-matching-part part))
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
solidify your Clojure knowledge if you haven't started already. The
[Clojure Cheatsheet](http://clojure.org/cheatsheet) is a great
reference listing all the built-in functions which operate on the data
structures we covered.

One great place to start would be to factor out the `loop` in the
`hit` function. Or, write out some Project Euler challenges. You can
also check out [4Clojure](<http://www.4clojure.com/problems>), an
online set of Clojure problems designed to test your knowledge. Just
write something!
