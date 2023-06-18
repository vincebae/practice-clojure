(ns pegboard.board-test
  (:require
   [clojure.test :refer :all]
   [pegboard.board :refer :all]))

(deftest test-num-pegs
  (let [test-fn #(is (= (num-pegs %1) %2))]
    (testing "get total number of pegs for row"
      (test-fn 1 1)
      (test-fn 2 3)
      (test-fn 3 6)
      (test-fn 4 10)
      (test-fn 5 15)
      (test-fn 6 21))))

(deftest test-peg-row
  (let [test-fn #(is (= (peg-row %1) %2))]
    (testing "get row for peg"
      (test-fn 1 1)
      (test-fn 2 2)
      (test-fn 3 2)
      (test-fn 4 3)
      (test-fn 5 3)
      (test-fn 6 3)
      (test-fn 7 4)
      (test-fn 11 5)
      (test-fn 15 5))))

(deftest test-peg-column
  (let [test-fn #(is (= (peg-column %1) %2))]
    (testing "get column for peg"
      (test-fn 1 1)
      (test-fn 2 1)
      (test-fn 3 2)
      (test-fn 4 1)
      (test-fn 5 2)
      (test-fn 6 3)
      (test-fn 7 1)
      (test-fn 11 1)
      (test-fn 15 5))))

(deftest test-find-peg
  (let [test-fn #(is (= (find-peg %1 %2) %3))]
    (testing "find peg num from row and column"
      (test-fn 0 0 nil)
      (test-fn 1 0 nil)
      (test-fn 1 2 nil)
      (test-fn 1 1 1)
      (test-fn 2 1 2)
      (test-fn 2 2 3)
      (test-fn 3 1 4)
      (test-fn 3 3 6)
      (test-fn 4 1 7)
      (test-fn 4 4 10)
      (test-fn 5 1 11)
      (test-fn 5 5 15))))

(deftest test-peg-neighbors
  (let
   [test-fn
    (fn [peg-num num-rows up-left up-right left right down-left down-right]
      (and
       (is (= (peg-up-left peg-num) up-left))
       (is (= (peg-up-right peg-num) up-right))
       (is (= (peg-left peg-num) left))
       (is (= (peg-right peg-num) right))
       (is (= (peg-down-left peg-num num-rows) down-left))
       (is (= (peg-down-right peg-num num-rows) down-right))))]
    (testing "get neighbors for peg"
      (test-fn 1 1 nil nil nil nil nil nil)
      (test-fn 1 5 nil nil nil nil 2 3)
      (test-fn 2 2 nil 1 nil 3 nil nil)
      (test-fn 2 5 nil 1 nil 3 4 5)
      (test-fn 3 2 1 nil 2 nil nil nil)
      (test-fn 3 5 1 nil 2 nil 5 6)
      (test-fn 5 5 2 3 4 6 8 9)
      (test-fn 11 5 nil 7 nil 12 nil nil)
      (test-fn 15 5 10 nil 14 nil nil nil))))

;; (deftest test-create-peg
;;   (let [test-fn #(is (= (create-peg %1 %2) %3))]
;;     (testing "create peg for peg number and total rows"
;;       (test-fn 1 5 [1 {:row 1 :column 1 :pegged true :connections ""}])
;;       (test-fn 2 5 [2 {:row 2 :column 1 :pegged true :connections ""}])
;;       (test-fn 3 5 [3 {:row 2 :column 2 :pegged true :connections ""}])
;;       (test-fn 4 5 [4 {:row 3 :column 1 :pegged true :connections ""}])
;;       (test-fn 5 5 [5 {:row 3 :column 2 :pegged true :connections ""}])
;;       (test-fn 6 5 [6 {:row 3 :column 3 :pegged true :connections ""}])
;;       (test-fn 7 5 [7 {:row 4 :column 1 :pegged true :connections ""}])
;;       (test-fn 11 5 [11 {:row 5 :column 1 :pegged true :connections ""}])
;;       (test-fn 15 5 [15 {:row 5 :column 5 :pegged true :connections ""}]))))
