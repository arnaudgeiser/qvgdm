(ns qvdm.frontend.app
  (:require [reagent.core :as r]
            [reagent.dom  :as rd]))

(def state (r/atom {:question 1
                    :visible 0
                    :jokers #{}
                    :fifty-fifty nil
                    :revealed false
                    :last-word false}))

(def letsplay      (js/Audio. "sounds/letsplay.mp3"))
(def letsplay2     (js/Audio. "sounds/letsplay2.mp3"))
(def correct       (js/Audio. "sounds/correct.mp3"))
(def final         (js/Audio. "sounds/final.mp3"))
(def easyquestions (js/Audio. "sounds/easyquestions.mp3"))
(def hardquestions (js/Audio. "sounds/hardquestions.mp3"))
(def win           (js/Audio. "sounds/win.mp3"))
(def rammeur       (js/Audio. "sounds/rammeur.mp3"))
(def all-sounds [letsplay letsplay2 easyquestions hardquestions final rammeur])

(defn pause-sound [sound]
  (.pause sound)
  (set! (.-currentTime sound) 0))

(defn pause-all-sounds []
  (run! pause-sound all-sounds))

(pause-all-sounds)

(defn next-question [{:keys [question] :as state}]
  (merge state {:question (inc question)
                :visible 0
                :fifty-fifty nil
                :revealed false
                :last-word false}))

(defn previous-question [{:keys [question] :as state}]
  (merge state {:question (max 1 (dec question))
                :visible 0
                :fifty-fifty nil
                :revealed false
                :last-word false}))

(defn make-visible [{:keys [visible] :as state}]
  (assoc state :visible (inc visible)))

(defn listen [sound]
  (.play sound))

(defn last-word [state]
  (listen final)
  (assoc state :last-word true))

(defn confirm [state sound]
  (listen sound)
  (assoc state :revealed true))

