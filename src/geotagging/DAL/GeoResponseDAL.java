package geotagging.DAL;

import geotagging.DES.Response;
import geotagging.IDAL.GeoResponseIDAL;
import geotagging.app.R;
import geotagging.utils.BackendHelperSingleton;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class GeoResponseDAL implements GeoResponseIDAL {
	
	private Activity cx;
	private String entity_id;
	//private String time;
	
	private BackendHelperSingleton helper;
	
	public GeoResponseDAL(Activity cx, String entity_id) {
		this.cx = cx;
		helper = BackendHelperSingleton.getInstance();
        this.entity_id = entity_id;
        //time = "2010-09-10 08:00:00";
	}

	public List<Response> getLatestResponsesByTime(String time) {
		String jsonText = helper.getJsonText(cx.getResources().getString(R.string.GeotaggingGetResponses)+entity_id);
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
                ne.setActualImg(helper.fetchImage(ne.getImage()));
                respsList.add(ne);
            }
			
			return respsList;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

}
