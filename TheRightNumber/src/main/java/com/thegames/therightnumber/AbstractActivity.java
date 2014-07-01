package com.thegames.therightnumber;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.thegames.therightnumber.managers.BillingManager;

/**
 * Created by gyljean-lambert on 20/03/2014.
 */
public abstract class AbstractActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BillingManager.getInstance().initBilling();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BillingManager.getInstance().disposeBilling();
    }
}
