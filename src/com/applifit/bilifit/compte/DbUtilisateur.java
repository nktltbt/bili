package com.applifit.bilifit.compte;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.applifit.bilifit.DataBase.DbAdapter;

/**
 * l'activité de gestion de la table utilisateur
 * @author Faissal
 *
 */
public class DbUtilisateur extends Activity{
	
	DbAdapter dbOpenHelper;
	Context context;
	SQLiteDatabase dbsql;
	
	public DbUtilisateur(Context context) {
		super();
		dbOpenHelper = new DbAdapter(context);
		this.context = context;
	}
	
	public DbUtilisateur open() {
		dbsql = dbOpenHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * ajouter utilisateur
	 * @param id
	 * @param nom
	 * @param prenom
	 * @param mail
	 * @param fonction
	 * @param profile
	 * @param password
	 * @param etat
	 * @param connecte
	 */
public void insertuser(int id, String nom, String prenom, String mail, String fonction, String profile, String password, int etat, int connecte){
		
		ContentValues values = new ContentValues();
		values.put("id",    id);
		values.put("nom",    nom);
		values.put("prenom", prenom);
		values.put("mail", mail);
		values.put("fonction", fonction);
		values.put("profile", profile);
		values.put("password", password);
		values.put("etat", etat);
		values.put("connecte", connecte);
		dbsql.insert("utilisateur", null, values);
		
	}
/**
 * modifier utilisateur
 * @param id
 * @param nom
 * @param prenom
 * @param fonction
 * @param password
 * @param etat
 * @param connecte
 */
public void setUser(int id, String nom, String prenom, String fonction, String password, int etat, int connecte){
	
	ContentValues values = new ContentValues();
	values.put("nom",nom);
	values.put("prenom", prenom);
	values.put("fonction", fonction);
	values.put("password", password);
	values.put("etat", etat);
	values.put("connecte", connecte);
	dbsql.update("utilisateur", values, "id=" + id, null);
}

/**
 * recuperer utilisateur par son mail
 * @param mail
 * @return
 */
	public Utilisateur getUser(String mail) {
		
		Utilisateur user;
		Cursor c = dbsql.query("utilisateur", new String[]{
				"id",
				"nom",
				"prenom",
				"mail",
				"fonction",
				"profile",
				"password",
				"etat"}, "mail like "+"'"+mail+"'", null, null, null, null);
		if(c.getCount()>0){
			c.moveToFirst();
			user = new Utilisateur(Integer.valueOf(c.getString(0)), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), Integer.valueOf(c.getString(7)));
			return user;
		}
		return null;
			
	}
	/**
	 * recuperer utilisateur par son id
	 * @param iduser
	 * @return
	 */
public Utilisateur getUser(int iduser) {
		
		Utilisateur user;
		Cursor c = dbsql.query("utilisateur", new String[]{
				"id",
				"nom",
				"prenom",
				"mail",
				"fonction",
				"profile",
				"password",
				"etat"}, "id ="+iduser, null, null, null, null);
		if(c.getCount()>0){
			c.moveToFirst();
			user = new Utilisateur(Integer.valueOf(c.getString(0)), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), Integer.valueOf(c.getString(7)));
			return user;
		}
		return null;
			
	}

	/**
	 * supprimer utilisateur
	 * @param id
	 * @return
	 */
	public boolean deleteUser(int id) {
		return dbsql.delete("utilisateur", "id = "+id, null)>0 ;
		
	}
	

}
