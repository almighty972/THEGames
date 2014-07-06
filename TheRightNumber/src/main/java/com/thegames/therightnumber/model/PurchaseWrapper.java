package com.thegames.therightnumber.model;

import com.thegames.therightnumber.Constants;

import java.io.Serializable;

/**
 * Created by gyljean-lambert on 06/07/2014.
 */
public class PurchaseWrapper implements Serializable {
    private static final long serialVersionUID = Constants.SERIALIZATION_UUID_VERSION;

    String mItemType;  // ITEM_TYPE_INAPP or ITEM_TYPE_SUBS
    String mOrderId;
    String mPackageName;
    String mSku;
    long mPurchaseTime;
    int mPurchaseState;
    String mDeveloperPayload;
    String mToken;
    String mOriginalJson;
    String mSignature;

    public PurchaseWrapper(String itemType, String orderId, String packageName, String sku,
                  long purchaseTime, int purchaseState, String devloperPayload, String token,
                  String originalJson, String signature) {
            mItemType = itemType;
            mOrderId = orderId;
            mPackageName = packageName;
            mSku = sku;
            mPurchaseTime = purchaseTime;
            mPurchaseState = purchaseState;
            mDeveloperPayload = devloperPayload;
            mToken = token;
            mOriginalJson = originalJson;
            mSignature = signature;
    }
}
