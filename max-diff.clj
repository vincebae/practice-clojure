#!/usr/bin/env bb

(defn max-diff1
  [data]
  (if (empty? data) -1
      (->> data
           (reductions min)
           (map - data)
           (filter (partial < 0))
           (apply max -1))))


(defn max-diff2
  [data]
  (if (empty? data) -1
      (apply max -1 (filter #(> % 0) (map - data (reductions min data))))))


(defn max-diff3
  [[x & xs]]
  (loop [[curr & remaining] xs curr-min x curr-max -1]
    (if (nil? curr)
      curr-max
      (recur remaining (min curr-min curr) (max curr-max (- curr curr-min))))))


(defn max-diff4
  [[x & xs]]
  (loop [prev x
         [curr & remaining] xs
         local-max -1
         total-max -1]
    (if (nil? curr)
      total-max
      (let [new-max (+ (max local-max 0) (- curr prev))]
        (recur curr remaining new-max (max new-max total-max))))))


(defn max-diff5
  [data]
  (if (empty? data) -1
      (->> (map - data (reductions min data))
           (filter (partial < 0))
           (apply max -1))))


(def max-diff max-diff5)


(defn run-test
  [index input expected]
  (let [actual (max-diff input)]
    (println "Test" index
             (if (= actual expected) "Success" "Failure")
             "Expected:" expected
             "Actual:" actual)))


(defn run-tests
  [test-cases]
  (loop [[curr & remaining] test-cases index 1]
    (when (some? curr)
      (run-test index (get curr 0) (get curr 1))
      (recur remaining (inc index)))))


(run-tests
  [[[1 2 3 4] 3]
   [[7 1 5 4] 4]
   [[9 6 4 2] -1]
   [[1 5 2 10] 9]
   [[] -1]])
