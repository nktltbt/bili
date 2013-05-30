package com.applifit.bi_lifit1.formulaire;

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
	int version;
	
	
	public Valeur(int id, String valeur, int idElement, int idUser, String date, int indice,int version) {
		super();
		this.id = id;
		this.valeur = valeur;
		this.idElement = idElement;
		this.idUser = idUser;
		this.date = date;
		this.indice = indice;
		this.version=version;
	}


	public Valeur(String valeur, int idElement, int idUser, String date, int indice,int version) {
		super();
		this.valeur = valeur;
		this.idElement = idElement;
		this.idUser = idUser;
		this.date = date;
		this.indice = indice;
		this.version=version;
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


	public int getVersion() {
		return version;
	}


	public void setVersion(int version) {
		this.version = version;
	}


	public void setId(int id) {
		this.id = id;
	}
	
}
