package com.applifit.bi_lifit1;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.LinearLayout;

import com.applifit.bi_lifit1.R;
import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.MyService;
import com.applifit.bi_lifit1.DataBase.ServiceCollecte;
import com.applifit.bi_lifit1.formulaire.DbValeur;

/**
 * l'activité d'enregistrement de données en locale
 * 
 * @author Faissal
 * 
 */
public class ActivityEnregistrer extends Activity {

	int iduser;
	ProgressDialog progressBar;
	int progressBarStatus = 0; // la valeur du statut de la progressbar
	Handler progressBarHandler = new Handler();
	String option;
	String s = "";

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
		if (option.equals("")) {
			Intent main = new Intent(this, Main.class);
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		} else

			// ------------------- Progress bar -------------------------

			// preparation de la progress bar dialog
			progressBar = new ProgressDialog(this);
		progressBar.setCancelable(false);
		progressBar.setMessage("Enregistrement de donnees ...");
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
				String s = "";

				// recuperer la date d'enregistrement et un indice unique
				Calendar c = Calendar.getInstance();
				int m = c.get(Calendar.MONTH) + 1;
				int a = c.get(Calendar.YEAR);
				int j = c.get(Calendar.DAY_OF_MONTH);
				int h = c.get(Calendar.HOUR_OF_DAY);
				int min = c.get(Calendar.MINUTE);
				int sec = c.get(Calendar.SECOND);
				int milsec = c.get(Calendar.MILLISECOND);
				String M;
				String J;
				String H;
				String Min;
				if (m < 10)
					M = "0" + m;
				else
					M = "" + m;
				if (j < 10)
					J = "0" + j;
				else
					J = "" + j;
				if (h < 10)
					H = "0" + h;
				else
					H = "" + h;
				if (min < 10)
					Min = "0" + min;
				else
					Min = "" + min;

				String date = a + "-" + M + "-" + J + "%20" + h + "%3A" + min;

				int indice = ((((c.get(Calendar.DAY_OF_MONTH) * 24) + c
						.get(Calendar.HOUR_OF_DAY) * 60) + c
						.get(Calendar.MINUTE) * 60) + c.get(Calendar.SECOND) * 500)
						+ c.get(Calendar.MILLISECOND);

				// recuperer les données envoyées par l'activité
				// RemplirForm.java
				Bundle extra = getIntent().getExtras();
				int version;
				version = extra.getInt("version");
				int idc = 0;
				if (!(version == 0)) {
					idc = extra.getInt("indice");
				}
				// parcour les données et les enregistrer dans la base locale
				for (int i = 0; i < extra.getInt("count"); i++) {
					s = extra.getString("element" + i);
					if (s != null) {
						String[] v = s.split("#0_0#"); // v[0] -> la valeur ,
														// v[1] -> l'id du champ
														// contenant la valeur
						if (version == 0) {
							val.insertValeur(v[0], Integer.valueOf(v[1]),
									iduser, date, indice, 0);
						} else if (version == 1) {
							val.deleteValeur(Integer.valueOf(v[1]), idc);
							val.insertValeur(v[0], Integer.valueOf(v[1]),
									iduser, date, idc, 1);
						} else if (version == 2) {
							try {
								val.deleteValeur(Integer.valueOf(v[1]), idc);
							} catch (Exception e) {
								// TODO: handle exception
							}
							val.insertValeur(v[0], Integer.valueOf(v[1]),
									iduser, date, idc, 2);
						}
					}
					// parametrer le statut de la progressBar
					progressBarStatus += 100 / extra.getInt("count");
					progressBarHandler.post(new Runnable() {
						public void run() {
							progressBar.setProgress(progressBarStatus);
						}
					});

				}
				progressBarStatus = 100;
				if (progressBarStatus >= 100) {
					// quitter la progressBar dialog
					progressBar.dismiss();
					finish();
					// lancer le service de synchronisation en arriere plan
					// après une minute
					Intent bindIntent = new Intent(ActivityEnregistrer.this,
							ServiceCollecte.class);
					startService(bindIntent);
					// retour à l'accueil
					Intent intent = new Intent(ActivityEnregistrer.this,
							MainActivity.class);
					intent.putExtra("enregistrement", 2);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
					overridePendingTransition(R.anim.fadin, R.anim.fadout);

				}
			}
		}).start();

		// ------------- Fin progress
		// ----------------------------------------------

		LinearLayout layout = new LinearLayout(this);
		setContentView(layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
