package com.mayoral.android_apps.mqtt_teleop;

public class RobotState {
    static public boolean estop = false;
    static public String robot_id = "robot";
    static public String ip_addrees = "10.0.0.24";
    static public int port = 1883;
    static public boolean conveyor_state = false;
    static{
        estop =false;
        robot_id= "robot";
        conveyor_state = false;
     }
};
