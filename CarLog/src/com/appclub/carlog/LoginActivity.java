package com.appclub.carlog;

import java.util.ArrayList;
//import java.util.HashMap;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	EditText inputEmail;
	EditText inputPassword;
	TextView loginErrorMsg;

	// JSON Response node names
	private static final String KEY_SUCCESS = "success";
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
		setContentView(R.layout.login_layout);

		// Edit Text
		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.password);

		// Create Buttons
		Button btnLogin = (Button) findViewById(R.id.sign_in_button);
		// Button btnLinkToRegister = (Button)
		// findViewById(R.id.btnLinkToRegisterScreen);

		loginErrorMsg = (TextView) findViewById(R.id.login_status_message);

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				new LogUserIn().execute();
			};

		});
	}

	/**
	 * Background ASync Task to Create new product
	 * */
	class LogUserIn extends AsyncTask<String, String, String>
	{

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Logging in..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Logging in
		 * */
		@Override
		protected String doInBackground(String... args)
		{
			String email = inputEmail.getText().toString();
			String password = inputPassword.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("password", password));

			// getting JSON Object
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.loginUser(email, password);

			// check LogCat for response
			Log.d("Login Response", json.toString());

			// check for login response
			try
			{
				if (json.getString(KEY_SUCCESS) != null)
				{
					// loginErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS);
					if (Integer.parseInt(res) == 1)
					{
						// user successfully logged in
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

						// KEY_IS_ADMIN saved in String isadmin
						String isadmin = json_user.getString(KEY_IS_ADMIN);

						System.out.println(isadmin);

						// if user is Admin go to AdminActivity
						if (isadmin.equals("true"))
						{
							System.out.println("Admin");

							// Launch Register Screen
							Intent register = new Intent(
									getApplicationContext(),
									RegisterActivity.class);

							// Close all views before launching
							// RegisterActivity
							register.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							// GoTo RegisterActivity
							startActivity(register);

							// if user is not Admin go to
							// dashboardActivity
						} else
						{
							System.out.println("No Admin");

							// Launch DashBoard Screen
							Intent dashboard = new Intent(
									getApplicationContext(),
									DashboardActivity.class);

							// Close all views before launching
							// DashBoard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							// GoTo DashBoardActivity
							startActivity(dashboard);
						}

						// Close Login Screen
						finish();

					} else
					{
						// Error in login
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
		@Override
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
						Toast.makeText(LoginActivity.this,
								"Incorrect username/password!",
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