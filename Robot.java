package org.usfirst.frc.team4534.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DigitalInput;

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
    Gyro gyro;
    SerialPort serial;
    //String mxpOutput;
    //String newMxpOutput;
    //Talon motorTalon;
    AnalogInput analogGyro,analogTemp;

    DigitalInput LM_0;
    DigitalInput LM_1;
    
    boolean liftMovingUp;
    boolean liftMovingDown;
    
    
    
    public Robot() {
        myRobot = new RobotDrive(0, 1);
        myRobot.setExpiration(0.1);
        controller = new Joystick(0);
        leftAxis = controller.getRawAxis(0);
        rightAxis = controller.getRawAxis(1);
        accel = new BuiltInAccelerometer();
        serial = new SerialPort(115200, SerialPort.Port.kMXP);

        
        // These might need to be changed later. 
        // Currently "LM_0" is the top limit switch,
        // and "LM_1" is the bottom limit switch.
        LM_0 = new DigitalInput(0);
        LM_1 = new DigitalInput(1);
        
        
        //analogInput = new AnalogInput(33);
        //motorTalon = new Talon(5);
        //motorTalon.set(100);

        //mxpOutput = "";
        //newMxpOutput = "";
        
        gyro = new Gyro(0);
    }

        
    /*
     * Runs the motors with tank steering.
     */
    public void operatorControl() {
        myRobot.setSafetyEnabled(true);
        double storedInputX = 0.0;
        double storedInputY = 0.0;
        while (isOperatorControl() && isEnabled()) {
        	//outputStringToDash(0, Double.toString(analog5.getVoltage()));
        	
        	double newX = controller.getRawAxis(1);
        	double newY = controller.getRawAxis(0)*-1;
        	
        	// Increment the stored inputs based on the joystick inputs
        	storedInputX = accelerate(storedInputX, newX);
        	storedInputY = accelerate(storedInputY, newY);
        	
        	// Uses the stored inputs instead of the joystick inputs (allows acceleration).
        	myRobot.arcadeDrive(storedInputX, storedInputY);
            Timer.delay(0.005);		// wait for a motor update time
            
            // Test rumble, for later use to give feedback for collisions using accelerometer
            if (newX != 0.0) {
            	controller.setRumble(Joystick.RumbleType.kLeftRumble, 1);
            }
            if (newY != 0.0) {
            	controller.setRumble(Joystick.RumbleType.kRightRumble, 1);
            }
            outputStringToDash(3, Double.toString(accel.getX()));
            outputStringToDash(4, Double.toString(accel.getY()));
        }
    }
    
    private final double libertyStop = 1;
    private final double libertySlow = 15;
    private final double libertyMedium = 30;
    
    public void turnTo(double angle) {
    	if(isEnabled()){
    		
    		myRobot.setSafetyEnabled(true);
    		
    		Double mediumSpeed = 0.70;
    		Double slowSpeed = 0.60;
    		Double fastSpeed = 0.80;
    		
    		while((scaleAngle(gyro.getAngle()) < angle-libertyStop) && isEnabled()) {
    			Double speed = fastSpeed;
    			
    			if(scaleAngle(gyro.getAngle()) > angle-libertySlow) {
    				speed = slowSpeed;
    			} else if(scaleAngle(gyro.getAngle()) > angle-libertyMedium) {
    				speed = mediumSpeed;
    			}
    			
    			
    			myRobot.tankDrive(speed, -speed);
    			
    			//turnSpeed = turnSpeed*0.75;
    			outputStringToDash(0,Double.toString(scaleAngle(gyro.getAngle())));
    		}
    			
    		while((scaleAngle(gyro.getAngle()) > angle+libertyStop) && isEnabled()){
    			Double speed = fastSpeed;
    			
    			if(scaleAngle(gyro.getAngle()) < angle+libertySlow) {
    				speed = slowSpeed;
    			} else if(scaleAngle(gyro.getAngle()) < angle+libertyMedium) {
    				speed = mediumSpeed;
    			}
    			
    			
    			myRobot.tankDrive(-speed, speed);
    			
    			//turnSpeed = turnSpeed*0.75;
    			outputStringToDash(0,Double.toString(scaleAngle(gyro.getAngle())));
    		}
    		
    		myRobot.drive(0.0,0.0);
    	}
    }
    
    public void turn(Double ang) {
    	if(isEnabled()){
    		
    		gyro.reset();
    		
    		Double angle = gyro.getAngle()+ang;
    		
    		myRobot.setSafetyEnabled(true);
    		
    		Double mediumSpeed = 0.70;
    		Double slowSpeed = 0.60;
    		Double fastSpeed = 0.80;
    		
    		while((gyro.getAngle() < angle-libertyStop) && isEnabled()) {
    			Double speed = fastSpeed;
    			
    			if(gyro.getAngle() > angle-libertySlow) {
    				speed = slowSpeed;
    			} else if(gyro.getAngle() > angle-libertyMedium) {
    				speed = mediumSpeed;
    			}
    			
    			
    			myRobot.tankDrive(speed, -speed);
    			
    			//turnSpeed = turnSpeed*0.75;
    			outputStringToDash(0,Double.toString(gyro.getAngle()));
    		}
    			
    		while((gyro.getAngle() > angle+libertyStop) && isEnabled()){
    			Double speed = fastSpeed;
    			
    			if(gyro.getAngle() < angle+libertySlow) {
    				speed = slowSpeed;
    			} else if(gyro.getAngle() < angle+libertyMedium) {
    				speed = mediumSpeed;
    			}
    			
    			
    			myRobot.tankDrive(-speed, speed);
    			
    			//turnSpeed = turnSpeed*0.75;
    			outputStringToDash(0,Double.toString(gyro.getAngle()));
    		}
    		
    	}
    		
    	myRobot.drive(0.0,0.0);
    }
    
    private Double scaleAngle(Double ang){
    	//ang++;
    	while(ang <  0) {
    		ang += 360;
    	}    	
    	while(ang > 360) {
    		ang -= 360;
    	}
    	
    	return ang;
    }
    
    public void test() {
    	while (isTest() && isEnabled()) {
    		String str = serial.readString();
    		if(!str.isEmpty()) {
    			outputStringToDash(1,str);
    		} else {
    			outputStringToDash(2,Double.toString(Math.random()));
    		}
    	}
    }
    
    public void autonomous() {
    	myRobot.setSafetyEnabled(true);
    	//int iter = 0;
    	//double messageDuration = 1;
    	//double delay = 0.005;
    	
    	//turnTo(180.0);
    	
        while (isAutonomous() && isEnabled()) {
        	//outputStringToDash(0,Double.toString(gyro.getAngle()));
        	Double ang = Double.parseDouble(SmartDashboard.getString("DB/String 9"));
        	if(ang != 0.0) {
        		turn(ang);
        		SmartDashboard.putString("DB/String 9", "0.0");
        	} else {
        		//TODO:Nothing
        	}
        	
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
    
    public double accelerate(double currentValue, double targetValue) {
    	double incrementValue = .2;
    	
    	// If the difference between the target value and the current value 
    	// are less than the increment value, set the increment value to the difference.
    	incrementValue = Math.min(incrementValue, Math.abs(targetValue - currentValue));
    	
    	// Depending on whether the target is greater than or less than the
    	// current value, increment or decrement from the current value.
        if (targetValue > currentValue) {
        	currentValue += incrementValue;
        } else if (targetValue < currentValue) {
        	currentValue -= incrementValue;
        }
        
        // Lower and upper limits for the value
        currentValue = Math.min(1, currentValue);
        currentValue = Math.max(-1, currentValue);
        
    	return currentValue;
    }

    public boolean touchingLiftLimitUp() {
    	return LM_0.get();
    }
    
    public boolean touchingLiftLimitDown() {
    	return LM_1.get();
    }
    
    public boolean liftStopButtonPressed() {
    	return false;
    }
    
    public boolean isPressingLiftButtonUp() {
    	return false;
    }
    
    public boolean isPressingLiftButtonDown() {
    	return false;
    }
    
    public void onLiftUp() {
    	if (!touchingLiftLimitUp() && !liftStopButtonPressed() &&
    		(isPressingLiftButtonUp() || liftMovingUp)) {
    		//set whatever motor to go up
    		liftMovingUp = true;
    	} else {
    		//stop whatever motor
    		liftMovingUp = false;
    	}
    }
    
    public void onLiftDown() {
    	if (!touchingLiftLimitUp() && !liftStopButtonPressed() &&
    		(isPressingLiftButtonDown() || liftMovingDown)) {
    		//set whatever motor to go down
    		liftMovingDown = true;
    	} else {
    		//stop whatever motor
    		liftMovingDown = false;
    	}
    }
}
