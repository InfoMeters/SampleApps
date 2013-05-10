package com.infometers.sampleapp2;

/**
 * This is an example application using the InfoMeters SDK.
 *
 * It has is composed of:
 * - UX
 * - SDK connectivity
 * - DB
 *
 * InfoMeters 2012
 */

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.infometers.devices.Converter;
import com.infometers.devices.Device;
import com.infometers.enums.ConnectionStatus;
import com.infometers.enums.DeviceIds;
import com.infometers.enums.DeviceTypes;
import com.infometers.helpers.ListHelper;
import com.infometers.interfaces.OnDeviceListener;
import com.infometers.records.Record;
import com.infometers.sdk.DeviceManager;


public class MeterActivity extends ListActivity implements OnDeviceListener {
	private static String TAG = "com.infometers.sampleapp2";
	

    //region Static

    private static final String PREFS_NAME = "InfometersAutoLogBook";

    //endregion

    //region Private Members

    // SDK handle
    private DeviceManager mDeviceManager = new DeviceManager();
    private Device mDevice = null;
    private DeviceIds mDeviceId = DeviceIds.OneTouchUltraMini;

    // UI
    private TextView mTextViewStatus;
    private View mHeader = null;
    private ProgressBar mProgressBarConnection;

    // DB
    private MeterArrayAdapter<Record> mAdapter;
    private static final List<Record> mRecords = new ArrayList<Record>();

    //endregion

    //region Constructor
    public MeterActivity() {
    }
    //endregion

