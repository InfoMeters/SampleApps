package com.infometers.sampleapp1;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;


// things from SDK
import com.infometers.devices.Converter;
import com.infometers.devices.Device;
import com.infometers.sdk.DeviceManager;
// how do we include just the sdk so we dont have to include other thigns?

// want to not have 
import com.infometers.enums.DeviceIds; // enum of devices types see DeviceIds.Java
import com.infometers.records.Record; // generic record class



/*
 * critcal for letting us know what is going on with SDK in a timely manner. might change it leter.
 * could be non critical maybe, but allows the sdk to notify the UX and user. Otherwise, the App / User level would need to always ask SDK for mStatus and data.
 * when it was designed we had a specific need to be able to deliever data into the UI but now it is not required.
 * interface, might have to have it, designed allow SDK to communicate with UX or just the app that needs the sdk data. (dont have another way)
 */
import com.infometers.interfaces.OnDeviceListener;
import com.infometers.serial.enums.ConnectionStatus;


// SDK : step 1 - DeviceDelegate interface
public class MainActivity extends Activity implements OnDeviceListener{
	// SDK : step 2 - Create Instance of SDK
    private DeviceManager mDeviceManager = new DeviceManager();
    private Device mDevice = null;
	private TextView mTextViewStatus;
    private int mProgress = 0;
    private String mMessage;
    private int mStatus;
    private Handler detailHandler;
    private Handler statusHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		statusHandler = new Handler() {
			@Override
			public void handleMessage(Message statusMsg) {
				String status = (String) statusMsg.obj;
				
				TextView statusTextView = (TextView)findViewById(R.id.textViewStatus);
				statusTextView.setText(status);
			}
		};

		// Set controls
        //mTextViewStatus = (TextView) findViewById(R.id.textViewStatus);        
        setTitle("Infometers SampleApp1 - " + DeviceIds.OneTouchUltraMini);
        onStatusMessage("Set DeviceManager : " + DeviceIds.OneTouchUltraMini);

		// SDK : 3 - initialize SerialPortManager and SDK
		Context context = this;
		OnDeviceListener deviceListener = this;
        mDeviceManager.init(context, deviceListener, "REPLACE_WITH_API_KEY"); // context , delegate

        // SDK : step 4 - setDevice for example LifeScan OneTouch Ultra Mini
        mDevice = mDeviceManager.createDevice(DeviceIds.OneTouchUltraMini);
	}

	@Override
	protected void onDestroy() {
        super.onDestroy();
        // SDK 5 : Add Cleanup
        mDeviceManager.cleanup();
	};

	
	// device delegate functions 
	// implementing the interface for device delegate
	
	
	@Override
	public void onStatusMessage(String message) {
        //mTextViewStatus.setText(String.format("[%d] : %s", mProgress, mMessage));
		
		this.mMessage = message;
		
		Message statusMsg = new Message();
		
		statusMsg.obj = message;
		
		statusHandler.sendMessage(statusMsg);
	}

	@Override
	public void onConnectionStatus(ConnectionStatus status) {
		// TODO Auto-generated method stub
		//mProgress = Converter.convertToInt(mStatus);
		//onStatusMessage("Connection Progress : " + mStatus);
		
		this.mStatus = Converter.convertToInt(status);
	}
	
	// UI activity functions
	
	public void onButtonConnectClicked(View v){
		// Sdk : step 6 - call connect function
		mDevice.connect();// connecting
		// we don't check if it is disconnected
	}

    public void onButtonOpenClicked(View v){
        // Sdk : step 6 - call connect function
        mDevice.open();// connecting
        // we don't check if it is disconnected
    }
	public void onButtonReadClicked(View v){
        // Sdk : step 7 - get records from device
        List<Record> records = mDevice.getRecords(); // reading records
        int count = 0;
        if(records != null)
        	count = records.size();
        	
		onStatusMessage("Got Records : " + count);
	}
	
	public void onButtonSmartReadClicked(View v){
		detailHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				@SuppressWarnings("unchecked")
				List<Record> records = (List<Record>) msg.obj;
				
				String tmp = "";
				
//				for(Record record : records) {
//					tmp += record.toString() + "\n";
//				}
				
				for(int i=0; i < records.size(); i++) {
					com.infometers.records.bloodglucose.Record bgr = (com.infometers.records.bloodglucose.Record) records.get(i);
					
					tmp += bgr.getDateString() + " " + Integer.toString(bgr.getValue()) + "\n";
					//Log.d("DEBUG", records.get(i).toString());
				}
				
				TextView detail = (TextView)findViewById(R.id.textViewData);
				detail.setText(tmp);
			}
		};
		
		Thread t = new Thread() {
			public void run() {
				// Sdk : step 7 - call connect function
                mDevice.connect();// connecting
				
				while(mStatus < 2) {}
				
				mDevice.open();
				
				while(mStatus < 3) {}
				
				// Sdk : step 9 - get records from device
				List<Record> records = mDevice.getRecords(); // reading records
				
				Message detailMsg = new Message();
				
				detailMsg.obj = records;
				
				detailHandler.sendMessage(detailMsg);
			}
		};
		
		t.start();
	}

	public void onButtonClearClicked(View v){
        mDevice.clear();
		onStatusMessage("Clear");
	}
}
