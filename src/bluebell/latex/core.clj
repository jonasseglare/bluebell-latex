(ns bluebell.latex.core
  (:require [clojure.spec :as spec]
            [bluebell.utils.string :as string]
            [bluebell.utils.indent :as indent]
            [bluebell.utils.defmultiple :as defmultiple]))

(defn block [opts & forms]
  (let [all-opts (merge {:pre "" :post ""} opts)]
    [(str  "\\begin" (:pre all-opts) "{" (:name all-opts) "}" (:post all-opts))
     (indent/indent forms)
     (str "\\end{" (:name all-opts) "}")]))

(def parens {"{" "}"
             "(" ")"
             "[" "]"})

(defn embrace [left args]
  (apply str `[~left ~@args ~(get parens left)]))

(defn br [& args]
  (embrace "{" args))

(defn sq [& args]
  (embrace "[" args))

(defn cmd [name & args]
  (apply str `("\\" ~name ~@args)))

(defn cmd1
  ([name arg]
   (cmd name (br arg)))
  ([name]
   #(cmd1 name %)))

(defn usepackage [name & opts]
  (apply cmd `("usepackage" ~@opts ~(br name))))

(def mathbf (cmd1 "mathbf"))

(def emph (cmd1 "emph"))

(defn inline-math [& args]
  (apply str `("$" ~@ args "$")))

(defn standalone
  [header body]
  [(cmd "documentclass" (br "standalone")) 
   header
   (block
    {:name "document"}
    body)])









;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; how to render it
(defn render [x]
  (indent/render x))
