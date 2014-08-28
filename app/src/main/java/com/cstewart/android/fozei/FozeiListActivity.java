package com.cstewart.android.fozei;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cstewart.android.fozei.base.FozeiActivity;
import com.cstewart.android.fozei.model.ArtSource;
import com.cstewart.android.fozei.model.ArtworkManager;
import com.cstewart.android.fozei.model.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;


public class FozeiListActivity extends FozeiActivity {
    private static final String TAG = FozeiListActivity.class.getSimpleName();

    @Inject
    ArtworkManager mArtworkManager;

    private ListView mArtSourceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fozei_list);
        mArtSourceListView = (ListView) findViewById(R.id.activity_fozei_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupAdapter();
    }

    private void setupAdapter() {
        mArtSourceListView.setAdapter(new FozeiAdapter(this, getArtSources()));
    }

    private List<ArtSource> getArtSources() {
        List<ArtSource> artSources = new ArrayList<ArtSource>();

        Intent queryIntent = new Intent(Constants.ACTION_MUZEI_ART_SOURCE);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentServices(queryIntent, PackageManager.GET_META_DATA);

        for (ResolveInfo ri : resolveInfos) {
            ArtSource artSource = new ArtSource();

            artSource.setLabel(ri.loadLabel(packageManager).toString());
            artSource.setDrawable(ri.loadIcon(packageManager));
            artSource.setComponentName(new ComponentName(ri.serviceInfo.packageName, ri.serviceInfo.name));
            artSource.setDescription(getDescription(ri, artSource.getComponentName()));
            parseMetaData(ri, artSource);

            artSources.add(artSource);
        }

        Collections.sort(artSources, new Comparator<ArtSource>() {
            @Override
            public int compare(ArtSource s1, ArtSource s2) {
                return s1.getLabel().compareTo(s2.getLabel());
            }
        });

        return artSources;
    }

    private void parseMetaData(ResolveInfo resolveInfo, ArtSource artSource) {
        Bundle metaData = resolveInfo.serviceInfo.metaData;
        if (metaData == null) {
            return;
        }

        String settingsActivity = metaData.getString("settingsActivity");
        if (!TextUtils.isEmpty(settingsActivity)) {
            artSource.setSettingsComponent(ComponentName.unflattenFromString(
                    resolveInfo.serviceInfo.packageName + "/" + settingsActivity));
        }

        int color = metaData.getInt("color", artSource.getColor());

        if (Color.alpha(color) != 255) {
            color = Color.argb(255,
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color));
        }
        artSource.setColor(color);
    }

    private String getDescription(ResolveInfo resolveInfo, ComponentName componentName) {
        try {
            Context packageContext = createPackageContext(componentName.getPackageName(), 0);
            Resources packageRes = packageContext.getResources();
            return packageRes.getString(resolveInfo.serviceInfo.descriptionRes);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Can't read package resources for source " + componentName);
        }
        return null;
    }

    private class FozeiAdapter extends ArrayAdapter<ArtSource> {

        public FozeiAdapter(Context context, List<ArtSource> artSources) {
            super(context, 0, artSources);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ArtSource artSource = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_art_source, parent, false);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscribe(artSource);
                    }
                });
            }

            View imageBackground = convertView.findViewById(R.id.view_art_source_image_background);
            imageBackground.setBackgroundColor(artSource.getColor());

            ImageView imageView = (ImageView) convertView.findViewById(R.id.view_art_source_image);
            imageView.setImageDrawable(artSource.getDrawable());

            TextView titleText = (TextView) convertView.findViewById(R.id.view_art_source_title);
            titleText.setText(artSource.getLabel());

            TextView descriptionText = (TextView) convertView.findViewById(R.id.view_art_source_description);
            descriptionText.setText(artSource.getDescription());

            final ImageButton settingsButton = (ImageButton) convertView.findViewById(R.id.view_art_source_setting);
            settingsButton.setVisibility(artSource.hasSettings() ? View.VISIBLE : View.GONE);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSettings(artSource);
                }
            });

            return convertView;
        }
    }

    private void startSettings(ArtSource artSource) {
        Intent settingsIntent = new Intent();
        settingsIntent.setComponent(artSource.getSettingsComponent());

        try {
            startActivity(settingsIntent);
        } catch (SecurityException exception) {
            Log.e(TAG, "Unable to start activity", exception);
            Toast.makeText(this, "Unable to start activity: " + exception, Toast.LENGTH_LONG).show();
        }
    }

    private void subscribe(ArtSource artSource) {
        mArtworkManager.setArtSource(artSource);

        Intent subscribeIntent = new Intent(Constants.ACTION_SUBSCRIBE)
                .setComponent(artSource.getComponentName())
                .putExtra(Constants.EXTRA_SUBSCRIBER_COMPONENT, new ComponentName(this, FozeiArtworkService.class))
                .putExtra(Constants.EXTRA_TOKEN, UUID.randomUUID().toString());

        startService(subscribeIntent);

        Intent artworkIntent = new Intent(this, FozeiArtworkActivity.class);
        startActivity(artworkIntent);
    }
}
