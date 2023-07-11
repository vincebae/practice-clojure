#!/usr/bin/env bb

(ns progress
  (:require
    [clojure.core.async :refer [thread]]))


(defn print-slowly
  [times pause-in-ms ch]
  ; (thread
    (loop [remaining times]
      (when (pos? remaining)
        (print ch)
        (flush)
        (Thread/sleep pause-in-ms)
        (recur (dec remaining)))))


(let [print-50 (partial print-slowly 50 100)]
  (doseq [ch [\. \- \+ \*]]
    (print-50 ch)
    (print \return))
  (println "\nDone."))
