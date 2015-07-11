package com.krisoflies.lilbudgeteer.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.krisoflies.lilbudgeteer.R;

import android.view.View.OnClickListener;

import java.util.ArrayList;

/* Created by Paolo on 6/7/2015.*/
public class AdapterEconCounsel extends ArrayAdapter<JSONEconCounsel> {
    private Activity act;
    private ArrayList<JSONEconCounsel> lEconCounsel;
    private static LayoutInflater inflater = null;

    public AdapterEconCounsel(Activity activity, int textViewResourceId, ArrayList<JSONEconCounsel> _lEconCounsel) {
        super(activity, textViewResourceId, _lEconCounsel);
        try {
            this.act = activity;
            this.lEconCounsel = _lEconCounsel;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            Log.d("AdapterEconCounsel",e.getLocalizedMessage());
        }
    }

    public int getCount() {
        return lEconCounsel.size();
    }



    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_name;
        public Button send_hRef;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.econ_view_row, null);
                holder = new ViewHolder();

                holder.display_name = (TextView) vi.findViewById(R.id.labelEconRow);
                holder.send_hRef = (Button) vi.findViewById(R.id.btnEconRow);
                OnClickListener buttonListener = new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String url = (String) v.getContentDescription();
                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                        v.getContext().startActivity(viewIntent);
                    }
                };
                holder.send_hRef.setOnClickListener(buttonListener);
                holder.send_hRef.setTag(position);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            holder.display_name.setText(lEconCounsel.get(position).getTitle());
            holder.send_hRef.setContentDescription(lEconCounsel.get(position).getHref());
        } catch (Exception e) {
            Log.d("getView",e.getLocalizedMessage());
        }
        return vi;
    }
}
