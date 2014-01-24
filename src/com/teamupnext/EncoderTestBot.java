/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package com.teamupnext;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class EncoderTestBot extends SimpleRobot {

    // ***** constants *****
    public static final int LEFT_STICK = 1;

    public static final int FL_JAG = 2;
    public static final int BL_JAG = 3;
    public static final int FR_JAG = 4;
    public static final int BR_JAG = 5;

    public static final int ENCODER_CODES_PER_REV = 360;

    // ***** end constants *****
    // declare CAN Jaguar Objects
    public static CANJaguar frontLeftMotor;
    public static CANJaguar backLeftMotor;
    public static CANJaguar frontRightMotor;
    public static CANJaguar backRightMotor;

    public static final double p = 10.0;
    public static final double i = 0.01;
    public static final double d = 0.01;

    public static final double PI = 3.141592653;
    public static final double WHEEL_DIAMETER = 6;
    public static final double INCHES_PER_REV = (PI * WHEEL_DIAMETER);

    // declare our drive train
    public static RobotDrive driveTrain;

    // declare joystick
    Joystick leftstick;

    CANJaguar jag;
    String jagFullName;
    String jagShortName;

    // constructor
    public EncoderTestBot() {

        leftstick = new Joystick(LEFT_STICK);
        createDriveTrain(FL_JAG, BL_JAG, FR_JAG, BR_JAG);
        // jag to focus on
        jag = frontRightMotor;
        jagFullName = "frontRightMotor";
        jagShortName = "fr";
        System.out.println("----" + INCHES_PER_REV + "----");
        // setup and zero our focus jag
        initJag(jag, jagFullName);
    }

    // autonomous mode
    public void autonomous() {
        System.out.println("---> Autonomous <---");
        driveTrain.setSafetyEnabled(false);
        zeroJagEncoder(jag, jagFullName);
        enablePositionControl(jag, jagShortName);
        driveByInches(jag, jagShortName, 120);

        /*
         try{
         double fr = frontRightMotor.getPosition();
         frontRightMotor.setX(fr + 25);
         System.out.println(jagShortName + "=" + jag.getPosition());
         }catch (CANTimeoutException ex) {
         System.out.println("--- Error running autonomous ---");
         System.out.println(ex.getMessage());
         ex.printStackTrace();
         }
         */
        printJagAutonomous(jag, jagShortName);
    }

    // teleop mode
    public void operatorControl() {

        zeroJagEncoder(jag, jagFullName);
        enableTeleopControl(jag, jagShortName);

        // turn on safety
        driveTrain.setSafetyEnabled(true);

        // and we're off!
        while (isOperatorControl() && isEnabled()) {

            driveTrain.mecanumDrive_Cartesian(leftstick.getX(), leftstick.getY(), leftstick.getZ() * -1, 0);
            printJagEncoderPos(jag, jagShortName);
            Timer.delay(0.01);
        }

        // stop the robot
        driveTrain.drive(0, 0);

    }

    // test mode
    public void test() {
        System.out.println("---> Test <---");
    }

    // disabled
    public void disabled() {
        System.out.println("---> Disabled <---");
    }

    // **************
    // custom methods
    // **************
    // this method creates and initializes the drive train, including the jags
    private void createDriveTrain(int fl, int bl, int fr, int br) {

        System.out.println("---> Initializing Drive Train <---");

        try {
            System.out.println("+++ Constructing CAN Bus +++");
            frontLeftMotor = new CANJaguar(fl);
            backLeftMotor = new CANJaguar(bl);
            frontRightMotor = new CANJaguar(fr);
            backRightMotor = new CANJaguar(br);

        } catch (CANTimeoutException ex) {
            System.out.println("--- Error Constructing CAN Bus ---");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        driveTrain = new RobotDrive(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);

    }

    // this method destroys the drive train
    private void destroyDriveTrain() {

        System.out.println("---> Destroying Drive Train <---");

        driveTrain.free();
        //driveTrain = null;
        frontLeftMotor = null;
        backLeftMotor = null;
        frontRightMotor = null;
        backRightMotor = null;
    }

    // this method initialized a single jag for control
    private void initJag(CANJaguar jag, String jagName) {

        try {
            System.out.println("+++ Constructing " + jagName + " +++");
            //jag.enableControl(0);
            jag.configEncoderCodesPerRev(ENCODER_CODES_PER_REV);
            //jag.changeControlMode(CANJaguar.ControlMode.kPosition);
            jag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            jag.disableControl();
        } catch (CANTimeoutException ex) {
            System.out.println("--- Error enabling closed control on " + jagName + " ---");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // this method zeros a single jag encoder
    private void zeroJagEncoder(CANJaguar jag, String jagName) {

        try {
            System.out.println("+++ Zeroing encoder on " + jagName + " +++");
            jag.changeControlMode(CANJaguar.ControlMode.kPosition);
            jag.enableControl(0);

        } catch (CANTimeoutException ex) {
            System.out.println("--- Error zeroing encoder on " + jagName + " ---");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    // this method prints the count/pos of a single jag encoder
    private void printJagEncoderPos(CANJaguar jag, String jagName) {

        try {
            System.out.println(jagName + ": " + jag.getPosition());
        } catch (CANTimeoutException ex) {
            System.out.println("--- Error Printing Encoder for " + jagName + "---");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void enablePositionControl(CANJaguar jag, String jagName) {

        try {
            jag.disableControl();
            System.out.println("---> Position control enabled <---");
            jag.changeControlMode(CANJaguar.ControlMode.kPosition);
            jag.setPID(p, i, d);
            jag.enableControl(0);
        } catch (CANTimeoutException ex) {
            System.out.println("--- Error enabling position control ---");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void enableTeleopControl(CANJaguar jag, String jagName) {

        try {
            System.out.println("---> Teleop control enabled <---");
            //jag.changeControlMode(CANJaguar.ControlMode.kPosition);
            //jag.enableControl(0);
            jag.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            jag.setPID(0, 0, 0);
            jag.disableControl();
        } catch (CANTimeoutException ex) {
            System.out.println("--- Error enabling TeleopControl control ---");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void printJagAutonomous(CANJaguar jag, String jagShortName) {

        while (isEnabled()) {
            try {
                System.out.println(jagShortName + "=" + jag.getPosition());
            } catch (CANTimeoutException ex) {
                System.out.println("--- Error enabling TeleopControl control ---");
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
            Timer.delay(0.01);
        }

    }

    //revPerIN = 18.849555918 
    private void driveByInches(CANJaguar jag, String jagShortName, double distance) {
        
        try{
            double fr = jag.getPosition();
            jag.setX(fr + INCHES_PER_REV / distance);
        }catch (CANTimeoutException ex) {
            System.out.println("--- Error running autonomous ---");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        printJagAutonomous(jag, jagShortName);
    }

    private void driveByFeet(CANJaguar jag, String jagShortName, double distance) {
        distance = (distance * 12);
        System.out.println("---------" + distance + "----------");
        driveByInches(jag, jagShortName, distance);

    }

}
