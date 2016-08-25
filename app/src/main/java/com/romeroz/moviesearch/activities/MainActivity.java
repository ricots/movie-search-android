package com.romeroz.moviesearch.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romeroz.moviesearch.R;
import com.romeroz.moviesearch.Utility;
import com.romeroz.moviesearch.adapters.ViewPagerAdapter;
import com.romeroz.moviesearch.fragments.FavoritesFragment;
import com.romeroz.moviesearch.fragments.SearchFragment;
import com.romeroz.moviesearch.fragments.SettingsFragment;
import com.romeroz.moviesearch.ui.CustomViewPager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // UI
    private ViewPagerAdapter mViewPagerAdapter;
    private CustomViewPager mViewPager;

    // From AppBar
    private EditText mSearchEditText;
    private AppCompatImageButton mSearchButton;

    //private TextView mToolbarTitleTextView;
    private LinearLayout mSearchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //mToolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitleTextView);
        mSearchLayout = (LinearLayout) findViewById(R.id.search_layout);
        mSearchEditText = (EditText) findViewById(R.id.search_edit_text);
        mSearchButton = (AppCompatImageButton) findViewById(R.id.search_button);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager();
        }

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSearchMovieHandler();
            }
        });

        // Searches after you press "Search" in softkeyboard
        // Remember to set android:imeOptions="actionSearch" in the last EditText
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH) {
                    buttonSearchMovieHandler();
                    return true;
                }
                return false;
            }
        });
    }

    private void buttonSearchMovieHandler(){
        // Get SearchFragment (first fragment)
        SearchFragment searchFragment = (SearchFragment) mViewPagerAdapter.getItem(0);
        searchFragment.searchForMovie(mSearchEditText.getText().toString());

        // Hide keyboard
        Utility.hideSoftKeyboard(MainActivity.this);
    }

    /**
     * Set up the ViewPager
     */
    private void setupViewPager() {
        SearchFragment searchFragment = SearchFragment.newInstance();
        FavoritesFragment favoritesFragment = FavoritesFragment.newInstance("","");
        SettingsFragment settingsFragment = SettingsFragment.newInstance("","");

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(searchFragment, "Search");
        mViewPagerAdapter.addFragment(favoritesFragment, "Favorites");
        mViewPagerAdapter.addFragment(settingsFragment, "Settings");

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mViewPagerAdapter);

        // Disable swiping left and right
        mViewPager.setPagingEnabled(false);

        // First fragment toolbar
        showSearchToolbar();
    }

    private void showSearchToolbar(){
        mSearchLayout.setVisibility(View.VISIBLE);
    }

    private void hideSearchToolbar() {
        mSearchLayout.setVisibility(View.GONE);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            // Scroll ViewPager to page without animation
            mViewPager.setCurrentItem(0, false);
            showSearchToolbar();

        } else if (id == R.id.nav_favorites) {

            mViewPager.setCurrentItem(1, false);
            hideSearchToolbar();

        } else if (id == R.id.nav_settings) {

            mViewPager.setCurrentItem(2, false);
            hideSearchToolbar();

        }
        // Set title (Only works if there are no other views inside of Toolbar view)
        getSupportActionBar().setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
