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
import com.cstewart.android.fozei.model.ArtworkManager;
import com.cstewart.android.fozei.model.Constants;
import com.cstewart.android.fozei.model.SourceState;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class FozeiArtworkActivity extends FozeiActivity {

    @Inject
    ArtworkManager mArtworkManager;
    @Inject EventBus mEventBus;

    private ImageView mImageView;

    private ArtSource mArtSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fozei_artwork);

        mArtSource = mArtworkManager.getArtSource();
        setTitle(mArtSource.getLabel());

        mImageView = (ImageView) findViewById(R.id.activity_fozei_artwork_image);
        updateImage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEventBus.unregister(this);
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

    public void onEventMainThread(SourceState sourceState) {
        updateImage();
    }

    private void nextImage() {
        Intent nextImageIntent = new Intent(Constants.ACTION_HANDLE_COMMAND)
                .setComponent(mArtSource.getComponentName())
                .setData(Uri.fromParts(Constants.URI_SCHEME_COMMAND,
                        Integer.toString(Constants.BUILTIN_COMMAND_ID_NEXT_ARTWORK), null))
                .putExtra(Constants.EXTRA_COMMAND_ID, Constants.BUILTIN_COMMAND_ID_NEXT_ARTWORK)
                .putExtra(Constants.EXTRA_SCHEDULED, true);
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
