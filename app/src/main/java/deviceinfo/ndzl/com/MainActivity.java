    package deviceinfo.ndzl.com;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAssignedNumbers;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;



import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileConfig;
import com.symbol.emdk.ProfileManager;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.bluetooth.BluetoothHeadset.VENDOR_RESULT_CODE_COMMAND_ANDROID;
import static android.content.ContentValues.TAG;
import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.ACTION_VIEW;


public class MainActivity extends Activity implements EMDKManager.EMDKListener {


    private ProfileManager profileManager = null;
    private EMDKManager emdkManager = null;
    Timer tim;


    TextView tvOut;
    TextView tvBattery;

    // DZLReceiver mIntent_Receiver;
    IntentFilter mIntentFilter;
    Intent iBattStat;

    Button btLaunch;
    Button virtKeyb;
    static public boolean bvirtualoff = true;

    Button btCamera;
    public static Intent service_is;
    public static Context main_context;
    Button btSpeak;
    Button btWait;
    EditText etPTT;
    Button btCPUtest;
    Button btReboot;

    String androidId;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_context = this;
        service_is = new Intent(this, SpeakerService.class);

        String DeviceSERIALNumber = Build.SERIAL;
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        String DeviceIMEI = null;
        try {
            DeviceIMEI = telephonyManager.getDeviceId();
        } catch (SecurityException se) {
            DeviceIMEI="NO_IMEI";
        }
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        tvOut = (TextView) findViewById(R.id.tvOutput);
        tvBattery = (TextView) findViewById(R.id.tvBattery);
        tvOut.setText("S/N: " + DeviceSERIALNumber + "\nIMEI: " + DeviceIMEI + "\nSecure.ANDROID_ID: "+androidId  );  //rem on lollipop!

        btLaunch = (Button)findViewById(R.id.idLaunch);
        btLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity( new Intent().setComponent( new ComponentName("com.android.launcher3", "com.android.launcher3.Launcher")));

                //28 OTT 2020 TEST X MATTIA BARWARE, REMOVE ALL BT PAIRING
                //https://stackoverflow.com/questions/39531986/delete-all-paired-bluetooth-devices-on-android/39533790

