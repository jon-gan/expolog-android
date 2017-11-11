package googleplaydeveloper.jdotspacegdot.expolog;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    private String[] mTitles;
    private String[] mTitlesDetail;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mCurrentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitles = getResources().getStringArray(R.array.titles);
        mTitlesDetail = getResources().getStringArray(R.array.titles_detail);
        mDrawerList = (ListView)findViewById(R.id.drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_activated_1, mTitles
        ));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(mCurrentPosition);
        }
        else {
            mCurrentPosition = 0;
            selectItem(0, null);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        Fragment fragment = getFragmentManager().findFragmentByTag("top");
                        if (fragment instanceof PlanFragment) {
                            mCurrentPosition = 0;
                        }
                        else if (fragment instanceof HierarchyFragment) {
                            mCurrentPosition = 1;
                        }
                        else if (fragment instanceof ExposureFragment) {
                            mCurrentPosition = 2;
                        }
                        else if (fragment instanceof HelpFragment) {
                            mCurrentPosition = 3;
                        }
                        else if (fragment instanceof PlanEditFragment) {
                            mCurrentPosition = 4;
                        }
                        else if (fragment instanceof ReflectFragment) {
                            mCurrentPosition = 5;
                        }
                        else if (fragment instanceof HierarchyEditFragment) {
                            mCurrentPosition = 6;
                        }
                        else if (fragment instanceof ExposureEditFragment) {
                            mCurrentPosition = 7;
                        }
                        setActionBarTitle(mCurrentPosition);
                        mDrawerList.setItemChecked(mCurrentPosition, true);
                    }
                }
        );

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        super.onSaveInstanceState(instanceState);
        instanceState.putInt("position", mCurrentPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position, null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItem(int position, Bundle args) {
        mCurrentPosition = position;
        Fragment fragment;
        switch(position) {
            case 1:
                fragment = new HierarchyFragment();
                break;
            case 2:
                fragment = new ExposureFragment();
                break;
            case 3:
                fragment = new HelpFragment();
                break;
            // detail activities are 4-7
            case 4:
                fragment = new PlanEditFragment();
                break;
            case 5:
                fragment = new ReflectFragment();
                break;
            case 6:
                fragment = new HierarchyEditFragment();
                break;
            case 7:
                fragment = new ExposureEditFragment();
                break;
            default:
                fragment = new PlanFragment();
        }
        if (args != null) {
            fragment.setArguments(args);
        }
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, "top")
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        setActionBarTitle(position);

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void setActionBarTitle(int position) {
        String title;
        if (position < mTitles.length) {
            title = mTitles[position];
        }
        else {
            title = mTitlesDetail[position - mTitles.length];
        }
        getActionBar().setTitle(title);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager manager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}
