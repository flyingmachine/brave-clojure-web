(ns the-divine-cheese-code.core
  (:require [clojure.java.browse :as browse])
  (:gen-class))

(def heists [{:location "Cologne, Germany"
              :cheese-name "x"
              :lat 50.95
              :lng 6.97}
             {:location "Zurich, Switzerland"
              :cheese-name "x"
              :lat 47.37
              :lng 8.55}
             {:location "Marseilles, France"
              :cheese-name "x"
              :lat 43.30
              :lng 5.37}
             {:location "Zurich, Switzerland"
              :cheese-name "x"
              :lat 47.37
              :lng 8.55}
             {:location "Vatican City"
              :lat 41.90
              :lng 12.45}])

(defn url
  [filename]
  (str "file:///"
       (System/getProperty "user.dir")
       "/"
       filename))

(defn -main
  [& args]
  (let [filename "map.html"]
    (spit filename (draw 200 200 (line (points the-divine-cheese-code.core/heists))))
    (browse/browse-url (url filename))))