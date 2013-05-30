package com.applifit.bi_lifit1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.Install;
import com.applifit.bi_lifit1.DataBase.MyService;
import com.applifit.bi_lifit1.DataBase.ServiceCollecte;
import com.applifit.bi_lifit1.DataBase.Syncronisation;
import com.applifit.bi_lifit1.compte.DbUtilisateur;
import com.applifit.bi_lifit1.compte.Utilisateur;

/**
 * l'activité pour afficher le profile et le bouton de synchronisation
 * @author Faissal
 *
 */
public class Profile extends Activity implements OnClickListener {
	
	//les layouts
	LinearLayout layoutGlobal;
	LinearLayout layouttitreSync;
	LinearLayout layoutSync;
	LinearLayout layouttitreProfile;
	LinearLayout layoutNom;
	LinearLayout layoutPrenom;
	LinearLayout layoutMail;
	LinearLayout layoutFonction;
	LinearLayout layoutProfile;
	LinearLayout layoutbtn;
	
	//les labelles
	TextView nomview;
	TextView prenomview;
	TextView mailview;
	TextView fonctionview;
	TextView profileview;
	TextView passeview;
	TextView passeConfirmview;
	
	//les labelles qui vont afficher les infos du profiles
	TextView nom;
	TextView prenom;
	TextView mail;
	TextView fonction;
	TextView profile;
	ImageView modifier;
	ImageView synchronisation;
	
	Utilisateur user;
	int iduser;
	 
