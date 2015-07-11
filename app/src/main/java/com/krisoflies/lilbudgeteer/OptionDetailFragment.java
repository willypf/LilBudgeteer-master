package com.krisoflies.lilbudgeteer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.krisoflies.lilbudgeteer.model.MainListContent;

/**
 * A fragment representing a single Option detail screen.
 * This fragment is either contained in a {@link OptionListActivity}
 * in two-pane mode (on tablets) or a {@link OptionDetailActivity}
 * on handsets.
 */
public class OptionDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private MainListContent.MainItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OptionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {//AQUI SE ENTRA LUEGO DE ENTRAR AL OPTION DETAIL
        super.onCreate(savedInstanceState);          //ACTIVITY EL FRAGMENTO CARGA LOSS DATOS DESCRIPTIVOS

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Aqui se cargan los datos de descripcion principales
            mItem = MainListContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_option_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.option_detail)).setText(mItem.getContent());
            ((TextView) getActivity().findViewById(R.id.txtDescription)).setText(mItem.getDescription());
        }

        return rootView;
    }
}
