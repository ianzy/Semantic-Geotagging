package geotagging.DAL;

import geotagging.DES.Response;
import geotagging.IDAL.GeoResponseIDAL;
import geotagging.app.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class GeoResponseDAL implements GeoResponseIDAL {
	
	private Activity cx;
	private String entity_id;
	//private String time;
	
	public GeoResponseDAL(Activity cx, String entity_id) {
		this.cx = cx;
		
        this.entity_id = entity_id;
        //time = "2010-09-10 08:00:00";
	}

	public List<Response> getLatestResponsesByTime(String time) {
		String jsonText = this.getJsonText(cx.getResources().getString(R.string.GeotaggingGetResponses)+entity_id+"&time="+time);
		Log.i("test", cx.getResources().getString(R.string.GeotaggingGetResponses)+entity_id+"&time="+time);
		Log.i("jsontext", jsonText);
		return getResponsesFromJsonText(jsonText);
	}

	public List<Response> getLatestTenResponses(int entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Response> getResponsesByEntityID(int entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void newResponse(int entityId) {
		// TODO Auto-generated method stub

	}
	
	//methods for getting responses need to be refactored
    private List<Response> getResponsesFromJsonText(String jsonText) {
    	
        if (jsonText == null)
        {
        	return null;
        }
        
        List<Response> respsList = new ArrayList<Response>();
        Response ne = null;
        try {
			JSONArray resps = new JSONArray(jsonText);
			for (int i=0; i<resps.length() ;i++)
            {
				ne = new Response();
                JSONObject resp = resps.getJSONObject(i);
                ne.setLat(resp.getJSONObject("resp").getDouble("lat"));
                ne.setLng(resp.getJSONObject("resp").getDouble("lng"));
                ne.setResp(resp.getJSONObject("resp").getString("resp"));
                ne.setLocation(resp.getJSONObject("resp").getString("location"));
                ne.setUsername(resp.getJSONObject("resp").getString("username"));
                ne.setTime(resp.getJSONObject("resp").getString("time"));
                ne.setImage(resp.getJSONObject("resp").getString("image"));
                ne.setActualImg(fetchImage(ne.getImage()));
                respsList.add(ne);
            }
			
			return respsList;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	private String getJsonText(String api){
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
    private Bitmap fetchImage(String fileUrl){
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
