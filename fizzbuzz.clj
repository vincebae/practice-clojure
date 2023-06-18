#!/usr/bin/env bb

(defn fizzbuzz
  [number]
  (-> '[]
      (#(if (= (mod number 3) 0) (conj % "fizz") %))
      (#(if (= (mod number 5) 0) (conj % "buzz") %))
      (#(if (= (mod number 7) 0) (conj % "tazz") %))
      (#(if (empty? %) (str number) (apply str %)))))


(defn fizzbuzz2
  [number]
  (cond-> '[]
    (zero? (mod number 3)) (conj "fizz")
    (zero? (mod number 5)) (conj "buzz")
    (zero? (mod number 7)) (conj "tazz")
    :always (#(apply str (if (empty? %) [number] %)))))


(defn fizzbuzz3
  [number]
  (cond-> nil
    (zero? (mod number 3)) (str "fizz")
    (zero? (mod number 5)) (str "buzz")
    (zero? (mod number 7)) (str "tazz")
    :always (or (str number))))


(defn fizzbuzz4
  [number]
  (->> [[3 "fizz"] [5 "buzz"] [7 "tazz"]]
       (reduce #(str %1 ({0 (second %2)} (mod number (first %2)))) "")
       (#(or (not-empty %) (str number)))))


(defn fizzbuzz5
  [number]
  (->> [[3 "fizz"] [5 "buzz"] [7 "tazz"]]
       (map #({0 (second %)} (mod number (first %))))
       (#(or (not-empty (apply str %)) (str number)))))


(defn fizzbuzz6
  [number]
  (let [nums [3 5 7] texts ["fizz" "buzz" "tazz"]]
    (->> nums
         (map #(mod number %))
         (map get (map #(hash-map 0 %) texts))
         (#(or (not-empty (apply str %)) (str number))))))


(doseq [x (range 1 121)]
  (println x ":" (fizzbuzz6 x)))
