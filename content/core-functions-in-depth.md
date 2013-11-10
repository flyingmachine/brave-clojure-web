--- 
title: Core Functions in Depth
link_title: Core Functions in Depth
kind: documentation
---

# Core Functions in Depth

If you're a huge *Vampire Diaries* fan like me then you'll remember
the episode where the female teenage love interest, Elena, really
starts to question her mysterious crush's behavior: "Why did he
instantly vanish without a trace, which is impossible, when I scraped
my knee?" and "How come his face turned into a horrible mask of death
when I nicked my finger?" and so on.

You might be asking yourself similar questions if you've started
playing with Clojure's core functions. "Why did `map` return what
looks like a list when I gave it a vector?" and "How come `reduce`
treats my map like a list of vectors?" and so on. (With Clojure,
though, you're at least spared from contemplating the profound
existential terror of being a seventeen-year-old for eternity.)

In this chapter, you'll learn about Clojure's deep, dark,
bloodthirsty, supernatur&ndash; \**cough*\* I mean, in this chapter,
you'll learn about a couple of Clojure's underlying concepts. This
will give you the grounding you need to read the documentation for
functions you haven't used before and to understand what's happening
when you give them a try.

You'll also see usage examples of the functions you'll be reaching for
the most. This will give you a solid foundation for writing your own
code and reading and learning from others' projects.

By the end, you'll understand:

* Programming to abstractions
    * The sequence abstraction
    * Lazy sequences
    * The collection abstraction
* Often-used functions
    * map
    * into
    * conj
    * apply
    * partial
* How to parse and query a CSV of vampire data

## Programming to Abstractions

Clojure emphasizes *programming to abstractions*. Since this phrase
doesn't mean anything without examples and explanation, let's explore
it a bit.

In general, programming to abstractions gives us power by letting us
use libraries of functions on a data structure regardless of that data
structure's implementation.

Take the sequence abstraction. First, some terminology: if a data
structure can be treated as a sequence, then we call that sequence a
"seq" because it's shorter and hipper, and that's how it's referred to
everywhere else.

If a data structure takes part in the sequence abstraction then it can
make use of the extensive seq library, which includes such superstar
functions as `map`, `reduce`, `filter`, `distinct`, `group-by`, and
dozens more. So, if you can treat a data structure as a seq then you
get oodles of functionality for free. Vectors, maps, lists, and sets
can all be treated as seqs.

A data structure can take part in the sequence abstraction if it's
possible to treat it as a *logical* list. "Logical" is emphasized in
"logical list" to distinguish it from the concrete implementation of a
linked list. Let's implement a linked list in Javascript so that we
can fully appreciate the distinction between the seq (logical list)
abstraction and the concrete implementation of a linked list.

In a linked list, nodes are linked in a linear sequence. Here's how
you might create one in Javascript:

```javascript
// "next" is null because this is the last node in the list
var node3 = {
  value: "last",
  next: null
};

// "next" points to node3 - that's the "link" in "linked list"
var node2 = {
  value: "middle",
  next: node3
};

var node1 = {
  value: "first",
  next: node2
};
```

Graphically, you could represent this list like this:

![Linked list](/images/core-functions-in-depth/linked-list.png)

There are three core functions that you can perform on a linked list:
`first`, `rest`, and `cons`. Once those are implemented, you can
implement `map`, `reduce`, `filter`, and so on top of them. Here's how
we would implement and use `first`, `rest`, and `cons` with our
Javascript example:

```javascript
// Note that the parameter is named "node" here. This might be
// confusing - you might think, "Ain't I getting the first element of
// a *list*? Well, you operate on the elements of a list one node at
// a time!
var first = function(node) {
  return node.value;
};

var rest = function(node) {
  return node.next;
};

// Append a new node to the beginning of the list
var cons = function(newValue, list) {
  return {
    value: newValue,
    next: list
  };
};

first(node1);
// => "first"

first(rest(node1));
// => "middle"

first(rest(rest(node1)));
// => "last"

first(cons("new first", node1));
// "new first"

first(rest(cons("new first", node1)));
// => "first"
```

