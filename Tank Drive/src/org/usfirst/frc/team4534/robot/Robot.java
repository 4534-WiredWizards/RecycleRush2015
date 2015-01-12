package org.usfirst.frc.team4534.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;

//import edu.wpi.first.wpilibj.Talon;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically it 
 * contains the code necessary to operate a robot with tank drive.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
    RobotDrive myRobot;  // class that handles basic drive operations
    Joystick controller;
    Double leftAxis;
    Double rightAxis;
    AnalogInput analogInput;
    BuiltInAccelerometer accel;
    //String mxpOutput;
    //String newMxpOutput;
    //Talon motorTalon;
    Gyro gyro1;
    AnalogInput analog5;
    public Robot() {
        myRobot = new RobotDrive(0, 1);
        myRobot.setExpiration(0.1);
        controller = new Joystick(0);
        leftAxis = controller.getRawAxis(0);
        rightAxis = controller.getRawAxis(1);
        accel = new BuiltInAccelerometer();
        //analogInput = new AnalogInput(33);
        //motorTalon = new Talon(5);
        //motorTalon.set(100);

        //mxpOutput = "";
        //newMxpOutput = "";

        //gyro1 = new Gyro(0);
        analog5 = new AnalogInput(5);

    }

        
    /*
     * Runs the motors with tank steering.
     */
    public void operatorControl() {
        myRobot.setSafetyEnabled(true);
        double storedInputX = 0.0;
        double storedInputY = 0.0;
        while (isOperatorControl() && isEnabled()) {
        	outputStringToDash(0, Double.toString(analog5.getVoltage()));
        	
        	double newX = controller.getRawAxis(1);
        	double newY = controller.getRawAxis(0)*-1;
        	boolean isAcceleratingX = newX > storedInputX;
        	boolean isAcceleratingY = newY > storedInputY;
        	
        	//storedInputX = getAcceleration(storedInputX, newX);
        	//storedInputY = getAcceleration(storedInputY, newY);
        	if (storedInputX > .9) {
        		storedInputX = 1;
        	}
        	if (storedInputY > .9) {
        		storedInputY = 1;
        	}
        	myRobot.arcadeDrive(controller.getRawAxis(1), controller.getRawAxis(0)*-1);
            Timer.delay(0.005);		// wait for a motor update time
        }
    }
    
    public void test() {
    	while (isTest() && isEnabled()) {
    		SmartDashboard.putString("DB/String 0", Double.toString(analog5.getAverageVoltage()));
    	}
    }
    
    public void autonomous() {
    	myRobot.setSafetyEnabled(true);
    	//int iter = 0;
    	//double messageDuration = 1;
    	//double delay = 0.005;
    	
        while (isAutonomous() && isEnabled()) {
        	//SmartDashboard.putString("DB/String 0", Double.toString(analog5.getAngle()));
        	/*
        	newMxpOutput = Integer.toString(analogInput.getValue());
            if (mxpOutput != newMxpOutput) {
            	mxpOutput = newMxpOutput;
            	iter = 0;
            }
            
            if (iter > Math.round(messageDuration/delay)) {
            	iter = 0;
            	mxpOutput = "";
            	newMxpOutput = "";
            }
            if (mxpOutput != "") {
            	SmartDashboard.putString("DB/String 0", mxpOutput);
            	iter++;
            } else  {
            	SmartDashboard.putString("DB/String 0", "");
            }
            Timer.delay(delay);
            */
        }
    }
    public void outputStringToDash(int num, String str) {
    	SmartDashboard.putString("DB/String "+num, str);
    }
    public double accelerate(int iteration) {
        int a = 2;
        double x = iteration / 400;
        double powered = Math.pow(x, a);
        return powered/(powered + Math.pow(1-x,a));
    }

    public double getIteration(double current, double last, int iteration) {
    	if (current > last) {
    		return iteration++;
    	} else if (current < last) {
    		return iteration--;
    	} else {
    		return iteration;
    	}
    }
}