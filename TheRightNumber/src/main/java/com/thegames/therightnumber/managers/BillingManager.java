package com.thegames.therightnumber.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.thegames.therightnumber.AbstractActivity;
import com.thegames.therightnumber.Constants;
import com.thegames.therightnumber.billing.IabHelper;
import com.thegames.therightnumber.billing.IabResult;
import com.thegames.therightnumber.billing.Inventory;
import com.thegames.therightnumber.billing.Purchase;
import com.thegames.therightnumber.interfaces.BillingListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
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


    private IabHelper mHelper;
    private String mLicence = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAptGh5cErTvmkS2f6NJjnTxtD9H2lfLfZ7viSDiXskPO5n42eNkU4F55ZCPkihcPym7+3wXHGjysiug+ElKKfKLcWQszWTHa9VaEWFASBcM8lg8hW9wSlxGXI9FET6+PbCdZQwxS7M2K3U+lPyDv3v9sQz89i5gRRIqbXylv4dTu/homEFoK0JNsyvq4Eiow/Hz4HJnbjcO4WiZOHM8AFeTQKBmeCp2+JMjKbJGz+ojjspWl7Sgeouc0TIYkV60ekmVieyIEZM+hBgfXGqO6IG5ucXObzl7UEtEGFwDojgxYgBOMkMYbWS/VZo6he1wYhGT1R7xnMLZ3X84Zsz6r74QIDAQAB";

    private boolean mPurchasedPack1;
    private boolean mPurchasedPack2;
    private boolean mPurchasedPack3;
    private boolean mPurchasedPremiumPack;


    /*
     * GETTERS / SETTERS
     */

    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    public IabHelper getIabHelper() {
        return mHelper;
    }

    public boolean isBillingReady() {
        return mBillingReady;
    }

    public void setBillingReady(boolean mBillingReady) {
        this.mBillingReady = mBillingReady;
    }

    public boolean isPurchasedPack1() {
        return mPurchasedPack1;
    }

    public void setPurchasedPack1(boolean mPurchasedPack1) {
        this.mPurchasedPack1 = mPurchasedPack1;
    }

    public boolean isPurchasedPack2() {
        return mPurchasedPack2;
    }

    public void setPurchasedPack2(boolean mPurchasedPack2) {
        this.mPurchasedPack2 = mPurchasedPack2;
    }

    public boolean isPurchasedPack3() {
        return mPurchasedPack3;
    }

    public void setPurchasedPack3(boolean mPurchasedPack3) {
        this.mPurchasedPack3 = mPurchasedPack3;
    }

    public boolean isPurchasedPremiumPack() {
        return mPurchasedPremiumPack;
    }

    public void setPurchasedPremiumPack(boolean mPurchasedPremiumPack) {
        this.mPurchasedPremiumPack = mPurchasedPremiumPack;
    }


    /*
     * CTOR
     */

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
                mBillingReady = true;

                // notify the listener
                if (mBillingListenerWeakReference != null && mBillingListenerWeakReference.get() != null) {
                    mBillingListenerWeakReference.get().onIabSetupFinished();
                }
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
                                Log.e(TAG, "Error getInAppProducts: " + result);
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
                            Log.e(TAG, "onIabPurchaseFinished()");
                            if (result.isFailure()) {
                                Log.e(TAG, "Error purchasing: " + result);
                                return;
                            } else if (purchase.getSku().equals(Constants.SKU_PACK_200_VIES)) {
                                consumePurchase(purchase, Constants.SKU_PACK_200_VIES);
                            } else if (purchase.getSku().equals(Constants.SKU_PACK_1500_VIES)) {
                                consumePurchase(purchase, Constants.SKU_PACK_1500_VIES);
                            } else if (purchase.getSku().equals(Constants.SKU_PACK_5000_VIES)) {
                                consumePurchase(purchase, Constants.SKU_PACK_5000_VIES);
                            } else if (purchase.getSku().equals(Constants.SKU_PREMIUM_NO_AD)) {
                                consumePurchase(purchase, Constants.SKU_PREMIUM_NO_AD);
                            }
                        }
                    }, "Y29tLnRoZWdhbWVzLnRoZXJpZ2h0bnVtYmVyLmFwcC5hbmRyb2lk"
            );
        }
    }

    public void queryPurchasedItems() {
        if(mHelper != null) {
            Log.e(TAG, " queryPurchasedItems DB1 ");
            mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                    if (result.isFailure()) {
                        Log.e(TAG, "Error queryPurchasedItems: " + result);
                    }
                    else {
                        Log.e(TAG, " queryPurchasedItems DB2 ");
                        mPurchasedPack1 = inventory.hasPurchase(Constants.SKU_PACK_200_VIES);
                        mPurchasedPack2 = inventory.hasPurchase(Constants.SKU_PACK_1500_VIES);
                        mPurchasedPack2 = inventory.hasPurchase(Constants.SKU_PACK_1500_VIES);
                        mPurchasedPremiumPack = inventory.hasPurchase(Constants.SKU_PREMIUM_NO_AD);

                        HashMap<String, Purchase> map = new HashMap<String, Purchase>();
                        map.put(Constants.SKU_PACK_200_VIES, inventory.getPurchase(Constants.SKU_PACK_200_VIES));
                        map.put(Constants.SKU_PACK_1500_VIES, inventory.getPurchase(Constants.SKU_PACK_1500_VIES));
                        map.put(Constants.SKU_PACK_5000_VIES, inventory.getPurchase(Constants.SKU_PACK_5000_VIES));
                        map.put(Constants.SKU_PREMIUM_NO_AD, inventory.getPurchase(Constants.SKU_PREMIUM_NO_AD));

                        // notify the listener
                        if (mBillingListenerWeakReference != null && mBillingListenerWeakReference.get() != null) {
                            mBillingListenerWeakReference.get().onQueryPurchasedItems(map);
                        }
                    }
                }
            });
        }
    }

    public void consumePurchase(Purchase purchase, String sku) {
        mHelper.consumeAsync(purchase,
                new IabHelper.OnConsumeFinishedListener() {
                    @Override
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                        if (result.isSuccess()) {
                            // provision the in-app purchase to the user
                            // (for example, credit 50 gold coins to player's character)
                            Log.i(TAG, "ACHAT CONSOMME POUR PACK: " + purchase.getSku());
                        }
                        else {
                            Log.e(TAG, "Erreur consumePurchase: " + result.getMessage());
                        }

                        // notify the listener
                        if (mBillingListenerWeakReference != null && mBillingListenerWeakReference.get() != null) {
                            mBillingListenerWeakReference.get().onConsumePurchasedItems(result.isSuccess(), purchase);
                        }
                    }
                });
    }

    public void consumePurchases(List<Purchase> purchases) {
        mHelper.consumeAsync(purchases, new IabHelper.OnConsumeMultiFinishedListener() {
                    @Override
                    public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
                        if (purchases != null && results != null && purchases.size() == results.size()) {
                            int i = 0;
                            for (IabResult result : results) {
                                Purchase purchase = purchases.get(i);
                                boolean consumeSuccess = result.isSuccess();
                                if (consumeSuccess) {
                                    // provision the in-app purchase to the user
                                    // (for example, credit 50 gold coins to player's character)
                                    Log.i(TAG, "ACHAT CONSOMME POUR PACK: " + purchase.getSku());

                                } else {
                                    Log.e(TAG, "Erreur consumePurchase: " + result.getMessage());
                                }

                                // notify the listener
                                if (mBillingListenerWeakReference != null && mBillingListenerWeakReference.get() != null) {
                                    mBillingListenerWeakReference.get().onConsumePurchasedItems(consumeSuccess, purchase);
                                }

                                i++;
                            }
                        }
                    }
                });
    }

    public void disposeBilling() {
        if (mHelper != null) {
            mHelper.dispose();
        }
        mHelper = null;
    }
}
