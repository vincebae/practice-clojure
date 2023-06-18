#!/usr/bin/env bb

(ns pegboard.board
  (:gen-class))

(def num-pegs
  (memoize
   (fn [num-rows]
     (/ (* num-rows (inc num-rows)) 2))))

(def peg-row
  (memoize
   (fn [peg-num]
     (loop [row 1]
       (if (<= peg-num (num-pegs row))
         row
         (recur (inc row)))))))

(def peg-column
  (memoize
   (fn [peg-num]
     (loop [row 1 remaining peg-num]
       (if (<= peg-num (num-pegs row))
         remaining
         (recur (inc row) (- remaining row)))))))

(def find-peg
  (memoize
   (fn [row column]
     (if (or (< row 1) (< column 1) (> column row))
       nil
       (+ (num-pegs (dec row)) column)))))

(def peg-up-left
  (memoize
   (fn [peg-num]
     (when (boolean peg-num)
       (find-peg (dec (peg-row peg-num)) (dec (peg-column peg-num)))))))

(def peg-up-right
  (memoize
   (fn [peg-num]
     (when (boolean peg-num)
       (find-peg (dec (peg-row peg-num)) (peg-column peg-num))))))

(def peg-left
  (memoize
   (fn [peg-num]
     (when (boolean peg-num)
       (find-peg (peg-row peg-num) (dec (peg-column peg-num)))))))

(def peg-right
  (memoize
   (fn [peg-num]
     (when (boolean peg-num)
       (find-peg (peg-row peg-num) (inc (peg-column peg-num)))))))

(def peg-down-left
  (memoize
   (fn [peg-num num-rows]
     (when (boolean peg-num)
       (let [row (inc (peg-row peg-num)) column (peg-column peg-num)]
         (if (> row num-rows) nil (find-peg row column)))))))

(def peg-down-right
  (memoize
   (fn [peg-num num-rows]
     (when (boolean peg-num)
       (let [row (inc (peg-row peg-num)) column (inc (peg-column peg-num))]
         (if (> row num-rows) nil (find-peg row column)))))))

(def find-connection
  (memoize
   (fn [neighbor-fn peg-num]
     (let* [neighbor (neighbor-fn peg-num) connection (neighbor-fn neighbor)]
           (and neighbor connection [neighbor connection])))))

(def find-connections
  (memoize
   (fn [peg-num num-rows]
     (let [neighbor-funs [peg-up-left
                          peg-up-right
                          peg-left
                          peg-right
                          #(peg-down-right % num-rows)
                          #(peg-down-left % num-rows)]]
       (filter boolean (map #(find-connection % peg-num) neighbor-funs))))))

(def create-peg
  (memoize
   (fn [peg-num num-rows]
     {:row (peg-row peg-num)
      :column (peg-column peg-num)
      :pegged true
      :connections (into {} (find-connections peg-num num-rows))})))

(def create-pegs
  (memoize
   (fn [num-rows]
     (loop [pegs {} peg-num 1]
       (if (> peg-num (num-pegs num-rows))
         pegs
         (recur (assoc pegs peg-num (create-peg peg-num num-rows)) (inc peg-num)))))))

(println (str (create-pegs 5)))
(println (map #(str % "\n") (create-pegs 5)))

;;
;;
;;
;;   (into {} (map (create-peg %  (range 1 (inc num-pegs)))))
;;
;; (defn create-board
;;   [num-rows]
;;     {:pegs (create-pegs num-rows)
;;      :count (num-pegs num-rows)
;;      :rows num-rows})
;;
;;
;; (defn show-board-plain
;;   [[x & xs]]
;;   (println x)
;;   (when (not-empty xs) (recur xs)))
;;
;;
;; (defn show-padding
;;   [rows columns]
;;   (loop [remaining (* (- rows columns) 2)]
;;     (when (> remaining 0) (print " ") (recur (dec remaining)))))
;;
;;
;; (defn show-row
;;   [row]
;;   (loop [remaining row]
;;     (when (not-empty remaining)
;;       (let [peg (first remaining)]
;;         (print (format "[%2d%s]"
;;                        (peg 0)
;;                        (if (:pegged (peg 1)) "*" "."))))
;;       (recur (rest remaining))))
;;   (println ""))
;;
;;
;; (defn show-board
;;   [board]
;;   (loop [start 1 columns 1]
;;     (when (<= start (:count board))
;;       (show-padding (:rows board) columns)
;;       (show-row (map #(vector % ((:pegs board) %)) (range start (+ start columns))))
;;       (recur (+ start columns) (inc columns)))))
;;
;;
;; (def num-rows 5)
;; (def pegs-board (create-board num-rows))
;; (show-board pegs-board)
;; (print (str pegs-board))
