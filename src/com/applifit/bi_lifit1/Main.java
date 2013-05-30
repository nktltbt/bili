package com.applifit.bi_lifit1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.applifit.bi_lifit1.R;
import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.Install;
import com.applifit.bi_lifit1.DataBase.Syncronisation;
import com.applifit.bi_lifit1.compte.DbUtilisateur;
import com.applifit.bi_lifit1.compte.Utilisateur;

public class Main extends Activity implements OnClickListener {
	
	ConnectionDetector cd;
	DbUtilisateur userDb = new DbUtilisateur(this);
	
	
	
	//Ajouter les différents layouts contenant nos vues(composants)
	LinearLayout Globale;
	LinearLayout layoutTitre;
	LinearLayout entreConnexion;
	LinearLayout layoutConnexion;
	LinearLayout layoutLogin;
	LinearLayout layoutPasse;
	LinearLayout layoutValider;
	
	//les vues
	ImageView logo;
	ImageView icon_login;
	ImageView icon_passe;
	ImageView button;
	EditText textLogin;
	EditText textPasse;
	
	Utilisateur userOn;
	TelephonyManager telephonyManager;
	String IdDevice ;
	
	


	//la fonction oncreate qui se lance automatiquement lors de lancement de l'activité (Main.java)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("");
		
		
		//detecte l'utilisateur connecté
		DbInstall connecte = new DbInstall(this);
		connecte.open();
		String option = connecte.getoptionApp("connecte");
		if(!option.equals("")) {
			finish();
			Intent main = new Intent(this, MainActivity.class);	
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		}
		else {
			

		//instanciation des layouts
		Globale = new LinearLayout(this);
		layoutConnexion = new LinearLayout(this);
		entreConnexion = new LinearLayout(this);
		layoutTitre = new LinearLayout(this);
		layoutLogin = new LinearLayout(this);
		layoutPasse = new LinearLayout(this);
		layoutValider = new LinearLayout(this);
		
		//instanciation des vues
		logo = new ImageView(this);
		icon_login = new ImageView(this);
		icon_passe = new ImageView(this);
		textLogin = new EditText(this);
		textPasse = new EditText(this);
		button = new ImageView(this);
		
		//attribuer l'image du logo
		BitmapDrawable imgLogo = (BitmapDrawable) this.getResources().getDrawable(R.drawable.connexion_logo);
		logo.setImageDrawable(imgLogo);
		
		//attribuer l'image du logo
		BitmapDrawable img_icon_logino = (BitmapDrawable) this.getResources().getDrawable(R.drawable.icon_login_connexion);
		icon_login.setImageDrawable(img_icon_logino);
		
		//attribuer l'image du logo
		BitmapDrawable img_icon_passe = (BitmapDrawable) this.getResources().getDrawable(R.drawable.icon_passe_connexion);
		icon_passe.setImageDrawable(img_icon_passe);
		
		//image du bouton login
		BitmapDrawable btnConnexion = (BitmapDrawable) this.getResources().getDrawable(R.drawable.button);
		button.setImageDrawable(btnConnexion);
//		button.setPadding(5, 20, 0, 0);
		button.setOnClickListener(this);
		
		//configuration des vues : largeur, hauteur, marges, taille du police ...
		
//		textLogin.setWidth(345);
//		textPasse.setWidth(345);
		textLogin.setHeight(30);
		textPasse.setHeight(30);
//		textLogin.setPadding(60, 25, 0, 5);
//		textPasse.setPadding(60, 32, 0, 0);
		textLogin.setTextSize(16);
		textPasse.setTextSize(16);
		textLogin.setBackgroundColor(Color.WHITE);	
		textPasse.setBackgroundColor(Color.WHITE);
		textLogin.setSingleLine(true);
		textPasse.setSingleLine(true);
		textPasse.setTransformationMethod(PasswordTransformationMethod.getInstance());
		
		
		
		//attribuer les vues à ses layouts
		layoutTitre.addView(logo);
		layoutLogin.addView(icon_login);
		layoutLogin.addView(textLogin);
		layoutPasse.addView(icon_passe);
		layoutPasse.addView(textPasse);
		layoutValider.addView(button);
		entreConnexion.addView(layoutLogin);
		entreConnexion.addView(layoutPasse);
		entreConnexion.addView(layoutValider);
		layoutConnexion.addView(entreConnexion);
		Globale.addView(layoutTitre);
		Globale.addView(layoutConnexion);
		

		
		//configuration des layouts 
		entreConnexion.setOrientation(LinearLayout.VERTICAL);
		layoutPasse.setOrientation(LinearLayout.HORIZONTAL);
		layoutTitre.setOrientation(LinearLayout.HORIZONTAL);
		layoutLogin.setOrientation(LinearLayout.HORIZONTAL);
		entreConnexion.setGravity(Gravity.RIGHT);
//		layoutValider.setMinimumWidth(380);
		layoutValider.setBackgroundColor(Color.parseColor("#FF0b74af"));
		
		//fond de la page
		BitmapDrawable background = (BitmapDrawable) this.getResources().getDrawable(R.drawable.fond);
		BitmapDrawable backconnexion = (BitmapDrawable) this.getResources().getDrawable(R.drawable.back_connexion);
		background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		
		//fond du bloc connexion
		layoutConnexion.setBackgroundDrawable(backconnexion);
		layoutConnexion.setGravity(Gravity.CENTER);
		layoutConnexion.setPadding(0, 0, 0, 20);
		textLogin.setWidth((int) ((backconnexion.getMinimumWidth())*0.80));
		textPasse.setWidth((int) ((backconnexion.getMinimumWidth())*0.80));
		//configuration de layout principale
		Globale.setGravity(Gravity.CENTER);
		Globale.setBackgroundDrawable(background);
		Globale.setOrientation(LinearLayout.VERTICAL);
		setContentView(Globale);
	 }
		
 }

	/**
	 * menu de l'application
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * action du bouton login en envoyant les données à l'activité "VerifLogin" pour les verifier
	 */
	@Override
	public void onClick(View arg0) {
		
		String login = textLogin.getText().toString();
		String passe="";
		int count=0;
		userDb.open();
		
		//crypter le mot de passe en utilisant MD5
		String plaintext = textPasse.getText().toString();
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(plaintext.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			passe = bigInt.toString(16);	
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (passe.length()<32){
			passe="0"+passe;
		}
		cd = new ConnectionDetector(getApplicationContext());
		userOn = null;
		
		if(cd.isConnectingToInternet()){ // teste connexion internet
			//recuperer les infos d'utilisateur enregistrées dans la base distant
			Syncronisation sync = new Syncronisation();
			
			
			userOn = sync.recuperUser(login);
			
			//recuperer l'id du mobile
			telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			IdDevice = telephonyManager.getDeviceId();
			
			if(userOn.getId()!=0 && userOn.getMail()!=null){ //verifier l'existance de l'utilisateur
				if(userOn.getPasse().equals(passe) && (userOn.getProfile().equals("CU") || userOn.getProfile().equals("CA"))){ //verifier ses infos 
						if(userOn.getDevice().equals(IdDevice)){ // si l'utilisateur a été deja connecté par le meme mobile
							Toast.makeText(this," vous etes connete", Toast.LENGTH_LONG).show();
							count = 2;
						}else if(userOn.getDevice().equals("null")){ // si l'utilisateur n'a jamais connecté par un mobile
							Toast.makeText(this," vous etes connete", Toast.LENGTH_LONG).show();
							count = 2;
							sync.modifDevice(userOn.getId(), IdDevice);
						}else { // si l'utilisateur tente de se connecter par un autre mobile
							Toast.makeText(this," vous etes connete", Toast.LENGTH_LONG).show();
							count = 2;
							sync.modifDevice(userOn.getId(), IdDevice);
						}
				}else // les infos d'utilisateur incorrectes
					Toast.makeText(this, "votre login et/ou passe sont incorrectes ", Toast.LENGTH_LONG).show();
				
			}else   // les infos d'utilisateur n'existent pas dans la base
				Toast.makeText(this, "votre login sont incorrectes ", Toast.LENGTH_LONG).show();
		
		
		if(count == 2){ // si les infos d'utilisateur sont correctes, une phase de synchronisation, est obligatoire
			Utilisateur userOff = userDb.getUser(login);
			if(userOff!=null)  userDb.deleteUser(userOff.getId());
			userDb.insertuser(userOn.getId(), userOn.getNom(), userOn.getPrenom(), userOn.getMail(), userOn.getFonction(), userOn.getProfile(), userOn.getPasse(), userOn.getEtat(),1);
			
			//parametrer la session
			DbInstall connecte = new DbInstall(this);
			connecte.open();
			connecte.insertoptionApp("connecte", String.valueOf(userOn.getId()));	
			
			finish(); // fermer l'activité
//			//lancer la synchronisation des formulaires
			Intent syncForm = new Intent(this, Install.class);	
			syncForm.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(syncForm);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
			
		}
}else  Toast.makeText(this, "Vous ne possedez pas de connexion internet !!", Toast.LENGTH_LONG).show();
		
		
		
	}



}
