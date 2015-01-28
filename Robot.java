package org.usfirst.frc.team4534.robot;


import edu.wpi.first.wpilibj.Jaguar;
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
    Lift lift;
    //String mxpOutput;
    //String newMxpOutput;
    //Talon motorTalon;
    AnalogInput analogGyro,analogTemp;

    DigitalInput LM_TOP;
    DigitalInput LM_BOTTOM;
    
    
    
    public Robot() {
        myRobot = new RobotDrive(0, 1);
        myRobot.setExpiration(0.1);
        controller = new Joystick(0);
        leftAxis = controller.getRawAxis(0);
        rightAxis = controller.getRawAxis(1);
        accel = new BuiltInAccelerometer();
        serial = new SerialPort(115200, SerialPort.Port.kMXP);
        
        final Integer liftMotorPort = 2;
        final Integer liftUpButtonNumber = 4;
        final Integer liftDownButtonNumber = 1;
        final Integer liftEmergencyStopButtonNumber = 2;
        

        
        // These might need to be changed later. 
        LM_TOP = new DigitalInput(0);
        LM_BOTTOM = new DigitalInput(1);
        
        //initialize the lift
        lift = new Lift(new Jaguar(liftMotorPort),LM_TOP,LM_BOTTOM,controller,liftUpButtonNumber, liftDownButtonNumber, liftEmergencyStopButtonNumber);
        
        
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
        	
        	//first, poll the lift
        	lift.poll();
        	
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
    	if (isEnabled()) {
    		
    		myRobot.setSafetyEnabled(true);
    		
    		Double mediumSpeed = 0.70;
    		Double slowSpeed = 0.60;
    		Double fastSpeed = 0.80;
    		
    		while ((scaleAngle(gyro.getAngle()) < angle-libertyStop) && isEnabled()) {
    			Double speed = fastSpeed;
    			
    			if (scaleAngle(gyro.getAngle()) > angle-libertySlow) {
    				speed = slowSpeed;
    			} else if (scaleAngle(gyro.getAngle()) > angle-libertyMedium) {
    				speed = mediumSpeed;
    			}
    			
    			
    			myRobot.tankDrive(speed, -speed);
    			
    			//turnSpeed = turnSpeed*0.75;
    			outputStringToDash(0,Double.toString(scaleAngle(gyro.getAngle())));
    		}
    			
    		while ((scaleAngle(gyro.getAngle()) > angle+libertyStop) && isEnabled()) {
    			Double speed = fastSpeed;
    			
    			if (scaleAngle(gyro.getAngle()) < angle+libertySlow) {
    				speed = slowSpeed;
    			} else if (scaleAngle(gyro.getAngle()) < angle+libertyMedium) {
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
    	if (isEnabled()) {
    		
    		gyro.reset();
    		
    		Double angle = gyro.getAngle()+ang;
    		
    		myRobot.setSafetyEnabled(true);
    		
    		Double mediumSpeed = 0.70;
    		Double slowSpeed = 0.60;
    		Double fastSpeed = 0.80;
    		
    		while ((gyro.getAngle() < angle-libertyStop) && isEnabled()) {
    			Double speed = fastSpeed;
    			
    			if (gyro.getAngle() > angle-libertySlow) {
    				speed = slowSpeed;
    			} else if (gyro.getAngle() > angle-libertyMedium) {
    				speed = mediumSpeed;
    			}
    			
    			
    			myRobot.tankDrive(speed, -speed);
    			
    			//turnSpeed = turnSpeed*0.75;
    			outputStringToDash(0,Double.toString(gyro.getAngle()));
    		}
    			
    		while ((gyro.getAngle() > angle+libertyStop) && isEnabled()) {
    			Double speed = fastSpeed;
    			
    			if (gyro.getAngle() < angle+libertySlow) {
    				speed = slowSpeed;
    			} else if (gyro.getAngle() < angle+libertyMedium) {
    				speed = mediumSpeed;
    			}
    			
    			
    			myRobot.tankDrive(-speed, speed);
    			
    			//turnSpeed = turnSpeed*0.75;
    			outputStringToDash(0,Double.toString(gyro.getAngle()));
    		}
    		
    	}
    		
    	myRobot.drive(0.0,0.0);
    }
    
    private Double scaleAngle(Double ang) {
    	//ang++;
    	while (ang <  0) {
    		ang += 360;
    	}    	
    	while (ang > 360) {
    		ang -= 360;
    	}
    	
    	return ang;
    }
    
    public void test() {
    	while (isTest() && isEnabled()) {
    		String str = serial.readString();
    		if (!str.isEmpty()) {
    			outputStringToDash(1,str);
    		} else {
    			outputStringToDash(2,Double.toString(Math.random()));
    		}
    	}
    }
    
    public void autonomous() {
    	myRobot.setSafetyEnabled(true);
    	
        while (isAutonomous() && isEnabled()) {
        	//outputStringToDash(0,Double.toString(gyro.getAngle()));
        	Double ang = Double.parseDouble(getStringFromDash(9));
        	if (ang != 0.0) {
        		turn(ang);
        		outputStringToDash(9, Double.toString(ang));
        	} else {
        		//TODO:Nothing
        	}
        }
    }
    
    public void outputStringToDash(int num, String str) {
    	SmartDashboard.putString("DB/String "+num, str);
    }
    
    public String getStringFromDash(int num) {
    	return SmartDashboard.getString("DB/String "+num);
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

    
}
