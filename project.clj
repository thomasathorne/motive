(defproject motive "0.1.0-SNAPSHOT"
  :description "Stochastic generators for music programming."
  :license ""
  :url "https://github.com/thomasathorne/motive"
  :dependencies [[com.rpl/specter "0.9.3"]]
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.7.0"]
                                       [overtone "0.10.1"]
                                       [org.clojure/clojurescript "0.0-3211"]]
                        :jvm-opts ^:replace []}})
