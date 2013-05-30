package com.applifit.bi_lifit1;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.applifit.bi_lifit1.R;
import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.Syncronisation;

/**
 * accueil
 * 
 * @author Faissal
 * 
 */
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	private TabHost tabHost;
	private int current = 1;
	int iduser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);
		setTitle("");
		overridePendingTransition(R.anim.fadin, R.anim.fadout);

		// si les données sont enregistrées avec succes par l'activité
		// ACtivityEnregistrer.java
		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			if (extra.getInt("enregistrement") == 2)
				Toast.makeText(this, "Enregistrement ...", Toast.LENGTH_LONG)
						.show();
			if (extra.getInt("current") == 1)
				current = 1;
			else if (extra.getInt("current") == 2)
				current = 2;
			else if (extra.getInt("current") == 3)
				current = 3;
		}
		//
		// //detecter l'utilisateur connecté
		DbInstall connecte = new DbInstall(this);
		connecte.open();
		String option = connecte.getoptionApp("connecte");
		if (option.equals("")) {
			// s'il n ya pas d'utilisateur connecté
			finish();
			Intent main = new Intent(this, Main.class);
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		} else
			// recuperer l'id utilisateur s'il est connecté
			iduser = Integer.valueOf(option);

		//
		//
		// try {
		this.tabHost = getTabHost();
		//
		// //parametrer le menu en bas

		setupTab("Formulaires", "tab1",
				new Intent().setClass(this, ListForm.class));
		setupTab("Historique", "tab2",
				new Intent().setClass(this, Dashboard.class));
		setupTab("Paramatres", "tab3",
				new Intent().setClass(this, Profile.class));
		// } catch (Exception e) {
		// Toast.makeText(this, e.getMessage()+iduser,
		// Toast.LENGTH_LONG).show();
		// // TODO: handle exception
		// }

		//
		// les couleurs par defaut des tab
		tabHost.getTabWidget().getChildAt(1)
				.setBackgroundColor(Color.parseColor("#e9e9e9"));
		tabHost.getTabWidget().getChildAt(2)
				.setBackgroundColor(Color.parseColor("#e9e9e9"));
		tabHost.getTabWidget().getChildAt(0)
				.setBackgroundColor(Color.parseColor("#e9e9e9"));
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
				.setBackgroundColor(Color.parseColor("#51c7dd")); // 2nd tab
																	// selected

		// navigation entre les tabs
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				setTabColor(tabHost);

			}
		});

	}

	// Change The Backgournd Color des Tabs
	public void setTabColor(TabHost tabhost) {

		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++)
			tabhost.getTabWidget().getChildAt(i)
					.setBackgroundColor(Color.parseColor("#e9e9e9")); // unselected
		tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
				.setBackgroundColor(Color.parseColor("#51c7dd")); // 2nd tab
																	// selected
	}

	private void setupTab(String name, String tag, Intent intent) {

		tabHost.addTab(tabHost.newTabSpec(tag)
				.setIndicator(createTabView(tabHost.getContext(), name))
				.setContent(intent));
		tabHost.setCurrentTabByTag("tab" + current);

	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_item,
				null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
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
				Toast.makeText(this, " pas de connexion internet",
						Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}