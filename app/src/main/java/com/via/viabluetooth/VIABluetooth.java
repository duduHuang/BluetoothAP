package com.via.viabluetooth;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by NedHuang on 2016/11/24.
 */

public class VIABluetooth extends Fragment {

    private static final String TAG = "VIABluetooth";
    private BluetoothAP mBluetoothAP = null;
    private VIABluetooth instance = this;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        View v = getView();
        try {
            mBluetoothAP = new BluetoothAP(instance.getActivity(), v);
        } catch (Exception e) {
            return;
        }
        if (!mBluetoothAP.isBluetoothEnable()) {
            // If BT is not on, request that it be enabled.
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, mBluetoothAP.REQUEST_ENABLE_BT);
        }
        mBluetoothAP.setupService();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_viabluetooth, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothAP != null) {
            mBluetoothAP.releaseBluetooth();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mBluetoothAP.onActivityResult(requestCode, resultCode, data);
    }
}
