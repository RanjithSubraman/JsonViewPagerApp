package com.goovis.windows;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goovis.adapter.CustomAdapter;
import com.goovis.internet.ConnectionDetector;
import com.goovis.json.JSONParser;
import com.goovis.pojoclasses.Product;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private List<Product> productList = new ArrayList<Product>();;
	// Pager images list
	private HashMap<Integer, ArrayList<String>> pagerImageMap = new HashMap<Integer, ArrayList<String>>();

	private ListView productListView;
	private ListAdapter productArrayAdapter;
	// Refresh menu item
	private MenuItem refreshMenuItem;
	private boolean refreshFlag = false;

	private Fragment imageFragment;

	// Internet connection availability class
	private ConnectionDetector internetConnectionDetector;
	// Creating JSON Parser object
	private JSONParser jParser = new JSONParser();

	String jsonProductURL = "https://www.zalora.com.my/mobile-api/women/clothing";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		productListView = (ListView) findViewById(R.id.product_listview);
		internetConnectionDetector = new ConnectionDetector(this);
		if (cacheObjectReading() != null) {
			updateListViewFromCache();
		}

		productListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (refreshFlag
						&& internetConnectionDetector.isConnectingToInternet()) {
					imageFragment = new FragmentImages();
					Bundle imageListBundle = new Bundle();
					imageListBundle.putStringArrayList("imageList",
							pagerImageMap.get(position));
					imageFragment.setArguments(imageListBundle);

					if (imageFragment != null) {
						getFragmentManager().beginTransaction()
								.replace(R.id.frame_container, imageFragment)
								.addToBackStack("back").commit();
					}
					productListView.setVisibility(View.GONE);
				} else if (internetConnectionDetector.isConnectingToInternet()) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Please wait !!!", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					Toast toast = Toast.makeText(getApplicationContext(),
							"No Internet Connection", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		if (cacheObjectReading() != null) {
			productListView.setVisibility(View.VISIBLE);
			updateListViewFromCache();
			if (imageFragment != null) {
				getFragmentManager().beginTransaction().remove(imageFragment)
						.commit();
			}
		}
	}

	private void updateListViewFromCache() {
		if (cacheObjectReading() != null) {
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					productArrayAdapter = new CustomAdapter(
							getApplicationContext(), cacheObjectReading());
					productListView.setAdapter(productArrayAdapter);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		refreshMenuItem = menu.getItem(1);
		registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (internetConnectionDetector.isConnectingToInternet()) {
					new LoadAllProduct().execute();
				}
			}
		}, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_about:
			Toast.makeText(getApplicationContext(), "Version 1.0",
					Toast.LENGTH_LONG).show();
			return true;
		case R.id.action_rate:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=com.trader.imageapp"));
			startActivity(intent);
			return true;
		case R.id.action_refresh:
			refreshMenuItem = item;
			if (internetConnectionDetector.isConnectingToInternet()) {
				new LoadAllProduct().execute();
			} else {
				showAlertDialog(MainActivity.this, "No Internet Connection",
						"You don't have internet connection.", false);
			}
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (imageFragment != null) {
			boolean isFragment = imageFragment.isVisible();
			menu.findItem(R.id.action_refresh).setVisible(!isFragment);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@SuppressWarnings("unchecked")
	private List<Product> cacheObjectReading() {
		FileInputStream fileInputStream;
		List<Product> productList = null;
		try {
			fileInputStream = getApplicationContext().openFileInput(
					"carmudi.txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(
					fileInputStream);
			productList = (List<Product>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return productList;

	}

	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProduct extends AsyncTask<String, String, String> {
		// JSON Node names
		JSONObject json;
		private static final String TAG_SUCCESS = "success";

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// set the progress bar view
			refreshMenuItem.setActionView(R.layout.action_progressbar);
			refreshMenuItem.expandActionView();
		}

		/**
		 * getting All products from URL
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			json = jParser.makeHttpRequest(jsonProductURL, "GET", params);

			// Check your log cat for JSON response
			Log.d("Response: ", json.toString());

			// Checking for SUCCESS TAG
			String success;
			try {
				success = json.getString(TAG_SUCCESS);
				Log.d("success: ", success);
				if (success == "true") {
					JSONObject metaDataJsonObjec = json
							.getJSONObject("metadata");
					if (metaDataJsonObjec != null) {
						JSONArray resultJsonArray = metaDataJsonObjec
								.getJSONArray("results");
						if (resultJsonArray != null) {
							for (int resultIndex = 0; resultIndex < resultJsonArray
									.length(); resultIndex++) {
								JSONObject resultJsonObjec = resultJsonArray
										.getJSONObject(resultIndex);
								if (resultJsonObjec != null) {
									JSONObject dataJsonObject = resultJsonObjec
											.getJSONObject("data");
									String productName = dataJsonObject
											.getString("name");
									String productPrice = dataJsonObject
											.getString("price");
									String productBrand = dataJsonObject
											.getString("brand");

									JSONArray imageJsonArray = resultJsonObjec
											.getJSONArray("images");
									JSONObject productImageIndexZeroObject = imageJsonArray
											.getJSONObject(0);
									String productImageUrl = productImageIndexZeroObject
											.getString("path");
									Bitmap productIamgeBitmap = urlToBitmapConversion(productImageUrl);

									// Image pager view
									ArrayList<String> pagerImagesList = new ArrayList<String>();
									for (int imageIndex = 0; imageIndex < imageJsonArray
											.length(); imageIndex++) {
										JSONObject productImageIndexObject = imageJsonArray
												.getJSONObject(imageIndex);
										String indexProductImageUrl = productImageIndexObject
												.getString("path");

										pagerImagesList
												.add(indexProductImageUrl);
									}
									pagerImageMap.put(resultIndex,
											pagerImagesList);

									/*
									 * Product product = new Product(
									 * productIamgeBitmap, productName,
									 * Double.parseDouble(productPrice),
									 * productBrand);
									 */
									Product product = new Product(
											productImageUrl, productName,
											Double.parseDouble(productPrice),
											productBrand);
									productList.add(product);
								}
							}
						}
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			// pDialog.dismiss();
			refreshMenuItem.collapseActionView();
			// remove the progress bar view
			refreshMenuItem.setActionView(null);
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					productArrayAdapter = new CustomAdapter(
							getApplicationContext(), productList);
					productListView.setAdapter(productArrayAdapter);
					cacheObjectWriting(productList);
				}
			});
			refreshFlag = true;
		}

		private Bitmap urlToBitmapConversion(String imageUrl) {
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						imageUrl).getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		private void cacheObjectWriting(List<Product> productList) {
			FileOutputStream fileOutputStream;
			try {
				fileOutputStream = getApplicationContext().openFileOutput(
						"carmudi.txt", Context.MODE_PRIVATE);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(
						fileOutputStream);
				objectOutputStream.writeObject(productList);
				objectOutputStream.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
