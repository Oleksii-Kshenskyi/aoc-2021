(ns day-three.core
  (:gen-class))

(require '[clojure.java.io :as io]
         '[clojure.string :as str])

(defn sort-pair [pair] [(get pair \0) (get pair \1)])

(defn split-input [streeng] (str/split streeng #"\s+"))

(defn freq-row [matrix pos-in-row]
  (let [zeros (filter #(= (nth % pos-in-row) \0) matrix)
        ones (filter #(= (nth % pos-in-row) \1) matrix)]
  (vector (count zeros) (count ones))))
(defn compare-freqs [freqs end-char]
  (cond
    (= (nth freqs 0) (nth freqs 1)) :equal
    (and (= end-char \1) (> (nth freqs 1) (nth freqs 0))) :ones
    (and (= end-char \1) (> (nth freqs 0) (nth freqs 1))) :zeroes
    (and (= end-char \0) (< (nth freqs 1) (nth freqs 0))) :ones
    (and (= end-char \0) (< (nth freqs 0) (nth freqs 1))) :zeroes
    :else (throw (Throwable. (str "Freqs comparison failed: " freqs)))))

(defn filter-once [matrix filter-char pos-in-row]
  (filter #(= filter-char (nth % pos-in-row)) matrix))

(defn filter-matrix [matrix end-char pos-in-row]
  (let [freqs (compare-freqs (freq-row matrix pos-in-row) end-char)]
    (case freqs
      :equal (filter-once matrix end-char pos-in-row)
      :zeroes (filter-once matrix \0 pos-in-row)
      :ones (filter-once matrix \1 pos-in-row))))


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


(defn get-part2-value [matrix end-char]
    (loop [filter-me matrix
           search-index 0]
      (if (= (count filter-me) 1)
        (nth filter-me 0)
        (recur (filter-matrix filter-me end-char search-index) (inc search-index)))))

(defn get-oxygen-generator [matrix] (get-part2-value matrix \1))

(defn get-co2-scrubber [matrix] (get-part2-value matrix \0))


(defn part-one [matrix]
  (let [gamma (get-gamma matrix)
        gval (Integer/parseInt gamma 2)
        eval (Integer/parseInt (get-epsilon gamma) 2)]
    (* gval eval)))

(defn part-two [matrix]
  (let [scrub (Integer/parseInt (get-co2-scrubber matrix) 2)
        oxygen (Integer/parseInt (get-oxygen-generator matrix) 2)]
    (* scrub oxygen)))

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
