package com.example.ChuniInputEmu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setupSharedPreferences();

        androidx.preference.PreferenceManager.setDefaultValues(this,R.xml.preferences, false);

        sharedPref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddress = sharedPref.getString(SettingsActivity.KEY_PREF_IPADDRESS, "test");

        Toast.makeText(this, "Sending data to " + ipAddress ,Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_fullscreen)
        {
            getSupportActionBar().hide();
        }
        return super.onOptionsItemSelected(item);
    }
}
