(defn vampire?
  [record]
  (instant-computation record))

(defn vampire-related-details
  [social-security-number]
  (ten-second-computation social-security-number))

(defn identify-vampire
  [social-security-numbers]
  (last (take-while #(not (vampire? %))
                    (map vampire-related-details
                         social-security-numbers))))

(defn identify-vampire
  [social-security-numbers]
  (first (drop-while #(not (vampire? %))
                     (map vampire-related-details
                          social-security-numbers))))

(defn snitch
  "Announce real identity to the world"
  [identity]
  (println (:real identity))
  (:real identity))

(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spiderman" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}
   {:alias "alias 5", :real "real 5"}
   {:alias "alias 6", :real "real 6"}
   {:alias "alias 7", :real "real 7"}
   {:alias "alias 8", :real "real 8"}
   {:alias "alias 9", :real "real 9"}
   {:alias "alias 10", :real "real 10"}
   {:alias "alias 11", :real "real 11"}
   {:alias "alias 12", :real "real 12"}
   {:alias "alias 13", :real "real 13"}
   {:alias "alias 14", :real "real 14"}
   {:alias "alias 15", :real "real 15"}
   {:alias "alias 16", :real "real 16"}
   {:alias "alias 17", :real "real 17"}
   {:alias "alias 18", :real "real 18"}
   {:alias "alias 19", :real "real 19"}
   {:alias "alias 20", :real "real 20"}
   {:alias "alias 21", :real "real 21"}
   {:alias "alias 22", :real "real 22"}
   {:alias "alias 23", :real "real 23"}
   {:alias "alias 24", :real "real 24"}
   {:alias "alias 25", :real "real 25"}
   {:alias "alias 26", :real "real 26"}
   {:alias "alias 27", :real "real 27"}
   {:alias "alias 28", :real "real 28"}
   {:alias "alias 29", :real "real 29"}
   {:alias "alias 30", :real "real 30"}
   {:alias "alias 31", :real "real 31"}
   {:alias "alias 32", :real "real 32"}
   {:alias "alias 33", :real "real 33"}])

(map snitch identities)

(into {:favorite-animal "kitty"} {:least-favorite-smell "dog"
                                  :relationship-with-teenager "creepy"})

(defn my-conj
  [target & additions]
  (into target additions))

(defn my-into
  [target additions]
  (apply conj target additions))


(def add-missing-elements
  (partial conj ["water" "earth" "air"]))

(defn my-partial
  [partialized-fn & args]
  (fn [& more-args]
    (apply partialized-fn (into args more-args))))

(defn my-complement
  [fun]
  (fn [& args]
    (not (apply fun args))))
