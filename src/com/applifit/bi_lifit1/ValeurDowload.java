package com.applifit.bi_lifit1;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.GetHTTP;
import com.applifit.bi_lifit1.DataBase.Syncronisation;
import com.applifit.bi_lifit1.compte.Utilisateur;
import com.applifit.bi_lifit1.formulaire.DbValeurUnit;

public class ValeurDowload extends Activity {
	private static final String ELEMENT_URL = Config.URL + "element/";
	private static final String FORMULAIRE_URL = Config.URL + "formulaire/";
	private static final String PARAM_URL = Config.URL + "parametre/";
	private static final String VALUE_URL = Config.URL + "element_valeur/";
	ConnectionDetector cd;
	int cree = 0;
	DbValeurUnit dbfu;
	String element;
	String elementsAsString = "";
	int err = 0;
	long formulaire_id;
	int idF;
	List<Integer> idFormOn = new ArrayList();
	int indice;
	NotificationManager notificationManager;
	String option;
	ProgressDialog progressBar;
	Handler progressBarHandler = new Handler();
	int progressBarStatus = 0;
	Utilisateur user;
	int iduser;
	String strerr = "ff";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_valeur_dowload);
		Bundle localBundle = getIntent().getExtras();
		this.idF = localBundle.getInt("idF");
		this.indice = localBundle.getInt("indice");
		this.notificationManager = ((NotificationManager) getSystemService("notification"));
		this.notificationManager.cancelAll();
		DbInstall localDbInstall = new DbInstall(this);
		localDbInstall.open();
		this.option = localDbInstall.getoptionApp("connecte");
		if (this.option.equals("")) {
			finish();
			Intent localIntent = new Intent(this, Main.class);
			localIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(localIntent);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
			return;
		}
		this.progressBar = new ProgressDialog(this);
		this.progressBar.setCancelable(false);
		this.progressBar.setMessage("Synchronisation ...");
		this.progressBar.setProgressStyle(1);
		this.progressBar.setProgress(0);
		this.progressBar.setMax(100);
		this.progressBar.show();
		this.dbfu = new DbValeurUnit(this);
		this.dbfu.open();
		new Thread(new Runnable() {
			public void run() {
				try {
					String[] arrayOfString;
					if (new ConnectionDetector(getApplicationContext())
							.isConnectingToInternet()) {
						String str = VALUE_URL
								+ "getValuesParIndiceString/indice/" + indice;
						GetHTTP req = new GetHTTP(str);
						arrayOfString = req.getResultat().split(",");
						dbfu.deleteAll();
						if (arrayOfString.length > 1) {
							int i = 0;
							while (i + 4 < arrayOfString.length) {
								dbfu.insertValeur(arrayOfString[i + 4],
										Integer.valueOf(arrayOfString[i + 2]),
										Integer.valueOf(arrayOfString[i + 1]),
										arrayOfString[i + 5],
										Integer.valueOf(arrayOfString[i + 3]));

								i = i + 6;
							}

						}
						progressBarStatus = 100;
						progressBarHandler.post(new Runnable() {
							public void run() {
								progressBar.setProgress(progressBarStatus);
							}
						});
						if (progressBarStatus >= 100) {
							progressBar.dismiss();
							finish();
							Intent localIntent3 = new Intent(
									ValeurDowload.this, ConsulForm.class);
							localIntent3
									.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							localIntent3.putExtra("message", strerr);
							localIntent3.putExtra("idF", idF);
							localIntent3.putExtra("indice", indice);
							startActivity(localIntent3);
							overridePendingTransition(R.anim.fadin,
									R.anim.fadout);
						}
					} else {
						// TODO

					}
				} catch (Exception e) {
					progressBar.dismiss();
					finish();
					Intent localIntent3 = new Intent(ValeurDowload.this,
							Profile.class);
					localIntent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					localIntent3.putExtra("message", e.getMessage() + "xxxx");
					localIntent3.putExtra("idF", idF);
					localIntent3.putExtra("indice", indice);
					startActivity(localIntent3);
					overridePendingTransition(R.anim.fadin, R.anim.fadout);
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.deconnecte:
			// teste de l'existance d'internet
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());
			if (cd.isConnectingToInternet()) {
				// liberer le compte utilisateur de leur mobile
				Syncronisation sync = new Syncronisation();
				sync.modifDevice(iduser, null);
				finish(); // fermer l'activité
				// fermer la session
				DbInstall connecte = new DbInstall(this);
				connecte.open();
				connecte.deleteoptionApp("connecte");
				Intent IntentDeconnecte = new Intent(this, Main.class);
				IntentDeconnecte.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(IntentDeconnecte);
				overridePendingTransition(R.anim.fadin, R.anim.fadout);
			} else
				Toast.makeText(this, " pas de connéxion internet",
						Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
