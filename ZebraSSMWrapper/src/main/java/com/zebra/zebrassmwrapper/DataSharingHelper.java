package com.zebra.zebrassmwrapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSharingHelper {

    public static Uri insertData(Context ctx, String key, String value, String packageName, String signature, boolean persistRequired, String secretKey)
    {
        HashMap<String, String> pkgNameSigMap = new HashMap<>();
        pkgNameSigMap.put(packageName, signature);
        return insertData(ctx, key, value,pkgNameSigMap, persistRequired, secretKey);
    }

    public static Uri insertData(Context ctx, String key, String value, Map<String,String> packageNameAndSingnatures, boolean persistRequired, String secretKey)
    {
            Uri cpUri = Uri.parse(Constants.AUTHORITY_DATA);
            ContentValues values = new ContentValues();

            String target_app_package = PackageManagementHelper.getTargetAppString(packageNameAndSingnatures);

            values.put(Constants.COLUMN_TARGET_APP_PACKAGE, target_app_package);
            values.put(Constants.COLUMN_DATA_NAME, key);
            values.put(Constants.COLUMN_DATA_VALUE, value);
            if ((secretKey != null && secretKey.isEmpty() == false)) {
                values.put(Constants.COLUMN_DATA_INPUT_FORM, "2");
                values.put(Constants.COLUMN_DATA_OUTPUT_FORM, "2");
                values.put(Constants.COLUMN_DATA_INPUT_ENCRYPTED_KEY, secretKey);
            } else {
                values.put(Constants.COLUMN_DATA_INPUT_FORM, "1");
                values.put(Constants.COLUMN_DATA_OUTPUT_FORM, "1");
            }
            values.put(Constants.COLUMN_DATA_PERSIST_REQUIRED, persistRequired);

            Uri createdRow = ctx.getContentResolver().insert(cpUri, values);

            return createdRow;
    }

    public static int updateData(Context ctx, String key, String value, String packageName, String signature, boolean persistRequired, String secretKey)
    {
        HashMap<String, String> pkgNameSigMap = new HashMap<>();
        pkgNameSigMap.put(packageName, signature);
        return updateData(ctx, key, value,pkgNameSigMap, persistRequired, secretKey);
    }
    public static int updateData(Context ctx, String key, String value, Map<String,String> packageNameAndSingnatures, boolean persistRequired, String secretKey)
    {
           Uri cpUri = Uri.parse(Constants.AUTHORITY_DATA);
           ContentValues values = new ContentValues();

           String target_app_package = PackageManagementHelper.getTargetAppString(packageNameAndSingnatures);


            values.put(Constants.COLUMN_TARGET_APP_PACKAGE, target_app_package);
            values.put(Constants.COLUMN_DATA_NAME, key);
            values.put(Constants.COLUMN_DATA_VALUE, value);
            if ((secretKey != null && secretKey.isEmpty() == false)) {
                values.put(Constants.COLUMN_DATA_INPUT_FORM, "2");
                values.put(Constants.COLUMN_DATA_OUTPUT_FORM, "2");
                values.put(Constants.COLUMN_DATA_INPUT_ENCRYPTED_KEY, secretKey);
            } else {
                values.put(Constants.COLUMN_DATA_INPUT_FORM, "1");
                values.put(Constants.COLUMN_DATA_OUTPUT_FORM, "1");
            }
            values.put(Constants.COLUMN_DATA_PERSIST_REQUIRED, persistRequired);

            int rowNumbers = ctx.getContentResolver().update(cpUri, values, null, null);
            return rowNumbers;
    }

    public static List<String> queryData(Context ctx, String dataName, boolean persistRequired)
    {
        List<String> valueString = null;
        String currentPackageName = ctx.getPackageName();

        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_DATA + "/[" + currentPackageName + "]");
        String persistData = persistRequired ? "true" : "false";
        String selection = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + currentPackageName + "'" +
                "AND " + Constants.COLUMN_DATA_PERSIST_REQUIRED + " = '" + persistData + "'" +
                "AND " + Constants.COLUMN_DATA_NAME + " = '" + dataName + "'" ;
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(cpUriQuery, null, selection, null, null);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Cursor Query Error: " + e.getMessage());
            return null;
        }

        try {
            if (cursor != null && cursor.moveToFirst()) {
                valueString = new ArrayList<>();
                StringBuilder strBuild = new StringBuilder();
                while (!cursor.isAfterLast()) {
                    String strOriginalAppPackage = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_ORIG_APP_PACKAGE));
                    String strTargetAppPackage = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_TARGET_APP_PACKAGE));
                    String strDataName = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_NAME));
                    String strDataValue = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_VALUE));
                    String strInputForm = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_INPUT_FORM));
                    String strOutputForm = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_OUTPUT_FORM));
                    String strPersistRequired = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_PERSIST_REQUIRED));

                    valueString.add(strDataValue);
                    cursor.moveToNext();
                }
                Log.d(Constants.TAG, "Query data: " + strBuild);
            } else {
                Log.i(Constants.TAG, "No Records Found");
            }
        } catch (Exception e) {
            Log.d(Constants.TAG, "Query data error: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return valueString;
    }

    public static int deleteData(Context ctx, String dataName){

        try
        {
        Uri cpUriDelete = Uri.parse(Constants.AUTHORITY_DATA + "/[" + ctx.getPackageName() + "]");

        String whereClause = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + ctx.getPackageName() + "'" +
                " AND " + Constants.COLUMN_DATA_PERSIST_REQUIRED + " = '" + true + "'" +
                " AND " + Constants.COLUMN_DATA_NAME + " = '" + dataName + "'";

        int rowsDeleted = ctx.getContentResolver().delete(cpUriDelete, whereClause, null);

        if (rowsDeleted > 0) {
            Log.i(Constants.TAG, "Nb rows Deleted: " + rowsDeleted + "for data: " + dataName);
        }
        else {
            Log.i(Constants.TAG, "Nothing to delete for data: " + dataName);
        }
        return rowsDeleted;
        } catch (Exception e) {
            Log.d(Constants.TAG, "Query data error: " + e.getMessage());
            return -1;
        }
    }
}
