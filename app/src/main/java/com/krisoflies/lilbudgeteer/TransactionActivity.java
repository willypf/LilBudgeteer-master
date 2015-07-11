package com.krisoflies.lilbudgeteer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.krisoflies.lilbudgeteer.controller.ApplicationUseManager;
import com.krisoflies.lilbudgeteer.controller.TransactionManager;
import com.krisoflies.lilbudgeteer.model.Transaction;

import java.util.Calendar;

public class TransactionActivity extends ActionBarActivity {

    private String path;
    private Calendar c = Calendar.getInstance();
    private int startYear = c.get(Calendar.YEAR);
    private int startMonth = c.get(Calendar.MONTH);
    private int startDay = c.get(Calendar.DAY_OF_MONTH);
    private boolean redOnSession = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        path = getFilesDir().getAbsolutePath();
        chargeConfigChoices();
        getSupportActionBar().setTitle("Transaction");
        //Inicializacion del dropbox
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargeConfigChoices();
    }

    private void chargeConfigChoices() {//usado para el setteo de los datos del spinner y de las observaciones
        int[] cfgChoice = TransactionManager.obtainCurrentLine(path, this);

        if (cfgChoice[1] == 0) {//permitir observaciones= si(1)|no(0)
            findViewById(R.id.edtObservation).setVisibility(View.GONE);
            findViewById(R.id.txtObservations).setVisibility(View.GONE);
        }

        if (cfgChoice[5] == 1) {//aviso de gasto excesivo (APARICION DE PANTALLA EN ROJO)
            if (cfgChoice[6] <= (cfgChoice[3] - cfgChoice[2])) {//se infrapaso el limite, pantalla roja
                int sdk = android.os.Build.VERSION.SDK_INT;
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.initialLay);

                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
                    layout.setBackgroundColor(Color.RED);
                else layout.setBackgroundColor(Color.RED);
                redOnSession = true;
                ApplicationUseManager.sendAlert("You have go below your minimun box. Watch over your money!", "Warning", this);
            }else {
                if(redOnSession){
                    Intent intent = new Intent(this, TransactionActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transactions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will  automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveTransaction(View view) {
        try {
            String strDate = "" + startDay + "/" + startMonth + "/" + startYear;
            String strQuantity = ((EditText) findViewById(R.id.edtQuantity)).getText().toString();
            if (TransactionManager.dataCorrectness(strDate, strQuantity, this)) {
                Transaction tr = new Transaction();

                boolean checked = ((RadioButton) findViewById(R.id.rdBIncome)).isChecked();
                if (checked)
                    tr.setType('i');//aqui se hace el seteo de si la transaccion es un ingreso o egreso.
                else tr.setType('e');

                String strCategory = ((Spinner) findViewById(R.id.spnThemes)).getSelectedItem().toString();
                String strObservations = ((EditText) findViewById(R.id.edtObservation)).getText().toString();

                tr.setCategory(strCategory);
                tr.setAmount(Double.parseDouble(strQuantity));
                tr.setDate(strDate);
                tr.setObservations(strObservations);

                if (TransactionManager.correctPassword(path, this) && TransactionManager.saveTransaction(path, tr, this)) {
                    ApplicationUseManager.sendAlert("Transaction Saved", "Informing", this);
                    clearTransaction();
                    chargeConfigChoices();
                }
            }
        } catch (Exception e) {
            Log.d("saveTransactionView", e.getLocalizedMessage());
        }
    }

    private void clearTransaction() {//ni el spinner ni el radio button deberian ser recargados
        ((EditText) findViewById(R.id.edtQuantity)).setText("");
        ((EditText) findViewById(R.id.edtObservation)).setText("");
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH);
        startDay = c.get(Calendar.DAY_OF_MONTH);
    }

    //El control del datetime picker entero, hacia abajo esta su implementacion y su logica de llamada desde boton
    @SuppressLint("ValidFragment")
    public class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(TransactionActivity.this, this, startYear, startMonth, startDay);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startYear = year;
            startMonth = monthOfYear;
            startDay = dayOfMonth;
        }
    }

    public void showTransactionDateDialog(View v) {
        DialogFragment dialogFragment = new StartDatePicker();
        dialogFragment.show(getFragmentManager(), "start_date_picker");
    }
}