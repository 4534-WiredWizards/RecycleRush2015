package org.usfirst.frc.team4534.robot;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;

public class AveragedBuiltInAccelerometer extends BuiltInAccelerometer {
	public AveragedBuiltInAccelerometer() {
		super();
	}
	
	private final int SAMPLECOUNT = 5;
	
	public double getAverageX() {
		double total = 0.0;
		
		for (int i = 0;i<SAMPLECOUNT;i++) {
			total += super.getX();
		}
		
		return Math.floor(total / SAMPLECOUNT);
	}
	
	public double getAverageY() {
		double total = 0.0;
		
		for (int i = 0;i<SAMPLECOUNT;i++) {
			total += super.getY();
		}
		
		return Math.floor(total / SAMPLECOUNT);
	}
	
	public double getAverageZ() {
		double total = 0.0;
		
		for (int i = 0;i<SAMPLECOUNT;i++) {
			total += super.getZ();
		}
		
		return Math.floor(total / SAMPLECOUNT);
	}
}
