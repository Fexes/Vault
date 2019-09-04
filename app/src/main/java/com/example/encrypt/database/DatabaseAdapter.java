package com.example.encrypt.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.example.encrypt.bean.AppInfo;
import com.example.encrypt.photo.ImageItem;
import com.example.encrypt.video.VideoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanjie.xu on 2017/7/5.
 * 本地数据库操作帮助类
 */

public class DatabaseAdapter {
    /**
     * add by xyj 20170904
     * 查询联系人sql
     */
    private static final String SQL_QUERY_CONTACT = "SELECT " +
            PsDatabaseHelper.ContactsColumns._ID + "," +
            PsDatabaseHelper.ContactsColumns.CONTACT_NAME + "," +
            PsDatabaseHelper.ContactsColumns.HAS_PHONE_NUMBER + "," +
            PsDatabaseHelper.ContactsColumns.PHOTO_FILE_ID +
            " FROM " + PsDatabaseHelper.Tables.CONTACTS;

    /**
     * add by xyj 20170904
     * 查询联系人通过id
     */
    private static final String SQL_QUERY_CONTACT_BY_CONTACTID = "SELECT " +
            PsDatabaseHelper.ContactsColumns._ID + "," +
            PsDatabaseHelper.ContactsColumns.CONTACT_NAME + "," +
            PsDatabaseHelper.ContactsColumns.HAS_PHONE_NUMBER + "," +
            PsDatabaseHelper.ContactsColumns.PHOTO_FILE_ID +
            " FROM " + PsDatabaseHelper.Tables.CONTACTS + " WHERE " +
            PsDatabaseHelper.ContactsColumns._ID + "=?";

    /**
     * add by xyj 20170904
     * 通过contact_id查询raw联系人
     */
    private static final String SQL_QUERY_RAW_CONTACT_BY_CONTACTID = "SELECT " +
            PsDatabaseHelper.RawContactsColumns._ID + "," +
            PsDatabaseHelper.RawContactsColumns.CONTACT_ID + "," +
            PsDatabaseHelper.RawContactsColumns.ACCOUNT_NAME + "," +
            PsDatabaseHelper.RawContactsColumns.ACCOUNT_TYPE +
            " FROM " + PsDatabaseHelper.Tables.RAW_CONTACTS + " WHERE " +
            PsDatabaseHelper.RawContactsColumns.CONTACT_ID + "=?";

    /**
     * add by xyj 20170921
     * 通过photoFileId查询Photo数据
     */
    private static final String SQL_QUERY_PHOTO_BY_PHOTOID = "SELECT " +
            PsDatabaseHelper.DataColumns.MIME_TYPE + "," +
            PsDatabaseHelper.DataColumns.DATA14 + "," +
            PsDatabaseHelper.DataColumns.DATA15 +
            " FROM " + PsDatabaseHelper.Tables.DATA + " WHERE " +
            PsDatabaseHelper.DataColumns.DATA14 + "=?";

    /**
     * add by xyj 20170904
     * 通过raw_contact_id查询联系人data数据
     */
    private static final String SQL_QUERY_DATA_BY_RAWCONTACTID = "SELECT " +
            PsDatabaseHelper.DataColumns._ID + "," +
            PsDatabaseHelper.DataColumns.MIME_TYPE + "," +
            PsDatabaseHelper.DataColumns.TYPE_NAME + "," +
            PsDatabaseHelper.DataColumns.RAW_CONTACT_ID + "," +
            PsDatabaseHelper.DataColumns.DATA1 + "," +
            PsDatabaseHelper.DataColumns.DATA2 + "," +
            PsDatabaseHelper.DataColumns.DATA3 + "," +
            PsDatabaseHelper.DataColumns.DATA4 + "," +
            PsDatabaseHelper.DataColumns.DATA5 + "," +
            PsDatabaseHelper.DataColumns.DATA6 + "," +
            PsDatabaseHelper.DataColumns.DATA7 + "," +
            PsDatabaseHelper.DataColumns.DATA8 + "," +
            PsDatabaseHelper.DataColumns.DATA9 + "," +
            PsDatabaseHelper.DataColumns.DATA10 + "," +
            PsDatabaseHelper.DataColumns.DATA11 + "," +
            PsDatabaseHelper.DataColumns.DATA12 + "," +
            PsDatabaseHelper.DataColumns.DATA13 + "," +
            PsDatabaseHelper.DataColumns.DATA14 + "," +
            PsDatabaseHelper.DataColumns.DATA15 +
            " FROM " + PsDatabaseHelper.Tables.DATA + " WHERE " +
            PsDatabaseHelper.DataColumns.RAW_CONTACT_ID + "=?";

    /**
     * add by xyj 20170904
     * 通过phone查询联系人data数据
     */
    private static final String QUERY_DATA_BY_PHONE = "SELECT " +
            PsDatabaseHelper.DataColumns.RAW_CONTACT_ID + "," +
            PsDatabaseHelper.DataColumns.MIME_TYPE + "," +
            PsDatabaseHelper.DataColumns.DATA1 + " FROM " +
            PsDatabaseHelper.Tables.DATA + " WHERE " +
            PsDatabaseHelper.DataColumns.DATA1 + "=? and " +
            PsDatabaseHelper.DataColumns.MIME_TYPE + "=?";