As noted above, you can implement `map` in terms of `first`, `rest`,
and `cons`:

```javascript
var map = function (list, transform) {
  if (list === null) {
    return null;
  } else {
    return cons(transform(first(list)), map(rest(list), transform));
  }
}

// Let's see it in action:
first(
  map(node1, function (val) { return val + " mapped!"})
);

// => "first mapped!"
```

So here's the cool thing &mdash; since `map` is implemented completely
in terms of `cons`, `first`, and `rest`, you could actually pass it
any data structure and it would work as long as `cons`, `first`, and
`rest` work on that data structure. Those three functions are the
abstraction's *interface*. Here's how they might work for an array:

```
var first = function (array) {
  return array[0];
}

var rest = function (array) {
  var sliced = array.slice(1, array.length);
  if (sliced.length == 0) {
    return null;
  } else {
    return sliced;
  }
}

var cons = function (newValue, array) {
  return [newValue].concat(array);
}


var list = ["Transylvania", "Forks, WA"];
map(list, function (val) { return val + " mapped!"})
// => ["Transylvania mapped!", "Forks, WA mapped!"]
```

Thus, if you can implement `first`, `rest`, and `cons` then you get
`map` for free, along with the aforementioned oodles of other
functions.

The takeaway here is that it's powerful to focus on what we can *do*
with a data structure and to ignore, as much as possible, its
implementation. Implementations rarely matter in and of themselves.
They're only a means to an end. We ultimately only care about what we
do with them, and by programming to abstractions we're able to re-use
libraries and thus do more with them.

By the way: Javascript doesn't provide an easy means of defining
different implementations of a function based on the type(s) of
argument(s), but Clojure does. It's pretty cool! But for now we won't
cover it. For now, just trust that Clojure makes it super easy.

## The Sequence Abstraction

Now that we understand the general approach of programming to
abstractions we can answer some of the questions we posed at the
beginning of the chapter:

* How come my map got turned into a list of vectors?
* Why did `map` return what looks like a list when I gave it a vector?
* Isn't Damon, my crush's hunky and troubled older brother, making
  lots of creepy puns involving consuming my blood as food? What's up
  with that?

### Seq Functions Convert Data Structures to Seqs

In the Javascript examples we gave above, we indicated one way that
you could allow a data structure to participate in the seq
abstraction: make `first`, `rest`, and `cons` work on that data
structure.

But! There's another way: explicitly convert the data structure to a
seq. So, instead of extending `first` etc. to work on your data
structure, you provide some way for your data structure to work with
those functions as they're currently implemented.

