package com.appclub.carlog;

import org.json.JSONException;
import org.json.JSONObject;
 
import de.appclub.carlog.library.DatabaseHandler;
import de.appclub.carlog.library.UserFunctions;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class RegisterActivity extends Activity {
    Button btnRegister;
    Button btnLinkToLogin;
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_USER_ID = "user_id";
    private static String KEY_USERNAME = "username";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_IS_ADMIN = "is_admin";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
 
        // Importing all assets like buttons, text fields
        inputFullName = (EditText) findViewById(R.id.registerUsername);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
 
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.registerUser(name, email, password);
 
                // check for login response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        registerErrorMsg.setText("");
                        String res = json.getString(KEY_SUCCESS);
                        if(Integer.parseInt(res) == 1){
                            // user successfully registered
                            // Store user details in SQLite Database
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            JSONObject json_user = json.getJSONObject("user");
 
                            // Clear all previous data in database
                            userFunction.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_USERNAME), json_user.getString(KEY_EMAIL), json.getString(KEY_USER_ID), json_user.getString(KEY_CREATED_AT),json_user.getString(KEY_IS_ADMIN));
                            
                            // Launch DashBoard Screen
                            Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                            
                            // Close all views before launching DashBoard
                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(dashboard);
                            
                            // Close Registration Screen
                            finish();
                        }else{
                            // Error in registration
                            registerErrorMsg.setText("Error occured in registration");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
 
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                // Close Registration View
                finish();
            }
        });
    }
}