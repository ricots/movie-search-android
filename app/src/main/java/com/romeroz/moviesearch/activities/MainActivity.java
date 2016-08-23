package com.romeroz.moviesearch.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.romeroz.moviesearch.R;
import com.romeroz.moviesearch.adapters.ViewPagerAdapter;
import com.romeroz.moviesearch.fragments.FavoritesFragment;
import com.romeroz.moviesearch.fragments.SearchFragment;
import com.romeroz.moviesearch.ui.CustomViewPager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // UI
    private ViewPagerAdapter mViewPagerAdapter;
    private CustomViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

    /**
     * Set up the ViewPager
     */
    private void setupViewPager() {
        SearchFragment searchFragment = SearchFragment.newInstance("","");
        FavoritesFragment favoritesFragment = FavoritesFragment.newInstance("","");
        //ProfileFragment profileFragment = ProfileFragment.newLoggedInUserInstance();

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(searchFragment, "Search");
        mViewPagerAdapter.addFragment(favoritesFragment, "Favorites");
        //mViewPagerAdapter.addFragment(profileFragment, "Profile");
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mViewPagerAdapter);
        // Disable swiping left and right
        mViewPager.setPagingEnabled(false);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            // Scroll ViewPager to page without animation
            mViewPager.setCurrentItem(0, false);

        } else if (id == R.id.nav_favorites) {

            mViewPager.setCurrentItem(1, false);

        } else if (id == R.id.nav_settings) {

            //mViewPager.setCurrentItem(2, false);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
