package com.inc.playground.playground;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AddEvent extends Activity {
	private static int RESULT_LOAD_IMAGE = 1;
	private Uri imageUri;
	WebView web_addstore;
	ProgressDialog progressBar;

	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addstore);

		web_addstore = (WebView) findViewById(R.id.web_addstore);

		web_addstore.getSettings().setJavaScriptEnabled(true);
		web_addstore.getSettings().getUserAgentString();

		web_addstore.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		progressBar = ProgressDialog.show(AddEvent.this, "Playground", "Loading...");

		web_addstore.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i("12", "Processing webview url click...");
				view.loadUrl(url);

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
		web_addstore.loadUrl("https://playground-1290.appspot.com/addevent.php");
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
}
