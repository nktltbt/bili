package com.applifit.bi_lifit1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.Syncronisation;
import com.applifit.bi_lifit1.formulaire.DbFormulaire;

/**
 * l'activité pour afficher l'ensemble des formulaire dans une liste
 * @author Faissal
 *
 */
@SuppressLint("NewApi")
public class ListForm extends Activity {

	private ListView maListForms;
	List<Integer> idForm = new ArrayList<Integer>();
	int iduser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_form);
		setTitle("Formulaires");
		
		

		maListForms = (ListView) findViewById(R.id.listviewperso);
		//Création de la ArrayList qui nous permettra de remplire la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        //On déclare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
        
        //detecte l'id de l'utilisateur connecté 
        DbInstall connecte = new DbInstall(this);
		connecte.open();
		String option = connecte.getoptionApp("connecte");
		if(option.equals("")) {
			//accueil s il y a pas d utilisateur connecté
			Intent main = new Intent(this, Main.class);	
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		}
		else {
			iduser = Integer.valueOf(option);
			
			DbFormulaire form = new DbFormulaire(this);
			form.open();
		
		// recuperer les formulaires de l'utilisateur 
        Cursor c = form.getAllFormulaire(iduser); 
        
        //parcour des formulaires
		if(c.getCount()>0){
			c.moveToFirst();
			 while (c.isAfterLast() == false) {
				 
				//Création d'une HashMap pour insérer les informations du premier item de notre listView
			        map = new HashMap<String, String>();
			        map.put("nom", (c.getString(1)+"  v"+String.valueOf(c.getInt(4))));
			        map.put("commentaire", c.getString(2));
			        listItem.add(map);
			        idForm.add(c.getInt(0));
		       	    c.moveToNext();
		        }
		}
		
		 
        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.affichageitem,
               new String[] {"nom", "commentaire"}, new int[] {R.id.nom, R.id.commentaire});
 
        //On attribut à notre listView l'adapter que l'on vient de créer
        maListForms.setAdapter(mSchedule);
        maListForms.setOnItemClickListener(new OnItemClickListener() {
			@Override
        	@SuppressWarnings("unchecked")
         	public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
				//on récupère la HashMap contenant les infos de notre item (titre, description, img)
        		HashMap<String, String> map = (HashMap<String, String>) maListForms.getItemAtPosition(position);
						modifierForm(idForm.get(position));
        	}


         });
	 }
	}
			/**
			 * methode lance l'activité qui affiche le formulaire pour le remplir
			 * @param id du formulaire
			 */
			private void modifierForm(int id) {
				finish();
				Intent verifier = new Intent(this, RemplirForm.class);	
				verifier.putExtra("id", id);
				verifier.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(verifier);
				overridePendingTransition(0,0);
				
			}
		
	/**
	 * methode qui crée textview qui affiche le titre et le commentaire du formulaire
	 * @param text à afficher
	 * @return le textview remplis
	 */
	public TextView textview(String text){
		TextView view = new TextView(this);
		view.setText(text);
		view.setTextSize(10);
		return view;
	}
	
	/**
	 * action button menu
	 */
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
			}else Toast.makeText(this," pas de connéxion internet", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
