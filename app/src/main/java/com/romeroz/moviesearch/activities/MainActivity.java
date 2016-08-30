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
import com.romeroz.moviesearch.custom.CustomViewPager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // UI
    private ViewPagerAdapter mViewPagerAdapter;
    private CustomViewPager mViewPager;
    private LinearLayout mSearchLayout;

    // AppBar
    private EditText mSearchEditText;
    private AppCompatImageButton mSearchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSearchLayout = (LinearLayout) findViewById(R.id.search_layout);
        mSearchEditText = (EditText) findViewById(R.id.search_edit_text);
        mSearchButton = (AppCompatImageButton) findViewById(R.id.search_button);

        /**
         * Set up drawer
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // Hide the keyboard
                Utility.hideSoftKeyboard(MainActivity.this);
            }
        };
        drawer.addDrawerListener(toggle);
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

        // Searches after you press "Search" in soft keyboard
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
        String searchText = mSearchEditText.getText().toString();

        // Get SearchFragment (first fragment)
        SearchFragment searchFragment = (SearchFragment) mViewPagerAdapter.getItem(0);
        searchFragment.searchForMovie(searchText);

        // Hide keyboard
        Utility.hideSoftKeyboard(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Making sure ToolBar stays relevant on rotate
        updateToolBarUI();
    }

    private void updateToolBarUI(){
        int currentItem = mViewPager.getCurrentItem();

        if(currentItem == 0){
            mSearchLayout.setVisibility(View.VISIBLE);
        } else if (currentItem == 1){
            mSearchLayout.setVisibility(View.GONE);
        }

        // Set ToolBar Title
        getSupportActionBar().setTitle(mViewPagerAdapter.getPageTitle(currentItem));
    }

    /**
     * Set up the ViewPager
     */
    private void setupViewPager() {
        SearchFragment searchFragment = SearchFragment.newInstance();
        FavoritesFragment favoritesFragment = FavoritesFragment.newInstance();

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(searchFragment, "Search");
        mViewPagerAdapter.addFragment(favoritesFragment, "Favorites");

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mViewPagerAdapter);

        // Disable swiping left and right
        mViewPager.setPagingEnabled(false);

        updateToolBarUI();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            // Scroll ViewPager to page without animation
            mViewPager.setCurrentItem(0, false);

        } else if (id == R.id.nav_favorites) {

            mViewPager.setCurrentItem(1, false);
        }

        updateToolBarUI();

        // Set title (Only works if there are no other views inside of Toolbar view)
        getSupportActionBar().setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}
