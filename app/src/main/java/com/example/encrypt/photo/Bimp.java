package com.example.encrypt.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.encrypt.video.VideoItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class Bimp {
	public static int max = 0;

	public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<>();
	public static ArrayList<VideoItem> tempSelectVideo = new ArrayList<>();


	private static Bitmap compressImage(Bitmap bitmap, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > size) {
			baos.reset();
			options -= 10;
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap1 = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap1;
	}
	

	public static Bitmap handleBitmap(String path) {

		int sampleSize = 6;

		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = sampleSize;

		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return compressImage(bitmap, 64);
	}
	

	
	
}
