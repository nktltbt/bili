package com.applifit.bi_lifit1.formulaire;

import com.applifit.bi_lifit1.DataBase.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * classe pour la gestion de la table parametre
 * @author Faissal
 *
 */
public class DbParametre {
	DbAdapter dbOpenHelper;
	Context context;
	SQLiteDatabase dbsql;
	
	public DbParametre(Context context) {
		super();
		dbOpenHelper = new DbAdapter(context);
		this.context = context;
	}
	
	public DbParametre open() {
		dbsql = dbOpenHelper.getWritableDatabase();
		return this;
	}
	
public void insertParametre(int id, String nom, String valeur, int idElement){
		
		ContentValues values = new ContentValues();
		values.put("id",id);
		values.put("nom",nom);
		values.put("valeur",valeur);
		values.put("idElement", idElement);
		dbsql.insert("parametre", null, values);
	}
	
	public Cursor getParametres(int idElement) {

		Cursor c = dbsql.query("parametre", new String[]{
				"id",
				"nom", "valeur", "idElement"}, "idElement = " + idElement, null, null, null, null);
		return c;
	}
	
public Cursor getAllParametre() {
		
	Cursor c = dbsql.query("parametre", new String[]{
			"id",
			"nom", "valeur", "idElement"}, null, null, null, null, null);
		return c;
	}
	
	public boolean deleteParametre(int id) {
		return dbsql.delete("parametre", "id = "+id, null)>0 ;
		
	}
	

}

