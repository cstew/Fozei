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

import com.cstewart.android.fozei.base.FozeiActivity;
import com.cstewart.android.fozei.model.ArtSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;


public class FozeiListActivity extends FozeiActivity {
    private static final String TAG = "FozeiListActivity";

    public static final String ACTION_MUZEI_ART_SOURCE
            = "com.google.android.apps.muzei.api.MuzeiArtSource";

    public static final String EXTRA_FROM_MUZEI_SETTINGS
            = "com.google.android.apps.muzei.api.extra.FROM_MUZEI_SETTINGS";

    public static final String ACTION_SUBSCRIBE = "com.google.android.apps.muzei.api.action.SUBSCRIBE";
    public static final String EXTRA_SUBSCRIBER_COMPONENT = "com.google.android.apps.muzei.api.extra.SUBSCRIBER_COMPONENT";
    public static final String EXTRA_TOKEN = "com.google.android.apps.muzei.api.extra.TOKEN";

    @Inject ArtworkManager mArtworkManager;

    private ListView mArtSourceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fozei_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupAdapter();
    }

    private void setupAdapter() {
        mArtSourceListView = (ListView) findViewById(R.id.activity_fozei_list);
        mArtSourceListView.setAdapter(new FozeiAdapter(this, getArtSources()));
    }

    private List<ArtSource> getArtSources() {
        List<ArtSource> artSources = new ArrayList<ArtSource>();

        Intent queryIntent = new Intent(ACTION_MUZEI_ART_SOURCE);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentServices(queryIntent, PackageManager.GET_META_DATA);

        for (ResolveInfo ri : resolveInfos) {
            ArtSource artSource = new ArtSource();

            artSource.setLabel(ri.loadLabel(packageManager).toString());
            artSource.setDrawable(ri.loadIcon(packageManager));
            artSource.setComponentName(new ComponentName(ri.serviceInfo.packageName,
                    ri.serviceInfo.name));

            artSources.add(artSource);
            Context packageContext;
            try {
                packageContext = createPackageContext(artSource.getComponentName().getPackageName(), 0);
                Resources packageRes = packageContext.getResources();
                artSource.setDescription(packageRes.getString(ri.serviceInfo.descriptionRes));
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Can't read package resources for source " + artSource.getComponentName());
            }
            Bundle metaData = ri.serviceInfo.metaData;
            if (metaData != null) {
                String settingsActivity = metaData.getString("settingsActivity");
                if (!TextUtils.isEmpty(settingsActivity)) {
                    artSource.setSettingsComponent(ComponentName.unflattenFromString(
                            ri.serviceInfo.packageName + "/" + settingsActivity));
                }

                artSource.setColor(metaData.getInt("color", artSource.getColor()));

                try {
                    float[] hsv = new float[3];
                    Color.colorToHSV(artSource.getColor(), hsv);
                    boolean adjust = false;
                    if (hsv[2] < 0.8f) {
                        hsv[2] = 0.8f;
                        adjust = true;
                    }
                    if (hsv[1] > 0.4f) {
                        hsv[1] = 0.4f;
                        adjust = true;
                    }
                    if (adjust) {
                        artSource.setColor(Color.HSVToColor(hsv));
                    }
                    if (Color.alpha(artSource.getColor()) != 255) {
                        artSource.setColor(Color.argb(255,
                                Color.red(artSource.getColor()),
                                Color.green(artSource.getColor()),
                                Color.blue(artSource.getColor())));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        Collections.sort(artSources, new Comparator<ArtSource>() {
            @Override
            public int compare(ArtSource s1, ArtSource s2) {
                return s1.getLabel().compareTo(s2.getLabel());
            }
        });

        return artSources;
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
                        mArtworkManager.setArtSource(artSource);

                        Intent subscribeIntent = new Intent(ACTION_SUBSCRIBE)
                                .setComponent(artSource.getComponentName())
                                .putExtra(EXTRA_SUBSCRIBER_COMPONENT, new ComponentName(getContext(), FozeiArtworkService.class))
                                .putExtra(EXTRA_TOKEN, UUID.randomUUID().toString());

                        getContext().startService(subscribeIntent);

                        Intent artworkIntent = new Intent(getContext(), FozeiArtworkActivity.class);
                        getContext().startActivity(artworkIntent);
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
                    Intent settingsIntent = new Intent();
                    settingsIntent.setComponent(artSource.getSettingsComponent());
                    getContext().startActivity(settingsIntent);
                }
            });

            return convertView;
        }
    }

}
