;; In ns below, notice that "gen-class" was removed
(ns fwpd.core
  ;; We haven't gone over require but we will.
  (:require [clojure.string :as s]))

(def filename "suspects.csv")

;; Later on we're going to be converting each row in the CSV into a
;; map, like {:name "Edward Cullen" :glitter-index 10}.
;; Since CSV can't store Clojure keywords, we need to associate the
;; textual header from the CSV with the correct keyword.
(def headers [:name :glitter-index])

(defn str->int
  [str]
  (Integer. str))

;; CSV is all text, but we're storing numeric data. We want to convert
;; it back to actual numbers.
(def conversions {:name identity
                  :glitter-index str->int})
(defn convert
  [header value]
  ((get conversions header) value))

(defn parse-csv
  "Convert a csv into rows of columns"
  [string]
  (map #(s/split % #",")
       (s/split string #"\n")))

(defn mapify
  "Return a seq of maps like {:name \"Edward Cullen\" :glitter-index 10}"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [header value]]
                   (assoc row-map header (convert header value)))
                 {}
                 (map vector headers unmapped-row)))
       rows))

(defn glitter-filter
  [minimum-glitter records]
  (filter #(>= (:glitter-index %) minimum-glitter) records))
