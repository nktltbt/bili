package com.applifit.bilifit.DataBase;
/**
 * classe pour la gestion des option de l'application
 * @author Faissal
 *
 */
public class OptionApp {
	
	int id; 
	String nom;
	String valeur;
	
	public OptionApp(String nom, String valeur) {
		super();
		this.nom = nom;
		this.valeur = valeur;
	}

	public OptionApp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OptionApp(int id, String nom, String valeur) {
		super();
		this.id = id;
		this.nom = nom;
		this.valeur = valeur;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getValeur() {
		return valeur;
	}

	public void setValeur(String valeur) {
		this.valeur = valeur;
	}

	public int getId() {
		return id;
	}
	
	
	

}
