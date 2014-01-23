/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Encoder;
//import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class RobotTemplate extends SimpleRobot {

    public static CANJaguar frontLeftMotor;
    public static CANJaguar backLeftMotor;
    public static CANJaguar frontRightMotor;
    public static CANJaguar backRightMotor;

    //public static Encoder BackRightEncoder;

    public static RobotDrive driveTrain;

    Joystick leftstick = new Joystick(1);

    public void autonomous() {

    }
    public void initDriveTrain(){
        System.out.println("---> Init <---");
        //BackRightEncoder.start();
        try {
            System.out.println("+++ Constructing CAN Bus +++");
            frontLeftMotor = new CANJaguar(2);
            backLeftMotor = new CANJaguar(3);
            frontRightMotor = new CANJaguar(4);
            backRightMotor = new CANJaguar(5);

        } catch (CANTimeoutException ex) {
            System.out.println("--- Error Constructing CAN Bus ---");
            ex.printStackTrace();
        }

        
        try {
            System.out.println("+++ Constructing backRightMotor +++");
            frontRightMotor.enableControl(0);
            frontRightMotor.configEncoderCodesPerRev(360);
            //backRightMotor.changeControlMode(CANJaguar.ControlMode.kPosition);
            frontRightMotor.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
        } catch (Exception e) {
            System.out.println("Error enabling closed control on Jag " + e.getMessage());

        }

        driveTrain = new RobotDrive(frontLeftMotor, backLeftMotor,
                frontRightMotor, backRightMotor);
    
    }        
    
    
    
    
    public void operatorControl() {

        initDriveTrain();
        
        while (isOperatorControl() && isEnabled()) {
            driveTrain.mecanumDrive_Cartesian(leftstick.getX(), leftstick.getY(), leftstick.getZ() * -1, 0);
            
             try {

             System.out.println("BR = " + frontRightMotor.getPosition());

             } catch (CANTimeoutException ex) {
             System.out.println("--- Error Printing Encoder ---");
             ex.printStackTrace();

             }
            

            Timer.delay(0.01);

        }
    }

    public void disabled() {

        if (isDisabled()) {
            System.out.println("---> disabled <---");
            //driveTrain.free();
            driveTrain = null;
            frontLeftMotor = null;
            backLeftMotor = null;
            frontRightMotor = null;
            backRightMotor = null;

        }
    }
}
