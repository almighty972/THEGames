package com.thegames.therightnumber.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.thegames.therightnumber.R;
import com.thegames.therightnumber.interfaces.NumericPadListener;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 24/05/2014.
 */
public class CustomNumericPad extends RelativeLayout implements View.OnClickListener {

    private Context mContext;
    private WeakReference<NumericPadListener> mWeakNumericPadListener = new WeakReference<NumericPadListener>(null);


    private Button mKeyPad1Button, mKeyPad2Button, mKeyPad3Button, mKeyPad4Button, mKeyPad5Button,
            mKeyPad6Button, mKeyPad7Button, mKeyPad8Button, mKeyPad9Button, mKeyPad0Button, mKeyPadOKButton;

    private RelativeLayout mKeyJokerRangeButton;


    /*
     * GETTERS / SETTERS
     */

    public WeakReference<NumericPadListener> getWeakNumericPadListener() {
        return mWeakNumericPadListener;
    }

    public void setWeakNumericPadListener(final NumericPadListener weakNumericPadListener) {
        if(weakNumericPadListener != null) {
            this.mWeakNumericPadListener = new WeakReference<NumericPadListener>(weakNumericPadListener);
        }
    }

    /* ************************* */



    public CustomNumericPad(Context context) {
        super(context);
        initView(context);
    }

    public CustomNumericPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomNumericPad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View view = inflate(context, R.layout.layout_numeric_pad, null);
        addView(view);

        mKeyPad1Button = (Button) view.findViewById(R.id.key1Button);
        mKeyPad2Button = (Button) view.findViewById(R.id.key2Button);
        mKeyPad3Button = (Button) view.findViewById(R.id.key3Button);
        mKeyPad4Button = (Button) view.findViewById(R.id.key4Button);
        mKeyPad5Button = (Button) view.findViewById(R.id.key5Button);
        mKeyPad6Button = (Button) view.findViewById(R.id.key6Button);
        mKeyPad7Button = (Button) view.findViewById(R.id.key7Button);
        mKeyPad8Button = (Button) view.findViewById(R.id.key8Button);
        mKeyPad9Button = (Button) view.findViewById(R.id.key9Button);
        mKeyPad0Button = (Button) view.findViewById(R.id.key0Button);
        mKeyPadOKButton = (Button) view.findViewById(R.id.keyOkButton);
        mKeyJokerRangeButton = (RelativeLayout) view.findViewById(R.id.keyJokerButton);

        setListeners();
    }

    private void setListeners() {
        mKeyPad1Button.setOnClickListener(this);
        mKeyPad2Button.setOnClickListener(this);
        mKeyPad3Button.setOnClickListener(this);
        mKeyPad4Button.setOnClickListener(this);
        mKeyPad5Button.setOnClickListener(this);
        mKeyPad6Button.setOnClickListener(this);
        mKeyPad7Button.setOnClickListener(this);
        mKeyPad8Button.setOnClickListener(this);
        mKeyPad9Button.setOnClickListener(this);
        mKeyPad0Button.setOnClickListener(this);
        mKeyPadOKButton.setOnClickListener(this);
        mKeyJokerRangeButton.setOnClickListener(this);
    }

    public void disableJokerKey() {
        mKeyJokerRangeButton.setEnabled(false);
    }

    public void enableJokerKey() {
        mKeyJokerRangeButton.setEnabled(true);
    }

    private void fireClickEvent(int keyNum) {
        if(mWeakNumericPadListener != null && mWeakNumericPadListener.get() != null) {
            mWeakNumericPadListener.get().onPressNumericKey(keyNum);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == mKeyPadOKButton) {
            if(mWeakNumericPadListener != null && mWeakNumericPadListener.get() != null) {
                mWeakNumericPadListener.get().onPressOk();
            }
        } else if (v == mKeyJokerRangeButton) {
            if(mWeakNumericPadListener != null && mWeakNumericPadListener.get() != null) {
                mWeakNumericPadListener.get().onPressJokerRangeKey();
            }
        } else {
            if(mKeyPad1Button == v) { fireClickEvent(1); }
            if(mKeyPad2Button == v) { fireClickEvent(2); }
            if(mKeyPad3Button == v) { fireClickEvent(3); }
            if(mKeyPad4Button == v) { fireClickEvent(4); }
            if(mKeyPad5Button == v) { fireClickEvent(5); }
            if(mKeyPad6Button == v) { fireClickEvent(6); }
            if(mKeyPad7Button == v) { fireClickEvent(7); }
            if(mKeyPad8Button == v) { fireClickEvent(8); }
            if(mKeyPad9Button == v) { fireClickEvent(9); }
            if(mKeyPad0Button == v) { fireClickEvent(0); }
        }
    }
}
