package petros.efthymiou.groovy

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.adevinta.android.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.internal.matcher.DrawableMatcher.Companion.withDrawable
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule






/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PlayListFeature
{
    /*

    What is ActivityTestRule?
ActivityTestRule provides functional testing of a single Activity.
ActivityTestRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity)
When launchActivity is set to true in the constructor, the Activity under test will be launched before each test annotated with Test and before methods annotated with Before, and it will be terminated after the test is completed and methods annotated with After are finished.
Also you can manually launch the activity with launchActivity(Intent).
@Rule
public ActivityTestRule<SampleActivity>
activityRule = new ActivityTestRule<>(SampleActivity.class, true, false);

Intent i = new Intent(activityRule.getActivity(), SampleActivity.class);
activityRule.launchActivity(i);
Don’t forget to add the dependency;
androidTestImplementation ‘com.android.support.test:rules:1.0.2’
     */



    //DEPRECATED
    //val mActivityRule = ActivityTestRule(MainActivity::class.java)


    var mActivityRule = ActivityScenarioRule(MainActivity::class.java)
    @Rule get  //!!!!TELL espresso TO INITIALISE ACTIVITY AND KILL IT AT END OF TEST

    @Test
    fun displayScreenTitle()
    {

        /**** DEFAULT CODE - AS CREATED BY ANDROID STUDIO

        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("petros.efthymiou.groovy", appContext.packageName)
        ***/

        //FROM BARISTA LIBRARY
        //MEANS - IS THIS VIEW DISPLAYED????????
        assertDisplayed("Playlists")

    }

    @Test
    fun displayListOfPlayLists()
    {

        /**************** NOTE ***************
         TEST INITIALLY FAILS ON ATTEMPT TO CALL
         getCount() ON NULL OBJECT
         ************************************/


        //Wait for UI to display - then see whether we get the remote data
        Thread.sleep(4000);


        //FROM BARISTA LIBRARY
        assertRecyclerViewItemCount(R.id.playlists_lists, 10)

        /*NOTE: CANNOT USE THIS alone ON A LIST - BECAUSE all ITEMS WILL HAVE THE SAME
                id:
        onView(withId(R.id.xxxxxxx))

        ...SO HAVE TO USE SEVERAL CONDITIONS IN THE ViewMatcher

        ...AS FOLLOWS

        */


        //FROM espresso


       onView( allOf(
                      withId(R.id.playlist_name),
                      isDescendantOfA(nthChildOf(withId(R.id.playlists_lists), 0))
                    )

             ).
            check(matches(withText("Hard Rock Cafe"))).
            check(matches(isDisplayed()))
        //-----------------------------------------------
        onView( allOf(withId(R.id.playlist_category), isDescendantOfA(nthChildOf(withId(R.id.playlists_lists), 0)))
            /*END OF MATCHER LIST*/).
        check(matches(withText("rock"))).
        check(matches(isDisplayed()))
        //-----------------------------------------------
        onView( allOf(withId(R.id.playlist_image), isDescendantOfA(nthChildOf(withId(R.id.playlists_lists), 0)))
            /*END OF MATCHER LIST*/).
        check(matches(withDrawable(R.mipmap.playlist))).  /*NOTE: withDrawable IS A barista library method*/
        check(matches(isDisplayed()))
    }



    //UTILITY METHOD for grabbing specified child
    fun nthChildOf(parentMatcher: Matcher<View>, childPosition: Int): Matcher<View>
    {
        return object : TypeSafeMatcher<View>()
        {
            override fun describeTo(description: Description)
            {
                description.appendText("position $childPosition of parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean
            {
                if (view.parent !is ViewGroup) return false
                val parent = view.parent as ViewGroup

                return (parentMatcher.matches(parent)
                        && parent.childCount > childPosition
                        && parent.getChildAt(childPosition) == view)
            }
        }
    }

}