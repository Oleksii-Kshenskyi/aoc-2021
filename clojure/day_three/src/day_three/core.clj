(ns day-three.core
  (:gen-class))

(require '[clojure.java.io :as io]
         '[clojure.string :as str])

(defn sort-pair [pair] [(get pair \0) (get pair \1)])
(defn update-gamma [gamma pair]
  (if (> (nth pair 0) (nth pair 1))
    (str gamma \0)
    (str gamma \1)))
(defn get-freqs [matrix]
  (->> matrix
       (apply map vector)
       (map frequencies)
       (map sort-pair)
       (into [])))
(defn get-gamma [matrix]
  (let [freqs (get-freqs matrix)]
    (loop [index 0
           gamma ""]
      (if (= index (count freqs))
        gamma
        (recur (inc index) (update-gamma gamma (nth freqs index)))))))
(defn get-epsilon [gamma] (apply str (map {\0 \1 \1 \0} gamma)))

(defn split-input [streeng] (str/split streeng #"\s+"))

(defn part-one [matrix]
  (let [gamma (get-gamma matrix)
        gval (Integer/parseInt gamma 2)
        eval (Integer/parseInt (get-epsilon gamma) 2)]
    (* gval eval)))
(defn part-two [matrix] 0)

(defn get-matrix [args]
  (->> (nth args 0)
       (slurp)
       (split-input)))
(defn file-exists [filename] (.exists (io/as-file filename)))

(defn -main [& args]
  (if (and (= (count args) 1) (file-exists (nth args 0)))
    (let [matrix (get-matrix args)]
      (println "Part 1 result: " (part-one matrix))
      (println "Part 2 result: " (part-two matrix)))

    (println "Need exactly ONE argument: an existing file name.")))
