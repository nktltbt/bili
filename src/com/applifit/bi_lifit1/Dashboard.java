package com.applifit.bi_lifit1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
 * l'activit� des Dashboard
 * 
 * @author Faissal
 * 
 */
public class Dashboard extends Activity {

	private ListView maListForms;
	List<Integer> idForm = new ArrayList<Integer>();
	int iduser;
	ConnectionDetector cd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cd = new ConnectionDetector(getApplicationContext());
		setContentView(R.layout.activity_dashboard);
		setTitle("Formulaires");

		maListForms = (ListView) findViewById(R.id.listviewperso1);
		// Cr�ation de la ArrayList qui nous permettra de remplire la listView
		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		// On d�clare la HashMap qui contiendra les informations pour un item
		HashMap<String, String> map;

		// detecte l'id de l'utilisateur connect�
		DbInstall connecte = new DbInstall(this);
		connecte.open();
		String option = connecte.getoptionApp("connecte");
		if (option.equals("")) {
			// accueil s il y a pas d utilisateur connect�
			Intent main = new Intent(this, Main.class);
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		} else {
			if (cd.isConnectingToInternet()) {
				iduser = Integer.valueOf(option);

				DbFormulaire form = new DbFormulaire(this);
				form.open();

				// recuperer les formulaires de l'utilisateur
				Cursor c = form.getAllFormulaire(iduser);

				// parcour des formulaires
				if (c.getCount() > 0) {
					c.moveToFirst();
					while (c.isAfterLast() == false) {

						// Cr�ation d'une HashMap pour ins�rer les informations
						// du premier item de notre listView
						map = new HashMap<String, String>();
						map.put("nom", (c.getString(1) + "  v" + String
								.valueOf(c.getInt(4))));
						map.put("commentaire", c.getString(2));
						listItem.add(map);
						idForm.add(c.getInt(0));
						c.moveToNext();
					}
				}

				// Cr�ation d'un SimpleAdapter qui se chargera de mettre les
				// items pr�sent dans notre list (listItem) dans la vue
				// affichageitem
				SimpleAdapter mSchedule = new SimpleAdapter(
						this.getBaseContext(), listItem,
						R.layout.affichageitem, new String[] { "nom",
								"commentaire" }, new int[] { R.id.nom,
								R.id.commentaire });

				// On attribut � notre listView l'adapter que l'on vient de
				// cr�er
				maListForms.setAdapter(mSchedule);
				maListForms.setOnItemClickListener(new OnItemClickListener() {
					@Override
					@SuppressWarnings("unchecked")
					public void onItemClick(AdapterView<?> a, View v,
							final int position, long id) {
						// on r�cup�re la HashMap contenant les infos de notre
						// item (titre, description, img)
						HashMap<String, String> map = (HashMap<String, String>) maListForms
								.getItemAtPosition(position);
						modifierForm(idForm.get(position));
					}

				});
			} else {
				  finish();
				    Intent localIntent1 = new Intent(this, ListValeur.class);
				    localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				    startActivity(localIntent1);
				    overridePendingTransition(R.anim.fadin, R.anim.fadout);
			}
		}
	}

	/**
	 * methode lance l'activit� qui affiche le formulaire pour le remplir
	 * 
	 * @param id
	 *            du formulaire
	 */
	private void modifierForm(int id) {
		finish();
		Intent verifier = new Intent(this, FormDownload.class);
		verifier.putExtra("id", id);
		verifier.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(verifier);
		overridePendingTransition(R.anim.fadin, R.anim.fadout);

	}

	/**
	 * methode qui cr�e textview qui affiche le titre et le commentaire du
	 * formulaire
	 * 
	 * @param text
	 *            � afficher
	 * @return le textview remplis
	 */
	public TextView textview(String text) {
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
			// teste de l'existance d'internet
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());
			if (cd.isConnectingToInternet()) {
				// liberer le compte utilisateur de leur mobile
				Syncronisation sync = new Syncronisation();
				sync.modifDevice(iduser, null);
				finish(); // fermer l'activit�
				// fermer la session
				DbInstall connecte = new DbInstall(this);
				connecte.open();
				connecte.deleteoptionApp("connecte");
				Intent IntentDeconnecte = new Intent(this, Main.class);
				IntentDeconnecte.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(IntentDeconnecte);
				overridePendingTransition(R.anim.fadin, R.anim.fadout);
			} else
				Toast.makeText(this, " pas de conn�xion internet",
						Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}

// public class Dashboard extends Activity {
//
// int iduser;
//
// @Override
// protected void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// // setContentView(R.layout.activity_dashboard);
//
// DbInstall connecte = new DbInstall(this);
// connecte.open();
// String option = connecte.getoptionApp("connecte");
// if (option.equals("")) {
// finish();
// Intent main = new Intent(this, Main.class);
// main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
// startActivity(main);
// overridePendingTransition(R.anim.fadin, R.anim.fadout);
// } else
// iduser = Integer.valueOf(option);
// LinearLayout layout = new LinearLayout(this);
// TextView texte = new TextView(this);
// texte.setText("Page des tableaux de bord");
// texte.setPadding(0, 0, 0, 0);
//
// layout.setPadding(10, 20, 0, 0);
// layout.setGravity(Gravity.LEFT);
// layout.addView(texte);
// setContentView(layout);
//
// }

// @Override
// public boolean onCreateOptionsMenu(Menu menu) {
// MenuInflater inflater = getMenuInflater();
// inflater.inflate(R.menu.menu, menu);
// return true;
// }
//
// @Override
// public boolean onOptionsItemSelected(MenuItem item) {
// switch (item.getItemId()) {
// case R.id.deconnecte:
// // teste de l'existance d'internet
// ConnectionDetector cd = new ConnectionDetector(
// getApplicationContext());
// if (cd.isConnectingToInternet()) {
// // liberer le compte utilisateur de leur mobile
// Syncronisation sync = new Syncronisation();
// sync.modifDevice(iduser, null);
// finish(); // fermer l'activit�
// // fermer la session
// DbInstall connecte = new DbInstall(this);
// connecte.open();
// connecte.deleteoptionApp("connecte");
// Intent IntentDeconnecte = new Intent(this, Main.class);
// IntentDeconnecte.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
// startActivity(IntentDeconnecte);
// overridePendingTransition(R.anim.fadin, R.anim.fadout);
// } else
// Toast.makeText(this, " pas de conn�xion internet",
// Toast.LENGTH_LONG).show();
// break;
// default:
// break;
// }
// return super.onOptionsItemSelected(item);
// }
// }