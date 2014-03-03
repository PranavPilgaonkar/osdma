package com.osdma.milestones.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.util.Log;

import com.osdma.milestones.db.Image;
import com.osdma.milestones.db.ImageHandler;

public class util {
	
	private static String timeStampForWaterMark;
	
	private static String createImageFile(String FOLDER_NAME,String comment, String FILE_EXTENSION) throws IOException {
	    //Bitmap bitmap_copy = Bitmap.createBitmap(imageBitmap);
		Bitmap imageBitmap = ShrinkBitmap(FOLDER_NAME,1024,768);
		Bitmap mutableBitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth()*2, imageBitmap.getHeight()*2, true);
		
	    timeStampForWaterMark = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    Canvas canvas = new Canvas(mutableBitmap);
	    Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(40);
        paint.setAntiAlias(true);
	    canvas.drawText(timeStampForWaterMark, 50, 190, paint);
	    canvas.drawText(comment, 50, 225, paint);
	    //paint.setColor(Color.BLACK);
	    //canvas.drawText(timeStampForWaterMark, 10, 10, paint);
	    
	    File image = new File(FOLDER_NAME);
	    
	    //System.out.println("File name : " + image.getPath() + "/" + image.getName());
	    FileOutputStream fileOutputStream = new FileOutputStream(image);
	    mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
	    fileOutputStream.flush();
	    fileOutputStream.close();
	    System.out.println(image.getPath());
	    return image.getPath();
	}
	
	public static Bitmap ShrinkBitmap(String file, int width, int height){
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
           bmpFactoryOptions.inJustDecodeBounds = true;
           Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
           
           /*int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
           int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);
            
           if (heightRatio > 1 || widthRatio > 1)
           {
            if (heightRatio > widthRatio)
            {
             bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
             bmpFactoryOptions.inSampleSize = widthRatio; 
            }
           }
            */
           if(bmpFactoryOptions.outWidth > 3000)
        	   bmpFactoryOptions.inSampleSize = 4;
           else
        	   bmpFactoryOptions.inSampleSize = 2;
           bmpFactoryOptions.inJustDecodeBounds = false;
           bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
           File image = new File(file);
           int rotation = getImageRotation(image.getAbsolutePath());
          Matrix mat = new Matrix();
          mat.postRotate(rotation);
         bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);   
        return bitmap;
    }
	
	private static int getImageRotation(String file) {
		// TODO Auto-generated method stub
		ExifInterface ei = null;
		try {
			ei = new ExifInterface(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

		switch(orientation) {
		    case ExifInterface.ORIENTATION_ROTATE_90:
		        return 90;
		    case ExifInterface.ORIENTATION_ROTATE_180:
		        return 180;
		    case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
		    // etc.
		}
		return 0;
	}

//	public static void addImage(Context context, String file_location, String latitude, String longitude){
//		try {
//			createImageFile(file_location,".jpg");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ImageHandler db = new ImageHandler(context);
//		db.add(new Image(System.currentTimeMillis(), file_location, latitude, longitude, timeStampForWaterMark, "false"));
//	}
	
	public static void addImage(Context context, String file_location, String latitude, String longitude,String comment){
		try {
			createImageFile(file_location,comment,".jpg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageHandler db = new ImageHandler(context);
		db.add(new Image(System.currentTimeMillis(), file_location, latitude, longitude, timeStampForWaterMark, "false"));
	}
	
	public static byte[] hash(String data) throws NoSuchAlgorithmException {
	    MessageDigest sha512 = MessageDigest.getInstance("SHA-512");        
	    byte[] passBytes = data.getBytes();
	    byte[] passHash = sha512.digest(passBytes);
	    return passHash;
	}
	
	public static String hash(byte[] md5) throws NoSuchAlgorithmException {
	    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
	    byte[] passHash = sha256.digest(md5);
	    return bytetostring(passHash);
	}
	
	public static final byte[] md5(String s) {
	    try {
	    	// Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        return messageDigest;

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public static String bytetostring(byte[] messageDigest){
		StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            String h = Integer.toHexString(0xFF & messageDigest[i]);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }
        return hexString.toString();
	}
}