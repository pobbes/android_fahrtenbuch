package com.appclub.carlog;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import de.appclub.carlog.library.DatabaseHandler;
import de.appclub.carlog.library.UserFunctions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity
{
	EditText inputFullName;
	EditText inputEmail;
	EditText inputPassword;
	TextView registerErrorMsg;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	// private static String KEY_ERROR = "error";
	// private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_USER_ID = "user_id";
	private static String KEY_USERNAME = "username";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	private static String KEY_IS_ADMIN = "is_admin";

	public static ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);

		// Edit Text
		inputFullName = (EditText) findViewById(R.id.registerUsername);
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);

		// Create Buttons
		Button btnRegister = (Button) findViewById(R.id.btnRegister);
		// Button btnLinkToLogin = (Button)
		// findViewById(R.id.btnLinkToLoginScreen);

		// registerErrorMsg = (TextView) findViewById(R.id.register_error);

		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				new RegisterUser().execute();
			}

		});

		// // Link to Login Screen
		// btnLinkToLogin.setOnClickListener(new View.OnClickListener()
		// {
		//
		// public void onClick(View view)
		// {
		// Intent i = new Intent(getApplicationContext(),
		// LoginActivity.class);
		// startActivity(i);
		//
		// // Close Registration View
		// finish();
		// }
		// });
	}

	/**
	 * Background ASync Task to Create new product
	 * */
	class RegisterUser extends AsyncTask<String, String, String>
	{
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setMessage("Registering user...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Registering User
		 * */
		protected String doInBackground(String... args)
		{
			String fullname = inputFullName.getText().toString();
			String email = inputEmail.getText().toString();
			String password = inputPassword.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", fullname));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("password", password));

			// getting JSON Object
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.registerUser(fullname, email,
					password);

			// check LogCat from response
			Log.d("Register Response", json.toString());

			// check for register response
			try
			{
				if (json.getString(KEY_SUCCESS) != null)
				{
					registerErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS);
					if (Integer.parseInt(res) == 1)
					{
						// user successfully registered
						// Store user details in SQLite Database
						DatabaseHandler db = new DatabaseHandler(
								getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");

						// Clear all previous data in database
						userFunction.logoutUser(getApplicationContext());
						db.addUser(json_user.getString(KEY_USERNAME),
								json_user.getString(KEY_EMAIL),
								json.getString(KEY_USER_ID),
								json_user.getString(KEY_CREATED_AT),
								json_user.getString(KEY_IS_ADMIN));

						// Launch DashBoard Screen
						Intent dashboard = new Intent(getApplicationContext(),
								DashboardActivity.class);

						// Close all views before launching DashBoard
						dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(dashboard);

						// Close Registration Screen
						finish();

					} else
					{
						// Error in registration
						this.onPostExecute("error");
					}
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String result)
		{
			// If return of doInBackground is "error" show toast
			if ("error".equals(result))
			{
				// dismiss the dialog once done
				pDialog.dismiss();

				// show Toast with error message
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						Toast.makeText(RegisterActivity.this,
								"Error occured in registration!",
								Toast.LENGTH_SHORT).show();

					}
				});

			} else
			{
				// dismiss the dialog once done
				pDialog.dismiss();
			}

		}
	};

}