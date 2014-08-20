package com.cstewart.android.fozei;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.cstewart.android.fozei.base.FozeiActivity;
import com.cstewart.android.fozei.model.ArtSource;
import com.cstewart.android.fozei.model.Artwork;
import com.cstewart.android.fozei.model.SourceState;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class FozeiArtworkActivity extends FozeiActivity {

    public static final String ACTION_HANDLE_COMMAND = "com.google.android.apps.muzei.api.action.HANDLE_COMMAND";
    public static final String EXTRA_COMMAND_ID = "com.google.android.apps.muzei.api.extra.COMMAND_ID";
    public static final String EXTRA_SCHEDULED = "com.google.android.apps.muzei.api.extra.SCHEDULED";

    private static final String URI_SCHEME_COMMAND = "muzeicommand";

    private static final int FIRST_BUILTIN_COMMAND_ID = 1000;

    public static final int BUILTIN_COMMAND_ID_NEXT_ARTWORK = FIRST_BUILTIN_COMMAND_ID + 1;

    @Inject ArtworkManager mArtworkManager;
    @Inject EventBus mEventBus;

    private ImageView mImageView;

    private ArtSource mArtSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fozei_artwork);

        mArtSource = mArtworkManager.getArtSource();

        mImageView = (ImageView) findViewById(R.id.activity_fozei_artwork_image);
        updateImage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fozei_artwork, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fozei_artwork_next:
                nextImage();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEventBus.unregister(this);
    }

    public void onEventMainThread(SourceState sourceState) {
        updateImage();
    }

    private void nextImage() {
        // Go to next
        Intent nextImageIntent = new Intent(ACTION_HANDLE_COMMAND)
                .setComponent(mArtSource.getComponentName())
                .setData(Uri.fromParts(URI_SCHEME_COMMAND,
                        Integer.toString(BUILTIN_COMMAND_ID_NEXT_ARTWORK), null))
                .putExtra(EXTRA_COMMAND_ID, BUILTIN_COMMAND_ID_NEXT_ARTWORK)
                .putExtra(EXTRA_SCHEDULED, true);
        startService(nextImageIntent);
    }

    private void updateImage() {
        SourceState sourceState = mArtworkManager.getSourceState();
        if (sourceState == null) {
            return;
        }

        Artwork artwork = sourceState.getCurrentArtwork();
        if (artwork == null) {
            return;
        }

        Uri uri = artwork.getImageUri();
        if (uri == null) {
            return;
        }

        Picasso.with(this).load(uri).into(mImageView);
    }
}
