# SDK Function Calls and enums

## Description of relevant calls:

Create a new instance of the InfoMeters SDK:

    Device mDevice = new Device();

Initialize the SDK and a SerialPort it uses, where:

    mDevice.init(context, mOnDeviceListener);

The parameters above are:

* `Context context = this;`
* `OnDeviceListener mOnDeviceListener = this;`

Set the correct id for your device (in this example, the LifeScan OneTouch UltraMini):

    mDevice.setDevice(DeviceIds.OneTouchUltraMini);

Call when you close the application to free up resources:

    mDevice.cleanup();

Used to establish a physical layer connection:

    mDevice.connect();

Used to connect to the device or make sure that the device is still connected.

    mDevice.open();

Read records from the setDevice. Returns a list array of Record values.

    mDevice.readRecords();

## Description of `OnDeviceListener` interface functions:

You need to implement two `OnDeviceListener` methods in your code. They provide up-to-date progress status via an enum passed to the application from the SDK. Designed to notify the app about the current state of connectivity, they let you know what is broken.


    public void onConnectionStatus(ConnectionStatus status);

More information about the `ConnectionStatus` enum is in the enum section below.

Provides up-to-date information about the physical device connection status:
    public void onStatusMessage(String text)

The specific messages are below. They are also designed to notify about the current state of connectivity. This is additional text information about the connection status. Connection error messages are sent via `onStatusMessage`. 

Message : Notes

"No adapters are plugged in!": Nothing is plugged into the mobile device.

“Set Device: OneTouchUltraMini”: Tells you the currently selected medical meter device.

“Connected Adapter”: Adapter is detected as connected to the smartphone.

“Already Connected!”: Adapter is still connected to the smartphone.

## Description of enums and structures:

### enum ConnectionStatus

<table>
    <tr>
        <th><pre>enum</pre></th><th>status</th>
    </tr>
    <tr>
        <td><pre>Invalid</pre></td><td>Invalid API key provided.</td>
    </tr>
    <tr>
        <td><pre>None</pre></td><td>No adapters or devices are detected.</td>
    </tr>
    <tr>
        <td><pre>Usb</pre></td><td>USB Adapters are detected.</td>
    </tr>
    <tr>
        <td><pre>Adapter</pre></td><td>Physical connection to device is established.</td>
    </tr>
    <tr>
        <td><pre>Device</pre></td><td>Device is connected and ready to send data.</td>
    </tr>
</table>

### enum DeviceIds.DeviceName

Use `DeviceName` to manually specify the device your patient is using. Currently supported devices and their ids are listed below. Click on the links to go to sites where you can purchase the devices.

<table>
    <tr>
        <th>Brand and Product Name</th><th>Relevant Enum DeviceName</th>
    </tr>
    <tr>
        <td><a href="http://www.amazon.com/OneTouch-Glucose-Monitoring-System-Silver/dp/B000KK8HBY">LifeScan OneTouch UltraMini</a></td><td><pre>OneTouchUltraMini</pre></td>
    </tr>
    <tr>
        <td><a href="http://www.solaramedicalsupplies.com/lifescan-onetouch-select-blood-glucose-meter">LifeScan OneTouch Select</a></td><td><pre>OneTouchSelect</pre></td>
    </tr>
    <tr>
        <td><a href="http://www.amazon.com/OneTouch-Ultra-Blood-Glucose-Lifescan/dp/B000O0FPY2">LifeScan OneTouch Ultra2</a></td><td><pre>OneTouchUltra2</pre></td>
    </tr>
    <tr>
        <td><a href="http://www.amazon.com/OneTouch-UltraSmart-Glucose-Monitoring-System/dp/B00008O2XL">LifeScan OneTouch UltraSmart</a></td><td><pre>OneTouchUltraSmart</pre></td>
    </tr>
    <tr>
        <td><a href="http://www.amazon.com/Medical-UA-767PC-Automatic-Pressure-Communication/dp/B00264GO1C">A&D Medical UA-767PC Automatic Blood Pressure Monitor</a></td><td><pre>AndBloodPressureUS767PC</pre></td>
    </tr>
    <tr>
        <td><a href="http://www.amazon.com/LifeSource-UC-321-Precision-Personal-Health/dp/B000B688P2">A&D Medical UC-321PC Precision Scale</a></td><td><pre>AndScaleUS321PC</pre></td>
    </tr>
    <tr>
        <td><a href="http://www.amazon.com/Omnis-Health-Embrace-Glucose-Audible/dp/B001C481TA">Omnis Health Embrace Talking Blood Glucose Meter</a></td><td><pre>Embrace</pre></td>
    </tr>
</table>


### com.infometers.common.records

The `Records` object type is designed to provide standardized record values for data coming from any device. The general object type has a timestamp. There are `Records` objects for each measurement type:

#### com.infometers.devices.records

<table>
    <tr>
        <th>Name</th><th>Type</th><th>Notes</th>
    </tr>
    <tr>
        <td><pre>mDate</pre></td><td><pre>private Long</pre></td><td><pre>public Long getDate() {
    return mDate;
}
public void setDate(Long mDate) {
    this.mDate = mDate;
}
</pre>
</tr>
    <tr>
        <td><pre>mMeasurementType</pre></td><td><pre>private int</pre></td><td><pre>public int getMeasurementType() {
    return mMeasurementType;
}
public void setMeasurementType(int value) {
    this.mMeasurementType = value;
}
</pre>
</tr>
</table>

#### com.infometers.devices.records.bloodpressure

<table>
    <tr>
        <th>Name</th><th>Type</th><th>Notes</th>
    </tr>
    <tr>
        <td><pre>mSys</pre></td><td><pre>protected int</pre></td><td><pre>public int getSys() {
    return mSys;
}
public void setSys(int value) {
    this.mSys = value;
}
</pre>
</tr>
    <tr>
        <td><pre>mDia</pre></td><td><pre>protected int</pre></td><td><pre>public int getDia() {
    return mDia;
}
public void setDia(int value) {
    this.mDia = value;
}
</pre>
</tr>
    <tr>
        <td><pre>mPul</pre></td><td><pre>protected int</pre></td><td><pre>public int getPul() {
    return mPul;
}
public void setPul(int value) {
    this.mPul = value;
}
</pre>
</tr>
</table>

#### com.infometers.devices.records.bloodglucose

<table>
    <tr>
        <th>Name</th><th>Type</th><th>Notes</th>
    </tr>
    <tr>
        <td><pre>mValue</pre></td><td><pre>protected int</pre></td><td><pre>public int getValue() {
    return mValue;
}
public void setValue(int value) {
    this.mValue = value;
}
</pre>
</tr>
    <tr>
        <td><pre>mNote</pre></td><td><pre>protected int</pre></td><td><pre>public String getNote() {
    return mNote;
}
public void setNote(String note) {
    this.mNote = note;
}
</pre>
</tr>
    <tr>
        <td><pre>mType</pre></td><td><pre>protected int</pre></td><td><pre>public Types getType() {
    return mType;
}
public void setType(Types value) {
    mType = value;
}
</pre>
</tr>
</table>
