package com.ikota.flickrclient.ui;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;

import com.ikota.flickrclient.IdlingResource.ListCountIdlingResource;
import com.ikota.flickrclient.IdlingResource.LoadingIdlingResource;
import com.ikota.flickrclient.IdlingResource.TimingIdlingResource;
import com.ikota.flickrclient.R;
import com.ikota.flickrclient.data.DataHolder;
import com.ikota.flickrclient.di.DummyAPIModule;
import com.ikota.flickrclient.di.PopularListModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import dagger.ObjectGraph;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class ImageListFragmentTest extends ActivityInstrumentationTestCase2<PopularListActivity> {

    public ImageListFragmentTest() {
        super(PopularListActivity.class);
    }

    @Rule
    public ActivityTestRule<PopularListActivity> activityRule = new ActivityTestRule<>(
            PopularListActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False so we can customize the intent per test method

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    private void setupMockServer(String response) {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        AndroidApplication app =
                (AndroidApplication) instrumentation.getTargetContext().getApplicationContext();

        // setup objectGraph to inject Mock API
        List modules = Collections.singletonList(new DummyAPIModule(response));
        ObjectGraph graph = ObjectGraph.create(modules.toArray());
        app.setObjectGraph(graph);
        app.getObjectGraph().inject(app);
    }

    @Test
    public void testProgress_show() {
        setupMockServer(DataHolder.LIST_JSON);
        PopularListActivity activity = activityRule.launchActivity(new Intent());
        ImageListFragment fragment = (ImageListFragment)activity.getSupportFragmentManager()
                .findFragmentByTag(ImageListFragment.class.getSimpleName());
        @SuppressWarnings("ConstantConditions")
        RecyclerView recyclerView = (RecyclerView)fragment.getView().findViewById(android.R.id.list);

        onView(ViewMatchers.withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        IdlingResource idlingResource = new LoadingIdlingResource(recyclerView);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, scrollTo()));
        Espresso.unregisterIdlingResources(idlingResource);
        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    // TODO : add test to check if toast is displayed.
    @Test
    public void testEmptyView_show() {
        String empty_response = "{\"photos\":{\"page\":1,\"pages\":25,\"perpage\":20,\"total\":500,\"photo\":[],\"stat\":\"ok\"}";
        setupMockServer(empty_response);
        activityRule.launchActivity(new Intent());

        onView(withId(android.R.id.empty)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        TimingIdlingResource idlingResource = new TimingIdlingResource(1000);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.empty)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void loadNextItems() {
        PopularListActivity activity = activityRule.launchActivity(new Intent());
        ImageListFragment fragment = (ImageListFragment)activity.getSupportFragmentManager()
                .findFragmentByTag(ImageListFragment.class.getSimpleName());
        @SuppressWarnings("ConstantConditions")
        RecyclerView recyclerView = (RecyclerView)fragment.getView().findViewById(android.R.id.list);

        ListCountIdlingResource idlingResource_24 = new ListCountIdlingResource(recyclerView, 24);
        Espresso.registerIdlingResources(idlingResource_24);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(18, scrollTo()));
        Espresso.unregisterIdlingResources(idlingResource_24);

        ListCountIdlingResource idlingResource_48 = new ListCountIdlingResource(recyclerView, 48);
        Espresso.registerIdlingResources(idlingResource_48);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(30, scrollTo()));
        Espresso.unregisterIdlingResources(idlingResource_48);

        ListCountIdlingResource idlingResource_72 = new ListCountIdlingResource(recyclerView, 72);
        Espresso.registerIdlingResources(idlingResource_72);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, scrollTo()));
        assertEquals(72, recyclerView.getAdapter().getItemCount());
    }

    /**
     * TODO: This test doesn't check if grid cell size is set correctly.
     * Reference URL of how to change configuration.
     * https://gist.github.com/Skipants/1001366/874b8d4b51bcd41ea95de408b9558e3cc557fe4f
     */
    @Test
    public void checkColumnNumInDifferntOrientation() {
        PopularListActivity activity = activityRule.launchActivity(new Intent());
        ImageListFragment fragment = (ImageListFragment)activity.getSupportFragmentManager()
                .findFragmentByTag(ImageListFragment.class.getSimpleName());
        @SuppressWarnings("ConstantConditions")
        RecyclerView recyclerView = (RecyclerView)fragment.getView().findViewById(android.R.id.list);

        // assertion in  portrait mode
        ListCountIdlingResource idlingResource1 = new ListCountIdlingResource(recyclerView, 1);
        Espresso.registerIdlingResources(idlingResource1);
        onView(withId(android.R.id.list)).perform(swipeLeft());
        Espresso.unregisterIdlingResources(idlingResource1);
        assertEquals(PopularListModule.PORTRAIT_COL,
                ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount());
        activity.finish();

        // change orientation to landscape
        Resources instrumentResources = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources();
        Configuration originalConfig = instrumentResources.getConfiguration();
        Configuration changeableConfig = new Configuration(originalConfig);
        changeableConfig.orientation = Configuration.ORIENTATION_LANDSCAPE; // Phone switches to landscape
        instrumentResources.updateConfiguration(changeableConfig, instrumentResources.getDisplayMetrics()); // Now the phone is "in landscape"

        // assertion in landscape mode
        PopularListActivity activity2 = activityRule.launchActivity(new Intent());
        ImageListFragment fragment2 = (ImageListFragment)activity2.getSupportFragmentManager()
                .findFragmentByTag(ImageListFragment.class.getSimpleName());
        @SuppressWarnings("ConstantConditions")
        RecyclerView recyclerView2 = (RecyclerView)fragment2.getView().findViewById(android.R.id.list);
        ListCountIdlingResource idlingResource2 = new ListCountIdlingResource(recyclerView, 1);
        Espresso.registerIdlingResources(idlingResource2);
        onView(withId(android.R.id.list)).perform(swipeLeft());
        Espresso.unregisterIdlingResources(idlingResource2);
        assertEquals(PopularListModule.HORIZONTAL_COL,
                ((GridLayoutManager) recyclerView2.getLayoutManager()).getSpanCount());
        activity.finish();

        // return the orientation to portrait mode
        changeableConfig.orientation = Configuration.ORIENTATION_PORTRAIT; // Phone switches to landscape
        instrumentResources.updateConfiguration(changeableConfig, instrumentResources.getDisplayMetrics()); // Now the phone is "in landscape"

    }

}