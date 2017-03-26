package com.sonymobile.lifelog.api.requests;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.api.models.MeSleepActivity;
import com.sonymobile.lifelog.utils.ISO8601Date;
import com.sonymobile.lifelog.utils.VolleySingleton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ivonybalazs on 2017. 03. 14..
 */

public class MeSleepRequest {


    private static final String TAG = "LifeLog:SleepAPI";
    private String mStartTime, mEndTime;
    private Integer mLimit;

    @Nullable
    private String mNextPage;


    private static final Uri ACTIVITIES_BASE_URL =
            Uri.parse(LifeLog.API_BASE_URL).buildUpon().appendEncodedPath("users/me/activities").build();

    public MeSleepRequest(Calendar start, Calendar end, Integer limit) {
        if (start != null) mStartTime = ISO8601Date.fromCalendar(start);
        if (end != null) mEndTime = ISO8601Date.fromCalendar(end);
        mLimit = limit;
    }

    public static MeSleepRequest prepareRequest(Calendar start, Calendar end, Integer limit) {
        if (limit == null || limit > 500) {
            limit = 500;
        }
        return new MeSleepRequest(start, end, limit);
    }

    public static MeSleepRequest prepareRequest(int limit ) {
        return prepareRequest(null, null, limit);
    }

    public void get(final Context context, final OnSleepFetched osf){

        final Context appContext = context.getApplicationContext();

        LifeLog.checkAuthentication(appContext, new LifeLog.OnAuthenticationChecked() {
            @Override
            public void onAuthChecked(boolean authenticated) {
                if(authenticated) {
                    callSleepAPI(appContext, osf);
                }
            }
        });

    }

    private void callSleepAPI(Context appContext, OnSleepFetched osf) {

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

        uriBuilder.appendQueryParameter("type", "sleep");

        final JsonObjectRequest activitiesRequest = new ActivitiesRequest(appContext, uriBuilder.toString(), osf);
        VolleySingleton.getInstance(appContext).addToRequestQueue(activitiesRequest);

    }

    private class ActivitiesRequest extends AuthedJsonObjectRequest {
        public ActivitiesRequest(final Context appContext, String url, final OnSleepFetched osf) {
            super(appContext, Request.Method.GET, url, (JSONObject) null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    ArrayList<MeSleepActivity> activities = new ArrayList<>(mLimit);

                    Log.v(TAG, jsonObject.toString());

                    try {
                        JSONArray resultArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < resultArray.length(); i++) {
                            activities.add(new MeSleepActivity(resultArray.getJSONObject(i)));
                        }

                        JSONArray links = jsonObject.optJSONArray("links");
                        if (links != null) {
                            for (int i = 0; i < links.length(); i++) {
                                JSONObject object = links.getJSONObject(i);
                                if (TextUtils.equals("next", object.optString("rel"))) {
                                    String href = object.optString("href");
                                    if (!TextUtils.isEmpty(href) && URLUtil
                                            .isNetworkUrl(href)) {
                                        mNextPage = href;
                                    }
                                }
                            }
                        }




                        osf.onSleepFetched(activities);
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


    public boolean getNextPage(@NonNull final Context context, @NonNull final OnSleepFetched onSleepFetched) {
        if (mNextPage == null) {
            return false;
        }
        Context appContext = context.getApplicationContext();
        final JsonObjectRequest sleepRequest = new ActivitiesRequest(appContext, mNextPage, onSleepFetched);
        mNextPage = null;
        VolleySingleton.getInstance(appContext).addToRequestQueue(sleepRequest);
        return true;

    }


    public interface OnSleepFetched {
        void onSleepFetched(List<MeSleepActivity> activities);
    }

}
