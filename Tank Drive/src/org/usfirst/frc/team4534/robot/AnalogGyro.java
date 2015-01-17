package org.usfirst.frc.team4534.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team4534.robot.AnalogGyroThread;

public class AnalogGyro {
	private AnalogInput gyro;
	private AnalogInput temp;
	private ArrayList<Double> values;
	private Double gyroBase = 2.5;
	private Double tempBase = 2.5;
	private Double degreesCelcius = 25.0;
	private Double angle;
	private boolean isTemp;
	
	int counter;
	
	private  final int SAMPLE_SIZE = 100;
	private  final int TEMP_SAMPLE_SIZE = 10;
	
	public AnalogGyro(int channel) {
		values = new ArrayList<Double>();
		gyro = new AnalogInput(channel);
		initGyro();
	}
	
	public AnalogGyro(int channelGyro, int channelTemp) {
		values = new ArrayList<Double>();
		gyro = new AnalogInput(channelGyro);
		temp = new AnalogInput(channelTemp);
		isTemp = true;
		//initTemp();
		initGyro();
	}
	
	public AnalogGyro(AnalogInput ai) {
		values = new ArrayList<Double>();
		gyro = ai;
		initGyro();
	}
	
	public AnalogGyro(AnalogInput ai1, AnalogInput ai2) {
		values = new ArrayList<Double>();
		gyro = ai1;
		temp = ai2;
		isTemp = true;
		//initTemp();
		initGyro();
	}
	
	public  void initTemp() {
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
		degreesCelcius = (milliVolts / 9) - 252.7777778;
	}
	
	public  Double getTemp() {
		return degreesCelcius;
	}
	
	public  void setTemp(Double t) {
		degreesCelcius = t;
	}
	
	public  void initGyro() {
		initGyro(null);
	}
	
	public  void initGyro(Double temp) {
		
		boolean useTemp = !(temp == null);
		
		do {
			try {
				values.add(gyro.getVoltage());
			} catch (NullPointerException e) {
				
			}
		} while(values.size() <= SAMPLE_SIZE);
		
		Double avg = 0.0;
		for(Double val : values) {
			avg += val;
		}
		
		avg = avg.doubleValue() / values.size(); //should be around 2.5V with the KOP gyro from recent years (2011-2014)
		gyroBase = avg;
		
		(new Thread(new AnalogGyroThread())).start();
	}
	
	
	 Double getAngle() {
		return angle;
	}
	
	 void setAngle(Double a) {
		angle = a;
	}
	
	
	public int getCount() {
		return counter;
	}
	
	private class GyroLoop implements Runnable {

		@Override
		public void run() {
			try {
				//run once per second
				while(true) {
					counter++;
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
				SmartDashboard.putString("DB/String 9", e.getMessage());
				return;
			}
		}
		
	}
	
}
