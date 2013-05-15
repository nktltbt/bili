package com.applifit.bilifit.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * classe pour gestion de la table optionApp qui contient les option de l'application
 * @author Faissal
 *
 */
public class DbInstall {
	DbAdapter dbOpenHelper;
	Context context;
	SQLiteDatabase dbsql;
	
	public DbInstall(Context context) {
		super();
		dbOpenHelper = new DbAdapter(context);
		this.context = context;
	}
	
	public DbInstall open() {
		dbsql = dbOpenHelper.getWritableDatabase();
		return this;
	}
	
	
public void insertoptionApp(String nom, String valeur){
		
		ContentValues values = new ContentValues();
		values.put("nom",    nom);
		values.put("valeur", valeur);
		dbsql.insert("optionApp", null, values);
		
	}
public void setoptionApp(int id, String nom, String valeur){
	
	ContentValues values = new ContentValues();
	values.put("nom",    nom);
	values.put("valeur", valeur);
	dbsql.update("optionApp", values, "id=" + id, null);
	
}
public void setoptionApp(String nom, String valeur){
	
	ContentValues values = new ContentValues();
	values.put("valeur", valeur);
	dbsql.update("optionApp", values, "nom like " + nom, null);
	
}

	public OptionApp getoptionApp() {
		
		OptionApp option;
		Cursor c = dbsql.query("optionApp", new String[]{
				"id",
				"nom",
				"valeur"}, null, null, null, null, null);
		c.moveToFirst();
		if(c.getCount()>0) option = new OptionApp(Integer.valueOf(c.getString(0)), c.getString(1), c.getString(2)) ;
		else option = new OptionApp("", "");
		return option;
	}
	
public String getoptionApp(String nom) {
		
		String option;
		Cursor c = dbsql.query("optionApp", new String[]{"valeur"}, "nom like "+"'"+nom+"'", null, null, null, null);
		c.moveToFirst();
		if(c.getCount()>0) option = c.getString(0) ;
		else option = "";
		return option;
	}
	
	
	
	public boolean deleteoptionApp(String nom) {
		return dbsql.delete("optionApp", "nom like "+"'"+nom+"'", null)>0 ;
		
	}
	

}
