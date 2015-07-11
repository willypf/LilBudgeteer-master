package com.krisoflies.lilbudgeteer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.krisoflies.lilbudgeteer.controller.ApplicationUseManager;

import java.io.File;

/*
 * An activity representing a list of Options. This activity has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched, lead to a {@link OptionDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a {@link OptionListFragment} and the item details
 * (if present) is a {@link OptionDetailFragment}.
 * <p/>
 * This activity also implements the required {@link OptionListFragment.Callbacks} interface to listen for item selections.
 */
public class OptionListActivity extends FragmentActivity
        implements OptionListFragment.Callbacks {
    /* Whether or not the activity is in two-pane mode, i.e. running on a tablet device.*/
    private boolean mTwoPane;

    @Override //PRIMERO EN ACTIVARSE
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_list);
        //If first use, creates the saving file
        File f = new File(getFilesDir().getAbsolutePath() + "/cfg.add");
        if (!(f.exists() && !f.isDirectory())) {
            try {
                ApplicationUseManager.initializeConfig(getFilesDir().getAbsolutePath(), this);
            } catch (Exception e) {
                ApplicationUseManager.sendAlert(e.getMessage(), "An initilization error has ocurred.", this);
            }
        }

        if (findViewById(R.id.option_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((OptionListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.option_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /* Callback method from {@link OptionListFragment.Callbacks} indicating that the item with the given ID was selected.*/
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(OptionDetailFragment.ARG_ITEM_ID, id);
            OptionDetailFragment fragment = new OptionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.option_detail_container, fragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity for the selected item ID.
            Intent detailIntent = new Intent(this, OptionDetailActivity.class);
            detailIntent.putExtra(OptionDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
