package com.krisoflies.lilbudgeteer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.krisoflies.lilbudgeteer.controller.ApplicationUseManager;
import com.krisoflies.lilbudgeteer.controller.TransactionManager;

import java.util.List;


public class ConfigurationActivity extends ActionBarActivity {

    private String path;
    private Activity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        //getActionBar().setTitle("Configuration");
        getSupportActionBar().setTitle("Configuration");
        path = getFilesDir().getAbsolutePath();
        addCheckboxListeners();//Adherimos listener a cada uno de los checkbox
        chargeConfigChoices();//RELLENAMOS LA CONFIGURACION
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();//noinspection SimplifiableIfStatement
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private void chargeConfigChoices() {
        int[] cfgChoice = TransactionManager.obtainCurrentLine(path, this);
        List<String> key = ApplicationUseManager.readFile(path + "/k.add");
        //List<String> DBkey = ApplicationUseManager.readFile(path + "/gd.add");
        if (cfgChoice[1] == 1)//la opcion de poder usar observaciones
            ((CheckBox) findViewById(R.id.ckbObservations)).setChecked(true);
        if (cfgChoice[5] == 1)//la opcion de aviso de gasto excesivo
            ((CheckBox) findViewById(R.id.ckbBorLimAl)).setChecked(true);
        if (cfgChoice[6] == 1)//el monto inicial elegido
            ((EditText) findViewById(R.id.edtIniAmount)).setText(cfgChoice[6]);
        if (!key.get(0).equals("0"))//la opcion de poder usar password para lo demas
            ((CheckBox) findViewById(R.id.ckbPassword)).setChecked(true);
    }

    private void addCheckboxListeners() {
        addListenerOnAlarmOn();
        addListenerOnObsPermitted();
        addListenerOnPasswordAllowed();
    }

    public void setInitialAmount(View view) {//este metodo setea el monto inicial de la aplicacion
        //Solo es util como monto referencial inicial. Es decir para que no te active la alarma de
        //endeudamiento desde el inicio.
        String strIniAmount = ((EditText) findViewById(R.id.edtIniAmount)).getText().toString();
        double iniAmount = Double.parseDouble(strIniAmount);
        int[] strRF = TransactionManager.obtainCurrentLine(path, this);
        strRF[6] = (int) iniAmount;

        TransactionManager.updateCurrentLine(path, strRF, this);
        ApplicationUseManager.sendAlert("Initial amount saved", "Info", this);
    }

    public void resetInitialAmount(View view) {//este metodo resetea el monto
        ((EditText) findViewById(R.id.edtIniAmount)).setText("0");
        int[] strRF = TransactionManager.obtainCurrentLine(path, this);
        strRF[6] = 0;

        TransactionManager.updateCurrentLine(path, strRF, this);
        ApplicationUseManager.sendAlert("Initial amount reset", "Info", this);
    }

    private void addListenerOnAlarmOn() {
        CheckBox ckb = (CheckBox) findViewById(R.id.ckbBorLimAl);
        ckb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//is chkIos checked?
                int[] strRF = TransactionManager.obtainCurrentLine(path, thisActivity);
                if (((CheckBox) v).isChecked()) {//el usuario ha checkeado el item
                    strRF[5] = 1;
                } else strRF[5] = 0;
                TransactionManager.updateCurrentLine(path, strRF, thisActivity);
            }
        });
    }

    public void addListenerOnObsPermitted() {
        CheckBox ckb = (CheckBox) findViewById(R.id.ckbObservations);
        ckb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//is chkIos checked?
                int[] strRF = TransactionManager.obtainCurrentLine(path, thisActivity);
                if (((CheckBox) v).isChecked()) {//el usuario ha checkeado el item
                    strRF[1] = 1;
                } else strRF[1] = 0;
                TransactionManager.updateCurrentLine(path, strRF, thisActivity);
            }
        });
    }

    public void addListenerOnPasswordAllowed() {
        CheckBox ckb = (CheckBox) findViewById(R.id.ckbPassword);
        ckb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    Intent intent;
                    intent = new Intent(thisActivity, PasswordsActivity.class);
                    intent.putExtra("Intention", 1);
                    startActivity(intent);
                } else {
                    String update = "";
                    List<String> strRF = ApplicationUseManager.readFile(path + "/k.add");
                    strRF.set(0, "" + 0);
                    for (int i = 0; i < strRF.size() - 1; i++) update += strRF.get(i) + ",";
                    update += strRF.get(strRF.size() - 1);
                    ApplicationUseManager.updateFile(path + "/k.add", update, thisActivity);
                    ApplicationUseManager.sendAlert("Password deactivated.", "Info", thisActivity);
                }
            }
        });
    }
}