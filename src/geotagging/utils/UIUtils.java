/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotagging.utils;

import geotagging.app.GeotaggingMap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class UIUtils {


    /**
     * Invoke "home" action, returning to {@link HomeActivity}.
     */
    public static void goHome(Context context) {
        final Intent intent = new Intent(context, GeotaggingMap.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * Invoke "search" action, triggering a default search.
     */
    public static void goSearch(Activity activity) {
        activity.startSearch(null, false, Bundle.EMPTY, false);
    }
    
    public static void goBack(Activity activity) {
    	activity.setResult(Activity.RESULT_CANCELED);
    	activity.finish();
    }

    public static String TimeParser(String time) {
    	DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	String res = null;
		try {
			Date now = new Date();
			Date date = format.parse(time);
			Calendar c = Calendar.getInstance();c.setTime(now);
			Calendar c2 = Calendar.getInstance();c2.setTime(date);
			
			long diffInSeconds = (c.getTimeInMillis()-c2.getTimeInMillis()) / 1000;

		    long diff[] = new long[] { 0, 0, 0, 0, 0 };
		    /* sec */diff[4] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		    /* min */diff[3] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		    /* hours */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		    /* days */diff[1] = (diffInSeconds = (diffInSeconds / 24));
		    /* weeks */diff[0] = (diffInSeconds = (diffInSeconds / 7));

		    if(diff[0] != 0) {
		    	res = String.format("%d week%s ago", diff[0],
				        diff[0] > 1 || diff[0]==0 ? "s" : "");
		    } else if (diff[0] == 0 && diff[1] != 0) {
		    	res = String.format("%d day%s ago", diff[1],
				        diff[1] > 1 || diff[1]==0 ? "s" : "");
		    } else if (diff[0] == 0 && diff[1] == 0 && diff[2] != 0) {
		    	res =  String.format("%d hour%s ago", diff[2],
				        diff[2] > 1 || diff[2]==0 ? "s" : "");
		    } else if (diff[0] == 0 && diff[1] == 0 && diff[2] == 0 && diff[3] != 0) {
		    	res =  String.format("%d minute%s ago", diff[3],
				        diff[3] > 1 || diff[3]==0 ? "s" : "");
		    } else if (diff[0] == 0 && diff[1] == 0 && diff[2] == 0 && diff[3] == 0) {
		    	res = String.format("%d second%s ago", diff[4],
		    			diff[4] > 1 || diff[4] == 0 ? "s" : "");
		    }
		    
		} catch (ParseException e) {
			
		}
		return res;
    }
    
}
