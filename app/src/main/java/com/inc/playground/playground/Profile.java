package com.inc.playground.playground;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import com.inc.playground.playground.Home;
import com.inc.playground.playground.R;
import com.inc.playground.playground.utils.Logingetset;

public class Profile extends Activity {
	public static final String MY_PREFS_NAME = "Store";
	String user_name1, Full_name, Email, imageprofile, user_name, full_name, user2, loginimage,emailid, Error;
	TextView txt_full, txt_user, txt_email;
	EditText edt_username, edt_fullname, edt_mailid;
	ImageView img_profile;
	Button btn_profile, btn_delete;
	private static int RESULT_LOAD_IMAGE = 1;
	String fullname, mail, username, picturepath, responseStr, key, userloginid;
	ProgressDialog progressDialog;
	ArrayList<Logingetset> login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		login = new ArrayList<Logingetset>();
		SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
		picturepath = prefs.getString("picturepath", null);
		userloginid = prefs.getString("score", null);

		Full_name = prefs.getString("fullname", null);
		user_name1 = prefs.getString("username", null);
		Email = prefs.getString("emilid", null);
		imageprofile = prefs.getString("picturepath", null);
		
		
		Log.d("imageprofile", "" + full_name);
		Log.d("imageprofile", "" + user_name1);
		Log.d("imageprofile", "" + Email);
		Log.d("imageprofile", "" + userloginid);
		edt_fullname = (EditText) findViewById(R.id.edt_fullname);
		edt_username = (EditText) findViewById(R.id.edt_username);
		edt_mailid = (EditText) findViewById(R.id.edt_malid);

		img_profile = (ImageView) findViewById(R.id.img_profile);
		// new DownloadImageTask(img_profile)
		// .execute(imageprofile);

		//img_profile.setImageBitmap(BitmapFactory.decodeFile(imageprofile));
		img_profile.setImageBitmap(decodeFile(picturepath));
		edt_mailid.setText(Email);
		edt_fullname.setText(Full_name);
		edt_username.setText(user_name1);
		
