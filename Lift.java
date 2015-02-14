package org.usfirst.frc.team4534.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import org.usfirst.frc.team4534.robot.AccelerativeJoystick;

public class Lift {
	Jaguar liftMotor;
	DigitalInput LM_TOP,LM_BOTTOM;
	AccelerativeJoystick joystick;
	int upButton,downButton,stopButton;
	
	private final Double LIFT_SPEED = 0.2;
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
		System.out.println("lift.moveUp() " + LIFT_SPEED);
		//if (!touchingLimitUp()) {
	    	liftMotor.set(LIFT_SPEED);
	    //	currentLiftState = LiftState.MOVING_UP;
		//} else {
	    //	liftMotor.set(0.0);
	    //	currentLiftState = LiftState.UP;
	    //}
	}
	
	public void moveDown() {
		System.out.println("lift.moveDown() -" + LIFT_SPEED);
		//if (!touchingLimitDown()) {
	    	liftMotor.set(-LIFT_SPEED);
	    //	currentLiftState = LiftState.MOVING_DOWN;
		//} else {
	    //	liftMotor.set(0.0);
	    //	currentLiftState = LiftState.DOWN;
	    //}
	}
	
	public void move() {
		
		double moveVal = joystick.getAcceleratedUpDownButtons(0, upButton, downButton, LIFT_INCREMENT, LIFT_SPEED);
		//if (!touchingLimitUp() && !touchingLimitDown()) {
			liftMotor.set(moveVal);
			System.out.println("Lift moving: " + moveVal);
		//} else {
		//	liftMotor.set(0.0);
		//}
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
		/*if(joystick.getRawButton(stopButton)) {
			emergencyStop();
		} else {
			if (!autoMode) {
				move();
			}
		}*/
		
		if(joystick.getRawButton(upButton)) {
			moveUp();
		} else if(joystick.getRawButton(downButton)) {
			moveDown();
		} else {
			liftMotor.set(0.0);
		}
		
		
		/*
		//next, check if the buttons are pressed
		if(joystick.getRawButton(upButton)) {
			moveUp();
		} else if(joystick.getRawButton(downButton)) {
			moveDown();
		} else {
			liftMotor.set(0.0);
		}
		*/
		
		/*
		//next, check if the limit switches are pressed
		if(touchingLimitUp()) {
			liftMotor.set(0.0);
			currentLiftState = LiftState.UP;
		}
		
		if(touchingLimitDown()) {
			liftMotor.set(0.0);
			currentLiftState = LiftState.DOWN;
		}
		*/
		
		
	}
	
	
	
}
