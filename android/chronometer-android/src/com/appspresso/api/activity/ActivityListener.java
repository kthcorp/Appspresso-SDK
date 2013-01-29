/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 앱스프레소 앱을 감싸는 안드로이드 액티비티({@link android.app.Activity})의 기본 동작을 오버라이드하기 위한 리스너.
 * <p>
 * 이 인터페이스의 일부 메소드만 필요하다면, 이 인터페이스 전체를 구현하는 대신 {@link ActivityAdapter} 클래스를 상속하고 해당 메소드만 오버라이드.
 * <p>
 * 
 * <pre>
 * public class MyPlugin implements AxPlugin {
 * 	private ActivityListener activityListener;
 * 
 * 	public void activate(AxRuntimeContext runtimeContext) {
 * 			activityListener = new ActivityListener() {
 * 				...
 * 			};
 * 			runtimeContext.addActivityListener(activityListener);
 * 		}
 * 
 * 	public void deactivate(AxRuntimeContext runtimeContext) {
 * 		runtimeContext.removeActivityListener(activityListener);
 * 	}
 * }
 * </pre>
 * 
 * @see com.appspresso.api.AxRuntimeContext#addActivityListener(ActivityListener)
 * @see com.appspresso.api.AxRuntimeContext#removeActivityListener(ActivityListener)
 * @see ActivityAdapter
 * @see android.app.Activity
 * @see http://developer.android.com/reference/android/app/Activity.html
 * @version 1.0
 */
public interface ActivityListener {

    //
    // lifeycle events
    //

    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides you with a
     * Bundle containing the activity's previously frozen state, if there was one. Always followed
     * by onStart().
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @param savedInstanceState
     * @see Activity#onCreate(android.os.Bundle)
     */
    void onActivityCreate(Activity activity, Bundle savedInstanceState);

    /**
     * Called after your activity has been stopped, prior to it being started again. Always followed
     * by onStart().
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @see Activity#onRestart()
     */
    void onActivityRestart(Activity activity);

    /**
     * Called when the activity is becoming visible to the user. Followed by onResume() if the
     * activity comes to the foreground, or onStop() if it becomes hidden.
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @see Activity#onStart()
     */
    void onActivityStart(Activity activity);

    /**
     * Called when the activity will start interacting with the user. At this point your activity is
     * at the top of the activity stack, with user input going to it. Always followed by onPause().
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @see Activity#onResume()
     */
    void onActivityResume(Activity activity);

    /**
     * Called when the system is about to start resuming a previous activity. This is typically used
     * to commit unsaved changes to persistent data, stop animations and other things that may be
     * consuming CPU, etc. Implementations of this method must be very quick because the next
     * activity will not be resumed until this method returns. Followed by either onResume() if the
     * activity returns back to the front, or onStop() if it becomes invisible to the user.
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @see Activity#onPause()
     */
    void onActivityPause(Activity activity);

    /**
     * Called when the activity is no longer visible to the user, because another activity has been
     * resumed and is covering this one. This may happen either because a new activity is being
     * started, an existing one is being brought in front of this one, or this one is being
     * destroyed. Followed by either onRestart() if this activity is coming back to interact with
     * the user, or onDestroy() if this activity is going away.
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @see Activity#onStop()
     */
    void onActivityStop(Activity activity);

    /**
     * The final call you receive before your activity is destroyed. This can happen either because
     * the activity is finishing (someone called finish() on it, or because the system is
     * temporarily destroying this instance of the activity to save space. You can distinguish
     * between these two scenarios with the isFinishing() method.
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @see Activity#onDestroy()
     */
    void onActivityDestroy(Activity activity);

    /**
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @see Activity#onRestoreInstanceState(android.os.Bundle)
     */
    void onRestoreInstanceState(Activity activity, Bundle savedInstanceState);

    /**
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @see Activity#onSaveInstanceState(android.os.Bundle)
     */
    void onSaveInstanceState(Activity activity, Bundle outState);

    //
    // other events
    //

    /**
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @return 처리했으면 true, 무시했으면 false
     * @see Activity#onActivityResult(int,int,android.content.Intent)
     */
    boolean onActivityResult(Activity activity, int requestCode, int resultCode,
            Intent imageReturnedIntent);

