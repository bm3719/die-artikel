(ns die-artikel.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(def dict "data.csv")

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> [:article :word]
            repeat)
       (rest csv-data)))

(defn read-file [f]
  (csv-data->maps
   (with-open [reader (io/reader (io/resource f))]
     (doall (csv/read-csv reader)))))

(defn valid-file?
  "Check an input file to ensure that it has a consistent number of fields.  If
  false, there exists one or more rows that have too few or too many fields." [f]
  (if-let [r (io/resource f)]
    (with-open [reader (io/reader r)]
      (->> (csv/read-csv reader)
           doall
           (map count)
           distinct
           first
           (= 2)))
    (throw (ex-info "Dictionary file not found" {:file f}))))

(defn next-word-article [dict]
  (nth dict (rand-int (count dict))))

(defn -main [& _]
  ;; Before attempting to parse file, ensure it's in valid form.
  (if (valid-file? dict)
    ;; Main interaction loop.
    (let [data (read-file dict)]
      (loop []
        (let [word-article (next-word-article data)]
          (println "Geben Sie den richtigen Artikel ein (oder \"q\" zum Beenden)")
          (println "___ " (:word word-article))
          (print "> ")
          (flush)
          (let [ans (read-line)]
            (if (= ans "q")
              (println "Auf Wiedersehen.")
              (do
                (if (= (:article word-article) ans)
                  (println "richtig!")
                  (println "falsch!"))
                (recur)))))))
    (println "Dictionary file format invalid.")))
