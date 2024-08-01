package com.arrowwould.statussaver.photovideo.saveimages.Utility;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.arrowwould.statussaver.photovideo.saveimages.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String SHAREDPREFERENCEFILENAME = "INBHSU-STATUS-SAVER-PREFERENCE";

    private final Context context;
    private final SharedPreferences preferences;

    public Utils(Context activity) {
        context = activity;
        preferences = context.getSharedPreferences(SHAREDPREFERENCEFILENAME, Context.MODE_PRIVATE);
    }
    public void saveBooleanValue(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBooleanValue(String key) {
        return preferences.getBoolean(key, true);
    }

    public void saveStringValue(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getStringValue(String key) {
        return preferences.getString(key, "");
    }


    public void saveInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }


    public void saveLong(String key, long value) {
        preferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }





    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities activeNetwork = connectivityManager.getNetworkCapabilities(network);
            return activeNetwork != null && ((
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                            || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                            || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            ));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            if (nwInfo == null) {
                return false;
            } else {
                return nwInfo.isConnected();
            }
        }
    }



    public boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public boolean isExternalStorageReadOnly() {
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }


    public void openCustomTab(Context context, String str) {
        Uri uri = Uri.parse(str);
        try {
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.items));
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.items));
            CustomTabsIntent customTabsIntent = intentBuilder.build();
            customTabsIntent.launchUrl(context, uri);
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }

    }


    // whatsapp Stat here
    public static final String IMAGE = "image";
    public static String mPath;

    public static void mediaScanner(Context context, String newFilePath, String oldFilePath, String fileType) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{newFilePath + new File(oldFilePath).getName()}, new String[]{fileType}, new MediaScannerConnection.MediaScannerConnectionClient() {
                public void onMediaScannerConnected() {
                }

                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBack(String paramString1, String paramString2) {
        Matcher localMatcher = Pattern.compile(paramString2).matcher(paramString1);
        if (localMatcher.find()) {
            return localMatcher.group(1);
        }
        return "";
    }

    public static boolean download(Context context, String sourceFile) {
        return copyFileInSavedDir(context, sourceFile);
    }

//    static boolean isVideoFile(String path) {
//        String mimeType = URLConnection.guessContentTypeFromName(path);
//        return mimeType != null && mimeType.startsWith("video");
//    }

    static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(Context context, String path) {
        if (path.startsWith("content")) {
            DocumentFile fromTreeUri = DocumentFile.fromSingleUri(context, Uri.parse(path));
            String mimeType = fromTreeUri.getType();
            return mimeType != null && mimeType.startsWith("video");
        } else {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");
        }
    }

    public static boolean copyFileInSavedDir(Context context, String sourceFile) {

        String finalPath;
        if (isVideoFile(context, sourceFile)) {
            finalPath = getDir(context, "Videos").getAbsolutePath();
        } else {
            finalPath = getDir(context, "Images").getAbsolutePath();
        }

        String pathWithName = finalPath + File.separator + new File(sourceFile).getName();
        Uri destUri = Uri.fromFile(new File(pathWithName));

        InputStream is = null;
        OutputStream os = null;
        try {
            Uri uri = Uri.parse(sourceFile);
            is = context.getContentResolver().openInputStream(uri);
            os = context.getContentResolver().openOutputStream(destUri, "w");

            byte[] buffer = new byte[1024];

            int length;
            while ((length = is.read(buffer)) > 0) os.write(buffer, 0, length);

            is.close();
            os.flush();
            os.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(destUri);
            context.sendBroadcast(intent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static File getDir(Context context, String folder) {

        File rootFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_DOWNLOADS + File.separator + context.getResources().getString(R.string.app_name) + File.separator + folder);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        return rootFile;

    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void shareFile(Context context, boolean isVideo, String path) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        if (isVideo) share.setType("Video/*");
        else share.setType("image/*");

        Uri uri;
        if (path.startsWith("content")) {
            uri = Uri.parse(path);
        } else {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", new File(path));
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(share);
    }

    public static void repostWhatsApp(Context context, boolean isVideo, String path) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        if (isVideo) share.setType("Video/*");
        else share.setType("image/*");

        Uri uri;
        if (path.startsWith("content")) {
            uri = Uri.parse(path);
        } else {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", new File(path));
        }
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage("com.whatsapp");
        context.startActivity(share);
    }


}
