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

`enum` : status

`Invalid` : Invalid API key provided.

`None` : No adapters or devices are detected.

`Usb` : USB Adapters are detected.

`Adapter` : Physical connection to device is established.

`Device` : Device is connected and ready to send data.


### enum DeviceIds.DeviceName

Use `DeviceName` to manually specify the device your patient is using. Currently supported devices and their ids are listed below. Click on the links to go to sites where you can purchase the devices.
 
Brand and Product Name : Relevant Enum DeviceName
[LifeScan OneTouch UltraMini](http://www.amazon.com/OneTouch-Glucose-Monitoring-System-Silver/dp/B000KK8HBY) : `OneTouchUltraMini`

[LifeScan OneTouch Select](http://www.solaramedicalsupplies.com/lifescan-onetouch-select-blood-glucose-meter) : `OneTouchSelect`

[LifeScan OneTouch Ultra2](http://www.amazon.com/OneTouch-Ultra-Blood-Glucose-Lifescan/dp/B000O0FPY2) : `OneTouchUltra2`

[LifeScan OneTouch UltraSmart](http://www.amazon.com/OneTouch-UltraSmart-Glucose-Monitoring-System/dp/B00008O2XL) : `OneTouchUltraSmart`

[A&D Medical UA-767PC Automatic Blood Pressure Monitor](http://www.amazon.com/Medical-UA-767PC-Automatic-Pressure-Communication/dp/B00264GO1C) : `AndBloodPressureUS767PC`

[A&D Medical UC-321PC Precision Scale](http://www.amazon.com/LifeSource-UC-321-Precision-Personal-Health/dp/B000B688P2) : `AndScaleUS321PC`

[Omnis Health Embrace Talking Blood Glucose Meter](http://www.amazon.com/Omnis-Health-Embrace-Glucose-Audible/dp/B001C481TA) : `Embrace`


### com.infometers.common.records

The `Records` object type is designed to provide standardized record values for data coming from any device. The general object type has a timestamp. There are `Records` objects for each measurement type: