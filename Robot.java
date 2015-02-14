package org.usfirst.frc.team4534.robot;


import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.DriverStation;

public class Robot extends SampleRobot {
	
    RobotDrive myRobot;  // class that handles basic drive operations
    AccelerativeJoystick controller;
    Double leftAxis;
    Double rightAxis;
    AnalogInput analogInput;
    AveragedBuiltInAccelerometer accel;
    Gyro gyro;
    
    SerialPort tegraSerial;
    SerialPort arduinoSerial;
    
    Lift lift;
    Jaguar liftMotor;
    AnalogInput analogGyro,analogTemp;

    DigitalInput LM_TOP;
    DigitalInput LM_BOTTOM;
    
    Command autonomousCommand;
    SendableChooser chooser;
    
    DriverStation.Alliance allianceColor;
    
    
    
    
    public Robot() {
    	
    	tegraSerial = new SerialPort(115200, SerialPort.Port.kOnboard);
    	arduinoSerial = new SerialPort(115200, SerialPort.Port.kMXP);
    	// myRobot = new RobotDrive(1, 2);
        // myRobot.setExpiration(0.1);
        
        controller = new AccelerativeJoystick(0);
        /*
        leftAxis = controller.getRawAxis(1);
        rightAxis = controller.getRawAxis(0);
        accel = new AveragedBuiltInAccelerometer();
        //serial 
        */
        
        
        final Integer liftMotorPort = 0;
        final Integer liftUpButtonNumber = 4;
        final Integer liftDownButtonNumber = 1;
        final Integer liftEmergencyStopButtonNumber = 2;

        liftMotor = new Jaguar(liftMotorPort);
        
        // These might need to be changed later. 
        LM_TOP = new DigitalInput(0);
        LM_BOTTOM = new DigitalInput(1);
        
        //initialize the lift
        lift = new Lift(liftMotor, LM_TOP, LM_BOTTOM, controller, liftUpButtonNumber, liftDownButtonNumber, liftEmergencyStopButtonNumber);
        
        gyro = new Gyro(0);
        
        chooser = new SendableChooser();
        //chooser.addObject(name, object);
        chooser.addDefault("Enter Auto Zone w/no Bump", "-1");
        chooser.addDefault("Enter Auto Zone with Bump", "0");
        chooser.addObject("1 Tote Auto", "1");
        chooser.addObject("2 Tote Auto", "2");
        chooser.addObject("3 Tote Auto", "3");
        SmartDashboard.putData("Chooser", chooser);
        
        allianceColor = DriverStation.getInstance().getAlliance();

        
        
    }

        
    /*
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	
    	controller.init();
    	
        //myRobot.setSafetyEnabled(true);

        while (isOperatorControl() && isEnabled()) {
        	
        	//first, poll the lift
        	lift.poll();
        	
        	/*
        	double axisX = controller.getAcceleratedAxis(1)*-1;
        	double axisY = controller.getAcceleratedAxis(0)*-1;
        	
        	
        	// Uses the stored inputs instead of the joystick inputs (allows acceleration).
        	myRobot.arcadeDrive(axisX, axisY);
            Timer.delay(0.005);		// wait for a motor update time
            
            double accelxAvg = 100*accel.getAverageX();
            double accelyAvg = 100*accel.getAverageY();
            double accelzAvg = 100*accel.getAverageZ();
            
            if ((accelyAvg > 80) || (accelxAvg > 80)) {
            	// if collided with something, or moved a little Too quickly,
            	controller.heavyRumbleCount = 10;
            	// TODO: incorporate other rumble check:  if joystick up and no forward movement, vibrate lightly; etc
            }
            
            controller.accelRumble();
            
            outputStringToDash(3, Double.toString(accelxAvg));
            outputStringToDash(4, Double.toString(accelyAvg));
            outputStringToDash(5, Double.toString(accelzAvg));
            */
        }
        
        
    }
    
    public void turn(Double ang, boolean scaledLiberties) {
    	double libertyStop = 1;
        double libertySlow = 15;
        double libertyMedium = 30;
    	if(scaledLiberties) {
    		libertyStop = 1;/*Math.ceil(ang/90);*/
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
    		
    		//System.out.println(gyro.getAngle());
    		
    		
    		while ((gyro.getAngle() < angle-libertyStop) && isEnabled()) {
    			Double speed = fastSpeed;
    			
    			SmartDashboard.putNumber("Gyro", gyro.getAngle());
    			
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
    			
    			SmartDashboard.putNumber("Gyro", gyro.getAngle());
    			
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
    	
    	SmartDashboard.putNumber("Gyro", gyro.getAngle());
    		
    	myRobot.drive(0.0,0.0);
    	myRobot.setSafetyEnabled(false);
    }
    
    private static double visionTargetDistance;
    private final static double visionTargetLiberty = 60.0;
    
    private Double getUpdatedVisionTargetDistance() {
    	//clear buffer
    	tegraSerial.readString();
    	
    	int reps = 1;
    	
    	double sum = 0.0;
    	
    	for(int i=0;i<reps;i++) {
    		String message = tegraSerial.readString();
    		System.out.println(message);
    		double firstValue = 0.0;
    		try{
    			String[] arr = message.split(";");
    			firstValue = Double.parseDouble(arr[arr.length-1]);
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
    	
    	double turnValue = average/10;
    	
    	return turnValue;
    }
    
    public Double getTurnSpeed(Double visionTargetDis) {
    	Double ret = Math.abs(visionTargetDis) / 320;
    	if(ret > 1.0) ret = 1.0;
    	ret += 0.5;    	
    	if(ret < 0.0) ret = 0.0;
    	return ret;
    }
    
    public void visionTurn() {
    	if (isEnabled()) {
    		
    		Double angle = 0.0;
    		
    		myRobot.setSafetyEnabled(true);
    		
    		
    		
    		visionTargetDistance = getUpdatedVisionTargetDistance();
    		
    		Double turningSpeed = getTurnSpeed(visionTargetDistance);
    		
    		while ((visionTargetDistance < angle-visionTargetLiberty) && isEnabled()) {
    			visionTargetDistance = getUpdatedVisionTargetDistance();
    			turningSpeed = getTurnSpeed(visionTargetDistance);
    			Double speed = turningSpeed;    			
    			
    			myRobot.tankDrive(speed, -speed);
    			
    			//turnSpeed = turnSpeed*0.75;
    			//outputStringToDash(0,Double.toString(gyro.getAngle()));
    		}
    			
    		while ((visionTargetDistance > angle+visionTargetLiberty) && isEnabled()) {
    			visionTargetDistance = getUpdatedVisionTargetDistance();
    			turningSpeed = getTurnSpeed(visionTargetDistance);
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
    	
    	//boolean rumble = true;
    	while (isTest() && isEnabled()) {
            liftMotor.set(controller.getAcceleratedAxis(1));
    		
    	}
    }
    
    public void autonomous() {
    	myRobot.setSafetyEnabled(false);
    	
       if (isAutonomous() && isEnabled()) {
        	Object chosenValue = chooser.getSelected();
        	
        	int totes = 0;
        	
        	try {
        		totes = Integer.parseInt(chosenValue.toString());
        	} catch (Exception e){
        		totes = 0;
        	}
        	
        	switch(totes) {
        		case -1:
        		default:
        			//drive into auto zone only
        			driveIntoAutoZone();
        			break;
        		case 0:
        			driveIntoAutoZone(-0.775,3.0);
        		case 1:
        			/*
        			//lift crate
        			lift.moveUp();
        			//wait for stop
        			while(lift.getCurrentLiftState() != Lift.LiftState.UP && lift.getCurrentLiftState() != Lift.LiftState.EMERGENCY_STOPPED) {
        				lift.poll();
        			}
        			*/
        			//turn 180
        			turn(180.0,true);
        			//delay part a sec
        			Timer.delay(0.1);
        			//correct turn
        			turn(178.0-gyro.getAngle(),true);
        			//drive into auto zone
        			driveIntoAutoZone(0.75,3.0);
        			break;
        		case 2:
        			/*
        			//lift crate
        			lift.moveUp();
        			//wait for stop
        			while(lift.getCurrentLiftState() != Lift.LiftState.UP && lift.getCurrentLiftState() != Lift.LiftState.EMERGENCY_STOPPED) {
        				lift.poll();
        			}
        			*/
        			//move back some
        			driveIntoAutoZone(-0.8,1.0);
        			//turn 90
        			turn(90.0,true);
        			//delay part a sec
        			Timer.delay(0.1);
        			//correct turn
        			turn(88.0-gyro.getAngle(),true);
        			//drive one crate distance
        			driveOneCrateDistanceSideways();
        			//turn -90
        			turn(-90.0,true);
        			//delay part a sec
        			Timer.delay(0.1);
        			//correct turn
        			turn(-88.0-gyro.getAngle(),true);
        			//align to crate
        			visionTurn();
        			//drive forward
        			driveOneCrateDistanceForwards();
        			/*
        			//lift crate
        			lift.moveUp();
        			//wait for stop
        			while(lift.getCurrentLiftState() != Lift.LiftState.UP && lift.getCurrentLiftState() != Lift.LiftState.EMERGENCY_STOPPED) {
        				lift.poll();
        			}
        			*/
        			//drive into auto zone
        			driveIntoAutoZone();
        			break;
        			
        	}
        	
        	//visionTurn();
        	//turn(turnValue);
        	
        	//myRobot.tankDrive(1.0, 1.0);
        	//Timer.delay(0.5);
        	//myRobot.tankDrive(0.0,0.0);
        	
        }
       
        while(isAutonomous() && isEnabled()) {
        	SmartDashboard.putNumber("Gyro", gyro.getAngle());
        }
        
        myRobot.setSafetyEnabled(true);
    }
    
    public void disabled() {
    	while(!isEnabled()) {
    		if (allianceColor != DriverStation.getInstance().getAlliance()) {
    			allianceColor = DriverStation.getInstance().getAlliance();
    			//Send message
    			String message = "";
    			if (allianceColor == DriverStation.Alliance.Blue) {
    				// In the blue alliance
    				message = "b";
    			} else if (allianceColor == DriverStation.Alliance.Red) {
    				// In the red alliance
    				message = "r";
    			} else {
    				// Alliance not set.
    				message = "d";
    			}
    			arduinoSerial.writeString(message);
    		}
    	}
    }
    
    private void driveIntoAutoZone() {
    	driveIntoAutoZone(-0.75,3.0);
    }
    
    private void driveIntoAutoZone(Double speed, Double time) {
    	//this code drives into the auto zone
    	myRobot.tankDrive(speed,speed);
    	Timer.delay(time);
    	myRobot.tankDrive(0.0, 0.0);
    }
    
    private void driveOneCrateDistanceSideways() {
    	driveIntoAutoZone(0.825,2.0);
    }
    
    private void driveOneCrateDistanceForwards() {
    	driveIntoAutoZone(0.775,1.0);
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
