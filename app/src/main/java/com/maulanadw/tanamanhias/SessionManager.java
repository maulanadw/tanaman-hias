package com.maulanadw.tanamanhias;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    // Variabel
    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    // Session
    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    // User session variabel
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    // Remember me variabel
    private static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSIONEMAIL = "email";
    public static final String KEY_SESSIONPASSWORD = "password";

    // Constructor
    public SessionManager(Context _context, String sessionName){
        context = _context;
        usersSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    // User login session

    // Remember Me session functions
    public void createRememberMeSession(String email, String password){
        editor.putBoolean(IS_REMEMBERME, true);
        editor.putString(KEY_SESSIONEMAIL, email);
        editor.putString(KEY_SESSIONPASSWORD, password);

        editor.commit();
    }

    public HashMap<String, String> getRememberMeDetailsFromSession(){
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_SESSIONEMAIL, usersSession.getString(KEY_SESSIONEMAIL, null));
        userData.put(KEY_SESSIONPASSWORD, usersSession.getString(KEY_SESSIONPASSWORD, null));

        return userData;
    }

    public boolean checkRememberMe(){
        if (usersSession.getBoolean(IS_REMEMBERME, false)){
            return true;
        } else {
            return false;
        }
    }
}
