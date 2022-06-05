(ns day-five.core
  (:gen-class))

(require '[clojure.java.io :as io])

(defn split-input [streeng] (re-seq #"(\d+),(\d+) -> (\d+),(\d+)" streeng))
(defn str->int [streeng] (Integer/parseInt streeng))
(defn into-line [rgx] [(str->int (nth rgx 1)) (str->int (nth rgx 2)) (str->int (nth rgx 3)) (str->int (nth rgx 4))])
(defn x1 [line] (nth line 0))
(defn y1 [line] (nth line 1))
(defn x2 [line] (nth line 2))
(defn y2 [line] (nth line 3))
(defn count-more-than-two [field]
  (->> field
       flatten
       (filter #(>= % 2))
       count))
(defn xs [lines]
  (->> lines
       (filter #(vector (x1 %) (x2 %)))
       flatten))
(defn ys [lines]
  (->> lines
       (filter #(vector (y1 %) (y2 %)))
       flatten))
(defn max-x [lines] (apply max (xs lines)))
(defn max-y [lines] (apply max (ys lines)))
(defn generate-field [lines]
  (let [max-x (max-x lines)
        max-y (max-y lines)]
   (into [] (repeat (inc max-y) (into [] (repeat (inc max-x) 0))))))
(defn get-field-at [field x y]
  (nth (nth field y) x))
(defn set-field-at [field x y new-value]
  (assoc field y (assoc (nth field y) x new-value)))


(defn rvl-detail [line field inc-func]
  (loop [current-x (x1 line)
         current-y (y1 line)
         current-field field]
    (if (= current-y (y2 line))
      (set-field-at current-field (x2 line) (y2 line) (inc (get-field-at current-field (x2 line) (y2 line))))
      (recur current-x (inc-func current-y) (set-field-at current-field current-x current-y (inc (get-field-at current-field current-x current-y)))))))
(defn run-vertical-line [line field]
  (cond
    (> (y2 line) (y1 line)) (rvl-detail line field inc)
    (> (y1 line) (y2 line)) (rvl-detail line field dec)))
(defn rhl-detail [line field inc-func]
  (loop [current-x (x1 line)
         current-y (y1 line)
         current-field field]
    (if (= current-x (x2 line))
      (set-field-at current-field (x2 line) (y2 line) (inc (get-field-at current-field (x2 line) (y2 line))))
      (recur (inc-func current-x) current-y (set-field-at current-field current-x current-y (inc (get-field-at current-field current-x current-y)))))))
(defn run-horizontal-line [line field]
  (cond
    (> (x2 line) (x1 line)) (rhl-detail line field inc)
    (> (x1 line) (x2 line)) (rhl-detail line field dec)))
(defn diag-detail [line field inc-x inc-y]
    (loop [current-x (x1 line)
         current-y (y1 line)
         current-field field]
    (if (and (= current-x (x2 line)) (= current-y (y2 line)))
      (set-field-at current-field (x2 line) (y2 line) (inc (get-field-at current-field (x2 line) (y2 line))))
      (recur (inc-x current-x) (inc-y current-y) (set-field-at current-field current-x current-y (inc (get-field-at current-field current-x current-y)))))))
(defn run-diagonal-line [line field]
  (cond
    (and (> (x2 line) (x1 line)) (> (y2 line) (y1 line))) (diag-detail line field inc inc)
    (and (> (x2 line) (x1 line)) (> (y1 line) (y2 line))) (diag-detail line field inc dec)
    (and (> (x1 line) (x2 line)) (> (y2 line) (y1 line))) (diag-detail line field dec inc)
    (and (> (x1 line) (x2 line)) (> (y1 line) (y2 line))) (diag-detail line field dec dec)))


(defn run-line [line field]
  (cond
    (= (x1 line) (x2 line)) (run-vertical-line line field)
    (= (y1 line) (y2 line)) (run-horizontal-line line field)
    :else (throw (Throwable. "run-line from part 1 does not support diagonal lines."))))
(defn run-line-v2 [line field]
  (cond
    (= (x1 line) (x2 line)) (run-vertical-line line field)
    (= (y1 line) (y2 line)) (run-horizontal-line line field)
    :else (run-diagonal-line line field)))

(defn part-one-lines [lines] (filter #(or (= (x1 %) (x2 %)) (= (y1 %) (y2 %))) lines))
(defn run-lines-func [lines field line-func]
  (loop [deez-lines lines
         field field]
    (if (empty? deez-lines)
      (count-more-than-two field)
      (recur (drop 1 deez-lines) (line-func (first deez-lines) field)))))
(defn part-one [lines field] (run-lines-func (part-one-lines lines) field run-line))
(defn part-two [lines field] (run-lines-func lines field run-line-v2))

(defn get-lines [args]
  (->> (nth args 0)
       (slurp)
       (split-input)
       (map into-line)))
(defn file-exists [filename] (.exists (io/as-file filename)))

(defn -main [& args]
  (if (and (= (count args) 1) (file-exists (nth args 0)))
    (let [lines (get-lines args)
          field (generate-field lines)]
      (println "Part 1 result: " (part-one lines field))
      (println "Part 2 result: " (part-two lines field)))

    (println "Need exactly ONE argument: an existing file name.")))
