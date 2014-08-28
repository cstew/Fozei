package com.cstewart.android.fozei.model;

public class Constants {

    public static final String ACTION_MUZEI_ART_SOURCE = "com.google.android.apps.muzei.api.MuzeiArtSource";

    public static final String ACTION_SUBSCRIBE = "com.google.android.apps.muzei.api.action.SUBSCRIBE";
    public static final String EXTRA_SUBSCRIBER_COMPONENT = "com.google.android.apps.muzei.api.extra.SUBSCRIBER_COMPONENT";
    public static final String EXTRA_TOKEN = "com.google.android.apps.muzei.api.extra.TOKEN";

    public static final String ACTION_PUBLISH_STATE = "com.google.android.apps.muzei.api.action.PUBLISH_UPDATE";
    public static final String EXTRA_STATE = "com.google.android.apps.muzei.api.extra.STATE";

    public static final String ACTION_HANDLE_COMMAND = "com.google.android.apps.muzei.api.action.HANDLE_COMMAND";
    public static final String EXTRA_COMMAND_ID = "com.google.android.apps.muzei.api.extra.COMMAND_ID";
    public static final String EXTRA_SCHEDULED = "com.google.android.apps.muzei.api.extra.SCHEDULED";

    public static final String URI_SCHEME_COMMAND = "muzeicommand";

    private static final int FIRST_BUILTIN_COMMAND_ID = 1000;

    public static final int BUILTIN_COMMAND_ID_NEXT_ARTWORK = FIRST_BUILTIN_COMMAND_ID + 1;
}
