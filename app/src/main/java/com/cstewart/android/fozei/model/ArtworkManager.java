package com.cstewart.android.fozei.model;

import com.cstewart.android.fozei.model.ArtSource;
import com.cstewart.android.fozei.model.SourceState;

public class ArtworkManager {

    private ArtSource mArtSource;
    private SourceState mSourceState;

    public SourceState getSourceState() {
        return mSourceState;
    }

    public void setSourceState(SourceState sourceState) {
        mSourceState = sourceState;
    }

    public ArtSource getArtSource() {
        return mArtSource;
    }

    public void setArtSource(ArtSource artSource) {
        mArtSource = artSource;
    }
}