    //region Override functions
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.blood_glucose_main);
            setButtons();

            // SDK : 3 - initialize SerialPort and SDK
            Context context = this;
            OnDeviceListener deviceListener = this;
            mDeviceManager.init(context, deviceListener, "REPLACE_WITH_API_KEY"); // context , delegate
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void setButtonStyle(int id, Typeface typeface) {
        Button b;
        b = (Button) findViewById(id);
        b.setTypeface(typeface, 1);
        b.setBackgroundResource(R.drawable.button_custom);
    }

    private void setButtons() {
        // Type face font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        // buttons and other elements being set and created

        setButtonStyle(R.id.deviceConnect, typeface);
        setButtonStyle(R.id.deviceOpen, typeface);
        setButtonStyle(R.id.deviceRead, typeface);
        setButtonStyle(R.id.clearData, typeface);
        setButtonStyle(R.id.saveData, typeface);

        mTextViewStatus = (TextView) findViewById(R.id.statusValue);

        // setting text type to the Roboto Thin
        mTextViewStatus.setTypeface(typeface);

        //
        // ProgressBar
        //
        mProgressBarConnection = (ProgressBar) findViewById(R.id.progressBarConnection);
        mProgressBarConnection.setMax(3);
        mProgressBarConnection.setProgress(0);
    }

    @Override
    protected void onStart() {
        try {
            super.onStart();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        restorePreferences();
        showRecords();
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            mDeviceManager.cleanup();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if(item.hasSubMenu())
                return false;

            DeviceIds deviceId = DeviceIds.None;
            int itemId = item.getItemId();
            try {
                switch (itemId) {
                    // If home icon is clicked return to blood_glucose_main Activity
                    case android.R.id.home:
                        Intent intent = new Intent(this, MeterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.one_touch_ultra_mini:
                        deviceId = DeviceIds.OneTouchUltraMini;
                        break;
                    case R.id.one_touch_select:
                        deviceId = DeviceIds.OneTouchSelect;
                        break;
                    case R.id.one_touch_ultra2:
                        deviceId = DeviceIds.OneTouchUltra2;
                        break;
                    case R.id.one_touch_ultra_smart:
                        deviceId = DeviceIds.OneTouchUltraSmart;
                        break;
                    case R.id.and_blood_pressure_us767pc:
                        deviceId = DeviceIds.AndBloodPressureUS767PC;
                        break;
                    case R.id.and_scale_uc321pl_modeA:
                        deviceId = DeviceIds.AndScaleUC321PL;
                        break;
                    case R.id.and_scale_uc321pl_modeB:
                        deviceId = DeviceIds.AndScaleUC321PL;
                        break;
                    case R.id.embrace:
                        deviceId = DeviceIds.Embrace;
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            setDevice(deviceId);

            // Additional Settings
            if(deviceId == DeviceIds.AndScaleUC321PL){
                com.infometers.devices.andmedical.uc321pl.Device.Modes mode;
                if(itemId == R.id.and_scale_uc321pl_modeA){
                    mode = com.infometers.devices.andmedical.uc321pl.Device.Modes.ModeA;
                }
                else{
                    mode = com.infometers.devices.andmedical.uc321pl.Device.Modes.ModeB;
                }
                com.infometers.devices.andmedical.uc321pl.Device device = (com.infometers.devices.andmedical.uc321pl.Device)mDevice;
                device.setMode(mode);
                device.setCommSettings();
            }
        }
        catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }


    //endregion

    //region Interface DeviceDelegate Implementation
    @Override
    public void onStatusMessage(String status) {
        try {
            final String fStatus = status;
            runOnUiThread(new Runnable() {
                public void run() {
                    mTextViewStatus.setText(fStatus);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    int mProgress;

    @Override
    public void onConnectionStatus(ConnectionStatus status) {
        mProgress = Converter.convertToInt(status);
        mProgressBarConnection.setProgress(mProgress);
		onStatusMessage(status.toString());
    }

    //endregion


    public void onButtonConnectClicked(View v) {
        mDeviceManager.connect(mDevice);
    }

    public void onButtonOpenClicked(View v) {
        mDevice.open();
    }

    public void onButtonReadClicked(View v) {
    	Log.d(TAG, "[START] onButtonReadClicked");
        onRead(mDevice);
    	Log.d(TAG, "[STOP] onButtonReadClicked");
    }

    public void onButtonClearDataClicked(View v) {
        onClear();
    }

    public void onButtonSaveDataClicked(View v) {
        onExport();
    }

    public void onButtonSmartReadClicked(View v) {
        onSmartRead(mDevice);
    }

    //endregion

    //region Private functions

    private void restorePreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String sDeviceType = settings.getString("deviceType", DeviceIds.OneTouchUltraMini.toString());
        DeviceIds deviceId = Converter.convertToDeviceId(sDeviceType);
        if (deviceId == DeviceIds.None)
            deviceId = DeviceIds.OneTouchUltraMini;

        setDevice(deviceId);
    }

    private void savePreferences() {

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("deviceType", mDeviceId.toString());

        // Commit the edits!
        editor.commit();
    }

    private void setDevice(DeviceIds deviceId) {
        if(deviceId == DeviceIds.None)
            return;

        mDeviceId = deviceId;

        ActionBar ab = getActionBar();
        ab.setTitle("Infometers SampleApp2");
        ab.setSubtitle(deviceId.toString());        
        mDevice = mDeviceManager.createDevice(deviceId);
        DeviceTypes deviceType = Converter.convertToDeviceType(deviceId);
        onStatusMessage(String.format("Type=%s", deviceType.toString()));
        setListView(deviceType);

        savePreferences();
    }

    private void setListView(DeviceTypes deviceType) {
        int resourceHeader = R.layout.blood_glucose_header;
        int resourceItem = R.layout.blood_glucose_item;
        switch (deviceType) {
            case BloodGlucose:
                resourceHeader = R.layout.blood_glucose_header;
                resourceItem = R.layout.blood_glucose_item;
                break;
            case BloodPressure:
                resourceHeader = R.layout.blood_pressure_header;
                resourceItem = R.layout.blood_pressure_item;
                break;
            case Scale:
                resourceHeader = R.layout.scale_header;
                resourceItem = R.layout.scale_item;
                break;
        }
        ListView listView = getListView();
        if (listView.getHeaderViewsCount() > 0)
            listView.removeHeaderView(mHeader);

        mHeader = getLayoutInflater().inflate(resourceHeader, null);
        listView.addHeaderView(mHeader);
        mHeader.setVisibility(View.VISIBLE);
        mAdapter = new MeterArrayAdapter<Record>(this, resourceItem, mRecords);
        mAdapter.notifyDataSetInvalidated();
        setListAdapter(mAdapter);
    }

    private void onRead(Device device) {
        try {
        	Log.d(TAG, "[START] onRead");
            List<Record> records = device.getRecords();
            mRecords.clear();
            if (ListHelper.isNullOrEmpty(records))
                return;

            mRecords.addAll(records);
            showRecords();
        	Log.d(TAG, "[STOP] onRead");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void showRecords() {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetInvalidated();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void onClear() {
        mRecords.clear();
        showRecords();
    }

    private void onExport() {
        // mUsbDevice.onExport();
    }

    String message;
    Handler detailHandler, statusHandler;

    public void onSmartRead(final Device device) {

        Thread t = new Thread() {
            private void sleep2(int m) {
                try {
                    sleep(m);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            public void run() {
                Log.d(TAG, "###########################################################");
                Log.d(TAG, "Check if Connected!");
                if (!device.isConnected()) {
                    Log.d(TAG, "Not Connected!");
                    Log.d(TAG, "Connect()");
                    mDeviceManager.connect(device);
                    Log.d(TAG, "Wait until connected!");
                    while (!device.isConnected()) {
                        sleep2(100);
                    }
                    Log.d(TAG, "Connected!");
                }

                Log.d(TAG, "Check if Open!");
                if (!device.isOpen()) {
                    Log.d(TAG, "Not Open!");
                    Log.d(TAG, "Open()");
                    device.open();
                    Log.d(TAG, "Wait until opened!");
                    while (!device.isOpen()) {
                        sleep2(100);
                    }
                    Log.d(TAG, "Opened()");
                }

                Log.d(TAG, "Read()");
                onRead(device);
            }
        };

        t.start();
    }


    //endregion
}




