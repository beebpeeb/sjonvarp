(ns tv.moment
  (:require ["moment" :as moment-js]))

(def date-time-format "YYYY-MM-DD HH:mm:ss")

(defn moment [s]
  (moment-js s date-time-format true))

(defn moment? [s]
  (some-> (moment s)
          (.isValid)))

(defn time-string [s]
  (some-> (moment s)
          (.format "HH:mm")))

(defn timestamp [s]
  (some-> (moment s)
          (.unix)))
