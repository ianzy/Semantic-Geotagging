package geotagging.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BackendHelperSingleton {
	
	// Private constructor prevents instantiation from other classes
   protected BackendHelperSingleton() {
   }
 
   /**
	* SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	* or the first access to SingletonHolder.INSTANCE, not before.
	*/
   private static class SingletonHolder { 
     public static final BackendHelperSingleton INSTANCE = new BackendHelperSingleton();
   }
 
   public static BackendHelperSingleton getInstance() {
     return SingletonHolder.INSTANCE;
   }
   
   //Backend helper methods
   public void postContent(String apiurl, String content) {
	   URL url;
       HttpURLConnection connection = null; 
       
       //{"entity":{"location":"1000-1136 Fairmont Dr San Bruno, CA 94066","created_at":"2010-11-03T07:39:57Z","title":"House on fire","updated_at":"2010-11-03T07:39:57Z","lng":-122.441361,"id":15,"description":"1000-1136 Fairmont Dr\nSan Bruno, CA 94066","lat":37.624123,"icon_uri":""}}
       try {
           //Create connection
           url = new URL(apiurl);
           connection = (HttpURLConnection)url.openConnection();
           connection.setRequestMethod("POST");
           connection.setRequestProperty("Content-Type", 
                "application/json;charset=UTF-8");
     			
           connection.setRequestProperty("Content-Length", 
                    Integer.toString(content.getBytes().length));
           connection.setRequestProperty("Content-Language", "en-US");  
     			
           connection.setUseCaches (false);
           connection.setDoInput(true);
           connection.setDoOutput(true);

           //Send request
           DataOutputStream wr = new DataOutputStream (
                       connection.getOutputStream ());
           wr.writeBytes (content);
           wr.flush ();
           wr.close ();
           Log.i("<<<<<<<<<<<<<<<<<<<<<<<here","test");
           //Get Response	
           InputStream is = connection.getInputStream();
           BufferedReader rd = new BufferedReader(new InputStreamReader(is));
           String line;
           
           StringBuffer response = new StringBuffer(); 
           while((line = rd.readLine()) != null) {
             response.append(line);
             Log.i("line:", line);
           }
           Log.i("response---------------------", response.toString());
           rd.close();
         } catch (Exception e) {
           e.printStackTrace();
         } finally {

           if(connection != null) {
             connection.disconnect(); 
           }
           
         }
   }
   
   public String getJsonText(String api){
		URL url;
		BufferedReader rd;
		StringBuilder sb = new StringBuilder("");
		try {
			url = new URL(api);
			URLConnection conn = url.openConnection();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        // Get the response
			String line = "";
           while ((line = rd.readLine()) != null) {
           	sb.append(line);
           }
           
           rd.close();
           return sb.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.v("title","message for catch");
			e.printStackTrace();
			return null;
		}
	}
	
	//methods to load the image
   public Bitmap fetchImage(String fileUrl){
         URL myFileUrl =null;          
         try {
              myFileUrl= new URL(fileUrl);
         } catch (MalformedURLException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
              return null;
         }
         try {
              HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
              conn.setDoInput(true);
              conn.connect();
              InputStream is = conn.getInputStream();
              
              return BitmapFactory.decodeStream(is);
        
         } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
              return null;
         }
    }
   
}
