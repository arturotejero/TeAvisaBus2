package com.chache.teavisabus2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner cmbLineas;
    Spinner cmbDestino;

    // URL to get contacts JSON
    private static String url = "https://raw.githubusercontent.com/mobilesiri/JSON-Parsing-in-Android/master/index.html";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.cmbLineas = (Spinner) findViewById(R.id.spinnerLinea);
        this.cmbDestino = (Spinner) findViewById(R.id.spinnerDestino);

        loadSpinnerLinea();
    }

    /* Cargamos las líneas en un array*/
    private void loadSpinnerLinea() {

        // Create an ArrayAdapter using the string array and a default spinner
        // layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.lineas, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        this.cmbLineas.setAdapter(adapter);

        // This activity implements the AdapterView.OnItemSelectedListener
        this.cmbLineas.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // Calling async task to get json
        new GetStudents().execute();

        Toast toast = Toast.makeText(this, "onItemSelected", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Toast toast = Toast.makeText(this, "onNothingSelected", Toast.LENGTH_LONG);
        toast.show();
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);

                // Getting JSON Array node
                JSONArray students = jsonObj.getJSONArray(TAG_STUDENT_INFO);

                // looping through All Students
                for (int i = 0; i < students.length(); i++) {
                    JSONObject c = students.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String sname = c.getString(TAG_STUDENT_NAME);
                    String email = c.getString(TAG_EMAIL);
                    String address = c.getString(TAG_ADDRESS);
                    String gender = c.getString(TAG_STUDENT_GENDER);

                    // Phone node is JSON Object
                    JSONObject phone = c.getJSONObject(TAG_STUDENT_PHONE);
                    String mobile = phone.getString(TAG_STUDENT_PHONE_MOBILE);
                    String home = phone.getString(TAG_STUDENT_PHONE_HOME);

                    // tmp hashmap for single student
                    HashMap<String, String> student = new HashMap<String, String>();

                    // adding every child node to HashMap key => value
                    student.put(TAG_ID, id);
                    student.put(TAG_STUDENT_NAME, sname);
                    student.put(TAG_EMAIL, email);
                    student.put(TAG_STUDENT_PHONE_MOBILE, mobile);

                    // adding student to students list
                    studentList.add(student);
                }
                return studentList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    private class GetStudents extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> studentList;
        ProgressDialog proDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            proDialog = new ProgressDialog(MainActivity.this);
            proDialog.setMessage("Please wait...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            LlamadaHTTP webreq = new LlamadaHTTP();

            // Making a request to url and getting response
            String jsonStr = webreq.makeWebServiceCall(url, LlamadaHTTP.GETRequest);

            Log.d("Response: ", "> " + jsonStr);

            studentList = ParseJSON(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);

            // Dismiss the progress dialog
            if (proDialog.isShowing())
                proDialog.dismiss();
            /**
             * Updating received data from JSON into ListView
             * */
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(
                    MainActivity.this,android.R.layout.simple_spinner_item, studentList);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            cmbDestino.setAdapter(adapter);

        }
    }
}
