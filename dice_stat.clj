#!/usr/bin/env bb

(ns dice-stat)


(defn roll-a-die
  [faces]
  (inc (rand-int faces)))


(defn roll-dice
  [faces number]
  (->> (repeatedly number #(roll-a-die faces))
       (apply +)))


(defn highest
  [faces number cnt]
  (let [samples 1000]
    (->>
      (repeatedly samples #(roll-dice faces number))
      (frequencies)
      (map #(vector (first %) (/ (second %) samples)))
      (sort-by second)
      (reverse)
      (take cnt))))

(highest 6 2 2)