(defn fifty-fifty [state]
  (-> state
      (update :jokers conj :fifty-fifty)
      (update :fifty-fifty #((fnil inc 0) %))))

(.addEventListener js/window "load"
                   (fn [_]
                     (.addEventListener js/document "keydown"
                                        (fn [e]
                                          (case (.-code e)
                                            "KeyE" (.play easyquestions)
                                            "KeyH" (.play hardquestions)
                                            "KeyC" (swap! state confirm correct)
                                            "KeyS" (pause-all-sounds)
                                            "KeyL" (.play letsplay)
                                            "KeyN" (.play letsplay2)
                                            "KeyF" (swap! state last-word)
                                            "KeyW" (swap! state confirm win)
                                            "KeyR" (listen rammeur)
                                            "Digit5" (swap! state fifty-fifty)
                                            "KeyA" (swap! state #(update % :jokers conj :audience))
                                            "KeyP" (swap! state #(update % :jokers conj :phone))
                                            "ArrowDown" (swap! state make-visible)
                                            "ArrowRight" (swap! state next-question)
                                            "ArrowLeft"  (swap! state previous-question)
                                            :else nil)))))

(def rewards
  [36
   360
   366
   "3'600"
   "3'660"
   "3'666"
   "36'000"
   "36'600"
   "36'660"
   "36'666"
   "360'000"
   "363'600"
   "363'636"
   "3'600'000"
   "36'000'000"])

(def questions
  {13 {:question "Combien de marches Lucie et Adri avaient-ils ?? monter depuis l???arr??t de bus des Fahys,
jusqu????? leur appartement ?? la rue de l???Or??e 36?"
       :answers ["100"
                 "142"
                 "150"
                 "160"]
       :answer 4}
   8 {:question "Quel est l???ordre d???arriv??e des locataires de l???immeuble du 36 ?"
      :answers ["Pauline, Morgan, Aur??lie, Elo??se, Adrien, Nad??ge"
                "Pauline, Manu, Morgan, Arnaud, Lucie, Nad??ge",
                "Pauline, Morgan, Manu, Aur??lie, Adrien, Nad??ge"
                "Pauline, Manu, Morgan, Arnaud, Lucie, Timoth??e"]
      :answer 3}
   3 {:question "Quelle ??tait la tenue pr??f??r??e d???Adri pour aller ?? la buanderie ?"
      :answers ["Cale??on + cravatte"
                "Cale??on + chaussettes"
                "Cale??on + chemise"
                "Cale??on + t-shirt"]
      :answer 2}
   4 {:question "Qui ??tait l???amie imaginaire d???Adri, ?? la rue de l???Or??e 36 ?"
      :answers ["Lucie J."
                "Lucie"
                "Carmen A."
                "Alexandra S."]
      :answer 1}
   14 {:question "Autour de quel accessoire Adri a-t-il fait la connaissance pour la toute premi??re fois de ses
      voisins de palier ?"
       :answers ["Un tire-bouchon"
                 "Une carte de lessive"
                 "Une cannette"
                 "Un arrosoir"]
       :answer 1}
   10 {:question "En moyenne, lors des soir??es karaok??, combien d???invit??s avaient Lucie et Adri dans leur
                appartement de la rue de l???Or??e 36 ?"
       :answers [14
                 2
                 6
                 0]
       :answer 4}
   6 {:question "Quelle liste Lucie faisait-elle au fond d?????tude ?"
      :answers ["La liste de commission"
                "La liste de projets de vacances"
                "La liste des BG"
                "La Todo list d'Adri"]
      :answer 3}
   11 {:question "Sur quelle cha??ne Lucie a-t-elle ??t?? interview??e pour la premi??re fois ?"
       :answers ["RTS"
                 "Canal Alpha"
                 "T??l?? Bilingue"
                 "RTN"]
       :answer 2}
   9 {:question "O?? ??taient les voisins et que faisaient-ils pendant que Lucie demandait Adri en mariage ?"
      :answers ["A Paris, sur la Tour Eiffel"
                "A Neuch??tel, au minigolf"
                "A Morat, pour un brunch"
                "Aux grottes de R??cl??re"]
      :answer 3}
   7 {:question "Quel ??v??nement a n??cessite un nettoyage complet de la cuisine de Lucie et Adri par Morgan,
       durant leurs vacances ?"
      :answers ["Une soir??e trop arros??e"
                "Le carnage d'un oiseau par la Grosse Berque"
                "Une invasion de fourmis"
                "Une infiltration d'eau suite ?? l'arrosage des tomates"]
      :answer 2}
   12 {:question "Qui ??tait pr??sent lors de la Campagne Arcadia Quest ?"
       :answers ["Arnaud, Morgan, Lucie et Adri"
                 "Arnaud, Morgan, Lionel et Adri"
                 "Nad??ge, Arnaud, Morgan et Adri"
                 "Lucie, Nad??ge, Arnaud et Adri"]
       :answer 2}
   5 {:question "Quel est ce bruit, souvent entendu ?? travers les murs des voisins de Lucie
et Adri ?? l???Or??e 36 ?"
      :answers ["Adri qui passe l'aspirateur"
                "Adri qui fait du rameur"
                "Lucie qui r??vise"
                "Adri qui cuisine"]
      :answer 2}
   1 {:question "A quel num??ro nous sommes-nous tous rencontr??s?"
      :answers [36 10 4 42]
      :answer 1}
   2 {:question "Comment s???orthographie le chat le plus connu de la rue de l'Or??e?"
      :answers ["La Grosse Berk" "La Grosse Bairk" "La Grosse Bergue" "La Grosse Berque"]
      :answer 4}
   15 {:question "Quel ??tait la couleur du PREMIER maillot officiel de l'??quipe des voisins du 36 ?"
       :answers ["Bleu" "Orange" "Vert" "Rose"]
       :answer 3}})

(defn current-question []
  (let [{:keys [question visible jokers last-word revealed fifty-fifty]} @state]
    (merge (get questions question)
           {:visible visible
            :numero question
            :jokers jokers
            :last-word last-word
            :revealed revealed
            :fifty-fifty fifty-fifty})))

(defn answer-class [last-word revealed answer current-answer]
  (str "answer "
       (if (and last-word (= answer current-answer)) "selected-answer " "")
       (if (and revealed (= answer current-answer)) "blinked-answer " "")))

(defn answer-visibility [visible fifty-fifty current-answer]
  (if (and (>= visible current-answer)
           (or (nil? fifty-fifty) (= (mod current-answer 2) (mod fifty-fifty 2))))
    "visible"
    "hidden"))

(defn game []
  (fn []
    (let [{:keys [numero question answers answer visible last-word revealed fifty-fifty jokers]} (current-question)
          [a1 a2 a3 a4] answers]
      [:div {:style {:height "100%"} :class "text"}
       [:div {:style {:height "58%" :font-size "2vw" :text-align "center"}}
        [:div {:style {:padding-top "24px" :font-size "3vw"}} "Qui veut gagner\n36'000'000"]
        [:div {:style {:margin-top "24px"}} (str "Question " numero)]
        [:div {:style {:margin-top "50px"}} "JOKERS"]
        [:div {:style {:margin-top "24px"}}
         [:img {:style {:margin-right "12px" :opacity (if (:fifty-fifty jokers) "0.2" "1")} :src "images/50-50.webp" :width 90 }]
         [:img {:style {:margin-right "12px" :opacity (if (:phone jokers) "0.2" "1")} :src "images/phone.webp" :width 90}]
         [:img {:style {:opacity (if (:audience jokers) "0.2" "1")} :src "images/audience.webp" :width 90}]]
        [:div {:style {:position "absolute" :font-size "1vw" :text-align "right" :right "15px" :top "24px"}}
         (map-indexed (fn [idx r]
                        [:div {:key idx :style {:color (if (= (- (count rewards) idx) numero) "orange" "white") :display "flex" :font-size "1.2em"}}
                         [:div {:style {:width "40px" :text-align "left"}} (- (count rewards) idx)]
                         [:div {:style {:flex-grow "1"}} (str r " .--")]])
                      (reverse rewards))]]
       [:div {:style {:height "14.5%" :display "flex" :align-items "center" :justify-content "center"} :id "question-answers"}
        [:div {:style {:width "12%"}}]
        [:div {:style {:width "76%" :font-size "4vh"} :id "question"} question]
        [:div {:style {:width "12%"}}]]
       [:div {:style {:height "2.5%"}}]
       [:div {:style {:height "8%" :display "flex" :align-items "center" :font-size "3vh"} :class "row1"}
        [:div {:style {:width "12%"}}]
        [:div {:style {:width "4%"}} "A"]
        [:div {:class (answer-class last-word revealed answer 1)
               :style {:visibility (answer-visibility visible fifty-fifty 1)}} a1]
        [:div {:style {:width "10%"}}]
        [:div {:style {:width "4%"}} "B"]
        [:div {:class (answer-class last-word revealed answer 2)
               :style {:visibility (answer-visibility visible fifty-fifty 2)}} a2]]
       [:div {:style {:height "2.5%"}}]
       [:div {:style {:height "8%" :display "flex" :align-items "center" :font-size "3vh"} :class "row2"}
        [:div {:style {:width "12%"}}]
        [:div {:style {:width "4%"}} "C"]
        [:div {:class (answer-class last-word revealed answer 3)
               :style {:visibility (answer-visibility visible fifty-fifty 3)}} a3]
        [:div {:style {:width "10%"}}]
        [:div {:style {:width "4%"}} "D"]
        [:div {:class (answer-class last-word revealed answer 4)
               :style {:visibility (answer-visibility visible fifty-fifty 4)}} a4]]])))

(defn init []
(rd/render
 [game] (.getElementById js/document "root")))
