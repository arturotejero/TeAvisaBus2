package com.chache.teavisabus2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Async task class to get json by making HTTP call
 */
public class LlamadaWebService extends AsyncTask<Void, Void, Void> {

    // URL to get contacts JSON
    private static String url = "http://wsteavisabus-teavisabus.rhcloud.com/WSTeAvisaBus/crunchify/emtservice/1835";
    // JSON Node names
    private static final String TAG_STUDENT_INFO = "studentsinfo";
    private static final String TAG_ID = "id";
    private static final String TAG_STUDENT_NAME = "sname";
    private static final String TAG_EMAIL = "semail";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_STUDENT_GENDER = "gender";
    private static final String TAG_STUDENT_PHONE = "sphone";
    private static final String TAG_STUDENT_PHONE_MOBILE = "mobile";
    private static final String TAG_STUDENT_PHONE_HOME = "home";
        ProgressDialog proDialog;
    Spinner cmbLineas;
    Spinner cmbDestino;
    Activity activity = null;

    public LlamadaWebService(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity mActivity = (MainActivity) activity;
        // Showing progress loading dialog
        proDialog = new ProgressDialog(mActivity);
        proDialog.setMessage("Por favor espere...");
        proDialog.setCancelable(false);
        proDialog.show();
        }

    @Override
    protected Void doInBackground(Void... arg0) {

        ArrayList<JsonBean> listBean = new ArrayList<>();
        ArrayList<String> lineas = new ArrayList<>();

        try {
            String readFeed = readFeed();
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                JSONObject json = new JSONObject(readFeed);
                JSONArray jsonArray = new JSONArray(json.optString("Line"));
                Log.i(MainActivity.class.getName(),
                        "Number of entries " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    JsonBean bean = new JsonBean();
                    bean.setName(jsonObject.optString("Name"));
                    listBean.add(bean);
                    lineas.add(jsonObject.optString("Line"));
                }
            }else {

                Toast avisoConexion = Toast.makeText(getApplicationContext(),
                        "No hay conexión a Internet", Toast.LENGTH_SHORT);
                avisoConexion.show();
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        this.cmbLineas = (Spinner) findViewById(R.id.spinnerLinea);
        this.cmbDestino = (Spinner) findViewById(R.id.spinnerDestino);

        cmbLineas.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lineas));

//        loadSpinnerLinea();
        return null;
    }

    public String readFeed() throws IOException
    {
        URL webService = new URL (url);
        HttpURLConnection urlConnection = (HttpURLConnection) webService.openConnection();
        String line;
        String response = "";

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader br = new BufferedReader (new InputStreamReader(in));

            while ((line = br.readLine()) != null) {
                response += line;
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }
        return response;
    }
}
