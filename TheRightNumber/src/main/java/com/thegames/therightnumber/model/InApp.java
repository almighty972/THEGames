package com.thegames.therightnumber.model;

import android.text.TextUtils;

import com.thegames.therightnumber.billing.SkuDetails;

/**
 * Created by gyljean-lambert on 08/06/2014.
 */
public class InApp {

    private String name;
    private String cost;

    public InApp() {}

    public InApp(SkuDetails skuDetails) {
        if(skuDetails != null && !TextUtils.isEmpty(skuDetails.getTitle())) {
            String rawTitle = skuDetails.getTitle();
            this.name = rawTitle.substring(0, rawTitle.indexOf("vies")).trim();
            this.cost = skuDetails.getPrice();
        }
    }

    public InApp(String lifeCount, String cost) {
        this.name = lifeCount;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
