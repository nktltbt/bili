package com.applifit.bilifit.formulaire;

/**
 *  la classe Valeur
 * @author Faissal
 *
 */
public class Valeur {
	
	int id;
	String valeur;
	int idElement;
	int idUser;
	String date;
	int indice;
	
	
	public Valeur(int id, String valeur, int idElement, int idUser, String date, int indice) {
		super();
		this.id = id;
		this.valeur = valeur;
		this.idElement = idElement;
		this.idUser = idUser;
		this.date = date;
		this.indice = indice;
	}


	public Valeur(String valeur, int idElement, int idUser, String date, int indice) {
		super();
		this.valeur = valeur;
		this.idElement = idElement;
		this.idUser = idUser;
		this.date = date;
		this.indice = indice;
	}
	
	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public int getIndice() {
		return indice;
	}


	public void setIndice(int indice) {
		this.indice = indice;
	}


	public Valeur() {
		super();
	}


	public String getValeur() {
		return valeur;
	}


	public void setValeur(String valeur) {
		this.valeur = valeur;
	}


	public int getIdElement() {
		return idElement;
	}


	public void setIdElement(int idElement) {
		this.idElement = idElement;
	}


	public int getIdUser() {
		return idUser;
	}


	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}


	public int getId() {
		return id;
	}
}
