(def asym-humanoid-body-parts [["head" 3]
                               ["left-eye" 1]
                               ["left-ear" 1]
                               ["mouth" 1]
                               ["nose" 1]
                               ["neck" 2]
                               ["left-shoulder" 3]
                               ["left-upper-arm" 3]
                               ["chest" 10]
                               ["back" 10]
                               ["left-forearm" 3]
                               ["abdomen" 6]
                               ["left-kidney" 1]
                               ["left-hand" 2]
                               ["left-knee" 2]
                               ["left-thigh" 4]
                               ["left-lower-leg" 3]
                               ["left-achilles" 1]
                               ["left-foot" 2]])

(defn has-matching-part?
  [[name weight]]
  (re-find #"^left-" name))

(defn matching-part
  [[name weight]]
  [(clojure.string/replace name #"^left-" "right-")
   weight])

(defn symmetrize-body-parts
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