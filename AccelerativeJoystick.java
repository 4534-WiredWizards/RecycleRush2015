package org.usfirst.frc.team4534.robot;

import edu.wpi.first.wpilibj.Joystick;

public class AccelerativeJoystick extends Joystick {
	public double heavyRumbleCount;
	private Double[] storedInputArray;
	
	public AccelerativeJoystick(int port) {
		
		//cast to the superclass
		super(port);
		
		init();
		
	}
	
	public void init() {
		//count the axises and assume default values of zero for acceleration
		int axisCount = super.getAxisCount();
				
		storedInputArray = new Double[axisCount];
				
		for (int i=0;i<axisCount;i++) {
			storedInputArray[i] = 0.0;
			//System.out.println(Integer.toString(i));
		}
	}
	
	public Double getAcceleratedAxis(int axis) {
		double currentValue = super.getRawAxis(axis);
		int mode = 0;
		//System.out.println(storedInputArray[2]);
		if (super.getRawAxis(2) != 0) {
			mode = 2;
		}
		if (super.getRawAxis(3) != 0) {
			mode = 3;
		}
		
		storedInputArray[axis] = accelerateValue(storedInputArray[axis],currentValue,mode);
		
		return storedInputArray[axis];
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
	
	private Double accelerateValue(Double currentValue, Double targetValue, int mode) {
		Double incrementValue = 0.1;
		Double maxValue = 1.0;
		if (mode == 2) {
			// Precise mode
			incrementValue = 0.05;
			maxValue = 0.7;
		} else if (mode == 3) {
			// Fast mode
			incrementValue = 0.5;
			maxValue = 1.0;
		}
		System.out.println("mode "+Integer.toString(mode) + " inc: " + Double.toString(incrementValue) + " max: " + Double.toString(maxValue));

		
		// If the difference between the target value and the current value 
    	// are less than the increment value, set the increment value to the difference.
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
        
        //System.out.println(mode);
        
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
