package com.krisoflies.lilbudgeteer;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.krisoflies.lilbudgeteer.controller.ApplicationUseManager;
import com.krisoflies.lilbudgeteer.controller.TransactionManager;

import java.io.IOException;
import java.util.StringTokenizer;

public class EraseTransactionActivity extends ActionBarActivity {
    private String path;
    private String[] arrTrans;
    private Object thisActivity;
    private int trueLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erase_transaction);
        path = getFilesDir().getAbsolutePath();
        thisActivity = this;
        getSupportActionBar().setTitle("Erase Transaction");
        ((RadioButton) findViewById(R.id.rdB1)).setChecked(true);
        setTransactGroupListener();
        setTransactionSpinValues();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_erase_transaction, menu);
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

    private void setTransactionSpinValues() {
        try {
            arrTrans = TransactionManager.obtainLast20(path, this);
            for (int i = 0; i < arrTrans.length; i++)
                if (arrTrans[i] == null) break;
                else trueLength++;
            String[] arrOne = new String[1];
            arrOne[0] = arrTrans[trueLength - 1];//la ultima transaccion realizada
            Spinner transaction = ((Spinner) findViewById(R.id.spnTransaction));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrOne);
            transaction.setAdapter(adapter);
        } catch (IOException e) {
            ApplicationUseManager.sendAlert("Could not load transactions: " + e.getMessage(), "Error", this);
        }
    }

    public void setTransactGroupListener() {
        RadioGroup rgTransact = (RadioGroup) findViewById(R.id.rgTransactNumber);
        rgTransact.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {// checkedId is the RadioButton selected
                RadioButton rb = (RadioButton) findViewById(checkedId);
                String strChoice = rb.getText().toString();
                String[] arrTransShow = new String[1];
                if (strChoice.equals("1")) {
                    arrTransShow = new String[1];
                    arrTransShow[0] = arrTrans[trueLength - 1];
                } else if (strChoice.equals("10"))
                    if (trueLength < 10) arrTransShow = new String[trueLength];
                    else if (strChoice.equals("20"))
                        if (trueLength < 20) arrTransShow = new String[trueLength];

                if (!strChoice.equals("1"))
                    for (int i = 0, j = arrTransShow.length - 1; i < trueLength; i++, j--)
                        arrTransShow[j] = arrTrans[i];

                Spinner transaction = ((Spinner) findViewById(R.id.spnTransaction));
                ArrayAdapter adapter = new ArrayAdapter<>((Context) thisActivity, android.R.layout.simple_spinner_item, arrTransShow);
                transaction.setAdapter(adapter);
            }
        });
    }

    public void eraseTransaction(View view) {
        Spinner transaction = ((Spinner) findViewById(R.id.spnTransaction));
        String selectedItem = transaction.getSelectedItem().toString();//este sera la transaccion a borrar seleccionada
        StringTokenizer strTok = new StringTokenizer(selectedItem, "|");
        int number = Integer.parseInt(strTok.nextToken());

        if (TransactionManager.correctPassword(path, this)) {
            TransactionManager.eraseTransaction(path, number, arrTrans.length, this);
            ApplicationUseManager.sendAlert("Transaction erased.", "Info", this);
        } else {
            ApplicationUseManager.sendAlert("Wrong password. Try again.", "Info", this);
        }
        ((EditText) findViewById(R.id.edtPassword)).setText("");
    }
}