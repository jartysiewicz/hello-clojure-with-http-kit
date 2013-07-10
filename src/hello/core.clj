(ns hello.core
  (:use [org.httpkit.server :only [run-server]]
        [compojure.core :only [defroutes GET POST]]
        [compojure.handler :only [site]]
        [clojure.tools.cli :only [cli]])
  (:require [ring.middleware.reload :as reload]
            [compojure.route :as route]
            [clojure.tools.logging :as log])
  (:import java.net.URL)
  (:gen-class))


(defroutes all-routes
  (GET "/" [] "Hello World!")
  (route/not-found "<p>Page not found.</p>")) ;; all other, return 404

(def app
  (site all-routes))

;; (def in-dev? true)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (let [[options args banner]
        (cli args
             ["-h" "--help" "Show Help" :default false :flag true]
             ["-p" "--port" "Port to listen to" :default 3000 :parse-fn #(Integer. %)]
          ;; ["-r" "--repl" "nREPL port to listen to" :default 7888 :parse-fn #(Integer. %)]
             ["-d" "--development" "Run server in development mode" :default false :flag true])]
        (defonce in-dev? (:development options))
        (when (:help options)
          (println banner)
          (System/exit 0))
        (log/info "Running server on port" (:port options) "with development mode" (:development options))
        ;; (defonce in-repl? (:repl options))
        ;; (defonce repl-server (repl/start-server :port (:repl options)))
        (let [handler (if in-dev?
                        (reload/wrap-reload (site #'all-routes)) ;; only reload when dev
                        (site all-routes))
              _ (run-server handler {:port (:port options)})])))