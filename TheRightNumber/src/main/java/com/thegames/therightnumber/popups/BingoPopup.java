package com.thegames.therightnumber.popups;


import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;

import com.thegames.therightnumber.R;
import com.thegames.therightnumber.uigame.PopupAction;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class BingoPopup extends Popup {

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static BingoPopup newInstance(String title, String body) {
        BingoPopup f = new BingoPopup();

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

        mShareAction = PopupAction.SHARE;
        mContinueAction = PopupAction.CONTINUE;

        setCancelable(false);

        mPopupReference = new WeakReference<Popup>(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitleTextView.setTextColor(getResources().getColor(R.color.green02));
        mTitleTextView.setTextSize(23.f);

        Spanned spannedBody = Html.fromHtml(mBody);
        mBodyTextView.setText(spannedBody);

        mBodyTextView.setGravity(Gravity.CENTER);
        mShareButtonsLayout.setVisibility(View.VISIBLE);
        mBottomButtonsLayout.setVisibility(View.GONE);
    }
}
