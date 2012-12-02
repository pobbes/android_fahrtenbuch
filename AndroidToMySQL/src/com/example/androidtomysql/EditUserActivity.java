package com.example.androidtomysql;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditUserActivity extends Activity {

	EditText txtUsername;
	EditText txtEmail;
	EditText txtPassword;
	Button btnSave;
	Button btnDelete;

	String id;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// URL to user_details
	private static final String url_user_details = "http://appclub.bplaced.net/android_connect/get_user_details.php";

	// URL to update_user
	private static final String url_update_user = "http://appclub.bplaced.net/android_connect/update_user.php";
	
	// URL to delete_user
	private static final String url_delete_user = "http://appclub.bplaced.net/android_connect/delete_user.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USER = "user";
	private static final String TAG_ID = "id";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_PASSWORD = "password";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_user);

		// save button
		btnSave = (Button) findViewById(R.id2.btnSave);
		btnDelete = (Button) findViewById(R.id2.btnDelete);

		// getting user_details from intent
		Intent i = getIntent();
		
		// getting user_id from intent
		id = i.getStringExtra(TAG_ID);

		// Getting complete user details in background thread
		new GetUserDetails().execute();

		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update user
				new SaveUserDetails().execute();
			}
		});

		// Delete button click event
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting user in background thread
				new DeleteUser().execute();
			}
		});

	}

	/**
	 * Background Async Task to Get complete user details
	 * */
	class GetUserDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditUserActivity.this);
			pDialog.setMessage("Loading user details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting user details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("id", id));

						// getting user details by making HTTP request
						// Note that user details URL will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_user_details, "GET", params);

						// check your log for JSON response
						Log.d("Single User Details", json.toString());
						
						// JSON success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received user details
							JSONArray userObj = json
									.getJSONArray(TAG_USER); // JSON Array
							
							// get first user object from JSON Array
							JSONObject user = userObj.getJSONObject(0);

							// user with this id found
							// Edit Text
							txtUsername = (EditText) findViewById(R.id2.inputUsername);
							txtEmail = (EditText) findViewById(R.id2.inputEmail);
							txtPassword = (EditText) findViewById(R.id2.inputPassword);

							// display user data in EditText
							txtUsername.setText(user.getString(TAG_USERNAME));
							txtEmail.setText(user.getString(TAG_EMAIL));
							txtPassword.setText(user.getString(TAG_PASSWORD));

						}else{
							// user with id not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

	/**
	 * Background Async Task to  Save user Details
	 * */
	class SaveUserDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditUserActivity.this);
			pDialog.setMessage("Saving user ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving user
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String username = txtUsername.getText().toString();
			String email = txtEmail.getText().toString();
			String password = txtPassword.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_ID, id));
			params.add(new BasicNameValuePair(TAG_USERNAME, username));
			params.add(new BasicNameValuePair(TAG_EMAIL, email));
			params.add(new BasicNameValuePair(TAG_PASSWORD, password));

			// sending modified data through HTTP request
			// Notice that update user URL accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_user,
					"POST", params);

			// check JSON success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about user update
					setResult(100, i);
					finish();
				} else {
					// failed to update user
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
			// dismiss the dialog once user updated
			pDialog.dismiss();
		}
	}

	/*****************************************************************
	 * Background Async Task to Delete User
	 * */
	class DeleteUser extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditUserActivity.this);
			pDialog.setMessage("Deleting User...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting user
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", id));

				// getting user details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_user, "POST", params);

				// check your log for JSON response
				Log.d("Delete User", json.toString());
				
				// JSON success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// user successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about user deletion
					setResult(100, i);
					finish();
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
			// dismiss the dialog once user deleted
			pDialog.dismiss();

		}

	}
}
