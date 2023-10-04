package com.zebra.usbpopupremovalhelper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.util.Base64;

public class PackageManagementHelper {
    // TODO: you can put your own signature here (at your own risk), as a String, not base64 encoded.
    public static Signature apkCertificate = null;

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
        return encoded;
    }


}
