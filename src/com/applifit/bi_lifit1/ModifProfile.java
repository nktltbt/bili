package com.applifit.bi_lifit1;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.Syncronisation;
import com.applifit.bi_lifit1.compte.DbUtilisateur;
import com.applifit.bi_lifit1.compte.Utilisateur;

/**
 *  l'activité modifier profile
 * @author Faissal
 *
 */
public class ModifProfile extends Activity implements OnClickListener {
	
	
	
	LinearLayout layoutGlobal;
	LinearLayout layoutNom;
	LinearLayout layoutPrenom;
	LinearLayout layoutFonction;
	LinearLayout layoutPasse;
	LinearLayout layoutbtn;
	Bundle extra;
	
	EditText nom;
	EditText prenom;
	EditText fonction;
	EditText passe;
//	EditText passeConfirm;
	
	TextView nomview;
	TextView prenomview;
	TextView fonctionview;
	TextView passeview;
	TextView passeConfirmview;
	
	ImageView valider;
	ImageView retour;

	/**
	 * au creation de l'activité
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("");
		
		overridePendingTransition(R.anim.fadin, R.anim.fadout);
		
		//les layouts
		layoutGlobal = new LinearLayout(this);
		layoutNom = new LinearLayout(this);
		layoutPrenom = new LinearLayout(this);
		layoutFonction = new LinearLayout(this);
		layoutPasse = new LinearLayout(this);
		layoutbtn = new LinearLayout(this);
		
		//les champs
		nom = new EditText(this);
		prenom = new EditText(this);
		fonction = new EditText(this);
		passe = new EditText(this);
		
		//les labelles 
		nomview = new TextView(this);
		prenomview = new TextView(this);
		fonctionview = new TextView(this);
		passeview = new TextView(this);
		passeConfirmview = new TextView(this);
		
		
		//detecter la largeur de l'ecran affin de parametrer la largeur des champs et des labelles 
		DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    int width = dm.widthPixels;

		//les boutons
		final ImageView valider = new ImageView(this);
		final ImageView retour = new ImageView(this);
		ImageView separateur = new ImageView(this);
		//image du bouton valider formulaire
		BitmapDrawable btnConnexion = (BitmapDrawable) this.getResources().getDrawable(R.drawable.modifier);
		valider.setImageDrawable(btnConnexion);
		valider.setBackgroundColor(Color.parseColor("#FF0b74af"));
		valider.setOnClickListener(this);
		valider.setMinimumWidth((int) (width*0.47));
		valider.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				valider.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
				return false;
			}
		});
		
		//image du bouton retour formulaire
		BitmapDrawable btnRetour = (BitmapDrawable) this.getResources().getDrawable(R.drawable.retour);
		retour.setImageDrawable(btnRetour);
		retour.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
		retour.setOnClickListener(this);
		retour.setMinimumWidth((int) (width*0.47));
		retour.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				retour.setBackgroundColor(Color.parseColor("#FF0F74B3"));
				return false;
			}
		});
		
		//image separateur 
		separateur.setBackgroundColor(Color.WHITE);
		separateur.setMinimumWidth((int) (width*0.02));
		
		
		
		nomview.setText("nom");
		prenomview.setText("prenom");
		fonctionview.setText("fonction");
		passeview.setText("mot de passe*");
		passeConfirmview.setText("*vide pour ne pas modifier le mot de passe");
		
		//recuperer les infos 
		extra = getIntent().getExtras();		
		nom.setText(extra.getString("nom"));
		prenom.setText(extra.getString("prenom"));
		fonction.setText(extra.getString("fonction"));
		
		layoutNom.addView(nomview);        layoutNom.addView(nom);
		layoutPrenom.addView(prenomview);     layoutPrenom.addView(prenom);
		layoutFonction.addView(fonctionview);   layoutFonction.addView(fonction);
		layoutPasse.addView(passeview);    layoutPasse.addView(passe);   layoutPasse.addView(passeConfirmview);
		layoutbtn.addView(retour);   layoutbtn.addView(separateur);           layoutbtn.addView(valider);
		
		layoutNom.setPadding(10, 5, 10, 5);
		layoutPrenom.setPadding(10, 5, 10, 5);
		layoutFonction.setPadding(10, 5, 10, 5);
		layoutPasse.setPadding(10, 5, 10, 30);
		
		//générer les ids des boutons pour definir leurs actions
		valider.setId(1);
		retour.setId(-1);
		valider.setOnClickListener(this);
		retour.setOnClickListener(this);
		
		
		
		nomview.setWidth((int) (width*0.95));
		prenomview.setWidth((int) (width*0.95));
		fonctionview.setWidth((int) (width*0.95));
		passeview.setWidth((int) (width*0.95));
		passeConfirmview.setWidth((int) (width*0.95));
		
		nom.setWidth((int) (width*0.95));
		prenom.setWidth((int) (width*0.95));
		fonction.setWidth((int) (width*0.95));
		passe.setWidth((int) (width*0.95));
		
		nomview.setTextSize(13);		  nom.setTextSize(13);
		prenomview.setTextSize(13);	 	  prenom.setTextSize(13);
		fonctionview.setTextSize(13);	  fonction.setTextSize(13);
		passeview.setTextSize(13);		  passe.setTextSize(13);
		passeConfirmview.setTextSize(13); 
		
		//les champs doivent etre uniligne
		nom.setSingleLine(true);
		prenom.setSingleLine(true);
		fonction.setSingleLine(true);
		passe.setSingleLine(true);
		
		//parametrer l'orientation des layouts
		layoutNom.setOrientation(LinearLayout.VERTICAL);
		layoutPrenom.setOrientation(LinearLayout.VERTICAL);
		layoutFonction.setOrientation(LinearLayout.VERTICAL);
		layoutPasse.setOrientation(LinearLayout.VERTICAL);
		layoutbtn.setOrientation(LinearLayout.HORIZONTAL);
		
		layoutGlobal.setOrientation(LinearLayout.VERTICAL);
		layoutGlobal.setPadding(10, 10, 10, 0);

		layoutGlobal.addView(layoutNom);
		layoutGlobal.addView(layoutPrenom);
		layoutGlobal.addView(layoutFonction);
		layoutGlobal.addView(layoutPasse);
		layoutGlobal.addView(layoutbtn);
		
		ScrollView scrol = new ScrollView(this);
		scrol.addView(layoutGlobal);
		scrol.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				valider.setBackgroundColor(Color.parseColor("#FF0F74B3"));
				retour.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
				return false;
			}
		});
		setContentView(scrol);
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
//		case R.id.form:
//			Intent form = new Intent(this, ListForm.class);
//			startActivity(form);
//			break;
//		case R.id.profile:
//			Intent profileInt = new Intent(this, Profile.class);
//			startActivity(profileInt);
//			break;
//		case R.id.dash:
//			Intent Intent = new Intent(this, Dashboard.class);	
//			startActivity(Intent);
//			break;
		case R.id.deconnecte:
			//teste de l'existance d'internet
			ConnectionDetector cd= new ConnectionDetector(getApplicationContext());
			if(cd.isConnectingToInternet()){
				//liberer le compte utilisateur de leur mobile 
				Syncronisation sync = new Syncronisation();
				sync.modifDevice(extra.getInt("id"), null);
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
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * gestion des evenements de clique
	 */
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case -1: // bouton retour
			finish();
			Intent retour = new Intent(this, MainActivity.class);
			retour.putExtra("current", 3);
			retour.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(retour);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
			break;
		case 1: //bouton valider
			
