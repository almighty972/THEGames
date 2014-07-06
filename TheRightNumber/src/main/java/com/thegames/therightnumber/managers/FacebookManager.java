package com.thegames.therightnumber.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.thegames.therightnumber.AbstractActivity;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 21/06/2014.
 */
public class FacebookManager {

    public static final String TAG = "FacebookManager";

    private static FacebookManager INSTANCE;
    private Context mAppContext;
    private WeakReference<AbstractActivity> mCurrentActivityWeakReference = new WeakReference<AbstractActivity>(null);

    private UiLifecycleHelper mUiHelper;

    /*
     * GETTERS / SETTERS
     */

    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    public UiLifecycleHelper getUiHelper() {
        return mUiHelper;
    }

    public void setUiHelper(UiLifecycleHelper uiHelper) {
        this.mUiHelper = uiHelper;
    }

    /*
     * CTOR
     */

    private FacebookManager(){}

    public static FacebookManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FacebookManager();
        }
        return INSTANCE;
    }

    public void setCurrentActivity(final AbstractActivity currentActivity) {
        if(currentActivity != null) {
            mCurrentActivityWeakReference = new WeakReference<AbstractActivity>(currentActivity);
            mUiHelper = new UiLifecycleHelper(mCurrentActivityWeakReference.get(), null);
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        mUiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
                boolean didCancel = FacebookDialog.getNativeDialogDidComplete(data);
                String postId = FacebookDialog.getNativeDialogPostId(data);
                Log.e(TAG, "===> Facebook post id: " + postId + ", dialog completed with success: " + didCancel);
            }
        });
    }

    public void share(String name, String caption, String description, String link, String picture) {
        if(mCurrentActivityWeakReference != null && mCurrentActivityWeakReference.get() != null) {
            if (FacebookDialog.canPresentShareDialog(mAppContext,
                    FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(mCurrentActivityWeakReference.get())
                        .setName(name)
                        .setLink(link)
                        .setDescription(description)
                        .setCaption(caption)
                        .setPicture(picture)
                        .build();
                mUiHelper.trackPendingDialogCall(shareDialog.present());
            } else {
                publishFeedDialog(name, caption, description, link, picture);
            }
        }
    }


    private void publishFeedDialog(String name, String caption, String description, String link, String picture) {
        if(mCurrentActivityWeakReference != null && mCurrentActivityWeakReference.get() != null) {
            Bundle params = new Bundle();
            params.putString("name", name);
            params.putString("caption", caption);
            params.putString("description", description);
            params.putString("link", link);
            params.putString("picture", picture);

            WebDialog feedDialog = (
                    new WebDialog.FeedDialogBuilder(mCurrentActivityWeakReference.get(),
                            Session.getActiveSession(),
                            params))
                    .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                        @Override
                        public void onComplete(Bundle values,
                                               FacebookException error) {
                            if (error == null) {
                                // When the story is posted, echo the success
                                // and the post Id.
                                final String postId = values.getString("post_id");
                                if (postId != null) {
                                    Toast.makeText(mCurrentActivityWeakReference.get(),
                                            "Posted story, id: " + postId,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // User clicked the Cancel button
                                    Toast.makeText(mAppContext,
                                            "Publish cancelled by user",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else if (error instanceof FacebookOperationCanceledException) {
                                // User clicked the "x" button
                                Toast.makeText(mAppContext,
                                        "Publish cancelled",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // Generic, ex: network error
                                Toast.makeText(mAppContext,
                                        "Error posting story",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    })
                    .build();
            feedDialog.show();
        }
    }
}
