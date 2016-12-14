package com.sumit.handleconfigchange;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sumit.handleconfigchange.taskfragment.TaskFragment;

public class MainActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_CURRENT_PROGRESS = "current_progress";
    private static final String KEY_PERCENT_PROGRESS = "percent_progress";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar mProgressBar;
    private TextView mPercentTextView;

    private TaskFragment mTaskFragment;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpEventHandlers();
    }

    private void setUpEventHandlers() {

        // Restore saved state.
        if (savedInstanceState != null) {
            mProgressBar.setProgress(savedInstanceState.getInt(KEY_CURRENT_PROGRESS));
            mPercentTextView.setText(savedInstanceState.getString(KEY_PERCENT_PROGRESS));
        }

        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is being retained
        // over a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTaskFragment.isRunning()) {
                    mTaskFragment.cancel();
                } else {
                    mTaskFragment.start();
                }
            }
        });

    }


    private void showAboutSnackBar() {
        Snackbar.make(coordinatorLayout, getString(R.string.about_app), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.dismiss), null).show();
    }

    private void initViews() {

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_horizontal);
        mPercentTextView = (TextView) findViewById(R.id.percent_progress);
        mPercentTextView.setText(getString(R.string.default_start_task_text));

        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            showAboutSnackBar();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreExecute() {
        Toast.makeText(this, R.string.task_started_msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProgressUpdate(int percent) {
        mProgressBar.setProgress(percent * mProgressBar.getMax() / 100);
        mPercentTextView.setText(getString(R.string.current_percent) + percent + " %");

    }

    @Override
    public void onCancelled() {
        mProgressBar.setProgress(0);
        mPercentTextView.setText(getString(R.string.default_start_task_text));
        Toast.makeText(this, R.string.task_cancelled_msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPostExecute() {
        mProgressBar.setProgress(mProgressBar.getMax());
        mPercentTextView.setText(getString(R.string.default_start_task_text));
        Toast.makeText(this, R.string.task_complete_msg, Toast.LENGTH_SHORT).show();

    }
}
