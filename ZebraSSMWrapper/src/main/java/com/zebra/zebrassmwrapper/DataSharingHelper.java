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

    public static class QueryResult
    {
        public String strOriginalAppPackage    = null;
        public String strTargetAppPackage      = null;
        public String strDataName              = null;
        public String strDataValue             = null;
        public String strInputForm             = null;
        public String strOutputForm            = null;
        public String strPersistRequired       = null;
    }

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
            values.put(Constants.COLUMN_DATA_PERSIST_REQUIRED, persistRequired ? "true" : "false");
            values.put(Constants.COLUMN_DATA_VALUE, value);
            if ((secretKey != null && secretKey.isEmpty() == false)) {
                values.put(Constants.COLUMN_DATA_INPUT_FORM, "2");
                values.put(Constants.COLUMN_DATA_OUTPUT_FORM, "2");
                values.put(Constants.COLUMN_DATA_INPUT_ENCRYPTED_KEY, secretKey);
            } else {
                values.put(Constants.COLUMN_DATA_INPUT_FORM, "1");
                values.put(Constants.COLUMN_DATA_OUTPUT_FORM, "1");
            }
        values.put(Constants.COLUMN_DATA_TYPE,"1");
        values.put(Constants.COLUMN_MULTI_INSTANCE_REQUIRED, "false");

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
            values.put(Constants.COLUMN_DATA_PERSIST_REQUIRED, persistRequired ? "true" : "false");

            int rowNumbers = ctx.getContentResolver().update(cpUri, values, null, null);
            return rowNumbers;
    }

    public static List<QueryResult> queryData(Context ctx, String dataName, boolean persistRequired)
    {
        List<QueryResult> results = null;
        String currentPackageName = ctx.getPackageName();

        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_DATA + "/[" + currentPackageName + "]");
        String persistData = persistRequired ? "true" : "false";
        String selection = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + currentPackageName + "'" +
                " AND " + Constants.COLUMN_DATA_PERSIST_REQUIRED + " = '" + persistData + "'" +
                " AND " + Constants.COLUMN_DATA_NAME + " = '" + dataName + "'" ;
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(cpUriQuery, null, selection, null, null);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Cursor Query Error: " + e.getMessage());
            return null;
        }

        try {
            if (cursor != null && cursor.moveToFirst()) {
                results = new ArrayList<>();
                StringBuilder strBuild = new StringBuilder();
                while (!cursor.isAfterLast()) {
                    QueryResult result = new QueryResult();
                    result.strOriginalAppPackage = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_ORIG_APP_PACKAGE));
                    result.strTargetAppPackage = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_TARGET_APP_PACKAGE));
                    result.strDataName = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_NAME));
                    result.strDataValue = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_VALUE));
                    result.strInputForm = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_INPUT_FORM));
                    result.strOutputForm = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_OUTPUT_FORM));
                    result.strPersistRequired = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_PERSIST_REQUIRED));

                    results.add(result);
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
        return results;
    }

    public static List<QueryResult> queryAllData(Context ctx, boolean persistRequired)
    {
        List<QueryResult> results = null;
        String currentPackageName = ctx.getPackageName();

        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_DATA + "/[" + currentPackageName + "]");
        String persistData = persistRequired ? "true" : "false";
        String selection = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + currentPackageName + "'" +
                "AND " + Constants.COLUMN_DATA_PERSIST_REQUIRED + " = '" + persistData + "'";//*/
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(cpUriQuery, null, selection, null, null);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Cursor Query Error: " + e.getMessage());
            return null;
        }

        try {
            if (cursor != null && cursor.moveToFirst()) {
                results = new ArrayList<>();
                StringBuilder strBuild = new StringBuilder();
                while (!cursor.isAfterLast()) {
                    QueryResult result = new QueryResult();
                    result.strOriginalAppPackage = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_ORIG_APP_PACKAGE));
                    result.strTargetAppPackage = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_TARGET_APP_PACKAGE));
                    result.strDataName = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_NAME));
                    result.strDataValue = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_VALUE));
                    result.strInputForm = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_INPUT_FORM));
                    result.strOutputForm = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_OUTPUT_FORM));
                    result.strPersistRequired = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_PERSIST_REQUIRED));

                    results.add(result);
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
        return results;
    }



    public static List<QueryResult> queryAllData(Context ctx)
    {
        List<QueryResult> results = null;
        String currentPackageName = ctx.getPackageName();

        Uri cpUriQuery = Uri.parse(Constants.AUTHORITY_DATA + "/[" + currentPackageName + "]");
        String selection = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + currentPackageName + "'";
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(cpUriQuery, null, selection, null, null);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Cursor Query Error: " + e.getMessage());
            return null;
        }

        try {
            if (cursor != null && cursor.moveToFirst()) {
                results = new ArrayList<>();
                StringBuilder strBuild = new StringBuilder();
                while (!cursor.isAfterLast()) {
                    QueryResult result = new QueryResult();
                    result.strOriginalAppPackage = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_ORIG_APP_PACKAGE));
                    result.strTargetAppPackage = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_TARGET_APP_PACKAGE));
                    result.strDataName = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_NAME));
                    result.strDataValue = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_VALUE));
                    result.strInputForm = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_INPUT_FORM));
                    result.strOutputForm = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_OUTPUT_FORM));
                    result.strPersistRequired = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATA_PERSIST_REQUIRED));

                    results.add(result);
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
        return results;
    }


    public static int deleteAllData(Context ctx)
    {
        try
        {
            Uri cpUriDelete = Uri.parse(Constants.AUTHORITY_DATA + "/[" + ctx.getPackageName() + "]");

            String whereClause = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + ctx.getPackageName() + "'";

            int rowsDeleted = ctx.getContentResolver().delete(cpUriDelete, whereClause, null);

            if (rowsDeleted > 0) {
                Log.i(Constants.TAG, "Nb rows Deleted: " + rowsDeleted);
            }
            else {
                Log.i(Constants.TAG, "Nothing to delete");
            }
            return rowsDeleted;
        } catch (Exception e) {
            Log.d(Constants.TAG, "Query data error: " + e.getMessage());
            return -1;
        }
    }


    public static int deleteAllData(Context ctx, boolean persist)
    {
        try
        {
            Uri cpUriDelete = Uri.parse(Constants.AUTHORITY_DATA + "/[" + ctx.getPackageName() + "]");
            String persistData = persist ? "true" : "false";

            String whereClause = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + ctx.getPackageName() + "'" + "AND " + Constants.COLUMN_DATA_PERSIST_REQUIRED + " = '" + persistData + "'";;

            int rowsDeleted = ctx.getContentResolver().delete(cpUriDelete, whereClause, null);

            if (rowsDeleted > 0) {
                Log.i(Constants.TAG, "Nb rows Deleted: " + rowsDeleted);
            }
            else {
                Log.i(Constants.TAG, "Nothing to delete");
            }
            return rowsDeleted;
        } catch (Exception e) {
            Log.d(Constants.TAG, "Query data error: " + e.getMessage());
            return -1;
        }
    }

    public static int deleteData(Context ctx, String dataName){

        try
        {
        Uri cpUriDelete = Uri.parse(Constants.AUTHORITY_DATA + "/[" + ctx.getPackageName() + "]");

        String whereClause = Constants.COLUMN_TARGET_APP_PACKAGE + " = '" + ctx.getPackageName() + "'" +
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
