(ns bluebell.latex.tikz
  (:require [bluebell.latex.core :as latex]
            [bluebell.latex.utils :as utils]))

(defn tikz-graph [& args]
  (utils/standalone
   [[:usepackage [::latex/arg "tikz"]]
    [:usepackage [::latex/arg "pgfplots"]]
    [:usetikzlibrary [::latex/arg "pgfplots.dateplot,"]]]
   [::latex/block :tikzpicture
    (or args [])]))
