package org.usfirst.frc.team4534.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import org.usfirst.frc.team4534.robot.AccelerativeJoystick;

public class Lift {
	Jaguar liftMotor;
	DigitalInput LM_TOP,LM_BOTTOM;
	AccelerativeJoystick joystick;
	int upButton,downButton,stopButton;
	
	private final Double LIFT_SPEED = 0.3;
	private final Double LIFT_INCREMENT = 0.05;
	
	public enum LiftState {
		MOVING_UP,
		UP,
		MOVING_DOWN,
		DOWN,
		EMERGENCY_STOPPED
	}
	
	private LiftState currentLiftState;
	
	public Lift(Jaguar liftMotor, DigitalInput LM_TOP, DigitalInput LM_BOTTOM, AccelerativeJoystick joystick, int upButton, int downButton, int stopButton) {
		//TODO:Constructor
		
		this.liftMotor = liftMotor;
		this.LM_TOP = LM_TOP;
		this.LM_BOTTOM = LM_BOTTOM;
		this.joystick = joystick;
		this.upButton = upButton;
		this.downButton = downButton;
		this.stopButton = stopButton;
		
		//assume the lift is down
		this.currentLiftState = LiftState.DOWN;
	}
	
	public LiftState getCurrentLiftState() {
		return currentLiftState;
	}
	
	private boolean touchingLimitUp() {
		return LM_TOP.get();
	}
	
	private boolean touchingLimitDown() {
		return LM_BOTTOM.get();
	}
	
	public void moveUp() {
		double speed = .3;
		if (joystick.getRawAxis(2) != 0) {
			speed = .2;
		}
		if (joystick.getRawAxis(3) != 0) {
			speed = .5;
		}

		System.out.println("lift.moveUp() " + speed);
		if (!touchingLimitUp()) {
	    	liftMotor.set(speed);
	    	currentLiftState = LiftState.MOVING_UP;
		} else {
			liftMotor.set(0.0);
	    	currentLiftState = LiftState.UP;
	    }
	}
	
	public void moveDown() {
		double speed = .3;
		if (joystick.getRawAxis(2) != 0) {
			speed = .2;
		}
		if (joystick.getRawAxis(3) != 0) {
			speed = .6;
		}
		
		System.out.println("lift.moveDown() -" + speed);
		if (!touchingLimitDown()) {
	    	liftMotor.set(-speed);
	    	currentLiftState = LiftState.MOVING_DOWN;
		} else {
	    	liftMotor.set(0.0);
	    	currentLiftState = LiftState.DOWN;
	    }
	}
	
	public void move() {
		
		double moveVal = joystick.getAcceleratedUpDownButtons(0, upButton, downButton, LIFT_INCREMENT, LIFT_SPEED);
		if (!touchingLimitUp() && !touchingLimitDown()) {
			liftMotor.set(moveVal);
			System.out.println("Lift moving: " + moveVal);
		} else {
			liftMotor.set(0.0);
		}
	}
	
	public void emergencyStop() {
		liftMotor.set(0.0);
		currentLiftState = LiftState.EMERGENCY_STOPPED;
	}
	
	public void poll() {
		poll(false);
	}
	
	public void poll(boolean autoMode) {
		
		
		//This function needs to be called continuously to ensure the lift shuts off when it does
		
		//SAFETY FIRST, check if the e-stop button is pressed
		if(joystick.getRawButton(stopButton)) {
			emergencyStop();
		} else {
			if (!autoMode) {
				//move();
			}
		}
		
		if(joystick.getRawButton(upButton)) {
			moveUp();
		} else if(joystick.getRawButton(downButton)) {
			moveDown();
		}/* else {
			liftMotor.set(0.0);
		}*/
		if(joystick.getRawAxis(3) != 0.0 && !touchingLimitUp() && !touchingLimitDown()) {
			if(currentLiftState == LiftState.MOVING_UP) {
				liftMotor.set(LIFT_SPEED+0.2);
			} else if(currentLiftState == LiftState.MOVING_DOWN){
				liftMotor.set(-(LIFT_SPEED+0.2));
			}
		} else if(!touchingLimitUp() && !touchingLimitDown()){
			if(currentLiftState == LiftState.MOVING_UP) {
				liftMotor.set(LIFT_SPEED);
			} else if(currentLiftState == LiftState.MOVING_DOWN){
				liftMotor.set(-LIFT_SPEED);
			}
		}
		
		
		//next, check if the limit switches are pressed
		if(touchingLimitUp() && currentLiftState == LiftState.MOVING_UP) {
			liftMotor.set(0.0);
			currentLiftState = LiftState.UP;
		}
		
		if(touchingLimitDown() && currentLiftState == LiftState.MOVING_DOWN) {
			liftMotor.set(0.0);
			currentLiftState = LiftState.DOWN;
		}
		
		
		
	}
	
	
	
}
