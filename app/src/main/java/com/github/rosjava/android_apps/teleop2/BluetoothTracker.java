package com.github.rosjava.android_apps.teleop2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


import static java.lang.Thread.sleep;


public class BluetoothTracker {

    public boolean isActive;
    public double threshold;
    //public BluetoothHidDevice device;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;
    BluetoothSocket socket;
    //BluetoothServerSocket serversocket;
    private BluetoothProfile.ServiceListener profileListener;
    private OutputStream outStream;
    private InputStream inStream;

    public BluetoothTracker() {
        //super.onStart(connectedNode);
        isActive = false;
        threshold =  40.0;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            //Does not support bluetooth
            Log.e("ABORT", "bluetooth not supported");
            return;
        }

        if(bluetoothAdapter.isEnabled()){
            Set<BluetoothDevice> boundDevices = bluetoothAdapter.getBondedDevices();
            Log.e("ABORT", "bluetooth not supported");

            if (boundDevices.size()>0){
                Object[] devices = (Object[]) boundDevices.toArray();

                String reference = "0000112f-0000-1000-8000-00805f9b34fb";

                int hack = 0;
                int uhack = 0;

                for (int i = 0; i < devices.length; i++) {
                    device = (BluetoothDevice) devices[i];
                    Log.i("DeviceID ", "Device " + device.getName());
                    Log.i("AddressID ", "Address " + device.getAddress());
                    ParcelUuid[] a = device.getUuids();
                    if (a!= null) {
                        for (int j = 0; j < a.length; j++) {
                            Log.w("errocisiisisismo ", "uuids:" + a[j]);


                            if (a[j].getUuid().toString().equals(reference)){
                                Log.e("device found", "device " + device.getName());
                                Log.e("uuid found", "uuid " + a[j]);
                                hack = i;
                                uhack = j;
                            }
                        }
                    }
                }

                device = (BluetoothDevice) devices[hack];
                Log.e("reference found", " on device " + device.getName());

                ParcelUuid[]uuids = device.getUuids();
                Log.e("reference found", " on uuid " + uuids[uhack].getUuid());

                ///try {
                    //socket = device.createInsecureRfcommSocketToServiceRecord(uuids[uhack].getUuid());
                    //socket = device.createRfcommSocketToServiceRecord(uuids[uhack].getUuid());
                    //socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001200-0000-1000-8000-00805f9b34fb"));

                    //socket.connect();
                for (int j = 0; j < uuids.length; j++) {
                    Log.w("this ", "uuids:" + uuids[j]);

                    try {
                        //The string is an identifiable name of your service, which the system automatically writes to a new Service Discovery Protocol (SDP) database entry on the device.
                        ParcelUuid SERVER = ParcelUuid.fromString("00001200-0000-1000-8000-00805f9b34fb");
                        //socket = device.createInsecureRfcommSocketToServiceRecord(SERVER.getUuid());
                        socket = device.createInsecureRfcommSocketToServiceRecord(uuids[j].getUuid());

                        //socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                        //socket = device.createInsecureRfcommSocketToServiceRecord()
                        //serversocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("TEST", uuids[uhack].getUuid());
                        Log.e("working", "This somehow is done");
                        Log.e("working -->", uuids[j].toString());

                    } catch (IOException e) {
                        Log.e("error", e.getMessage());
                        e.printStackTrace();
                    }

                    try{
                        socket.connect();
                        inStream = socket.getInputStream();
                        outStream = socket.getOutputStream();
                        outStream.write("FUnciona".getBytes());
                        sleep(1000);
                        Log.e("working2", "This somehow is done");
                        Log.e("working2 -->", uuids[j].toString());
                    } catch (IOException | InterruptedException e) {
                        Log.e("error", e.getMessage());
                        e.printStackTrace();
                    }


                }

                //} catch (IOException e) {
                  //  Log.e("errocisiisisismo ", "ERROR >>>>> " + e.getMessage());
                   // e.printStackTrace();
                //}
            }
            else {
                Log.e("errocisiisisismo ", "Bluetooth deshabilitado");
            }
        }

        /*
        profileListener = new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                //Context context = this. getApplicationContext();
                //CharSequence text = "In Callback";
                //Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                //toast.show();
                Log.w("ON servoce Listene", "PROFILE : " + String.valueOf(profile));
                if(profile == BluetoothProfile.HID_DEVICE){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        //device = (BluetoothDevice) proxy;
                    }

                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                if(profile == BluetoothProfile.HID_DEVICE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        device = null;
                    }
                }
            }
        };

         */
    }
}
