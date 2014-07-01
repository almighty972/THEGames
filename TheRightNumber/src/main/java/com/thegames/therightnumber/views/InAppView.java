package com.thegames.therightnumber.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thegames.therightnumber.R;
import com.thegames.therightnumber.model.InApp;

/**
 * Created by gyljean-lambert on 08/06/2014.
 */
public class InAppView extends RelativeLayout {

    private Context mContext;
    private InApp mInApp;

    private TextView mInappLifeCountTextView, mInappCostTextView;
    private ImageView mInappImageView;
    private LinearLayout mInappRootLayout;

    public InAppView(Context context, InApp inApp) {
        super(context);
        mInApp = inApp;
        initView(context);
    }

    public InAppView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public InAppView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View view = inflate(context, R.layout.layout_inapp, null);
        addView(view);

        mInappRootLayout = (LinearLayout) view.findViewById(R.id.inappRootLayout);
        mInappLifeCountTextView = (TextView) view.findViewById(R.id.inappLifeCountTextView);
        mInappCostTextView = (TextView) view.findViewById(R.id.inappCostTextView);
        mInappImageView = (ImageView) view.findViewById(R.id.inappImageView);

        if(mInApp != null) {
            mInappLifeCountTextView.setText(String.valueOf(mInApp.getName()));
            mInappCostTextView.setText(String.valueOf(mInApp.getCost()));
        }
    }
}
