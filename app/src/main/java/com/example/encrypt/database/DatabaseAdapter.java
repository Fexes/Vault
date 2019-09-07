package com.example.encrypt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.encrypt.photo.ImageItem;
import com.example.encrypt.video.VideoItem;

import java.util.ArrayList;


public class DatabaseAdapter {


    private static final String TAG = "DatabaseAdapter";

    private Context mContext;
    private static PsDatabaseHelper mDbHelper;

    public DatabaseAdapter(Context context) {
        this.mContext = context;
        mDbHelper = PsDatabaseHelper.getInstance(context);
    }





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
