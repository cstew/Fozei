package com.cstewart.android.fozei.model;

import android.content.ComponentName;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

public class ArtSource {
    private String mLabel;
    private String mDescription;
    private ComponentName mComponentName;
    private Drawable mDrawable;
    private int mColor;
    private ComponentName mSettingsComponent;

    public ArtSource() {
        mColor = Color.WHITE;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    public void setComponentName(ComponentName componentName) {
        mComponentName = componentName;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public ComponentName getSettingsComponent() {
        return mSettingsComponent;
    }

    public void setSettingsComponent(ComponentName settingsComponent) {
        mSettingsComponent = settingsComponent;
    }

    public boolean hasSettings() {
        return mSettingsComponent != null;
    }

    @Override
    public String toString() {
        return mLabel;
    }
}