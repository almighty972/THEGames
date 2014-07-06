package com.thegames.therightnumber.interfaces;

import com.thegames.therightnumber.billing.Inventory;
import com.thegames.therightnumber.billing.Purchase;

import java.util.HashMap;

/**
 * Created by gyljean-lambert on 21/06/2014.
 */
public interface BillingListener {

    void onIabSetupFinished();
    void onProductsReceived(Inventory inventory);
    void onQueryPurchasedItems(HashMap<String, Purchase> itemsMap);
    void onConsumePurchasedItems(boolean consumeSuccess, Purchase purchase);
}
