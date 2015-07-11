package com.krisoflies.lilbudgeteer;

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


public class PieChartReportActivity extends ActionBarActivity {
    /** Colors to be used for the pie slices. */
    //como son 11 categorias se necesitan 11 colores para identificar cada una.
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
                    Log.d("Failure",e.getLocalizedMessage());
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
                        Toast.makeText(PieChartReportActivity.this, "No chart element selected", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        for (int i = 0; i < mSeries.getItemCount(); i++) {
                            mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
                        }
                        mChartView.repaint();
                        Toast.makeText(
                                PieChartReportActivity.this,
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
}