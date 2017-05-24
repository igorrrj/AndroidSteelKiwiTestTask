package com.example.igor.gitapiexample.search;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.igor.gitapiexample.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.igor.gitapiexample.Constants.SEARCH_URL;

public class SearchFragment extends Fragment {
    ListView repoListView;
    TextView total;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search,container, false);
        repoListView=(ListView)rootView.findViewById(R.id.listViewSReps);
        total=(TextView)rootView.findViewById(R.id.no_matches_text);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage( getString(R.string.dialog_message) );
        progressDialog.setCancelable(false);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                progressDialog.show();

                if(query.contains(" "))
                    query=query.replace(' ','+');
                sendRequest(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }

    void sendRequest(String searchText)
    {
        final String url = SEARCH_URL + searchText;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseRepo(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
    void parseRepo(String response)
    {
        ArrayList<HashMap<String, String>> result=new ArrayList<>();
        try {

            JSONObject obj=new JSONObject(response);
           if(obj.getString("total_count").equals("0"))
           {

                repoListView.setVisibility(View.GONE);
                total.setVisibility(View.VISIBLE);
                progressDialog.cancel();

           }
           else
            {

                JSONArray items=obj.getJSONArray("items");
                for(int i=0;i<items.length();i++)
                {
                    JSONObject jsonObject=items.getJSONObject(i);
                    HashMap<String, String>hm=new HashMap<>();
                    hm.put("full_name",jsonObject.getString("full_name"));

                    hm.put("stargazers_count",jsonObject.getString("stargazers_count"));
                    hm.put("forks_count",jsonObject.getString("forks_count"));

                    String desc=jsonObject.getString("description");
                    if(desc.equals("null"))
                        desc="No description";
                    hm.put("description",desc);

                    String lang=jsonObject.getString("language");
                    if(lang.equals("null"))
                        lang="";
                    hm.put("language",lang);

                    result.add(hm);
                }
                if(repoListView.getVisibility()!=View.VISIBLE)
                {
                   repoListView.setVisibility(View.VISIBLE);
                }
                repoListView.setAdapter(new SearchRepoAdapter(getActivity(),result));
                progressDialog.cancel();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
