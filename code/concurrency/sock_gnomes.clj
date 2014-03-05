;; First we add sock-and-gnome creation technology
(def sock-varieties
  #{"darned" "argyle" "wool" "horsehair" "mulleted"
    "passive-aggressive" "striped" "polka-dotted"
    "athletic" "business" "power" "invisible" "gollumed"})

(defn sock-count
  [sock-variety count]
  {:variety sock-variety
   :count count})

(defn generate-sock-gnome
  "Create a sock gnome state with no socks"
  [name]
  {:name name
   :socks #{}})

;; Here are our actual refs
(def sock-gnome (ref (generate-sock-gnome "Barumpharumph")))
(def dryer (ref {:name "LG 1337"
                 :socks (set (map #(sock-count % 2) sock-varieties))}))

;; Now let's perform the transfer:
(defn steal-sock
  [gnome dryer]
  (dosync
   (when-let [pair (some #(if (= (:count %) 2) %) (:socks @dryer))]
     (let [updated-count (sock-count (:variety pair) 1)]
       (alter gnome update-in [:socks] conj updated-count)
       (alter dryer update-in [:socks] disj pair)
       (alter dryer update-in [:socks] conj updated-count)))))

(defn similar-socks
  [target-sock sock-set]
  (filter #(= (:variety %) (:variety target-sock)) sock-set))


#{{:variety "passive-aggressive", :count 2} {:variety "power", :count 2}
  {:variety "athletic", :count 2} {:variety "business", :count 2}
  {:variety "argyle", :count 2} {:variety "horsehair", :count 2}
  {:variety "gollumed", :count 2} {:variety "darned", :count 2}
  {:variety "polka-dotted", :count 2} {:variety "wool", :count 2}
  {:variety "mulleted", :count 2} {:variety "striped", :count 2}
  {:variety "invisible", :count 2}}
