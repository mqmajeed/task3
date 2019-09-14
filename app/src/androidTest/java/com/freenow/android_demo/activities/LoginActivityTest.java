package com.freenow.android_demo.activities;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.freenow.android_demo.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    private static String URL = "https://randomuser.me/api/?seed=a1f30d446f820665";
    private static String username;
    private static String password;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");
    @BeforeClass
    public static void LogingCredentials() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();

        Response response = client.newCall(request).execute();
        JsonParser mJsonParser = new JsonParser();
        JsonObject jsonObject = mJsonParser.parse(response.body().string()).getAsJsonObject();
        JsonArray results = jsonObject.getAsJsonArray("results");
        JsonElement jsonElement = results.get(0);
        JsonObject jsonUser = jsonElement.getAsJsonObject();
        JsonObject login = jsonUser.getAsJsonObject("login");
        username = login.get("username").getAsString();
        password = login.get("password").getAsString();
    }
    // To verify the wrong credentials if entered
     @Test
    public void Test1_loginFailure() throws InterruptedException {
         onView(withId(R.id.edt_username)).perform(typeText("test"));
         onView(withId(R.id.edt_password)).perform(typeText("password"), closeSoftKeyboard());
         onView(withId(R.id.btn_login)).perform(click());

     Thread.sleep(2000);
    onView(withText(R.string.message_login_fail)).check(matches(isDisplayed()));
    }

    // To verify the correct credentials if entered
    @Test
    public void Test2_loginSuccess() throws InterruptedException{
    onView(allOf(withId(R.id.edt_username),
                                withClassName(is("android.support.design.widget.TextInputLayout")),
                                isDisplayed()));

//        onView(withId(R.id.edt_username)).perform(typeText("crazydog335"), closeSoftKeyboard());
        onView(withId(R.id.edt_username)).perform(typeText(username), closeSoftKeyboard());
        Thread.sleep(2000);

     onView(allOf(withId(R.id.edt_password),
                                withClassName(is("android.support.design.widget.TextInputLayout")),
                                  isDisplayed()));
//        onView(withId(R.id.edt_password)).perform(typeText("venture"), closeSoftKeyboard());
        onView(withId(R.id.edt_password)).perform(typeText(password), closeSoftKeyboard());
            Thread.sleep(2000);

         onView(allOf(withId(R.id.btn_login),
                      withText("Login"),
                      withId(android.R.id.content),
                      isDisplayed()));
        onView(withId(R.id.btn_login)).perform(click());
        Thread.sleep(2000);
    }

    // Search for "sa", select the 2nd result (via the name, not the index) from the list, then click the call button.
    @Test
        public void Test3_DriverSearch() throws InterruptedException {
        Thread.sleep(2000);
        onView(allOf(withId(R.id.textSearch),
                        withClassName(is("android.support.design.widget.CoordinatorLayout")),
                        isDisplayed()));
        onView(withId(R.id.textSearch)).perform(typeText("sa"), closeSoftKeyboard());
        Thread.sleep(5000);

        onView(withText("Sarah Scott"))
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed())) .perform(click());

               onView(allOf(withId(R.id.fab),
                            withId(android.R.id.content),
                            isDisplayed()));
        onView(withId(R.id.fab)).perform(click());

                }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
