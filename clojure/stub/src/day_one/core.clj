(ns day-one.core
  (:gen-class))

(require '[clojure.java.io :as io])

(defn part-one [input] 0)
(defn part-two [input] 0)

(defn file-contents [args] (slurp (nth args 0)))
(defn file-exists [filename] (.exists (io/as-file filename)))

(defn -main [& args]
  (if (and (= (count args) 1) (file-exists (nth args 0)))
    (let [contents (file-contents args)]
      (println "Part 1 result: " (part-one contents))
      (println "Part 2 result: " (part-two contents)))

    (println "Need exactly ONE argument: an existing file name.")))
