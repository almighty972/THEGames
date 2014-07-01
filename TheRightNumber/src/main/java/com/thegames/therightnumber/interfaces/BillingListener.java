package com.thegames.therightnumber.interfaces;

import com.thegames.therightnumber.billing.Inventory;

/**
 * Created by gyljean-lambert on 21/06/2014.
 */
public interface BillingListener {

    void onProductsReceived(Inventory inventory);
}
