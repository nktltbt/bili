package com.applifit.bilifit.formulaire;

/**
 *  la classe Parametre
 * @author Faissal
 *
 */
public class Parametre {
	
	int id;
	String nom;
	String valeur;
	int IdElement;
	
	
	public Parametre() {
		super();
	}


	public Parametre(int id, String nom, String valeur, int Idelement) {
		super();
		this.id = id;
		this.nom = nom;
		this.valeur = valeur;
		this.IdElement = Idelement;
	}


	public Parametre(String nom, String valeur, int Idelement) {
		super();
		this.nom = nom;
		this.valeur = valeur;
		this.IdElement = Idelement;
	}


	public int getIdElement() {
		return IdElement;
	}


	public void setElement(int IdElement) {
		this.IdElement = IdElement;
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
