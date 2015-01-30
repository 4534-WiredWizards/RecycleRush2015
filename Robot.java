package org.usfirst.frc.team4534.robot;


import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
	
    RobotDrive myRobot;  // class that handles basic drive operations
    AccelerativeJoystick controller;
    Double leftAxis;
    Double rightAxis;
    AnalogInput analogInput;
    BuiltInAccelerometer accel;
    Gyro gyro;
    SerialPort serial;
    Lift lift;
    AnalogInput analogGyro,analogTemp;

    DigitalInput LM_TOP;
    DigitalInput LM_BOTTOM;
    
    
    
    public Robot() {
        myRobot = new RobotDrive(0, 1);
        myRobot.setExpiration(0.1);
        controller = new AccelerativeJoystick(0);
        leftAxis = controller.getRawAxis(1);
        rightAxis = controller.getRawAxis(0);
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
        
        gyro = new Gyro(0);
    }

        
    /*
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	
        myRobot.setSafetyEnabled(true);

        while (isOperatorControl() && isEnabled()) {
        	
        	//first, poll the lift
        	lift.poll();
        	
        	double axisX = controller.getAcceleratedAxis(1);
        	double axisY = controller.getAcceleratedAxis(0)*-1;
        	
        	
        	// Uses the stored inputs instead of the joystick inputs (allows acceleration).
        	myRobot.arcadeDrive(axisX, axisY);
            Timer.delay(0.005);		// wait for a motor update time
            
            //reports averaged accelerometer 
            double accelx1 = (100 * accel.getX());
            double accelx2 = (100 * accel.getX());
            double accelx3 = (100 * accel.getX());
            double accelx4 = (100 * accel.getX());
            double accelx5 = (100 * accel.getX());
            double accely1 = (100 * accel.getY());
            double accely2 = (100 * accel.getY());
            double accely3 = (100 * accel.getY());
            double accely4 = (100 * accel.getY());
            double accely5 = (100 * accel.getY());
            
            double accelxAvg = Math.floor(((accelx1 + accelx2 + accelx3 + accelx4 + accelx5) / 5));
            double accelyAvg = Math.floor(((accely1 + accely2 + accely3 + accely4 + accely5) / 5));
            
            if ((accelyAvg > 80) || (accelxAvg > 80)) {
            	controller.rumble(true);
//            	Timer.delay(.25);
//				this delays ((ALL)) robot code by .25 seconds to rumble controller for .25 seconds, improvement for how rumble is managed is likely important
            }
            else {
            	controller.rumble(false);
            }
            
            outputStringToDash(3, Double.toString(accelxAvg));
            outputStringToDash(4, Double.toString(accelyAvg));
        }
    }
    
    //these values are used to have the robot correct itself in the turn method
    private final double libertyStop = 1;
    private final double libertySlow = 15;
    private final double libertyMedium = 30;
    
    
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
    
    public void test() {
    	boolean rumble = true;
    	while (isTest() && isEnabled()) {
    		if(rumble) {
    			rumble = false;
    		} else {
    			rumble = true;
    		}
    		
    		controller.rumble(rumble);
    		Timer.delay(0.5);
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
    
}
