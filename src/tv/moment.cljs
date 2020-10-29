(ns tv.moment
  (:require ["moment" :as moment-js]))

(defn moment [s]
  (moment-js s "YYYY-MM-DD HH:mm:ss" true))

(defn moment? [s]
  (some-> (moment s)
          (.isValid)))

(defn time-string [s]
  (some-> (moment s)
          (.format "HH:mm")))

(defn timestamp [s]
  (some-> (moment s)
          (.unix)))
