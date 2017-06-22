(ns bluebell.latex.example
  (:require [bluebell.latex.core :as latex]
            [bluebell.latex.io-utils :as io-utils]))

(def testdoc [[:documentclass :opt "a4paper" "article"]
              [:title "Mjaoelimjao"]
              [:begin "document"
               :body
               [:maketitle]
               ["A line here\n" "Another line"]]])

(def compiled (io-utils/display testdoc))
