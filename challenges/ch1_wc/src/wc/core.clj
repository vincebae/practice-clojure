#!/usr/bin/env bb

;; Coding challenge for wc tool.
;; https://codingchallenges.fyi/challenges/challenge-wc
;; Written for bb script without cli library

(ns wc.core
  (:require
   [clojure.java.io :as io]
   [clojure.string :as s]))

(defn make-vector
  [value]
  (cond
    (nil? value) []
    (vector? value) value
    (coll? value) (into [] value)
    :else [value]))

(defn get-arg
  [arg-map keys]
  (let [key-vec (make-vector keys)]
    (and (seq key-vec) (reduce #(get %1 %2) arg-map key-vec))))

(defn set-arg
  [arg-map keys value]
  (let [[x & xs] (make-vector keys)]
    (cond
      (nil? x) arg-map  ; empty keys. just returns original arg-map.
      (nil? (first xs)) (assoc arg-map x value)  ; last key.
      :else (->> (arg-map x)
                 (#(if (map? %) % {}))
                 (#(set-arg % xs value))
                 (assoc arg-map x)))))

(defn add-arg
  [arg-map keys value]
  (let [key-vec (make-vector keys)]
    (if (or (empty? key-vec) (nil? value))
      arg-map
      (-> (get-arg arg-map key-vec)
          (make-vector)
          (conj value)
          (#(set-arg arg-map key-vec %))))))

(def show-opts-arg-map
  {"-c" :bytes "--bytes" :bytes,
   "-m" :chars "--chars" :chars,
   "-w" :words "--words" :words,
   "-l" :lines "--lines" :lines})

(def default-opts [:lines :words :bytes])
(def all-opts [:lines :words :chars :bytes])

(defn parse-args
  ([args] (parse-args args {}))
  ([args arg-map]
   (if-let [[x & xs] args]
     (->>
      (if (contains? show-opts-arg-map x)
        [:show (show-opts-arg-map x)]
        [:filename x])
      (apply add-arg arg-map)
      (recur xs))
     arg-map)))

(defn get-counts
  [text]
  {:bytes (count (.getBytes text))
   :chars (count text)
   :words (if (empty? text) 0 (count (s/split text #"\s+")))
   :lines (reduce #(if (= %2 \newline) (inc %1) %1) 0 text)})

(defn get-results
  [text show-opts]
  (let [counts (get-counts text)
        show-opts-ordered (filter (into #{} show-opts) all-opts)]
    (-> (map counts show-opts-ordered)
        (make-vector))))

(defn wc-by-reader
  [reader show-opts]
  (->> (slurp reader)
       (#(get-results % show-opts))
       (s/join " ")))

(defn -main
  [args]
  (let* [arg-map (parse-args args)
         show-opts (get arg-map :show default-opts)]
        (if (:filename arg-map)
          (doseq [filename (:filename arg-map)]
            (try
              (with-open [file-reader (io/reader filename)]
                (println (wc-by-reader file-reader show-opts) filename))
              (catch Exception e
                (println (.getMessage e)))))
          (println (wc-by-reader *in* show-opts)))))
