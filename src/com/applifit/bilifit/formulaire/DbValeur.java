package com.applifit.bilifit.formulaire;

import com.applifit.bilifit.DataBase.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * classe pour la gestion de la table valeur
 * @author Faissal
 *
 */
public class DbValeur {
		DbAdapter dbOpenHelper;
		Context context;
		SQLiteDatabase dbsql;
		
		public DbValeur(Context context) {
			super();
			dbOpenHelper = new DbAdapter(context);
			this.context = context;
		}
		
		public DbValeur open() {
			dbsql = dbOpenHelper.getWritableDatabase();
			return this;
		}
		
	public void insertValeur(String valeur, int idElement, int idUser, String date, int indice){
			
			ContentValues values = new ContentValues();
			values.put("valeur",    valeur);
			values.put("iduser",    idUser);
			values.put("idelement",    idElement);
			values.put("date",    date);
			values.put("indice",    indice);
			dbsql.insert("valeur", null, values);
			
		}
		
		public Valeur getValeur(int idElement, int idUser) {
			
			String select = " idelement = " + idElement + " and iduser='"+idUser+"'";
			
			Valeur v;
			Cursor c = dbsql.query("valeur", new String[]{
					"id",
					"valeur", "idElement", "idUser", "date", "indice"}, select, null, null, null, null);
			if(c!=null && c.getCount()!=0) {
				c.moveToFirst();
				c.moveToLast();
				v = new Valeur(c.getInt(0), c.getString(1),c.getInt(2), c.getInt(3), c.getString(4), c.getInt(5),2);
				return v;
			}
			else return null;
		
		}
		
		public Cursor getValeurs(int idElement) {

			Cursor c = dbsql.query("valeur", new String[]{
					"id",
					"valeur", "idElement", "idUser", "date", "indice"}, "idelement = " + idElement, null, null, null, null);
			return c;
		}
		
	public Cursor getAllValeur() {
			
		Cursor c = dbsql.query("valeur", new String[]{
				"id",
				"valeur", "idElement", "idUser", "date", "indice"}, null, null, null, null, null);
			return c;
		}
		
		public boolean deleteValeur(int id) {
			return dbsql.delete("valeur", "id = "+id, null)>0 ;
			
		}
		

	}

