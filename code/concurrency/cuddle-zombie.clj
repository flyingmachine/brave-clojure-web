(def fred (atom {:cuddle-hunger-level 0
                 :percent-deteriorated 0}))

(swap! fred (fn [state] (merge-with + state {:cuddle-hunger-level 1})))
