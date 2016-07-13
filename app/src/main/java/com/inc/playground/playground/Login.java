package com.inc.playground.playground;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.EventUserObject;
import com.inc.playground.playground.utils.Logingetset;
import com.inc.playground.playground.utils.NetworkUtilities;

import com.inc.playground.playground.Register;
import com.inc.playground.playground.Review;
import com.inc.playground.playground.utils.User;


public class Login extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
	private static final String TAG = "MainActivity";
	String Error, user2;
	String key, id, method;
	Button btn_loginfb, btn_logingoogle;
	ArrayList<Logingetset> login;
	String imagefb,facebook_id;
	public static final String MY_PREFS_NAME = "Login";
	String value, personname, personemail,user_token;
	private static final int RC_SIGN_IN = 0;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	String personPhotoUrl;
	String ppic,fullname,user_name,fullimage,email_id;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;
	String name, email,userloginid;
	// private static String APP_ID = "823483137763059";
	private static String APP_ID = "1609067259420394";
	// Instance of Facebook Class
	public static Facebook facebook;
	public static AsyncFacebookRunner mAsyncRunner;

	private SharedPreferences mPrefs;
	View v;
	public static GlobalVariables globalVariables;
	public User currentUser;
	public String createdCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		globalVariables = ((GlobalVariables) this.getApplication());
		SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
		if (prefs.getString("userid", null) != null) {
			userloginid = prefs.getString("userid", null);
		}

		TextView textview1 = (TextView) findViewById(R.id.txt_header);

		buildGoogleApiClient();

		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);


		final ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primaryColor)));

		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		Intent iv = getIntent();
		value = iv.getStringExtra("key");
		id = iv.getStringExtra("id");

		login = new ArrayList<Logingetset>();

		btn_loginfb = (Button) findViewById(R.id.btn_fb);

		btn_loginfb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String prodel = "new";

				method = "facebook";
				SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
				editor.putString("myfbpic", "" + method);
				editor.putString("delete", "" + prodel);
				editor.commit();
				loginToFacebook();
