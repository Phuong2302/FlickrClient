package com.ikota.flickrclient.ui;

import com.ikota.flickrclient.network.retrofit.FlickrService;
import com.ikota.flickrclient.network.retrofit.FlickrURL;

import javax.inject.Singleton;

import dagger.Provides;
import retrofit.RestAdapter;

@dagger.Module(
        injects = MainApplication.class,
        library = true
)
public class FlickrAPIModule {

    @Provides @Singleton
    public FlickrService provideFlickrService() {
        return new RestAdapter.Builder()
                .setEndpoint(FlickrURL.END_POINT)
                .build()
                .create(FlickrService.class);
    }

}
