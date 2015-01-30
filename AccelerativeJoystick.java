package org.usfirst.frc.team4534.robot;

import edu.wpi.first.wpilibj.Joystick;

public class AccelerativeJoystick extends Joystick {
	
	private Double[] storedInputArray;
	
	public AccelerativeJoystick(int port) {
		
		//cast to the superclass
		super(port);
		
		//count the axises and assume default values of zero for acceleration
		int axisCount = super.getAxisCount();
		
		storedInputArray = new Double[axisCount];
		
		for(int i=0;i<axisCount;i++) {
			storedInputArray[i] = 0.0;
		}
		
	}
	
	public Double getAcceleratedAxis(int axis) {
		double currentValue = super.getRawAxis(axis);
		
		storedInputArray[axis] = accelerateValue(storedInputArray[axis],currentValue);
		
		return storedInputArray[axis];
	}
	
	
	
	private Double accelerateValue(Double currentValue, Double targetValue) {
		Double incrementValue = 0.2;
		
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
	
	public void rumble(boolean on) {
		if(on) {
			super.setRumble(RumbleType.kLeftRumble, 1);
			super.setRumble(RumbleType.kRightRumble, 1);
		} else {
			super.setRumble(RumbleType.kLeftRumble, 0);
			super.setRumble(RumbleType.kRightRumble, 0);
		}
	}
}