	/**
	 * au creration de l'activité
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//detecter l'utilisateur connecté
		DbInstall connecte = new DbInstall(this);
		connecte.open();
		String option = connecte.getoptionApp("connecte");
		if(option.equals("")) {
			// s'il n ya pas d'utilisateur connecté 
			Intent main = new Intent(this, Main.class);	
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		}
		else { 
		//recuperer l'id utilisateur s'il est connecté
		iduser = Integer.valueOf(option);

		//titre du bouton synchronisation
		TextView titreSync = new TextView(this);
		titreSync.setText("Synchronisation");
		String mes="";
		try {
			mes=getIntent().getExtras().getString("message");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (!mes.equals("")){
			titreSync.setText(mes);
		}
		titreSync.setTextColor(Color.parseColor("#FF0F74B3"));
		titreSync.setPadding(0, 5, 20, 10);
		
		 DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    int width = dm.widthPixels;
		
		//bouton synchroniosation
		synchronisation = new ImageView(this);
		synchronisation.setOnClickListener(this);
//		synchronisation.setWidth((int) (width));
//		synchronisation.setText("Synchroniser manuellement");
//		synchronisation.setHeight(10);
		synchronisation.setId(2);
//		synchronisation.setTextSize(14);
		//image du bouton retour formulaire
		BitmapDrawable btnSynchrinisation = (BitmapDrawable) this.getResources().getDrawable(R.drawable.btnsync);
		synchronisation.setImageDrawable(btnSynchrinisation);
		synchronisation.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
		synchronisation.setMinimumWidth((int) (width));
		synchronisation.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				synchronisation.setBackgroundColor(Color.parseColor("#FF0F74B3"));
				return false;
			}
		});
		

		//titre du bloc affichant le profile
		TextView titreProfil = new TextView(this);
		titreProfil.setText("Profile");
		titreProfil.setTextColor(Color.parseColor("#FF0F74B3"));
		titreProfil.setPadding(0, 5, 20, 10);
		
		//instanciation des layouts
		layoutGlobal = new LinearLayout(this);
		layouttitreSync = new LinearLayout(this);
		layoutSync = new LinearLayout(this);
		layouttitreProfile = new LinearLayout(this);
		layoutNom = new LinearLayout(this);
		layoutPrenom = new LinearLayout(this);
		layoutMail = new LinearLayout(this);
		layoutFonction = new LinearLayout(this);
		layoutProfile = new LinearLayout(this);
		layoutbtn = new LinearLayout(this);
		
		//instanciation des labelles
		nom = new TextView(this);
		prenom = new TextView(this);
		mail = new TextView(this);
		fonction = new TextView(this);
		profile = new TextView(this);
		
		//instanciation des labelles qui vont afficher les infos du profiles
		nomview = new TextView(this);
		prenomview = new TextView(this);
		mailview = new TextView(this);
		fonctionview = new TextView(this);
		profileview = new TextView(this);
		passeview = new TextView(this);
		passeConfirmview = new TextView(this);
		
		//bouton modifier
		modifier = new ImageView(this);
//		modifier.setText("Modifier");
//		modifier.setTextSize(14);
//		modifier.setWidth((int) (width));
//		modifier.setHeight(10);
		BitmapDrawable btnModifier = (BitmapDrawable) this.getResources().getDrawable(R.drawable.btnmodifier);
		modifier.setImageDrawable(btnModifier);
		modifier.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
		modifier.setMinimumWidth((int) (width));
		modifier.setHovered(true);
		modifier.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
					modifier.setBackgroundColor(Color.parseColor("#FF0F74B3"));
				return false;
			}
		});
		
		
		
		//textes des labelles
		nomview.setText("nom");
		prenomview.setText("prenom");
		mailview.setText("mail");
		fonctionview.setText("fonction");
		profileview.setText("profile");
		
		//ajouter les vues aux layouts
		layouttitreSync.addView(titreSync);
		layoutSync.addView(synchronisation);
		layouttitreProfile.addView(titreProfil);
		layoutNom.addView(nomview);        layoutNom.addView(nom); 
		layoutPrenom.addView(prenomview);     layoutPrenom.addView(prenom);
		layoutMail.addView(mailview);       layoutMail.addView(mail);
		layoutFonction.addView(fonctionview);   layoutFonction.addView(fonction);
		layoutProfile.addView(profileview);    layoutProfile.addView(profile);
		layoutbtn.addView(modifier);  
		
		titreProfil.setWidth((int) (width));
		titreSync.setWidth((int) (width));
		
		//padding de chaque layout
		layoutNom.setPadding(0, 10, 0, 5);
		layoutPrenom.setPadding(0, 10, 0, 5);
		layoutMail.setPadding(0, 10, 0, 5);
		layoutFonction.setPadding(0, 10, 0, 5);
		layoutProfile.setPadding(0, 10, 0, 5);
		layoutbtn.setPadding(0, 10, 0, 5);
		layoutSync.setPadding(0, 10, 0, 10);
		layouttitreSync.setPadding(0, 10, 0, 0);
		layouttitreProfile.setPadding(0, 10, 0, 0);
		
		//arriere plan titre bloc synchronisation
		BitmapDrawable backTitre = (BitmapDrawable) this.getResources().getDrawable(R.drawable.separateur);
		backTitre.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		titreSync.setBackgroundDrawable(backTitre);
		
		//arriere plan titre bloc profile
		backTitre.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		titreProfil.setBackgroundDrawable(backTitre);
		
		//id bouton modifier pour parametrer son action
		modifier.setId(1);
		modifier.setOnClickListener(this);
		
		//largeur des labelles
		nomview.setWidth(130);
		prenomview.setWidth(130);
		mailview.setWidth(130);
		fonctionview.setWidth(130);
		profileview.setWidth(130);
		passeview.setWidth(130);
		passeConfirmview.setWidth(130);
		
		nomview.setTextSize(13);		  nom.setTextSize(13);
		prenomview.setTextSize(13);	 	  prenom.setTextSize(13);
		mailview.setTextSize(13);		  mail.setTextSize(13);
		fonctionview.setTextSize(13);	  fonction.setTextSize(13);
		profileview.setTextSize(13);	  profile.setTextSize(13);
		
		//orientation du layout globale 
		layoutGlobal.setOrientation(LinearLayout.VERTICAL);
		layoutGlobal.setPadding(10, 10, 10, 0);
		
		//ajouter les sous layouts au layout globale 
		layoutGlobal.addView(layouttitreSync);
		layoutGlobal.addView(layoutSync);
		layoutGlobal.addView(layouttitreProfile);
		layoutGlobal.addView(layoutNom);
		layoutGlobal.addView(layoutPrenom);
		layoutGlobal.addView(layoutMail);
		layoutGlobal.addView(layoutFonction);
		layoutGlobal.addView(layoutProfile);
		layoutGlobal.addView(layoutbtn);
		
		ScrollView scrol = new ScrollView(this);
        scrol.addView(layoutGlobal);
        scrol.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
					modifier.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
					synchronisation.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
				return true;
			}
		});
		setContentView(scrol);
		
		//recuperer les infos du profile de la base locale
		DbUtilisateur userDb = new DbUtilisateur(this);
		userDb.open();
		user = userDb.getUser(iduser);		
		//remplir les labelles par les infos du profile
		nom.setText(user.getNom());
		prenom.setText(user.getPrenom());
		mail.setText(user.getMail());
		fonction.setText(user.getFonction());
		profile.setText(user.getProfile());
	}	
		
	}

	/**
	 * action bouton menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  	MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.menu, menu);
		    return true;
	}
	
	/**
	 * action des options du menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.deconnecte:
			//teste de l'existance d'internet
			ConnectionDetector cd= new ConnectionDetector(getApplicationContext());
			if(cd.isConnectingToInternet()){
				//liberer le compte utilisateur de leur mobile 
				Syncronisation sync = new Syncronisation();
				sync.modifDevice(iduser, null);
				finish(); // fermer l'activité
				//fermer la session
				DbInstall connecte = new DbInstall(this);
				connecte.open();
				connecte.deleteoptionApp("connecte");
				Intent IntentDeconnecte = new Intent(this, Main.class);	
				IntentDeconnecte.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(IntentDeconnecte);
				overridePendingTransition(R.anim.fadin, R.anim.fadout);
			}else Toast.makeText(this," pas de connexion internet", Toast.LENGTH_LONG).show();
			break;
//		case R.id.exit:
//			finish();
//            System.exit(0);
//			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * gestion des evenement de clique
	 */
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case 2: // action bouton synchronisation
				finish();
				Intent bindIntent = new Intent(this, MyService.class);
				startService(bindIntent);
				
				// lancer le service de synchronisation en arriere plan après une minute
				Intent bindInt = new Intent(this, ServiceCollecte.class);
				startService(bindInt);
				
				Intent sync = new Intent(this, Install.class);	
				sync.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				sync.putExtra("to", 3);
				startActivity(sync);
				overridePendingTransition(R.anim.fadin, R.anim.fadout);
				break;
			case 1: //action bouton modifier
				finish();
				Intent modifIntent = new Intent(this, ModifProfile.class);
				//envoyer les données
				modifIntent.putExtra("login", user.getMail());
				modifIntent.putExtra("passe", user.getPrenom());
				modifIntent.putExtra("nom", user.getNom());
				modifIntent.putExtra("prenom", user.getPrenom());
				modifIntent.putExtra("fonction", user.getFonction());
				modifIntent.putExtra("profile", user.getProfile());
				modifIntent.putExtra("id", user.getId());
				//lancer l'activité
				modifIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(modifIntent);
				overridePendingTransition(R.anim.fadin, R.anim.fadout);
			break;
		}
		
	}

}
