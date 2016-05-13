package com.inc.playground.playground;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import com.inc.playground.playground.utils.AlertDialogManager;
import com.inc.playground.playground.utils.ConnectionDetector;
import com.inc.playground.playground.utils.ImageLoader;
import com.inc.playground.playground.utils.Reviewgetset;
import com.inc.playground.playground.utils.User;

public class Review extends Activity {
	String id;
	ProgressDialog progressDialog;
	ArrayList<Reviewgetset> rest;
	String key, Error;
	ListView list_review;
	Button btn_add;
	View layout12;
	public static final String MY_PREFS_NAME = "Store";
	String uservalue;
	RelativeLayout rl_home, rl_back;
	ImageView img_back;
	EditText edt_comment;
	RatingBar rb1234;
	String usercomment, userrate;
	ArrayList<User> rest1;
	String pic;
	InterstitialAd mInterstitialAd;
	boolean interstitialCanceled;
	ConnectionDetector cd;
	AlertDialogManager alert = new AlertDialogManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);

		if (getString(R.string.bannerads).equals("yes")) {
			AdView mAdView = (AdView) findViewById(R.id.adView);
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
		} else if (getString(R.string.bannerads).equals("no")) {

			AdView mAdView = (AdView) findViewById(R.id.adView);
			mAdView.setVisibility(View.GONE);

		}

		if (getString(R.string.insertialads).equals("yes")) {
			interstitialCanceled = false;
			CallNewInsertial();
		} else if (getString(R.string.insertialads).equals("no")) {

		}
		
		Typeface tf = Typeface.createFromAsset(Review.this.getAssets(), "fonts/Roboto-Light.ttf");

		TextView txt_head = (TextView) findViewById(R.id.textView1);
		txt_head.setTypeface(tf);

		SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

		// check user is created or not
		if (prefs.getString("myfbpic", null) != null) {
			pic = prefs.getString("myfbpic", null);
		}
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		rest = new ArrayList<Reviewgetset>();
		rest1 = new ArrayList<User>();
		Intent iv = getIntent();
		id = iv.getStringExtra("id");
		Log.d("id123", "" + id);
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout12 = v;

				// list_review.setEnabled(false);
				SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

				// check user is created or not
				if (prefs.getString("score", null) != null) {
					uservalue = prefs.getString("score", null);
					Log.d("user3", uservalue);

					if (rl_back == null) {
						RelativeLayout rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);
						layout12 = getLayoutInflater().inflate(R.layout.ratedialog, rl_dialoguser, false);
						rl_dialoguser.addView(layout12);

						edt_comment = (EditText) layout12.findViewById(R.id.txt_description);
						rb1234 = (RatingBar) layout12.findViewById(R.id.rate1234);
						Button btn_submit = (Button) layout12.findViewById(R.id.btn_submit);
						btn_submit.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								// rl_adddialog.setVisibility(View.GONE);
								layout12 = v;
								// list_review.setEnabled(true);
								// rl_home.setAlpha(1.0f);

								try {
									usercomment = edt_comment.getText().toString().replace(" ", "%20");
									userrate = String.valueOf(rb1234.getRating());
									if (usercomment.equals(null)) {
										usercomment = "";
									}
								} catch (NullPointerException e) {
									// TODO: handle exception
								}

								Log.d("comment", "" + usercomment);
								Log.d("rate", "" + userrate);
								if (usercomment.equals("")) {
									edt_comment.setError("Review Please");
								} else {
									new getratedetail().execute();

									AlertDialog.Builder builder = new AlertDialog.Builder(Review.this);
									builder.setMessage(getString(R.string.dialog_description)).setTitle(getString(R.string.dialog_title));

									builder.setNeutralButton(android.R.string.ok,
											new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int id) {
											dialog.cancel();
											new getreviewdetail().execute();
										}
									});
									AlertDialog alert = builder.create();
									alert.show();
									View myView = findViewById(R.id.rl_back);
									ViewGroup parent = (ViewGroup) myView.getParent();
									parent.removeView(myView);
									img_back = (ImageView) findViewById(R.id.img_back);
								}
							}
						});

						Button btn_cancel = (Button) layout12.findViewById(R.id.btn_cancel);
						btn_cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								// list_review.setEnabled(true);
								View myView = findViewById(R.id.rl_back);
								ViewGroup parent = (ViewGroup) myView.getParent();
								parent.removeView(myView);
								img_back = (ImageView) findViewById(R.id.img_back);

							}
						});

					}
				} else {
					// Intent iv = new Intent(Review.this, Login.class);
					// iv.putExtra("key", "review");
					// iv.putExtra("id", "" + id);
					// startActivity(iv);

					RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
					if (rl_back == null) {
						Log.d("second", "second");
						final RelativeLayout rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);

						layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);

						rl_dialoguser.addView(layout12);
						rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Review.this, R.anim.popup));

						TextView txt_dia = (TextView) layout12.findViewById(R.id.txt_dia);
						txt_dia.setText(getString(R.string.error_required_registration));

						Button btn_yes = (Button) layout12.findViewById(R.id.btn_yes);
						btn_yes.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								// finish();
								// rl_dialoguser.setVisibility(View.GONE);
								View myView = findViewById(R.id.rl_back);
								ViewGroup parent = (ViewGroup) myView.getParent();
								parent.removeView(myView);
							}
						});
					}
				}
			}

		});

		new getreviewdetail().execute();
	}

	public class getreviewdetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Review.this);
			progressDialog.setMessage("Loading");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			getdetailforNearMe();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			// }
			// else {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
				if (key.equals("status")) {
					Toast.makeText(Review.this, getString(R.string.error_add_review), Toast.LENGTH_LONG).show();

				} else if (key.equals("user")) {

					list_review = (ListView) findViewById(R.id.list_review);
					LazyAdapter lazy = new LazyAdapter(Review.this, rest);
					list_review.setAdapter(lazy);

					list_review.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							// TODO Auto-generated method stub
							list_review.setEnabled(false);
							if (rl_back == null) {
								RelativeLayout rl_dialog = (RelativeLayout) findViewById(R.id.rl_infodialog);
								layout12 = getLayoutInflater().inflate(R.layout.reviewclick_dialog, rl_dialog, false);
								rl_dialog.addView(layout12);

								TextView txt_name_comment = (TextView) layout12.findViewById(R.id.txt_nameuser);
								txt_name_comment.setText("" +Html.fromHtml(rest.get(position).getUsername()));
								try {
									RatingBar rb = (RatingBar) layout12.findViewById(R.id.rate1234);
									rb.setRating(Float.parseFloat(rest.get(position).getRatting()));
								} catch (NumberFormatException e) {
									// TODO: handle exception
								}

								TextView txt_comment_desc = (TextView) layout12.findViewById(R.id.txt_desc);
								txt_comment_desc.setText("" + Html.fromHtml(rest.get(position).getReview()));

								String image = rest.get(position).getImage();

								ImageView img_user = (ImageView) layout12.findViewById(R.id.img_my);
								img_user.setImageResource(R.drawable.default_img);
								if (image != null) {
									new DownloadImageTask(img_user).execute(rest.get(position).getImage());
								} else {
									img_user.setImageResource(R.drawable.default_img);
								}

								Button btn_ok = (Button) layout12.findViewById(R.id.btn_ok);
								btn_ok.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										list_review.setEnabled(true);
										View myView = findViewById(R.id.rl_back);
										ViewGroup parent = (ViewGroup) myView.getParent();
										parent.removeView(myView);
									}
								});
							}
						}
					});
				}
				// }
			}
		}

	}

	private void getdetailforNearMe() {
		// TODO Auto-generated method stub

		URL hp = null;
		try {
			rest.clear();
			hp = new URL(getString(R.string.link) + "rest/get_ratting_review.php?store_id=" + id);

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
			JSONObject jObject = new JSONObject(total);
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
					Reviewgetset temp = new Reviewgetset();
					/*
					 * for (int k = 0; k < jarr.length(); k++) { JSONObject
					 * Obj1; Obj1 = j.getJSONObject(k);
					 * temp.setImages(Obj1.getString("images")); String images =
					 * Obj1.getString("images"); Log.d("images", "" + images);
					 * 
					 * }
					 */

					temp.setId(Obj.getString("id"));

					// temp.setLat(Obj.getString("lat"));

					rest.add(temp);

				}
			} else if (currentKey.equals("Review")) {
				key = "user";
				JSONArray j = jObject.getJSONArray("Review");
				// JSONArray j = new JSONArray(total);
				Log.d("jsonarray", "" + j);
				Log.d("URL1", "" + j);
				for (int i = 0; i < j.length(); i++) {

					JSONObject Obj;
					Obj = j.getJSONObject(i);
					// JSONArray jarr = Obj.getJSONArray("images");
					Reviewgetset temp = new Reviewgetset();
					/*
					 * for (int k = 0; k < jarr.length(); k++) { JSONObject
					 * Obj1; Obj1 = j.getJSONObject(k);
					 * temp.setImages(Obj1.getString("images")); String images =
					 * Obj1.getString("images"); Log.d("images", "" + images);
					 * 
					 * }
					 */

					temp.setReview_id(Obj.getString("review_id"));
					temp.setReview(Obj.getString("review"));

					temp.setStore_id(Obj.getString("store_id"));
					temp.setRatting(Obj.getString("ratting"));
					temp.setUsername(Obj.getString("username"));
					temp.setImage(Obj.getString("image"));
					// temp.setLat(Obj.getString("lat"));

					rest.add(temp);

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
	}

	public class LazyAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Reviewgetset> data;
		private LayoutInflater inflater = null;
		Typeface tf = Typeface.createFromAsset(Review.this.getAssets(), "fonts/Roboto-Medium.ttf");
		Typeface tf1 = Typeface.createFromAsset(Review.this.getAssets(), "fonts/Roboto-Light.ttf");

		String s;

		public LazyAdapter(Activity a, ArrayList<Reviewgetset> str) {
			activity = a;
			data = str;
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;

			if (convertView == null) {

				// vi = inflater.inflate(R.layout.reviewcell, null);
				vi = inflater.inflate(R.layout.reviewcell, null);
			}

			TextView txt_name = (TextView) vi.findViewById(R.id.text_name);
			txt_name.setText(Html.fromHtml(data.get(position).getUsername()));
			txt_name.setTypeface(tf);

			TextView txt_comment = (TextView) vi.findViewById(R.id.txt_review);
			txt_comment.setText(Html.fromHtml(data.get(position).getReview()));
			txt_comment.setTypeface(tf1);
			String image = "";
			/*
			 * if (pic != null) { image =
			 * data.get(position).getImage().replace("http", "https"); } else {
			 * image = data.get(position).getImage(); }
			 */
			image = data.get(position).getImage();
			Log.d("fbimageprofile123", "" + image);
			Log.d("username", "" + data.get(position).getUsername());
			ImageView img_user = (ImageView) vi.findViewById(R.id.img_user);
			img_user.setImageResource(R.drawable.default_img);
			if (image != null) {
				ImageLoader imgLoader = new ImageLoader(Review.this);

				imgLoader.DisplayImage(image, img_user);
				// new DownloadImageTask(img_user).execute(image+"?type=large");
			} else {
				img_user.setImageResource(R.drawable.default_img);
			}

			try {
				RatingBar rb = (RatingBar) vi.findViewById(R.id.rate1);

				rb.setRating(Float.parseFloat(data.get(position).getRatting()));
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}

			return vi;
		}
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

	public class getratedetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			URL hp = null;
			try {
				/*
				 * if (uservalue != null) { hp = new
				 * URL(getString(R.string.liveurl) + "userfeedback.php?res_id="
				 * + id + "&&user_id=" + uservalue + "&&ratting=" + userrate +
				 * "&&comment=" + usercomment);
				 * 
				 * // hp = new URL( //
				 * "http://192.168.1.106/restourant/userfeedback.php?res_id=" //
				 * + id + "&&user_id=" + uservalue // + "&&ratting=" + userrate
				 * + "&&comment=" // + usercomment); } else {
				 */
				hp = new URL(getString(R.string.link) + "rest/post_ratting_review.php?user_id=" + uservalue
						+ "&&store_id=" + id + "&&ratting=" + userrate + "&review=" + usercomment);

				// hp = new URL(
				// "http://192.168.1.106/restourant/userfeedback.php?res_id="
				// + id + "&&user_id=" + user2 + "&&ratting="
				// + userrate + "&&comment=" + usercomment);
				// }

				Log.d("userurl", "" + hp);
				URLConnection hpCon = hp.openConnection();
				hpCon.connect();
				InputStream input = hpCon.getInputStream();
				Log.d("input", "" + input);

				BufferedReader r = new BufferedReader(new InputStreamReader(input));

				String x = "";
				// x = r.readLine();
				String total = "";

				while (x != null) {
					total += x;
					x = r.readLine();
				}
				Log.d("totalid", "" + total);

				JSONObject j = new JSONObject("Status");

				Log.d("j", "" + j);
				for (int i = 0; i < j.length(); i++) {
					JSONObject Obj;
					Obj = j.getJSONObject(String.valueOf(i));
					User temp = new User();

					temp.setStatus(Obj.getString("Status"));
					Log.d("statusrate", "" + Obj.getString("Status"));
					rest1.add(temp);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				// TODO: handle exception
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

		}

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Your code to show add

				if (interstitialCanceled) {

				} else {

					if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
						mInterstitialAd.show();

					} else {

						// ContinueIntent();
					}
				}

			}
		}, 5000);
	}

	private void CallNewInsertial() {
		cd = new ConnectionDetector(Review.this);

		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(Review.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			return;
		} else {
			// AdView mAdView = (AdView) findViewById(R.id.adView);
			// AdRequest adRequest = new AdRequest.Builder().build();
			// mAdView.loadAd(adRequest);
			Log.d("call", "call");

			mInterstitialAd = new InterstitialAd(Review.this);
			mInterstitialAd.setAdUnitId(getString(R.string.insertial_ad_key));
			requestNewInterstitial();
			mInterstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdClosed() {

				}

			});

		}
	}

	private void requestNewInterstitial() {
		Log.d("request", "request");
		final AdRequest adRequest = new AdRequest.Builder().build();
		mInterstitialAd.loadAd(adRequest);

	}

	@Override
	public void onPause() {
		mInterstitialAd = null;
		interstitialCanceled = true;
		super.onPause();
	}

}
