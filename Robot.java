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
            	controller.heavyRumbleCount = 10;
            	// TODO: incorporate other rumble check:  if joystick up and no forward movement, vibrate lightly; etc
            }
            
            controller.accelRumble();
            
            outputStringToDash(3, Double.toString(accelxAvg));
            outputStringToDash(4, Double.toString(accelyAvg));
        }
        
        
    }
    
    public void turn(Double ang, boolean scaledLiberties) {
    	double libertyStop = 1;
        double libertySlow = 15;
        double libertyMedium = 30;
    	if(scaledLiberties) {
    		libertyStop = Math.ceil(ang/90);
    		libertySlow = Math.ceil(ang/6);
    		libertyMedium = Math.ceil(ang/3);
    	}
    	turn(ang, libertyStop, libertySlow, libertyMedium);
    }
    
    public void turn(Double ang) {
        //these values are used to have the robot correct itself in the turn method
        final double libertyStop = 1;
        final double libertySlow = 15;
        final double libertyMedium = 30;
    	turn(ang, libertyStop, libertySlow, libertyMedium);
    }
    
    
    public void turn(Double ang, double libertyStop, double libertySlow, double libertyMedium) {
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
    
    private static double visionTargetDistance;
    private final static double visionTargetLiberty = 15.0;
    
    private Double getUpdatedVisionTargetDistance() {
    	//clear buffer
    	serial.readString();
    	
    	int reps = 1;
    	
    	double sum = 0.0;
    	
    	for(int i=0;i<reps;i++) {
    		String message = serial.readString();
    		System.out.println(message);
    		double firstValue = 0.0;
    		try{
        		firstValue = Double.parseDouble(message.split(";")[0]);
        	} catch(NumberFormatException e) {
        		
        	} catch(ArrayIndexOutOfBoundsException e) {
        		
        	}
    		if(firstValue != 0.0) {
    			sum += firstValue;
    		} else {
    			i--;
    		}
    	}
    	
    	double average = sum/reps;
    	
    	double turnValue = average/5;
    	
    	return turnValue;
    }
    
    public void visionTurn() {
    	if (isEnabled()) {
    		
    		Double angle = 0.0;
    		
    		myRobot.setSafetyEnabled(true);
    		
    		Double turningSpeed = 0.80;
    		
    		visionTargetDistance = getUpdatedVisionTargetDistance();
    		
    		while ((visionTargetDistance < angle-visionTargetLiberty) && isEnabled()) {
    			visionTargetDistance = getUpdatedVisionTargetDistance();
    			Double speed = turningSpeed;    			
    			
    			myRobot.tankDrive(speed, -speed);
    			
    			//turnSpeed = turnSpeed*0.75;
    			//outputStringToDash(0,Double.toString(gyro.getAngle()));
    		}
    			
    		while ((visionTargetDistance > angle+visionTargetLiberty) && isEnabled()) {
    			visionTargetDistance = getUpdatedVisionTargetDistance();
    			Double speed = turningSpeed;    			
    			
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
    
    public void autonomous() {
    	myRobot.setSafetyEnabled(true);
    	
        while (isAutonomous() && isEnabled()) {
        	
        	visionTurn();
        	//turn(turnValue);
        	
        	//myRobot.tankDrive(1.0, 1.0);
        	//Timer.delay(0.5);
        	//myRobot.tankDrive(0.0,0.0);
        	
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
