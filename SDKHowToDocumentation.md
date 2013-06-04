# SDK How To Documentation

## General Steps

1.  Download the **SDK Package** and place `infometers-android-sdk.jar` into the `libs` folder in your project.

2.  Set up the **InfoMeters SDK** by going thru the steps below. (See Version I: Making a new app and/or Version II: Adding to your app)

3.  Test your application by **reading data** into your **app** from medical monitoring device(s).

## Setting Up the Infometers SDK

Here we describe the simple, several step process to enabling the InfoMeters SDK to push  medical device data to your application or service.

**Version 1**: explains how to create an application from scratch with InfoMeters SDK. The resulting application code is included in the SampleApp1 folder.


## Version I: Empty App: (Sample App1)

### Step 1: Add Permissions to AndroidManifest.xml

Make sure you have the following permissions included in your manifest file:

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />


Also include:

    <uses-feature
        android:name="android.hardware.usb.host"
        android:required="true">
    </uses-feature>


### Step 2: Add the following Include statements

    // SDK : step 2 - include statements
    import android.content.Context;
    import android.os.Message;
    import java.util.List;

    import com.infometers.interfaces.OnDeviceListener;
    import com.infometers.serial.enums.ConnectionStatus;
    import com.infometers.enums.DeviceIds; // enum of devices types 
    import com.infometers.records.Record; // generic record class
    import com.infometers.devices.Device;
    import com.infometers.sdk.DeviceManager;

    // only needed if getting String from ConnectionStatus
    import com.infometers.devices.Converter;

### Step 3: Add OnDeviceListener Interface

    // SDK : step 3 - OnDeviceListener interface
    public class MainActivity extends Activity implements OnDeviceListener{

### Step 4: Instantiate class variables

    // SDK : step 4 - Create Instance of SDK DeviceManager and Device
    DeviceManager mDeviceManager = new DeviceManager();
    Device mDevice;
    String message;
    int status;

### Step 5: Add to OnCreate

Put these initialize calls into onCreate. These are called whenever the activity is started.

NOTE: You need to add your API key string here.

    // SDK : 5 - Initialize SerialPort and SDK 
    Context context = this;
    OnDeviceListener mOnDeviceListener = this;
    mDeviceManager.init(context, mOnDeviceListener, APIkey); // Please add your key here

    // Manually set the device here
    // SDK : setDevice for example LifeScan OneTouch Ultra Mini
    mDevice = mDeviceManager.createDevice(DeviceIds.OneTouchUltraMini);

### Step 6: Add to OnDestroy

    //SDK : step 6 - Add cleanup
    mDeviceManager.cleanup();

### Step 7: Implement OnDeviceListener Functions

    //SDK : step 7 - Implement OnDeviceListener interface functions
    public void onStatusMessage(String message){
      this.message = message;
      Message statusMsg = new Message();
      statusMsg.obj = message;

      //statusHandler described in onCreate in SampleApp1, sets the statusTextView
      statusHandler.sendMessage(statusMsg); 
    }

    public void onConnectionStatus(ConnectionStatus status){
      this.status = Converter.convertToInt(status);
    }

### Step 8-10: SmartRead: Use one click to connect to device, open connection, and read data

These calls need to be inside a separate Thread. The Message object created at the end is to be used with a Handler object to send information about the status back to the UI thread.

This is connected to the big SmartRead button on the bottom.

    //SDK : step 8 - establish connection 
    mDeviceManager.connect(mDevice);// connecting

    while(status < 2) {}

    //SDK : step 9 - open communication
    mDevice.open();

    while(status < 3) {}

    // Sdk : step 10 - get records from device
    List<Record> records = mDevice.getRecords(); // reading records

    Message detailMsg = new Message();
    detailMsg.obj = records;
    detailHandler.sendMessage(detailMsg);

### ALTERNATIVELY

### Step 8-10: Connect to device, open connection, and read data can be done as separate functions

This is connected to three top row buttons.

    public void onButtonConnectClicked(View v){
      // Sdk : step 8 - call connect function
      mDevice.connect();
      }

    public void onButtonOpenClicked(View v){
          // Sdk : step 9 - call open function
          mDevice.open();
    }

    public void onButtonReadClicked(View v){
          // Sdk : step 10 - get records from device
          List<Record> records = mDevice.getRecords(); // reading records
          int count = 0;
          if(records != null)
              count = records.size();

        onStatusMessage("Got Records : " + count);
    }

### Step 11: Extract the values Record list

    // Sdk : step 11 - extract the values from record list
    // See com.infometers.records.Record for record types
    List<Record> records = (List<Record>) msg.obj;
            
    String tmp = "";    
            
    for(int i=0; i < records.size(); i++) {
    com.infometers.records.bloodglucose.Record bgr = (com.infometers.records.bloodglucose.Record) records.get(i);
              
    tmp += bgr.getDateString() + " " + Integer.toString(bgr.getValue()) + "\n";

    }

## Version 2:

Version 2 explains how to add the InfoMeters SDK to an existing app. The resulting application code is included in the SampleApp2 folder.

Coming soon...
