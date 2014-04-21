(defn titleize
  [topic]
  (str topic " for the Brave and True"))

(map titleize ["Hamsters" "Ragnarok"])
(map titleize '("Empathy" "Decorating"))
(map titleize #{"Elbows" "Soap Carving"})

(defn label-key-val
  [[key val]]
  (str "key: " key ", val: " val))

(map label-key-val {:name "Edward"
                    :occupation "perennial high-schooler"})

(into {}
      (map (fn [[key val]] [key (inc val)])
           {:max 30 :min 10}))

(def human-consumption   [8.1 7.3 6.6 5.0])
(def critter-consumption [0.0 0.2 0.3 1.1])
(defn unify-diet-data
  [human critter]
  {:human human
   :critter critter})

(def sum #(reduce + %))
(def avg #(/ (sum %) (count %)))
(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))

(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 10})

(reduce (fn [new-map [key val]]
          (if (> val 4)
            (assoc new-map key val)
            new-map))
        {}
        {:human 4.1
         :critter 3.9})

(def food-journal
  [{:month 1 :day 1 :human 5.3 :critter 2.3}
   {:month 1 :day 2 :human 5.1 :critter 2.0}
   {:month 2 :day 1 :human 4.9 :critter 2.1}
   {:month 2 :day 2 :human 5.0 :critter 2.5}
   {:month 3 :day 1 :human 4.2 :critter 3.3}
   {:month 3 :day 2 :human 4.0 :critter 3.8}
   {:month 4 :day 1 :human 3.7 :critter 3.9}
   {:month 4 :day 2 :human 3.7 :critter 3.6}])

(take-while #(= (:month %) 2)
            (drop-while #(< (:month %) 2) food-journal))

(filter #(< % 5) food-journal)

(defn person
  [name occupation]
  {:name name
   :occupation occupation})

(def vampire-database
  {0 {:makes-blood-puns? false, :has-pulse? true  :name "McFishwich"}
   1 {:makes-blood-puns? false, :has-pulse? true  :name "McMackson"}
   2 {:makes-blood-puns? true,  :has-pulse? false :name "Damon Salvatore"}
   3 {:makes-blood-puns? true,  :has-pulse? true  :name "Mickey Mouse"}})

(defn vampire-related-details
  [social-security-number]
  (Thread/sleep 1000)
  (get vampire-database social-security-number))

(defn vampire?
  [record]
  (and (:makes-blood-puns? record)
       (not (:has-pulse? record))))

(defn identify-vampire
  [social-security-numbers]
  (first (filter vampire?
                 (map vampire-related-details social-security-numbers))))

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
