package de.appclub.carlog.library;

import java.util.ArrayList;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
 
import android.content.Context;

public class UserFunctions
{

	private JSONParser jsonParser;
	 
    // Testing in LocalHost using WAMP or XAMPP
    // use http://10.0.2.2/ to connect to your LocalHost IE http://localhost/
    private static String loginURL = "http://appclub.bplaced.net/carlog_connect/";
    private static String registerURL = "http://appclub.bplaced.net/carlog_connect/";
 
    private static String login_tag = "login";
    private static String register_tag = "register";
 
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
 
    /**
     * function make Login Request
     * @param email
     * @param password
     * @param is_admin
     * */
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
//         return JSON
//         Log.e("JSON", json.toString());
        return json;
    }
 
    /**
     * function make Login Request
     * @param name
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String username, String email, String password){
//    	Building Parameters
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("tag", register_tag));
    	params.add(new BasicNameValuePair("username", username));
    	params.add(new BasicNameValuePair("email", email));
    	params.add(new BasicNameValuePair("password", password));
 
//    	getting JSON Object
    	JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
//    	return JSON
    			return json;
    }
 
    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
    

    /**
     * Function to check if user is admin
     * */
    public boolean isUserAdmin(String isadmin){
        if(isadmin == "true"){
            // user is admin
            return true;
        }
        return false;
    }
    
    
    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }
	
}
