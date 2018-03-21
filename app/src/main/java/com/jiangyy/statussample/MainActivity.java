package com.jiangyy.statussample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jiangyy.statusview.StatusView;

public class MainActivity extends AppCompatActivity {

    private StatusView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatusView = findViewById(R.id.statusView);
    }

    public void testClick(View view) {
        Toast.makeText(this, "testClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.isLoading:
                mStatusView.isLoading();
                return true;
            case R.id.empty:
                mStatusView.isEmpty();
                return true;
            case R.id.noNetwork:
                mStatusView.isNoNetwork();
                return true;
            case R.id.error:
                mStatusView.isError();
                return true;
            case R.id.isFinished:
                mStatusView.isFinished();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
