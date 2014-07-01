package com.thegames.therightnumber;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.thegames.therightnumber.popups.Popup;

/**
 * Created by gyljean-lambert on 24/05/2014.
 */
public abstract class AbstractFragment extends Fragment {

    private AdView mAdView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recherchez AdView comme ressource et chargez une demande.
        mAdView = (AdView)view.findViewById(R.id.adView);
        if(mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("87ABE0391AFEE440EFC081DF3EB77D4E").build();
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdView.destroy();
    }


    /**
     * Allows to display DialogFragment of type Popup anywhere from an {@link AbstractFragment}
     * @param popup A DialogFragment of type Popup to show
     */
    protected void showPopupDialog(Popup popup) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        if(popup != null) {
            popup.show(ft, "dialog");
        }
    }

    protected void openUrl(String url) {
        Intent visitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        getActivity().startActivity(visitIntent);
    }
}
