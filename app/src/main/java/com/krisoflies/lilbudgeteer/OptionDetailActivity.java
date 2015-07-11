package com.krisoflies.lilbudgeteer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.krisoflies.lilbudgeteer.controller.ApplicationUseManager;
import com.krisoflies.lilbudgeteer.controller.TransactionManager;

public class OptionDetailActivity extends ActionBarActivity //implements DateTimeFragment.OnFragmentInteractionListener{
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {//AQUI SE HA ENTRADO EN LA VENTANA DE DESCRIPCION DE TRANSACCION
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_detail);// Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Actions");
        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape). In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it. For more information, see the Fragments API guide at:
        // http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(OptionDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(OptionDetailFragment.ARG_ITEM_ID));
            OptionDetailFragment fragment = new OptionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.option_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design: http://developer.android.com/design/patterns/navigation.html#up-vs-back
            navigateUpTo(new Intent(this, OptionListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setContentView(View view) {//el control del flujo de la aplicacion, txtDescription y el boton son lo unico accesible desde el mando central
        CharSequence strChoice = ((TextView) findViewById(R.id.txtDescription)).getText();

        if (strChoice.equals("Save your daily transactions (loss or gains), for tracking how you expend your money."))//Abrir ventana de transacciones
        {//MODO INCORRECTO!!!!! --> //setContentView(R.layout.activity_transactions);
            Intent intent = new Intent(this, TransactionActivity.class);
            startActivity(intent);
        } else if (strChoice.equals("The parameters of the application are managed here. Passwords, warnings and other configurations are managed here.")) {//Abrir ventana de configuracion
            Intent intent = new Intent(this, ConfigurationActivity.class);
            startActivity(intent);
        } else if (strChoice.equals("Generates the balance register up to today. Produces" +
                " an Excel file1.")) {//Abrir ventana de reporte
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        } else if (strChoice.equals("Erases the transactions up to the last 100. Must be erased one by one and with the use of a password")) {//Abrir ventana de reporte
            int [] currentLine =TransactionManager.obtainCurrentLine(getFilesDir().getAbsolutePath(), this);
            if(currentLine[0]>0) {
                Intent intent = new Intent(this, EraseTransactionActivity.class);
                startActivity(intent);
            }else ApplicationUseManager.sendAlert("There are no transactions.","Error",this);
        } else if (strChoice.equals("View economical advices of today, chances are it might be helpful.")) {//Abrir ventana de reporte
            Intent intent = new Intent(this, EconomicalAdviceActivity.class);
            startActivity(intent);
        } else if (strChoice.equals("View statistic by theme, and overall economical life.")) {//Abrir ventana de reporte
            Intent intent = new Intent(this, PieChartReportActivity.class);
            startActivity(intent);
        } else if (strChoice.equals("Study by a range of dates your economical life. Better knowledge, better decisions. Make a safe bet.")) {//Abrir ventana de reporte
            Intent intent = new Intent(this, MoneyCycleActivity.class);
            startActivity(intent);
        }

    }
}