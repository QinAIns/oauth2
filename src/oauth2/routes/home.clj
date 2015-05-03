(ns oauth2.routes.home
  (:require [oauth2.layout :as layout]
            [compojure.core :refer [defroutes GET POST PUT]]
            [ring.util.response :refer [redirect file-response]]
            [clojure.java.io :as io]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [buddy.core.hash :as hash]
            [oauth2.db.core :as db])
  (:import [java.io File FileInputStream FileOutputStream]))

(defn authorize
  ;;授权请求
  [response_type client_id redirect_uri scope state]
  {:status 302
   :headers {"Location" (str redirect_uri "?code=123&state=" state )}})

(defn home-page []
  (layout/render
   "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn register-page [{:keys [flash]}]
  (layout/render
   "register.html"
   (merge {:message ""}
          (select-keys flash [:login_name :password :password2 :errors]))))

(defn validate-register [params]
  (first
   (b/validate
    params
    :login_name v/required
    :password [v/required [v/min-count 6]]
    :password2 [[(fn [password] (= password (:password params)))
                 :message "密码不一致"]])))

(defn register! [{:keys [params]}]
  (if-let [errors (validate-register params)]
    (-> (redirect "/register")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/create-user! (merge {:id ()})))))

(defn validate-login [params]
  (first
   (b/validate
    params
    :login_name v/required
    :password [v/required [v/min-count 6]])))

(defn login-page [{:keys [flash]}]
  (layout/render
   "login.html"
   (merge {:message ""}
          (select-keys flash [:login_name :password :errors]))))

(defn login! [{:keys [params]}]
  (if-let [errors (validate-login params)]
    (-> (redirect "/login")
        (assoc :flash (assoc params :errors errors)))
    (do
      (str "success"))))

(defn profile-page []
  (layout/render
   "profile.html"))

(defn profile! [{:keys [paramss]}]
  )

(def resource-path "/tmp/")

(defn file-path [path & [filename]]
  (java.net.URLDecoder/decode
    (str path File/separator filename)
    "utf-8"))

(defn upload-file
  "uploads a file to the target folder
   when :create-path? flag is set to true then the target path will be created"
  [path {:keys [tempfile size filename]}]
  (try
    (with-open [in (new FileInputStream tempfile)
                out (new FileOutputStream (file-path path filename))]
      (let [source (.getChannel in)
            dest   (.getChannel out)]
        (.transferFrom dest source 0 (.size source))
        (.flush out)))))


(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/profile" [] (profile-page))
  (POST "/profile" [avatar]
       (def a avatar) 
       (upload-file resource-path avatar)
       (redirect (str "/files/" (:filename avatar))))

  (GET "/files/:filename" [filename]
       (file-response (str resource-path filename)))
  
  (GET "/register" request (register-page request))
  (POST "/register" request (register! request))
  (GET "/login" request (login-page request))
  (POST "/login" request (login! request))
  (GET "/authorize" [response_type client_id redirect_uri scope state] (authorize response_type client_id redirect_uri scope state)))
