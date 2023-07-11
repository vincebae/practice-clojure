(ns json-parser.parser-test
  (:require
   [clojure.test :refer [deftest is]]
   [json-parser.parser :refer [parse]]
   [json-parser.test-utils :refer [expand-test-cases testing-is-equal]]))

(defmacro testing-parse-error
  [desc & cases]
  (list `expand-test-cases desc cases
        (fn [{input# :input error# :error}]
          `(is (and (= (first ~input#) :error)
                    (= (second ~input#) ~error#))))))

(deftest parse-tests-step1
  (testing-is-equal
   "parse test cases for step1"
   {:actual (parse "{}")
    :expect {}}
   {:actual (parse "")
    :expect nil}))

(deftest parse-tests-step1-error-cases
  (testing-parse-error
   "parse error test cases for step1"
   {:desc "curly bracket unclosed"
    :input (parse "{")
    :error "parse ended in invalid state: read-pair-start-state"}
   {:desc "curly bracket closed without open"
    :input (parse "}")
    :error "invalid token: [:close-curly] in start-state"}
   {:desc "curly bracket opened twice"
    :input (parse "{{")
    :error "invalid token: [:open-curly] in read-pair-start-state"}))

(deftest parse-tests-step2
  (testing-is-equal
   "parse test cases for step2"
   {:desc "key and string value pair both with double quotes"
    :actual (parse "{\"key\": \"value\"}")
    :expect {:key "value"}}
   {:desc "key and string value pair both with single quotes"
    :actual (parse "{'key': 'value'}")
    :expect {:key "value"}}
   {:desc "key and string value pair with mixed single and double quotes"
    :actual (parse "{'key': \"value\"}")
    :expect {:key "value"}}
   {:desc "multiple key and string value pairs delimited by comma"
    :actual (parse "{'key1': 'value1', 'key2': 'value2'}")
    :expect {:key1 "value1", :key2 "value2"}}))

(deftest parse-tests-step2-error-cases
  (testing-parse-error
   "parse error test cases for step2"
   {:desc "missing key before colon"
    :input (parse "{ : \"value\" }")
    :error "invalid token: [:colon] in read-pair-start-state"}
   {:desc "missing value after colon"
    :input (parse "{\"key\" : }")
    :error "invalid token: [:colon][:close-curly] in read-pair-start-state"}
   {:desc "missing colon between key value pair"
    :input (parse "{\"key\" \"value\"}")
    :error "invalid token: [:string \"value\"][:close-curly] in read-pair-start-state"}
   {:desc "trailing comma"
    :input (parse "{'key1': 'value1',}")
    :error "invalid token: [:close-curly] after [:comma] in read-pair-end-state"}
   {:desc "missing comma between pairs"
    :input (parse "{'key1' : 'value1' 'key2' : 'value2'}")
    :error "invalid token: [:string \"key2\"] in read-pair-end-state"}))
