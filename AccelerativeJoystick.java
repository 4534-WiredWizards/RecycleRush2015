package org.usfirst.frc.team4534.robot;

import edu.wpi.first.wpilibj.Joystick;

public class AccelerativeJoystick extends Joystick {
	public double heavyRumbleCount;
	private Double[] storedInputArray;
	private Double[] storedButtonArray;
	
	public AccelerativeJoystick(int port) {
		
		//cast to the superclass
		super(port);
		
		init();
		
	}
	
	public void init() {
		//count the axises and assume default values of zero for acceleration
		int axisCount = super.getAxisCount();
				
		storedInputArray = new Double[axisCount];
		storedButtonArray = new Double[1];
		
		for (int i=0;i<axisCount;i++) {
			storedInputArray[i] = 0.0;
			//System.out.println(Integer.toString(i));
		}
	}
	
	public Double getAcceleratedAxis(int axis) {
		double currentValue = super.getRawAxis(axis);
		
		double incrementValue = 0.1;
		double maxValue = 0.8;

		if (super.getRawAxis(2) != 0) {
			incrementValue = 0.05;
			maxValue = 0.5;
		}
		if (super.getRawAxis(3) != 0) {
			incrementValue = 0.5;
			maxValue = 1.0;
		}
		
		storedInputArray[axis] = accelerateValue(storedInputArray[axis], currentValue, incrementValue, maxValue);
		
		return storedInputArray[axis];
	}
	
	
	
	public Double getAcceleratedUpDownButtons(int key, int upButton, int downButton, double incrementValue, double maxValue) {
		boolean currentValueUp = super.getRawButton(upButton);
		boolean currentValueDown = super.getRawButton(downButton);

		double actualValue = 0;
		if (currentValueUp) {
			actualValue += maxValue;
		}
		if (currentValueDown) {
			actualValue -= maxValue;
		}
		
		
		storedButtonArray[key] = accelerateValue(storedButtonArray[key], actualValue, incrementValue, maxValue);
		
		return storedButtonArray[key];
	}
	
	
	public void accelRumble() {
        if (heavyRumbleCount > 0) {
        	rumble(1);
        	heavyRumbleCount --;
        } 
        else {
        	rumble(0);
        }
	}
	
	private Double accelerateValue(Double currentValue, Double targetValue, Double incrementValue, Double maxValue) {
		// If the difference between the target value and the current value 
    	// are less than the increment value, set the increment value to the difference.
		System.out.println("cur" + currentValue);
		System.out.println("target" + targetValue);
		System.out.println("inc" + incrementValue);
		System.out.println("max" + maxValue);
    	incrementValue = Math.min(incrementValue, Math.abs(targetValue - currentValue));
    	
    	// Depending on whether the target is greater than or less than the
    	// current value, increment or decrement from the current value.
        if (targetValue > currentValue) {
        	currentValue += incrementValue;
        } 
        else if (targetValue < currentValue) {
        	currentValue -= incrementValue;
        }
        
        // Lower and upper limits for the value
        currentValue = Math.min(maxValue, currentValue);
        currentValue = Math.max(-maxValue, currentValue);
        
    	return currentValue;
	}
	
	public void rumble(boolean on) {
		if(on) {
			super.setRumble(RumbleType.kLeftRumble, 1);
			super.setRumble(RumbleType.kRightRumble, 1);
		} 
		else {
			super.setRumble(RumbleType.kLeftRumble, 0);
			super.setRumble(RumbleType.kRightRumble, 0);
		}
	}
	
	public void rumble(double val) {
		super.setRumble(RumbleType.kLeftRumble, (float) val);
		super.setRumble(RumbleType.kRightRumble, (float) val);
	}
}
