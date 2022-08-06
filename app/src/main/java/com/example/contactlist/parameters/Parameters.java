package com.example.contactlist.parameters;

import android.net.Uri;

public class Parameters {

    public static final String CONTENT_AUTHORITY = "com.example.contactlist.parameters";
    public static final Uri BASE_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    // path name should be similar to your table name
    public static final String PATH_CONTACTS = "contactlist";

    public static final int BD_VERSION=1;
    public static final String DB_NAME = "contacts_db";
    public static final String TABLE_NAME = "contacts_table";


    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone_number";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PICTURE = "picture";
}
