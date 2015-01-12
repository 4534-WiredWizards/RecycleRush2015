package org.usfirst.frc.team4534.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogGyro {
	AnalogInput gyro;
	ArrayList<Double> values;
	Double base;
	
	static int SAMPLE_SIZE = 100;
	
	public AnalogGyro(int channel) {
		gyro = new AnalogInput(channel);
	}
	
	public AnalogGyro(AnalogInput ai) {
		gyro = ai;
	}
	
	void initGyro() {
		while(values.size() <= SAMPLE_SIZE) {
			values.add(gyro.getVoltage());
		}
		
		Double avg = 0.0;
		for(Double val : values) {
			avg += val;
		}
		avg = avg.doubleValue() / values.size();
	}
	
}
