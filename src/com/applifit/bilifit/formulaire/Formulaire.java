package com.applifit.bilifit.formulaire;

/***
 *  la classe Formulaire
 * @author Faissal
 *
 */
public class Formulaire {
	
	public int getEtat() {
		return etat;
	}

	public void setEtat(int etat) {
		this.etat = etat;
	}

	int id;
	String nom;
	String commentaire;
	int version;
	int iduser;
	int etat;
	
	public Formulaire(int id, String nom, String commentaire, int version, int iduser, int etat) {
		super();
		this.id = id;
		this.nom = nom;
		this.commentaire = commentaire;
		this.iduser = iduser;
		this.version = version;
		this.etat = etat;
	}

	public Formulaire(String nom, String commentaire, int version, int iduser, int etat) {
		super();
		this.nom = nom;
		this.commentaire = commentaire;
		this.iduser = iduser;
		this.version = version;
		this.etat = etat;
	}

	public int getIduser() {
		return iduser;
	}

	public Formulaire() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getId() {
		return id;
	}
	
	
	
	

}
