(ns oauth2.routes.home
  (:require [oauth2.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [oauth2.db.core :as db]))
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

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/register" request (register-page request))
  (POST "/register" request (register! request))
  (GET "/login" request (login-page request))
  (POST "/login" request (login! request))
  (GET "/authorize" [response_type client_id redirect_uri scope state] (authorize response_type client_id redirect_uri scope state)))
