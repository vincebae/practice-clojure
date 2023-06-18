(ns wc.int-test
  (:require
   [clojure.java.shell :as sh]
   [clojure.test :refer [deftest is testing]]))

(defmacro testing-wc-output
  [description & testcases]
  (let*
   [expand (fn [{:keys [params expect]}]
             `(is (= (:out (sh/sh "bash" "-c" (str "./wc.clj " ~params)))
                     ~expect)))
    expanded (map expand testcases)]
   `(testing ~description ~@expanded)))

(deftest main-tests
  (testing-wc-output
   "main-tests"
   {:params "test/wc/testfiles/empty.txt"
    :expect "0 0 0 test/wc/testfiles/empty.txt\n"}
   {:params "test/wc/testfiles/space.txt"
    :expect "1 0 2 test/wc/testfiles/space.txt\n"}
   {:params "test/wc/testfiles/test_file*"
    :expect (str "2 3 18 test/wc/testfiles/test_file1.txt\n"
                 "1 3 21 test/wc/testfiles/test_file2.txt\n"
                 "4 7 42 test/wc/testfiles/test_file3.txt\n")}
   ;; Output order should be words and bytes.
   {:params "-c -w test/wc/testfiles/test_file1.txt"
    :expect "3 28 test/wc/testfiles/test_file1.txt\n"}
   ;; Output order should be lines and chars.
   {:params "--chars --lines test/wc/testfiles/test_file1.txt"
    :expect "2 18 test/wc/testfiles/test_file1.txt\n"}
   ;; Output order should be lines words chars and bytes.
   {:params "--bytes -m --words -l test/wc/testfiles/test_file1.txt"
    :expect "2 3 18 28 test/wc/testfiles/test_file1.txt\n"}
   ;; No file found
   {:params "invalid_file"
    :expect "invalid_file (No such file or directory)\n"}))
