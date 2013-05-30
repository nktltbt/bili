package com.applifit.bi_lifit1.DataBase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;

import com.applifit.bi_lifit1.ConnectionDetector;
import com.applifit.bi_lifit1.Main;
import com.applifit.bi_lifit1.MainActivity;
import com.applifit.bi_lifit1.Profile;
import com.applifit.bi_lifit1.R;
import com.applifit.bi_lifit1.formulaire.DbElement;
import com.applifit.bi_lifit1.formulaire.DbFormulaire;
import com.applifit.bi_lifit1.formulaire.DbParametre;
import com.applifit.bi_lifit1.formulaire.Element;
import com.applifit.bi_lifit1.formulaire.Formulaire;
import com.applifit.bi_lifit1.formulaire.Parametre;

/**
 * classe qui synchronise les formulaires après l'authentification d'utilisateur
 * ou le clique sur le bouton synchroniser de la page du profile
 * 
 * @author Faissal
 * 
 */
public class Install extends Activity {

	NotificationManager notificationManager;
	ConnectionDetector cd;
	int cree = 0;

	List<Integer> idFormOn = new ArrayList<Integer>();

	ProgressDialog progressBar;
	int progressBarStatus = 0;
	Handler progressBarHandler = new Handler();

	String option;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_install);
		overridePendingTransition(R.anim.fadin, R.anim.fadout);

		notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		// detecter l'utilisateur connecté
		DbInstall connecte = new DbInstall(this);
		connecte.open();
		option = connecte.getoptionApp("connecte");
		if (option.equals("")) { // s'il n y a pas d'utilisateur connecté
			finish();
			Intent main = new Intent(this, Main.class);
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		} else {
			// ------------------- Progress bar -------------------------

			// preparation de la progressBar dialog
			progressBar = new ProgressDialog(this);
			progressBar.setCancelable(false);
			progressBar.setMessage("Synchronisation ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.show();

			new Thread(new Runnable() {
				public void run() {
					int iduser = Integer.valueOf(option);

					cd = new ConnectionDetector(getApplicationContext());

					if (cd.isConnectingToInternet()) { // teste l'existance
														// d'internet
						DbFormulaire formDb = new DbFormulaire(Install.this);
						formDb.open();

						// recuperer les formulaires d'utilisateur en ligne
						Syncronisation sync = new Syncronisation();
						List<Formulaire> list_form = sync
								.recuperFormulaire(iduser);
						int indice;
						if (list_form.size() != 0)
							indice = (100 / list_form.size());
						else
							indice = 100;

						Iterator<Formulaire> fr = list_form.iterator();
						while (fr.hasNext()) {
							Formulaire formOn = (Formulaire) fr.next();
							Formulaire formOff = formDb.getFormulaire(formOn
									.getId());
							// s'il y a un nouveau formulaire ou une autre
							// version
							if (formOff == null) {
								Formulaire f = (formDb
										.getFormulaireByNom(formOn.getNom()));
								if (f != null)
									formDb.deleteFormulaire(f.getId());

								formDb.insertformulaire(formOn.getId(),
										formOn.getNom(),
										formOn.getCommentaire(),
										formOn.getVersion(),
										formOn.getIduser(), formOn.getEtat());
								insererElmentFormulaire(formOn.getId());
								cree++;
							}
							idFormOn.add(formOn.getId());

							progressBarStatus += indice;
							progressBarHandler.post(new Runnable() {
								public void run() {
									progressBar.setProgress(progressBarStatus);
								}
							});

						}

						// supprimer les formulaires supprimés en ligne
						Cursor cur = formDb.getAllIdFormulaire();
						int exist = 0;
						if (cur.getCount() > 0) {
							cur.moveToFirst();
							while (cur.isAfterLast() == false) {
								Iterator<Integer> i = idFormOn.iterator();
								while (i.hasNext()) {
									int h = i.next();
									if (h == cur.getInt(0))
										exist = 1;
								}
								if (exist == 0) {
									formDb.deleteFormulaire(cur.getInt(0));
								} else
									exist = 0;
								cur.moveToNext();
							}
						}

						progressBarStatus = 100;
						progressBarHandler.post(new Runnable() {
							public void run() {
								progressBar.setProgress(progressBarStatus);
							}
						});

						// notification
						if (cree != 0) {
							int NOTFICATION_ID = 198990;
							NotificationManager nm = null;
							nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
							String MyText = "Bi-lifit : Synchronisation!!";
							String TitleText = "Synchronisation";
							String NotiText = "Formulaire : " + cree;
							Notification mNotification = new Notification(
									R.drawable.ic_form_notif, MyText, System
											.currentTimeMillis());
							Intent MyIntent = new Intent(
									getApplicationContext(), Main.class);

							PendingIntent StartIntent = PendingIntent
									.getActivity(getApplicationContext(), 0,
											MyIntent, 0);
							mNotification.setLatestEventInfo(
									getApplicationContext(), TitleText,
									NotiText, StartIntent);
							nm.notify(NOTFICATION_ID, mNotification);
						}

						DbInstall install = new DbInstall(Install.this);
						install.open();
						install.insertoptionApp("installation", "ok");
					}
					if (progressBarStatus >= 100) {
						// close the progress bar dialog
						progressBar.dismiss();
						finish();

						 Intent bindIntent = new Intent(Install.this,
						 MyService.class);
						 startService(bindIntent);

						Bundle extra = getIntent().getExtras();
						Intent intent = new Intent(Install.this,
								MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						if (extra != null)
							intent.putExtra("current", extra.getInt("to"));
						startActivity(intent);
						overridePendingTransition(R.anim.fadin, R.anim.fadout);
					}
				}
			}).start();

		}

	}

	/**
	 * ajouter les champs
	 * 
	 * @param idform
	 */
	private void insererElmentFormulaire(int idform) {
		DbElement elementdb = new DbElement(this);
		elementdb.open();

		Syncronisation sync = new Syncronisation();
		List<Element> list_element = sync.recuperElement(idform);

		Iterator<Element> elem = list_element.iterator();
		while (elem.hasNext()) {

			// inserer les element dans la base
			Element element = (Element) elem.next();
			elementdb.insertElement(element.getId(), element.getType(),
					element.getPosition_x(), element.getPosition_y(), idform);
			insererParametreElment(element.getId());

		}

	}

	/**
	 * ajouter les parametres d'un champ
	 * 
	 * @param idElement
	 */
	private void insererParametreElment(int idElement) {
		DbParametre parametredb = new DbParametre(this);
		parametredb.open();

		Syncronisation sync = new Syncronisation();
		List<Parametre> list_parametre = sync.recuperParametre(idElement);

		Iterator<Parametre> parm = list_parametre.iterator();
		while (parm.hasNext()) {
			// inserer les element dans la base
			Parametre parametre = (Parametre) parm.next();
			parametredb.insertParametre(parametre.getId(), parametre.getNom(),
					parametre.getValeur(), idElement);
		}

	}
}
