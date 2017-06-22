(ns bluebell.latex.utils)

(defn standalone [header body]
  [[:documentclass "standalone"] ;; tlmgr install standalone
   header
   [:begin "document" :body body]])
