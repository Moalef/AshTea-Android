package mgks.os.swv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultLauncher;

public class FileProcessing {

    private final Activity activity;
    private final ActivityResultLauncher<Intent> resultLauncher;

    public FileProcessing(Activity activity, ActivityResultLauncher<Intent> resultLauncher) {
        this.activity = activity;
        this.resultLauncher = resultLauncher;
    }

    public boolean onShowFileChooser(
            WebView webView,
            ValueCallback<Uri[]> filePathCallback,
            WebChromeClient.FileChooserParams fileChooserParams) {

        if (!SWVContext.ASWP_FUPLOAD) {
            return false;
        }

        SWVContext.asw_file_path = filePathCallback;

        String[] acceptTypes = fileChooserParams.getAcceptTypes();

        // Use Android's system file picker.
        // This allows users to select existing images from their gallery
        // without requiring CAMERA permission.
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);

        // Determine the requested MIME type.
        if (acceptTypes != null && acceptTypes.length > 0) {

            if (acceptTypes.length == 1 && !acceptTypes[0].isEmpty()) {
                contentSelectionIntent.setType(acceptTypes[0]);
            } else {
                contentSelectionIntent.setType("*/*");
                contentSelectionIntent.putExtra(
                        Intent.EXTRA_MIME_TYPES,
                        acceptTypes
                );
            }

        } else {
            // If the website doesn't specify a type,
            // allow the user to select any file.
            contentSelectionIntent.setType("*/*");
        }

        // Allow multiple files if enabled in the app configuration.
        if (SWVContext.ASWP_MULFILE) {
            contentSelectionIntent.putExtra(
                    Intent.EXTRA_ALLOW_MULTIPLE,
                    true
            );
        }

        if (resultLauncher != null) {
            resultLauncher.launch(contentSelectionIntent);
        } else {
            if (SWVContext.asw_file_path != null) {
                SWVContext.asw_file_path.onReceiveValue(null);
                SWVContext.asw_file_path = null;
            }

            return false;
        }

        return true;
    }
}