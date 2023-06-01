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


(deftest test-peg-neighbors
  (let
    [test-fn
     #(and
        (is (= (peg-up-left %1) %3))
        (is (= (peg-up-right %1) %4))
        (is (= (peg-left %1) %5))
        (is (= (peg-right %1) %6))
        (is (= (peg-down-left %1 %2) %7))
        (is (= (peg-down-right %1 %2) %8)))]
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
