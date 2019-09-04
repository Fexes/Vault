package com.example.encrypt.photo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;

import com.example.encrypt.video.VideoItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class AlbumHelper {
    final String TAG = getClass().getSimpleName();
    Context context;
    static ContentResolver cr;

    HashMap<String, String> thumbnailList = new HashMap<String, String>();

    List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

    private static AlbumHelper instance;

    private AlbumHelper() {
    }

    public static AlbumHelper getHelper() {
        if (instance == null) {
            instance = new AlbumHelper();
        }
        return instance;
    }

    public void init(Context context) {
        if (this.context == null) {
            this.context = context;
            cr = context.getContentResolver();

        }
    }

    private void getThumbnail() {
        String[] projection = {Thumbnails._ID, Thumbnails.IMAGE_ID,
                Thumbnails.DATA};
        Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        getThumbnailColumnData(cursor);
    }

    private void getThumbnailColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
//			int _id;
            int image_id;
            String image_path;
//			int _idColumn = cur.getColumnIndex(Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

            do {
                // Get the field values
//				_id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

                // Do something with the values.
                // Log.i(TAG, _id + " image_id:" + image_id + " path:"
                // + image_path + "---");
                // HashMap<String, String> hash = new HashMap<String, String>();
                // hash.put("image_id", image_id + "");
                // hash.put("path", image_path);
                // thumbnailList.add(hash);
                thumbnailList.put("" + image_id, image_path);
            } while (cur.moveToNext());
        }
    }




    boolean hasBuildImagesBucketList = false;

    void buildImagesBucketList() {
        long startTime = System.currentTimeMillis();

        getThumbnail();

        String columns[] = new String[]{Media._ID, Media.BUCKET_ID,
                Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
                Media.SIZE, Media.BUCKET_DISPLAY_NAME, Media.DATE_ADDED, Media.MIME_TYPE, Media.WIDTH, Media.HEIGHT};
        Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null, Media._ID + " DESC ");
        bucketList.clear();//add by dongrp
        if (cur.moveToFirst()) {
            int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
            int bucketDisplayNameIndex = cur
                    .getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
            int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
            int dateAddedIdIndex = cur.getColumnIndexOrThrow(Media.DATE_ADDED);
            int mimeTypeIdIndex = cur.getColumnIndexOrThrow(Media.MIME_TYPE);
            int widthIndex = cur.getColumnIndexOrThrow(Media.WIDTH);
            int heightIndex = cur.getColumnIndexOrThrow(Media.HEIGHT);
//			int totalNum = cur.getCount();

            do {
                String _id = cur.getString(photoIDIndex);
                String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
                String title = cur.getString(photoTitleIndex);
                String size = cur.getString(photoSizeIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);
                String picasaId = cur.getString(picasaIdIndex);
                String dateAdded = cur.getString(dateAddedIdIndex);
                String mimeType = cur.getString(mimeTypeIdIndex);
                String width = cur.getString(widthIndex);
                String height = cur.getString(heightIndex);

				/*Log.i(TAG, _id + ", bucketId: " + bucketId + ", picasaId: "
						+ picasaId + " name:" + name + " path:" + path
						+ " title: " + title + " size: " + size + " bucket: "
						+ bucketName + "---");*/

                ImageBucket bucket = bucketList.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    bucketList.put(bucketId, bucket);
                    bucket.imageList = new ArrayList<ImageItem>();
                    bucket.bucketName = bucketName;
                }
                bucket.count++;
                ImageItem imageItem = new ImageItem();
                imageItem.imageId = _id;
                imageItem.imagePath = path;
                imageItem.thumbnailPath = thumbnailList.get(_id);
                imageItem.setBucket_display_name(bucketName);
                imageItem.setBucketId(bucketId);
                imageItem.setDisplayName(name);
                imageItem.setDateAdded(dateAdded);
                imageItem.setMimeType(mimeType);
                imageItem.setSize(size);
                imageItem.setTitle(title);
                imageItem.setWidth(width);
                imageItem.setHeight(height);
                bucket.imageList.add(imageItem);

            } while (cur.moveToNext());
        }

        Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Entry<String, ImageBucket> entry = (Entry<String, ImageBucket>) itr
                    .next();
            ImageBucket bucket = entry.getValue();
			/*Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", "
					+ bucket.count + " ---------- ");*/
            for (int i = 0; i < bucket.imageList.size(); ++i) {
                ImageItem image = bucket.imageList.get(i);
				/*Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath
						+ ", " + image.thumbnailPath);*/
            }
        }
        hasBuildImagesBucketList = true;
        long endTime = System.currentTimeMillis();
        //Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
    }


    public List<ImageBucket> getImagesBucketList(boolean refresh) {
        if (refresh || (!refresh && !hasBuildImagesBucketList)) {
            buildImagesBucketList();
        }
        List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
        Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Entry<String, ImageBucket> entry = (Entry<String, ImageBucket>) itr
                    .next();
            tmpList.add(entry.getValue());
        }
        return tmpList;
    }




    public static ArrayList<VideoItem> getSystemVideoList(Context context) {
        ArrayList<VideoItem> sysVideoList = new ArrayList<>();

        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
        String[] videoThumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};

        // MediaStore.Video.Media.DATA：视频文件路径；
        String[] videoColumns = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.TITLE, MediaStore.Video.Media.ALBUM,
                MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.WIDTH, MediaStore.Video.Media.HEIGHT};

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoColumns, null, null, MediaStore.Video.Media._ID + " DESC ");

/*		if(cursor==null){
			Toast.makeText(SystemVideoChooseActivity.this, "没有找到可播放视频文件", 1).show();
			return;
		}*/

        if (cursor.moveToFirst()) {
            do {
                VideoItem videoItem = new VideoItem();
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, videoThumbColumns,
                        MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    videoItem.setThumbPath(thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                }
                videoItem.setId(String.valueOf(id));
                videoItem.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                videoItem.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                videoItem.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                videoItem.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
                videoItem.setDateAdded(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)));
                videoItem.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                videoItem.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)));
                videoItem.setBucketId(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)));
                videoItem.setBucketDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)));
                videoItem.setWidth(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)));
                videoItem.setHeight(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)));
                sysVideoList.add(videoItem);
            } while (cursor.moveToNext());
        }
        return sysVideoList;
    }


}
