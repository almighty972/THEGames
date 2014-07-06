package com.thegames.therightnumber.billing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.thegames.therightnumber.AbstractFragment;
import com.thegames.therightnumber.Constants;
import com.thegames.therightnumber.R;
import com.thegames.therightnumber.interfaces.BillingListener;
import com.thegames.therightnumber.managers.BillingManager;
import com.thegames.therightnumber.model.InApp;
import com.thegames.therightnumber.views.InAppView;

import java.util.HashMap;

/**
 * Created by gyljean-lambert on 21/06/2014.
 */
public class InAppProductsFragments extends AbstractFragment implements BillingListener {

    private LinearLayout mInAppFragmentRootLayout;
    private ProgressBar mProgressRing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inapp_products, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInAppFragmentRootLayout = (LinearLayout) view.findViewById(R.id.inappFragmentRootLayout);
        mProgressRing = (ProgressBar) view.findViewById(R.id.progressRing);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressRing.setVisibility(View.VISIBLE);
        BillingManager.getInstance().setBillingListener(this);
        BillingManager.getInstance().getInAppProducts();
    }

    @Override
    public void onIabSetupFinished() {

    }

    @Override
    public void onProductsReceived(Inventory inventory) {
        mProgressRing.setVisibility(View.GONE);
        InApp inapp = null;

        // adding free inapp
        inapp = new InApp("25", getString(R.string.free_inapp), "");
        addInappView(inapp);

        // adding other inapp
        if(inventory != null) {
            if(inventory.hasDetails(Constants.SKU_PACK_200_VIES)) {
                inapp = new InApp(inventory.getSkuDetails(Constants.SKU_PACK_200_VIES));
                addInappView(inapp);
            } else if(inventory.hasDetails(Constants.SKU_PACK_200_VIES)) {
                inapp = new InApp(inventory.getSkuDetails(Constants.SKU_PACK_200_VIES));
                addInappView(inapp);
            } else if(inventory.hasDetails(Constants.SKU_PACK_200_VIES)) {
                inapp = new InApp(inventory.getSkuDetails(Constants.SKU_PACK_200_VIES));
                addInappView(inapp);
            }
        }
    }

    @Override
    public void onQueryPurchasedItems(HashMap<String, Purchase> itemsMap) {

    }

    @Override
    public void onConsumePurchasedItems(boolean consumeSuccess, Purchase purchase) {

    }

    private void addInappView(InApp inApp) {
        if(mInAppFragmentRootLayout != null) {
            mInAppFragmentRootLayout.addView(new InAppView(getActivity(), inApp));
        }
    }
}
