package deviceinfo.ndzl.com;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MyReceiver extends BroadcastReceiver {


    @Override
    @SuppressWarnings("unchecked")
    public void onReceive(Context context, Intent iHS) {

        //Toast.makeText(context, "Received an intent.", Toast.LENGTH_SHORT).show();
        Bundle bundle = iHS.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {

                if(key.equals("android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_ARGS"))
                {
                    Object[] alArgs = (Object[])bundle.get(key);
                    if(alArgs.length==4) {
                        Log.i(TAG, "HEADSET_TYPE_OF_INFO="+ (alArgs[0].equals("XHSTBATSOC")?"CHARGING_LEVEL":"BATTERY_HEALTH"));
                        Log.i(TAG, "HEADSET_INFO="+alArgs[1]);
                        Log.i(TAG, "HEADSET_BATTERY_SERIALNUMBER="+alArgs[2]);
                        Log.i(TAG, "HEADSET_BATTERY_MFD="+alArgs[3]);
                    }

                }
                else{
                    Object value = bundle.get(key);
                    Log.i(TAG, String.format("HEADSET_EXTRA-%s %s (%s)", key, value.toString(), value.getClass().getName()));
                }

            }
            Log.i(TAG, "---------------------------");
        }
    }
}

/*
*
*
* 2-17 15:37:30.418 25092 25140 D HeadsetStateMachine: Exit processUnknownAt()
12-17 15:37:30.418 25092 25133 D bt_btif : bta_ag_hfp_result : res = 20
12-17 15:37:30.418 25092 25140 D HeadsetStateMachine: Exit Connected processMessage()
12-17 15:37:30.419  1946  1946 I GsaVoiceInteractionSrv: O received Intent { act=android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT cat=[android.bluetooth.headset.intent.category.companyid.224] flg=0x10 (has extras) }
12-17 15:37:30.419  1318  1332 W BroadcastQueue: Background execution not allowed: receiving Intent { act=android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT cat=[android.bluetooth.headset.intent.category.companyid.224] flg=0x10 (has extras) } to com.google.android.googlequicksearchbox/com.google.android.apps.gsa.broadcastreceiver.external.ExternalCommonBroadcastReceiver
12-17 15:37:30.921 25092 25133 D bt_btif : bta_ag_hdl_event: p_scb 0xf8d02a68
12-17 15:37:30.921 25092 25133 D bt_btif : bta_ag_rfc_data: setting sys busy
12-17 15:37:30.921 25092 25133 D bt_btif : bta_ag_rfc_data: resetting idle timer
12-17 15:37:30.921 25092 25112 D bt_btif : btif_hf_upstreams_evt: event=BTA_AG_AT_UNAT_EVT
12-17 15:37:30.921 25092 25112 D BluetoothHeadsetServiceJni: unknown_at_callback:: string +ANDROID=XHSTBATSOH,100,A0044,05072016
12-17 15:37:30.921 25092 25112 D HeadsetStateMachine: Enter onUnknownAt()
12-17 15:37:30.921 25092 25112 D HeadsetStateMachine: getDevice()
12-17 15:37:30.921 25092 25112 D HeadsetStateMachine: Exit onUnknownAt()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Connected process message=101, numConnectedDevices=1
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Connected: event type: 15event device : 00:1B:41:A3:EA:5C
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Enter processUnknownAt()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Enter parseUnknownAt()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Exit parseUnknownAt()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Enter getAtCommandType()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Exit getAtCommandType()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Enter processVendorSpecificAt()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Enter generateArgs()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Enter findChar()
12-17 15:37:30.922 25092 25140 I chatty  : uid=1002(bluetooth) HeadsetStateMac identical 2 lines
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Enter findChar()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Exit findChar()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Exit generateArgs()
12-17 15:37:30.922 25092 25140 D HeadsetStateMachine: Enter broadcastVendorSpecificEventIntent()
12-17 15:37:30.924 25092 25140 D HeadsetStateMachine: Exit broadcastVendorSpecificEventIntent()
12-17 15:37:30.924 25092 25140 D HeadsetStateMachine: getByteAddress()
12-17 15:37:30.924 25092 25140 D bt_btif : BTHF: at_response
12-17 15:37:30.924 25092 25140 D HeadsetStateMachine: Exit processVendorSpecificAt()
12-17 15:37:30.924 25092 25133 D bt_btif : bta_ag_api_result: p_scb 0xf8d02a68
12-17 15:37:30.924 25092 25140 D HeadsetStateMachine: Exit processUnknownAt()
12-17 15:37:30.924 25092 25133 D bt_btif : bta_ag_hfp_result : res = 20
12-17 15:37:30.924 25092 25140 D HeadsetStateMachine: Exit Connected processMessage()
12-17 15:37:30.925  1946  1946 I GsaVoiceInteractionSrv: O received Intent { act=android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT cat=[android.bluetooth.headset.intent.category.companyid.224] flg=0x10 (has extras) }
12-17 15:37:31.430  4911  5216 W SearchServiceCore: Abort, client detached.
12-17 15:37:30.925  1318  1332 W BroadcastQueue: Background execution not allowed: receiving Intent { act=android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT cat=[android.bluetooth.headset.intent.category.companyid.224] flg=0x10 (has extras) } to com.google.android.googlequicksearchbox/com.google.android.apps.gsa.broadcastreceiver.external.ExternalCommonBroadcastReceiver

* */