package com.applifit.bi_lifit1;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.applifit.bi_lifit1.R;
import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.GetHTTP;
import com.applifit.bi_lifit1.compte.DbUtilisateur;
import com.applifit.bi_lifit1.compte.Utilisateur;
import com.applifit.bi_lifit1.formulaire.DbElement;
import com.applifit.bi_lifit1.formulaire.DbFormulaireUnit;

public class FormDownload extends Activity {

	private static final String VALUE_URL = Config.URL + "element_valeur/";
	ConnectionDetector cd;
	int iduser;
	ProgressDialog progressBar;
	int progressBarStatus = 0; // la valeur du statut de la progressbar
	Handler progressBarHandler = new Handler();
	DbFormulaireUnit dbfu;
	String element;
	String elementsAsString = "";
	Utilisateur user;
	long formulaire_id;
	int idF;
	List<Integer> idFormOn = new ArrayList();
	NotificationManager notificationManager;
	String option;
	String strerr = "";

	/**
	 * au creation de l'actiyité
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setVisible(false);
		setContentView(R.layout.activity_form_download);
		overridePendingTransition(R.anim.fadin, R.anim.fadout);
		this.idF = getIntent().getExtras().getInt("id");
		this.notificationManager = ((NotificationManager) getSystemService("notification"));
		this.notificationManager.cancelAll();
		DbInstall connecte = new DbInstall(this);
		connecte.open();
		option = connecte.getoptionApp("connecte");
		if (option.equals("")) {
			finish();
			Intent main = new Intent(this, Main.class);
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		}
		// ------------------- Progress bar -------------------------

		// preparation de la progress bar dialog
		progressBar = new ProgressDialog(this);
		progressBar.setCancelable(false);
		progressBar.setMessage("Télécharcher des donnée ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();
		/**
		 * thread à lancer pour enregistrer le données
		 */
		DbUtilisateur localDbUtilisateur = new DbUtilisateur(this);
		localDbUtilisateur.open();
		this.user = localDbUtilisateur.getUser(Integer.valueOf(this.option)
				.intValue());
		this.element = "";
		DbElement localDbElement = new DbElement(this);
		localDbElement.open();
		Cursor localCursor = localDbElement.getElements(this.idF);

		if (localCursor.getCount() > 0) {
			localCursor.moveToFirst();
			this.element = localCursor.getInt(0) + "";
			localCursor.moveToNext();

			while (!localCursor.isAfterLast()) {
				this.element = (this.element + "-" + localCursor.getInt(0));
				localCursor.moveToNext();
			}
			this.dbfu = new DbFormulaireUnit(this);
			this.dbfu.open();
			progressBar = new ProgressDialog(this);
			progressBar.setCancelable(false);
			progressBar.setMessage("Télécharcher des données ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						strerr=strerr+"1";
						int i = Integer.valueOf(FormDownload.this.option)
								.intValue();
						int j = FormDownload.this.idF;
						cd = new ConnectionDetector(FormDownload.this
								.getApplicationContext());
						String str;
						String[] arrayOfString;
						int k;
						strerr=strerr+"1";
						if (FormDownload.this.cd.isConnectingToInternet()) {
							strerr=strerr+"1";
							if ((FormDownload.this.user.getProfile()
									.equals("CA"))) {
								str = FormDownload.VALUE_URL
										+ "recupererValues/elements/"
										+ FormDownload.this.element;
							} else
								str = FormDownload.VALUE_URL
										+ "recupererValues/elements/"
										+ FormDownload.this.element
										+ "/compte/" + i;
							strerr=strerr+str;
							GetHTTP localGetHTTP = new GetHTTP(str);
							arrayOfString = localGetHTTP.getResultat().split(
									",");
							dbfu.deleteFormulaire();
							if (arrayOfString.length > 1) {
								k = 0;
								while (k + 4 < arrayOfString.length) {
									
									int m = Integer.parseInt(arrayOfString[k]);
									int n = Integer
											.parseInt(arrayOfString[(k + 1)]);
									strerr=strerr+n+"xx";
									dbfu.insertformulaireUnit(j, m, n,
											arrayOfString[(k + 2)],
											arrayOfString[(k + 3)],
											arrayOfString[(k + 4)]);
									k = k + 5;
								}
							}
						} else {
							// TODO
						}

					} catch (Exception e) {
						strerr = "xxxx" + e.getMessage();
					}
					progressBarStatus = 100;
					if (progressBarStatus >= 100) {
						// quitter la progressBar dialog
						progressBar.dismiss();
						finish();
						Intent intent = new Intent(FormDownload.this,
								FormWeb.class);
//						intent.putExtra("message", strerr + "y");
						intent.putExtra("idF",idF);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(intent);
						overridePendingTransition(R.anim.fadin, R.anim.fadout);
					}

				}
			}).start();

		} else {
			// TODO
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
