package com.applifit.bilifit;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.LinearLayout;

import com.applifit.bilifit.DataBase.DbInstall;
import com.applifit.bilifit.DataBase.MyService;
import com.applifit.bilifit.DataBase.ServiceCollecte;
import com.applifit.bilifit.formulaire.DbValeur;

/**
 * l'activité d'enregistrement de données en locale
 * @author Faissal
 *
 */
public class ActivityEnregistrer extends Activity {
	
	int iduser;
	ProgressDialog progressBar;
	int progressBarStatus = 0; //la valeur du statut de la progressbar
	Handler progressBarHandler = new Handler(); 
	String option;
	String s="";

	/**
	 * au creation de l'actiyité
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setVisible(false);
		
		DbInstall connecte = new DbInstall(this);
		connecte.open();
		option = connecte.getoptionApp("connecte");
		if(option.equals("")) {
			Intent main = new Intent(this, Main.class);	
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		}
		else 
			
//		    -------------------  Progress bar -------------------------
			
							// preparation de la progress bar dialog
							progressBar = new ProgressDialog(this);
							progressBar.setCancelable(false);
							progressBar.setMessage("Enregistrement de donnŽes ...");
							progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
							progressBar.setProgress(0);
							progressBar.setMax(100);
							progressBar.show();
	/**
	 * thread à lancer pour enregistrer le données 		
	 */
	new Thread(new Runnable() {
		  public void run() {	
			iduser = Integer.valueOf(option);
			
			DbValeur val = new DbValeur(ActivityEnregistrer.this);
			val.open();
			String s="";
			
			//recuperer la date d'enregistrement et un indice unique
			Calendar c = Calendar.getInstance();
			int m = c.get(Calendar.MONTH) + 1;
			String date = c.get(Calendar.YEAR)+"-"+m+"-"+c.get(Calendar.DAY_OF_MONTH)+"%20"+c.get(Calendar.HOUR_OF_DAY)+"%3A"+c.get(Calendar.MINUTE);
			int indice = c.get(Calendar.YEAR)+c.get(Calendar.MONTH)+c.get(Calendar.DAY_OF_MONTH)+c.get(Calendar.HOUR_OF_DAY)+c.get(Calendar.MINUTE)+c.get(Calendar.SECOND)+c.get(Calendar.MILLISECOND);
			
			//recuperer les données envoyées par l'activité RemplirForm.java
			Bundle extra = getIntent().getExtras();	
			
			//parcour les données et les enregistrer dans la base locale
			for(int i=0; i<extra.getInt("count"); i++){
				s=extra.getString("element"+i);
				if(s!=null){
					String[] v = s.split("#0_0#");  //v[0] -> la valeur , v[1] -> l'id du champ contenant la valeur
					val.insertValeur(v[0], Integer.valueOf(v[1]), iduser,date, indice);
				}
				//parametrer le statut de la progressBar
				 progressBarStatus+=100/extra.getInt("count");
				  progressBarHandler.post(new Runnable() {
						public void run() {
						  progressBar.setProgress(progressBarStatus);
						}
					  });
			  
			}
			progressBarStatus=100;
		if (progressBarStatus >= 100) {
			// quitter la progressBar dialog
			progressBar.dismiss();
			finish();
			// lancer le service de synchronisation en arriere plan après une minute
			Intent bindIntent = new Intent(ActivityEnregistrer.this, ServiceCollecte.class);
			startService(bindIntent);
			// retour à l'accueil
			Intent intent = new Intent(ActivityEnregistrer.this, MainActivity.class);
				intent.putExtra("enregistrement", 2);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				overridePendingTransition(R.anim.fadin, R.anim.fadout);
			
		}
	  }
       }).start();
							
// ------------- Fin progress ----------------------------------------------
	
		LinearLayout layout = new LinearLayout(this);
		setContentView(layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