                Toast.makeText(getApplicationContext(), "TRYING BT PAIRING REMOVAL", Toast.LENGTH_SHORT).show();

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        try {
                            //if(device.getName().contains("abc")){
                                Method m = device.getClass()
                                        .getMethod("removeBond", (Class[]) null);
                                m.invoke(device, (Object[]) null);
                            //}
                        } catch (Exception e) {
                            Log.e("fail", e.getMessage());
                        }
                    }
                }

            }



        });

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        if (results.statusCode == EMDKResults.STATUS_CODE.SUCCESS) {

            // EMDKManager object creation success

        } else {

            // EMDKManager object creation failed.
        }

        //Intent i =  getPackageManager().getLaunchIntentForPackage("nexive.settings.autoclock");

        Button btOpenGMPAS = (Button) findViewById(R.id.btOpenGMPAS);

        btOpenGMPAS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(ACTION_VIEW);
                //i.setPackage("com.google.android.apps.maps");

                ComponentName cn = new ComponentName("com.symbol.btapp", "com.symbol.btapp.BTActivity");

                i.setComponent(cn);

                startActivity(i);
            }
        });


        virtKeyb = (Button) findViewById(R.id.btVirtualKeyb);
        virtKeyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bvirtualoff) {
                    String[] modifyData = new String[1];
                    EMDKResults results = profileManager.processProfile("VIRTUALKEYB_SHOW", ProfileManager.PROFILE_FLAG.SET, modifyData);
                    MainActivity.bvirtualoff=false;
                }
                else{
                    String[] modifyData = new String[1];
                    EMDKResults results = profileManager.processProfile("VIRTUALKEYB_HIDE", ProfileManager.PROFILE_FLAG.SET, modifyData);
                    MainActivity.bvirtualoff=true;
                }
            }
        });



        btCamera = (Button) findViewById(R.id.btCamera);
        btCamera.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dispatchTakePictureIntent();
                                                }
                                            });


        btSpeak = (Button) findViewById(R.id.btSpeak);
        btSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String daparlare = etPTT.getText().toString();
                httpGET_SPEAK("3", daparlare);
            }
        });

        btWait= (Button) findViewById(R.id.btWait);
        btWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeakerService.parlaTesto("in attesa sul canale 3");
                httpGET_WAIT("3");
            }
        });

        etPTT = (EditText)findViewById(R.id.etPTT);

        btCPUtest= (Button) findViewById(R.id.cputest);
        btCPUtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "CPU Test started", Toast.LENGTH_SHORT).show();
                Random rnd = new Random();
                double d =1.0;
                long t0 = System.currentTimeMillis();
                for(int i=0; i<10000; i++){
                    d=d*Math.sin(2*Math.PI*rnd.nextDouble()) +Math.hypot(rnd.nextDouble(), rnd.nextDouble());
                }
                long t1 = System.currentTimeMillis();

                Toast.makeText(getApplicationContext(), "10000 iter., time "+(t1-t0)+"ms\n result="+d, Toast.LENGTH_SHORT).show();
            }
        });

       // public final static int DISPATCH_KEY_FROM_IME = 10028;
        /*public static final int KEYCODE_SYMBOL_BLUE = 10027;

        public static final int KEYCODE_SYMBOL_ORANGE = 10028;

        public static final int KEYCODE_SYMBOL_SHIFT = 10032;
        */

        btReboot= (Button) findViewById(R.id.btReboot);
        btReboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] modifyData = new String[1];

                //EMDKResults results = profileManager.processProfile("REBOOT", ProfileManager.PROFILE_FLAG.SET, modifyData);

                //TRYING TO SET THE STATUS OF ORANGE BUTTON IN MC22 MC33

            }
        });

        tim = new Timer("NIK", false);
        tim.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        readBatteryInfo();
                    }

                });
            }
        }, 100, 1000);

        IntentFilter headset_intentFilter = new IntentFilter();
        headset_intentFilter.addCategory(BluetoothHeadset.VENDOR_SPECIFIC_HEADSET_EVENT_COMPANY_ID_CATEGORY+"."+BluetoothAssignedNumbers.GOOGLE);
        headset_intentFilter.addAction("android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT");
        headset_intentFilter.addAction(VENDOR_RESULT_CODE_COMMAND_ANDROID);

        registerReceiver(new MyReceiver(), headset_intentFilter);

        IntentFilter intentFilter = new IntentFilter();
        //mIntent_Receiver = new DZLReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        readXMLbackupVoltageThresholds();

        serviceTalk("CIAO NICOLA");

        hideNavBar(); //a test

        //RUNTIME PERMISSIONS
        requestPermissions(   new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                3003);
    }

    void hideNavBar(){
        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 3003: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //GPS ROLLOVER TEST NOV.4,2019
                    //TESTS FOR PRINTERS POSITION, DEC.10,2020
                    locationManager = (LocationManager)  getSystemService(Context.LOCATION_SERVICE);
                    LocationListener locationListener = new MyLocationListener();
                    try {
                        //LocationManager.GPS_PROVIDER: GPS TESTED SUCCESSFULLY ON TC25 API 27 - ON WAN NETWORK WITH GPS ENABLED / HIGH ACCURACY
                        //LocationManager.NETWORK_PROVIDER: WORKS FINE, NEEDS TO BE CALLED FIRST, NOT IN THE EXCEPTION CATCH
                        // NETWORK_PROVIDER WORKS ON TC57 WITH WAN SWITCHED OFF AND WLAN ENABLED BUT NOT CONNECTED
                        //  MC22 after enabling locationing in GMaps! il solo flag del menu Location non dava accesso alle coordinate... Connesso a WLAN-.
                        // TC20 ok ma collegato a rete WLAN

                        locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    } catch (SecurityException se) { int a=0;  }
                    catch (IllegalArgumentException iae){
                        //GPS not provided - OR LOCATIONING NOT ENABLED
                        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Permissions denied, stopping here sorry.", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
            }
        }
    }



    final int REQUEST_IMAGE_CAPTURE = 1;

    //https://developer.android.com/training/camera/photobasics#java
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void httpGET_SPEAK(String destinationChannel, String messageToBeSent) {
        //String _msg = messageToBeSent.replaceAll(" ", "_").toUpperCase();
        String _msg = null;
        try {
            _msg = URLEncoder.encode(messageToBeSent, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String _url ="https://clouddumplogger.appspot.com/cmb?speak="+_msg+"&ch="+destinationChannel;
        new HTTP_GET().execute(_url, "false");
    }


    public void httpGET_WAIT(String listeningOnChannel) {

        //ANDROID_ID Added in API level 3 public static final String ANDROID_ID On Android 8.0 (API level 26) and higher versions of the platform, a 64-bit number (expressed as a hexadecimal string), unique to each combination of app-signing key, user, and device. Values of ANDROID_ID are scoped by signing key and user. The value may change if a factory reset is performed on the device or if an APK signing key changes. For more information about how the platform handles ANDROID_ID in Android 8.0 (API level 26) and higher, see Android 8.0 Behavior Changes.

        String _askchannel_url = "https://clouddumplogger.appspot.com/cmb?assign-channel-to="+listeningOnChannel;//  androidId ;
         new HTTP_GET().execute(_askchannel_url, "false");



    }


    void readBatteryInfo() {
        iBattStat = registerReceiver(null, mIntentFilter);

        int bkvoltage = iBattStat.getExtras().getInt("bkvoltage");
        String mfd = iBattStat.getExtras().getString("mfd");
        String serialnumber = iBattStat.getExtras().getString("serialnumber");
        String partnumber = iBattStat.getExtras().getString("partnumber");
        int ratedcapacity = iBattStat.getExtras().getInt("ratedcapacity");
        int cycle = iBattStat.getExtras().getInt("cycle");

        int battery_usage_numb = iBattStat.getExtras().getInt("battery_usage_numb"); //OK PER TC75, TESTED ON 12DIC2018

        Log.i("battery_usage_numb", ""+battery_usage_numb);


        //  String bkupbattlevel =  getString(R.id.tvOutput);
        int bbl =  backupVoltageToLevel(bkvoltage);
        String avviso = bbl>75 ? "It's safe to hotswap battery" : "DO NOT HOTSWAP BATTERY!";
        String battInfo = "Backup Battery: " + bbl + "%\n"+avviso+"\nBackup Battery: " + bkvoltage + "mV\nMain Battery S/N: " + serialnumber + "\nMain Battery MFD: " + mfd;
        tvBattery.setText(battInfo);
        tvBattery.setBackgroundColor(bbl>75 ? Color.GREEN : Color.RED);

        Bundle bundle = iBattStat.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.i(TAG, String.format("MAIN_BATTERY_EXTRA-%s %s (%s)", key, value.toString(), value.getClass().getName()));
            }
            Log.i(TAG, "---------------------------");
        }
    }

    int[] backupVoltageThresholds = {2431, 2551, 2611, 2661};

    void readXMLbackupVoltageThresholds() {
        try {
            InputStream bckbattxml_is = new FileInputStream("/system/etc/batterymanager/batterymanager-update.xml");

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            myparser.setInput(bckbattxml_is, null);

            int event = myparser.getEventType();
            String text="";
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myparser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = myparser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("LevelFor25")) {
                            backupVoltageThresholds[0] = Integer.parseInt( text );
                        } else if (name.equals("LevelFor50")) {
                            backupVoltageThresholds[1] = Integer.parseInt( text );
                        } else if (name.equals("LevelFor75")) {
                            backupVoltageThresholds[2] = Integer.parseInt( text );
                        } else if (name.equals("LevelFor100")) {
                            backupVoltageThresholds[3] = Integer.parseInt( text );
                        } else {
                        }
                        break;
                }
                event = myparser.next();
            }

        } catch (FileNotFoundException e1) {
        } catch (XmlPullParserException e2) {
        } catch (IOException e3){}


    }

    //backupVoltageToLevel got by decompiling BatteryManager.apk
    //backup battery level is computed from voltage
    public int backupVoltageToLevel(int paramAnonymousInt) {


        //actual values, different from device to device, are written in
        //   /system/etc/batterymanager/batterymanager-update.xml

        /*
        *     <BackupBatteryLevelParams>
                <BackupBatteryLevelParam>
                    <LevelFor25>2450</LevelFor25>
                    <LevelFor50>2500</LevelFor50>
                    <LevelFor75>2550</LevelFor75>
                    <LevelFor100>2580</LevelFor100>
                </BackupBatteryLevelParam>
              </BackupBatteryLevelParams>
        * */

        if (paramAnonymousInt < backupVoltageThresholds[0]) {
            return 0;
        }
        if ((paramAnonymousInt >= backupVoltageThresholds[0]) && (paramAnonymousInt < backupVoltageThresholds[1])) {
            return 25;
        }
        if ((paramAnonymousInt >= backupVoltageThresholds[1]) && (paramAnonymousInt < backupVoltageThresholds[2])) {
            return 50;
        }
        if ((paramAnonymousInt >= backupVoltageThresholds[2]) && (paramAnonymousInt < backupVoltageThresholds[3])) {
            return 75;
        }
        if (paramAnonymousInt >= backupVoltageThresholds[3]) {
            return 100;
        }
        return 0;
    }


    /*  ET1:
    *   Battery Pack Rechargeable Lithium Ion 3.7V, 4620 mAh or 5640 mAh Smart battery.
        Backup Battery NiMH battery (rechargeable) 15 mAh 3.6 V (not user accessible).
    * */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onOpened(EMDKManager emdkManager) {

        this.emdkManager = emdkManager;
        String[] modifyData = new String[1];

        profileManager = (ProfileManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);

        //EMDKResults results = profileManager.processProfile("CALL_GMAPS", ProfileManager.PROFILE_FLAG.SET, modifyData);

        //EMDKResults results = profileManager.processProfile("SDCARD_ENCRYPT", ProfileManager.PROFILE_FLAG.SET, modifyData);

        //TEST LED NDZL 14 SET 2017: NOTIFICATION SPARITE IN EMDK 6.6


        blinkLED();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        emdkManager.release();
    }


    @Override
    public void onClosed() {

    }


    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.

    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.



    }

    void blinkLED() {
        Notification.Builder noteBuilder = new Notification.Builder(this)
                .setAutoCancel(true)
                //.setPriority(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher)
                //.setColor(Color.RED)
                .setLights(Color.YELLOW, 100, 200);



        Notification note = noteBuilder.build();
        note.defaults = 0;

        Context ctx = this;// ams.mContext.createPackageContext(this, 0);
        Intent runningIntent = new Intent( Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        runningIntent.setData(Uri.fromParts("package", "deviceinfo.ndzl.com", null));
        PendingIntent pi = PendingIntent.getActivity(this, 0, runningIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //note.setLatestEventInfo(this, "N.DZL NOTIFIC. TITLE", "Touch for more information or to stop the app", pi); //
        note.ledARGB = Color.YELLOW;
        NotificationManager mgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.notify(0, note);



    }

    void serviceTalk(String words){
        service_is.putExtra("WORDS_TO_SAY", words);
        service_is.putExtra("LANGUAGE", "ITA");
        startService(service_is);
    }


    class MyLocationListener implements LocationListener {
        public String longit = "";
        public String latit = "";

        @Override
        public void onLocationChanged(Location loc) {
            // Toast.makeText(  getBaseContext(), "Location changed: Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            latit = "" + loc.getLatitude();
            longit = "" + loc.getLongitude();
            String gps_time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(loc.getTime());

            tvOut.setText(""+gps_time+ " Lat:" + loc.getLatitude() + " Lng:" + loc.getLongitude());

        }

        @Override
        public void onProviderDisabled(String provider) {
            tvOut.setText("Locationing Listener::onProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            tvOut.setText("Locationing Listener::onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            tvOut.setText("Locationing Listener::onStatusChanged");
        }
    }


}


/*
class DZLReceiver extends BroadcastReceiver {
    public DZLReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Action: " + intent.getAction(), Toast.LENGTH_SHORT).show();

        if (  BATTERY_STATE_CHANGED_INTENT.equals(intent.getAction())) {

            int bkvoltage = intent.getExtras().getInt("bkvoltage");
            String mfd = intent.getExtras().getString("mfd");
            String serialnumber = intent.getExtras().getString("serialnumber");
            String partnumber = intent.getExtras().getString("partnumber");
            int ratedcapacity = intent.getExtras().getInt("ratedcapacity");
            int cycle = intent.getExtras().getInt("cycle");

        }

    }
}*/




