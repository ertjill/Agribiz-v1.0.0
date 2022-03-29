package com.example.agribiz_v100.location;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BrgyManagement {
    private static final String TAG = "BrgyManagement";
    String brgy_url;
    private List<JSONObject> brgyList;
    private List<String> brgyNames;
    private AsyncResponse delegate = null;

    public BrgyManagement(String code,AsyncResponse asyncResponse) {
        delegate = asyncResponse;
        brgyList = new ArrayList<>();
        brgyNames = new ArrayList<>();
        brgy_url = "https://psgc.gitlab.io/api/municipalities/"+code+"/barangays/";
        new MyBarangay().execute();
    }

    public List<JSONObject> getbrgyList() {
        return brgyList;
    }

    public List<String> getbrgyNames() {
        return brgyNames;
    }

    public class MyBarangay extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... provinceCode) {
            getbrgyList(getBrgysString(brgy_url));
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            delegate.processFinish(brgyList, brgyNames);
            for (JSONObject brgy : brgyList
            ) {
                try {
                    Log.d(TAG, brgy.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    public String getBrgysString(String brgy) {
        String string = "";
        try {
            URL url = new URL(brgy);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String stringBuffer;
            while ((stringBuffer = bufferedReader.readLine()) != null) {
                string = String.format("%s%s", string, stringBuffer);
            }
            bufferedReader.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return string;
    }

    public void getbrgyList(String jsonString) {
        try {
            JSONArray brgy = new JSONArray(jsonString);
            for (int i = 0; i < brgy.length(); i++) {
                brgyList.add(brgy.getJSONObject(i));
                brgyNames.add(brgy.getJSONObject(i).get("name").toString());
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public interface AsyncResponse {
        void processFinish(List<JSONObject> brgyList, List<String> brgyNames);
    }

}


