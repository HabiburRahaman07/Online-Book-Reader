package com.example.onlinebookreader;

import android.app.Activity;
import android.content.Intent;

public class NavigationHelper {

    public static void navigate(Activity currentActivity, int selectedItemId) {
        if (selectedItemId == R.id.navigation_home) {
            if (!(currentActivity instanceof MainActivity)) {
                startNewActivity(currentActivity, MainActivity.class);
            }
        } else if (selectedItemId == R.id.navigation_search) {
            if (!(currentActivity instanceof SearchActivity)) {
                startNewActivity(currentActivity, SearchActivity.class);
            }
        } else if (selectedItemId == R.id.navigation_library) {
            if (!(currentActivity instanceof LibraryActivity)) {
                startNewActivity(currentActivity, LibraryActivity.class);
            }
        } else if (selectedItemId == R.id.navigation_account) {
            if (!(currentActivity instanceof AccountActivity)) {
                startNewActivity(currentActivity, AccountActivity.class);
            }
        }
    }

    private static void startNewActivity(Activity currentActivity, Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(currentActivity, targetActivity);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
}
