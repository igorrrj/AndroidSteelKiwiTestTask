package com.example.igor.gitapiexample;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igor.gitapiexample.auth.LoginActivity;
import com.example.igor.gitapiexample.repos.UserReposFragment;
import com.example.igor.gitapiexample.search.SearchFragment;

import java.io.InputStream;

import static com.example.igor.gitapiexample.auth.LoginActivity.LOGIN_PREFERENCES;
import static com.example.igor.gitapiexample.auth.LoginActivity.LOGIN_PREF_KEY;
import static com.example.igor.gitapiexample.auth.LoginActivity.USER_PREFERENCES;
import static com.example.igor.gitapiexample.auth.LoginActivity.USER_PREF_KEY_AVATAR;
import static com.example.igor.gitapiexample.auth.LoginActivity.USER_PREF_KEY_LOGIN;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences loginPreferences = getSharedPreferences(LOGIN_PREFERENCES,MODE_PRIVATE);
        boolean ans=loginPreferences.getBoolean(LOGIN_PREF_KEY,false);

        if(!loginPreferences.getBoolean(LOGIN_PREF_KEY,false)){
           openLoginScreen();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new UserReposFragment()).commit();
        getSupportActionBar().setTitle("Repository");

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initializeUser();
    }
    private void openLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.nav_user_repos) {
            fragmentClass=UserReposFragment.class;
            getSupportActionBar().setTitle("Repository");
        } else if (id == R.id.nav_search) {
            fragmentClass=SearchFragment.class;
            getSupportActionBar().setTitle("Search");

        } else if (id == R.id.nav_about) {
            fragmentClass=AboutFragment.class;
            getSupportActionBar().setTitle("About");
        } else if (id == R.id.nav_log_out) {
            AccountManager accountManager=AccountManager.get(this);
            Account[] gitHubAccounts = accountManager.getAccountsByType(getString(R.string.accountType));

            for (Account account : gitHubAccounts) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    accountManager.removeAccountExplicitly(account);
                }
            }
            Intent in = new Intent(MainActivity.this, LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);
            finish();
            return false;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fragment!=null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void initializeUser()
    {
        View header=navigationView.getHeaderView(0);
        TextView userName=(TextView)header.findViewById(R.id.user_login);
        ImageView avatar=(ImageView) header.findViewById(R.id.avatarImage);

        SharedPreferences userPreferences=getSharedPreferences(USER_PREFERENCES,MODE_PRIVATE);
        userName.setText(userPreferences.getString(USER_PREF_KEY_LOGIN,""));

        new DownloadImageTask((ImageView) header.findViewById(R.id.avatarImage))
                .execute(userPreferences.getString(USER_PREF_KEY_AVATAR,""));
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView avatarImage;

        public DownloadImageTask(ImageView bmImage) {
            this.avatarImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon= BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("ErrorDownloadingImage", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            avatarImage.setImageBitmap(result);
        }
    }
}
