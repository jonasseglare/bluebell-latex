(ns bluebell.latex.io-utils
  (:import [java.awt Desktop])
  (:require [bluebell.latex.core :as latex]
            [clojure.java.io :as io]
            [bluebell.tag.core :as tag]
            [clojure.java.shell :as shell]))

(defn with-suffix [basename suf]
  (str basename suf))

(defn full-name [settings suf]
  (with-suffix
    (io/file (:dir settings) (:name settings))
    suf))

(defn local-name [settings suf]
  (with-suffix (:name settings) suf))

(defn compile-latex [settings]
  {:output-data (shell/sh "pdflatex" (local-name settings ".tex")
                          :dir (:dir settings))
   :output-filename (full-name settings ".pdf")})

(def settings {:name "tmplatexfile"
               :dir "/tmp/"
               :compile-latex compile-latex})

(defn perform-compilation [settings]
  ((:compile-latex settings) settings))

(defn to-pdf
  ([code settings]
   (do
     (spit (full-name settings ".tex")
           (latex/render code))
     (perform-compilation settings)))
  ([code]
   (to-pdf code settings)))

(defn display
  ([code settings]
   (let [output (to-pdf code settings)]
     (if (= 0 (-> output :output-data :exit))
       (do (.open (Desktop/getDesktop) (io/file (full-name settings ".pdf")))
           (tag/tag-success output))
       (tag/tag-failure output))))
  ([code]
   (display code settings)))

(defn display-silent [& args]
  (let [r (apply display args)]
    (if (tag/failure? r)
      r)))
