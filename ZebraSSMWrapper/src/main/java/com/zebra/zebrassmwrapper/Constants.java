package com.zebra.zebrassmwrapper;

public class Constants {
    protected static final String COLUMN_DATA_TYPE = "data_type";
    protected static final String COLUMN_TARGET_APP_PACKAGE = "target_app_package";
    protected static final String COLUMN_ORIG_APP_PACKAGE = "orig_app_package";
    protected static final String COLUMN_DATA_NAME = "data_name";
    protected static final String COLUMN_DATA_VALUE = "data_value";
    protected static final String COLUMN_DATA_INPUT_FORM = "data_input_form";
    protected static final String COLUMN_DATA_OUTPUT_FORM = "data_output_form";
    protected static final String COLUMN_DATA_INPUT_ENCRYPTED_KEY = "data_input_encrypted_key";
    protected static final String COLUMN_DATA_PERSIST_REQUIRED = "data_persist_required";
    protected static final String COLUMN_MULTI_INSTANCE_REQUIRED = "multi_instance_required";
    protected static final String COLUMN_TARGET_PATH = "target_path";

    protected static final String  SECURE_FILE_URI    = "secure_file_uri";
    protected static final String  SECURE_FILE_NAME   = "secure_file_name";
    protected static final String  SECURE_IS_DIR      = "secure_is_dir";
    protected static final String  SECURE_FILE_CRC    = "secure_file_crc";
    protected static final String  SECURE_FILE_PERSIST   = "secure_file_persist";

    protected static final String TAG = "ZSSMWrapper";
    protected static final String AUTHORITY_DATA = "content://com.zebra.securestoragemanager.securecontentprovider/data";

    protected static final String AUTHORITY_FILE = "content://com.zebra.securestoragemanager.securecontentprovider/files/";

    protected static final String ACTION_FILE_DEPLOYMENT_NOTIFICATION  = "com.zebra.configFile.action.notify";
    protected static final String ACTION_SIGNATURE_PUBLIC_KEY  = "com.zebra.zebrassmwrapper.action.signature.publickey";
    protected static final String ACTION_SIGNATURE_EXCHANGE  = "com.zebra.zebrassmwrapper.action.signature.exchange";
    protected static final String ACTION_SIGNATURE_DESTINATION_ACKNOWLEDGE_RECEIVED = "com.zebra.zebrassmwrapper.action.signature.acknowledgereceived";
    protected static final String EXTRA_SIGNATURE_EXCHANGE_PACKAGE_NAME  = "packagename";
    protected static final String EXTRA_SIGNATURE_EXCHANGE_SIGNATURE  = "signature";
    protected static final String EXTRA_SIGNATURE_EXCHANGE_PUBLIC_KEY  = "public_key";
}