    /**
     * add by xyj 20170904
     * 通过rawContactId查询联系人data数据
     */
    private static final String QUERY_DATA_BY_RAWCONTACTID = "SELECT " +
            PsDatabaseHelper.DataColumns.RAW_CONTACT_ID + "," +
            PsDatabaseHelper.DataColumns.MIME_TYPE + "," +
            PsDatabaseHelper.DataColumns.DATA1 + " FROM " +
            PsDatabaseHelper.Tables.DATA + " WHERE " +
            PsDatabaseHelper.DataColumns.RAW_CONTACT_ID + "=? and " +
            PsDatabaseHelper.DataColumns.MIME_TYPE + "=?";

    private static final String TAG = "DatabaseAdapter";
    private static final String VCF_FILE_PATH = "/test.vcf";
    private Context mContext;
    private static PsDatabaseHelper mDbHelper;

    public DatabaseAdapter(Context context) {
        this.mContext = context;
        mDbHelper = PsDatabaseHelper.getInstance(context);
    }

    /**
     * App操作
     */
    public void addApp(AppInfo appInfo) {
        ContentValues values = new ContentValues();
        values.put(PsDatabaseHelper.AppsColumns.APP_NAME, appInfo.getAppName());
        values.put(PsDatabaseHelper.AppsColumns.PACKAGE_NAME, appInfo.getPackageName());
        insert(PsDatabaseHelper.Tables.APPS, null, values);
    }

