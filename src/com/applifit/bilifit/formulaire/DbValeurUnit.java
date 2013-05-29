package com.applifit.bilifit.formulaire;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.applifit.bilifit.DataBase.DbAdapter;

public class DbValeurUnit
{
  Context context;
  DbAdapter dbOpenHelper;
  SQLiteDatabase dbsql;

  public DbValeurUnit(Context paramContext)
  {
    this.dbOpenHelper = new DbAdapter(paramContext);
    this.context = paramContext;
  }

  public boolean deleteAll()
  {
    return this.dbsql.delete("valeurunit", null, null) > 0;
  }

  public boolean deleteValeur(int paramInt)
  {
    return this.dbsql.delete("valeurunit", "id = " + paramInt, null) > 0;
  }

  public Cursor getAllValeur()
  {
    return this.dbsql.query("valeurunit", new String[] { "id", "valeur", "idElement", "idUser", "date", "indice" }, null, null, null, null, null);
  }

  public Valeur getValeur(int paramInt1, int paramInt2)
  {
    String str = " idelement = " + paramInt1 + " and iduser='" + paramInt2 + "'";
    Cursor localCursor = this.dbsql.query("valeurunit", new String[] { "id", "valeur", "idElement", "idUser", "date", "indice" }, str, null, null, null, null);
    Valeur localValeur = null;
    if (localCursor != null)
    {
      int i = localCursor.getCount();
      localValeur = null;
      if (i != 0)
      {
        localCursor.moveToFirst();
        localCursor.moveToLast();
        localValeur = new Valeur(localCursor.getInt(0), localCursor.getString(1), localCursor.getInt(2), localCursor.getInt(3), localCursor.getString(4), localCursor.getInt(5), 2);
      }
    }
    return localValeur;
  }

  public Cursor getValeurs(int paramInt)
  {
    return this.dbsql.query("valeurunit", new String[] { "id", "valeur", "idElement", "idUser", "date", "indice" }, "idelement = " + paramInt, null, null, null, null);
  }

  public void insertValeur(String paramString1, int paramInt1, int paramInt2, String paramString2, int paramInt3)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("valeur", paramString1);
    localContentValues.put("iduser", Integer.valueOf(paramInt2));
    localContentValues.put("idelement", Integer.valueOf(paramInt1));
    localContentValues.put("date", paramString2);
    localContentValues.put("indice", Integer.valueOf(paramInt3));
    this.dbsql.insert("valeurunit", null, localContentValues);
  }

  public DbValeurUnit open()
  {
    this.dbsql = this.dbOpenHelper.getWritableDatabase();
    return this;
  }
}

/* Location:           C:\Documents and Settings\Administrateur\Bureau\dex2jar-0.0.9.14\dex2jar-0.0.9.14\Bilifitmob-dex2jar.jar
 * Qualified Name:     com.applifit.bilifit.formulaire.DbValeurUnit
 * JD-Core Version:    0.6.2
 */