			//recuperer les données saisis 
			int _id = extra.getInt("id");
			String _nom = nom.getText().toString();
			String _prenom = prenom.getText().toString();
			String _fonction = fonction.getText().toString();
			
			DbUtilisateur user = new DbUtilisateur(this);
			user.open();
			
			
			String Mdp = null;
			if(!passe.getText().toString().equals("")){ //si le champ mot de passe n'est pas vide
				String plaintext = passe.getText().toString();
				// cryptage du mot de passe
				MessageDigest m;
				try {
					m = MessageDigest.getInstance("MD5");
					m.reset();
					m.update(plaintext.getBytes());
					byte[] digest = m.digest();
					BigInteger bigInt = new BigInteger(1,digest);
					Mdp = bigInt.toString(16);	
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else { // si le champ mot de passe est vide, gardons le mot de passe actuel
				Utilisateur u= user.getUser(_id);
				Mdp =u.getPasse();
			}
		//modification du profile hors ligne	
		user.setUser(_id, _nom, _prenom, _fonction, Mdp, 1,1);
				
		// modification du profile en ligne
				ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
			    if(cd.isConnectingToInternet()){ 
					StrictMode.ThreadPolicy policy = new StrictMode.
					ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy); 
					//generer l'url pour modifier
					
					String rq = Config.URL+"compte/modifier" +
						"/id/" +_id+
						"/nom/" +_nom.replace(" ", "%20")+
						"/prenom/" +_prenom.replace(" ", "%20")+
						"/fonction/" +_fonction.replace(" ", "%20")+
						"/pass/"+Mdp;
					
					HttpClient httpclient = new DefaultHttpClient();
				    try {
				    	//envoyer la requete
						HttpResponse response = httpclient.execute(new HttpGet(rq));
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}

				Intent profile = new Intent(this, MainActivity.class);
				profile.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    			startActivity(profile);
    			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		break;
		}
	}
	
	/**
	 * action bouton retour du mobile
	 */
	@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) 
        {   // do something on back.
            Intent lIntentObj = new Intent(this, MainActivity.class);
            lIntentObj.putExtra("current", 3);
            lIntentObj.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(lIntentObj);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
            finish();
            return true;    
           }  
        return super.dispatchKeyEvent(event);
    }

}
