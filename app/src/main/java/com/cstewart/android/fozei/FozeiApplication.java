package com.cstewart.android.fozei;

import android.app.Application;
import android.content.Context;

import com.cstewart.android.fozei.model.ArtworkManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Created by chris on 8/19/14.
 */
public class FozeiApplication extends Application {

    private ObjectGraph mObjectGraph;

    public static FozeiApplication get(Context context) {
        return (FozeiApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(new FozeiGraph());
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }

    @Module (
            injects = {
                    FozeiListActivity.class,
                    FozeiArtworkActivity.class,
                    FozeiArtworkService.class
            }
    )
    public static class FozeiGraph {

        @Provides @Singleton
        ArtworkManager provideArtworkManager() {
            return new ArtworkManager();
        }

        @Provides @Singleton
        EventBus provideEventBus() {
            return EventBus.getDefault();
        }

    }
}
