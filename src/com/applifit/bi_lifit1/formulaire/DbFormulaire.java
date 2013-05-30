package com.applifit.bi_lifit1.formulaire;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.applifit.bi_lifit1.DataBase.DbAdapter;

/**
 * classe pour la gestion de la table formulaire
 * @author Faissal
 *
 */
public class DbFormulaire {

	DbAdapter dbOpenHelper;
	Context context;
	SQLiteDatabase dbsql;
	
	public DbFormulaire(Context context) {
		super();
		dbOpenHelper = new DbAdapter(context);
		this.context = context;
	}
	
	public DbFormulaire open() {
		dbsql = dbOpenHelper.getWritableDatabase();
		return this;
	}
	
public void insertformulaire(int id, String nom, String commentaire, int version, int idUser, int etat){
		ContentValues values = new ContentValues();
		values.put("id",    id);
		values.put("nom",    nom);
		values.put("commentaire", commentaire);
		values.put("idUser", idUser);
		values.put("version", version);
		values.put("etat", etat);
		dbsql.insert("formulaire", null, values);
	}
public void setformulaire(int id, String nom, String commentaire, int version, int idUser, int etat){
	ContentValues values = new ContentValues();
	values.put("nom",    nom);
	values.put("commentaire", commentaire);
	values.put("idUser", idUser);
	values.put("version", version);
	values.put("etat", etat);
	dbsql.update("formulaire", values, "id=" + id, null);
}
	
	public Formulaire getFormulaire(int id) {
		Formulaire form;
		Cursor c = dbsql.query("formulaire", new String[]{
				"id",
				"nom",
				"commentaire",
				"idUser",
				"version",
				"etat"}, "id = "+id, null, null, null, null);
		c.moveToFirst();
		if(c.getCount()==0) return null;
		form = new Formulaire(Integer.valueOf(c.getString(0)), c.getString(1), c.getString(2), Integer.valueOf(c.getString(4)), Integer.valueOf(c.getString(3)), Integer.valueOf(c.getString(5)));
		return form;
	}
	public Formulaire getFormulaireByNom(String nom) {
		Formulaire form;
		Cursor c = dbsql.query("formulaire", new String[]{
				"id",
				"nom",
				"commentaire",
				"idUser",
				"version",
				"etat"}, "nom = '"+nom+"'", null, null, null, null);
		c.moveToFirst();
		if(c.getCount()==0) return null;
		form = new Formulaire(Integer.valueOf(c.getString(0)), c.getString(1), c.getString(2), Integer.valueOf(c.getString(4)), Integer.valueOf(c.getString(3)), Integer.valueOf(c.getString(5)));
		return form;
	}
	 
public Cursor getAllFormulaire(int iduser) {
		
		Cursor c = dbsql.query("formulaire", new String[]{
				"id",
				"nom",
				"commentaire",
				"idUser",
				"version",
				"etat"}, "idUser="+iduser , null, null, null, null);
		return c;
	}

public Cursor getAllIdFormulaire() {
	
	Cursor c = dbsql.query("formulaire", new String[]{
			"id"}, null , null, null, null, null);
	return c;
}
	
	public boolean deleteFormulaire(int id) {
		return dbsql.delete("formulaire", "id = "+id, null)>0 ;
		
	}
}

