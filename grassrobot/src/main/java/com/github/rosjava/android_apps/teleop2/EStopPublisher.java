package com.github.rosjava.android_apps.teleop2;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import std_msgs.Bool;
import std_msgs.String;

public class EStopPublisher extends AbstractNodeMain {
    public Publisher<std_msgs.Bool> publisher;
    boolean state;

    @Override
    public void onStart(ConnectedNode connectedNode) {
        state = false;
        publisher = connectedNode.newPublisher("/estop", Bool._TYPE);
        /*
        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void setup() {

            }

            @Override
            protected void loop() throws InterruptedException {
                String msg = publisher.newMessage();
                msg.setData("HELLO WORLD");
                publisher.publish(msg);
                Thread.sleep(1000);
            }
        });
        */
    }

    @Override
    public void onShutdown(Node node) {
        publisher.shutdown();
        node.shutdown();
    }

    protected void toggleState(){
        state = !state;
    }

    public boolean getState(){
        return state;
    }

    public void publish(){
        Bool msg = publisher.newMessage();
        msg.setData(state);
        toggleState();
        publisher.publish(msg);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("grassrobotics_app/estop_publisher");
    }
}
