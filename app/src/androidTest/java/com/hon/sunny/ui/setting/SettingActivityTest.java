package com.hon.sunny.ui.setting;

import android.content.Context;
import android.content.Intent;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.hon.sunny.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Created by Frank Hon on 2019-09-12 00:40.
 * E-mail: frank_hon@foxmail.com
 */
@RunWith(AndroidJUnit4.class)
public class SettingActivityTest {

    private ActivityTestRule<TestToolbarActivity> activityRule = new ActivityTestRule<>(
            TestToolbarActivity.class,
            true,
            true
    );

    @Before
    public void init() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Intent intent=new Intent(appContext,TestToolbarActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityRule.launchActivity(intent);
    }

    @Test
    public void show() {
        onView(withId(R.id.appbar_layout)).check(matches(isDisplayed()));
        while (true){

        }
    }
}