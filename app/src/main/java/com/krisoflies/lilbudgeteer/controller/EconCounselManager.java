package com.krisoflies.lilbudgeteer.controller;

import android.util.Log;

import com.krisoflies.lilbudgeteer.model.JSONEconCounsel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/* by Paolo on 6/7/2015. */
public class EconCounselManager {

    public static ArrayList<JSONEconCounsel> setCounsels(String JSONEconomics) {
        ArrayList<JSONEconCounsel> listECounsel = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(JSONEconomics);
            JSONObject jResults = jObject.getJSONObject("results");
            JSONArray jArray = jResults.getJSONArray("EconomicalInfo");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject oneObject = jArray.getJSONObject(i);
                JSONObject jsDescrip = oneObject.getJSONObject("URL");
                //Finally pulling the kimono data from its objects
                String href = jsDescrip.getString("href");
                String text = jsDescrip.getString("text");
                //The filling of the counsels list.
                JSONEconCounsel jsCounsel = new JSONEconCounsel();
                jsCounsel.setHref(href);
                jsCounsel.setTitle(text);
                listECounsel.add(jsCounsel);
            }
        } catch (JSONException e) {
            Log.d("setCounsels", e.getLocalizedMessage());
        }
        return listECounsel;
    }
}