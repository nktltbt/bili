package com.applifit.bilifit.DataBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

/**
 * classe pour recuperer les données de la base distant
 * @author Faissal
 *
 */
public class GetHTTP {
	
	String url;
	String Rs;
	
	
	
	 public GetHTTP(String url) {
		super();
		this.url = url;
	}
	 
	 public String getResultat(){
		 return GetHTML(url, null);
	 }

			//=======================================================
		// Recupèrer les données du Web
		//=======================================================
			public String GetHTML(String url, List <NameValuePair> nvps) {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				try {
					HttpResponse res;
					URI uri = new URI(url);
			    	if (nvps!=null){
			    		HttpPost methodpost = new HttpPost(uri);
			    		methodpost.addHeader("pragma","no-cache");
			    		methodpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			    		res = httpClient.execute(methodpost);
				    } else {
			    		HttpGet methodget = new HttpGet(uri);
				    	methodget.addHeader("pragma","no-cache");
				    	res = httpClient.execute(methodget);
				    }
			    	if(res.getEntity()!=null){
			    		InputStream data = res.getEntity().getContent();
				    	return generateString(data);
			    	}
			    		    	        	
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "";
			}
			
			//=======================================================
			// Generer le String 
			//=======================================================
			static public String generateString(InputStream stream) {
				InputStreamReader reader = new InputStreamReader(stream);
				BufferedReader buffer = new BufferedReader(reader);
				StringBuilder sb = new StringBuilder();
				try { 
					String cur;   
					while ((cur = buffer.readLine()) != null) {   
						sb.append(cur).append("\n");  
					}  
				} catch (IOException e) {  
					e.printStackTrace();  
				}
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return sb.toString(); 
			}

}
