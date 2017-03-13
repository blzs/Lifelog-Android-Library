package com.sonymobile.lifelog.api.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivonybalazs on 2017. 03. 13..
 */

public class MePhysicalActivity {

    String id = "";
    String type = "";
    String subtype = "";
    String startTime = "";
    String endTime = "";
    List<String>  steps = new ArrayList<>();
    List<String> distances = new ArrayList<>();
    List<String> aee = new ArrayList<>();
    List<String> tee = new ArrayList<>();

    public MePhysicalActivity(JSONObject obj) throws JSONException {
        id = obj.getString("id");
        startTime = obj.getString("startTime");
        endTime = obj.getString("endTime");
        type = obj.getString("type");
        subtype = obj.getString("subtype");

        JSONObject detailsObj = obj.getJSONObject("details");

        JSONArray stepsJSON = detailsObj.getJSONArray("steps");
        JSONArray distanceJSON = detailsObj.getJSONArray("distance");
        JSONArray aeeJSON = detailsObj.getJSONArray("aee");
        JSONArray teeJSON = detailsObj.getJSONArray("tee");

        try {

            for (int i = 0; i < stepsJSON.length(); i++) {
                steps.add(stepsJSON.get(i).toString());
            }

            for (int i = 0; i < distanceJSON.length(); i++) {
                distances.add(distanceJSON.get(i).toString());
            }

            for (int i = 0; i < aeeJSON.length(); i++) {
                aee.add(aeeJSON.get(i).toString());
            }

            for (int i = 0; i < teeJSON.length(); i++) {
                tee.add(teeJSON.get(i).toString());
            }



        }catch (Exception e ){

        }
    }



}
