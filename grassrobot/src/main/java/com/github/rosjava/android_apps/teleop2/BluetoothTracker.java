package com.github.rosjava.android_apps.teleop2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import std_msgs.Bool;


public class BluetoothTracker extends AbstractNodeMain {

    public boolean isActive;
    public double threshold;
    public Publisher<Bool> publisher;
    //public BluetoothHidDevice device;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;
    BluetoothSocket socket;
    private BluetoothProfile.ServiceListener profileListener;

    public BluetoothTracker() {
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        //super.onStart(connectedNode);
        isActive = false;
        threshold =  40.0;
        publisher = connectedNode.newPublisher("/bluetooth", Bool._TYPE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter.isEnabled()){
            Set<BluetoothDevice> boundDevices = bluetoothAdapter.getBondedDevices();
            if (boundDevices.size()>0){
                Object[] devices = (Object[]) boundDevices.toArray();

                String reference = "00001200-0000-1000-8000-00805f9b34fb";
                int hack = 0;
                for (int i = 0; i < devices.length; i++) {
                    device = (BluetoothDevice) devices[i];
                    Log.e("errocisiisisismo ", "Device " + device.getName());
                    Log.w("errocisiisisismo ", "Address " + device.getAddress());
                    ParcelUuid[] a = device.getUuids();
                    if (a!= null) {
                        for (int j = 0; j < a.length; j++) {
                            Log.w("errocisiisisismo ", "uuids  " + a[j]);
                            if (a[j].equals(reference)){
                                Log.e("reference found", "device " + device.getName());
                                hack = i;
                            }
                        }
                    }
                }
                device = (BluetoothDevice) devices[hack];

                ParcelUuid[]uuids = device.getUuids();
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuids[0].getUuid());
                    socket.connect();

                } catch (IOException e) {
                    Log.e("errocisiisisismo ", "ERROR AQUI");
                    e.printStackTrace();
                }
                Log.e("errocisiisisismo ", "No devices apropiados");
            }
            else {
                Log.e("errocisiisisismo ", "Bluetooth deshabilitado");
            }
        }
        try {
            device.createRfcommSocketToServiceRecord(UUID.fromString("00001200-0000-1000-8000-00805f9b34fb"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(bluetoothAdapter == null){
            //Does not support bluetooth
        }

        profileListener = new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                //Context context = this. getApplicationContext();
                //CharSequence text = "In Callback";
                //Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                //toast.show();
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
    }

    public void publish(boolean command){
        isActive = command;
        Bool msg = publisher.newMessage();
        msg.setData(isActive);
        publisher.publish(msg);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("grassrobotics_app/ebluetooth_publisher");
    }

    @Override
    public void onShutdown(Node node) {
        publisher.shutdown();
        node.removeListeners();
        node.shutdown();
    }
}
