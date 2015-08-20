package com.ikota.flickrclient.ui;

import android.app.Application;

import com.ikota.flickrclient.di.FlickrAPIModule;
import com.ikota.flickrclient.network.retrofit.FlickrService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;


public class AndroidApplication extends Application {

    private ObjectGraph objectGraph = null;

    @Inject FlickrService flickrService;

    @Override
    public void onCreate() {
        super.onCreate();
        if(objectGraph == null) {
            List modules = Collections.singletonList(new FlickrAPIModule());
            objectGraph = ObjectGraph.create(modules.toArray());
        }
    }

    // used to set ObjectGraph for test
    public void setObjectGraph(ObjectGraph graph) {
        objectGraph = graph;
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    public FlickrService api() {
        return flickrService;
    }

}
