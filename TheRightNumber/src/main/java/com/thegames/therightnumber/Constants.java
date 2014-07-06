package com.thegames.therightnumber;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class Constants {

    public static final String JSON_FILE_NAME = "dataset";
    public static final String JSON_FILE = JSON_FILE_NAME + ".json";
    public static final long SERIALIZATION_UUID_VERSION = 1L;

    /*
     * SHARED PREFERENCES
     */

    public static final String SHARED_PREFERENCES = "prefs.the.right.number";
    public static final String PREFS_SCORE = "prefs.score";
    public static final String PREFS_COUNTDOWN_TIME = "prefs.countdown.time";
    public static final String PREFS_COUNTDOWN_STARTED_TIME = "prefs.countdown.started.time";
    public static final String PREFS_APP_FIRST_LAUNCH = "prefs.app.first.launch";
    public static final String PREFS_USER_IS_PREMIUM = "prefs.user.is.premium";
    public static final String PREFS_USER_PREMIUM_PURCHASE_OBJECT = "prefs.user.premium.purchase.object";

    /*
     * GAMEPLAY
     */

    public static final int WRONG_ANSWER_TRIES_TRIGGER = 2;
    public static final int COUNTDOWN_INIT_TIME =  60000;  //1800000; // 30 minutes in millis
    public static final int LIVES_GIFT = 50; // lives
    public static final int INITIAL_SCORE = 200; // lives
    public static final int JOKER_RANGE_MALUS = 10; // lives
    public static final int FREE_IN_APP_VIDEO_LIVES_COUNT = 25; // lives
    public static final String SKU_COUNTDOWN = "countdown";

    /**
     * BILLING
     */

    public static final String SKU_PACK_200_VIES = "200_vies";
    public static final String SKU_PACK_1500_VIES = "pack1500vies";
    public static final String SKU_PACK_5000_VIES = "pack5000vies";
    public static final String SKU_FREE_INAPP_VIDEO = "free_inapp_video";
    public static final String SKU_PREMIUM_NO_AD = "ad_free_app";
}
