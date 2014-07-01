package com.thegames.therightnumber;

import com.orm.SugarApp;
import com.thegames.therightnumber.managers.BillingManager;
import com.thegames.therightnumber.managers.GameManager;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class TheRightNumberApp extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        initDatas();
    }

    private void initDatas() {
        BillingManager.getInstance().setAppContext(this);
        GameManager.getInstance().setAppContext(this);
        GameManager.getInstance().populateDatabase();
    }
}
