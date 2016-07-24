package com.inc.playground.playgroundApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.inc.playground.playgroundApp.utils.Constants;
import com.inc.playground.playgroundApp.utils.Logingetset;
import com.inc.playground.playgroundApp.utils.NetworkUtilities;

public class Register extends Activity {
	EditText edt_fullname, edt_mailid, edt_username, edt_password;
	Button btn_register;
	String emailpattern, fullname, mail, username, password,loginimage;
	ArrayList<Logingetset> login;
	String Error, user2, user_name, full_name, email_id, imageprofile;
	String key, picturepath;
	ImageView img_profile;
	private static int RESULT_LOAD_IMAGE = 1;
	public static final String MY_PREFS_NAME = "Store";
	String file, encodedString;
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	private Bitmap bitmap;
	String uploadImage, Full_name, User_name, emailid;
	String responseStr, prodel;
	ProgressDialog progressDialog;
	Typeface tf1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
		if (prefs.getString("fullname", null) != null) {
			Full_name = prefs.getString("fullname", null);
		} else {
			Full_name = "";
		}

		if (prefs.getString("delete", null) != null) {
			prodel = prefs.getString("delete", null);
		} else {
			prodel = "";
		}

		if (prefs.getString("emilid", null) != null) {
			email_id = prefs.getString("emilid", null);
		} else {
			email_id = "";
		}

		if (prefs.getString("username", null) != null) {
			User_name = prefs.getString("username", null);
		} else {
			User_name = "";
		}

		tf1 = Typeface.createFromAsset(Register.this.getAssets(), "fonts/Roboto-Light.ttf");
		TextView textview1 = (TextView) findViewById(R.id.txt_header);
		textview1.setTypeface(tf1);
		login = new ArrayList<Logingetset>();

		// img_profile.setImageResource(R.drawable.default_circle_img);

		edt_fullname = (EditText) findViewById(R.id.edt_fullname);
		edt_fullname.setText("" + Full_name);
		edt_mailid = (EditText) findViewById(R.id.edt_mailid);
		edt_mailid.setText("" + email_id);
		edt_password = (EditText) findViewById(R.id.edt_password);
		img_profile = (ImageView) findViewById(R.id.img_profile);
		if (prodel.equals("delete")) {
			edt_fullname.setText("");
			edt_mailid.setText("");
			edt_password.setText("");
			img_profile.setImageResource(R.drawable.default_circle_img);
		}

		if (prefs.getString("picturepath", null) != null) {
			picturepath = prefs.getString("picturepath", null);
			Log.d("PICTURE", "" + picturepath);
		}
		if (picturepath != null) {
			img_profile.setImageBitmap(decodeFile(picturepath));
			Log.d("picturepath", "" + picturepath);
			
		}else {
			if (prefs.getString("picture", null)!=null) {
				loginimage = prefs.getString("picture", null);
				new DownloadImageTask(img_profile)
				.execute(loginimage);
			}
		}
		

		img_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				fullname = edt_fullname.getText().toString();
				mail = edt_mailid.getText().toString();

				password = edt_password.getText().toString();

				if (mail.matches(emailpattern)) {
					if (fullname.equals("")) {
						edt_fullname.setError("Enter Fullname");
					} else {

						if (password.equals("")) {
							edt_password.setError("Enter Password");
						} else {
							// uploadImage();
							new PostDataAsyncTask().execute();

						}

					}
				} else {
					edt_mailid.setError("Enter Valid Email Address");
				}
			}

		});
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

			Log.d("picturepath", "" + picturepath);
			cursor.close();

			// img_profile.setImageBitmap(BitmapFactory.decodeFile(picturepath));
			img_profile.setImageBitmap(decodeFile(picturepath));
		}

	}

	public class getlogin extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//progressDialog = new ProgressDialog(Register.this);
			//progressDialog.setMessage("Loading..");
			//progressDialog.setCancelable(true);
			//progressDialog.show();
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

				hp = new URL(getString(R.string.link) + "rest/user_register.php?name=" + fullname + "&username="
						+ username + "&email=" + mail + "&password=" + password);
				Log.d("URL", "" + hp);
				URLConnection hpCon = hp.openConnection();
				hpCon.connect();

				InputStream input = hpCon.getInputStream();
				Log.d("input", "" + input);

				BufferedReader r = new BufferedReader(new InputStreamReader(input));

				String x = "";
				x = r.readLine();
				String total = "";

				while (x != null) {
					total += x;
					x = r.readLine();
				}
				Log.d("URL", "" + total);
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
						temp.setFullimage(Obj.getString("fullimage"));
						user2 = Obj.getString("user_id");
						user_name = Obj.getString("username");
						full_name = Obj.getString("name");
						emailid = Obj.getString("email");
						imageprofile = Obj.getString("fullimage");
						// temp.setLat(Obj.getString("lat"));

						login.add(temp);

					}

				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Error = e.getMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Error = e.getMessage();
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
			/*if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}*/
			if (key.equals("user")) {

				SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
				editor.putString("score", "" + user2);
				editor.putString("username", "" + user_name);
				editor.putString("fullname", "" + full_name);
				editor.putString("emilid", "" + emailid);
				editor.putString("imageprofile", "" + imageprofile);
				editor.putString("picturepath", "" + picturepath);
				editor.commit();

				Intent iv = new Intent(Register.this, Home.class);
				startActivity(iv);

				Toast.makeText(Register.this, "Register Successful..", Toast.LENGTH_LONG).show();
			} else if (key.equals("status")) {
				Toast.makeText(Register.this, getString(R.string.error_duplicate_email), Toast.LENGTH_LONG).show();
			}
		}
	}

	public class PostDataAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// do stuff before posting data
			progressDialog = new ProgressDialog(Register.this);
			progressDialog.setMessage("Loading..");
			progressDialog.setCancelable(true);
			progressDialog.show();
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
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
				new getlogin().execute();
			}
			
		}
	}
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

	private void postdata() throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		String TAG = "REGISTER";
		HttpClient httpClient = new DefaultHttpClient();
		HttpEntity entity;
		JSONObject cred = new JSONObject();
		String urlQuery = "/register/";
		if(picturepath!=null)
		{

			Bitmap picAsBitmap = decodeFile(picturepath);
			String imgString = Base64.encodeToString(getBytesFromBitmap(picAsBitmap),
					Base64.NO_WRAP);

			try {
				cred.put(Constants.EMAIL, mail);
				cred.put(Constants.FULLNAME, fullname);
				cred.put(Constants.PASSWORD,password);
				cred.put(Constants.PHOTO,imgString);
			} catch (JSONException e) {
				Log.i(TAG, e.toString());
			}

		}else {
			entity = MultipartEntityBuilder.create().addTextBody("email", mail).addTextBody("name", fullname)
					.addTextBody("password", password)
					.addTextBody("username", username).build();
		}
		Log.i(TAG, cred.toString());
		String responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + urlQuery);
		JSONObject myObject = null;
		String responseStatus = null;
		try {
			myObject = new JSONObject(responseString);
			responseStatus = myObject.getString(Constants.RESPONSE_STATUS);
		} catch (JSONException e) {
			Log.i(TAG, e.toString());
			e.printStackTrace();
		}

		if (!responseStatus.equals(Constants.RESPONSE_OK)) {

			Logingetset temp = new Logingetset();
			temp.setId("Login Failed");
			login.add(temp);


		}
		Log.v("Response", "Response: " + responseStatus);

			// you can add an if statement here and do other actions based
			// on the response
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

}
