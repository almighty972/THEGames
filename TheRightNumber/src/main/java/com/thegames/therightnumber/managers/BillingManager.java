package com.thegames.therightnumber.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.thegames.therightnumber.AbstractActivity;
import com.thegames.therightnumber.Constants;
import com.thegames.therightnumber.billing.IabHelper;
import com.thegames.therightnumber.billing.IabResult;
import com.thegames.therightnumber.billing.Inventory;
import com.thegames.therightnumber.billing.Purchase;
import com.thegames.therightnumber.interfaces.BillingListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gyljean-lambert on 21/06/2014.
 */
public class BillingManager {

    public static final String TAG = "GameManager";

    private static BillingManager INSTANCE;
    private Context mAppContext;
    private SharedPreferences mSharedPreferences;
    private boolean mBillingReady;
    private WeakReference<AbstractActivity> mCurrentActivityWeakReference = new WeakReference<AbstractActivity>(null);
    private WeakReference<BillingListener> mBillingListenerWeakReference = new WeakReference<BillingListener>(null);

    IabHelper mHelper;

    String mLicence = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAptGh5cErTvmkS2f6NJjnTxtD9H2lfLfZ7viSDiXskPO5n42eNkU4F55ZCPkihcPym7+3wXHGjysiug+ElKKfKLcWQszWTHa9VaEWFASBcM8lg8hW9wSlxGXI9FET6+PbCdZQwxS7M2K3U+lPyDv3v9sQz89i5gRRIqbXylv4dTu/homEFoK0JNsyvq4Eiow/Hz4HJnbjcO4WiZOHM8AFeTQKBmeCp2+JMjKbJGz+ojjspWl7Sgeouc0TIYkV60ekmVieyIEZM+hBgfXGqO6IG5ucXObzl7UEtEGFwDojgxYgBOMkMYbWS/VZo6he1wYhGT1R7xnMLZ3X84Zsz6r74QIDAQAB";

    IInAppBillingService mService;

    /*
     * GETTERS / SETTERS
     */

    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    public boolean isBillingReady() {
        return mBillingReady;
    }

    public void setBillingReady(boolean mBillingReady) {
        this.mBillingReady = mBillingReady;
    }

    private BillingManager(){}

    public static BillingManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new BillingManager();
        }
        return INSTANCE;
    }

    public void setCurrentActivity(final AbstractActivity currentActivity) {
        if(currentActivity != null) {
            mCurrentActivityWeakReference = new WeakReference<AbstractActivity>(currentActivity);
        }
    }

    public void setBillingListener(final BillingListener billingListener) {
        if(billingListener != null) {
            mBillingListenerWeakReference = new WeakReference<BillingListener>(billingListener);
        }
    }

    /*
     * BILLING METHODS
     */

    public void initBilling() {
        if(mHelper == null || !mBillingReady) {
            mHelper = new IabHelper(mAppContext, mLicence);
//            mHelper.enableDebugLogging(true);
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Log.d(TAG, "Problem setting up In-app Billing: " + result);
                        mBillingReady = false;
                    }
                    // Hooray, IAB is fully set up!
                    Log.d(TAG, "INAPP BILLING OK! " + result);
                    mBillingReady = true;
                }
            });
        }

    }

    public void getInAppProducts() {
        if(mHelper != null && mBillingReady) {
            List<String> additionalSkuList = new ArrayList<String>();
            additionalSkuList.add(Constants.SKU_PACK_200_VIES);
            additionalSkuList.add(Constants.SKU_PACK_1500_VIES);
            additionalSkuList.add(Constants.SKU_PACK_5000_VIES);
            mHelper.queryInventoryAsync(true, additionalSkuList,
                    new IabHelper.QueryInventoryFinishedListener() {
                        @Override
                        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                            if (result.isFailure()) {
                                // handle error
                                return;
                            }
                            if (mBillingListenerWeakReference != null && mBillingListenerWeakReference.get() != null) {
                                mBillingListenerWeakReference.get().onProductsReceived(inventory);
                            }
                        }
                    }
            );
        }
    }

    public void purchaseInApp(String productId) {
        if(mHelper != null && mBillingReady) {
            mHelper.launchPurchaseFlow(mCurrentActivityWeakReference.get(), productId, 10001,
                    new IabHelper.OnIabPurchaseFinishedListener() {
                        @Override
                        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                            if (result.isFailure()) {
                                Log.d(TAG, "Error purchasing: " + result);
                                return;
                            } else if (purchase.getSku().equals(Constants.SKU_PACK_200_VIES)) {

                            } else if (purchase.getSku().equals(Constants.SKU_PACK_1500_VIES)) {

                            } else if (purchase.getSku().equals(Constants.SKU_PACK_5000_VIES)) {

                            } else if (purchase.getSku().equals(Constants.SKU_AD_FREE)) {

                            }
                        }
                    }, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ"
            );
        }
    }

    public void disposeBilling() {
        if (mHelper != null) {
            mHelper.dispose();
        }
        mHelper = null;
    }

}