    /**
     * Called when the activity has detected the user's press of the back key. The default
     * implementation simply finishes the current activity, but you can override this to do whatever
     * you want.
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @return 처리했으면 true, 무시했으면 false
     * @see Activity#onBackPressed()
     */
    boolean onBackPressed(Activity activity);

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu
     * items in to menu. This is only called once, the first time the options menu is displayed. To
     * update the menu every time it is displayed, see onPrepareOptionsMenu(Menu). The default
     * implementation populates the menu with standard system menu items. These are placed in the
     * CATEGORY_SYSTEM group so that they will be correctly ordered with application-defined menu
     * items. Deriving classes should always call through to the base implementation. You can safely
     * hold on to menu (and any items created from it), making modifications to it as desired, until
     * the next time onCreateOptionsMenu() is called. When you add items to the menu, you can
     * implement the Activity's onOptionsItemSelected(MenuItem) method to handle them there.
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @param menu The options menu in which you place your items.
     * @return 처리했으면 true, 무시했으면 false
     * @see Activity#onCreateOptionsMenu(android.view.Menu)
     */
    boolean onCreateOptionsMenu(Activity activity, Menu menu);

    /**
     * Prepare the Screen's standard options menu to be displayed. This is called right before the
     * menu is shown, every time it is shown. You can use this method to efficiently enable/disable
     * items or otherwise dynamically modify the contents. The default implementation updates the
     * system menu items based on the activity's state. Deriving classes should always call through
     * to the base class implementation.
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @param menu The options menu as last shown or first initialized by onCreateOptionsMenu()
     * @return 처리했으면 true, 무시했으면 false
     * @see Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    boolean onPrepareOptionsMenu(Activity activity, Menu menu);

    /**
     * This hook is called whenever an item in your options menu is selected. The default
     * implementation simply returns false to have the normal processing happen (calling the item's
     * Runnable or sending a message to its Handler as appropriate). You can use this method for any
     * items for which you would like to do processing without those other facilities. Derived
     * classes should call through to the base class for it to perform the default menu handling.
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @param item The menu item that was selected.
     * @return 처리했으면 true, 무시했으면 false
     * @see Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    boolean onOptionsItemSelected(Activity activity, MenuItem item);

    /**
     * This is called for activities that set launchMode to "singleTop" in their package, or if a
     * client used the FLAG_ACTIVITY_SINGLE_TOP flag when calling startActivity(Intent). In either
     * case, when the activity is re-launched while at the top of the activity stack instead of a
     * new instance of the activity being started, onNewIntent() will be called on the existing
     * instance with the Intent that was used to re-launch it. An activity will always be paused
     * before receiving a new intent, so you can count on onResume() being called after this method.
     * Note that getIntent() still returns the original Intent. You can use setIntent(Intent) to
     * update it to this new Intent.
     * 
     * @param activity 앱스프레소 앱을 감싸는 액티비티
     * @param intent
     * 
     * @see Activity#onNewIntent(android.content.Intent)
     */
    void onNewIntent(Activity activity, Intent intent);

    /**
     * Called when the current Window of the activity gains or loses focus. This is the best
     * indicator of whether this activity is visible to the user. The default implementation clears
     * the key tracking state, so should always be called.
     * 
     * Note that this provides information about global focus state, which is managed independently
     * of activity lifecycles. As such, while focus changes will generally have some relation to
     * lifecycle changes (an activity that is stopped will not generally get window focus), you
     * should not rely on any particular order between the callbacks here and those in the other
     * lifecycle methods such as onResume().
     * 
     * As a general rule, however, a resumed activity will have window focus... unless it has
     * displayed other dialogs or popups that take input focus, in which case the activity itself
     * will not have focus when the other windows have it. Likewise, the system may display
     * system-level windows (such as the status bar notification panel or a system alert) which will
     * temporarily take window input focus without pausing the foreground activity.
     * 
     * @param hasFocus Whether the window of this activity has focus.
     */
    void onWindowFocusChanged(boolean hasFocus);

}
