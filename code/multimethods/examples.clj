(ns were-creatures)
(defmulti full-moon-behavior (fn [were-creature] (:were-type were-creature)))
(defmethod full-moon-behavior :wolf
  [were-creature]
  (str (:name were-creature) " will howl and murder"))
(defmethod full-moon-behavior :simmons
  [were-creature]
  (str (:name were-creature) " will encourage people and sweat to the oldies"))
(full-moon-behavior {:were-type :wolf
                     :name "Rachel from next door"})
(full-moon-behavior {:were-type :simmons
                     :name "Andy the baker"})


(defmethod full-moon-behavior nil
  [were-creature]
  (str (:name were-creature) " will stay at home and eat ice cream"))
(full-moon-behavior {:were-type :nil
                     :name "Marty the nurse"})

(defmethod full-moon-behavior :default
  [were-creature]
  (str (:name were-creature) " will stay up all night fantasy footballing"))
(full-moon-behavior {:were-type :office-worker
                     :name "Jimmy from sales"})

(ns random-namespace)
(defmethod were-creatures/full-moon-behavior :murray
  [were-creature]
  (str (:name were-creature) " will show up and dance at random parties"))

(were-creatures/full-moon-behavior {:name "Laura the intern" :were-type :murray})

;; type
(ns user)
(defmulti types (fn [x y] [(class x) (class y)]))
(defmethod types [java.lang.String java.lang.String]
  [x y]
  "Two strings!")
(types "String 1" "String 2")
; => "Two strings!" "String 2")

;; rest args
(defmulti ex (fn [& args] true))
(defmethod ex true
  [& args]
  args)


;; protocols
(ns data-psychology)
(defprotocol Psychodynamics
  "Plumb the inner depths of your data types"
  (thoughts [x] "The data type's innermost thoughts")
  (feelings-about [x] [x y] "Feelings about self or other"))


(extend-type java.lang.String
  Psychodynamics
  (thoughts [x] "Truly, the character defines the data type")
  (feelings-about
    ([x] "longing for a simpler way of life")
    ([x y] (str "envious of " y "'s simpler way of life"))))

(extend-type java.lang.Object
  Psychodynamics
  (thoughts [x] "Maybe the Internet is just a vector for toxoplasmosis")
  (feelings
    ([x] "meh")
    ([x y] (str "meh about" y))))