    public List<AppInfo> getApps(PackageManager packageManager) {
        String sql = "SELECT " +
                PsDatabaseHelper.AppsColumns._ID + "," +
                PsDatabaseHelper.AppsColumns.APP_NAME + "," +
                PsDatabaseHelper.AppsColumns.PACKAGE_NAME +
                " FROM " + PsDatabaseHelper.Tables.APPS;
        Log.i(TAG, "sql = " + sql);
        List<AppInfo> list = new ArrayList<AppInfo>();
        Cursor cursor = query(sql, null);
        try {
            while (cursor.moveToNext()) {
                AppInfo appInfo = new AppInfo();
                appInfo.setId(cursor.getInt(0));
                appInfo.setAppName(cursor.getString(1));
                appInfo.setPackageName(cursor.getString(2));
                try {

                    appInfo.setAppIcon(packageManager.getApplicationIcon(appInfo.getPackageName()));
                } catch (PackageManager.NameNotFoundException e) {
                    String whereClause = PsDatabaseHelper.AppsColumns.PACKAGE_NAME + "=?";
                    String[] whereArgs = {appInfo.getPackageName()};
                    delete(PsDatabaseHelper.Tables.APPS, whereClause, whereArgs);
                    continue;
                }
                list.add(appInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }


    public Cursor getAppCursor() {
        String sql = "SELECT " +
                PsDatabaseHelper.AppsColumns._ID + "," +
                PsDatabaseHelper.AppsColumns.APP_NAME + "," +
                PsDatabaseHelper.AppsColumns.PACKAGE_NAME +
                " FROM " + PsDatabaseHelper.Tables.APPS;
        Log.i(TAG, "sql = " + sql);
        Cursor cursor = query(sql, null);
        return cursor;
    }

    public void deleteAppByPackageName(String packageName) {

        String whereClause = PsDatabaseHelper.AppsColumns.PACKAGE_NAME + "=?";
        String[] whereArgs = {packageName};
        delete(PsDatabaseHelper.Tables.APPS, whereClause, whereArgs);
    }


    public boolean isExistsApp(PackageInfo activityInfo) {
        boolean isExitsts;
        String sql = "select * from " + PsDatabaseHelper.Tables.APPS +
                " where " + PsDatabaseHelper.AppsColumns.PACKAGE_NAME + "=?";
        Cursor cursor = query(sql, new String[]{activityInfo.packageName});
        isExitsts = cursor.moveToFirst();
        cursor.close();
        return isExitsts;
    }

    /**
     * Contact操作
     */





//                .replaceAll(" ", "").replaceAll("-", "").trim());
//        insert(PsDatabaseHelper.Tables.CONTACTS, null, values);
//    }
//
//    public void addContacts(List<People> peoples, boolean isVcf) {
//        Log.i(TAG, "[addContacts]" + isVcf);
//        if (isVcf) {
//            List<People> peopleList = ImportExportUtils.readData(VCF_FILE_PATH);
//            for (People people : peoples){
//                peopleList.add(people);
//                Log.i(TAG, "[addContacts] peopleList = " + peopleList.size());
//            }
//            //将联系人写入文件
//            if (peopleList.size() > 0){
//                ImportExportUtils.writeData(peopleList, VCF_FILE_PATH);
//            }else {
//                ImportExportUtils.removeDataFile(VCF_FILE_PATH);
//            }
//        } else {
//            peoples.forEach((People people) -> addContact(people));
//        }
//    }
//    public void deleteContactsById(int id) {
//        delete(PsDatabaseHelper.Tables.CONTACTS, PsDatabaseHelper.ContactsColumns._ID + "=?", new String[]{String.valueOf(id)});
//    }

/*
注释联系人相关    */
/**
     * add by xyj 20170904
     * 添加单个Contact到数据库
     *
     * @param info
     *//*

    public void addContact(ContactInfo info) {
        ContentValues contactValues = new ContentValues();
        contactValues.put(PsDatabaseHelper.ContactsColumns._ID, info.getContactId());
        contactValues.put(PsDatabaseHelper.ContactsColumns.CONTACT_NAME, info.getDisplayName());
        contactValues.put(PsDatabaseHelper.ContactsColumns.HAS_PHONE_NUMBER, info.getHasPhoneNumber());
        contactValues.put(PsDatabaseHelper.ContactsColumns.PHOTO_FILE_ID, info.getPhotoFileId());
        insert(PsDatabaseHelper.Tables.CONTACTS, null, contactValues);
        List<ContactInfo.RawContactInfo> rawContactInfos = info.getRawContactInfos();
        for (ContactInfo.RawContactInfo rawContactInfo : rawContactInfos) {
            ContentValues rawContactInfoValues = new ContentValues();
            rawContactInfoValues.put(PsDatabaseHelper.RawContactsColumns._ID, rawContactInfo.rawContactId);
            rawContactInfoValues.put(PsDatabaseHelper.RawContactsColumns.CONTACT_ID, rawContactInfo.contactId);
            rawContactInfoValues.put(PsDatabaseHelper.RawContactsColumns.ACCOUNT_NAME, rawContactInfo.accountName);
            rawContactInfoValues.put(PsDatabaseHelper.RawContactsColumns.ACCOUNT_TYPE, rawContactInfo.accountType);
            insert(PsDatabaseHelper.Tables.RAW_CONTACTS, null, rawContactInfoValues);
            List<ContactInfo.DataInfo> dataInfos = rawContactInfo.dataInfos;
            for (ContactInfo.DataInfo dataInfo : dataInfos) {
                ContentValues dataInfoValues = new ContentValues();
                dataInfoValues.put(PsDatabaseHelper.DataColumns.MIME_TYPE, dataInfo.mimeType);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.TYPE_NAME, dataInfo.typeName);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.RAW_CONTACT_ID, dataInfo.rawContactId);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA1, dataInfo.data1);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA2, dataInfo.data2);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA3, dataInfo.data3);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA4, dataInfo.data4);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA5, dataInfo.data5);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA6, dataInfo.data6);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA7, dataInfo.data7);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA8, dataInfo.data8);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA9, dataInfo.data9);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA10, dataInfo.data10);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA11, dataInfo.data11);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA12, dataInfo.data12);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA13, dataInfo.data13);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA14, dataInfo.data14);
                dataInfoValues.put(PsDatabaseHelper.DataColumns.DATA15, dataInfo.data15);
                insert(PsDatabaseHelper.Tables.DATA, null, dataInfoValues);
            }
        }
    }

    */
/**
     * add by xyj 20170904
     * 添加多个Contact到数据库
     *
     * @param contactInfoList
     *//*

    public void addContact(List<ContactInfo> contactInfoList) {
        for (ContactInfo contactInfo : contactInfoList) {
            addContact(contactInfo);
        }
    }

//    public void deleteContactsByPhone(String phone, boolean isVcf) {
//        if (isVcf) {
//            List<People> peopleList = ImportExportUtils.readData(VCF_FILE_PATH);
//            for (int i = 0; i < peopleList.size(); i++) {
//                //从内存list删除需要删除的联系人
//                if (peopleList.get(i).getPhoneNum().equals(phone)) peopleList.remove(i);
//            }
//            //将操作完的联系人重新写入文件
//            if (peopleList.size() > 0) {
//                ImportExportUtils.writeData(peopleList, VCF_FILE_PATH);
//            } else {
//                ImportExportUtils.removeDataFile(VCF_FILE_PATH);
//            }
//        } else {
//            delete(PsDatabaseHelper.Tables.CONTACTS, PsDatabaseHelper.ContactsColumns.CONTACT_NUMBER + "=?", new String[]{String.valueOf(phone)});
//        }
//    }

    */
/**
     * 根据Id删除联系人
     *
     * @param id
     *//*

    public void deleteContactById(int id) {
        ContactInfo info = getContactById(id);
        Log.i(TAG, "[deleteContactById] getContactById(id) = " + info.toString());
        if (info != null) {
            delete(PsDatabaseHelper.Tables.CONTACTS, PsDatabaseHelper.ContactsColumns._ID + "=?", new String[]{String.valueOf(id)});
        }
        if (info.getRawContactInfos().size() > 0) {
            for (ContactInfo.RawContactInfo rawContactInfo : info.getRawContactInfos()) {
                int rawContactId = rawContactInfo.rawContactId;
                delete(PsDatabaseHelper.Tables.RAW_CONTACTS, PsDatabaseHelper.RawContactsColumns.CONTACT_ID + "=?",
                        new String[]{String.valueOf(id)});
                delete(PsDatabaseHelper.Tables.DATA, PsDatabaseHelper.DataColumns.RAW_CONTACT_ID + "=?",
                        new String[]{String.valueOf(rawContactInfo.rawContactId)});
            }
        }
    }

    */
/**
     * add by xyj 20170926
     * 获取联系人总数
     *
     * @return
     *//*

    public int getContactCounts() {
        String sql = "SELECT " +
                PsDatabaseHelper.ContactsColumns._ID +
                " FROM " + PsDatabaseHelper.Tables.CONTACTS;
        Cursor query = query(sql, null);
        return query.getCount();
    }

    */
/**
     * add by xyj 20170904
     * 根据contactId获取联系人
     *
     * @param id
     * @return
     *//*

    public ContactInfo getContactById(int id) {
        ContactInfo info = new ContactInfo();
        Cursor cursor = query(SQL_QUERY_CONTACT_BY_CONTACTID, new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            info.setContactId(cursor.getInt(cursor.getColumnIndex(PsDatabaseHelper.ContactsColumns._ID)));
            info.setDisplayName(cursor.getString(cursor.getColumnIndex(PsDatabaseHelper.ContactsColumns.CONTACT_NAME)));
            info.setHasPhoneNumber(cursor.getInt(cursor.getColumnIndex(PsDatabaseHelper.ContactsColumns.HAS_PHONE_NUMBER)));
            info.setPhotoFileId(cursor.getInt(cursor.getColumnIndex(PsDatabaseHelper.ContactsColumns.PHOTO_FILE_ID)));
            info.setPhotoData(getPhotoDataById(info.getPhotoFileId()));
            //根据contact_id查询所有RawContacts
            getRawContact(info, null, null);
            Log.i(TAG, "[getContacts] people.toString = " + info.toString());
        }
        cursor.close();
        return info;
    }

    */
/**
     * 获取简单的联系人数据
     *
     * @return
     *//*

    public List<ContactInfo> getPhoneContacts() {
        return getContacts(PsDatabaseHelper.DataColumns.MIME_TYPE + "=?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE});
    }

    */
/**
     * 分页分批获取联系人数据
     * @param pageSize        每页最大的数目
     * @param currentOffset   当前的偏移量
     * @return
     *//*

    public List<ContactInfo> getContactsByPage(int pageSize, int currentOffset) {
        return getContacts(" limit " + pageSize + " offset " + currentOffset, null);
    }

    */
/**
     * add by xyj 20170904
     * 获取所有联系人
     *
     * @return
     *//*

    public List<ContactInfo> getContacts(String selection, String[] selectionArgs) {
        List<ContactInfo> list = new ArrayList<>();
        String sql = SQL_QUERY_CONTACT;
        if (selection != null) {
            sql = SQL_QUERY_CONTACT + selection;
        }
        Cursor cursor = query(sql, null);
        while (cursor.moveToNext()) {
            ContactInfo info = new ContactInfo();
            info.setContactId(cursor.getInt(cursor.getColumnIndex(PsDatabaseHelper.ContactsColumns._ID)));
            info.setDisplayName(cursor.getString(cursor.getColumnIndex(PsDatabaseHelper.ContactsColumns.CONTACT_NAME)));
            info.setHasPhoneNumber(cursor.getInt(cursor.getColumnIndex(PsDatabaseHelper.ContactsColumns.HAS_PHONE_NUMBER)));
            info.setPhotoFileId(cursor.getInt(cursor.getColumnIndex(PsDatabaseHelper.ContactsColumns.PHOTO_FILE_ID)));
            //根据contact_id查询所有RawContacts
            info.setPhotoData(getPhotoDataById(info.getPhotoFileId()));
            getRawContact(info, selection, selectionArgs);
            Log.i(TAG, "[getContacts] info.toString = " + info.toString());
            list.add(info);
        }
        cursor.close();

        return list;
    }

    */
/**
     * add by xyj 20170904
     * 根据contact_id查询所有RawContacts
     *
     * @param info
     *//*

    private void getRawContact(ContactInfo info, String selection, String[] selectionArgs) {
        Cursor cursor = query(SQL_QUERY_RAW_CONTACT_BY_CONTACTID, new String[]{String.valueOf(info.getContactId())});
        while (cursor.moveToNext()) {
            ContactInfo.RawContactInfo rawContactInfo = new ContactInfo.RawContactInfo();
            rawContactInfo.rawContactId = cursor.getInt(cursor.getColumnIndex(PsDatabaseHelper.RawContactsColumns._ID));
            rawContactInfo.contactId = cursor.getInt(cursor.getColumnIndex(PsDatabaseHelper.RawContactsColumns.CONTACT_ID));
            rawContactInfo.accountName = cursor.getString(cursor.getColumnIndex(PsDatabaseHelper.RawContactsColumns.ACCOUNT_NAME));
            rawContactInfo.accountType = cursor.getString(cursor.getColumnIndex(PsDatabaseHelper.RawContactsColumns.ACCOUNT_TYPE));
            //根据rawContactId查询所有data
            getData(rawContactInfo, selection, selectionArgs);
            info.getRawContactInfos().add(rawContactInfo);
        }

        cursor.close();
    }

    */
/**
     * 通过id获取Photo数据
     *
     * @param photoId
     *//*

    public byte[] getPhotoDataById(int photoId) {
        String strBuff = null;
        byte[] buffArray = null;
        Cursor dataCursor = query(SQL_QUERY_PHOTO_BY_PHOTOID, new String[]{String.valueOf(photoId)});
        while (dataCursor.moveToNext()) {
            strBuff = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA15));
        }
        dataCursor.close();
        if (strBuff != null) {
            buffArray = Base64.decode(strBuff, Base64.DEFAULT);
        }
        return buffArray;
    }

    */
/**
     * add by xyj 20170904
     * 根据rawContactId查询所有data
     *
     * @param rawContactInfo
     *//*

    private void getData(ContactInfo.RawContactInfo rawContactInfo, String selection, String[] selectionArgs) {
        String sql = SQL_QUERY_DATA_BY_RAWCONTACTID;
        String[] strings = new String[]{String.valueOf(rawContactInfo.rawContactId)};
        if (selection != null && selectionArgs != null) {
            sql = SQL_QUERY_DATA_BY_RAWCONTACTID + " AND " + selection;
            int length = selectionArgs.length;
            strings = new String[]{
                    String.valueOf(rawContactInfo.rawContactId),
                    selectionArgs[length - 1]};
        }
        Cursor dataCursor = query(sql, strings);
        while (dataCursor.moveToNext()) {
            ContactInfo.DataInfo dataInfo = new ContactInfo.DataInfo();
            dataInfo.id = dataCursor.getInt(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns._ID));
            dataInfo.mimeType = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.MIME_TYPE));
            dataInfo.typeName = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.TYPE_NAME));
            dataInfo.rawContactId = dataCursor.getInt(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.RAW_CONTACT_ID));
            dataInfo.data1 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA1));
            dataInfo.data2 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA2));
            dataInfo.data3 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA3));
            dataInfo.data4 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA4));
            dataInfo.data5 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA5));
            dataInfo.data6 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA6));
            dataInfo.data7 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA7));
            dataInfo.data8 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA8));
            dataInfo.data9 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA9));
            dataInfo.data10 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA10));
            dataInfo.data11 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA11));
            dataInfo.data12 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA12));
            dataInfo.data13 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA13));
            dataInfo.data14 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA14));
            dataInfo.data15 = dataCursor.getString(dataCursor.getColumnIndex(PsDatabaseHelper.DataColumns.DATA15));
            rawContactInfo.dataInfos.add(dataInfo);
        }

        dataCursor.close();
    }

//    public List<People> getContacts(boolean isVcf) {
//        List<People> list = new ArrayList<>();
//        if (isVcf) {
//            list.addAll(ImportExportUtils.readData(VCF_FILE_PATH));
//        } else {
//            list.addAll(getContacts());
//        }
//        return list;
//    }

    public List<ContactInfo> getContacts(boolean isVcf) {
//        if (isVcf) {
//            list.addAll(ImportExportUtils.readData(VCF_FILE_PATH));
//        } else {
        return getContacts(null, null);
//        }
    }

    */
/**
     * 根据条件和参数查找是否存在联系人
     * 如果存在返回cursor
     *
     * @param selection
     * @param arg
     * @return
     *//*

    public Cursor getContactCursor(String selection, String arg) {
        if (arg == null) {
            return null;
        }

        String formartArg = PhoneNumUtil.formatNumber(arg);
        Log.i(TAG, "[getContactCursor] number = " + arg + "formartArg = " + formartArg);
        String[] columnNames = new String[]{
                PsDatabaseHelper.DataColumns.RAW_CONTACT_ID,
                PsDatabaseHelper.ContactsColumns.CONTACT_NAME};
        MatrixCursor cursor = new MatrixCursor(columnNames);

        Cursor cursorPhone = query(QUERY_DATA_BY_PHONE,
                new String[]{formartArg, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE});
        while (cursorPhone.moveToNext()) {
            int rawcontactid = cursorPhone.getInt(cursorPhone.getColumnIndex(PsDatabaseHelper.DataColumns.RAW_CONTACT_ID));
            String phoneNum = cursorPhone.getString(cursorPhone.getColumnIndex(PsDatabaseHelper.DataColumns.DATA1));
            String name = "";
            Log.i(TAG, "[getContactCursor] rawcontactid = " + rawcontactid + " phoneNum = " + phoneNum);
            if (rawcontactid > 0 && phoneNum != null && phoneNum.equals(formartArg)) {
                Cursor cursorName = query(QUERY_DATA_BY_RAWCONTACTID,
                        new String[]{String.valueOf(rawcontactid), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
                while (cursorName.moveToNext()) {
                    name = cursorName.getString(cursorName.getColumnIndex(PsDatabaseHelper.DataColumns.DATA1));
                    if (TextUtils.isEmpty(name)) {
                        name = phoneNum;
                        Log.i(TAG, "[getContactCursor] data1 = " + phoneNum +
                                " cursorName.count = " + cursorName.getCount() + "cursor.count = " + cursor.getCount() +
                                "cursor.columncount = " + cursor.getColumnCount());
                    }
                }
            }
            Object[] curArray = new Object[]{rawcontactid, name};//显示姓名
            cursor.addRow(curArray);//电话号码
        }
        return cursor;
    }

    public Cursor getContactCursor(String selection, String[] selectionArgs, boolean isVcf) {
//        if (isVcf) {
//            return getCursorByPeoples(selectionArgs[0], getContacts(true));
//        } else {
        return getContactCursor(selection, selectionArgs[0]);
//        }
    }

//    private Cursor getCursorByPeoples(String arg, List<People> contacts) {
//        String[] columnNames = new String[]{
//                PsDatabaseHelper.ContactsColumns._ID,
//                PsDatabaseHelper.ContactsColumns.RAWCONTACT_ID,
//                PsDatabaseHelper.ContactsColumns.CONTACT_NAME,
//                PsDatabaseHelper.ContactsColumns.CONTACT_NUMBER};
//        MatrixCursor cursor = new MatrixCursor(columnNames);
//        for (People people : contacts) {
//            if (people.getPhoneNum().replaceAll(" ", "").replaceAll("-", "").trim()
//                    .equals(arg.replaceAll(" ", "").replaceAll("-", "").trim())) {
//                Log.e(TAG, "getCursorByPeoples: arg = " + arg);
//                cursor.addRow(people.getColumnData());
//            }
//        }
//        return cursor;
//    }

*/


/*
注释Sms相关
    */
/**
     * sms操作 20170707 add by wangmeng
     *//*

    public void addSms(SmsInfo sms) {
        ContentValues values = new ContentValues();
        values.put(PsDatabaseHelper.SmsColumns.THREAD_ID, String.valueOf(sms.getThread_id()));
        values.put(PsDatabaseHelper.SmsColumns.SMS_ADDRESS, sms.getAddress());
        values.put(PsDatabaseHelper.SmsColumns.SMS_BODY, sms.getBody());
        values.put(PsDatabaseHelper.SmsColumns.SMS_DATE, Long.valueOf(sms.getDate()));
        values.put(PsDatabaseHelper.SmsColumns.SMS_PERSON, sms.getPerson());
        values.put(PsDatabaseHelper.SmsColumns.SMS_TYPE, String.valueOf(sms.getType()));
        values.put(PsDatabaseHelper.SmsColumns.SMS_READ, sms.getRead());
        //Log.e("wangmeng","===add===threadId: "+sms.getThread_id());
        //Log.e("wangmeng","===add===address: "+sms.getAddress());
        // Log.e("wangmeng","===add===body: "+sms.getBody());
        Log.e("wangmeng", "===add===date: " + sms.getDate());
        //Log.e("wangmeng","===add===type: "+sms.getType());
        insert(PsDatabaseHelper.Tables.SMS, null, values);
    }

    public void deleteSmsByAddress(String address) {
        delete(PsDatabaseHelper.Tables.SMS, PsDatabaseHelper.SmsColumns.SMS_ADDRESS + "=?", new String[]{address});
    }

    public void deleteSmsById(String id) {
        delete(PsDatabaseHelper.Tables.SMS, PsDatabaseHelper.SmsColumns._ID + "=?", new String[]{id});
    }

    public void insertSms(ContentValues contentValues) {
        insert(PsDatabaseHelper.Tables.SMS, null, contentValues);
    }

    public Cursor getSmsCursor(String selection, String[] selectionArgs) {
        String sql = "SELECT " +
                PsDatabaseHelper.SmsColumns._ID + "," +
                PsDatabaseHelper.SmsColumns.THREAD_ID + "," +
                PsDatabaseHelper.SmsColumns.SMS_ADDRESS + "," +
                PsDatabaseHelper.SmsColumns.SMS_BODY + "," +
                PsDatabaseHelper.SmsColumns.SMS_DATE + "," +
                PsDatabaseHelper.SmsColumns.SMS_PERSON + "," +
                PsDatabaseHelper.SmsColumns.SMS_TYPE + "," +
                PsDatabaseHelper.SmsColumns.SMS_READ +
                " FROM " + PsDatabaseHelper.Tables.SMS + " WHERE " +
                selection;
        return query(sql, new String[]{selectionArgs[0].replaceAll(" ", "").replaceAll("-", "").trim()});
    }

    public List<SmsInfo> getSmsByNum(String phone) {
        String sql = "SELECT " +
                PsDatabaseHelper.SmsColumns._ID + "," +
                PsDatabaseHelper.SmsColumns.THREAD_ID + "," +
                PsDatabaseHelper.SmsColumns.SMS_ADDRESS + "," +
                PsDatabaseHelper.SmsColumns.SMS_BODY + "," +
                PsDatabaseHelper.SmsColumns.SMS_DATE + "," +
                PsDatabaseHelper.SmsColumns.SMS_PERSON + "," +
                PsDatabaseHelper.SmsColumns.SMS_TYPE + "," +
                PsDatabaseHelper.SmsColumns.SMS_READ +
                " FROM " + PsDatabaseHelper.Tables.SMS +
                " WHERE " + PsDatabaseHelper.SmsColumns.SMS_ADDRESS + "=?";
        Log.i(TAG, "sql = " + sql);
        List<SmsInfo> list = new ArrayList<>();
        Cursor cursor = query(sql, new String[]{phone});
        try {
            while (cursor.moveToNext()) {
                SmsInfo sms = new SmsInfo();
                sms.setId(cursor.getInt(0));
                sms.setThread_id(cursor.getLong(1));
                sms.setAddress(cursor.getString(2));
                sms.setBody(cursor.getString(3));
                sms.setDate(cursor.getLong(4));
                sms.setPerson(cursor.getString(5));
                sms.setType(cursor.getInt(6));
                sms.setRead(cursor.getString(7));
                list.add(sms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }

    //add get first sms by wangmeng 20170902
    public List<SmsInfo> getLastSms(String phone) {
        String sql = "SELECT " +
                PsDatabaseHelper.SmsColumns._ID + "," +
                PsDatabaseHelper.SmsColumns.THREAD_ID + "," +
                PsDatabaseHelper.SmsColumns.SMS_ADDRESS + "," +
                PsDatabaseHelper.SmsColumns.SMS_BODY + "," +
                PsDatabaseHelper.SmsColumns.SMS_DATE + "," +
                PsDatabaseHelper.SmsColumns.SMS_PERSON + "," +
                PsDatabaseHelper.SmsColumns.SMS_TYPE + "," +
                PsDatabaseHelper.SmsColumns.SMS_READ +
                " FROM " + PsDatabaseHelper.Tables.SMS +
                " WHERE " + PsDatabaseHelper.SmsColumns.SMS_ADDRESS + "=?";
        Log.i(TAG, "sql = " + sql);
        List<SmsInfo> list = new ArrayList<>();
        Cursor cursor = query(sql, new String[]{phone});
        try {
            if (cursor.moveToLast()) {
                SmsInfo sms = new SmsInfo();
                sms.setId(cursor.getInt(0));
                sms.setThread_id(cursor.getLong(1));
                sms.setAddress(cursor.getString(2));
                sms.setBody(cursor.getString(3));
                sms.setDate(cursor.getLong(4));
                sms.setPerson(cursor.getString(5));
                sms.setType(cursor.getInt(6));
                sms.setRead(cursor.getString(7));
                list.add(sms);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }

    */
/**
     * CallRecord操作
     *//*

    public void addCallRecord(CallRecord callRecord) {
        ContentValues values = new ContentValues();
        values.put(PsDatabaseHelper.CallRecordClumns.DATE, Long.valueOf(callRecord.getDate()));
        values.put(PsDatabaseHelper.CallRecordClumns.DURATION, Long.valueOf(callRecord.getDuration()));
        values.put(PsDatabaseHelper.CallRecordClumns.NAME, callRecord.getName());
        values.put(PsDatabaseHelper.CallRecordClumns.NUMBER, callRecord.getPhoneNum());
        values.put(PsDatabaseHelper.CallRecordClumns.TYPE, Long.valueOf(callRecord.getType()));
        values.put(PsDatabaseHelper.CallRecordClumns.PHOTO_FILE_ID, Long.valueOf(callRecord.getPhotoFileId()));
        insert(PsDatabaseHelper.Tables.CALLRECORD, null, values);
    }

    public void deleteCallRecordByNum(String number) {
        delete(PsDatabaseHelper.Tables.CALLRECORD, PsDatabaseHelper.CallRecordClumns.NUMBER + "=?",
                new String[]{number.replaceAll(" ", "").replaceAll("-", "").trim()});
    }

    public List<CallRecord> getCallRecordsByNum(String phone) {
        String sql = "SELECT " +
                PsDatabaseHelper.CallRecordClumns._ID + "," +
                PsDatabaseHelper.CallRecordClumns.NAME + "," +
                PsDatabaseHelper.CallRecordClumns.NUMBER + "," +
                PsDatabaseHelper.CallRecordClumns.DATE + "," +
                PsDatabaseHelper.CallRecordClumns.DURATION + "," +
                PsDatabaseHelper.CallRecordClumns.TYPE + "," +
                PsDatabaseHelper.CallRecordClumns.PHOTO_FILE_ID + " FROM " +
                PsDatabaseHelper.Tables.CALLRECORD + " WHERE " +
                PsDatabaseHelper.CallRecordClumns.NUMBER + "=?";
        Log.i(TAG, "sql = " + sql);
        List<CallRecord> list = new ArrayList<>();
        Cursor cursor = query(sql, new String[]{phone.replaceAll(" ", "").replaceAll("-", "").trim()});
        try {
            if (cursor.moveToFirst()) {
                do {
                    CallRecord callRecord = new CallRecord();
                    callRecord.setId(cursor.getInt(0));
                    callRecord.setName(cursor.getString(1));
                    callRecord.setPhoneNum(cursor.getString(2));
                    callRecord.setDate(cursor.getLong(3));
                    callRecord.setDuration(cursor.getInt(4));
                    callRecord.setType(cursor.getInt(5));
                    callRecord.setPhotoFileId(cursor.getInt(6));
                    list.add(callRecord);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }

    public List<CallRecord> getCallRecords() {
        String sql = "SELECT " +
                PsDatabaseHelper.CallRecordClumns._ID + "," +
                PsDatabaseHelper.CallRecordClumns.NAME + "," +
                PsDatabaseHelper.CallRecordClumns.NUMBER + "," +
                PsDatabaseHelper.CallRecordClumns.DATE + "," +
                PsDatabaseHelper.CallRecordClumns.DURATION + "," +
                PsDatabaseHelper.CallRecordClumns.TYPE + "," +
                PsDatabaseHelper.CallRecordClumns.PHOTO_FILE_ID + " FROM " +
                PsDatabaseHelper.Tables.CALLRECORD + " ORDER BY " +
                PsDatabaseHelper.CallRecordClumns.NUMBER + " ASC";
        Log.i(TAG, "sql = " + sql);
        List<CallRecord> list = new ArrayList<>();
        Cursor cursor = query(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    CallRecord callRecord = new CallRecord();
                    callRecord.setId(cursor.getInt(0));
                    callRecord.setName(cursor.getString(1));
                    callRecord.setPhoneNum(cursor.getString(2));
                    callRecord.setDate(cursor.getLong(3));
                    callRecord.setDuration(cursor.getInt(4));
                    callRecord.setType(cursor.getInt(5));
                    callRecord.setPhotoFileId(cursor.getInt(6));
                    list.add(callRecord);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }

    public void insertCallLog(ContentValues contentValues) {
        insert(PsDatabaseHelper.Tables.CALLRECORD, null, contentValues);
    }
*/


    /**
     * 以下三个方法：是私密图片数据的 查、增、删 方法 add by dongrp 20170727
     */
    public ArrayList<ImageItem> getPhoto() {
        String sql = "SELECT * " + " FROM " + PsDatabaseHelper.Tables.FILES + " ORDER BY " + PsDatabaseHelper.FilesClumns._ID + " DESC ";
        Log.i(TAG, "sql = " + sql);
        ArrayList<ImageItem> list = new ArrayList<ImageItem>();
        Cursor cursor = query(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    ImageItem imageItem = new ImageItem();
                    imageItem.setImageId(cursor.getString(0));
                    imageItem.setImagePath(cursor.getString(1));
                    imageItem.setSize(cursor.getString(3));
                    imageItem.setDisplayName(cursor.getString(4));
                    imageItem.setTitle(cursor.getString(5));
                    imageItem.setDateAdded(cursor.getString(6));
                    imageItem.setMimeType(cursor.getString(7));
                    imageItem.setBucketId(cursor.getString(8));
                    imageItem.setBucket_display_name(cursor.getString(9));
                    imageItem.setWidth(cursor.getString(10));
                    imageItem.setHeight(cursor.getString(11));
                    list.add(imageItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }

    public void insertPhoto(ContentValues contentValues) {
        insert(PsDatabaseHelper.Tables.FILES, null, contentValues);
    }

    public void deletePhoto(String id) {
        delete(PsDatabaseHelper.Tables.FILES, PsDatabaseHelper.FilesClumns._ID + "=?", new String[]{id});
    }

    /**
     * 以下三个方法是：是私密视频数据的 查、增、删 方法 add by dongrp 20170913
     */
    public ArrayList<VideoItem> getVideo() {
        String sql = "SELECT * " + " FROM " + PsDatabaseHelper.Tables.Video + " ORDER BY " + PsDatabaseHelper.VideoClumns._ID + " DESC ";
        Log.i(TAG, "sql = " + sql);
        ArrayList<VideoItem> list = new ArrayList<VideoItem>();
        Cursor cursor = query(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    VideoItem videoItem = new VideoItem();
                    videoItem.setId(cursor.getString(0));
                    videoItem.setPath(cursor.getString(1));
                    videoItem.setDisplayName(cursor.getString(2));
                    videoItem.setSize(cursor.getString(3));
                    videoItem.setMimeType(cursor.getString(4));
                    videoItem.setDateAdded(cursor.getString(5));
                    videoItem.setTitle(cursor.getString(6));
                    videoItem.setAlbum(cursor.getString(7));
                    videoItem.setBucketId(cursor.getString(8));
                    videoItem.setBucketDisplayName(cursor.getString(9));
                    videoItem.setWidth(cursor.getString(10));
                    videoItem.setHeight(cursor.getString(11));
                    list.add(videoItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }

    public void insertVideo(ContentValues contentValues) {
        insert(PsDatabaseHelper.Tables.Video, null, contentValues);
    }

    public void deleteVideo(String id) {
        delete(PsDatabaseHelper.Tables.Video, PsDatabaseHelper.VideoClumns._ID + "=?", new String[]{id});
    }

    /**
     * 统一封装数据库接口
     */
    private long insert(String table, String nullColumnHack, ContentValues values) {
        return PsDatabaseHelper.getInstance(mContext).getDatabase(true).insert(table, nullColumnHack, values);
    }

    private Cursor query(String sql, String[] selectionArgs) {
        Log.i(TAG, "[query] sql = " + sql);
        return PsDatabaseHelper.getInstance(mContext).getDatabase(true).rawQuery(sql, selectionArgs);
    }

    private int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return PsDatabaseHelper.getInstance(mContext).getDatabase(true).update(table, values, whereClause, whereArgs);
    }

    private int delete(String table, String whereClause, String[] whereArgs) {
        return PsDatabaseHelper.getInstance(mContext).getDatabase(true).delete(table, whereClause, whereArgs);
    }


}
