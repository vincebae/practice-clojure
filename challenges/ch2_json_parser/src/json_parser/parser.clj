;; https://codingchallenges.fyi/challenges/challenge-json-parser/

(ns json-parser.parser
  (:require
   [json-parser.lexer :refer [tokenize]]
   [json-parser.utils :refer [error]]))

(defn- build-map
  "Build clojure map from tokens from json string"
  [tokens]
  (letfn
   [(start-state
      [curr [token & tokens]]
      (case (first token)
        nil nil
        :error token
        :open-curly (read-pair-start-state curr tokens)
        (error "invalid token: " token, " in start-state")))

    (read-pair-start-state
      [curr [token & tokens]]
      (case (first token)
        nil (error "parse ended in invalid state: read-pair-start-state")
        :error token
        :close-curly (or curr {})
        :string (let [[colon value & rest-tokens] tokens]
                  (if (and (= (first colon) :colon) (= (first value) :string))
                    (read-pair-end-state
                     (assoc curr (keyword (second token)) (second value))
                     rest-tokens)
                    (error "invalid token: " colon value " in read-pair-start-state")))
        (error "invalid token: " token " in read-pair-start-state")))

    (read-pair-end-state
      [curr [token & tokens]]
      (case (first token)
        nil (error "parse ended in invalid state: read-pair-end-state")
        :error token
        :comma (if (= (first (first tokens)) :close-curly)
                 (error "invalid token: " (first tokens)
                        " after " token " in read-pair-end-state")
                 (read-pair-start-state curr tokens))
        :close-curly curr
        (error "invalid token: " token " in read-pair-end-state")))]

    ;; starts the state machine recursively using trampoline.
    (trampoline start-state nil (seq tokens))))

(defn parse
  "Parse json text and build clojure map"
  [json-text]
  (->> json-text
       tokenize
       build-map))
