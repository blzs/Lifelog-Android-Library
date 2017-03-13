package com.sonymobile.lifelog.api.requests;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.api.models.MePhysicalActivity;
import com.sonymobile.lifelog.utils.ISO8601Date;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ivonybalazs on 2017. 03. 13..
 */

public class MePhysicalRequest {

    private static final String TAG = "LifeLog:ActivitesAPI";
    private String mStartTime, mEndTime;
    private Integer mLimit;


    private static final Uri ACTIVITIES_BASE_URL =
            Uri.parse(LifeLog.API_BASE_URL).buildUpon().appendEncodedPath("users/me/activities").build();

    public MePhysicalRequest(Calendar start, Calendar end, Integer limit) {
        if (start != null) mStartTime = ISO8601Date.fromCalendar(start);
        if (end != null) mEndTime = ISO8601Date.fromCalendar(end);
        mLimit = limit;
    }

    public static MePhysicalRequest prepareRequest(Calendar start, Calendar end, Integer limit){
        if (limit == null || limit > 500) {
            limit = 500;
        }
        return new MePhysicalRequest(start, end, limit);
    }

    public static MePhysicalRequest prepareRequest(Integer limit){
        return prepareRequest(null, null, limit);
    }

    public void get(final Context context, final OnActivityFetched oaf) {

        final Context appContext = context.getApplicationContext();

        LifeLog.checkAuthentication(appContext, new LifeLog.OnAuthenticationChecked() {
            @Override
            public void onAuthChecked(boolean authenticated) {
                if(authenticated) {
                    callMeAPI(appContext, oaf);
                }
            }
        });

    }

    private void callMeAPI(Context appContext, OnActivityFetched oaf) {

        Uri.Builder uriBuilder = ACTIVITIES_BASE_URL.buildUpon();

        if (!TextUtils.isEmpty(mStartTime)) {
            uriBuilder.appendQueryParameter("start_time", mStartTime);
        }
        if (!TextUtils.isEmpty(mEndTime)) {
            uriBuilder.appendQueryParameter("end_time", mEndTime);
        }
        if (mLimit > 0) {
            uriBuilder.appendQueryParameter("limit", String.valueOf(mLimit));
        }

        uriBuilder.appendQueryParameter("type", "physical");

        final JsonObjectRequest activitiesRequest = new ActivitiesRequest(appContext, uriBuilder.toString(), oaf);
        VolleySingleton.getInstance(appContext).addToRequestQueue(activitiesRequest);

    }


    private class ActivitiesRequest extends AuthedJsonObjectRequest {
        public ActivitiesRequest(final Context appContext, String url, final OnActivityFetched oaf) {
            super(appContext, Request.Method.GET, url, (JSONObject) null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    ArrayList<MePhysicalActivity> activities = new ArrayList<>(mLimit);

                    Log.v(TAG, jsonObject.toString());

                    try {
                        JSONArray resultArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < resultArray.length(); i++) {
                            activities.add(new MePhysicalActivity(resultArray.getJSONObject(i)));
                        }



                        oaf.onActivityFetched(activities);
                    } catch (JSONException e) {
                            Log.w(TAG, "JSONException", e);
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                                if (volleyError.networkResponse != null) {
                                    Log.w(TAG, "VolleyError: "
                                            + new String(volleyError.networkResponse.data), volleyError);
                                } else {
                                    Log.w(TAG, volleyError);
                                }
                        }
                    });


        }
    }

    public interface OnActivityFetched  {
        void onActivityFetched(List<MePhysicalActivity> activities);
    }
}
