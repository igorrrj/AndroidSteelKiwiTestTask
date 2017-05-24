package com.example.igor.gitapiexample.search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.igor.gitapiexample.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Igor on 24.02.2017.
 */

public class SearchRepoAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> result;
    HashMap<String, String> map = new HashMap<String, String>();
    private Context context;

    public SearchRepoAdapter(Context context, ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        this.result = arraylist;
    }

    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View listViewItem = inflater.inflate(R.layout.item_search, parent, false);
        TextView fullName, description,language,starCount,forks;
        fullName = (TextView) listViewItem.findViewById(R.id.full_name);
        description = (TextView) listViewItem.findViewById(R.id.description);
        language = (TextView) listViewItem.findViewById(R.id.language);
        starCount = (TextView) listViewItem.findViewById(R.id.stargazers_count);
        forks = (TextView) listViewItem.findViewById(R.id.forks);
        try {

            map = result.get(position);
            fullName.setText(map.get("full_name"));
            description.setText(map.get("description"));
            language.setText(map.get("language"));
            starCount.setText(map.get("stargazers_count"));
            forks.setText(map.get("forks_count"));


        } catch (Exception e) {
            Log.e("SearchRepoAdapterException:", e.getMessage());
            e.printStackTrace();
        }

        return listViewItem;
    }
}