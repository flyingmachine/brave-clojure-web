(if true
  (do (println "Success!")
      "abra cadabra")
  (do (println "Failure :(")
      "hocus pocus"))


(if (has-matching-part? (first remaining-asym-parts))
  (recur (rest remaining-asym-parts)
         (conj (conj (conj final-body-parts part) (first remaining-asym-parts))
               (matching-part (first remaining-asym-parts))))
  (recur (rest remaining-asym-parts)
         (conj (conj final-body-parts part) (first remaining-asym-parts))))
