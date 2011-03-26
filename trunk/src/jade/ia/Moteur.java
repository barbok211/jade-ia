package jade.ia;

import jade.metier.Controleur;
import jade.metier.ControleurFactory;
import jade.metier.ControleurFactory.TypeEntite;
import jade.model.Container;
import jade.model.Entite;
import jade.model.QuestionEntite;

/**
 * Moteur d'inférence générique
 */
public abstract class Moteur {



    // Réponses transmises par le client
    public enum Reponse { OUI, NON, BOF };

    // Fonctions à redéfinir par les moteurs spécialisés
    public abstract TypeEntite getTypeEntite();
    private Controleur controleur;

    // Variables utilisées par l'inférence
    private Reponse derniereDecision; // Dernière "vrai réponse (après calcul de la réponse "Je ne sais pas"
    private QuestionEntite derniereQuestion;
    private QuestionEntite prochaineQuestion;
    private Entite proposition;
    private Boolean finInference;

    /**
     * Initialisation du moteur d'inférence propre à l'entité demandée
     */
    public Moteur() {
        controleur = ControleurFactory.create(this.getTypeEntite());
        initialiser();
    }

    private void initialiser()
    {
        finInference = false;
        proposition = null;
        prochaineQuestion = null;
        derniereDecision = null;
        derniereQuestion = null;
    }

    /**
     * Retourne vrai tant que l'inférence retourne des questions à poser
     * @return vrai si il y a encore des questions à poser
     */
    public boolean peutPoserQuestion() {
        return (finInference==false);
    }

    
    /**
     * Retourne la prochaine question à poser
     * @return
     */
    public String getProchaineQuestion() {
        
        // Première question
        if (derniereQuestion==null)
        {
            derniereQuestion = controleur.fetchFirstQuestion();
            return derniereQuestion.getQuestion();

        // En cours d'inférence
        } else {
            this.derniereQuestion = this.prochaineQuestion;
            this.prochaineQuestion = null;

            return derniereQuestion.getQuestion();
        }
        
    }

  
    /**
     * L'utilisateur envoie une réponse, on avance l'inférence
     * @param reponse
     */
    public void envoyerReponse(Reponse reponse)
    {

       derniereDecision=Reponse.OUI;
       switch(reponse)
       {
           case OUI:

                // Réponse OUI qui amène une nouvelle question
                if (derniereQuestion.getOuiQuestion()>0)
                {
                    this.prochaineQuestion = controleur.fetchQuestionById(derniereQuestion.getOuiQuestion(), true);
                }
                // Réponse OUI qui amène une proposition finale
                else
                {
                    this.proposition = controleur.fetchEntiteById(derniereQuestion.getOuiRep(), true);
                    this.finInference = true;
                }
            break;

            case NON:

                // Réponse NON qui amène à une nouvelle question
                if (derniereQuestion.getNonQuestion()>0)
                {
                    this.prochaineQuestion = controleur.fetchQuestionById(derniereQuestion.getNonQuestion(), true);
                }

                // Réponse NON qui amène à une proposition finale
                else
                {
                    this.proposition = controleur.fetchEntiteById(derniereQuestion.getNonRep(), true);
                    this.finInference = true;
                }
            break;

            // Réponse "Je ne sais pas"
            case BOF:

                // On récupère la question plébéscitée
                Container container = controleur.fetchPrivilegiedEntity(this.derniereQuestion);
                this.derniereDecision = container.getDecision();

                // On a une nouvelle question
                if (container.hasQuestion()) this.prochaineQuestion = container.getQuestionEntity();
                
                // On a une solution
                else {
                    this.proposition = container.getEntity();
                    this.finInference = true;
                }
                
            break;
       }
    }

    /**
     * Retourne la proposition si elle a été inférée
     * @return la proposition
     */
    public Entite getProposition() {
        return this.proposition;
    }


    /**
     * On enregistre que la proposition donnée est validée par l'utilisateur
     */
    public void actionPropositionCorrecte() {
        controleur.goodProposition(this.proposition);
    }

    /**
     * On demande au moteur d'apprendre une nouvelle réponse et de mettre à jour
     * son arbre d'inférence
     * @param question : Nouvelle question
     * @param reponse : Nouvelle réponse
     * @param oui : La nouvelle réponse répond-elle au OUI de la question ?
     * @return résultat de l'opération
     */
    public boolean apprendreNouvelleReponse(String question, String reponse, Boolean oui) {
        return controleur.formulerNouvelleQuestion(
                derniereQuestion,
                derniereDecision,
                proposition,
                question,
                reponse,
                oui
        );
    }

    /**
     * Vérifie si une entité existe déjà dans la base de donnée
     * @param nom Nom de l'entité
     * @return Vrai si l'entité existe déjà, sinon faux
     */
    public Entite verifierExistence(String nom) {
        return controleur.fetchEntiteByNom(nom);

    }

    /**
     * Recherche la première question commune parmi les parents de deux entités données
     * @param entiteCorrespondante Entité1
     * @param proposition Entité2
     * @return la première question parente commune
     */
    public QuestionEntite rechercherBifurcation(Entite entiteCorrespondante, Entite proposition) {
        QuestionEntite qA = controleur.fetchQuestionByEntite(entiteCorrespondante);
        QuestionEntite qB = controleur.fetchQuestionByEntite(proposition);
        return controleur.fetchPremiereQuestionCommune(qA, qB);
    }

}
