package com.example.encrypt.photo;

import android.graphics.Bitmap;

import java.io.Serializable;


public class ImageItem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	private Bitmap bitmap;
	public boolean isSelected = false;
    public String size;
	public String displayName;
	public String title;
	public String dateAdded;
	public String mimeType;
	public String bucketId;
	public String bucket_display_name;
	public String width,height;

	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public Bitmap getBitmap() {
		if(bitmap == null){
			bitmap = Bimp.handleBitmap(imagePath);
		}
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getSize() {
		return size;
	}


	public void setSize(String size) {
		this.size = size;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getBucketId() {
		return bucketId;
	}

	public void setBucketId(String bucketId) {
		this.bucketId = bucketId;
	}

	public String getBucket_display_name() {
		return bucket_display_name;
	}

	public void setBucket_display_name(String bucket_display_name) {
		this.bucket_display_name = bucket_display_name;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String toString() {
		return "ImageItem{" +
				"imageId='" + imageId + '\'' +
				", imagePath='" + imagePath + '\'' +
				", size='" + size + '\'' +
				", displayName='" + displayName + '\'' +
				", title='" + title + '\'' +
				", dateAdded='" + dateAdded + '\'' +
				", mimeType='" + mimeType + '\'' +
				", bucketId='" + bucketId + '\'' +
				", bucket_display_name='" + bucket_display_name + '\'' +
				'}';
	}
}
