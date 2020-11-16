package com.github.rosjava.android_apps.teleop2;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import std_msgs.Bool;


public class BluetoothTracker extends AbstractNodeMain {

    public boolean isActive;
    public double threshold;
    public Publisher<Bool> publisher;


    @Override
    public void onStart(ConnectedNode connectedNode) {
        //super.onStart(connectedNode);
        isActive = false;
        threshold =  40.0;
        publisher = connectedNode.newPublisher("/bluetooth", Bool._TYPE);
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
        node.shutdown();
    }
}
