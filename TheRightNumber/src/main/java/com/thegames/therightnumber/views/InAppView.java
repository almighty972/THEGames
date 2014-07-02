package com.thegames.therightnumber.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thegames.therightnumber.R;
import com.thegames.therightnumber.interfaces.InAppViewListener;
import com.thegames.therightnumber.model.InApp;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 08/06/2014.
 */
public class InAppView extends RelativeLayout implements View.OnClickListener {

    private Context mContext;
    private InApp mInApp;

    private TextView mInappLifeCountTextView, mInappCostTextView, mInAppSmallDescTextTview;
    private ImageView mInappImageView;
    private LinearLayout mInappRootLayout;

    private WeakReference<InAppViewListener> mInAppViewListenerWeakReference = new WeakReference<InAppViewListener>(null);

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

    public void setInAppListener(final InAppViewListener listener) {
        if(listener != null) {
            mInAppViewListenerWeakReference = new WeakReference<InAppViewListener>(listener);
        }
    }

    private void initView(Context context) {
        mContext = context;
        View view = inflate(context, R.layout.layout_inapp, null);
        addView(view);

        mInappRootLayout = (LinearLayout) view.findViewById(R.id.inappRootLayout);
        mInappRootLayout.setOnClickListener(this);
        mInappLifeCountTextView = (TextView) view.findViewById(R.id.inappLifeCountTextView);
        mInappRootLayout.setOnClickListener(this);
        mInappCostTextView = (TextView) view.findViewById(R.id.inappCostTextView);
        mInappRootLayout.setOnClickListener(this);
        mInAppSmallDescTextTview = (TextView) view.findViewById(R.id.inAppSmallDescTextTview);
        mInappRootLayout.setOnClickListener(this);
        mInappImageView = (ImageView) view.findViewById(R.id.inappImageView);
        mInappRootLayout.setOnClickListener(this);

        if(mInApp != null) {
            mInappLifeCountTextView.setText(String.valueOf(mInApp.getName()));
            mInappCostTextView.setText(String.valueOf(mInApp.getCost()));
            mInAppSmallDescTextTview.setText(mInApp.getBelowText());
        }
    }

    @Override
    public void onClick(View v) {
        if(mInAppViewListenerWeakReference != null &&
                mInAppViewListenerWeakReference.get() != null) {
            mInAppViewListenerWeakReference.get().onSelectInApp(mInApp.getSku());
        }
    }
}
