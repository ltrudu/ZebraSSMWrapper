package com.zebra.zebrassmwrapper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import androidx.core.content.FileProvider;

public class FileSharingHelper {

    public static Uri insertFile(Context context, File sourceFile, Map<String,String> packageNameAndSingnatures, boolean persist_required) throws FileNotFoundException {
        if(sourceFile.exists() == false || sourceFile.isFile() == false)
        {
            Log.e(Constants.TAG, "SSM Insert File - error: File not found or is directory : " + sourceFile.getPath());
            return null;
        }

        String targetSSMPath = context.getPackageName() + "/" + sourceFile.getName();
        return insertFile(context, sourceFile, targetSSMPath, packageNameAndSingnatures, persist_required);
    }

    public static Uri insertFile(Context context, File sourceFile, String targetSSMPath, Map<String,String> packageNameAndSingnatures, boolean persist_required) throws FileNotFoundException {

        if(sourceFile.exists() == false || sourceFile.isFile() == false)
        {
            Log.e(Constants.TAG, "SSM Insert File - error: File not found or is directory : " + sourceFile.getPath());
            return null;
        }

        String target_app_package = PackageManagementHelper.getTargetAppString(packageNameAndSingnatures);
        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_FILE + context.getPackageName());

        try {

            ContentValues values = new ContentValues();

            values.put(Constants.COLUMN_TARGET_APP_PACKAGE, target_app_package); // If app signature is not used, pass in "null" for "signature"
            values.put(Constants.COLUMN_DATA_NAME, sourceFile.getPath());  // Replace “sourcePath” with the file to deploy located on the device, e.g. "/sdcard/A.txt"
            values.put(Constants.COLUMN_DATA_VALUE, targetSSMPath); // Replace “targetSSMPath” with the package name of the target app that is accessing the deployed file (or retrieve the app package using context.getPackageName()) followed by "/" and the full path of the file, e.g. "context.getPackageName()/A.txt"
            values.put(Constants.COLUMN_DATA_PERSIST_REQUIRED, persist_required);

            Uri createdRow  = context.getContentResolver().insert(cpUriQuery, values);

            Log.i(Constants.TAG, "SSM Insert File: " + createdRow.toString());
            return createdRow;
        } catch(Exception e){

            Log.e(Constants.TAG, "SSM Insert File - error: " + e.getMessage() + "\n\n");
        }
        return null;
    }

    public static Uri insertFileWithURI(Context context, File file, String targetSSMPath, Map<String,String> packageNameAndSingnatures, boolean persist_required)
    {
        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        // The "file" path is passed to the FileProvier() API, which returns the source input uri to deploy the file.
        // Example content uri returned: content://com.zebra.sampleapp.provider/enterprise/usr/A.txt
        context.getApplicationContext().grantUriPermission("com.zebra.securestoragemanager", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION); // Needed to grant permission for SSM to read the uri
        Log.i(Constants.TAG, "File Content Uri "+  contentUri);
        return insertFileFromURI(context, contentUri, targetSSMPath, packageNameAndSingnatures, persist_required);
    }

    public static Uri insertFileFromURI(Context context, Uri contentUri, String targetSSMPath, Map<String,String> packageNameAndSingnatures, boolean persist_required)
    {
        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_FILE + context.getPackageName());
        Log.i(Constants.TAG, "authority  "+  cpUriQuery.toString());
        String target_app_package = PackageManagementHelper.getTargetAppString(packageNameAndSingnatures);

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_TARGET_APP_PACKAGE, target_app_package); // If app signature is not used, pass in "null" for "signature"
            values.put(Constants.COLUMN_DATA_NAME, String.valueOf(contentUri));  // Passes the content uri as a input source
            values.put(Constants.COLUMN_DATA_VALUE, targetSSMPath); // Replace “targetSSMPath” with the package name of the target app that is accessing the deployed file (or retrieve the app package using context.getPackageName()) followed by "/" and the full path of the file, e.g. "context.getPackageName()/A.txt"
            values.put(Constants.COLUMN_DATA_PERSIST_REQUIRED, persist_required);

            Uri createdRow  = context.getContentResolver().insert(cpUriQuery, values);
            Log.i(Constants.TAG, "SSM Insert File: " + createdRow.toString());
            return createdRow;
        } catch(Exception e){
            Log.e(Constants.TAG, "SSM Insert File - error: " + e.getMessage() + "\n\n");
        }
        return null;
    }

    public static List<SSMFileInfo> queryFile(Context context, String targetSSMPath, boolean persist_required) {
        String selection = Constants.COLUMN_DATA_PERSIST_REQUIRED + " = '" + persist_required + "'"+
                " AND " + Constants.COLUMN_TARGET_PATH + "='" + targetSSMPath + "'";
        return querySelection(context, selection, persist_required);
    }

    public static List<SSMFileInfo> queryAllFiles(Context context, String targetSSMPath, boolean persist_required) {
        String selection = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + context.getPackageName() + "'" +
                " AND " + Constants.COLUMN_DATA_PERSIST_REQUIRED + " = '" + persist_required + "'";
        return querySelection(context, selection, persist_required);
    }

    private static List<SSMFileInfo> querySelection(Context context, String selection, boolean persist_required)
    {
        List<SSMFileInfo> foundFiles = null;
        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_FILE);
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(cpUriQuery, null, selection, null, null);

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error: "+ e.getMessage());
        }

        try {
            if(cursor !=null && cursor.moveToFirst()){

                StringBuilder strBuild = new StringBuilder();
                String uriString = null;
                while (!cursor.isAfterLast()) {
                    SSMFileInfo info = SSMFileInfo.createFromCursor(cursor);
                    foundFiles.add(info);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "Query data error: " + e.getMessage());
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return foundFiles;
    }

    public static int deleteFile(Context context, String targetSSMPath, boolean persist_required) {
        String selection = Constants.COLUMN_DATA_PERSIST_REQUIRED + " = '" + persist_required + "'"+
                " AND " + Constants.COLUMN_TARGET_PATH + "='" + targetSSMPath + "'";
        return deleteSelection(context, selection, persist_required);
    }

    public static int deleteAllFiles(Context context, String targetSSMPath, boolean persist_required) {
        String selection = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + context.getPackageName() + "'" +
                " AND " + Constants.COLUMN_DATA_PERSIST_REQUIRED + " = '" + persist_required + "'";
        return deleteSelection(context, selection, persist_required);
    }

    private static int deleteSelection(Context context, String selection, boolean persist_required)
    {
        int deletedItems = -1;
        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_FILE);
        try {
            deletedItems = context.getContentResolver().delete(cpUriQuery, selection, null);
            return deletedItems;
        } catch (Exception e) {
            Log.e(Constants.TAG, "Error: "+ e.getMessage());
        }
        return deletedItems;
    }

    public static int updateFileWithURI(Context context, File file, String targetSSMPath, Map<String,String> packageNameAndSingnatures, boolean persist_required)
    {
        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        // The "file" path is passed to the FileProvier() API, which returns the source input uri to deploy the file.
        // Example content uri returned: content://com.zebra.sampleapp.provider/enterprise/usr/A.txt
        context.getApplicationContext().grantUriPermission("com.zebra.securestoragemanager", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION); // Needed to grant permission for SSM to read the uri
        Log.i(Constants.TAG, "File Content Uri "+  contentUri);
        return updateFileFromURI(context, contentUri, targetSSMPath, packageNameAndSingnatures, persist_required);
    }

    public static int updateFileFromURI(Context context, Uri contentUri, String targetSSMPath, Map<String,String> packageNameAndSingnatures, boolean persist_required)
    {
        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_FILE + context.getPackageName());
        Log.i(Constants.TAG, "authority  "+  cpUriQuery.toString());
        String target_app_package = PackageManagementHelper.getTargetAppString(packageNameAndSingnatures);

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_TARGET_APP_PACKAGE, target_app_package); // If app signature is not used, pass in "null" for "signature"
            values.put(Constants.COLUMN_DATA_NAME, String.valueOf(contentUri));  // Passes the content uri as a input source
            values.put(Constants.COLUMN_DATA_VALUE, targetSSMPath); // Replace “targetSSMPath” with the package name of the target app that is accessing the deployed file (or retrieve the app package using context.getPackageName()) followed by "/" and the full path of the file, e.g. "context.getPackageName()/A.txt"
            values.put(Constants.COLUMN_DATA_PERSIST_REQUIRED, persist_required);

            int updateddRow  = context.getContentResolver().update(cpUriQuery, values, null, null);
            Log.i(Constants.TAG, "SSM Updated rows: " + updateddRow);
            return updateddRow;
        } catch(Exception e){
            Log.e(Constants.TAG, "SSM Insert File - error: " + e.getMessage() + "\n\n");
        }
        return -1;
    }

    public static int updateFile(Context context, File sourceFile, Map<String,String> packageNameAndSingnatures, boolean persist_required) throws FileNotFoundException {
        if(sourceFile.exists() == false || sourceFile.isFile() == false)
        {
            Log.e(Constants.TAG, "SSM Insert File - error: File not found or is directory : " + sourceFile.getPath());
            return -1;
        }

        String targetSSMPath = context.getPackageName() + "/" + sourceFile.getName();
        return updateFile(context, sourceFile, targetSSMPath, packageNameAndSingnatures, persist_required);
    }

    public static int updateFile(Context context, File sourceFile, String targetSSMPath, Map<String,String> packageNameAndSingnatures, boolean persist_required) throws FileNotFoundException {

        if(sourceFile.exists() == false || sourceFile.isFile() == false)
        {
            Log.e(Constants.TAG, "SSM Insert File - error: File not found or is directory : " + sourceFile.getPath());
            return -1;
        }

        String target_app_package = PackageManagementHelper.getTargetAppString(packageNameAndSingnatures);
        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_FILE + context.getPackageName());

        try {

            ContentValues values = new ContentValues();

            values.put(Constants.COLUMN_TARGET_APP_PACKAGE, target_app_package); // If app signature is not used, pass in "null" for "signature"
            values.put(Constants.COLUMN_DATA_NAME, sourceFile.getPath());  // Replace “sourcePath” with the file to deploy located on the device, e.g. "/sdcard/A.txt"
            values.put(Constants.COLUMN_DATA_VALUE, targetSSMPath); // Replace “targetSSMPath” with the package name of the target app that is accessing the deployed file (or retrieve the app package using context.getPackageName()) followed by "/" and the full path of the file, e.g. "context.getPackageName()/A.txt"
            values.put(Constants.COLUMN_DATA_PERSIST_REQUIRED, persist_required);

            int updatedRow  = context.getContentResolver().update(cpUriQuery, values, null,null);

            Log.i(Constants.TAG, "SSM Updated rows: " + updatedRow);
            return updatedRow;
        } catch(Exception e){

            Log.e(Constants.TAG, "SSM Insert File - error: " + e.getMessage() + "\n\n");
        }
        return -1;
    }
}
