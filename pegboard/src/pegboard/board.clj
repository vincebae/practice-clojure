#!/usr/bin/env bb

(ns pegboard.board
  (:gen-class))

(def max-rows 10)

(def num-pegs
  (memoize
   (fn [num-rows]
     (/ (* num-rows (inc num-rows)) 2))))

(def pegs
  ((fn [num-rows]
     (loop [peg-num (num-pegs num-rows) row num-rows column num-rows acc {}]
       (if (= peg-num 0)
         acc
         (recur
          (dec peg-num) ; peg-num
          (if (= column 1) (dec row) row) ; row
          (if (= column 1) (dec row) (dec column)) ; column
          (assoc acc peg-num [row column] [row column] peg-num))))) ; acc
   max-rows))

(defn peg-row [peg-num] (nth (pegs peg-num) 0))

(defn peg-column [peg-num] (nth (pegs peg-num) 1))

(defn find-peg [row column] (pegs [row column]))

(def peg-up-left
  (memoize
   (fn [peg-num]
     (when (boolean peg-num)
       (pegs [(dec (peg-row peg-num)) (dec (peg-column peg-num))])))))

(def peg-up-right
  (memoize
   (fn [peg-num]
     (when (boolean peg-num)
       (pegs [(dec (peg-row peg-num)) (peg-column peg-num)])))))


(def peg-left
  (memoize
   (fn [peg-num]
     (when (boolean peg-num)
       (pegs [(peg-row peg-num) (dec (peg-column peg-num))])))))

(def peg-right
  (memoize
   (fn [peg-num]
     (when (boolean peg-num)
       (pegs [(peg-row peg-num) (inc (peg-column peg-num))])))))

(def peg-down-left
  (memoize
   (fn [peg-num num-rows]
     (when (boolean peg-num)
       (let [row (inc (peg-row peg-num)) column (peg-column peg-num)]
         (if (> row num-rows) nil (pegs [row column])))))))

(def peg-down-right
  (memoize
   (fn [peg-num num-rows]
     (when (boolean peg-num)
       (let [row (inc (peg-row peg-num)) column (inc (peg-column peg-num))]
         (if (> row num-rows) nil (pegs [row column])))))))

(defn create-connection
  [peg-num num-rows]
  (let
   [total-pegs (num-pegs num-rows) row (peg-row peg-num) column (peg-column peg-num)]

    ""))

(defn create-peg
  [peg-num num-rows]
  (vector
   peg-num
   {:row (peg-row peg-num)
    :column (peg-column peg-num)
    :pegged true
    :connections (create-connection peg-num num-rows)}))

;;
;; (defn create-pegs
;;   [num-rows]
;;   (loop [pegs {} start 1 columns 1]
;;     (if (<= num-rows columns)
;;       (map #(assoc pegs % create-peg(% )
;;       pegs)))
;;
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
