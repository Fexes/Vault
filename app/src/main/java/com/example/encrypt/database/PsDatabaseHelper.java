package com.example.encrypt.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by yanjie.xu on 2017/7/5.
 * 本地数据库维护类
 */

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
        public static final String APPS = "apps";
        public static final String CONTACTS = "contacts";
        public static final String RAW_CONTACTS = "raw_contacts";
        public static final String DATA = "data";
        public static final String SMS = "sms";
        public static final String CALLRECORD = "call_record";
        public static final String FILES = "files";
        public static final String Video = "video";
    }

    public interface AppsColumns {
        public static final String _ID = "id";
        public static final String PACKAGE_NAME = "package_name";
        public static final String APP_NAME = "app_name";
    }

    public interface ContactsColumns {
        public static final String _ID = "id";
        public static final String HAS_PHONE_NUMBER = "has_phone_number";
        public static final String PHOTO_FILE_ID = "photo_file_id";
        public static final String CONTACT_NAME = "display_name";
    }


    public interface RawContactsColumns {
        public static final String _ID = "id";
        public static final String CONTACT_ID = "contact_id";
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
    }


    public interface DataColumns {
        public static final String _ID = "id";
        public static final String MIME_TYPE = "mime_type";
        public static final String TYPE_NAME = "type_name";
        public static final String RAW_CONTACT_ID = "raw_contact_id";
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        public static final String DATA5 = "data5";
        public static final String DATA6 = "data6";
        public static final String DATA7 = "data7";
        public static final String DATA8 = "data8";
        public static final String DATA9 = "data9";
        public static final String DATA10 = "data10";
        public static final String DATA11 = "data11";
        public static final String DATA12 = "data12";
        public static final String DATA13 = "data13";
        public static final String DATA14 = "data14";
        public static final String DATA15 = "data15";
    }

    public interface WhatsApp{
        public static final String PROFILE_TYPE = "vnd.android.cursor.item/vnd.com.whatsapp.profile";
        public static final String VOIP_CALL_TYPE = "vnd.android.cursor.item/vnd.com.whatsapp.voip.call";
        public static final String VIDEO_CALL_TYPE = "vnd.android.cursor.item/vnd.com.whatsapp.video.call";
    }


    public interface SmsColumns {
        public static final String _ID = "id";
        public static final String THREAD_ID = "thread_id";
        public static final String SMS_DATE = "sms_date";
        public static final String SMS_PERSON = "sms_person";
        public static final String SMS_TYPE = "sms_type";
        public static final String SMS_ADDRESS = "sms_address";
        public static final String SMS_BODY = "sms_body";
        public static final String SMS_READ = "sms_read";
    }

    public interface CallRecordClumns {
        public static final String _ID = "id";
        public static final String DATE = "date";
        public static final String NUMBER = "number";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String DURATION = "duration";
        public static final String PHOTO_FILE_ID = "photo_file_id";
    }


    public interface FilesClumns {
        public static final String _ID = "_id";
        public static final String _DATA = "_data";
        public static final String _SOURCE_DATA = "_source_data";
        public static final String _SIZE = "_size";
        public static final String _DISPLAY_NAME = "_display_name";
        public static final String TITLE = "title";
        public static final String DATE_ADDED = "date_added";
        public static final String MIME_TYPE = "mime_type";
        public static final String BUCKET_ID = "bucket_id";
        public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
    }

    public interface VideoClumns {
        public static final String _ID = "_id";
        public static final String DATA = "_data";
        public static final String DISPLAY_NAME = "_display_name";
        public static final String SIZE = "_size";
        public static final String MIME_TYPE = "mime_type";
        public static final String DATE_ADDED = "date_added";
        public static final String TITLE = "title";
        public static final String ALBUM = "album";
        public static final String BUCKET_ID = "bucket_id";
        public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
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

    /**
     * 创建数据表
     *
     * @param sqLiteDatabase
     */
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