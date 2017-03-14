package com.sonymobile.lifelog.api.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivonybalazs on 2017. 03. 14..
 */

public class MeSleepActivity {

    String id = "";
    String type = "";
    String startTime = "";
    String endTime = "";

    List<String> states = new ArrayList<>();

    public MeSleepActivity(JSONObject jsonObject) throws JSONException {

        id = jsonObject.getString("id");
        startTime = jsonObject.getString("startTime");
        endTime = jsonObject.getString("endTime");
        type = jsonObject.getString("type");


        JSONObject detailsObj = jsonObject.getJSONObject("details");

        JSONArray statesJSON = detailsObj.getJSONArray("state");


        try {

            for (int i = 0; i < statesJSON.length(); i++) {
                states.add(statesJSON.get(i).toString());
            }

        }catch (Exception e ){

        }


    }



}
