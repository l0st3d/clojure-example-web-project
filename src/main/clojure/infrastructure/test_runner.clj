(ns infrastructure.test-runner
  (:require [clojure.test.junit :as junit]
	    [clojure.contrib.logging :as log]
	    [clojure.test :as test]
	    [clojure.contrib.duck-streams :as duck]
	    [clojure.contrib.find-namespaces :as find-ns]))

(def results (atom []))

(defn report [m]
  (swap! results conj m)
  (junit/junit-report m))

(defmacro with-junit-output
  "taken and adapted from clojure.test.junit"
  [& body]
  `(test/with-test-out
     (binding [test/report report
	       junit/*var-context* (list)
	       junit/*depth* 0]
       (println "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
       (let [result# ~@body]
	 result#))))

(defn- load-and-require-namespaces [src-dir-name]
  (let [test-namespaces (filter #(not (or (= % 'infrastructure.run-unit-tests)
					  (= % 'infrastructure.run-integation-tests)))
				(find-ns/find-namespaces-in-dir (java.io.File. src-dir-name)))]
    (doall (map #(do (log/debug (str " >> requiring " %)) (require %)) test-namespaces))
    test-namespaces))

(defn run-tests-and-exit [test-type-name src-dir-name]
  (log/info (str "Running " test-type-name " tests in " src-dir-name))
  (log/info (str "in process id " (.. (java.io.File."/proc/self") getCanonicalFile getName)))
  (let [test-namespaces (load-and-require-namespaces src-dir-name)]
    (.mkdir (java.io.File. "./target/test-reports/"))
    (when-not *compile-files*
      (doseq [namespace test-namespaces]
	(let [result (binding [test/*test-out* (duck/writer (str "target/test-reports/TEST-" test-type-name "-" namespace ".xml"))]
		       (with-junit-output
			 (test/run-tests namespace)))]
	  (if (< 0 (+ (result :fail) (result :error)))
	    (log/error (str "[FAILURE] in " namespace " : " result)))))
      (Thread/sleep 500)
      (shutdown-agents)
      (log/info (str "Finished running " test-type-name " tests"))
      (if (some #(< 0 (+ (get % :fail 0)
			 (get % :error 0)))
		@results)
	(do
	  (dorun
	   (map test/report @results))
	  (System/exit 127))
	(System/exit 0)))))

(defn run-all-integration-tests []
  "useful from the REPL"
  (load-and-require-namespaces "src/integration-test/clojure")
  (test/run-all-tests))

(defn run-all-unit-tests []
  "useful from the REPL"
  (load-and-require-namespaces "src/test/clojure")
  (test/run-all-tests))

(defn load-all-test-namespaces []
  (load-and-require-namespaces "src/test/clojure")
  (load-and-require-namespaces "src/integration-test/clojure"))