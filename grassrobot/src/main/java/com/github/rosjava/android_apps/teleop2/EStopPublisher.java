package com.github.rosjava.android_apps.teleop2;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import std_msgs.String;

public class EStopPublisher extends AbstractNodeMain {
    public Publisher<std_msgs.String> publisher;

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher("/estop", String._TYPE);
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
    }

    public void publish(){
        String msg = publisher.newMessage();
        msg.setData("HELLO WORLD");
        publisher.publish(msg);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("grassrobotics_app/estop_publisher");
    }
}
