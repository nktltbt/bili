package com.applifit.bilifit.formulaire;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.applifit.bilifit.DataBase.DbAdapter;

/**
 * classe pour la gestion de la table element
 * @author Faissal
 *
 */
public class DbElement {
	DbAdapter dbOpenHelper;
	Context context;
	SQLiteDatabase dbsql;
	
	public DbElement(Context context) {
		super();
		dbOpenHelper = new DbAdapter(context);
		this.context = context;
	}
	
	public DbElement open() {
		dbsql = dbOpenHelper.getWritableDatabase();
		return this;
	}
	
public void insertElement(int id, String type, int position_x, int position_y, int idForm){
		
		ContentValues values = new ContentValues();
		values.put("id",    id);
		values.put("type",    type);
		values.put("position_x",    position_x);
		values.put("position_y",    position_y);
		values.put("idFormulaire",    idForm);
		dbsql.insert("element", null, values);
		
	}
	
	public Element getElement(int idForm, int x, int y) {
		
		String select = " idFormulaire = " + idForm + " and position_x='"+x+"'"+ " and position_y='"+y+"'";
		
		Element v;
		Cursor c = dbsql.query("element", new String[]{
				"id",
				"type", "position_x", "position_y", "idFormulaire"}, select, null, null, null, null);
		if(c!=null && c.getCount()!=0) {
			c.moveToFirst();
			c.moveToLast();
			v = new Element(c.getInt(0), c.getString(1),c.getInt(2), c.getInt(3), c.getInt(4));
			return v;
		}
		else return null;
	
	}
	
	public Cursor getElements(int idForm) {

		Cursor c = dbsql.query("element", new String[]{
				"id",
				"type", "position_x", "position_y", "idFormulaire"}, "idFormulaire = " + idForm, null, null, null, "position_y,position_x");
		return c;
	}
	
public Cursor getAllElement() {
		
	Cursor c = dbsql.query("element", new String[]{
			"id",
			"type", "position_x", "position_y", "idFormulaire"}, null, null, null, null, null);
		return c;
	}
	
	public boolean deleteElement(int id) {
		return dbsql.delete("element", "id = "+id, null)>0 ;
		
	}
	

}

