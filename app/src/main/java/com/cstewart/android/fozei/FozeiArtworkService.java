package com.cstewart.android.fozei;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cstewart.android.fozei.model.SourceState;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class FozeiArtworkService extends IntentService {
    private static final String TAG = FozeiArtworkService.class.getSimpleName();

    public static final String ACTION_PUBLISH_STATE = "com.google.android.apps.muzei.api.action.PUBLISH_UPDATE";
    public static final String EXTRA_STATE = "com.google.android.apps.muzei.api.extra.STATE";

    @Inject ArtworkManager mArtworkManager;
    @Inject EventBus mEventBus;

    public FozeiArtworkService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FozeiApplication.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received intent: " + intent);

        if (ACTION_PUBLISH_STATE.equals(intent.getAction())) {
            SourceState sourceState = getSourceState(intent);
            mArtworkManager.setSourceState(sourceState);
            mEventBus.post(sourceState);
        }
    }

    private SourceState getSourceState(Intent intent) {
        if (intent.hasExtra(EXTRA_STATE)) {
            Bundle bundle = intent.getBundleExtra(EXTRA_STATE);
            if (bundle != null) {
                return SourceState.fromBundle(bundle);
            }
        }

        return null;
    }
}
