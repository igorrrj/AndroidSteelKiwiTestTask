package com.example.igor.gitapiexample.repos;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.igor.gitapiexample.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.igor.gitapiexample.Constants.USER_REPOSITORY_URL;

/**
 * Created by Igor on 19.05.2017.
 */

public class UserReposFragment extends Fragment {

    ProgressDialog progressDialog;
    ArrayList<HashMap<String,String>> repo_array;
    ListView userReposListView;
    String token;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_repos,container, false);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage( getString(R.string.dialog_message) );
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            getToken();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        userReposListView=(ListView)rootView.findViewById(R.id.listViewUserRepos);


        return rootView;
    }

    private void getToken() throws AuthenticatorException, OperationCanceledException, IOException {
        AccountManager accountManager=AccountManager.get(getActivity());
        Account[] accounts=accountManager.getAccountsByType(getString(R.string.accountType));
        if(accounts.length > 0)
        {
            AccountManagerFuture<Bundle> accountManagerFuture= accountManager.getAuthToken(accounts[0], getString(R.string.accountType), true, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        Bundle bundle = future.getResult();
                        if (bundle.containsKey(AccountManager.KEY_INTENT)) {
                            Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
                            intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, 0);
                        } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                            token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                            sendUserRepoRequest(token);
                        }
                    } catch (Exception e) {
                        Log.e("TEST", e.getMessage(), e);

                    }
                }
            },null);

        }

    }


    private void sendUserRepoRequest(String token) {
        StringRequest stringRequest=new StringRequest(USER_REPOSITORY_URL+token, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    showJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                 Log.e("ErrorUserRepo.Response", error.getMessage());
                progressDialog.dismiss();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void showJson(String response) throws JSONException {

        repo_array=new ArrayList<>();
        JSONArray repos = new JSONArray(response);

        for(int j=0;j<repos.length();++j)
        {
            JSONObject obj=repos.getJSONObject(j);
            HashMap<String,String> map=new HashMap<>();

            map.put("name",obj.getString("name"));
            map.put("description",obj.getString("description"));
            map.put("language",obj.getString("language"));

            repo_array.add(map);
        }

        userReposListView.setAdapter(new UserRepoAdapter(getActivity(),repo_array));
        progressDialog.cancel();
    }

}