//				Log.d("name123", "" + name);
//				Log.d("email123", "" + "http://graph.facebook.com/" + imagefb + "/picture");

				// logoutFromFacebook();

			}
		});
		btn_logingoogle = (Button) findViewById(R.id.btn_google);
		btn_logingoogle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//mGoogleApiClient.connect();
				String prodel = "new";
				SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
				editor.putString("delete", "" + prodel);
				editor.commit();
				method = "google";
				signInWithGplus();

				Log.d("personname", "" + personname);
				Log.d("personemail", "" + personemail);

			}
		});
	}

	private void buildGoogleApiClient() {
		// TODO Auto-generated method stub
		mGoogleApiClient = new GoogleApiClient.Builder(Login.this).addConnectionCallbacks(Login.this)
				.addOnConnectionFailedListener(Login.this).addApi(Plus.API, Plus.PlusOptions.builder().build())
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

	}

	public class getlogin extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			// TODO Auto-generated method stub

			URL hp = null;
			String url = null;

			try {
				// login.clear();
				JSONObject cred = new JSONObject();
				String urlQuery = "/login/";
				if (method.equals("login")) {
					try {
						//whats is it mostafa? can you comment here?
                        cred.put(Constants.MODE, Constants.LOGIN_MODE);
                        cred.put(Constants.EMAIL, "mostafa");
						cred.put(Constants.EMAIL, "mostafa");
						cred.put(Constants.PASSWORD, "12345");
					} catch (JSONException e) {
						Log.i(TAG, e.toString());
					}


				} else if (method.equals("google")) {
					try {
                        cred.put(Constants.MODE, Constants.GOOGLE_MODE);
						cred.put(Constants.EMAIL, personemail);
						cred.put(Constants.NAME, personname);
						cred.put(Constants.PHOTO_URL, personPhotoUrl);
					} catch (JSONException e) {
						Log.i(TAG, e.toString());
					}



				} else if (method.equals("facebook")) {
                    cred.put(Constants.MODE, Constants.FACEBOOK_MODE);
					cred.put(Constants.FACEBOOK_ID, facebook_id);
					cred.put(Constants.NAME, name);
					cred.put(Constants.PHOTO_URL, imagefb);
				}

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
				else{ //
					key = "user";

					JSONObject resonseMessage = myObject.getJSONObject(Constants.RESPONSE_MESSAGE);
					Log.d("jsonarray", "" + resonseMessage);
					Log.d("URL1", "" + resonseMessage);

					Logingetset temp = new Logingetset();
					if(method.equals("login")){
                        user_token = resonseMessage.getString(Constants.ID);
                        fullname = resonseMessage.getString(Constants.FULLNAME);
                        email_id = resonseMessage.getString(Constants.EMAIL);
                        user_name = email_id;
                        user2 = email_id;
                        fullimage = "https://storage.googleapis.com/sports-bucket/Source/1965583_10204825358656748_4079077085336938408_o.jpg";
                        //TODO:Upload user image instead
						createdCount = resonseMessage.getString(Constants.CREATED_COUNT);

                    }
                    else if(method.equals("facebook")){
                        user_token = resonseMessage.getString(Constants.ID);
                        fullname= name;
                        fullimage = imagefb;
                        email_id = email;
                        user2 =user_token;
                    }
                    else if(method.equals("google")){
                        user_token = resonseMessage.getString(Constants.ID);;
                        fullname= personname;
                        fullimage = personPhotoUrl;
                        email_id = personemail;
                        user2 =user_token;

                    }

					temp.setUser_id(user_token);
					temp.setName(fullname);
					temp.setUsername(email_id);
					temp.setEmail(email_id);
					temp.setImage(fullimage);
					login.add(temp);


				}

				// sorting data from miles wise in home page list
				/*
				 * Collections.sort(rest, new Comparator<Restgetset>() {
				 *
				 * @Override public int compare(Restgetset lhs, Restgetset rhs)
				 * { // TODO Auto-generated method stub return
				 * Double.compare(lhs.getMiles(), rhs.getMiles()); } });
				 */
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Error = e.getMessage();
			} catch (NullPointerException e) {
				// TODO: handle exception
				Error = e.getMessage();
			} catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			if (method.equals("login")) {
				if (key.equals("user")) {
					Toast.makeText(Login.this, user_token, Toast.LENGTH_LONG).show();
					SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
					editor.putString("userid", "" + user2);
					editor.putString("username", "" + user_name);
					editor.putString("emilid", "" + email_id);
					editor.putString("fullname", "" + fullname);
					editor.putString("picture", "" + fullimage);
					editor.commit();
//					if (value.equals("home")) {


                    Intent iv = new Intent(Login.this,Splash.class);
                    startActivity(iv);
					Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();

				} else if (key.equals("status")) {
					Toast.makeText(Login.this, "Username or Password is Incorrect", Toast.LENGTH_LONG)
							.show();
				}

			} else if (method.equals("facebook")) {
				if (key.equals("user")) {
					SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
					editor.putString("userid", "" + user_token);
					editor.putString("username", "" + user_name);
					editor.putString("emilid", "" + email_id);
					editor.putString("fullname", "" + fullname);
					editor.putString("picture", "" + fullimage);
					editor.commit();
                    Log.i("UserLogin", user_token);

					// create userObject
					createUserObject();
					Intent iv = new Intent(Login.this,MainActivity.class);
                    startActivity(iv);
					finish();
                    Toast.makeText(Login.this, "Login Successful with Facebook", Toast.LENGTH_LONG).show();


				} else if (key.equals("status")) {
					Toast.makeText(Login.this, "Username or Password is Incorrect", Toast.LENGTH_LONG)
							.show();
				}
			} else if (method.equals("google")) {
				if (key.equals("user")) {
					SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("userid", "" + user_token);
					editor.putString("username", "" + user_name);
                    editor.putString("emilid", "" + email_id);
                    editor.putString("fullname", "" + fullname);
					editor.putString("picture", "" + fullimage);
					editor.commit();
                    Log.i("UserLogin", user_token);
					// create userObject
					createUserObject();
                    Intent iv = new Intent(Login.this,MainActivity.class);
					startActivity(iv);
					finish();
                    Toast.makeText(Login.this, "Login Successful with Google+", Toast.LENGTH_LONG).show();

				} else if (key.equals("status")) {
					Toast.makeText(Login.this, "Username or Password is Incorrect", Toast.LENGTH_LONG)
							.show();
				}
			}

		}

	}

	public void getProfileInformation() {
		Bundle bundle = new Bundle();
		bundle.putString("fields", "id,name,first_name,last_name,email,picture,gender,birthday,work");
		mAsyncRunner.request("me", bundle, new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				mPrefs = getPreferences(MODE_PRIVATE);
				String app_id = mPrefs.getString("access_id", null);
				try {

					Bundle bundle = new Bundle();
					bundle.putString("fields", "id,name,first_name,last_name,email,picture,gender,birthday,work");
					Log.d("bundle", facebook.request("me", bundle));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("Profile", response);
				String json = response;
				try {
					JSONObject profile = new JSONObject(json); //facebook profile

					// getting name of the user
					name = profile.getString("name");
					// getting email of the user

					facebook_id = profile.getString("id");
					imagefb = facebook_id;
//                    try {
//                        email = profile.getString("email");
//                    }
//                    catch (JSONException e){
//
//                    }
					JSONObject picture = profile.getJSONObject("picture");
					JSONObject data = picture.getJSONObject("data");
					ppic = data.getString("url");
					Log.d("ppic", "" + ppic);
					Log.d("fbimage", "" + imagefb);
					Log.d("fbname", "" + name);
					if (name != null) {
						if (ppic != null) {
							name = name.replace(" ", "%20");

							imagefb = "https://graph.facebook.com/" + imagefb + "/picture?type=large";
//                            email = email.replace(" ", "%20");
							new getlogin().execute();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e, Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});

	}

	public void postToWall() {
		// post on user's wall.
		facebook.dialog(this, "feed", new DialogListener() {

			@Override
			public void onFacebookError(FacebookError e) {
			}

			@Override
			public void onError(DialogError e) {
			}

			@Override
			public void onComplete(Bundle values) {
			}

			@Override
			public void onCancel() {
			}
		});

	}

	public void loginToFacebook() {
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);

		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);

		}

		if (!facebook.isSessionValid()) {

			facebook.authorize(this, new String[] { "email", "publish_actions" },Facebook.FORCE_DIALOG_AUTH, new DialogListener() {

				@Override
				public void onCancel() {
					// Function to handle cancel event
					Log.d("hello", "hello");
				}

				@Override
				public void onComplete(Bundle values) {

					// Function to handle complete event
					// Edit Preferences and update facebook acess_token
					Log.d("hello", "hello1");
					Log.d("accesstoken", "" + facebook.getAccessToken());
					Log.d("accesstokenexp", "" + facebook.getAccessExpires());
					Log.d("accesstokenid", "" + facebook.getAppId());

					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires", facebook.getAccessExpires());
					editor.putString("access_id", facebook.getAppId());
					editor.commit();
                    getProfileInformation();
//                    Log.d("Finish", "" + facebook.getAppId());

				}

				@Override
				public void onError(DialogError error) {
					// Function to handle error
					Log.d("hello", "hello2");
				}

				@Override
				public void onFacebookError(FacebookError fberror) {
					// Function to handle Facebook errors
					Log.d("hello", "hello3");
				}

			});
		}
		Log.d("login", "login");

		// getProfileInformation();
	}


	public void logoutFromFacebook() {
		mAsyncRunner.logout(this, new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Logout from Facebook", response);
				if (Boolean.parseBoolean(response) == true) {
					// User successfully Logged out
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e, Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
        getProfileInformation1();
		updateUI(true);

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateUI(false);
	}

	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 */
	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			// btnSignIn.setVisibility(View.GONE);
			// btnSignOut.setVisibility(View.VISIBLE);
			// btnRevokeAccess.setVisibility(View.VISIBLE);
			// llProfileLayout.setVisibility(View.VISIBLE);
		} else {
			// btnSignIn.setVisibility(View.VISIBLE);
			// btnSignOut.setVisibility(View.GONE);
			// btnRevokeAccess.setVisibility(View.GONE);
			// llProfileLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * Sign-in into google
	 */
	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	/**
	 * Method to resolve any signin errors
	 */
	private void resolveSignInError() {
		if (mGoogleApiClient.isConnected()) {

		} else {
			if (mConnectionResult.hasResolution()) {
				try {
					mIntentInProgress = true;
					mConnectionResult.startResolutionForResult(Login.this, RC_SIGN_IN);


				} catch (SendIntentException e) {
					mIntentInProgress = false;
					mGoogleApiClient.connect();
				}
			}
		}

	}

	private void getProfileInformation1() {
		try {

			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				personname = currentPerson.getDisplayName();
				personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				personemail = Plus.AccountApi.getAccountName(mGoogleApiClient);

				Log.d("mydata", "Name: " + personname + ", plusProfile: " + personGooglePlusProfile + ", email: "
						+ personemail + ", Image: " + personPhotoUrl);
				Log.d("image", "" + personPhotoUrl);

				if (personname != null) {
					if (personemail != null) {
						personname = personname.replace(" ", "%20");
						personemail = personemail.replace(" ", "%20");
						personPhotoUrl = personPhotoUrl.replace("?sz=50", "");
						new getlogin().execute();
					}
				}
			} else {
				Toast.makeText(getApplicationContext(), "Person information is null", Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createUserObject()
	{
		currentUser = new User();
		currentUser.SetUserId(user_token);
		currentUser.setName(fullname);
		currentUser.setEmail(email_id);
		currentUser.setPhotoUrl(fullimage);

		new GetUserEventsAsyncTask().execute();
		globalVariables.SetCurrentUser(currentUser);
	}
	public class GetUserEventsAsyncTask extends AsyncTask<String, Integer, String> {

		private ProgressDialog dialog = new ProgressDialog(Login.this);

		@Override
		protected void onPreExecute() {
			initProgressDialog(dialog);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... strings) {
//			String responseString;
			String responseStringUserInfo;
			try {
				JSONObject cred = new JSONObject();
				try {
					cred.put(NetworkUtilities.TOKEN,"StubToken");
					cred.put(NetworkUtilities.USER_ID,currentUser.GetUserId());
				} catch (JSONException e) {
					Log.i(TAG, e.toString());
				}
//				responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_events_by_user/");
				responseStringUserInfo = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_user_info/");
			} catch (Exception ex) {
				Log.e(TAG, "getUserEvents.doInBackground or responseStringCreatedCount.doInBackground: failed to doPost");
				Log.i(TAG, ex.toString());
//				responseString ="";
				responseStringUserInfo = "";
			}
			// Convert string received from server to JSON array
			JSONArray eventsTableJSONArr;
//			JSONObject responseJSON=null;
			JSONObject ServerJSONUserInfo;
			JSONObject responseJSONUserInfo;
			try {
//				responseJSON = new JSONObject(responseString);
//				eventsFromServerJSON = responseJSON.getJSONArray(Constants.RESPONSE_MESSAGE);
				Set<String> userEvents = new HashSet<>();

				responseJSONUserInfo = new JSONObject(responseStringUserInfo);
				ServerJSONUserInfo = responseJSONUserInfo.getJSONObject(Constants.RESPONSE_MESSAGE);//.getJSONObject(Constants.EVENT_ENTRIES);//problem
				createdCount = ServerJSONUserInfo.getString("createdCount");
				eventsTableJSONArr = ServerJSONUserInfo.getJSONArray(Constants.EVENT_ENTRIES);//Todo:update what i get

//				ArrayList<EventsObject> userEventsObjectsOld = NetworkUtilities. eventListToArrayList(eventsTableJSONArr, globalVariables.GetCurrentLocation());

				ArrayList<EventUserObject> userEventsObjects =  NetworkUtilities.allUserEvents(ServerJSONUserInfo, globalVariables.GetCurrentLocation());

				for(EventUserObject eUObject : userEventsObjects ){
					String eventId = eUObject.GetId(); //currentObject.getString(Constants.EVENT_ID);
					userEvents.add(eventId);//TODO: need to update the other types of events?
				}
				currentUser.SetUserEvents(userEvents);
				currentUser.setUserEventsObjects(userEventsObjects);
				currentUser.setCreatedNumOfEvents(createdCount);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String lenghtOfFile) {
			// do stuff after posting data
			try
			{
				if(dialog.isShowing())
				{
					dialog.dismiss();
				}
				// do your Display and data setting operation here
			}
			catch(Exception e)
			{

			}
			Log.d("successful", "successful");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
	}

	private void initProgressDialog(ProgressDialog dialog)
	{
		String message = "Loading ...";
		SpannableString spanMessage = new SpannableString(message);
		spanMessage.setSpan(new RelativeSizeSpan(1.2f),0,spanMessage.length(),0);
		spanMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#104e8b")), 0, spanMessage.length(), 0);
		dialog.setTitle("Please wait");
		dialog.setMessage(spanMessage);
		dialog.setIcon(R.drawable.pg_loading);
		dialog.show();
		Window window = dialog.getWindow();
		window.setLayout(800,420);
	}
}
