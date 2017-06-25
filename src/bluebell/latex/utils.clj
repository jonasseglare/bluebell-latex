(ns bluebell.latex.utils
  (:require [bluebell.latex.core :as latex]))

(defn standalone [header body]
  [[:documentclass [::latex/arg "standalone"]] ;; tlmgr install standalone
   header
   [::latex/block :document
    body]])
