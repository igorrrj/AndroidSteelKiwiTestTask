package com.example.igor.gitapiexample.repos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igor.gitapiexample.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Igor on 24.02.2017.
 */

public class UserRepoAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> result;
    HashMap<String, String> map = new HashMap<String, String>();
    private Context context;

    public UserRepoAdapter(Context context, ArrayList<HashMap<String, String>> arraylist) {
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
        final View listViewItem = inflater.inflate(R.layout.item_user_repos, parent, false);
        TextView name, description,language;
        name = (TextView) listViewItem.findViewById(R.id.name);
        description = (TextView) listViewItem.findViewById(R.id.description);
        language = (TextView) listViewItem.findViewById(R.id.language);

        try {

            map = result.get(position);
            String desc=map.get("description");
            String lang=map.get("language");
            if(lang.equals("null"))
            {
                ImageView img=(ImageView)listViewItem.findViewById(R.id.lang_icon);
                img.setVisibility(View.INVISIBLE);
                lang="";
            }
            if (desc.equals("null"))
                desc="No description";

            name.setText(map.get("name"));
            description.setText(desc);
            language.setText(lang);

        } catch (Exception e) {
            Log.e("UserRepoAdapterException:", e.getMessage());
            e.printStackTrace();
        }

        return listViewItem;
    }
}