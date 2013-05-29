package com.applifit.bilifit.formulaire;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.applifit.bilifit.DataBase.DbAdapter;

public class DbFormulaireUnit
{
  Context context;
  DbAdapter dbOpenHelper;
  SQLiteDatabase dbsql;

  public DbFormulaireUnit(Context paramContext)
  {
    this.dbOpenHelper = new DbAdapter(paramContext);
    this.context = paramContext;
  }

  public boolean deleteFormulaire()
  {
    try
    {
      int i = this.dbsql.delete("formulaireunit", null, null);
      return i > 0;
    }
    catch (Exception localException)
    {
    }
    return true;
  }

  public boolean deleteFormulaire(int paramInt)
  {
    return this.dbsql.delete("formulaireunit", "id = " + paramInt, null) > 0;
  }

  public Cursor getAllFormulaire()
  {
    return this.dbsql.query("formulaireunit", new String[] { "idF", "id", "indice", "nom", "prenom", "date" }, null, null, null, null, null);
  }

  public Cursor getAllIdFormulaire()
  {
    return this.dbsql.query("formulaireunit", new String[] { "indice" }, null, null, null, null, null);
  }

  public void insertformulaireUnit(int idF, int id, int indice, String nom, String prenom, String date)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("idF", Integer.valueOf(idF));
    localContentValues.put("id", Integer.valueOf(id));
    localContentValues.put("indice", Integer.valueOf(indice));
    localContentValues.put("nom", nom);
    localContentValues.put("prenom", prenom);
    localContentValues.put("date", date);
    this.dbsql.insert("formulaireunit", null, localContentValues);
  }

  public DbFormulaireUnit open()
  {
    this.dbsql = this.dbOpenHelper.getWritableDatabase();
    return this;
  }
}

/* Location:           C:\Documents and Settings\Administrateur\Bureau\dex2jar-0.0.9.14\dex2jar-0.0.9.14\Bilifitmob-dex2jar.jar
 * Qualified Name:     com.applifit.bilifit.formulaire.DbFormulaireUnit
 * JD-Core Version:    0.6.2
 */