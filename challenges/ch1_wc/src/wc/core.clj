#!/usr/bin/env bb

;; Coding challenge for wc tool.
;; https://codingchallenges.fyi/challenges/challenge-wc
;; Written for bb script without cli library

(ns wc.core
  (:require
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

(def default-opts [:lines :words :chars])
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

(defn get-result-text
  [results filename]
  (s/join " " (conj results filename)))

(defn read-file-to-text
  [filename]
  (try
    {:result (slurp filename)}
    (catch Exception e
      {:error (.getMessage e)})))

(defn wc-by-filename
  [filename show-opts]
  (let* [read-result (read-file-to-text filename)
         text (:result read-result)]
        (if text
          (get-result-text (get-results text show-opts) filename)
          (:error read-result))))

(defn -main
  [args]
  (let* [arg-map (parse-args args)
         show-opts (get arg-map :show default-opts)]
        (doseq [filename (:filename arg-map)]
          (println (wc-by-filename filename show-opts)))))
