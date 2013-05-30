package com.applifit.bi_lifit1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.applifit.bi_lifit1.DataBase.DbInstall;
import com.applifit.bi_lifit1.DataBase.Syncronisation;
import com.applifit.bi_lifit1.formulaire.DbElement;
import com.applifit.bi_lifit1.formulaire.DbFormulaire;
import com.applifit.bi_lifit1.formulaire.DbParametre;
import com.applifit.bi_lifit1.formulaire.DbValeurUnit;
import com.applifit.bi_lifit1.formulaire.Formulaire;

public class ConsulForm extends Activity implements OnClickListener {

	TableLayout layout; // pour afficher les champs
	List<String> valeurs = new ArrayList<String>(); // pour recuperer les ids
													// des champs et leur type
	List<Integer> Dates = new ArrayList<Integer>();
	List<Integer> Heures = new ArrayList<Integer>();
	TextView titre;
	int iduser;
	int width; // largeur de l'ecran
	int widthChamp; // largeur des champs
	String valeurre = "";
	HashMap<Integer, Integer> j = new HashMap();
	int idF;
int indice;
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadin, R.anim.fadout);

		// recuperer l'id du formulaire à afficher
		Bundle extra = getIntent().getExtras();
		idF = extra.getInt("idF");
		indice = extra.getInt("indice");
		// detecter l'utilisateur connecté
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

		// recuperer les parametres du formulaire du locale
		DbFormulaire form = new DbFormulaire(this);
		form.open();
		Formulaire f = form.getFormulaire(idF);

		// parametrer le titre du formulaire
		titre = new TextView(this);
		titre.setText(f.getNom() + " v" + f.getVersion());
		titre.setTextColor(Color.parseColor("#ffffffff"));
		titre.setLayoutParams(new android.view.ViewGroup.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		// arriere plan du titre
		BitmapDrawable backTitre = (BitmapDrawable) this.getResources()
				.getDrawable(R.drawable.titre);
		backTitre.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		titre.setBackgroundDrawable(backTitre);

		titre.setPadding(10, 8, 20, 8);

		layout = new TableLayout(this);
		layout.addView(titre);

		// pour recuperer les champs du formulaires et leurs parametres
		DbElement elementdb = new DbElement(this);
		elementdb.open();
		DbParametre parametredb = new DbParametre(this);
		parametredb.open();
		DbValeurUnit localDbValeurUnit = new DbValeurUnit(this);
		localDbValeurUnit.open();

		// - - - - - - - - Get resolution ecran - - - - - - - - - -
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		double screenInches = Math.sqrt(x + y);

		int NbrLayout = 0; // definir le nombre de colonnes que l'ecran peut
							// afficher (0 càd 1 colonne, 1 càd 2 colonnes ...)
		width = dm.widthPixels; // largeur ecran

		Configuration config = getResources().getConfiguration();
		if (((config.orientation == Configuration.ORIENTATION_PORTRAIT) && screenInches <= 6)
				|| width <= 400) {
			// s'il sagit d'un smartphone, dans ce cas on force le mobile
			// d'afficher le formulaire dans une colonne
			widthChamp = width;
		} else { // si la largeur de l'ecran plus grande, le nombre de colonnes
					// sera auomatique
			NbrLayout = (width / 400) - 1;
			widthChamp = width / (NbrLayout + 1);

		}

		layout.setMinimumWidth(width);

		int h = 0; // correspond à chaque ligne, chaque valeur de h donne une
					// ligne d'affichage du formulaires
		int c = 0; // le nombre de colonne du champ dans la base

		TableRow[] row = new TableRow[Config.NBR_ROW]; // chaque row[..]
														// correspond à une
														// ligne du formulaire,
														// max lignes = 50
		// h=0, c'est la 1ere ligne du formulaire
		row[0] = new TableRow(this);

		Cursor curelement = elementdb.getElements(idF); // recuperer tout les
														// champs du formulaire
		if (curelement.getCount() > 0) { // s'il y a pas de champ
			curelement.moveToFirst();
			String type; // type du champ (texte, checkbox, date, time,...)
			int idElement; // idetifiant du champ
			int pos_x; // sa position dans le grid (pos_x --> colonne)
			while (curelement.isAfterLast() == false) {
				type = curelement.getString(1);
				idElement = curelement.getInt(0);
				pos_x = curelement.getInt(2);
				Cursor localCursor2 = localDbValeurUnit.getValeurs(idElement);
				localCursor2.moveToFirst();
				localCursor2.moveToLast();
				valeurre = "";
				if (localCursor2.getCount() > 0)
					valeurre = localCursor2.getString(1);
				if (type.equals("text")) { // s'il sagit d'un texte

					String titre = null;
					String taille_police = null;
					String couleur_police = null;
					String sousligne = null;
					String gras = null;
					String italique = null;
					String dimension = null;
					String persistent = null;

					// recuperer les parametre du champ
					Cursor curParam = parametredb.getParametres(idElement);
					if (curParam.getCount() > 0) {
						curParam.moveToFirst();
						while (curParam.isAfterLast() == false) {

							if (curParam.getString(1).equals("taille_police"))
								taille_police = curParam.getString(2);
							else if (curParam.getString(1).equals("couleur"))
								couleur_police = curParam.getString(2);
							else if (curParam.getString(1).equals("titre"))
								titre = curParam.getString(2);
							else if (curParam.getString(1).equals("sousligne"))
								sousligne = curParam.getString(2);
							else if (curParam.getString(1).equals("gras"))
								gras = curParam.getString(2);
							else if (curParam.getString(1).equals("italique"))
								italique = curParam.getString(2);
							else if (curParam.getString(1).equals("dimension"))
								dimension = curParam.getString(2);
							else if (curParam.getString(1).equals("persistent"))
								persistent = curParam.getString(2);

							curParam.moveToNext();
						}
					}

					if (c == pos_x && c <= NbrLayout) {
						// si l'ecran peut afficher le champ dans leur position
						row[h].addView(addEditText(idElement, type, NbrLayout,
								titre, taille_police, couleur_police,
								sousligne, gras, italique, dimension,
								persistent));
						c++;
					} else {
						// si l'ecran ne peut pas afficher le champ dans leur
						// position
						layout.addView(row[h]); // ajouter la ligne actuel
						// preparation d'une nouvelle ligne
						c = 0;
						h++;

						row[h] = new TableRow(this);
						row[h].addView(addEditText(idElement, type, NbrLayout,
								titre, taille_police, couleur_police,
								sousligne, gras, italique, dimension,
								persistent));
						c++;
					}
				} else if (type.equals("checkbox")) { // s'il sagit d'un
														// checkbox
					String titre = null;
					String libelles = null;
					String orientation = null;

					// recuperer les parametre du champ
					Cursor curParam = parametredb.getParametres(idElement);
					if (curParam.getCount() > 0) {
						curParam.moveToFirst();
						while (curParam.isAfterLast() == false) {

							if (curParam.getString(1).equals("titre"))
								titre = curParam.getString(2);
							else if (curParam.getString(1).equals("libelles"))
								libelles = curParam.getString(2);
							else if (curParam.getString(1)
									.equals("orientation"))
								orientation = curParam.getString(2);

							curParam.moveToNext();
						}
					}

					if (c == pos_x && c <= NbrLayout) {
						// si l'ecran peut afficher le champ dans leur position
						row[h].addView(addCheckBox(idElement, type, NbrLayout,
								titre, libelles, orientation));
						c++;
					} else {
						// si l'ecran ne peut pas afficher le champ dans leur
						// position
						layout.addView(row[h]); // ajouter la ligne actuel
						// preparation d'une nouvelle ligne
						c = 0;
						h++;

						row[h] = new TableRow(this);
						row[h].addView(addCheckBox(idElement, type, NbrLayout,
								titre, libelles, orientation));
						c++;
					}

				} else if (type.equals("radio")) { // s'il sagit d'un radio
					String titre = null;
					String libelles = null;
					String orientation = null;

					// recuperer les parametre du champ
					Cursor curParam = parametredb.getParametres(idElement);
					if (curParam.getCount() > 0) {
						curParam.moveToFirst();
						while (curParam.isAfterLast() == false) {

							if (curParam.getString(1).equals("titre"))
								titre = curParam.getString(2);
							else if (curParam.getString(1).equals("libelles"))
								libelles = curParam.getString(2);
							else if (curParam.getString(1)
									.equals("orientation"))
								orientation = curParam.getString(2);

							curParam.moveToNext();
						}
					}

					if (c == pos_x && c <= NbrLayout) {
						// si l'ecran peut afficher le champ dans leur position
						row[h].addView(addRadio(idElement, type, NbrLayout,
								titre, libelles, orientation));
						c++;
					} else {
						// si l'ecran ne peut pas afficher le champ dans leur
						// position
						layout.addView(row[h]);// ajouter la ligne actuel
						// preparation d'une nouvelle ligne
						c = 0;
						h++;

						row[h] = new TableRow(this);
						row[h].addView(addRadio(idElement, type, NbrLayout,
								titre, libelles, orientation));
						c++;
					}

				} else if (type.equals("combobox")) { // s'il sagit d'un
														// combobox
					String titre = null;
					String libelles = null;
					String orientation = null;
					// recuperer les parametre du champ
					Cursor curParam = parametredb.getParametres(idElement);
					if (curParam.getCount() > 0) {
						curParam.moveToFirst();
						while (curParam.isAfterLast() == false) {

							if (curParam.getString(1).equals("titre"))
								titre = curParam.getString(2);
							else if (curParam.getString(1).equals("libelles"))
								libelles = curParam.getString(2);
							else if (curParam.getString(1)
									.equals("orientation"))
								orientation = curParam.getString(2);

							curParam.moveToNext();
						}
					}

					if (c == pos_x && c <= NbrLayout) {
						// si l'ecran peut afficher le champ dans leur position
						row[h].addView(addComboBox(idElement, type, NbrLayout,
								titre, libelles, orientation));
						c++;
					} else {
						// si l'ecran ne peut pas afficher le champ dans leur
						// position
						layout.addView(row[h]);// ajouter la ligne actuel
						// preparation d'une nouvelle ligne
						c = 0;
						h++;

						row[h] = new TableRow(this);
						row[h].addView(addComboBox(idElement, type, NbrLayout,
								titre, libelles, orientation));
						c++;
					}
				} else if (type.equals("date")) { // s'il sagit d'une date
					String titre = null;

					// recuperer les parametre du champ
					Cursor curParam = parametredb.getParametres(idElement);
					if (curParam.getCount() > 0) {
						curParam.moveToFirst();
						while (curParam.isAfterLast() == false) {
							if (curParam.getString(1).equals("titre"))
								titre = curParam.getString(2);
							curParam.moveToNext();
						}
					}
					if (c == pos_x && c <= NbrLayout) {
						// si l'ecran peut afficher le champ dans leur position
						row[h].addView(addDate(idElement, type, NbrLayout,
								titre));
						c++;
					} else {
						// si l'ecran ne peut pas afficher le champ dans leur
						// position
						layout.addView(row[h]);// ajouter la ligne actuel
						// preparation d'une nouvelle ligne
						c = 0;
						h++;
						row[h] = new TableRow(this);
						row[h].addView(addDate(idElement, type, NbrLayout,
								titre));
						c++;
					}
				} else if (type.equals("time")) { // s'il sagit d'un time
					String titre = null;
					// recuperer les parametre du champ
					Cursor curParam = parametredb.getParametres(idElement);
					if (curParam.getCount() > 0) {
						curParam.moveToFirst();
						while (curParam.isAfterLast() == false) {
							if (curParam.getString(1).equals("titre"))
								titre = curParam.getString(2);
							curParam.moveToNext();
						}
					}
					if (c == pos_x && c <= NbrLayout) {
						// si l'ecran peut afficher le champ dans leur position
						row[h].addView(addHeure(idElement, type, NbrLayout,
								titre));
						c++;
					} else {
						// si l'ecran ne peut pas afficher le champ dans leur
						// position
						layout.addView(row[h]);// ajouter la ligne actuel
						// preparation d'une nouvelle ligne
						c = 0;
						h++;

						row[h] = new TableRow(this);
						row[h].addView(addHeure(idElement, type, NbrLayout,
								titre));
						c++;
					}

				} else if (type.equals("chiffre")) { // s'il sagit d'un chiffre
					String titre = null;
					String taille_police = null;
					String couleur_police = null;
					String sousligne = null;
					String gras = null;
					String italique = null;
					String dimension = null;
					String persistent = null;

					// recuperer les parametre du champ
					Cursor curParam = parametredb.getParametres(idElement);
					if (curParam.getCount() > 0) {
						curParam.moveToFirst();
						while (curParam.isAfterLast() == false) {

							if (curParam.getString(1).equals("taille_police"))
								taille_police = curParam.getString(2);
							else if (curParam.getString(1).equals("couleur"))
								couleur_police = curParam.getString(2);
							else if (curParam.getString(1).equals("titre"))
								titre = curParam.getString(2);
							else if (curParam.getString(1).equals("sousligne"))
								sousligne = curParam.getString(2);
							else if (curParam.getString(1).equals("gras"))
								gras = curParam.getString(2);
							else if (curParam.getString(1).equals("italique"))
								italique = curParam.getString(2);
							else if (curParam.getString(1).equals("dimension"))
								dimension = curParam.getString(2);
							else if (curParam.getString(1).equals("persistent"))
								persistent = curParam.getString(2);

							curParam.moveToNext();
						}
					}

					if (c == pos_x && c <= NbrLayout) {
						// si l'ecran peut afficher le champ dans leur position
						row[h].addView(addEditNumber(idElement, type,
								NbrLayout, titre, taille_police,
								couleur_police, sousligne, gras, italique,
								dimension, persistent));
						c++;
					} else {
						// si l'ecran ne peut pas afficher le champ dans leur
						// position
						layout.addView(row[h]); // ajouter la ligne actuel
						// preparation d'une nouvelle ligne
						c = 0;
						h++;

						row[h] = new TableRow(this);
						row[h].addView(addEditNumber(idElement, type,
								NbrLayout, titre, taille_police,
								couleur_police, sousligne, gras, italique,
								dimension, persistent));
						c++;
					}

				}
				int i1 = curelement.getInt(0);
//				String err = idElement + "ff" + i1;
//				try {
//					if (type.equals("combobox")) {
//						int se = j.get(i1);
//						err = err + se + "yy";
//						Spinner spin = (Spinner) findViewById(i1);
//						err = err + i1 + "yy";
////						spin.setSelection(se);
//					}
//				} catch (Exception e) {
//					finish();
//					Intent localIntent3 = new Intent(this, Profile.class);
//					localIntent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//					localIntent3.putExtra("message", e.getMessage() + "xxxx"
//							+ err);
//
//					startActivity(localIntent3);
//					overridePendingTransition(R.anim.fadin, R.anim.fadout);
//				}

				curelement.moveToNext();
			}
		}

		// }
		// }
		layout.addView(row[h]);

		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.LEFT);

		final ImageView valider = new ImageView(this);
		final ImageView retour = new ImageView(this);
		ImageView separateur = new ImageView(this);
		// image du bouton valider formulaire
		BitmapDrawable btnConnexion = (BitmapDrawable) this.getResources()
				.getDrawable(R.drawable.modifier);
		valider.setImageDrawable(btnConnexion);
		valider.setBackgroundColor(Color.parseColor("#FF0b74af"));
		valider.setId(1);
		valider.setOnClickListener(this);
		valider.setMinimumWidth((int) (width * 0.47));
		valider.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				valider.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
				return false;
			}
		});

		// image du bouton retour formulaire
		BitmapDrawable btnRetour = (BitmapDrawable) this.getResources()
				.getDrawable(R.drawable.retour);
		retour.setImageDrawable(btnRetour);
		retour.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
		retour.setId(-1);
		retour.setOnClickListener(this);
		retour.setMinimumWidth((int) (width * 0.47));
		retour.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				retour.setBackgroundColor(Color.parseColor("#FF0F74B3"));
				return false;
			}
		});

		// image separateur
		separateur.setBackgroundColor(Color.WHITE);
		separateur.setMinimumWidth((int) (width * 0.02));

		LinearLayout layoutvalider = new LinearLayout(this);
		layoutvalider.setOrientation(LinearLayout.HORIZONTAL);
		layoutvalider.setPadding(10, 10, 10, 20);

		layoutvalider.addView(retour);
		layoutvalider.addView(separateur);
		layoutvalider.addView(valider);

		ScrollView scrol = new ScrollView(this);
		layout.setPadding(0, 0, 0, 0);
		layout.addView(layoutvalider);
		scrol.addView(layout);
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
	 * methode qui ajoute le champ time
	 * 
	 * @param idElement
	 * @param type
	 * @param nbrlayout
	 * @param titre
	 * @return
	 */
	private View addHeure(int idElement, String type, int nbrlayout,
			String titre) {
		LinearLayout l = new LinearLayout(this);

		TextView view = new TextView(this);
		if (titre == null)
			titre = "heure";
		view.setText(titre);
		view.setWidth((int) (widthChamp * 0.95));
		view.setTextSize(16);
		view.setTextColor(Color.parseColor("#0F74B3"));
		view.setTypeface(null, Typeface.BOLD);
		view.setPadding(0, 10, 0, 0);

		// BitmapDrawable backTitre = (BitmapDrawable)
		// this.getResources().getDrawable(R.drawable.separateur);
		// backTitre.setTileModeXY(Shader.TileMode.REPEAT,
		// Shader.TileMode.REPEAT);
		// view.setBackgroundDrawable(backTitre);

		Calendar c = Calendar.getInstance();

		EditText heuretexte = new EditText(this);
		heuretexte.setWidth((int) (widthChamp * 0.80));
		heuretexte.setTextSize(16);
		heuretexte.setHeight(10);
		heuretexte.setSingleLine(true);
		heuretexte.setTextColor(Color.parseColor("#FF555555"));

		heuretexte.setId(Integer.valueOf(idElement));

		int i = c.get(Calendar.HOUR_OF_DAY);
		int j = c.get(Calendar.MINUTE);
		String h = " ";
		if (i < 10)
			h = 0 + "" + i;
		else
			h = "" + i;
		if (j < 10)
			h += ":" + 0 + "" + j;
		else
			h += ":" + j;
		heuretexte.setText(h);
		heuretexte.setText(valeurre);
		ImageView pickerh = new ImageView(this);
		pickerh.setBackgroundResource(drawable.ic_menu_day);
		pickerh.setId(Integer.valueOf(idElement));
		Heures.add(idElement);
		pickerh.setOnClickListener(this);

		LinearLayout layHeure = new LinearLayout(this);
		layHeure.setGravity(Gravity.LEFT);
		layHeure.setOrientation(LinearLayout.HORIZONTAL);

		layHeure.addView(heuretexte);
		layHeure.addView(pickerh);

		valeurs.add(idElement + "," + type);

		l.setMinimumWidth((int) (widthChamp * 0.95));
		l.setPadding(10, 5, 10, 5);
		l.setOrientation(LinearLayout.VERTICAL);
		l.addView(view);
		l.addView(layHeure);
		return l;
	}

	/**
	 * methode qui ajoute le champ date
	 * 
	 * @param idElement
	 * @param type
	 * @param nbrlayout
	 * @param titre
	 * @return
	 */
	private View addDate(int idElement, String type, int nbrlayout, String titre) {
		LinearLayout l = new LinearLayout(this);

		TextView view = new TextView(this);
		if (titre == null)
			titre = "date";
		view.setText(titre);
		view.setWidth((int) (widthChamp * 0.95));
		view.setTextSize(16);
		view.setTextColor(Color.parseColor("#0F74B3"));
		view.setTypeface(null, Typeface.BOLD);
		view.setPadding(0, 10, 0, 0);

		// BitmapDrawable backTitre = (BitmapDrawable)
		// this.getResources().getDrawable(R.drawable.separateur);
		// backTitre.setTileModeXY(Shader.TileMode.REPEAT,
		// Shader.TileMode.REPEAT);
		// view.setBackgroundDrawable(backTitre);

		Calendar c = Calendar.getInstance();

		EditText datetexte = new EditText(this);
		datetexte.setWidth((int) (widthChamp * 0.80));
		datetexte.setTextSize(16);
		datetexte.setHeight(10);
		datetexte.setTextColor(Color.parseColor("#FF555555"));

		datetexte.setSingleLine(true);

		datetexte.setId(Integer.valueOf(idElement));
		int m = c.get(Calendar.MONTH) + 1;
		int j = c.get(Calendar.DAY_OF_MONTH);
		String d = " ";
		if (m < 10)
			d = "-" + 0 + "" + (m);
		else
			d = "-" + (m);
		if (j < 10)
			d += "-" + 0 + "" + j;
		else
			d += "-" + j;
		datetexte.setText(c.get(Calendar.YEAR) + d);
		datetexte.setText(valeurre);
		ImageView picker = new ImageView(this);
		picker.setBackgroundResource(drawable.ic_menu_my_calendar);
		picker.setId(Integer.valueOf(idElement));
		Dates.add(idElement);
		picker.setOnClickListener(this);

		LinearLayout layDate = new LinearLayout(this);
		layDate.setGravity(Gravity.LEFT);
		layDate.setOrientation(LinearLayout.HORIZONTAL);
		layDate.setLayoutParams(new android.view.ViewGroup.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		layDate.addView(datetexte);
		layDate.addView(picker);

		valeurs.add(idElement + "," + type);

		l.setMinimumWidth((int) (widthChamp * 0.75));
		l.setPadding(10, 5, 10, 5);
		l.setOrientation(LinearLayout.VERTICAL);
		l.addView(view);
		l.addView(layDate);
		return l;
	}

	/**
	 * methode qui ajoute le champ radio
	 * 
	 * @param idElement
	 * @param type
	 * @param nbrlayout
	 * @param titre
	 * @param libelles
	 * @param orientation
	 * @return
	 */
	private View addRadio(int idElement, String type, int nbrlayout,
			String titre, String libelles, String orientation) {
		LinearLayout l = new LinearLayout(this);

		TextView view = new TextView(this);
		if (titre == null)
			titre = "selectionner";
		view.setText(titre);
		view.setWidth((int) (widthChamp * 0.95));
		view.setTextSize(16);
		view.setTextColor(Color.parseColor("#0F74B3"));
		view.setTypeface(null, Typeface.BOLD);

		BitmapDrawable backTitre = (BitmapDrawable) this.getResources()
				.getDrawable(R.drawable.separateur);
		backTitre.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		view.setBackgroundDrawable(backTitre);

		view.setPadding(0, 10, 0, 0);

		l.setMinimumWidth(260);
		l.setPadding(10, 5, 10, 5);
		l.setOrientation(LinearLayout.VERTICAL);
		l.addView(view);

		StringTokenizer libel = null;

		if (libelles == null)
			libelles = "";
		// String[] libelle = libelles.split("/");
		String[] libelle = libelles.split("\\*_\\*");

		RadioButton[] rb = new RadioButton[libelle.length];
		RadioGroup rg = new RadioGroup(this); // create the RadioGroup
		rg.setOrientation(RadioGroup.VERTICAL);// or RadioGroup.VERTICAL
		for (int i = 0; i < libelle.length; i++) {
			rb[i] = new RadioButton(this);
			rg.addView(rb[i]); // the RadioButtons are added to the radioGroup
								// instead of the layout
			rb[i].setText(libelle[i]);
			rb[i].setTextColor(Color.parseColor("#FF555555"));
			rb[i].setTextSize(16);
			rb[i].setWidth((int) (widthChamp * 0.95));
			if (this.valeurre.equals(libelle[i]))
				rb[i].setChecked(true);

		}

		rg.setId(Integer.valueOf(idElement));
		valeurs.add(idElement + "," + type);
		l.addView(rg);

		return l;
	}

	/**
	 * methode qui ajoute le champ checkbox
	 * 
	 * @param idElement
	 * @param type
	 * @param nbrlayout
	 * @param titre
	 * @param libelles
	 * @param orientation
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private View addCheckBox(int idElement, String type, int nbrlayout,
			String titre, String libelles, String orientation) {
		LinearLayout l = new LinearLayout(this);

		TextView view = new TextView(this);
		if (titre == null)
			titre = "selectionner";
		view.setText(titre);
		view.setWidth((int) (widthChamp * 0.95));
		view.setTextSize(16);
		view.setTextColor(Color.parseColor("#0F74B3"));
		view.setTypeface(null, Typeface.BOLD);
		view.setPadding(0, 10, 0, 0);

		BitmapDrawable backTitre = (BitmapDrawable) this.getResources()
				.getDrawable(R.drawable.separateur);
		backTitre.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		view.setBackgroundDrawable(backTitre);

		l.setMinimumWidth(400);
		l.setPadding(10, 5, 10, 5);
		l.setOrientation(LinearLayout.VERTICAL);
		l.addView(view);

		LinearLayout checkLayout = new LinearLayout(this);
		checkLayout.setOrientation(LinearLayout.VERTICAL);

		if (libelles == null)
			libelles = "";
		// String[] libelle = libelles.split("/");
		String[] libelle = libelles.split("\\*_\\*");

		CheckBox[] check = new CheckBox[libelle.length];
		for (int i = 0; i < libelle.length; i++) {
			check[i] = new CheckBox(this);
			check[i].setText(libelle[i]);
			check[i].setTextSize(16);
			check[i].setWidth((int) (widthChamp * 0.95));
			check[i].setTextColor(Color.parseColor("#FF555555"));
			check[i].setId(Integer.valueOf(idElement + "" + i));
			if ((this.valeurre.endsWith("*_*" + libelle[i]))
					|| (this.valeurre.startsWith(libelle[i] + "*_*"))
					|| (this.valeurre.contains("*_*" + libelle[i] + "*_*"))
					|| (this.valeurre.equals(libelle[i])))
				check[i].setChecked(true);
			if ((libelle.length == 1) && (this.valeurre.equals("X")))
				check[i].setChecked(true);
			checkLayout.addView(check[i]);
		}
		l.addView(checkLayout);

		valeurs.add(idElement + "," + type + "," + libelle.length);

		return l;
	}

	/**
	 * methode qui ajoute le champ combobox
	 * 
	 * @param idElement
	 * @param type
	 * @param nbrlayout
	 * @param titre
	 * @param libelles
	 * @param orientation
	 * @return
	 */
	private View addComboBox(int idElement, String type, int nbrlayout,
			String titre, String libelles, String orientation) {
		LinearLayout l = new LinearLayout(this);

		TextView view = new TextView(this);
		if (titre == null)
			titre = "selectionner";
		view.setText(titre);
		view.setWidth((int) (widthChamp * 0.95));
		view.setTextSize(16);
		view.setTextColor(Color.parseColor("#0F74B3"));
		view.setTypeface(null, Typeface.BOLD);
		view.setPadding(0, 10, 0, 0);

		BitmapDrawable backTitre = (BitmapDrawable) this.getResources()
				.getDrawable(R.drawable.separateur);
		backTitre.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		view.setBackgroundDrawable(backTitre);

		Spinner spinner = new Spinner(this);
		List<String> list = new ArrayList<String>();

		StringTokenizer libel = null;

		if (libelles == null)
			libelles = "";
		// libel = new StringTokenizer(libelles, "/");
		libel = new StringTokenizer(libelles, "\\*_\\*");
		int i = 0;
		int h=0;
		while (libel.hasMoreElements()) {

			String str = String.valueOf(libel.nextElement());
			list.add(str);
			if (str.equals(valeurre))
				this.j.put(idElement, i);
			h=i;
			i++;
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setSelection(h);
		valeurs.add(idElement + "," + type);
		spinner.setId(Integer.valueOf(idElement));

		l.setMinimumWidth(260);
		l.setPadding(10, 5, 10, 5);
		l.setOrientation(LinearLayout.VERTICAL);
		l.addView(view);
		l.addView(spinner);
		return l;
	}

	/**
	 * methode qui ajoute le champ texte
	 * 
	 * @param idElement
	 * @param type
	 * @param nbrlayout
	 * @param titre
	 * @param taille_police
	 * @param couleur_police
	 * @param sousligne
	 * @param gras
	 * @param italique
	 * @param dimension
	 * @param persistent
	 * @return
	 */
	private View addEditText(int idElement, String type, int nbrlayout,
			String titre, String taille_police, String couleur_police,
			String sousligne, String gras, String italique, String dimension,
			String persistent) {

		LinearLayout l = new LinearLayout(this);

		TextView view = new TextView(this);
		if (titre == null)
			titre = "texte";
		view.setText(titre);
		view.setWidth((int) (widthChamp * 0.95));
		view.setTextSize(16);
		view.setTextColor(Color.parseColor("#0F74B3"));
		view.setTypeface(null, Typeface.BOLD);
		view.setPadding(0, 10, 0, 0);

		// BitmapDrawable backTitre = (BitmapDrawable)
		// this.getResources().getDrawable(R.drawable.separateur);
		// backTitre.setTileModeXY(Shader.TileMode.REPEAT,
		// Shader.TileMode.REPEAT);
		// view.setBackgroundDrawable(backTitre);

		EditText text = new EditText(this);
		text.setWidth((int) (widthChamp * 0.95));

		text.setHeight(10);

		text.setSingleLine(true);
		text.setTextSize(16);
		text.setTextColor(Color.parseColor("#FF555555"));
		text.setText(valeurre);
		valeurs.add(idElement + "," + type);
		text.setId(Integer.valueOf(idElement));

		l.setPadding(10, 5, 10, 5);
		l.setOrientation(LinearLayout.VERTICAL);
		l.addView(view);
		l.addView(text);
		return l;

	}

	/**
	 * methode qui ajoute le champ number
	 * 
	 * @param idElement
	 * @param type
	 * @param nbrlayout
	 * @param titre
	 * @param taille_police
	 * @param couleur_police
	 * @param sousligne
	 * @param gras
	 * @param italique
	 * @param dimension
	 * @param persistent
	 * @return
	 */
	private View addEditNumber(int idElement, String type, int nbrlayout,
			String titre, String taille_police, String couleur_police,
			String sousligne, String gras, String italique, String dimension,
			String persistent) {

		LinearLayout l = new LinearLayout(this);

		TextView view = new TextView(this);
		if (titre == null)
			titre = "chiffre";
		view.setText(titre);
		view.setWidth((int) (widthChamp * 0.95));
		view.setTextSize(16);
		view.setTextColor(Color.parseColor("#0F74B3"));
		view.setTypeface(null, Typeface.BOLD);
		view.setPadding(0, 10, 0, 0);

		// BitmapDrawable backTitre = (BitmapDrawable)
		// this.getResources().getDrawable(R.drawable.separateur);
		// backTitre.setTileModeXY(Shader.TileMode.REPEAT,
		// Shader.TileMode.REPEAT);
		// view.setBackgroundDrawable(backTitre);

		EditText text = new EditText(this);
		text.setWidth((int) (widthChamp * 0.95));
		text.setInputType(InputType.TYPE_CLASS_NUMBER);

		text.setSingleLine(true);
		text.setTextSize(16);
		text.setTextColor(Color.parseColor("#FF555555"));
		text.setText(valeurre);
		valeurs.add(idElement + "," + type);
		text.setId(Integer.valueOf(idElement));

		l.setMinimumWidth(260);
		l.setPadding(10, 5, 10, 5);
		l.setOrientation(LinearLayout.VERTICAL);
		l.addView(view);
		l.addView(text);
		return l;

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
				Toast.makeText(this, " pas de connexion internet",
						Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * action du bouton retour du mobile
	 */

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // do something on
															// back.
			finish();
			Intent lIntentObj = new Intent(this, MainActivity.class);
			lIntentObj.putExtra("current", 1);
			lIntentObj.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(lIntentObj);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);

			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * gestion des evenements de clique
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case -1: // action bouton retour
			finish();
			Intent retour = new Intent(this, FormWeb.class);
			retour.putExtra("idF", idF);
			retour.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(retour);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
			break;
		case 1: // action bouton enregistrer
			Intent enregistrer = new Intent(this, ActivityEnregistrer.class);
			int e = 0;
			String type = null;
			String id = null;
			String valeur;
			enregistrer.putExtra("version", 2);
			enregistrer.putExtra("indice", indice);
			// recuperer les ids des champs et leur type
			Iterator i = valeurs.iterator();
			while (i.hasNext()) {
				String element = i.next().toString();
				String[] s = element.split(",");
				if (s != null) {
					type = s[1];
					id = s[0];
				}

				// traitement du champs par type
				if (type.equals("text")) {
					valeur = (((EditText) findViewById(Integer.valueOf(id)))
							.getText().toString());
					if (valeur.equals(""))
						valeur = " ";
					enregistrer.putExtra("element" + e, valeur + "#0_0#" + id);
				} else if (type.equals("chiffre")) {
					valeur = (((EditText) findViewById(Integer.valueOf(id)))
							.getText().toString());
					if (valeur.equals(""))
						valeur = " ";
					enregistrer.putExtra("element" + e, valeur + "#0_0#" + id);
				} else if (type.equals("radio")) {
					RadioGroup rg = ((RadioGroup) findViewById(Integer
							.valueOf(id)));
					RadioButton radio_checked = (RadioButton) findViewById(rg
							.getCheckedRadioButtonId());
					if (radio_checked != null) {
						valeur = (radio_checked.getText().toString());
						if (!valeur.equals(""))
							valeur = valeur + "#0_0#" + id;
						else
							valeur = "X" + "#0_0#" + id;
						;
						enregistrer.putExtra("element" + e, valeur);
					} else
						enregistrer.putExtra("element" + e, " " + "#0_0#" + id);
				} else if (type.equals("checkbox")) {
					String rs = "";
					for (int j = 0; j < Integer.valueOf(s[2]); j++) {
						if (((CheckBox) findViewById(Integer.valueOf(id + ""
								+ j))).isChecked()) {
							valeur = (((CheckBox) findViewById(Integer
									.valueOf(id + "" + j))).getText()
									.toString());

							if (valeur.equals(""))
								valeur = "X";

							if (rs.isEmpty())
								rs = valeur;
							else
								rs += "*_*" + valeur;
						}
					}
					if (rs != "")
						enregistrer.putExtra("element" + e, rs + "#0_0#" + id);
					else
						enregistrer.putExtra("element" + e, " " + "#0_0#" + id);
				} else if (type.equals("combobox")) {
					if (((Spinner) findViewById(Integer.valueOf(id)))
							.getSelectedItem() == null)
						valeur = " " + "#0_0#" + id;
					else
						valeur = (((Spinner) findViewById(Integer.valueOf(id)))
								.getSelectedItem().toString() + "#0_0#" + id);

					enregistrer.putExtra("element" + e, valeur);
				} else if (type.equals("date")) {
					String date = ((EditText) findViewById(Integer.valueOf(id)))
							.getText().toString();
					if (date.equals(""))
						date = " ";
					enregistrer.putExtra("element" + e, date + "#0_0#" + id);
				} else if (type.equals("time")) {
					String h = ((EditText) findViewById(Integer.valueOf(id)))
							.getText().toString();
					if (h.equals(""))
						h = " ";
					enregistrer.putExtra("element" + e, h + "#0_0#" + id);
				}
				e++;
			}
			enregistrer.putExtra("count", e);
			finish();
			enregistrer.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(enregistrer);
			overridePendingTransition(R.anim.fadin, R.anim.fadout);
			break;
		}

		// pour synchroniser le datePiker avec son champ
		Iterator ds = Dates.iterator();
		while (ds.hasNext()) {
			final int dateP = (Integer) ds.next();
			if (v.getId() == dateP) {
				String d = ((EditText) findViewById(dateP)).getText()
						.toString();
				String[] date = d.split("-");
				int a = Integer.valueOf(date[0]);
				int m = Integer.valueOf(date[1]) - 1;
				int j = Integer.valueOf(date[2]);
				DatePickerDialog datedialog = new DatePickerDialog(this,
						new OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker datePicker, int i,
									int j, int k) {
								String d = " ";
								if (j < 9)
									d = i + "-" + 0 + "" + (j + 1);
								else
									d = i + "-" + (j + 1);
								if (k < 10)
									d += "-" + 0 + "" + k;
								else
									d += "-" + k;
								((EditText) findViewById(dateP)).setText(d);
							}
						}, a, m, j);
				datedialog.setTitle("date");
				datedialog.show();
			}
		}

		// pour synchroniser le timePiker avec son champ
		Iterator hs = Heures.iterator();
		while (hs.hasNext()) {

			final int heureP = (Integer) hs.next();
			if (v.getId() == heureP) {
				String h = ((EditText) findViewById(heureP)).getText()
						.toString();
				String[] date = h.split(":");
				int hr = Integer.valueOf(date[0]);
				int m = Integer.valueOf(date[1]);
				TimePickerDialog datedialog = new TimePickerDialog(this,
						new OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker datePicker, int i,
									int j) {
								String h = " ";
								if (i < 10)
									h = 0 + "" + i;
								else
									h = "" + i;
								if (j < 10)
									h += ":" + 0 + "" + j;
								else
									h += ":" + j;
								((EditText) findViewById(heureP)).setText(h);
							}

						}, hr, m, true);
				datedialog.setTitle("heure");
				datedialog.show();
			}
		}

	}

}
