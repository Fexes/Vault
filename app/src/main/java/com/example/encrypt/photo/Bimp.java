package com.example.encrypt.photo;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import com.example.encrypt.video.VideoItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.crypto.CipherInputStream;


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
	

	public static Bitmap handleBitmap(Uri uri, ContentResolver cr) {

		int sampleSize = 6;

		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;

		try {
			BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		options.inSampleSize = sampleSize;

		options.inJustDecodeBounds = false;
		try {
			return compressImage(BitmapFactory.decodeStream(cr.openInputStream(uri), null, options), 768);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}



	public static Bitmap handleBitmap(CipherInputStream fis) {

		int sampleSize = 6;

		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;

		BitmapFactory.decodeStream(fis, null, options);

		options.inSampleSize = sampleSize;

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(fis, null, options);
	}


	public static String bitmapToBase64(Bitmap bitmap) {
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				baos.flush();
				baos.close();
				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
}
