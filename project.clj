(defproject motive "0.2.0-SNAPSHOT"
  :description "Tools for algorithmic music generation."
  :license {:name "The I Haven't Got Around To This Yet License"
            :url  "https://not-even-a-404.com"
            :comment "Based loosely upon YAPL (Yet Another Parody License)"}
  :url "https://github.com/thomasathorne/motive"
  :dependencies [[com.rpl/specter "0.9.3"]]
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.7.0"]
                                       [overtone "0.10.1"]
                                       [org.clojure/clojurescript "0.0-3211"]]
                        :jvm-opts ^:replace []}})
