package com.osdma.milestones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.osdma.R;
import com.osdma.milestones.REST.jsonSend;
import com.osdma.milestones.adapters.GridViewAdapter;
import com.osdma.milestones.db.Image;
import com.osdma.milestones.db.ImageHandler;
import com.osdma.milestones.utils.Dataloader;
import com.osdma.milestones.utils.util;

public class MainActivity extends Activity{

	private static final int CAMERA_REQUEST = 0;
	private static final String FOLDER_NAME = "/OSDMA";
	private static final String Photo_Post_URL = "http://119.81.38.147:8080/image"; 
	private static final String User_Post_URL = "http://119.81.38.147:8080/user"; 
	private ArrayList<ImageItem> data = new ArrayList<ImageItem>();
	private GridViewAdapter customGridAdapter;
	private Context context;
	private SharedPreferences settings = null;
	private String PREF_NAME = "OSDMADATA";
	private String USERNAME = "username";
	private String PASSWORD = "password";
	private String SITENO = "sitenumber";
	public double latitude;
    public double longitude;
    public long fileName;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);  
        Log.d("OSDMA", "Inside onCreate....");
        context = this;
        settings = this.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	     // getting GPS status
	     boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	     Log.d("OSDMA","GPS:"+isGPSEnabled);
	
	     // check if GPS enabled     
	     if(isGPSEnabled){
          Criteria criteria = new Criteria();
          String provider = locationManager.getBestProvider(criteria, false);
          Location location = locationManager.getLastKnownLocation(provider);

         if(location != null)
         {
             longitude = location.getLongitude();
             latitude = location.getLatitude();
         }else
         {
             locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

                 @Override
                 public void onStatusChanged(String provider, int status, Bundle extras) {
                     // TODO Auto-generated method stub

                 }

                 @Override
                 public void onProviderEnabled(String provider) {
                     // TODO Auto-generated method stub

                 }

                 @Override
                 public void onProviderDisabled(String provider) {
                     // TODO Auto-generated method stub

                 }

                 @Override
                 public void onLocationChanged(Location location) {
                     // TODO Auto-generated method stub
                     longitude = location.getLongitude();
                     latitude = location.getLatitude();
                     Log.d("OSDMA","changed lat long : "+latitude +" "+ longitude);
                 }
             });
         }

     }
     else
     {
         Log.d("OSDMA","GPS DISABLED");
     }
        ////////////////////////////////////////////////////////////////////////////////////////
        
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.mainLayout);
		
		LinearLayout gridLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.gallery_layout, null);
		relativeLayout.addView(gridLinearLayout);
		setImageGrid(gridLinearLayout);
    }
    
    private class HttpAsyncTask extends AsyncTask<List<String>, Void, String> {
        
    	@Override
        protected String doInBackground(List<String>... images) {
    		ImageHandler db = new ImageHandler(context);
    		Image image = null;
    		String ImageLocation = null;
    		String temp = null;
    		ByteArrayOutputStream baos;
    		Bitmap bm=null;
    		String response = null;
    		byte[] b;
    		for(int i = 0; i<images[0].size(); i++){
	    		try{  
				    bm = BitmapFactory.decodeFile(images[0].get(i));
				    //Log.d("OSDMA",i+":"+images[0].get(i));
				    ImageLocation = images[0].get(i);
			  		baos = new ByteArrayOutputStream();  
			  		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
			  		b = baos.toByteArray();
			        temp=Base64.encodeToString(b, Base64.DEFAULT);
			        }catch(Exception e){
			            e.printStackTrace();
			        }catch(OutOfMemoryError e){
			            baos=new  ByteArrayOutputStream();
			            bm.compress(Bitmap.CompressFormat.JPEG,50, baos);
			            b=baos.toByteArray();
			            temp=Base64.encodeToString(b, Base64.DEFAULT);
			            ImageLocation = images[0].get(i);
			            //Log.e("EWN", "Out of memory error catched");
			        }
	    		final JSONObject imgjsonObjSend = new JSONObject();
	    		final JSONObject userjsonObjSend = new JSONObject();
				try {
					String password = settings.getString(PASSWORD, "");
					
					userjsonObjSend.put("username", settings.getString(USERNAME, ""));
					try {
						userjsonObjSend.put("password", util.bytetostring(util.hash(util.hash(password.getBytes())+password)));
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					userjsonObjSend.put("sitename", settings.getString(SITENO, ""));
					//ImageHandler db = new ImageHandler(context);
					image = db.get(ImageLocation);
					//Log.d("OSDMA",image.datetime);
					imgjsonObjSend.put("photodata", temp);
					imgjsonObjSend.put("username", settings.getString(USERNAME, ""));
					/*imgjsonObjSend.put("password", settings.getString(PASSWORD, ""));*/
					imgjsonObjSend.put("sitename", settings.getString(SITENO, ""));
					imgjsonObjSend.put("latitude", image.lattitude);
					imgjsonObjSend.put("longitude", image.longitude);
					imgjsonObjSend.put("photodate", image.datetime);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				/*new Thread(new Runnable() {
			         public void run() {
			        	 final String response = jsonSend.SendHttpPost(User_Post_URL, userjsonObjSend);
			        	 if("User Success".equals(response)){
			        		 final String uploadStatus = jsonSend.SendHttpPost(Photo_Post_URL, imgjsonObjSend);
			        		 if("Successfully uploaded photo".equals(uploadStatus)){
			        			 
			        		 }else{
			        			 display(uploadStatus);
			        		 }
			        	 }else{
			        		 display(response);
			        	 }
			         }
			     }).start();*/
				
				response = jsonSend.SendHttpPost(User_Post_URL, userjsonObjSend);
				Log.d("OSDMA","After Getting response for authentication : " + response);
	        	 if("User Success".equals(response)){
	        		 //Toast.makeText(context, "User Authenticated!", Toast.LENGTH_LONG).show();
	        		 ((Activity) context).runOnUiThread(new Runnable(){
	 					@Override
	 					public void run() {
	 						Toast.makeText(context, "User and Site Authenticated!", Toast.LENGTH_LONG).show();
	 					}
	 				});
	        		 String uploadStatus = jsonSend.SendHttpPost(Photo_Post_URL, imgjsonObjSend);
	        		 if("Successfully uploaded photo".equals(uploadStatus)){	        			 
	        			 image.issync = "true";
	        			 Log.d("OSDMA","Updatig DB IF : " + db.update(image));
	        			 response = uploadStatus;
	        			 customGridAdapter.isSendSuccessful = true;
	        		 }else{
	        			 Log.d("OSDMA","Could not upload image...");
	        			 response = uploadStatus;
//	        			 display(uploadStatus);
	        		 }
	        	 }else{
//	        		 display(response);
	        	 }
    		}  
    		return response;
        }
    	
    	private void display(final String response){
    		if(response!=null && !response.trim().equals("null")){
    		((Activity) context).runOnUiThread(new Runnable(){
					@Override
					public void run() {
					  Builder builder = new AlertDialog.Builder(context);
				      builder.setMessage("Response : " + response);
				      builder.setCancelable(true);
				      builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							
						}
				      });
				      AlertDialog dialog = builder.create();
				      dialog.show();
					}
				});
    		}else{
    			Log.d("OSDMA","Inside else response is null : " + response);
    		}
    	}
    	
        
        @Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			//Show the Progress Dialog
			/*pd = new ProgressDialog(context);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("Uploading Picture...");
			pd.setCancelable(false);
			pd.show();*/
		}

		// onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	//Toast.makeText(getBaseContext(), "Sending Finished!", Toast.LENGTH_LONG).show();
        	Log.d("OSDMA", "Inside onPostExecute of HTTPAsyncTask....");
        	customGridAdapter.isSendClicked = false;
            customGridAdapter.notifyDataSetChanged();
        	display(result);
       }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //return true;
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case R.id.action_settings:
            openSettings();
            return true;
        case R.id.takeapic:
        	takePictureActivity();
        	return true;
        case R.id.deletepic:
        	deletePictureActivity();
        	return true;
        case R.id.uploadpic:
        	uploadPictureActivity();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
		}
	}

	private void takePictureActivity() {
		Toast.makeText(this, "Camera Clicked", Toast.LENGTH_SHORT).show();
		launchCamera();
	}
	
	private void deletePictureActivity() {
		
		Log.d("OSDMA","Delete icon clicked");
        thumbnailsselection = customGridAdapter.getThumbnailsSelection();
		final int len = thumbnailsselection.length;
		
		int imageSelectedCount = 0;
		for(int k=len-1;k>=0;k--){
			if (thumbnailsselection[k]) imageSelectedCount++ ;
		}
		
		Log.d("OSDMA","Images selected count = "+imageSelectedCount);
		if(imageSelectedCount == 0 ){
		    Toast.makeText(getApplicationContext(),
		            "Please select at least one image",
		            Toast.LENGTH_LONG).show();
		    return ;
		}
		else
		{
			//confirm and if yes call delete function
			confirmThenDelete();     
		}   
	}
	
	private void uploadPictureActivity() {

		if("".equals(settings.getString(USERNAME, "")) || "".equals(settings.getString(USERNAME, "")) || "".equals(settings.getString(USERNAME, ""))){
    		Toast.makeText(getApplicationContext(),
                    "Please enter Username, Password and Site Name in Settings",
                    Toast.LENGTH_LONG).show();
    		return;
    	}
		
		thumbnailsselection = customGridAdapter.getThumbnailsSelection();
		List<String> nameValuePairs = new ArrayList<String>();           	 
        final int len = thumbnailsselection.length;
        int cnt = 0;
        customGridAdapter.isSendClicked = true;
        customGridAdapter.notifyDataSetChanged();
        for (int i =0; i<len; i++)
        {
            if (thumbnailsselection[i]){
                cnt++;
                nameValuePairs.add(data.get(i).getTitle());
            }
        }
        if (cnt == 0){
            Toast.makeText(getApplicationContext(),
                    "Please select at least one image",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "You've selected Total " + cnt + " image(s). We are Syncing Photos",
                    Toast.LENGTH_LONG).show();
            
            new HttpAsyncTask().execute(nameValuePairs);
        }
		
		
	}

	
	
	private void deleteSelectedImage(){
		
                thumbnailsselection = customGridAdapter.getThumbnailsSelection();
        final int len = thumbnailsselection.length;
        ArrayList<Boolean> thumbnailList = new ArrayList<Boolean>();
        int cnt = 0;
        //Log.d("OSDMA","deleteSelectedImage : " + data.size());
        for (int i =len-1; i>=0; i--)
        {
                Log.d("OSDMA","thumbnailsselection : " + i + " : "+ thumbnailsselection[i]);
            if (thumbnailsselection[i]){
                cnt++;
                File file = new File(data.get(i).getTitle());
                boolean deleted = file.delete();
                Log.d("OSDMA","Deleted: "+data.get(i).getTitle());
                customGridAdapter.remove(customGridAdapter.getItem(i));
                
            }else{
                    thumbnailList.add(false);
            }
        }
        
        if(cnt>0){
            Toast.makeText(getApplicationContext(),
                    "You've selected Total " + cnt + " image(s). We are Deleting Photos",
                    Toast.LENGTH_LONG).show();
            Log.d("OSDMA","deleteSelectedImage : "+cnt);
        }
        //Log.d("SelectedImages", selectImages);
        boolean[] newArray = new boolean[thumbnailsselection.length-cnt];
        int newArrayIndex = 0;
        for (int index = 0; index < thumbnailsselection.length; index++) {
                        if(!thumbnailsselection[index]){
                                newArray[newArrayIndex] = thumbnailsselection[index];
                                newArrayIndex++;
                        }
                }
        thumbnailsselection = newArray;
        customGridAdapter.setThumbnailsSelection(thumbnailsselection);
        gridView.setAdapter(customGridAdapter);
    }
	
	private void confirmThenDelete(){
		
		new AlertDialog.Builder(this)
		.setTitle("Delete")
		.setMessage("Do you want to delete selected pictures(s) ?")
		.setIcon(android.R.drawable.ic_delete)
		.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {

		    public void onClick(DialogInterface dialog, int whichButton) {
		    	
		    	//'Yes' confirmed, now calling method: deleteSelectedImage()	
		    	deleteSelectedImage();
		    }})
		 .setNegativeButton(R.string.Cancel, null).show();
		
	}
	
	private void openSettings(){
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			LinearLayout set = new LinearLayout(this);
			set.setOrientation(1);
		    final EditText username = new EditText(this);
		    if("".equals(settings.getString(USERNAME, ""))) {
		    	username.setHint("UserName");
		    }
		    else {
		    	username.setText(settings.getString(USERNAME, ""));
		    }
		    
		    final EditText password = new EditText(this);
		    if("".equals(settings.getString(PASSWORD, ""))) {
		    	password.setHint("PASSWORD");
		    }
		    else {
		    	password.setText(settings.getString(PASSWORD, ""));
		    }
		    
		    final EditText siteno = new EditText(this);
		    if("".equals(settings.getString(SITENO, ""))) {
		    	siteno.setHint("SITE NUMBER");
		    }
		    else {
		    	siteno.setText(settings.getString(SITENO, ""));
		    }
		    
		    set.addView(username);
		    set.addView(password);
		    set.addView(siteno);
		    alert.setView(set);
			alert.setNeutralButton("OK", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences.Editor editor = settings.edit();
			        editor.putString(USERNAME, username.getText().toString());
			        editor.putString(PASSWORD, password.getText().toString());
			        editor.putString(SITENO, siteno.getText().toString());
			        editor.commit();
				}
				
			}).show();
	}
	
	private void launchCamera(){
		//Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		//startActivityForResult(cameraIntent, CAMERA_REQUEST);
		if(!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).exists()){
		      //Creating directory in sd card
		      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).mkdir();
	    }
		fileName = System.currentTimeMillis();
		File imageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME), ""+fileName+".jpg");
		
        String path = imageDirectory.toString().toLowerCase();
        String name = imageDirectory.getName().toLowerCase();

        ContentValues values = new ContentValues(); 
        values.put(Media.TITLE, "Image");
        values.put(Images.Media.BUCKET_ID, path.hashCode());
        values.put(Images.Media.BUCKET_DISPLAY_NAME,name);

        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Media.DESCRIPTION, "Image capture by camera");
        
        values.put("_data", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).getPath()+"/"+fileName+".jpg");
        Uri uri = getContentResolver().insert( Media.EXTERNAL_CONTENT_URI , values);
        Intent i = new Intent("android.media.action.IMAGE_CAPTURE"); 

        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(i, CAMERA_REQUEST);
	} 
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {  
			showCommentDialog();
			/*Bitmap photo = (Bitmap) data.getExtras().get("data"); 
            Log.d("OSDMA","Width : " + photo.getWidth());
            Log.d("OSDMA","Height : " + photo.getHeight());
            Log.d("OSDMA","Pictures path : " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            //capturedImage.setImageBitmap(Bitmap.createScaledBitmap(photo, photo.getWidth()*2, photo.getHeight()*2, true));
            try {
				String file_name = util.createImageFile(photo, FOLDER_NAME, FILE_EXTENSION);
				util.addImage(context, file_name, ""+latitude, ""+longitude);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
        }  
	}
	
	private void showCommentDialog(){
		
		final View view = getLayoutInflater().inflate(R.layout.comment_dialog, null);
		new AlertDialog.Builder(this)
		.setTitle("Comment")
		.setView(view)
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

		    public void onClick(DialogInterface dialog, int whichButton) {
		    	
		    	//'Yes' confirmed, now calling method: deleteSelectedImage()	
		    	EditText comment = (EditText)view.findViewById(R.id.comment);
		    	String commentStr = comment.getText().toString().trim();
		    	Log.d("OSDMA","Comment : " + commentStr);
		    	if(comment!=null && !comment.equals("")){
		    		util.addImage(context, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).getPath()+"/"+fileName+".jpg", ""+latitude, ""+longitude, commentStr);
		    	}else{
		    		util.addImage(context, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).getPath()+"/"+fileName+".jpg", ""+latitude, ""+longitude, "");
		    	}
		    	getData();
		    }}).show();
		 
	}
	GridView gridView;
	boolean[] thumbnailsselection; 

	private void setImageGrid(View view){
    	gridView = (GridView)view.findViewById(R.id.imageGrid);
    	getData();
   	}
    
    /*private ArrayList<ImageItem> getData() {
		// TODO Auto-generated method stub
    	if(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).exists() && Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).isDirectory()){
	    	if(!data.isEmpty()){
	    		data.clear();
	    	}
	    	File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).listFiles();
	    	for (int index = 0; index < files.length; index++) {
				if(!files[index].isDirectory()){
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					Bitmap bitmap = BitmapFactory.decodeFile(files[index].getAbsolutePath(), options);
					data.add(new ImageItem(bitmap, files[index].getAbsolutePath()));
				}
			}
    	}
    	return data;
	}*/
	
	private void getData() {
        // TODO Auto-generated method stub
	    Log.d("OSDMA","Inside getdata....");
	    if(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).exists() && Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).isDirectory()){
	         if(!data.isEmpty()){
	                 data.clear();
	         }
	         File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+FOLDER_NAME).listFiles();
	         ImageLoader imageLoader = new ImageLoader();
	         imageLoader.execute(files);
	    }
	    Log.d("OSDMA","Data length : " + data.size());
	}
    
    public class ImageLoader extends AsyncTask<File, Integer, ArrayList<ImageItem>>{

        
        @Override
        protected ArrayList<ImageItem> doInBackground(File... files) {
                // TODO Auto-generated method stub
                ArrayList<ImageItem> data = new ArrayList<ImageItem>();
                        for (int index = 0; index < files.length; index++) {
                                if(!files[index].isDirectory()){
                                        //Log.d("OSDMA","Time : " + System.currentTimeMillis());
                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                        options.inJustDecodeBounds = false;
                                        options.inDither = true;
                                        options.inSampleSize = 4;
                                        Bitmap bitmap = BitmapFactory.decodeFile(files[index].getAbsolutePath(),options);
                                        data.add(new ImageItem(bitmap, files[index].getAbsolutePath()));
                                        
                                        Log.d("OSDMA","Time : " + System.currentTimeMillis());
                                }
                        }
         return data;
        }

        @Override
        protected void onPostExecute(ArrayList<ImageItem> result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                Log.d("OSDMA", "Setting Grid Adapter in onPostExecute...");
                Dataloader.arrayList = result;
                
                customGridAdapter = new GridViewAdapter(MainActivity.this, R.layout.row_grid, result);
                thumbnailsselection = customGridAdapter.getThumbnailsSelection();
                MainActivity.this.data = result;
                gridView.setAdapter(customGridAdapter);
        }
        
    }
	
}