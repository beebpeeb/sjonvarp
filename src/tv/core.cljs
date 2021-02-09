(ns tv.core
  (:require [goog.dom :as dom]
            [ajax.core :as ajax]
            [citrus.core :as citrus]
            [rum.core :as rum]
            [tv.components :as components]
            [tv.show :refer [tv-show]]
            [tv.specs :refer [explain-schedule valid-schedule?]]))

(enable-console-print!)

;;; Helpers

(defn response->schedule
  "Constructs a TV schedule from the given API response, when valid, or returns nil"
  [{:keys [results]}]
  (when-some [schedule (map tv-show results)]
    (if (valid-schedule? schedule)
      schedule
      (explain-schedule schedule))))

;;; Controller

(defmulti control-schedule (fn [event] event))

(defmethod control-schedule :init []
  {:effect/http {:on-failed :fetch-failed
                 :on-ok :fetch-ok
                 :url "https://apis.is/tv/ruv"}
   :state {:error nil :schedule nil}})

(defmethod control-schedule :fetch-ok [_ [response] state]
  {:state (assoc state :schedule (response->schedule response))})

(defmethod control-schedule :fetch-failed [_ [error] state]
  {:state (assoc state :error (:status-text error))})

;;; Effects

(defn fetch! [reconciler controller {:keys [on-failed on-ok url]}]
  (ajax/GET url {:error-handler #(citrus/dispatch! reconciler controller on-failed %)
                 :handler #(citrus/dispatch! reconciler controller on-ok %)
                 :keywords? true
                 :response-format :json}))

;;; Reconciler

(defonce reconciler
  (citrus/reconciler
    {:controllers {:schedule control-schedule}
     :effect-handlers {:effect/http fetch!}
     :state (atom nil)}))

;;; Initialize

(defonce init-ctrl (citrus/broadcast-sync! reconciler :init))

(rum/mount (components/Container reconciler) (dom/getElement "main"))
