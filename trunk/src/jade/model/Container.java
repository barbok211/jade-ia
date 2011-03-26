

package jade.model;

import jade.ia.Moteur.Reponse;


/**
 * Container permettant de regrouper une question, une entité et une décision
 */
public class Container {

    private QuestionEntite qe = null;
    private Entite e = null;
    private Reponse decision = null;

    public Entite getEntity() {
        return e;
    }

    public QuestionEntite getQuestionEntity() {
        return qe;
    }

    public void setEntity(Entite e) {
        this.e = e;
    }

    public void setQuestionEntity(QuestionEntite qe) {
        this.qe = qe;
    }

    public Boolean hasEntity() {
        return e!=null;
    }

    public Boolean hasQuestion() {
        return qe!=null;
    }

    public Reponse getDecision() {
        return decision;
    }

    public void setDecision(Reponse decision) {
        this.decision = decision;
    }








}
