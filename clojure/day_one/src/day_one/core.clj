(ns day-one.core
  (:gen-class))

(require '[clojure.java.io :as io]
         '[clojure.string :as str])

(defn split-input [streeng] (str/split streeng #"\s+"))
(defn str->int [streeng] (Integer/parseInt streeng))
(defn into-ints [streengs] (map str->int streengs))

(defn part-one [ints]
  (->> ints
       (partition 2 1)
       (filter #(> (nth % 1) (nth % 0)))
       (count)))
(defn part-two [ints]
  (->> ints
       (partition 3 1)
       (map #(reduce + %))
       (part-one)))

(defn get-ints [args]
  (->> (nth args 0)
       (slurp)
       (split-input)
       (into-ints)))
(defn file-exists [filename] (.exists (io/as-file filename)))

(defn -main [& args]
  (if (and (= (count args) 1) (file-exists (nth args 0)))
    (let [ints (get-ints args)]
      (println "Part 1 result: " (part-one ints))
      (println "Part 2 result: " (part-two ints)))

    (println "Need exactly ONE argument: an existing file name.")))
