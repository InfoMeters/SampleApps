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
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.infometers.devices.Converter;
import com.infometers.devices.Device;
import com.infometers.enums.DeviceIds;
import com.infometers.enums.DeviceTypes;
import com.infometers.helpers.ListHelper;
import com.infometers.helpers.Log;
import com.infometers.interfaces.OnAddRecordListener;
import com.infometers.interfaces.OnDeviceListener;
import com.infometers.records.Record;
import com.infometers.sdk.DeviceManager;
import com.infometers.serial.enums.ConnectionStatus;


public class MeterActivity extends ListActivity implements OnDeviceListener, OnAddRecordListener {
    protected static Log Log = new Log(true);

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
        com.infometers.helpers.Log.EnabledGlobalLogging(true);
    }
    //endregion

    //region Override functions
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            catchUncaughtException();

            setContentView(R.layout.activity_main);
            setButtons();

            // SDK : 3 - initialize SerialPortManager and SDK
            Context context = this;
            OnDeviceListener deviceListener = this;
            mDeviceManager.init(context, deviceListener, "REPLACE_WITH_API_KEY"); // context , delegate
        } catch (Exception e) {
            Log.e(e);
        }
    }

    Thread.UncaughtExceptionHandler mUEHandler;

    private void catchUncaughtException() {
        mUEHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                Log.e(android.util.Log.getStackTraceString(e));
                Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        };

        Thread.setDefaultUncaughtExceptionHandler(mUEHandler);
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
        setButtonStyle(R.id.buttonSmartRead, typeface);

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
            Log.e(e);
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
            Log.e(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.hasSubMenu())
                return false;

            DeviceIds deviceId = DeviceIds.None;
            int itemId = item.getItemId();
            try {
                switch (itemId) {
                    // If home icon is clicked return to activity_main Activity
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
                Log.e(e);
            }

            setDevice(deviceId);
            savePreferences();
            // Additional Settings
            if (deviceId == DeviceIds.AndScaleUC321PL) {
                boolean isModeA = itemId == R.id.and_scale_uc321pl_modeA;
                setScaleMode(isModeA);
            }
            setTitle();
        } catch (Exception ex) {
            Log.e(ex);
        }

        return true;
    }


    private void setScaleMode(boolean isModeA) {
        com.infometers.devices.andmedical.uc321pl.Device.Modes mode;
        if (isModeA) {
            mode = com.infometers.devices.andmedical.uc321pl.Device.Modes.ModeA;
        } else {
            mode = com.infometers.devices.andmedical.uc321pl.Device.Modes.ModeB;
        }
        com.infometers.devices.andmedical.uc321pl.Device device = (com.infometers.devices.andmedical.uc321pl.Device) mDevice;
        device.setMode(mode);
        device.setCommSettings();
        device.setRecordListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }


    //endregion

    //region Interface  - OnAddRecordListener

    @Override
    public void addRecord(Record record) {
        try {
            mRecords.add(record);
            showRecords();
        } catch (Exception e) {
            Log.e(e);
        }
    }

    //endregion


    //region Interface  - DeviceDelegate
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
            Log.e(e);
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
        mDevice.connect();
    }

    public void onButtonOpenClicked(View v) {
        mDevice.open();
    }

    public void onButtonReadClicked(View v) {
        Log.ds();
        onRead(mDevice);
        Log.ds();
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
        // Restore Mode for Scale
        if (mDeviceId == DeviceIds.AndScaleUC321PL) {
            String sMode = settings.getString("AndScaleUC321PL_Mode", com.infometers.devices.andmedical.uc321pl.Device.Modes.ModeA.toString());
            boolean isModeA = sMode.equals(com.infometers.devices.andmedical.uc321pl.Device.Modes.ModeA.toString());
            setScaleMode(isModeA);
        }
        setTitle();
    }

    private void savePreferences() {

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("deviceType", mDeviceId.toString());

        // Save Mode for Scale
        if (mDeviceId == DeviceIds.AndScaleUC321PL) {
            com.infometers.devices.andmedical.uc321pl.Device device = (com.infometers.devices.andmedical.uc321pl.Device) mDevice;
            com.infometers.devices.andmedical.uc321pl.Device.Modes mode = device.getMode();
            editor.putString("AndScaleUC321PL_Mode", mode.toString());
        }

        // Commit the edits!
        editor.commit();
    }

    private void setDevice(DeviceIds deviceId) {
        if (deviceId == DeviceIds.None)
            return;

        mDeviceId = deviceId;
        mDevice = mDeviceManager.createDevice(deviceId);
        DeviceTypes deviceType = Converter.convertToDeviceType(deviceId);
        setListView(deviceType);
    }

    private void setTitle() {
        ActionBar ab = getActionBar();
        ab.setTitle("Infometers SampleApp2");
        String subTitle = mDeviceId.toString();
        if (mDeviceId == DeviceIds.AndScaleUC321PL) {
            com.infometers.devices.andmedical.uc321pl.Device device = (com.infometers.devices.andmedical.uc321pl.Device) mDevice;
            com.infometers.devices.andmedical.uc321pl.Device.Modes mode = device.getMode();
            subTitle += " - " + mode.toString();
        }
        ab.setSubtitle(subTitle);
    }

    private void setListView(DeviceTypes deviceType) {
        int resourceHeader = R.layout.blood_glucose_header;
        int resourceItem = R.layout.blood_glucose_item;
        int resourceImage = R.drawable.diabetes;

        switch (deviceType) {
            case BloodGlucose:
                resourceHeader = R.layout.blood_glucose_header;
                resourceItem = R.layout.blood_glucose_item;
                resourceImage = R.drawable.diabetes;
                break;
            case BloodPressure:
                resourceHeader = R.layout.blood_pressure_header;
                resourceItem = R.layout.blood_pressure_item;
                resourceImage = R.drawable.blood_pressure;
                break;
            case Scale:
                resourceHeader = R.layout.scale_header;
                resourceItem = R.layout.scale_item;
                resourceImage = R.drawable.body_weight;
                break;
        }
        ListView listView = getListView();
        if (listView.getHeaderViewsCount() > 0)
            listView.removeHeaderView(mHeader);

        ImageView imageView = (ImageView)findViewById(R.id.imageViewDeviceType);
        if(imageView != null)
            imageView.setImageResource(resourceImage);

        mHeader = getLayoutInflater().inflate(resourceHeader, null);
        listView.addHeaderView(mHeader);
        mHeader.setVisibility(View.VISIBLE);
        mAdapter = new MeterArrayAdapter<Record>(this, resourceItem, mRecords);
        setListAdapter(mAdapter);
        showRecords();
    }

    private void onRead(Device device) {
        try {
            Log.ds();
            List<Record> records = device.getRecords();
            mRecords.clear();
            if (ListHelper.isNullOrEmpty(records))
                return;

            mRecords.addAll(records);
            showRecords();
            Log.de();
        } catch (Exception e) {
            Log.e(e);
        }
    }

    private void showRecords() {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            Log.e(e);
        }
    }

    private void onClear() {

        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    mRecords.clear();
                }
            });
        } catch (Exception e) {
            Log.e(e);
        }
        showRecords();
    }

    private void onExport() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,  getExportText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private String getExportText(){
        StringBuilder sb = new StringBuilder();
        for(Record r : mRecords ){
            sb.append(r.getText());
            sb.append("\r\n");
        }
        return sb.toString();
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
                Log.d("###########################################################");
                Log.d("Check if Connected!");
                if (!device.isConnected()) {
                    Log.d("Not Connected!");
                    Log.d("Connect()");
                    device.connect();
                    Log.d("Wait until connected!");
                    while (!device.isConnected()) {
                        sleep2(100);
                    }
                    Log.d("Connected!");
                }

                Log.d("Check if Open!");
                if (!device.isOpen()) {
                    Log.d("Not Open!");
                    Log.d("Open()");
                    Log.d("Wait until opened!");
                    while (!device.isOpen()) {
                        device.open();
                        sleep2(100);
                    }
                    Log.d("Opened()");
                }

                Log.d("Read()");
                onRead(device);
            }
        };

        t.start();
    }


    //endregion
}




