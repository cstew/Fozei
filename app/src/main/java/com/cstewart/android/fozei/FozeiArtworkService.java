package com.cstewart.android.fozei;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cstewart.android.fozei.model.ArtworkManager;
import com.cstewart.android.fozei.model.Constants;
import com.cstewart.android.fozei.model.SourceState;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class FozeiArtworkService extends IntentService {
    private static final String TAG = FozeiArtworkService.class.getSimpleName();

    @Inject
    ArtworkManager mArtworkManager;
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

        if (Constants.ACTION_PUBLISH_STATE.equals(intent.getAction())) {
            SourceState sourceState = getSourceState(intent);
            mArtworkManager.setSourceState(sourceState);
            mEventBus.post(sourceState);
        }
    }

    private SourceState getSourceState(Intent intent) {
        if (intent.hasExtra(Constants.EXTRA_STATE)) {
            Bundle bundle = intent.getBundleExtra(Constants.EXTRA_STATE);
            if (bundle != null) {
                return SourceState.fromBundle(bundle);
            }
        }

        return null;
    }
}
