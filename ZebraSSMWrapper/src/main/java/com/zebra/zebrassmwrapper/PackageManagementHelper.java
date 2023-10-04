package com.zebra.zebrassmwrapper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.util.Base64;
import java.util.Map;

public class PackageManagementHelper {
    // TODO: you can put your own signature here (at your own risk), as a String, not base64 encoded.
    public static Signature apkCertificate = null;

    public static String getTargetAppString(Map<String,String> packageNameAndSingnatures)
    {
        String target_app_package = "{\"pkgs_sigs\":[";
        int index = 0;
        for (Map.Entry<String, String> entry : packageNameAndSingnatures.entrySet()) {
            String entryKey = entry.getKey();
            String entryValue = entry.getValue();
            target_app_package += "{\"pkg\":\"" + entryKey + "\",\"sig\":\"" + entryValue + "\"}";
            index++;
            if (packageNameAndSingnatures.size() > 1 && index != packageNameAndSingnatures.size()) {
                target_app_package += ",";
            }
        }
        target_app_package += "]}";
        return target_app_package;
    }

    public static String getPackageName(Context context, IResultCallbacks iResultCallbacks)
    {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
        } catch (PackageManager.NameNotFoundException e) {
            if (iResultCallbacks != null) {
                iResultCallbacks.onError("PackageManager.NameNotFoundException : Couldn't retrieve packageInfo from package manager, check if the context passed is correct.",e.getMessage());
                return null;
            }
        }
        String strPackageName = packageInfo.packageName;
        if (iResultCallbacks != null) {
            iResultCallbacks.onDebugStatus("Package name: " + strPackageName);
        }
        if (iResultCallbacks != null) {
            iResultCallbacks.onSuccess("Success", strPackageName);
        }
        return strPackageName;
    }

    public static String getPackagePath(Context context, IResultCallbacks iResultCallbacks)
    {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
        } catch (PackageManager.NameNotFoundException e) {
            if (iResultCallbacks != null) {
                iResultCallbacks.onError("PackageManager.NameNotFoundException : Couldn't retrieve packageInfo from package manager, check if the context passed is correct.",e.getMessage());
                return null;
            }
        }
        String path = context.getApplicationInfo().sourceDir;
        if (iResultCallbacks != null) {
            iResultCallbacks.onDebugStatus("Package path: " + path);
        }
        if (iResultCallbacks != null) {
            iResultCallbacks.onSuccess("Success", path);
        }
        return path;
    }

    public static String getSignature(Context context, IResultCallbacks iResultCallbacks)
    {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
        } catch (PackageManager.NameNotFoundException e) {
            if (iResultCallbacks != null) {
                iResultCallbacks.onError("PackageManager.NameNotFoundException : Couldn't retrieve packageInfo from package manager, check if the context passed is correct.",e.getMessage());
                return null;
            }
        }
        // Use custom signature if it has been set by the user
        Signature sig = apkCertificate;

        // Let's check if we have a custom certificate
        if (sig == null) {
            // Nope, we will get the first apk signing certificate that we find
            // You can copy/paste this snippet if you want to provide your own
            // certificate
            // TODO: use the following code snippet to extract your custom certificate if necessary
            final Signature[] arrSignatures = packageInfo.signingInfo.getApkContentsSigners();
            if (arrSignatures == null || arrSignatures.length == 0) {
                if (iResultCallbacks != null) {
                    iResultCallbacks.onError("Error : Package has no signing certificates... how's that possible ?","");
                    return null;
                }
            }
            sig = arrSignatures[0];
        }

        /*
         * Get the X.509 certificate.
         */
        final byte[] rawCert = sig.toByteArray();

        // Get the certificate as a base64 string
        String encoded = Base64.getEncoder().encodeToString(rawCert);
        if (iResultCallbacks != null) {
            iResultCallbacks.onSuccess("Success", encoded);
        }
        return encoded;
    }


}
