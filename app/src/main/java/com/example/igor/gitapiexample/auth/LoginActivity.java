package com.example.igor.gitapiexample.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.igor.gitapiexample.MainActivity;
import com.example.igor.gitapiexample.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.igor.gitapiexample.Constants.ACCESS_TOKEN_URL;
import static com.example.igor.gitapiexample.Constants.INTENT_EXTRA_URL;
import static com.example.igor.gitapiexample.Constants.LOGIN_URL;
import static com.example.igor.gitapiexample.Constants.USER_URL;

public class LoginActivity extends AccountAuthenticatorActivity {
    public static final String USER_PREFERENCES = "user_data";
    public static final String USER_PREF_KEY_LOGIN = "login";
    public static final String USER_PREF_KEY_TOKEN = "token";
    public static final String USER_PREF_KEY_AVATAR = "avatar";

    public static final String LOGIN_PREFERENCES="login_pref";
    public static final String LOGIN_PREF_KEY="logined";

    public static final String AUTHTOKEN_TYPE="authtokenType";
    public static final String USERNAME = "username";

    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    Button logInButton;
    private AccountManager accountManager;
    private Account[] accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp=getSharedPreferences(LOGIN_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(LOGIN_PREF_KEY,false);
        editor.apply();
        Log.e("Loged?",sp.getBoolean(LOGIN_PREF_KEY,false)+"");

        logInButton=(Button)findViewById(R.id.loginButton);

        accountManager = AccountManager.get(this);
        accounts = accountManager.getAccountsByType(getString(R.string.accountType));
        if (accounts != null && accounts.length > 0) {
            openMain();
        }

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoadingDialog();
                openLogin();
            }
        });

    }

    void openLogin()
    {
        String gitLoginUrl=LOGIN_URL + "client_id="+ getString(R.string.client_id)+"&scope=" + getString(R.string.scopes);
        Intent intent = new Intent(this, LoginWebViewActivity.class);
        intent.putExtra(INTENT_EXTRA_URL, gitLoginUrl);
        startActivityForResult(intent, 0);
        Log.e("Opened Login","true");
        progressDialog.dismiss();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK)
        {
            onLoginUser(data.getData());
            openLoadingDialog();
            Log.e("On Activity Result","true");
            Log.e("Request Code=",requestCode+" | ResutCode= "+resultCode+"");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        onLoginUser(uri);
    }

    void onLoginUser(Uri uri)
    {
        if (uri != null && uri.getScheme().equals("http"))
        {
            sharedPreferences=getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
            String code=uri.getQueryParameter("code");
            sendTokenRequest(ACCESS_TOKEN_URL,code);
            Log.e("OnLogined User","true");

        }
    }

    void endAuth(String login)
    {
        String  token;
        token=sharedPreferences.getString(USER_PREF_KEY_TOKEN,"");
        progressDialog.setMessage("Loading user profile");
        Log.e("LOGINE",login);
        Account account=new Account(login,getString(R.string.accountType));
        Bundle data=new Bundle();
        data.putString("USER_NAME",login);
        data.putString("TOKEN",token);

        accountManager.addAccountExplicitly(account,null,data);
        accountManager.setAuthToken(account,getString(R.string.accountType),token);

        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, token);

        setAccountAuthenticatorResult(result);

        SharedPreferences sp=getSharedPreferences(LOGIN_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(LOGIN_PREF_KEY,true);
        editor.apply();

        Log.e("TOKN",token);

        Log.e("EndAuth","OK");
        openMain();
        progressDialog.dismiss();
    }

    void sendTokenRequest(String url, final String codeStr){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int bInd=response.indexOf("access_token=");
                int lInd=response.indexOf("&scope");

                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(USER_PREF_KEY_TOKEN,response.substring(bInd,lInd));
                editor.apply();
                Log.e("POstResponse", response);
                Log.e("Token", response.substring(bInd,lInd));

                sendUserRequest(response.substring(bInd,lInd));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorPost.Response", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_id",getString(R.string.client_id));
                params.put("client_secret", getString(R.string.client_secret));
                params.put("code",codeStr);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    void sendUserRequest(String token)
    {
        StringRequest stringRequest=new StringRequest(USER_URL+token, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject= new JSONObject(response);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(USER_PREF_KEY_LOGIN,jsonObject.getString("login"));
                    editor.putString(USER_PREF_KEY_AVATAR,jsonObject.getString("avatar_url"));
                    Log.e("AVATAR",jsonObject.getString("avatar_url"));
                    editor.apply();

                    endAuth(jsonObject.getString("login"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("ErrorPost.Response", error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Log.e("LOGIN",sharedPreferences.getString(USER_PREF_KEY_LOGIN,""));

    }

    private void openMain() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

     void openLoadingDialog() {
        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage( getString(R.string.dialog_message) );
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}


