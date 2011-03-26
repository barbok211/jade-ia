/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jade.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Entité Question
 */
public abstract class QuestionEntite {

    private int id;
    private String question;
    private int ouiRep;
    private int nonRep;
    private int ouiQuestion;
    private int nonQuestion;
    private int parent;
    private int nbJoue;

    public  QuestionEntite() {}
    public abstract void init(ResultSet res) throws SQLException;
    
    public QuestionEntite(int id, String question, int ouiRep, int nonRep, int ouiQuestion, int nonQuestion, int parent, int nbJoue) {
        this.id = id;
        this.question = question;
        this.ouiRep = ouiRep;
        this.nonRep = nonRep;
        this.ouiQuestion = ouiQuestion;
        this.nonQuestion = nonQuestion;
        this.parent = parent;
        this.nbJoue = nbJoue;
    }

    public int getId() {
        return id;
    }

    public int getNbJoue() {
        return nbJoue;
    }

    public int getNonQuestion() {
        return nonQuestion;
    }

    public int getNonRep() {
        return nonRep;
    }

    public int getOuiQuestion() {
        return ouiQuestion;
    }

    public int getOuiRep() {
        return ouiRep;
    }

    public int getParent() {
        return parent;
    }

    public String getQuestion() {
        return question;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNbJoue(int nbJoue) {
        this.nbJoue = nbJoue;
    }

    public void setNonQuestion(int nonQuestion) {
        this.nonQuestion = nonQuestion;
    }

    public void setNonRep(int nonRep) {
        this.nonRep = nonRep;
    }

    public void setOuiQuestion(int ouiQuestion) {
        this.ouiQuestion = ouiQuestion;
    }

    public void setOuiRep(int ouiRep) {
        this.ouiRep = ouiRep;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
