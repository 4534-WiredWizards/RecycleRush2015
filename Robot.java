package org.usfirst.frc.team4534.robot;


import edu.wpi.first.wpilibj.AnalogInput;
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
    AveragedBuiltInAccelerometer accel;
    Gyro gyro;
    SerialPort serial = new SerialPort(115200, SerialPort.Port.kOnboard);;
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
        accel = new AveragedBuiltInAccelerometer();
        //serial 
        
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
            
            
            double accelxAvg = (100 * accel.getAverageX());
            double accelyAvg = (100 *accel.getAverageY());
            
            if ((accelyAvg > 80) || (accelxAvg > 80)) {
            	// if collided with something, or moved a little Too quickly,
            	controller.rumblecount = 10;
            	// TODO: incorporate other rumble check:  if joystick up and no forward movement, vibrate lightly; etc
            }
            if (controller.rumblecount > 0) {
            	controller.rumble(1);
            	controller.rumblecount --;
            } 
            else {
            	controller.rumble(0);
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
    	myRobot.setSafetyEnabled(false);
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
    
    private class AutoCommands {
    	public static final String LEFT = "L";
    	public static final String RIGHT = "R";
    	public static final String PERFECT = "P";
    	public static final String NONE = "N";
    }
    
    public void autonomous() {
    	myRobot.setSafetyEnabled(true);
    	
    	//clear the serial buffer
    	serial.readString();
    	
        while (isAutonomous() && isEnabled()) {
        	String message = serial.readString();
        	//System.out.println(message);
        	
        	String firstCharacter = "0";
        	
        	try{
        		firstCharacter = message.substring(0,1);
        	} catch(StringIndexOutOfBoundsException e) {
        		
        	}
        	
        	switch(firstCharacter) {
        		case AutoCommands.LEFT:
        			System.out.println("LEFT");
        			turn(-5.0);
        			break;
        		case AutoCommands.RIGHT:
        			System.out.println("RIGHT");
        			turn(5.0);
        			break;
        		case AutoCommands.PERFECT:
        			System.out.println("PERFECT");
        			break;
        		case AutoCommands.NONE:
        			System.out.println("NONE");
        			break;
        		default:
        			break;
        	
        	}
        	
        	Timer.delay(0.05);
        }
        
        myRobot.setSafetyEnabled(false);
    }
    
    
    
    public static StringBuilder singleOccurence(String s)
    {
        StringBuilder sb = new StringBuilder();
        if (s.length() > 0) {
            char prev = s.charAt(0);
            sb.append(prev);
            for (int i = 1; i < s.length(); ++i) {
                char cur = s.charAt(i);
                if (cur != prev) {
                    sb.append(cur);
                    prev = cur;
                }
            }
        }
        return sb;
    }
    
    public void outputStringToDash(int num, String str) {
    	SmartDashboard.putString("DB/String "+num, str);
    }
    
    public String getStringFromDash(int num) {
    	return SmartDashboard.getString("DB/String "+num);
    }
    
}
