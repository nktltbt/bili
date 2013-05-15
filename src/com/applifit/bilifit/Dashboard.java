package com.applifit.bilifit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applifit.bilifit.DataBase.DbInstall;
import com.applifit.bilifit.DataBase.Syncronisation;

/**
 * l'activité des Dashboard
 * @author Faissal
 *
 */
public class Dashboard extends Activity {
	
	int iduser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_dashboard);
		
		
		
		DbInstall connecte = new DbInstall(this);
		connecte.open();
		String option = connecte.getoptionApp("connecte");
		if(option.equals("")) {
			finish();
			Intent main = new Intent(this, Main.class);	
			main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
		}
		else iduser = Integer.valueOf(option);
		LinearLayout layout = new LinearLayout(this);
		TextView texte = new TextView(this);
		texte.setText("Page des tableaux de bord");
		texte.setPadding(0, 0, 0, 0);
		
		layout.setPadding(10, 20, 0, 0);
		layout.setGravity(Gravity.LEFT);
		layout.addView(texte);
		setContentView(layout);
		
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
