package com.via.viabluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by NedHuang on 2016/11/24.
 */

public class MainActivity extends FragmentActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            VIABluetooth viaBluetooth = new VIABluetooth();
            transaction.replace(R.id.sample_content_fragment, viaBluetooth).commit();
        }
    }
}
