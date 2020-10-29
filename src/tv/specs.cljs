(ns tv.specs
  (:require [cljs.spec.alpha :as spec]
            [clojure.string :as string]
            [tv.moment :as moment]))

;;; General

(spec/def ::date-time moment/moment?)
(spec/def ::non-empty-string (complement string/blank?))

;;; TV Show

(spec/def :tv.show/description (spec/nilable string?))
(spec/def :tv.show/react-key string?)
(spec/def :tv.show/start-time ::date-time)
(spec/def :tv.show/status #{:live :repeat :no-status})
(spec/def :tv.show/subtitle (spec/nilable string?))
(spec/def :tv.show/title ::non-empty-string)

(spec/def ::tv-show
  (spec/keys :req [:tv.show/description
                   :tv.show/react-key
                   :tv.show/start-time
                   :tv.show/status
                   :tv.show/subtitle
                   :tv.show/title]))

(spec/def ::tv-schedule
  (spec/coll-of ::tv-show :distinct true :min-count 1))

;;; API

(def conform-schedule (partial spec/conform ::tv-schedule))
(def explain-schedule (partial spec/explain ::tv-schedule))
(def valid-schedule? (partial spec/valid? ::tv-schedule))