Clojure functions often use the `seq` function to
do this. From the [Clojure documentation](http://clojure.org/sequences):

> Clojure uses the ISeq interface to allow many data structures to
> provide access to their elements as sequences. The seq function
> yields an implementation of ISeq appropriate to the collection.

If those details don't really make sense, don't worry about it too
much. The important thing to know is that many functions will call
`seq` on a collection argument before doing anything else. The `map`
function does this, for example:

```clojure
; identity returns whatever was passed to it
(identity "Stefan Salvatore from Vampire Diaries")
; => "Stefan Salvatore from Vampire Diaries"

;; Map returns a new sequence consisting of the result of calling
;; "identity" on each member of the sequence it was given
(map identity {:name "Bill Compton" :occupation "Dead mopey guy"})
; => ([:name "Bill Compton"] [:occupation "Dead mopey guy"])
```

Since we know that `map` calls `seq` on its collection arguments, and
since `identity` returns whatever value was passed to it, we can
deduce that the `seq` function converts a map data structure into a
sequence of vectors, each of which is a key/value pair:

```clojure
(seq {:name "Bill Compton" :occupation "Dead mopey guy"})
; => ([:name "Bill Compton"] [:occupation "Dead mopey guy"])
```

I wanted to point out this example in particular because it might be
surprising and confusing. It was for me when I first started Clojure.
Knowing these underlying mechanisms will save you from the kind of
frustration and general mopiness of the kind of often seen among male
vampires trying to retain their humanity.

### Seq Functions Sometimes Return Lazy Seqs

Why do `map` and other functions return what looks like a list?

```clojure
(map identity {:name "Van Helsing" :occupation "Living angry guy"})
; => ([:name "Van Helsing"] [:occupation "Living angry guy"])
```

As we saw in the last section, `map` first calls `seq` on the
collection you pass to it. So that's part of the answer &mdash; the
functions which operate on seqs call `seq` on their arguments and
don't bother to convert them back.

But that's not the whole story. Some functions, like `map`, return
a "lazy seq". A lazy seq is a seq whose members aren't computed until
you try to access them. Computing a seq's members is called
"realizing" the seq.

There are two reasons for lazy seqs. First, they're more efficient
because they don't do unnecessary computations. For example, pretend
that you're part of a modern-day task force whose purpose is to
identify vampires. You know that there is a single vampire out a group
of one million suspects. Your boss gives you a list of one million
social security numbers and shouts, "Get it done, McFishwich!"

Here's one way that you could do that:

```clojure
(defn vampire?
  "Returns boolean"
  [record]
  (instant-computation record))

(defn vampire-related-details
  "Looks up vampire related details in super sophisticated database"
  [social-security-number]
  (ten-second-computation social-security-number))


;; To understand the function below, you need to understand
;; drop-while:
(drop-while neg? [-1 -2 0 1 2])
; => (0 1 2)

;; The strategy here is to keep dropping members of a sequence if
;; we know they're not a vampire. Then the first member of the
;; remaining sequence is a vampire
(defn identify-vampire
  [social-security-numbers]
  (first (drop-while #(not (vampire? %))
                     (map vampire-related-details
                          social-security-numbers))))
```

As you can see, it takes 10 seconds to pull up each potential
vampire's details. A non-lazy implementation of this would apply
`vampire-related-details` to every social security number before
passing the result to `drop-while`. This would take 116 days (10
million seconds), and half your city could be dead by then!

Instead, since `map` returns a lazy seq, `vampire-related-details`
doesn't get called until it's actually needed. At least, it's useful
to think of it that way. Sometimes lazy seqs are chunked, meaning that
they realize 32 members at a time:

```clojure
(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spiderman" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}
   {:alias "alias 5", :real "real 5"}
   ; ... Just pretend that there are actually maps here for 6-30
   {:alias "alias 31", :real "real 31"}
   {:alias "alias 32", :real "real 32"}
   {:alias "alias 33", :real "real 33"}
   {:alias "alias 33", :real "real 34"}])

(defn snitch
  "Announce real identity to the world"
  [identity]
  (println (:real identity))
  (:real identity))

(map snitch [{:alias "Batman" :real "Bruce Wayne}])

(def revealed-identities (map snitch identities))
(first revealed-identities)
;; The following gets printed
Bruce Wayne
Peter Parker
Your mom
Your dad
real 5
... (real 6-30 would actually be printed)
real 31
real 32
```

Notice that `real 33` and `real 34` were *not* printed. Only 32 lines
were printed. Clojure doesn't realize a lazy list until you try to
read a value from it, and then it usually realizes 32 members at a
time. This is done for the sake of efficiency.

Note, also, that Clojure caches the values of the lazy seq. It doesn't
have to re-compute them when you try to access them again. Continuing
where we left off from the previous example:

```clojure
;; Since the lazy seq has already realized the first member, it
;; doesn't run the snitch function again and nothing gets printed
(first revealed-identities)
; => "Bruce Wayne"
```

Sometimes you need to realize the entire seq without bothering with
trying to take every member. Usually the only reason you'd want to do
this if you want to produce side effects &mdash; for example, if you
want to print every single real identity in the example above. In that
case, you use `doall` on the seq. The purpose of `doall` is to realize
the seq.

And that covers lazy seqs! Now you'll know what the heck is going on
next time you call `map` on a map!

### About Those Creepy Puns

He's a vampire, dammit! Why can't you see that!?!?

## The Collection Abstraction

The collection abstraction is closely related to the sequence
abstraction. All of Clojure's core data structures &mdash; vectors,
maps, lists and sets &mdash; take part in both abstractions. This
makes complete sense when you think about it. (Think about it! Now!)

They differ in that the sequence abstraction is "about" operating on
members individually while the collection abstraction is "about" the
data structure as a whole. For example, the collection functions
`count`, `empty?`, and `every?` aren't about any individual
element; they're about the whole.

```clojure
(empty? [])
; => true

(empty? ["no!"])
; => false
```

Practically speaking, you'll rarely consciously think "OK, self!
You're working with the collection as a whole now. Think in terms of
the collection abstraction!" Nevertheless, it's useful to know the
concepts which underly the functions and data structures you're using.

Now we'll examine two common collection functions whose similarities
can be a bit confusing.

### Into

One of the most import collection functions is `into`. As you now
know, many seq functions return a seq rather than the original data
structure. You'll probably want to convert the return value back into
the original value, and `into` lets you do that:

```clojure
(map identity {:sunlight-reaction "Glitter!"})
; => ([:sunlight-reaction "Glitter!"])

(into {} (map identity {:sunlight-reaction "Glitter!"}))
```

This will work with other data structures as well:

```clojure
;; convert back to vector
(map identity [:garlic :sesame-oil :fried-eggs])
; map returns a seq
; => (:garlic :sesame-oil :fried-eggs)

(into [] (map identity [:garlic :sesame-oil :fried-eggs]))
; => [:garlic :sesame-oil :fried-eggs]

;; convert back to set
(map identity [:garlic-clove :garlic-clove])
; => (:garlic-clove :garlic-clove)

;; sets only contain unique values
(into #{} (map identity [:garlic-clove :garlic-clove]))
; => #{:garlic-clove}
```

The first argument of `into` doesn't have to be empty:

```clojure
(into {:favorite-emotion "gloomy"} [[:sunlight-reaction "Glitter!"]])
; => {:favorite-emotion "gloomy" :sunlight-reaction "Glitter!"}

(into ["cherry"] '("pine" "spruce"))
```

And of course, both arguments can be the same type:

```clojure
(into {:favorite-animal "kitty"} {:least-favorite-smell "dog"
                                  :relationship-with-teenager "creepy"})
; =>
; {:favorite-animal "kitty"
;  :relationship-with-teenager "creepy"
;  :least-favorite-smell "dog"}
```

If `into` were asked to describe its strengths at a job interview, it
would say "I'm great at taking two collections and adding all the
elements from the second to the first."

### Conj

Conj also adds elements to a collection, but it does it in a
slightly different way:

```clojure
(conj [0] [1])
; => [0 [1]]
;; Whoopsie! Looks like it added the entire vector [1] onto [0].
;; Compare to into:

(into [0] [1])
; => [0 1]

;; Here's what we want:
(conj [0] 1)
; => [0 1]

;; We can supply as many elements to add as we want:
(conj [0] 1 2 3 4)
; => [0 1 2 3 4]

;; We can also add to maps:
(conj {:time "midnight"} [:place "ye olde cemetarium"])
; => {:place "ye olde cemetarium" :time "midnight"}
```

The two are so similar, you could even define `conj` in terms of
`into`:

```clojure
(defn my-conj
  [target & additions]
  (into target additions))

(my-conj [0] 1 2 3)
; => [0 1 2 3]
```

This kind of pattern isn't that uncommon. You'll see two functions
which do the same thing, it's just that one takes a rest-param (`conj`)
and one takes a seqable data structure (`into`).

## Function Functions

Learning to take advantage of Clojure's ability to accept functions as
arguments and return functions as values is really fun, even if it
takes some getting used to.

Two of Clojure's functions, `apply` and `partial` might seem
especially weird because they both accept *and* return functions.
Let's unweird them.

### apply

Remember how we defined `conj` in terms of `into` above? Well, we can
also define `into` in terms of `conj` by using `apply`:

```clojure
(defn my-into
  [target additions]
  (apply conj target additions))

(my-into [0] [1 2 3])
; => [0 1 2 3]

;; the above call to my-into is equivalient to calling:
(conj [0] 1 2 3)
```

`apply` "explodes" a seqable data structure so that it can be passed
to a function which expects a rest-param.

```clojure
;; Max takes a rest-param, comparing all the arguments passed to it.
;; We pass only one argument and max returns it:
(max [0 1 2])
; => [0 1 2]

;; Let's "explode" the argument:
(apply max [0 1 2])
; => 2
```

Ta-da!

### partial

Let's look at some examples of `partial` before describing what it
does:

```clojure
(def add10 (partial + 10))
(add10 3) ;=> 13
(add10 5) ;=> 15

(def add-missing-element
  (partial conj ["water" "earth" "air"]))

(add-missing-elements "unobtainium" "adamantium")
; => ["water" "earth" "air" "unobtainium" "adamantium"]
```

So, `partial` takes a function and any number of arguments. It then
returns a new function. When you call the returned function, it calls the
original function with the original arguments you supplied it along
with the new arguments. Ugh that is an inelegant description. Here's
how you might define `partial`:

```clojure
(defn my-partial
  [partialized-fn & args]
  (fn [& more-args]
    (apply partialized-fn (into args more-args))))

(def add20 (my-partial + 20))
(add20 3) ; => 23
```

Ta-da!

### Bonus Function: complement

Here's one more function to demonstrate the usefulness and versatility
of higher-order functions. Remember the `identify-vampire` function
above? Here it is again so that you don't have to overexert your
scrolling finger:

```clojure
(defn identify-vampire
  [social-security-numbers]
  (first (drop-while #(not (vampire? %))
                     (map vampire-related-details
                          social-security-numbers))))
```

Look at the first argument to `drop-while`, `#(not (vampire? %))`.
It's so common to want the *complement* (the negation) of a boolean
function that there's a function for that:

```clojure
;; define complement
(def not-vampire? (complement vampire?))

;; change identify-vampire to use complemented function
(defn identify-vampire
  [social-security-numbers]
  (first (drop-while not-vampire?
                     (map vampire-related-details
                          social-security-numbers))))
```

Here's how you might implement `complement`:

```clojure
(defn my-complement
  [fun]
  (fn [& args]
    (not (apply fun args))))

(def my-pos? (complement neg?))
(my-pos? 1)  ; => true
(my-pos? -1) ; => false
```

As you can see, `complement` is a fairly humble function. It does one
little thing, and does it well. This isn't going to map reduce
terabytes of data for your or something like that.

But it does demonstrate the power of higher-order functions. They
allow you build up libraries of utility functions in a way which is
impossible in most other languages. In aggregate, these utility
functions make your life a lot easier.

Ta-da!

## FWPD

To pull everything together, let's write the beginnings of a
sophisticated vampire data analysis program for the Forks, Washington
Police Department (FWPD).

The FWPD has a sophisticated new database technology called CSV
(comma-separated values). Our job is to parse this state-of-the-art
"csv" and analyze it for potential vampires. We'll do that by
filtering on each suspect's "glitter index", a 0-10 prediction of the
suspect's vampireness invented by Larry Page and Sergey Brin. Let's
create a new leiningen project for our tool:

```
lein new app fwpd
```

Under the new `fwpd` directory, create a file named `suspects.csv` and
enter contents like the following:

```
Name,Glitter Index
Edward Cullen,10
Bella Swan,0
Charlie Swan,0
Jacob Black,3
Carlisle Cullen,6
```

Now it's time to get our hands dirty. Make your file
`fwpd/src/fwpd/core.clj` look like this:

```clojure
;; In ns below, notice that "gen-class" was removed
(ns fwpd.core
  ;; We haven't gone over require but we will.
  (:require [clojure.string :as s]))

(def filename "suspects.csv")

;; Later on we're going to be converting each row in the CSV into a
;; map, like {:name "Edward Cullen" :glitter-index 10}.
;; Since CSV can't store Clojure keywords, we need to associate the
;; textual header from the CSV with the correct keyword.
(def headers->keywords {"Name" :name
                        "Glitter Index" :glitter-index})

(defn str->int
  "If argument is a string, convert it to an integer"
  [str]
  (if (string? str)
    (read-string (re-find #"^-?\d+$" str))
    str))

;; CSV is all text, but we're storing numeric data. We want to convert
;; it back to actual numbers.
(def conversions {:name identity
                  :glitter-index str->int})

(defn parse
  "Convert a csv into rows of columns"
  [string]
  (map #(s/split % #",")
       (s/split string #"\n")))

(defn mapify
  "Return a seq of maps like {:name \"Edward Cullen\" :glitter-index 10}"
  [rows]
  (let [;; headers becomes the seq (:name :glitter-index)
        headers (map #(get headers->keywords %) (first rows))
        ;; unmapped-rows becomes the seq
        ;; (["Edward Cullen" "10"] ["Bella Swan" "0"] ...)
        unmapped-rows (rest rows)]
    ;; Now let's return a seq of {:name "X" :glitter-index 10}
    (map (fn [unmapped-row]
           ;; We're going to use map to associate each header with its
           ;; column. Since map returns a seq, we use into to convert
           ;; it into a map.
           (into {}
                 ;; map can actually take multiple seq arguments. In
                 ;; this case, we're passing a seq of headers and a
                 ;; seq of cells. map applies the anonymous function
                 ;; to headers[0], unmapped-row[0], then headers[1],
                 ;; unmapped-row[1] and so forth
                 (map (fn [header column]
                        ;; associate the header with the converted column
                        [header ((get conversions header) column)])
                      headers
                      unmapped-row)))
         unmapped-rows)))

(defn glitter-filter
  [minimum-glitter records]
  (filter #(>= (:glitter-index %) minimum-glitter) records))
```

Notice that we took out the `-main` function. This is because, for
right now, we only care about running the above code in the REPL. Try
this out in your REPL:

```clojure
;; slup reads a file
(mapify (parse (slurp filename)))
```

`mapify` is the most complicated function of the bunch, and I
recommend going through it to really figure out what's going on. One
place to start might be the inner `map`:

```clojure
(map (fn [header column]
       ;; associate the header with the converted column
       [header ((get conversions header) column)])
     headers
     unmapped-row)
```

You could try breaking that out into a separate function and passing
it arguments to understand what it does:

```clojure
(defn mapify-row
  [headers unmapped-row]
  (map (fn [header column]
       ;; associate the header with the converted column
       [header ((get conversions header) column)])
     headers
     unmapped-row))

(mapify-row [:name] ["Joe"])
; => ([:name "Joe"])
```

That's a strategy that's generally useful in figuring out someone
else's code: figure out what data is being passed to some nested
function call, then extract that nested function call and pass it the
same kind of data and see what happens.

Now that you know how to get mapified records, you can filter on their
glitter index:

```clojure
(glitter-filter 3 (mapify (parse (slurp filename))))
({:name "Edward Cullen", :glitter-index 10}
 {:name "Jacob Black", :glitter-index 3}
 {:name "Carlisle Cullen", :glitter-index 6})
```

You better go round up those sketchy characters!

### Vampire Analysis 2.0

The vampire analysis program you now have is already decades ahead of
anything else on the market. But how could you make it better? I
suggest trying the following:

* Turn the result of your glitter filter into a list of names
* Write a function, `append`, which will append a new suspect to your
  list of suspects
* Write a function, `validate`, which will check that `:name` and
  `:glitter-index` are present when you `append`. Validate should
  accept two arguments: a map of keywords to validating functions,
  similar to `conversions`, and the record to be validated
* Write a function which will take your list of maps and convert it
  back to a CSV string. You'll need to use the `clojure.string/join`
  function.

Good luck, McFishwich!

## Chapter Summary

In this chapter, you learned:

* Clojure emphasizes programming to abstractions.
* The sequence abstraction deals with operating on the individual
  elements of a sequence. Seq functions often convert their arguments
  to a seq and return a lazy seq.
* Lazy evaluation improves performance by delaying computations until
  they're needed.
* The collection abstraction deals with data structures as a whole.
* Never trust someone who sparkles in sunlight.
