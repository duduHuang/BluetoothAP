package com.via.viabluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by NedHuang on 2016/11/24.
 */

public class BluetoothAP {

    private static final String TAG = "BluetoothAP";
    private Activity mAct = null;
    private View mView = null;
    private String mAddress;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    protected static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothService mBluetoothService = null;

    private DeviceList mDeviceList = null;

    public BluetoothAP(Activity a, View view) throws Exception {
        mAct = a;
        mView = view;
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mConversationView = (ListView) view.findViewById(R.id.in);
        if (mBluetoothAdapter == null) {
            throw new Exception("Bluetooth is not available");
        }
    }

    public boolean isBluetoothEnable() {
        if (!mBluetoothAdapter.isEnabled()) {
            return false;
        } else {
            return true;
        }
    }

    public void releaseBluetooth() {
        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }

    /**
     * Set up the bluetooth service.
     */
    public void setupService() {
        Log.d(TAG, "setupService()");
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(mAct, R.layout.message);
        mConversationView.setAdapter(mConversationArrayAdapter);
        // Initialize the BluetoothChatService to perform bluetooth connections
        if (mBluetoothService != null) {
            mBluetoothService.stop();
            mBluetoothService = null;
        }
        mBluetoothService = new BluetoothService(mAct, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        if (mBluetoothService != null) {
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                mBluetoothService.start();
            }
        }
        scanDevice();
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != mAct) {
                        Toast.makeText(mAct, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != mAct) {
                        Toast.makeText(mAct, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, true);
//                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, false);
//                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    Log.d(TAG, "BT enabled");
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(mAct, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    mAct.finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
//    private void connectDevice(Intent data, boolean secure) {
     private void connectDevice(boolean secure) {
         Log.d(TAG, "connectDevice()");
        // Get the device MAC address
//        String address = data.getExtras().getString(DeviceList.EXTRA_DEVICE_ADDRESS);

        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);
        // Attempt to connect to the device
        mBluetoothService.connect(device, secure);
    }

    private void scanDevice() {
        mDeviceList = new DeviceList(mAct);

        mDeviceList.setOnConnectionListener(new DeviceList.onConnectionListener() {
            @Override
            public void getAddress(int resultCode, String address) {
                mAddress = address;
                Log.d(TAG, "getAddress: " + mAddress);
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(true);
                }
            }
        });
    }
}
