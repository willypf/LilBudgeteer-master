package com.krisoflies.lilbudgeteer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.krisoflies.lilbudgeteer.controller.ApplicationUseManager;
import java.util.List;

public class PasswordsActivity extends ActionBarActivity {

    private int savingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) savingType = extras.getInt("Intention");

        setContentView(R.layout.activity_passwords);
        if (savingType == 1) {//se habre la ventana de password para transacciones
            (findViewById(R.id.edtUserDen)).setVisibility(View.GONE);
            (findViewById(R.id.txtUserDen)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txtUserKey)).setText("Password");
        }

        getSupportActionBar().setTitle("Password Manager");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_passwords, menu);
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

    public void saveKeys() {
        if (savingType == -1)
            ApplicationUseManager.sendAlert("Intent not transfering pass save type", "Error", this);
        else {
            if (savingType == 1) {//estamos hablando de la contrasenha de usuario
                String password = ((EditText) findViewById(R.id.edtUserKey)).getText().toString();
                String update = "";
                List<String> strRF = ApplicationUseManager.readFile(getFilesDir().getAbsolutePath() + "/k.add");
                strRF.set(0, "" + 1);
                strRF.set(1, password);
                for (int i = 0; i < strRF.size() - 1; i++) update += strRF.get(i) + ",";
                update += strRF.get(strRF.size() - 1);
                ApplicationUseManager.updateFile(getFilesDir().getAbsolutePath() + "/k.add", update, this);
                ApplicationUseManager.sendAlert("Password activated.", "Info", this);
            } else {//estamos hablando de la contrasenha de GDRIVE
                String gmailAcc = ((EditText) findViewById(R.id.edtUserDen)).getText().toString();
                String password = ((EditText) findViewById(R.id.edtUserKey)).getText().toString();
                String update = "";
                List<String> strRF = ApplicationUseManager.readFile(getFilesDir().getAbsolutePath() + "/gd.add");
                strRF.set(0, gmailAcc);
                strRF.set(1, password);
                for (int i = 0; i < strRF.size() - 1; i++) update += strRF.get(i) + ",";
                update += strRF.get(strRF.size() - 1);
                ApplicationUseManager.updateFile(getFilesDir().getAbsolutePath() + "/gd.add", update, this);
                ApplicationUseManager.sendAlert("Dropbox account saved.", "Info", this);
            }
        }
    }
}
