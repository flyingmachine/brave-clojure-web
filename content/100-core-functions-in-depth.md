--- 
title: Core Functions in Depth
link_title: Core Functions in Depth
---

# Core Functions in Depth

If you're a huge *Vampire Diaries* fan like me then you'll remember
the episode where the female teenage love interest, Elena, really
starts to question her mysterious crush's behavior: "Why did he
completely vanish without a trace, which is impossible, when I scraped
my knee?" and "How come his face turned into a horrible mask of death
when I nicked my finger?" and so on.

You might be asking yourself similar questions if you've started
playing with Clojure's core functions. "Why did `map` return what
looks like a list when I gave it a vector?" and "How come my map got
turned into a list of vectors?" and so on. (With Clojure, though, you
at least don't have to contemplate the profound existential terror of
being a seventeen-year-old for eternity.)

In this chapter, you'll learn about Clojure's deep, dark,
bloodthirsty, supernatur&ndash; \**cough*\* I mean, in this
chapter, you'll learn a couple underlying concepts which are key to
understanding Clojure's core functions. You'll also see usage examples
of the functions you'll be reaching for the most. By the end, you'll
understand:

* Programming to abstractions
    * The sequence abstraction
    * The collection abstraction
* Lazy evaluation
* Often-used functions

## Programming to Abstractions

Clojure emphasizes *programming to abstractions*. Since this phrase
doesn't mean anything without examples and explanation, let's explore
it a bit.

Take the sequence (seq) abstraction, for example. If a data structure
takes part in the seq abstraction then it can make use of the
extensive seq library, which includes such superstar functinos as
`map`, `reduce`, `filter`, `first`, `rest`, and dozens more. So, if
you can treat a data structure as a seq then you get oodles of
functionality for free.

A data structure can take part in the seq abstraction if it's possible
to treat its members as a *logical* list. "Logical" is emphasized in
"logical list" to distinguish it from a concrete implementation of a
linked list. Let's have a look at linked lists so that we can fully
appreciate this distinction.

In a linked list, nodes are linked in a linear sequence. Here's how
you might create one in Javascript:

```javascript
var node3 = {
  value: "last",
  next: null
};

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
`first`, `rest`, and `cons`. Here's how we would implement and use
these with our Javascript example:

```javascript
var first = function(node) {
  return node.value;
};

var rest = function(node) {
  return node.next;
};

var cons = function(newValue, node) {
  return {
    value: newValue,
    next: node
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

So for our purposes, an abstraction is a logical relationship among
the elements of a data structure, independent of the data structure's
implementation.
