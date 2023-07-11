#!/usr/bin/env bb

;; Lazy quick sort from Joy of Clojure.

(ns quicksort
  [:require [clojure.test :refer [deftest is testing]]])

(defn sort-parts
  [work]
  (lazy-seq
   (loop [[part & parts] work]
     (if-let [[pivot & xs] (seq part)]
       (let [smaller? #(< % pivot)]
         (recur (list*
                 (filter smaller? xs)
                 pivot
                 (remove smaller? xs)
                 parts)))
       (when-let [[x & parts] parts]
         (cons x (sort-parts parts)))))))

(defn qsort
  [xs]
  (sort-parts (list xs)))

(defn qsort-simple [xs]
  (if-let [[pivot & ys] (seq xs)]
    (let [smaller? #(< % pivot)]
      (concat (qsort-simple (filter smaller? ys))
              (cons pivot (qsort-simple (remove smaller? ys)))))
    []))

(defn rand-ints
  ([n] (rand-ints n n))
  ([n m] (take n (repeatedly #(rand-int m)))))

;; (rand-ints 10 20)
(deftest quicksort-tests
  (testing "quicksort"
    (is (= (qsort [17 4 3 15 5 13 7 12 5 6])
           [3 4 5 5 6 7 12 13 15 17]))
    (is (= (qsort-simple '(17 4 3 15 5 13 7 12 5 6))
           [3 4 5 5 6 7 12 13 15 17]))))

;; [[17 4 3 15 5 13 7 12 5 6]]
;; part = [17 4 3 15 5 13 7 12 5 6], parts = nil
;; pivot = 17 xs = [4 3 15 5 13 7 12 5 6], parts = nil
;; (recur [[4 3 5 5 6] 7 [15 13 12]])
;; part = [4 3 5 5 6], parts = [7 [15 13 12]]
;; pivot = 4, xs = [3 5 5 6], parts = [7 [15 13 12]]
;; (recur [[3] 4 [5 5 6] 7 [15 13 12]])
;; part = [3], parts = [4 [5 5 6] 7 [15 13 12]]
;; pivot = 3, xs = nil, parts = [4 [5 5 6] 7 [15 13 12]]
;; (recur [[] 3 [] 4 [5 5 6] 7 [15 13 12]])
;; part = [], parts = [3 [] 4 [5 5 6] 7 [15 13 12]]
;; (cons 3 (sort-parts [[] 4 [5 5 6] 7 [15 13 12]]))
;; (cons 3 (cons 4 (sort-parts [5 5 6] 7 [15 13 12])))


