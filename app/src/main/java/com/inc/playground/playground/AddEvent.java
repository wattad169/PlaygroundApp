package com.inc.playground.playground;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.DownloadImageBitmapTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.inc.playground.playground.utils.NetworkUtilities.eventListToArrayList;


public class AddEvent extends Activity {
	private static int RESULT_LOAD_IMAGE = 1;
	private Uri imageUri;
	WebView web_addstore;
	ProgressDialog progressBar;
	public static GlobalVariables globalVariables;
	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;
	public static final String TAG = "AddEventActivity";
	Map<String,String> headers =  new HashMap<String,String>();

	public class JavaScriptInterface {
		Context mContext;
		Application currentApplication;

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c,Application inputApplication) {
			mContext = c;
			currentApplication = inputApplication;
		}

		/** Show a toast from the web page */
		@JavascriptInterface //not used
		public void showToast(String toast) {
			JSONObject responseJSON = null;
			JSONArray eventsFromServerJSON = null;
			globalVariables = ((GlobalVariables) this.currentApplication);
			try {
				responseJSON = new JSONObject(toast);
				eventsFromServerJSON = responseJSON.getJSONArray(Constants.RESPONSE_MESSAGE);
				Intent iv = new Intent(AddEvent.this,EventInfo.class);
				EventsObject newEvent = eventListToArrayList(eventsFromServerJSON, globalVariables.GetCurrentLocation()).get(0);
				ArrayList<EventsObject> newHomeEvents = globalVariables.GetHomeEvents();
				newHomeEvents.add(newEvent);
				globalVariables.SetHomeEvents(newHomeEvents);
				iv.putExtra("eventObject",newEvent);
				startActivity(iv);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addstore);
		setPlayGroundActionBar();
		web_addstore = (WebView) findViewById(R.id.web_addstore);
		web_addstore.addJavascriptInterface(new JavaScriptInterface(this,getApplication()), "Android");
		web_addstore.getSettings().setJavaScriptEnabled(true);
//		web_addstore.addJavascriptInterface(this, "android");
		web_addstore.getSettings().getUserAgentString();

		web_addstore.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		progressBar = ProgressDialog.show(AddEvent.this, "Playground", "Loading...");

		web_addstore.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i("12", "Processing webview url click..." + url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i("12", "Finished loading URL: " + url);
				if (progressBar.isShowing()) {
					progressBar.dismiss();
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Log.e("12", "Error: " + description);
				Toast.makeText(AddEvent.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
				alertDialog.setTitle("Error");
				alertDialog.setMessage(description);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						return;
					}
				});

				alertDialog.show();
			}
		});
		web_addstore.setWebChromeClient(new WebChromeClient() {

			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				AddEvent.this.showAttachmentDialog(uploadMsg);
			}

			// For Android > 3.x
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
				AddEvent.this.showAttachmentDialog(uploadMsg);
			}

			// For Android > 4.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
				AddEvent.this.showAttachmentDialog(uploadMsg);
			}

		});
		SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
		String usertoken = prefs.getString("userid", null);
		headers.put("uname",usertoken);
		web_addstore.loadUrl("https://playground-1290.appspot.com/addevent.php?uname="+usertoken);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();

			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

	public class myWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub

			view.loadUrl(url);
			return true;

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

		}
	}

	// flipscreen not loading again
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void showAttachmentDialog(ValueCallback<Uri> uploadMsg) {
		this.mUploadMessage = uploadMsg;

		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("image/*");

		this.startActivityForResult(Intent.createChooser(i, "Choose type of attachment"), FILECHOOSER_RESULTCODE);
	}
	public void setPlayGroundActionBar(){
		String userLoginId,userFullName,userEmail,userPhoto;
		Bitmap imageBitmap =null;
		GlobalVariables globalVariables;
		final ActionBar actionBar = getActionBar();
		final String MY_PREFS_NAME = "Login";
		SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
		globalVariables = ((GlobalVariables) this.getApplication());
		if (prefs.getString("userid", null) != null){
			userLoginId = prefs.getString("userid", null);
			userFullName = prefs.getString("fullname", null);
			userEmail = prefs.getString("emilid", null);
			userPhoto = prefs.getString("picture", null);
			actionBar.setCustomView(R.layout.actionbar_custom_view_home);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayUseLogoEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
			ImageView img_profile = (ImageView) findViewById(R.id.img_profile_action_bar);
			imageBitmap = globalVariables.GetUserPictureBitMap();
			if(imageBitmap==null){
				Log.i(TAG,"downloading");
				try {
					imageBitmap = new DownloadImageBitmapTask().execute(userPhoto).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

			}
			else {
				Log.i(TAG,"Image found");
			}
			img_profile.setImageBitmap(imageBitmap);
			globalVariables.SetUserPictureBitMap(imageBitmap); // Make the imageBitMap global to all activities to avoid downloading twice
		}
	}
}
