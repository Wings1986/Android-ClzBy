package com.closeby.clzby.activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Global {

	public static String kServerURL = "http://www.clzby.com/clzbyapi";
	
	public static String kGUID = "ffc995e9-8e43-4f4a-aa25-8405e158cacb";

	public static String getURLEncoded(String url) {
		try {
			url = URLDecoder.decode(url, "UTF-8");

			url = url.replaceAll("\\+", "%20");
			url = url.replace(" ", "%20");

		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return url;
	}

	public static String getLeftTime(String strEndTime) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
			Date endTime = sdf.parse(strEndTime);

			long distanceBetweenDates = endTime.getTime() - new Date().getTime();

			int SECONDS = 60;
			int SECONDS_IN_HOUR = 60 * SECONDS;

			int hours = (int) distanceBetweenDates / SECONDS_IN_HOUR;
			int minutes = ((int) distanceBetweenDates % SECONDS_IN_HOUR) / SECONDS;
			int sec = ((int) distanceBetweenDates % SECONDS_IN_HOUR) % SECONDS;

			if (hours <= 0 && minutes <= 0 && sec <= 0) {
				return "Expired";
			}

			return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, sec);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}

	}

	public static String getHour(String strTime) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.US);
			Date endTime = sdf.parse(strTime);

			long distanceBetweenDates = endTime.getTime() - new Date().getTime();

			int SECONDS = 60;
			int SECONDS_IN_HOUR = 60 * SECONDS;

			int hours = (int) distanceBetweenDates / SECONDS_IN_HOUR;
			int minutes = ((int) distanceBetweenDates % SECONDS_IN_HOUR) / SECONDS;
			int sec = ((int) distanceBetweenDates % SECONDS_IN_HOUR) % SECONDS;

			if (hours <= 0 && minutes <= 0 && sec <= 0) {
				return "Expired";
			}

			return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, sec);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}

	}

	public static String base64StringForImage(Bitmap bmp) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
		byte[] byteArrayImage = baos.toByteArray();

		String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

		return encodedImage;
	}

	// bitmap
	public static Bitmap getThumbnail(Context context, Uri uri) throws IOException {
		final int THUMBNAIL_SIZE = 350;

		InputStream input = context.getContentResolver().openInputStream(uri);

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;// optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1)
				|| (onlyBoundsOptions.outHeight == -1))
			return null;

		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;

		double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE)
				: 1.0;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true;// optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		input = context.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}

	public static Bitmap scaleImage(Context context, Uri photoUri) throws IOException {

		final int MAX_IMAGE_DIMENSION = 350;


		InputStream is = context.getContentResolver().openInputStream(photoUri);
		BitmapFactory.Options dbo = new BitmapFactory.Options();
		dbo.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, dbo);
		is.close();

		int rotatedWidth, rotatedHeight;
		int orientation = getOrientation(context, photoUri);

		if (orientation == 90 || orientation == 270) {
			rotatedWidth = dbo.outHeight;
			rotatedHeight = dbo.outWidth;
		} else {
			rotatedWidth = dbo.outWidth;
			rotatedHeight = dbo.outHeight;
		}

		Bitmap srcBitmap;
		is = context.getContentResolver().openInputStream(photoUri);
		if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
			float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
			float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
			float maxRatio = Math.max(widthRatio, heightRatio);

			// Create the bitmap from file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = (int) maxRatio;
			srcBitmap = BitmapFactory.decodeStream(is, null, options);
		} else {
			srcBitmap = BitmapFactory.decodeStream(is);
		}
		is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
		if (orientation > 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(orientation);

			srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
					srcBitmap.getHeight(), matrix, true);
		}

		String type = context.getContentResolver().getType(photoUri);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (type.equals("image/png")) {
			srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		} else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
			srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		}
		byte[] bMapArray = baos.toByteArray();
		baos.close();
		return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
	}

	public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
		Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}

}
