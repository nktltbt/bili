package com.applifit.bilifit.compte;

/**
 * la classe utilisateur
 * @author Faissal
 *
 */
public class Utilisateur {
	
	private int id;
	private String nom;
	private String prenom;
	private String mail;
	private String fonction;
	private String profile;
	private String password;
	private int etat;
	private String device;
	
	
	public Utilisateur() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Utilisateur(String nom, String prenom, String mail, String fonction,
			String profile, int etat) {
		super();
		this.nom = nom;
		this.prenom = prenom;
		this.mail = mail;
		this.fonction = fonction;
		this.profile = profile;
		this.etat = etat;
	}

	public Utilisateur(int id, String nom, String prenom, String mail,
			String fonction, String profile, String password, int etat) {
		super();
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.mail = mail;
		this.fonction = fonction;
		this.profile = profile;
		this.password = password;
		this.etat = etat;
	}

	public Utilisateur(int id, String nom, String prenom, String mail,
			String fonction, String profile, String password, int etat, String device) {
		super();
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.mail = mail;
		this.fonction = fonction;
		this.profile = profile;
		this.password = password;
		this.etat = etat;
		this.device=device;
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

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getMail() {
		
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getFonction() {
		return fonction;
	}

	public void setFonction(String fonction) {
		this.fonction = fonction;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getPasse() {
		return password;
	}

	public void setPasse(String password) {
		this.password = password;
	}

	public int getEtat() {
		return etat;
	}

	public void setEtat(int etat) {
		this.etat = etat;
	}

	public int getId() {
		return id;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}
	
	
	

}
