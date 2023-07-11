;; https://codingchallenges.fyi/challenges/challenge-json-parser/

(ns json-parser.lexer
  [:require [clojure.string :as s]
   [json-parser.utils :refer [error]]])

(def sign-char-set (into #{} "+-"))
(def number-char-set (into #{} ".0123456789"))

(defn tokenize
  "Tokenize given string into token vector using state machine."
  [input-string]
  (letfn
   [(add-ch-to-str [curr ch]
      (let [last-index (dec (count curr))]
        (->> (get-in curr [last-index 1])
             (#(str % ch))
             (assoc-in curr [last-index 1]))))

    (normal-state [curr [ch & ch-rest]]
      (cond
        (nil? ch) curr
        (or (sign-char-set ch)
            (number-char-set ch)) (number-state (conj curr [:number (str ch)]) ch-rest)
        (s/blank? (str ch)) (normal-state curr ch-rest)
        (= ch \{) (normal-state (conj curr [:open-curly]) ch-rest)
        (= ch \}) (normal-state (conj curr [:close-curly]) ch-rest)
        (= ch \:) (normal-state (conj curr [:colon]) ch-rest)
        (= ch \,) (normal-state (conj curr [:comma]) ch-rest)
        (= ch \") (str-double-state (conj curr [:string ""]) ch-rest)
        (= ch \') (str-single-state (conj curr [:string ""]) ch-rest)
        :else (conj curr (error "invalid character: " ch " in normal-state"))))

    (str-double-state [curr [ch & ch-rest]]
      (cond
        (nil? ch) (conj curr (error "tokenize ended in invalid state: str-double-state"))
        (= ch \") (normal-state curr ch-rest)
        :else (str-double-state (add-ch-to-str curr ch) ch-rest)))

    (str-single-state [curr [ch & ch-rest]]
      (cond
        (nil? ch) (conj curr (error "tokenize ended in invalid state: str-single-state"))
        (= ch \') (normal-state curr ch-rest)
        :else (str-single-state (add-ch-to-str curr ch) ch-rest)))

    (number-state [curr [ch & ch-rest]]
      (cond
        (number-char-set ch) (number-state (add-ch-to-str curr ch) ch-rest)
        :else nil))
    ]

    ;; starts the state machine recursively using trampoline.
    (trampoline normal-state [] (seq input-string))))
