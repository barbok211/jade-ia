/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jade.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Entité de base
 */
public abstract class Entite {

    private int id;
    private String nom;
    private int nbJoues;
    private int nbTrouves;

    public Entite() {}

    public abstract void init(ResultSet res) throws SQLException;
    
    public Entite(int id, String nom, int nb_joues, int nbTrouves) {
        this.nom = nom;
        this.nbJoues = nb_joues;
        this.nbTrouves = nbTrouves;
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public int getNbJoues() {
        return nbJoues;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setNbJoues(int nbJoues) {
        this.nbJoues = nbJoues;
    }

    public int getNbTrouves() {
        return nbTrouves;
    }

    public void setNbTrouves(int nbTrouves) {
        this.nbTrouves = nbTrouves;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
