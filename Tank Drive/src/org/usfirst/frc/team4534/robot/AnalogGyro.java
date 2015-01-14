package org.usfirst.frc.team4534.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.AnalogInput;

import org.usfirst.frc.team4534.robot.AnalogGyroThread;

public class AnalogGyro {
	private static AnalogInput gyro;
	private static AnalogInput temp;
	private static ArrayList<Double> values;
	private static Double gyroBase = 2.5;
	private static Double tempBase = 2.5;
	private static Double degreesCelcius = 25.0;
	private static Double angle;
	private static boolean isTemp;
	
	private static final int SAMPLE_SIZE = 100;
	private static final int TEMP_SAMPLE_SIZE = 10;
	
	public AnalogGyro(int channel) {
		gyro = new AnalogInput(channel);
		initGyro();
	}
	
	public AnalogGyro(int channelGyro, int channelTemp) {
		gyro = new AnalogInput(channelGyro);
		temp = new AnalogInput(channelTemp);
		isTemp = true;
		initGyro();
	}
	
	public AnalogGyro(AnalogInput ai) {
		gyro = ai;
		initGyro();
	}
	
	public AnalogGyro(AnalogInput ai1, AnalogInput ai2) {
		gyro = ai1;
		temp = ai2;
		isTemp = true;
		initGyro();
	}
	
	public static void getTemp() {
		//calculate new temperature
		while(values.size() <= TEMP_SAMPLE_SIZE) {
			values.add(temp.getVoltage());
		}
		
		Double avg = 0.0;
		for(Double val : values) {
			avg += val;
		}
		avg = avg.doubleValue() / values.size(); //should be around 2.5V with the KOP gyro from recent years (2011-2014)
		
		Double milliVolts = avg - tempBase;
		
		//divide by 9mv per degree
		Double alteredDegrees = milliVolts / 9;
	}
	
	public static void initGyro() {
		while(values.size() <= SAMPLE_SIZE) {
			values.add(gyro.getVoltage());
		}
		
		Double avg = 0.0;
		for(Double val : values) {
			avg += val;
		}
		avg = avg.doubleValue() / values.size(); //should be around 2.5V with the KOP gyro from recent years (2011-2014)
		gyroBase = avg;
		
		(new Thread(new AnalogGyroThread())).start();
	}
	
	
	static Double getAngle() {
		return angle;
	}
	
	static void setAngle(Double a) {
		angle = a;
	}
	
	private static class GyroLoop implements Runnable {

		@Override
		public void run() {
			try {
				while(true) {
				Double alteredValue = gyro.getVoltage() - gyroBase;
				
				Double millivolts = alteredValue*1000;
				
				//divide by 7mv per degree, and add to angle
				Double alterAngle = millivolts/7;
				
				angle += alterAngle;
				
				//if less than 0, or greater than 360, loop over
				while(angle < 0) {
					angle += 360;
				}
			
				while(angle > 360) {
					angle -= 360;
				}
				
				Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				//TODO: Do something here.
			}
		}
		
	}
	
}
