package com.applifit.bi_lifit1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.Syncronisation;
import com.applifit.bi_lifit1.formulaire.DbElement;
import com.applifit.bi_lifit1.formulaire.DbFormulaire;
import com.applifit.bi_lifit1.formulaire.DbValeur;
import com.applifit.bi_lifit1.formulaire.Formulaire;

public class ListValeur extends Activity implements OnClickListener {

	ArrayList<Integer> idForm = new ArrayList<Integer>();
	ArrayList<Integer> idice = new ArrayList<Integer>();
	int iduser;
	ArrayList<HashMap<String, String>> ma = new ArrayList<HashMap<String, String>>();
	private ListView maListForms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_valeur);
		setTitle("Formulaires");
		maListForms = (ListView) findViewById(R.id.listviewperso3);
		DbElement localDbElement;
		DbFormulaire localDbFormulaire;
		Cursor localCursor1;

		// detecte l'id de l'utilisateur connecté
		DbInstall connecte = new DbInstall(this);
		connecte.open();
		String option = connecte.getoptionApp("connecte");
		if (option.equals("")) {
			// accueil s il y a pas d utilisateur connecté
			Intent main = new Intent(this, Main.class);
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		} else {
			iduser = Integer.valueOf(option);
			DbValeur localDbValeur = new DbValeur(this);
			localDbValeur.open();
			localDbElement = new DbElement(this);
			localDbElement.open();
			DbFormulaire form = new DbFormulaire(this);
			form.open();

			final ImageView localImageView = new ImageView(this);
			localImageView.setImageDrawable((BitmapDrawable) getResources()
					.getDrawable(R.drawable.retour));
			localImageView.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
			localImageView.setId(-1);
			localImageView.setOnClickListener(this);
			this.maListForms.addFooterView(localImageView);

			localCursor1 = localDbValeur.getAllValeur();
			localCursor1.moveToFirst();
			while (!localCursor1.isAfterLast()) {
				boolean ok = true;
				int indice = localCursor1.getInt(5);
				int idE = localCursor1.getInt(2);
				Cursor localCursor2 = localDbElement.getElementsid(idE);
				localCursor2.moveToFirst();
				int idFo = localCursor2.getInt(4);
				Formulaire fo = form.getFormulaire(idFo);
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
					HashMap<String, String> localHashMap = new HashMap<String, String>();
					localHashMap.put("nom",
							localCursor1.getString(4).replace("%20", " ")
									.replace("%3A", ":"));
					localHashMap.put("commentaire", fo.getNom());
					this.ma.add(0, localHashMap);
					this.idForm.add(0, idFo);
					this.idice.add(0, indice);
				}
				localCursor1.moveToNext();
			}
			// recuperer les formulaires de l'utilisateur

			// Création d'un SimpleAdapter qui se chargera de mettre les items
			// présent dans notre list (listItem) dans la vue affichageitem
			SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
					ma, R.layout.affichageitem, new String[] { "nom",
							"commentaire" }, new int[] { R.id.nom,
							R.id.commentaire });

			// On attribut à notre listView l'adapter que l'on vient de créer
			maListForms.setAdapter(mSchedule);
			maListForms.setOnItemClickListener(new OnItemClickListener() {
				@Override
				@SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> a, View v,
						final int position, long id) {
					// on récupère la HashMap contenant les infos de notre item
					// (titre, description, img)
					HashMap<String, String> map = (HashMap<String, String>) maListForms
							.getItemAtPosition(position);
					modifierForm(idForm.get(position),idice.get(position));
				}

			});
		}
	}

	/**
	 * methode lance l'activité qui affiche le formulaire pour le remplir
	 * 
	 * @param id
	 *            du formulaire
	 */
	private void modifierForm(int idF,int indice) {
		finish();
		Intent verifier = new Intent(this, ValeurNonConnect.class);
		verifier.putExtra("idF", idF);
		verifier.putExtra("indice", indice);
		verifier.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(verifier);
		overridePendingTransition(R.anim.fadin, R.anim.fadout);

	}

	/**
	 * methode qui crée textview qui affiche le titre et le commentaire du
	 * formulaire
	 * 
	 * @param text
	 *            à afficher
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

	public void onClick(View paramView) {
		finish();
		Intent localIntent = new Intent(this, MainActivity.class);
		localIntent.putExtra("current", 3);
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(localIntent);
		overridePendingTransition(R.anim.fadin, R.anim.fadout);
	}
}
