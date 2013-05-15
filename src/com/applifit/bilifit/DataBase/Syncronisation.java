package com.applifit.bilifit.DataBase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.StrictMode;
import android.widget.Toast;

import com.applifit.bilifit.Config;
import com.applifit.bilifit.compte.Utilisateur;
import com.applifit.bilifit.formulaire.Element;
import com.applifit.bilifit.formulaire.Formulaire;
import com.applifit.bilifit.formulaire.Parametre;

/**
 * classe qui communique avec le serveur
 * @author Faissal
 *
 */
public class Syncronisation {
	
	Utilisateur u = new Utilisateur();
	Formulaire form = new Formulaire();
	Element element = new Element();
	Parametre parametre = new Parametre();

	
	
	public Syncronisation() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	

/**
 * modifier l'id du mobile pour un utilisateur pour le connecter ou le deconnecter
 * @param iduser
 * @param IdDevice
 */
	public void modifDevice(int iduser, String IdDevice) {
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		
		
			String rq = Config.URL+"compte/modifierDevice/device/"+IdDevice+"/id/"+iduser;
			HttpClient httpclient = new DefaultHttpClient();
		    try {
				HttpResponse response = httpclient.execute(new HttpGet(rq));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	/**
	 * recuperer les infos du profile par mail
	 * @param mail
	 * @return
	 */
	public Utilisateur recuperUser (String mail){
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 

		
		
		GetHTTP get = new GetHTTP(Config.URL+"compte/recupererCompte/email/"+mail);
		String user = get.getResultat();
		try {
		JSONObject json = new JSONObject(user);
		if(json==null) return null;
		int _id = (Integer) json.get("id");
		String _mail = (String) json.get("mail");
		String _fonction = (String) json.get("fonction");
		String _nom = (String) json.get("nom");
		String _prenom = (String) json.get("prenom");
		String _password = (String) json.get("password");
		String _profil = (String) json.get("profil");
		int _etat = (Integer) json.get("etat");	
		String _device =json.getString("device");

			u = new Utilisateur(_id, _nom, _prenom, _mail, _fonction, _profil, _password, _etat, _device);
		
		} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} 
		return u;
	}

	/**
	 * recuperer les parametres des formulaires d'un utilisateur
	 * @param iduser
	 * @return
	 */
	public List<Formulaire> recuperFormulaire (int iduser){
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		List<Formulaire> list_form = new ArrayList<Formulaire>();
		

		GetHTTP get = new GetHTTP(Config.URL+"formulaire/listerFormulaireLastVersion/compte/"+iduser);
		String formS = get.getResultat();
		try {
		JSONArray  jsonArr = new JSONArray(formS);
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject json = jsonArr.getJSONObject(i);
			if(json!=null) {
				int _id = (Integer) json.get("id");
				int _version = (Integer) json.get("version");
				String _nom = (String) json.get("nom");
				String _commentaire = (String) json.get("commentaire");
//				int _iduser = (Integer) json.get("creator");
				int _etat = (Integer) json.get("etat");
				form = new Formulaire(_id, _nom, _commentaire, _version, iduser, _etat);
				list_form.add(form);
			}
		}
		
		} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} 
		return list_form;
	}



	/**
	 * recuperer les champs d'un formulaire
	 * @param idform
	 * @return
	 */
	public List<Element> recuperElement(int idform) {
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy); 
				
				List<Element> list_element = new ArrayList<Element>();

				GetHTTP get = new GetHTTP(Config.URL+"element/listerElement/formulaire/"+idform);
				String elementS = get.getResultat();
				try {
				JSONArray  jsonArr = new JSONArray(elementS);
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject json = jsonArr.getJSONObject(i);
					if(json!=null) {
						int _id = (Integer) json.get("id");
						String _type = (String) json.get("type");
						int _position_x = (Integer) json.get("position_x");
						int _position_y = (Integer) json.get("position_y");
						element = new Element(_id, _type, _position_x, _position_y, idform);
						list_element.add(element);
					}
				}
				
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				} 
				return list_element;
	}
	
	/**
	 * recuperer les parametres d'un champ
	 * @param idelement
	 * @return
	 */
	public List<Parametre> recuperParametre(int idelement) {
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy); 
				
				List<Parametre> list_parametre = new ArrayList<Parametre>();

				GetHTTP get = new GetHTTP(Config.URL+"parametre/listerParametre/element/"+idelement);
				String parametreS = get.getResultat();
				try {
				JSONArray  jsonArr = new JSONArray(parametreS);
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject json = jsonArr.getJSONObject(i);
					if(json!=null) {
						int _id = (Integer) json.get("id");
						String _nom = (String) json.get("nom");
						String _valeur = (String) json.get("valeur");
						parametre = new Parametre(_id, _nom, _valeur, idelement);
						list_parametre.add(parametre);
					}
				}
				
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				} 
				return list_parametre;
	}
	
}
