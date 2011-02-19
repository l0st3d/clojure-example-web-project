(ns infrastructure.boot-dev-env
  (:require
   [clojure.contrib.logging :as log]
   [infrastructure.boot :as boot]
   [infrastructure.test-runner :as test-runner]))

(boot/-main)

(test-runner/load-all-test-namespaces)
