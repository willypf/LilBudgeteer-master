package com.krisoflies.lilbudgeteer;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.krisoflies.lilbudgeteer.controller.EconCounselManager;
import com.krisoflies.lilbudgeteer.model.AdapterEconCounsel;
import com.krisoflies.lilbudgeteer.model.JSONEconCounsel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class EconomicalAdviceActivity extends ListActivity {
    ArrayList<JSONEconCounsel> listItems = new ArrayList<>();//DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<JSONEconCounsel> adbEconCounsel;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_economical_advice);
        //getActionBar().setTitle("Economical Advice");
        act = this;
        new HttpAsyncTask(EconomicalAdviceActivity.this).execute("https://www.kimonolabs.com/api/5pbdszrg?apikey=N5CkQzdEIGXSh4tm2lIRQOmED2NTQpvK");
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;
        private ListActivity activity;

        public HttpAsyncTask(ListActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Progress start");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                Toast.makeText(getBaseContext(), "New!", Toast.LENGTH_LONG).show();
            }
            listItems = EconCounselManager.setCounsels(result);
            adbEconCounsel = new AdapterEconCounsel(act, 0, listItems);
            setListAdapter(adbEconCounsel);
            adbEconCounsel.notifyDataSetChanged();

            if (listItems.size() > 0) {
                Toast.makeText(activity, "OK", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "An error has ocurred. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static String GET(String url) {
        InputStream inputStream;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();// create HttpClient
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));// make GET request to the given URL
            inputStream = httpResponse.getEntity().getContent();// receive response as inputStream
            if (inputStream != null)// convert inputstream to string
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";//AQUI DEBO USAR LOS STRINGS PARA POBLAR LO QUE NECESITO POBLAR AL MODO DE LO QUE HIZO EL PROFE DE KIMONO ANTERIOR
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        String line;
        String result = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null)result += line;
            inputStream.close();
        } catch (Exception e) {
            Log.d("convertInputStreamToString",e.getLocalizedMessage());
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_economical_advice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}