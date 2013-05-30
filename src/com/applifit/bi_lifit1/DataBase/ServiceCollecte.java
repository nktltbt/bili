package com.applifit.bi_lifit1.DataBase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.os.StrictMode;

import com.applifit.bi_lifit1.Config;
import com.applifit.bi_lifit1.ConnectionDetector;
import com.applifit.bi_lifit1.Main;
import com.applifit.bi_lifit1.R;
import com.applifit.bi_lifit1.formulaire.DbElement;
import com.applifit.bi_lifit1.formulaire.DbValeur;

/**
 * classe responsable de la synchronisation en arri�re plan de collecte
 * 
 * @author Faissal
 * 
 */
public class ServiceCollecte extends Service {

	boolean started;
	TimerTask task;
	Timer timer;

	NotificationManager notificationManager;
	ConnectionDetector cd;
	int data = 0;
	ArrayList<Integer> idForm = new ArrayList<Integer>();
	ArrayList<Integer> idice = new ArrayList<Integer>();
	List<Integer> idFormOn = new ArrayList<Integer>();
	String strerr = "ok";

	public class MyBinder extends Binder {
		ServiceCollecte getService() {
			return ServiceCollecte.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		timer = new Timer();
		task = new TimerTask() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// ______________________Debut code synchronisation
				// __________________________________________

				notificationManager = (NotificationManager) ServiceCollecte.this
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancelAll();

				// detecter l'utilisateur connect�
				DbInstall connecte = new DbInstall(ServiceCollecte.this);
				connecte.open();
				String option = connecte.getoptionApp("connecte");
				if (!option.equals("")) {// s'il y a un utilisateur connect�
					int iduser = Integer.valueOf(option);

					cd = new ConnectionDetector(getApplicationContext());

					// teste l'existance d'internet
					if (cd.isConnectingToInternet()) {
						// recuperer les valeurs collecter en locale
						DbValeur valeurDb = new DbValeur(ServiceCollecte.this);
						valeurDb.open();
						DbElement elementdb = new DbElement(
								ServiceCollecte.this);
						elementdb.open();
						Cursor c = valeurDb.getAllValeur();
						if (c.getCount() > 0) {
							c.moveToFirst();
							while (c.isAfterLast() == false) {
								StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
										.permitAll().build();
								StrictMode.setThreadPolicy(policy);
								// generer l'url pour envoyer les donn�es
								// collect�es
								String valeur = c.getString(1);
								int version = c.getInt(6);
								int ide = c.getInt(2);
								int indice = c.getInt(5);
								Cursor localCursor2 = elementdb
										.getElementsid(ide);
								localCursor2.moveToFirst();
								int idFo = localCursor2.getInt(4);
								// la fonction URLEncoder.encode remplace les
								// espaces par des +
								boolean ok = true;
								Iterator<Integer> it1 = idForm.iterator();
								Iterator<Integer> it2 = idice.iterator();
								while (it1.hasNext()) {
									int idce = it2.next();
									int ifo = it1.next();
									if ((ifo == idFo) && (indice == idce)) {
										ok = false;
									}
								}

								if (ok) {
									idForm.add(idFo);
									idice.add(indice);
								}
								valeur = valeur.replace(" ", "*_#*");
								try {
									valeur = URLEncoder.encode(valeur, "utf-8");
								} catch (UnsupportedEncodingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								valeur = valeur.replace("*_%23*", "%20");
								if (version == 0) {
									data++;
									String rq = Config.URL
											+ "element_valeur/ajouterValeur"
											+ "/valeur/" + valeur + "/element/"
											+ c.getInt(2) + "/compte/"
											+ c.getInt(3) + "/date/"
											+ c.getString(4) + "/indice/"
											+ c.getInt(5);

									HttpClient httpclient = new DefaultHttpClient();
									try {
										HttpResponse response = httpclient
												.execute(new HttpGet(rq));
									} catch (ClientProtocolException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (version == 1 || version == 2) {
									data++;
									String rq = Config.URL
											+ "element_valeur/modifierValeur/valeur/"
											+ valeur + "/element/"
											+ c.getInt(2) + "/indice/"
											+ c.getInt(5);
									HttpPut put = new HttpPut(rq);
									HttpClient httpclient = new DefaultHttpClient();
									put.addHeader("pragma", "no-cache");
									try {
										HttpResponse response = httpclient
												.execute(put);
									} catch (Exception e) {
										// TODO: handle exception
									}
								}

								c.moveToNext();
							}
							while (idice.size() > 10) {
								int ind = idice.get(0);
								int idF = idForm.get(0);
								Cursor curelement = elementdb.getElements(idF);
								curelement.moveToFirst();
								while (!curelement.isAfterLast()) {
									int idElement = curelement.getInt(0);
									valeurDb.deleteValeur(idElement, ind);
								}
								idice.remove(0);
								idForm.remove(0);
							}
							try {
								while (idice.size() >= 0) {
									int ind = idice.get(0);
									int idF = idForm.get(0);
									Cursor curelement = elementdb
											.getElements(idF);
									curelement.moveToFirst();
									while (!curelement.isAfterLast()) {
										int idElement = curelement.getInt(0);
										Cursor cur = valeurDb.getValeursindice(
												idElement, ind);
										cur.moveToFirst();
										String valeur = cur.getString(0);
										String date = cur.getString(4);
										int idU = cur.getInt(3);
										valeurDb.deleteValeur(idElement, ind);
										valeurDb.insertValeur(valeur,
												idElement, idU, date, ind, 3);
										// valeurDb.update(idElement, ind, 3);
									}
									idice.remove(0);
									idForm.remove(0);
								}
							} catch (Exception e) {
								strerr = e.getMessage();
							}

						}

						// Notification -----------------------------------
						if (data != 0) {
							int NOTFICATION_ID = 198991;
							NotificationManager nm = null;
							nm = (NotificationManager) getSystemService(ServiceCollecte.this.NOTIFICATION_SERVICE);
							// String MyText = "Bi-lifit : Synchronisation!!";
							String MyText = "Bi-lifit:" + strerr;
							String TitleText = "Synchronisation";
							String NotiText = "Données : " + data;
							Notification mNotification = new Notification(
									R.drawable.ic_form_notif, MyText,
									System.currentTimeMillis());
							mNotification.vibrate = new long[] { 0, 100, 25,
									100 };
							mNotification.ledARGB = Color.RED;
							Intent MyIntent = new Intent(
									getApplicationContext(), Main.class);

							MyIntent.putExtra("extendedTitle", TitleText);
							MyIntent.putExtra("extendedText", NotiText);

							PendingIntent StartIntent = PendingIntent
									.getActivity(getApplicationContext(), 0,
											MyIntent, 0);
							mNotification.setLatestEventInfo(
									getApplicationContext(), TitleText,
									NotiText, StartIntent);
							nm.notify(NOTFICATION_ID, mNotification);
						}
						data = 0;

					}
				}

			}
		};

	}

	@Override
	public void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}

	public void onStart(Intent intent, int startId) {
		if (started == false) {
			// le service de synchronisation se lance apr�s dis secondes
			timer.scheduleAtFixedRate(task, 0, 10000);
			started = true;
		}
		super.onStart(intent, startId);
	}

}
