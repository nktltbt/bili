package com.applifit.bilifit.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * la classe de creation de la base de données
 * @author Faissal
 *
 */
public class DbAdapter extends SQLiteOpenHelper{
	
	
	private static final int DataBase_version = 1;
	private static final String DataBase_name="BilifitDB.db";
	
	
	public DbAdapter(Context context) {
		super(context, DataBase_name, null, DataBase_version);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//la table utilisateur
		db.execSQL("create table utilisateur(" +
				"id integer primary key autoincrement," +
				"nom text," +
				"prenom text," +
				"mail text," +
				"fonction text," +
				"profile text," +
				"etat integer," +
				"password text, connecte integer);");
				
		// la table formulaire
		db.execSQL(" create table formulaire(" +
				"id integer primary key autoincrement," +
				"nom text," +
				"commentaire text," +
				"idUser integer,"+
				"version integer, etat integer);");
		//la table element
		db.execSQL("create table element(" +
				"id integer primary key autoincrement, type text, position_x integer, position_y integer, idFormulaire integer);");
				
		// la table parametre
		db.execSQL(" create table parametre(" +
				"id integer primary key autoincrement, nom text, valeur text, idElement integer);");
		
		// la table valeur
				db.execSQL(" create table valeur(" +
						"id integer primary key autoincrement, valeur text, idElement integer, idUser integer, date text, indice integer);");
		// la table optionsApp
		db.execSQL(" create table optionApp(" +
				"id integer primary key autoincrement, nom text, valeur text);");
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	

}
