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
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.os.StrictMode;

import com.applifit.bi_lifit1.ConnectionDetector;
import com.applifit.bi_lifit1.Main;
import com.applifit.bi_lifit1.R;
import com.applifit.bi_lifit1.formulaire.DbElement;
import com.applifit.bi_lifit1.formulaire.DbFormulaire;
import com.applifit.bi_lifit1.formulaire.DbParametre;
import com.applifit.bi_lifit1.formulaire.DbValeur;
import com.applifit.bi_lifit1.formulaire.Element;
import com.applifit.bi_lifit1.formulaire.Formulaire;
import com.applifit.bi_lifit1.formulaire.Parametre;
/**
 * classe responsable de la synchronisation en arrière plan
 * @author Faissal
 *
 */
public class MyService extends Service {


	
	boolean   started;
	TimerTask task;
	Timer	  timer;
	
	NotificationManager notificationManager;
	ConnectionDetector cd;
	int cree=0;

	List<Integer> idFormOn = new ArrayList<Integer>();
	
	
	public class MyBinder extends Binder{
		MyService getService(){
			return MyService.this;
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
		task  = new TimerTask(){			

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// ______________________Debut  code synchronisation __________________________________________
				
				 notificationManager = (NotificationManager)MyService.this.getSystemService(Context.NOTIFICATION_SERVICE);
			     notificationManager.cancelAll(); 

			   //detecter l'utilisateur connecté
				DbInstall connecte = new DbInstall(MyService.this);
				connecte.open();
				String option = connecte.getoptionApp("connecte");
				if(!option.equals("")) {// s'il y a un utilisateur connecté
					int iduser = Integer.valueOf(option);
				
					cd = new ConnectionDetector(getApplicationContext());
					
					 //teste l'existance d'internet
					if(cd.isConnectingToInternet()){
						DbFormulaire formDb = new DbFormulaire(MyService.this);
						formDb.open();
						
						
						
						//recuperer les formulaires d'utilisateur en ligne
						Syncronisation sync = new Syncronisation();
						List<Formulaire> list_form=  sync.recuperFormulaire(iduser);
						
						Iterator<Formulaire> fr = list_form.iterator();
						while(fr.hasNext()){
						  Formulaire formOn = (Formulaire) fr.next();
						  Formulaire formOff = formDb.getFormulaire(formOn.getId());
						//s'il y a un nouveau formulaire ou une autre version 
//						  if(formOn.getEtat()==1 || formOn.getEtat()==2){
							  if(formOff==null){
								  Formulaire f= (formDb.getFormulaireByNom(formOn.getNom()));
								  if(f!=null) formDb.deleteFormulaire(f.getId());
								  
								  formDb.insertformulaire(formOn.getId(), formOn.getNom(),formOn.getCommentaire(), formOn.getVersion(), formOn.getIduser(), formOn.getEtat());
							         insererElmentFormulaire(formOn.getId());
							         cree++;
							  }
							  idFormOn.add(formOn.getId());
//						  }
						  	
						} 

						//supprimer les formulaires supprimés en ligne
						Cursor cur = formDb.getAllIdFormulaire();
						int exist=0;
						if(cur.getCount()>0){
							cur.moveToFirst();
							 while (cur.isAfterLast() == false) {
								 Iterator<Integer> i = idFormOn.iterator();
								 while(i.hasNext()){ 
									 int h = i.next();
									 if(h==cur.getInt(0)) exist=1;
								 }
								 if(exist==0) { formDb.deleteFormulaire(cur.getInt(0));  }else exist=0;
								 cur.moveToNext();
							 }
						}
						
						
						// Notification -----------------------------------
						if(cree!=0){
							 int NOTFICATION_ID = 198990; 
							 NotificationManager nm = null; 
							 nm = (NotificationManager) getSystemService(MyService.this.NOTIFICATION_SERVICE);
							 String MyText = "Bi-lifit : Synchronisation!!";    
							 String TitleText = "Synchronisation";
							 String NotiText  = "Formulaires : "+cree;
							 Notification mNotification = new Notification(R.drawable.ic_form_notif,MyText,System.currentTimeMillis());
							 mNotification.vibrate = new long[]{0,100,25,100};
							 mNotification.ledARGB  = Color.RED;
							 Intent MyIntent = new Intent( getApplicationContext(), Main.class);
	
							 MyIntent.putExtra("extendedTitle", TitleText);
							 MyIntent.putExtra("extendedText" , NotiText);           
	
							 PendingIntent StartIntent = PendingIntent.getActivity(getApplicationContext(),0,MyIntent,0);                      
							 mNotification.setLatestEventInfo(getApplicationContext(),TitleText,NotiText, StartIntent);                                           
							 nm.notify(NOTFICATION_ID , mNotification );       
						}
						

						cree=0; 
							
					}
				}
			}						
		};
		
	}
	

	/**
	 *  ajouter les champs
	 * @param idform
	 */
	private void insererElmentFormulaire(int idform) {
		DbElement elementdb = new DbElement(this);
		elementdb.open();
		
		Syncronisation sync = new Syncronisation();
		List<Element> list_element=  sync.recuperElement(idform);
		
		Iterator<Element> elem = list_element.iterator();
		while(elem.hasNext()){
			
			// inserer les element dans la base
			Element element = (Element) elem.next();
			elementdb.insertElement(element.getId(), element.getType(), element.getPosition_x(), element.getPosition_y(), idform);
			insererParametreElment(element.getId());
			
		}
		
		
	}
   /**
    * ajouter les parametres d'un champ
    * @param idElement
    */
	private void insererParametreElment(int idElement) {
		DbParametre parametredb = new DbParametre(this);
		parametredb.open();
		
		Syncronisation sync = new Syncronisation();
		List<Parametre> list_parametre=  sync.recuperParametre(idElement);
		
		
		Iterator<Parametre> parm = list_parametre.iterator();
		while(parm.hasNext()){
			// inserer les element dans la base
			Parametre parametre = (Parametre) parm.next();
			parametredb.insertParametre(parametre.getId(), parametre.getNom(), parametre.getValeur(), idElement);
		}
	
	}
	
	
	
	@Override
	public void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}
	
	public void onStart(Intent intent, int startId) {
		if(started==false){
			//le service de synchronisation se lance après une minute
			timer.scheduleAtFixedRate(task, 60000, 60000);
			started=true;
		}
		super.onStart (intent, startId);
	}

}
