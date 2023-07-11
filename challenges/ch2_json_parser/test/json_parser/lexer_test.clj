(ns json-parser.lexer-test
  (:require
   [clojure.test :refer [deftest is]]
   [json-parser.lexer :refer [tokenize]]
   [json-parser.test-utils :refer [expand-test-cases testing-is-equal]]))

(defmacro testing-tokenize-error
  [desc & cases]
  (list `expand-test-cases desc cases
        (fn [{input# :input error# :error}]
          `(is (and (= (first (last ~input#)) :error)
                    (= (second (last ~input#)) ~error#))))))

(deftest tokenize-tests-step1
  (testing-is-equal
   "tokenize test cases for step1"
   {:actual (tokenize "")
    :expect []}
   {:actual (tokenize " ")
    :expect []}
   {:actual (tokenize "\n")
    :expect []}
   {:actual (tokenize "{}")
    :expect [[:open-curly] [:close-curly]]}
   {:actual (tokenize " {\n} ")
    :expect [[:open-curly] [:close-curly]]}))

(deftest tokenize-tests-step1-error-cases
  (testing-tokenize-error
   "tokenize error test cases for step1"
   {:desc "string should start with either quote or double quote"
    :input (tokenize "a")
    :error "invalid character: a in normal-state"}))

(deftest tokenize-tests-step2
  (testing-is-equal
   "tokenize test cases for step2"
   {:desc "key and string value pair both with double quotes"
    :actual (tokenize "{\"key\": \"value\"}")
    :expect [[:open-curly] [:string "key"] [:colon] [:string "value"] [:close-curly]]}
   {:desc "key and string value pair both with single quotes"
    :actual (tokenize "{'key': 'value'}")
    :expect [[:open-curly] [:string "key"] [:colon] [:string "value"] [:close-curly]]}
   {:desc "key and string value pair with mixed single and double quotes"
    :actual (tokenize "{'key': \"value\"}")
    :expect [[:open-curly] [:string "key"] [:colon] [:string "value"] [:close-curly]]}
   {:desc "multiple key and string value pairs delimited by comma"
    :actual (tokenize "{'key1': 'value1', 'key2': 'value2'}")
    :expect [[:open-curly] [:string "key1"] [:colon] [:string "value1"]
             [:comma] [:string "key2"] [:colon] [:string "value2"]
             [:close-curly]]}))
