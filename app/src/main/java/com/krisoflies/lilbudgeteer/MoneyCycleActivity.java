package com.krisoflies.lilbudgeteer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;


public class MoneyCycleActivity extends ActionBarActivity {

    private String path;
    private Calendar c = Calendar.getInstance();
    private int startYear = c.get(Calendar.YEAR);
    private int startMonth = c.get(Calendar.MONTH);
    private int startDay = c.get(Calendar.DAY_OF_MONTH);
    private boolean redOnSession = false;

    private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.BLACK, Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.YELLOW, Color.WHITE, Color.RED };
    /** The main series that will include all the data. */
    private CategorySeries mSeries = new CategorySeries("");
    /** The main renderer for the main dataset. */
    private DefaultRenderer mRenderer = new DefaultRenderer();
    /** Button for adding entered data to the current series. */
    private Button mAdd;
    /** Edit text field for entering the slice value. */
    private EditText mValue;
    /** The chart view that displays the data. */
    private GraphicalView mChartView;

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        mSeries = (CategorySeries) savedState.getSerializable("current_series");
        mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("current_series", mSeries);
        outState.putSerializable("current_renderer", mRenderer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart_report);
        mValue = (EditText) findViewById(R.id.xValue);

        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(180);
        mRenderer.setDisplayValues(true);

        mAdd = (Button) findViewById(R.id.add);
        mAdd.setEnabled(true);
        mValue.setEnabled(true);

        mAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double value = 0;
                try {
                    value = 2.2;
                } catch (NumberFormatException e) {
                    Log.d("Failure", e.getLocalizedMessage());
                    return;
                }
                mValue.setText("");
                mValue.requestFocus();
                mSeries.add("Series " + (mSeries.getItemCount() + 1), value);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
                mRenderer.addSeriesRenderer(renderer);
                mChartView.repaint();
            }
        });
    }



    /*private GraphicalView mChartView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_cycle);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (mChartView == null) {
            //LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                        Toast.makeText(MoneyCycleActivity.this, "No chart element selected", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        for (int i = 0; i < mSeries.getItemCount(); i++) {
                            mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
                        }
                        mChartView.repaint();
                        Toast.makeText(
                                MoneyCycleActivity.this,
                                "Chart data point index " + seriesSelection.getPointIndex() + " selected"
                                        + " point value=" + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,                    LayoutParams.FILL_PARENT));
        } else {
            mChartView.repaint();
        }
    }

//pruebas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_money_cycle, menu);
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
            return new DatePickerDialog(MoneyCycleActivity.this, this, startYear, startMonth, startDay);
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
