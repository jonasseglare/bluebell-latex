(ns bluebell.latex.example
  (:require [bluebell.latex.core :as latex]
            [bluebell.latex.io-utils :as io-utils]))

(defn tikz-demo []
  (io-utils/display-silent
   (latex/standalone
    (latex/usepackage "tikz")
    (latex/block {:name "tikzpicture"}
                 (latex/cmd "draw" (latex/sq "style=dashed") " (2, .5) circle (0.5);")
                 (latex/cmd "draw" (latex/sq "fill=green!50") " (1, 1) ellipse (.5 and 1);")))))
;; begin{tikzpicture}
;; \draw[style=dashed] (2,.5) circle (0.5);
;; \draw[fill=green!50] (1,1)
;; ellipse (.5 and 1);
;; \draw[fill=blue] (0,0) rectangle (1,1);
;; \draw[style=thick]
;; (3,.5) -- +(30:1) arc(30:80:1) -- cycle;
;; \end{tikzpicture}
