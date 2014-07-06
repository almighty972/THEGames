package com.thegames.therightnumber.uihome;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thegames.therightnumber.AbstractActivity;
import com.thegames.therightnumber.AbstractFragment;
import com.thegames.therightnumber.Constants;
import com.thegames.therightnumber.R;
import com.thegames.therightnumber.billing.Inventory;
import com.thegames.therightnumber.billing.Purchase;
import com.thegames.therightnumber.helpers.SerializationHelper;
import com.thegames.therightnumber.interfaces.BillingListener;
import com.thegames.therightnumber.interfaces.HomeListener;
import com.thegames.therightnumber.interfaces.PopupDialogListener;
import com.thegames.therightnumber.managers.AdManager;
import com.thegames.therightnumber.managers.BillingManager;
import com.thegames.therightnumber.managers.FacebookManager;
import com.thegames.therightnumber.managers.GameManager;
import com.thegames.therightnumber.model.PurchaseWrapper;
import com.thegames.therightnumber.popups.LevelsDonePopup;
import com.thegames.therightnumber.popups.Popup;
import com.thegames.therightnumber.popups.RemoveAdPopup;
import com.thegames.therightnumber.uigame.GameActivity;
import com.thegames.therightnumber.uigame.PopupAction;

import org.jraf.android.backport.switchwidget.Switch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomeActivity extends AbstractActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BillingManager.getInstance().initBilling();
        BillingManager.getInstance().setCurrentActivity(this);
        FacebookManager.getInstance().setCurrentActivity(this);
        FacebookManager.getInstance().getUiHelper().onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FacebookManager.getInstance().getUiHelper().onResume();
        boolean appFirstLaunch = GameManager.getInstance().getBooleanPref(Constants.PREFS_APP_FIRST_LAUNCH, true);
        if(!appFirstLaunch) { // show interstitial if it is not the app first launch
            AdManager.getInstance().setCurrentActivity(this);
            AdManager.getInstance().showInterstitial();
        } else {
            GameManager.getInstance().saveBooleanPref(Constants.PREFS_APP_FIRST_LAUNCH, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FacebookManager.getInstance().getUiHelper().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BillingManager.getInstance().disposeBilling();
        FacebookManager.getInstance().getUiHelper().onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FacebookManager.getInstance().getUiHelper().onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!BillingManager.getInstance().getIabHelper().handleActivityResult(requestCode, resultCode, data)){
            super.onActivityResult(requestCode, resultCode, data);
        }
        FacebookManager.getInstance().handleActivityResult(requestCode, resultCode, data);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HomeFragment extends AbstractFragment implements BillingListener, PopupDialogListener, HomeListener {

        private DrawerLayout mDrawerLayout;
        private RelativeLayout mLeftDrawer;
        private LinearLayout mAdPremiumLayout;
        private ListView mDrawerList;
        private Button mPlayButton, mFbRecommendButton;
        private ImageButton mMenuImageButton;

        private Switch mAdSwitch;

        public HomeFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mAdPremiumLayout = (LinearLayout) view.findViewById(R.id.adPremiumLayout);
            mPlayButton = (Button) view.findViewById(R.id.playBtn);
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gameIntent = new Intent(getActivity(), GameActivity.class);
                    startActivity(gameIntent);
                }
            });

            mFbRecommendButton = (Button) view.findViewById(R.id.fbRecommendButton);
            mFbRecommendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FacebookManager.getInstance().share("test0", "test1", "test2", null, "");
                }
            });

            mMenuImageButton = (ImageButton) view.findViewById(R.id.menuImageButton);
            mMenuImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleDrawer();
                }
            });

            mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
            mLeftDrawer = (RelativeLayout) view.findViewById(R.id.left_drawer);
            mDrawerList = (ListView) view.findViewById(R.id.menuListView);

            // Set the adapter for the list view
            MenuAdapter menuAdapter = new MenuAdapter(getActivity(),
                    R.layout.drawer_list_item, getResources().getStringArray(R.array.menu_entries));
            mDrawerList.setAdapter(menuAdapter);

            // Set the list's click listener
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

            mAdSwitch = (Switch) view.findViewById(R.id.adSwitch);
            boolean userIsPremium = GameManager.getInstance().getBooleanPref(Constants.PREFS_USER_IS_PREMIUM, false);
            if(!userIsPremium) {
                mAdSwitch.setChecked(true);
                mAdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            RemoveAdPopup removeAdPopup = RemoveAdPopup.newInstance(getString(R.string.popup_remove_ad_title),
                                    getString(R.string.popup_remove_ad_option_two));
                            removeAdPopup.setPopupListener(HomeFragment.this);
                            showPopupDialog(removeAdPopup);
                        }
                    }
                });
            } else {
                mAdPremiumLayout.setVisibility(View.GONE);
            }
            GameManager.getInstance().setHomeListener(this);
            BillingManager.getInstance().setBillingListener(this);
        }

        /** Swaps fragments in the main content view */
        private void selectItem(int position) {
            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mLeftDrawer);
        }

        @Override
        public void onIabSetupFinished() {
            BillingManager.getInstance().queryPurchasedItems();
        }

        @Override
        public void onProductsReceived(Inventory inventory) {

        }

        @Override
        public void onQueryPurchasedItems(HashMap<String, Purchase> itemsMap) {
            if(itemsMap != null && !itemsMap.isEmpty()) {
                ArrayList<Purchase> purchases = new ArrayList<Purchase>();
                Iterator it = itemsMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if(pair.getValue() instanceof Purchase &&
                            pair.getValue() != null) {
                        purchases.add((Purchase) pair.getValue());
                    }
                }

                // If there are unconsumed purchased items consume them
                if(!purchases.isEmpty()) {
                    BillingManager.getInstance().consumePurchases(purchases);
                }
            }
        }

        @Override
        public void onConsumePurchasedItems(boolean consumeSuccess, Purchase purchase) {
            Log.d("", "onConsumePurchasedItems -> success: " + consumeSuccess + ", " + purchase.getSku());
            if(consumeSuccess && purchase != null) {
                GameManager.getInstance().addBonusLives(purchase.getSku());
                GameManager.getInstance().saveBooleanPref(Constants.PREFS_USER_IS_PREMIUM, true);
                PurchaseWrapper purchaseWrapper = new PurchaseWrapper(purchase.getItemType(),
                        purchase.getOrderId(), purchase.getPackageName(), purchase.getSku(),
                        purchase.getPurchaseTime(), purchase.getPurchaseState(), purchase.getDeveloperPayload(),
                        purchase.getToken(), purchase.getOriginalJson(), purchase.getSignature());

                String purchaseB64Str = SerializationHelper.objectToBase64String(purchaseWrapper);
                GameManager.getInstance().saveStringPref(Constants.PREFS_USER_PREMIUM_PURCHASE_OBJECT, purchaseB64Str); // finally we can save our object to shared preferences. Could be useful for later
                if(mAdPremiumLayout != null) {
                    mAdPremiumLayout.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public <T extends Popup> void onClickPopupButton(T popup, PopupAction popupAction) {
            popup.dismiss();
            GameManager.getInstance().handlePopupAction(popup, popupAction);
        }

        @Override
        public void onCountdownFinished() {

        }

        @Override
        public void onDismiss() {
            boolean userIsPremium = GameManager.getInstance().getBooleanPref(Constants.PREFS_USER_IS_PREMIUM, false);
            if(!userIsPremium) {
                mAdSwitch.setChecked(true);
            } else {
                mAdPremiumLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClickBuyPremiumInapp() {
            BillingManager.getInstance().purchaseInApp(Constants.SKU_PREMIUM_NO_AD);
        }

        private class DrawerItemClickListener implements ListView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position);
            }
        }

        private void toggleDrawer() {
            if (!mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
                mDrawerLayout.openDrawer(mLeftDrawer);
            } else {
                mDrawerLayout.closeDrawer(mLeftDrawer);
            }
        }

        /**
         * Menu Drawer adapter
         */
        private class MenuAdapter extends ArrayAdapter<String> {

            private String[] mItems;
            private Context mContext;
            private int mResourceId;

            public MenuAdapter(Context context, int resource, String[] objects) {
                super(context, resource, objects);
                mItems = objects;
                mContext = context;
                mResourceId = resource;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View row = convertView;
                ItemHolder holder = null;

                if(row == null) {
                    LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                    row = inflater.inflate(mResourceId, parent, false);

                    holder = new ItemHolder();
                    holder.entryName = (TextView)row.findViewById(R.id.menuEntryNameTextView);
                    if(position == 0) {
                        holder.entryName.setBackgroundResource(R.drawable.selector_menu_item_round_topright);
                    } else {
                        holder.entryName.setBackgroundResource(R.drawable.selector_menu_item);
                    }

                    row.setTag(holder);
                } else {
                    holder = (ItemHolder)row.getTag();
                }

                holder.entryName.setText(mItems[position]);

                return row;
            }
        }

        static class ItemHolder
        {
            TextView entryName;
        }

    }





}
