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
    String[] steps;
    List<String> distances = new ArrayList<>();
    List<String> aee = new ArrayList<>();
    List<String> tee = new ArrayList<>();

    public MePhysicalActivity(JSONObject obj) throws JSONException {
        id = obj.getString("id");
        startTime = obj.getString("startTime");
        endTime = obj.getString("endTime");
        type = obj.getString("type");
        subtype = obj.getString("subtype");

        JSONArray jarr = obj.getJSONArray("details");
        for (int i = 0; i < jarr.length(); i++) {
            try {


            } catch (Exception e ){

            }

        }
    }



}
