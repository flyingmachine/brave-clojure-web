--- 
title: "Writing Macros"
link_title: "Writing Macros"
kind: documentation
draft: true
---

# Writing Macros

When I was 18 I got a job as a night auditor at a hotel in Santa Fe,
New Mexico. Four nights a week from 11pm till 7am I would dutifully
check people in and out, fold laundry, and play my Playstation 2 in
the back room. All in all it was a decent job, providing 8 hours of
pay for 2 hours of work.

After a few months of this schedule, though, I had become a different
person. My emotions in particular had taken on a life of their own.
One night, around 3am, I was watching an infomercial for a product
claiming to restore men's hair. As I watched the story of one
formerly-bald individual, I became overwhelmed with joy. "At last!",
my brain said to itself, "This man has gotten the love and success he
deserves! What an incredible product this is, giving hope to the
hopeless!"

Throughout the intervening years I've found myself wondering if I
could somehow recreate the emotional abandon and appreciation for life
induced by chronic sleep deprivation and waging war against my
circadian rhythms. The ultimate solution would be some kind of potion
&mdash; a couple quaffs to unleash my inner Richard Simmons, but not
for too long.

Just as a potion would allow me to temporarily alter my fundamental
nature, macros allow you to modify Clojure in ways that just aren't
possible with other languages. With macros, you can extend Clojure to
suit your problem space, building up the language itself.

In this chapter we'll thoroughly explore the art of writing
macros. By the end, you'll understand:

* The tools used to write macros
    * quote
    * syntax quote
    * unqoute
    * unwrapping
    * macroexpand
* Gotchas
    * double eval
    * variable capture
* Why use macros at all

## Potion Crafting

Because we're big dorks who love metaphors, 

Here's where we're going:

```clojure
;; Example of if-valid usage
;; Example of not using if-valid
;; macros to look at
;; * when
;; * infix
;; * reverse

```clojure
(defn error-messages-for
  "return a vector of error messages or nil if no errors
validation-check-groups is a seq of alternating messages and
validation checks"
  [value validation-check-groups]
  (for [[error-message validation] (partition 2 validation-check-groups)
        :when (not (validation value))]
    error-message))

(defn validate
  "returns a map of errors"
  [to-validate validations]
  (let [validations (vec validations)]
    (loop [errors {} v validations]
      (if-let [validation (first v)]
        (let [[fieldname validation-check-groups] validation
              value (get to-validate fieldname)
              error-messages (error-messages-for value validation-check-groups)]
          (if (empty? error-messages)
            (recur errors (rest v))
            (recur (assoc errors fieldname error-messages) (rest v))))
        errors))))

(defmacro if-valid
  [to-validate validations errors-name & then-else]
  `(let [to-validate# ~to-validate
         validations# ~validations
         ~errors-name (validate to-validate# validations#)]
     (if (empty? ~errors-name)
       ~(first then-else)
       ~(second then-else))))
```
