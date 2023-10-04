package com.zebra.zebrassmwrapper;

import android.database.Cursor;

import androidx.annotation.NonNull;

public class SSMFileInfo {
    String secure_file_uri;
    String secure_file_name;
    String secure_is_dir;
    String secure_file_crc;
    String secure_file_persist;

    @NonNull
    @Override
    public String toString() {
        String content = "";
        content += "secure_file_uri = " + secure_file_uri + "\n";
        content += "secure_file_name = " + secure_file_name + "\n";
        content += "secure_is_dir = " + secure_is_dir + "\n";
        content += "secure_file_crc = " + secure_file_crc + "\n";
        content += "secure_file_persist = " + secure_file_persist;
        return content;
    }

    public static SSMFileInfo createFromCursor(Cursor cursor)
    {
        SSMFileInfo info = new SSMFileInfo();
        info.secure_file_uri = cursor.getString (cursor.getColumnIndexOrThrow(  Constants.SECURE_FILE_URI       ));
        info.secure_file_name = cursor.getString(cursor.getColumnIndexOrThrow(  Constants.SECURE_FILE_NAME      ));
        info.secure_is_dir = cursor.getString(cursor.getColumnIndexOrThrow(     Constants.SECURE_IS_DIR     ));
        info.secure_file_crc = cursor.getString(cursor.getColumnIndexOrThrow(   Constants.SECURE_FILE_CRC       ));
        return info;
    }
}
