package com.thegames.therightnumber.popups;


import android.os.Bundle;
import android.view.View;

import com.thegames.therightnumber.R;
import com.thegames.therightnumber.uigame.PopupAction;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class OutchPopup extends Popup {

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static OutchPopup newInstance(String title, String body) {
        OutchPopup f = new OutchPopup();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("body", body);

        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = getArguments().getString("title");
        mBody = getArguments().getString("body");

        mPositiveAction = PopupAction.OK;
        mNegativeAction = PopupAction.FRIEND_HELP;

        mPopupReference = new WeakReference<Popup>(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPositiveButton.setText(getString(R.string.ok));
        mNegativeButton.setText(getString(R.string.get_help));
    }
}
