package com.applifit.bi_lifit1.formulaire;

import com.applifit.bi_lifit1.DataBase.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * classe pour la gestion de la table valeur
 * 
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

	public void insertValeur(String valeur, int idElement, int idUser,
			String date, int indice, int version) {

		ContentValues values = new ContentValues();
		values.put("valeur", valeur);
		values.put("iduser", idUser);
		values.put("idelement", idElement);
		values.put("date", date);
		values.put("indice", indice);
		values.put("version", version);
		dbsql.insert("valeur", null, values);

	}

	public Valeur getValeur(int idElement, int idUser) {

		String select = " idelement = " + idElement + " and iduser='" + idUser
				+ "'";

		Valeur v;
		Cursor c = dbsql.query("valeur", new String[] { "id", "valeur",
				"idElement", "idUser", "date", "indice", "version" }, select,
				null, null, null, null);
		if (c != null && c.getCount() != 0) {
			c.moveToFirst();
			c.moveToLast();
			v = new Valeur(c.getInt(0), c.getString(1), c.getInt(2),
					c.getInt(3), c.getString(4), c.getInt(5), c.getInt(6));
			return v;
		} else
			return null;

	}

	public Cursor getValeurs(int idElement) {

		Cursor c = dbsql.query("valeur", new String[] { "id", "valeur",
				"idElement", "idUser", "date", "indice" }, "idelement = "
				+ idElement, null, null, null, null);
		return c;
	}

	public Cursor getValeursindice(int idelement, int indice) {
		return this.dbsql.query("valeur", new String[] { "id", "valeur",
				"idElement", "idUser", "date", "indice" }, "idelement = "
				+ idelement + " and indice='" + indice + "'", null, null, null,
				null);
	}

	public Cursor getAllValeur() {

		Cursor c = dbsql.query("valeur", new String[] { "id", "valeur",
				"idElement", "idUser", "date", "indice","version" }, null, null, null,
				null, null);
		return c;
	}

	public boolean deleteValeur(int id) {
		return dbsql.delete("valeur", "id = " + id, null) > 0;

	}

	public boolean deleteValeur(int idE, int indice) {
		return this.dbsql.delete("valeur", "idelement = " + idE
				+ " and indice='" + indice + "'", null) > 0;
	}
	  public int update(int idE, int indice, int version)
	  {
	    ContentValues localContentValues = new ContentValues();
	    localContentValues.put("version", Integer.valueOf(version));
	    return this.dbsql.update("valeur", localContentValues, "idelement = " + idE + " and indice='" + indice + "'", null);
	  }
}
