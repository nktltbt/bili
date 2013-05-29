package com.applifit.bilifit;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FormDownload extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_download);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.form_download, menu);
		return true;
	}

}
