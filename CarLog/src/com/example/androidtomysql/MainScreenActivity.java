package com.example.androidtomysql;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
 
public class MainScreenActivity extends Activity{
 
    Button btnViewUsers;
    Button btnNewUser;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
 
        // Buttons
        btnViewUsers = (Button) findViewById(R.id.btnViewUsers);
        btnNewUser = (Button) findViewById(R.id.btnCreateUser);
 
        // view users click event
        btnViewUsers.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching All users Activity
                Intent i = new Intent(getApplicationContext(), AllUsersActivity.class);
                startActivity(i);
 
            }
        });
 
        // view users click event
        btnNewUser.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching create new user activity
                Intent i = new Intent(getApplicationContext(), NewUserActivity.class);
                startActivity(i);
 
            }
        });
    }
}
