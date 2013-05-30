package com.applifit.bi_lifit1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.Toast;

import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.Syncronisation;
import com.applifit.bi_lifit1.formulaire.DbFormulaireUnit;

public class FormWeb extends Activity implements OnClickListener {
	List<Integer> idForm = new ArrayList();
	int iduser;
	private ListView maListForms;
	int width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (new ConnectionDetector(getApplicationContext())
				.isConnectingToInternet()) {
			ArrayList localArrayList;
			Cursor localCursor;
			setContentView(R.layout.activity_form_web);
			setTitle("Formulaires");
			this.maListForms = ((ListView) findViewById(R.id.listviewperso2));
			localArrayList = new ArrayList();
			DbInstall localDbInstall = new DbInstall(this);
			localDbInstall.open();
			String str = localDbInstall.getoptionApp("connecte");
			if (str.equals("")) {
				finish();
				Intent localIntent2 = new Intent(this, Main.class);
				localIntent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(localIntent2);
				overridePendingTransition(R.anim.fadin, R.anim.fadout);
				return;
			}
			this.iduser = Integer.valueOf(str).intValue();
			DbFormulaireUnit localDbFormulaireUnit = new DbFormulaireUnit(this);
			localDbFormulaireUnit.open();
			localCursor = localDbFormulaireUnit.getAllFormulaire();
			final ImageView localImageView = new ImageView(this);
			localImageView.setImageDrawable((BitmapDrawable) getResources()
					.getDrawable(R.drawable.retour));
			localImageView.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
			localImageView.setId(-1);
			localImageView.setOnClickListener(this);
			this.maListForms.addFooterView(localImageView);
			if (localCursor.getCount() > 0) {
				localCursor.moveToFirst();
				while (!localCursor.isAfterLast()) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("nom", (localCursor.getString(5)));
					map.put("commentaire", localCursor.getString(3)+" "
							+ localCursor.getString(4));
					localArrayList.add(0,map);
					idForm.add(0,localCursor.getInt(2));
					localCursor.moveToNext();
				}
				SimpleAdapter mSchedule = new SimpleAdapter(
						this.getBaseContext(), localArrayList,
						R.layout.affichageitem, new String[] { "nom",
								"commentaire" }, new int[] { R.id.nom,
								R.id.commentaire });

				// On attribut à notre listView l'adapter que l'on vient de
				// créer
				maListForms.setAdapter(mSchedule);
				maListForms.setOnItemClickListener(new OnItemClickListener() {
					@Override
					@SuppressWarnings("unchecked")
					public void onItemClick(AdapterView<?> a, View v,
							final int position, long id) {
						// on récupère la HashMap contenant les infos de notre
						// item (titre, description, img)
						HashMap<String, String> map = (HashMap<String, String>) maListForms
								.getItemAtPosition(position);
						consulForm(idForm.get(position));
					}

				});

			}
		} else {
			// TODO
		}
	}

	private void consulForm(int paramInt) {
		 finish();
		 Bundle localBundle = getIntent().getExtras();
		 Intent localIntent = new Intent(this, ValeurDowload.class);
		 localIntent.putExtra("indice", paramInt);
		 localIntent.putExtra("idF", localBundle.getInt("idF"));
		 localIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		 startActivity(localIntent);
		 overridePendingTransition(R.anim.fadin, R.anim.fadout);
	}

	public void onClick(View paramView) {
		finish();
		Intent localIntent = new Intent(this, MainActivity.class);
		localIntent.putExtra("current", 2);
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(localIntent);
		overridePendingTransition(R.anim.fadin, R.anim.fadout);
	}

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

}
