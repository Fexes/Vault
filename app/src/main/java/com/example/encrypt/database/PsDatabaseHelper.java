package com.example.encrypt.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class PsDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "PsDatabaseHelper";


    private static final String SQL_CREATE_APPS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.APPS + " (" +
            AppsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AppsColumns.APP_NAME + " TEXT, " +
            AppsColumns.PACKAGE_NAME + " TEXT" +
            ");";


    private static final String SQL_CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.CONTACTS + " (" +
            ContactsColumns._ID + " INTEGER," +
            ContactsColumns.HAS_PHONE_NUMBER + " INTEGER, " +
            ContactsColumns.CONTACT_NAME + " TEXT, " +
            ContactsColumns.PHOTO_FILE_ID + " INTEGER" +
            ");";


    private static final String SQL_CREATE_RAWCONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.RAW_CONTACTS + " (" +
            RawContactsColumns._ID + " INTEGER," +
            RawContactsColumns.CONTACT_ID + " INTEGER, " +
            RawContactsColumns.ACCOUNT_NAME + " TEXT, " +
            RawContactsColumns.ACCOUNT_TYPE + " TEXT" +
            ");";


    private static final String SQL_CREATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.DATA + " (" +
            DataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataColumns.RAW_CONTACT_ID + " INTEGER, " +
            DataColumns.MIME_TYPE + " TEXT, " +
            DataColumns.TYPE_NAME + " TEXT, " +
            DataColumns.DATA1 + " TEXT, " +
            DataColumns.DATA2 + " TEXT, " +
            DataColumns.DATA3 + " TEXT, " +
            DataColumns.DATA4 + " TEXT, " +
            DataColumns.DATA5 + " TEXT, " +
            DataColumns.DATA6 + " TEXT, " +
            DataColumns.DATA7 + " TEXT, " +
            DataColumns.DATA8 + " TEXT, " +
            DataColumns.DATA9 + " TEXT, " +
            DataColumns.DATA10 + " TEXT, " +
            DataColumns.DATA11 + " TEXT, " +
            DataColumns.DATA12 + " TEXT, " +
            DataColumns.DATA13 + " TEXT, " +
            DataColumns.DATA14 + " TEXT, " +
            DataColumns.DATA15 + " TEXT" +
            ");";


    private static final String SQL_CREATE_SMS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.SMS + " (" +
            SmsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            SmsColumns.THREAD_ID + " INTEGER," +
            SmsColumns.SMS_ADDRESS + " TEXT, " +
            SmsColumns.SMS_BODY + " TEXT," +
            SmsColumns.SMS_DATE + " TEXT," +
            SmsColumns.SMS_PERSON + " TEXT," +
            SmsColumns.SMS_TYPE + " TEXT," +
            SmsColumns.SMS_READ + " TEXT" +
            ");";


    private static final String SQL_CREATE_CALLRECORD_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.CALLRECORD + " (" +
            CallRecordClumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CallRecordClumns.DATE + " INTEGER, " +
            CallRecordClumns.NUMBER + " TEXT, " +
            CallRecordClumns.NAME + " TEXT, " +
            CallRecordClumns.TYPE + " INTEGER, " +
            CallRecordClumns.DURATION + " INTEGER, " +
            CallRecordClumns.PHOTO_FILE_ID + " INTEGER " +
            ");";


    private static final String SQL_CREATE_FILES_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.FILES + " (" +
            FilesClumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FilesClumns._DATA + " TEXT, " +
            FilesClumns._SOURCE_DATA + " TEXT, " +
            FilesClumns._SIZE + " INTEGER, " +
            FilesClumns._DISPLAY_NAME + " TEXT, " +
            FilesClumns.TITLE + " TEXT, " +
            FilesClumns.DATE_ADDED + " INTEGER, " +
            FilesClumns.MIME_TYPE + " TEXT, " +
            FilesClumns.BUCKET_ID + " TEXT, " +
            FilesClumns.BUCKET_DISPLAY_NAME + " TEXT, " +
            FilesClumns.WIDTH + " INTEGER, " +
            FilesClumns.HEIGHT + " INTEGER " +
            ");";


    private static final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.Video + " (" +
            VideoClumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            VideoClumns.DATA + " TEXT, " +
            VideoClumns.DISPLAY_NAME + " TEXT, " +
            VideoClumns.SIZE + " INTEGER, " +
            VideoClumns.MIME_TYPE + " TEXT, " +
            VideoClumns.DATE_ADDED + " INTEGER, " +
            VideoClumns.TITLE + " TEXT, " +
            VideoClumns.ALBUM + " TEXT, " +
            VideoClumns.BUCKET_ID + " TEXT, " +
            VideoClumns.BUCKET_DISPLAY_NAME + " TEXT, " +
            VideoClumns.WIDTH + " INTEGER, " +
            VideoClumns.HEIGHT + " INTEGER " +
            ");";

    private final static int VERSION = 1;
    private final static String DB_NAME = "privatespace.db";
    private Context mContext;
    private static PsDatabaseHelper sSingleton;

    public interface Tables {
        String APPS = "apps";
        String CONTACTS = "contacts";
        String RAW_CONTACTS = "raw_contacts";
        String DATA = "data";
        String SMS = "sms";
        String CALLRECORD = "call_record";
        String FILES = "files";
        String Video = "video";
    }

    public interface AppsColumns {
        String _ID = "id";
        String PACKAGE_NAME = "package_name";
        String APP_NAME = "app_name";
    }

    public interface ContactsColumns {
        String _ID = "id";
        String HAS_PHONE_NUMBER = "has_phone_number";
        String PHOTO_FILE_ID = "photo_file_id";
        String CONTACT_NAME = "display_name";
    }


    public interface RawContactsColumns {
        String _ID = "id";
        String CONTACT_ID = "contact_id";
        String ACCOUNT_NAME = "account_name";
        String ACCOUNT_TYPE = "account_type";
    }


    public interface DataColumns {
        String _ID = "id";
        String MIME_TYPE = "mime_type";
        String TYPE_NAME = "type_name";
        String RAW_CONTACT_ID = "raw_contact_id";
        String DATA1 = "data1";
        String DATA2 = "data2";
        String DATA3 = "data3";
        String DATA4 = "data4";
        String DATA5 = "data5";
        String DATA6 = "data6";
        String DATA7 = "data7";
        String DATA8 = "data8";
        String DATA9 = "data9";
        String DATA10 = "data10";
        String DATA11 = "data11";
        String DATA12 = "data12";
        String DATA13 = "data13";
        String DATA14 = "data14";
        String DATA15 = "data15";
    }



    public interface SmsColumns {
        String _ID = "id";
        String THREAD_ID = "thread_id";
        String SMS_DATE = "sms_date";
        String SMS_PERSON = "sms_person";
        String SMS_TYPE = "sms_type";
        String SMS_ADDRESS = "sms_address";
        String SMS_BODY = "sms_body";
        String SMS_READ = "sms_read";
    }

    public interface CallRecordClumns {
        String _ID = "id";
        String DATE = "date";
        String NUMBER = "number";
        String NAME = "name";
        String TYPE = "type";
        String DURATION = "duration";
        String PHOTO_FILE_ID = "photo_file_id";
    }


    public interface FilesClumns {
        String _ID = "_id";
        String _DATA = "_data";
        String _SOURCE_DATA = "_source_data";
        String _SIZE = "_size";
        String _DISPLAY_NAME = "_display_name";
        String TITLE = "title";
        String DATE_ADDED = "date_added";
        String MIME_TYPE = "mime_type";
        String BUCKET_ID = "bucket_id";
        String BUCKET_DISPLAY_NAME = "bucket_display_name";
        String WIDTH = "width";
        String HEIGHT = "height";
    }

    public interface VideoClumns {
        String _ID = "_id";
        String DATA = "_data";
        String DISPLAY_NAME = "_display_name";
        String SIZE = "_size";
        String MIME_TYPE = "mime_type";
        String DATE_ADDED = "date_added";
        String TITLE = "title";
        String ALBUM = "album";
        String BUCKET_ID = "bucket_id";
        String BUCKET_DISPLAY_NAME = "bucket_display_name";
        String WIDTH = "width";
        String HEIGHT = "height";
    }

    public PsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    public static synchronized PsDatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new PsDatabaseHelper(context, DB_NAME, null, VERSION);
            Log.i(TAG, "PsDatabaseHelper new object");
        }
        return sSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "onCreate");
        createTable(sqLiteDatabase);
    }

    private void createTable(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE_APPS_TABLE);


        sqLiteDatabase.execSQL(SQL_CREATE_CONTACTS_TABLE);


        sqLiteDatabase.execSQL(SQL_CREATE_RAWCONTACTS_TABLE);


        sqLiteDatabase.execSQL(SQL_CREATE_DATA_TABLE);


        sqLiteDatabase.execSQL(SQL_CREATE_SMS_TABLE);


        sqLiteDatabase.execSQL(SQL_CREATE_CALLRECORD_TABLE);


        sqLiteDatabase.execSQL(SQL_CREATE_FILES_TABLE);


        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public SQLiteDatabase getDatabase(boolean writable) {
        return writable ? getWritableDatabase() : getReadableDatabase();
    }
}