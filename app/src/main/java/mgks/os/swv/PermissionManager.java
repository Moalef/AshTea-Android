package mgks.os.swv;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private static final String TAG = "PermissionManager";

    public static final int INITIAL_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 102;

    private final Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Requests configured permissions that are actually needed.
     */
    public void requestInitialPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permissionGroup : SWVContext.ASWP_REQUIRED_PERMISSIONS) {
            switch (permissionGroup) {

                case "NOTIFICATIONS":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                            && !isNotificationPermissionGranted()) {

                        permissionsToRequest.add(
                                Manifest.permission.POST_NOTIFICATIONS
                        );
                    }
                    break;

                /*
                 * Gallery/file selection is handled by Android's system
                 * ACTION_GET_CONTENT intent in FileProcessing.java.
                 *
                 * Therefore, we do NOT request storage or media permissions
                 * just to let users select images from the gallery.
                 */
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            Log.d(
                    TAG,
                    "Requesting initial permissions: " + permissionsToRequest
            );

            ActivityCompat.requestPermissions(
                    activity,
                    permissionsToRequest.toArray(new String[0]),
                    INITIAL_REQUEST_CODE
            );
        } else {
            Log.d(TAG, "No initial permissions need to be requested.");
        }
    }

    /**
     * Checks whether notification permission has been granted.
     */
    public boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }

        // Notification runtime permission did not exist before Android 13.
        return true;
    }
}