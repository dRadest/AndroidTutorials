package com.example.android.draddest.mdcexercise;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests for testing functionality of the navigation drawer
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationDrawerTest {

    /*
     *  ActivityTestRule handles
     *  launching the activity before any methods are run
     *  and shutting it down after all finish
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void clickOnHomeIcon_OpensDrawer() {
        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer))
                .check(matches(isClosed(Gravity.LEFT))); // Left Drawer should be closed.

        // Open the drawer
        String navigateUpDesc = mActivityRule.getActivity()
                .getString(android.support.v7.appcompat.R.string.abc_action_bar_up_description);
        onView(withContentDescription(navigateUpDesc)).perform(click());

        // Check if drawer is open
        onView(withId(R.id.drawer))
                .check(matches(isOpen(Gravity.LEFT))); // Left drawer is open open.
    }
}