		if(picturepath!=null)
		{
			
		}else {
			if (prefs.getString("picture", null)!=null) {
				loginimage = prefs.getString("picture", null);
				new DownloadImageTask(img_profile)
				.execute(loginimage);
			}
		}
		
		
		btn_profile = (Button) findViewById(R.id.btn_update);
		btn_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				fullname = edt_fullname.getText().toString();
				mail = edt_mailid.getText().toString();
				username = edt_username.getText().toString();
				new PostDataAsyncTask().execute();
			}
		});

		btn_delete = (Button) findViewById(R.id.btn_delete);
		btn_delete.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create(); // Read
																							// Update
				alertDialog.setTitle("Delete?");
				alertDialog.setMessage("Are you sure you want to delete account?");

				alertDialog.setButton("Continue..", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// here you can add functions
						String prodel = "delete";
						SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
						editor.putString("delete", "" + prodel);

						editor.commit();
						Intent iv = new Intent(Profile.this, Home.class);
						startActivity(iv);
					}
				});

				alertDialog.show();

			}
		});

		img_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
	}

	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		Bitmap mIcon11;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];

			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", "" + e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	public class PostDataAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// do stuff before posting data
		}

		@Override
		protected String doInBackground(String... strings) {
			try {

				// postText();
				postdata();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String lenghtOfFile) {
			// do stuff after posting data
			Log.d("successful", "successful");
			
			new getlogin().execute();
		}
	}

	private void postdata() {
		// TODO Auto-generated method stub
		
		Log.d("datacheck", ""+fullname+picturepath+username+mail+userloginid);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpEntity entity;
		if(picturepath!=null)
		{
			 entity = MultipartEntityBuilder.create().addTextBody("email", mail)
					.addTextBody("fullname", fullname).addBinaryBody("file", new File(picturepath),
							ContentType.create("application/octet-stream"), "filename")
					.addTextBody("username", username).addTextBody("user_id", userloginid).build();
		}
		else {
			 entity = MultipartEntityBuilder.create().addTextBody("email", mail)
					.addTextBody("fullname", fullname)
					.addTextBody("username", username).addTextBody("user_id", userloginid).build();
		}

		HttpPost httpPost = new HttpPost(getString(R.string.link) + "rest/update_user.php");
		httpPost.setEntity(entity);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity result = response.getEntity();
		if (result != null) {

			// String responseStr = "";
			try {
				responseStr = EntityUtils.toString(result).trim();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v("Response", "Response: " + responseStr);

			// you can add an if statement here and do other actions based
			// on the response
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaColumns.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturepath = cursor.getString(columnIndex);
			SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
			editor.putString("picturepath", "" + picturepath);

			editor.commit();
			Log.d("picturepath", "" + picturepath);
			cursor.close();

			img_profile.setImageBitmap(BitmapFactory.decodeFile(picturepath));

		}

	}

	public class getlogin extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Profile.this);
			progressDialog.setMessage("Loading..");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// TODO Auto-generated method stub

			URL hp = null;
			try {
				login.clear();

				/*
				 * SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,
				 * MODE_PRIVATE);
				 * 
				 * if (prefs.getString("timestamp", null) != null) { timestamp =
				 * prefs.getString("timestamp", null);
				 * 
				 * } else { // timestamp = "off"; }
				 */
				// Log.d("timestamp", "" + timestamp);
				// hp = new
				// URL("http://192.168.1.107/store/rest/category.php?timestamp=1403283079");

				/*
				 * hp = new URL(getString(R.string.link) +
				 * "rest/user_register.php?name=" + fullname + "&username=" +
				 * username + "&email=" + mail + "&password=" + password);
				 * Log.d("URL", "" + hp); URLConnection hpCon =
				 * hp.openConnection(); hpCon.connect();
				 * 
				 * InputStream input = hpCon.getInputStream(); Log.d("input", ""
				 * + input);
				 * 
				 * BufferedReader r = new BufferedReader(new
				 * InputStreamReader(input));
				 * 
				 * String x = ""; x = r.readLine(); String total = "";
				 * 
				 * while (x != null) { total += x; x = r.readLine(); }
				 * Log.d("URL", "" + total);
				 */
				JSONObject jObject = new JSONObject(responseStr);
				Log.d("URL1", "" + jObject);
				String currentKey = "";
				Iterator<String> iterator = jObject.keys();
				while (iterator.hasNext()) {
					currentKey = iterator.next();
					Log.d("currentkey", "" + currentKey);
				}
				if (currentKey.equals("Status")) {
					key = "status";
					JSONArray j1 = jObject.getJSONArray("Status");
					Log.d("jsonarray", "" + j1);
					for (int i = 0; i < j1.length(); i++) {

						JSONObject Obj;
						Obj = j1.getJSONObject(i);
						// JSONArray jarr = Obj.getJSONArray("images");
						Logingetset temp = new Logingetset();
						/*
						 * for (int k = 0; k < jarr.length(); k++) { JSONObject
						 * Obj1; Obj1 = j.getJSONObject(k);
						 * temp.setImages(Obj1.getString("images")); String
						 * images = Obj1.getString("images"); Log.d("images", ""
						 * + images);
						 * 
						 * }
						 */

						temp.setId(Obj.getString("id"));

						// temp.setLat(Obj.getString("lat"));

						login.add(temp);

					}
				} else if (currentKey.equals("User Info")) {
					key = "user";
					JSONArray j = jObject.getJSONArray("User Info");
					// JSONArray j = new JSONArray(total);
					Log.d("jsonarray", "" + j);
					Log.d("URL1", "" + j);
					for (int i = 0; i < j.length(); i++) {

						JSONObject Obj;
						Obj = j.getJSONObject(i);
						// JSONArray jarr = Obj.getJSONArray("images");
						Logingetset temp = new Logingetset();
						/*
						 * for (int k = 0; k < jarr.length(); k++) { JSONObject
						 * Obj1; Obj1 = j.getJSONObject(k);
						 * temp.setImages(Obj1.getString("images")); String
						 * images = Obj1.getString("images"); Log.d("images", ""
						 * + images);
						 * 
						 * }
						 */

						temp.setUser_id(Obj.getString("user_id"));
						temp.setName(Obj.getString("name"));
						temp.setUsername(Obj.getString("username"));
						temp.setEmail(Obj.getString("email"));
						temp.setImage(Obj.getString("image"));
						user2 = Obj.getString("user_id");
						user_name = Obj.getString("username");
						full_name = Obj.getString("name");
						emailid = Obj.getString("email");
						imageprofile = Obj.getString("image");
						// temp.setLat(Obj.getString("lat"));

						login.add(temp);

					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Error = e.getMessage();
			} catch (NullPointerException e) {
				// TODO: handle exception
				Error = e.getMessage();
			}

		
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			// Log.d("id", login.get(0).getId());
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (key.equals("user")) {
				SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
				editor.putString("score", "" + user2);
				editor.putString("username", "" + user_name);
				editor.putString("fullname", "" + full_name);
				editor.putString("emilid", "" + emailid);
				editor.putString("imageprofile", "" + imageprofile);
				editor.commit();
				Intent iv = new Intent(Profile.this, Home.class);
				startActivity(iv);
			} else if (key.equals("status")) {
				Toast.makeText(Profile.this, "try different one", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }
}
