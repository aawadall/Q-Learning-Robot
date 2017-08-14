package robot;

import lejos.hardware.device.DLights;
import lejos.hardware.Button;
import lejos.hardware.LED;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.RangeFinderAdapter;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import lejos.utility.Delay;

public class Robot {
	public static float lastDistance;
	static final float MAX_DISTANCE = 100f;
    static final int DETECTOR_DELAY = 100;
    static final float INFINITY = 100;
    static final int MAX_STEPS = 3000;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int StateVolume = (int)(1+Arms.MaxA/Arms.MA)*(1+(Arms.MaxD/Arms.MD));
		int ActionVolume = 9; 
		float alpha = (float) 0.15;
		float gamma = (float) 0.8;
		float epsilon = (float) 0.001;
		float lmbda = 0.9f;
		float rewardSum =0;
		Policy p = new Policy(StateVolume,ActionVolume,alpha,gamma,epsilon,lmbda);
		lastDistance = 100;
		EV3IRSensor ir  = new EV3IRSensor(SensorPort.S1);
		RangeFeatureDetector detector = 
				new RangeFeatureDetector(new RangeFinderAdapter(ir.getDistanceMode()), 
				MAX_DISTANCE, DETECTOR_DELAY);
        detector.enableDetection(true);

        for (int j=0;j<12;j++)
        	Arms.act(1, 1);
        for (int j=1;j<MAX_STEPS;j++){
        	int state=Arms.getState();
        	int[] a=p.getBestAction(state);
        		Arms.act(a[0],a[1]);
        	float R = getReward(detector);
        	rewardSum+=R;
        	LCD.drawString("Reward "+R+"  ", 1, 3);
        	LCD.drawString("Avg "+rewardSum/j+" ", 1, 4);
        	int pending = MAX_STEPS-j;
        	LCD.drawString("Pending "+pending+"  ", 1, 5);
        	
        	LCD.refresh();
        	Delay.msDelay(1);
        	int S_1 = Arms.getState();
        	p.Q_learning(state, S_1, Arms.getActionIndex(a[0], a[1]), R);
		//switch to Q_lambda_learning		
		//p.Q_lambda_learning(state, S_1, Arms.getActionIndex(a[0], a[1]), R,a);
		}
        p.dumpQ();
	}
	
	public static float getReward(RangeFeatureDetector d){
		Feature f;
		f = d.scan();
		//while (f == null)
		//	f = d.scan();
		
		float newDist;
		if (f == null)
			newDist = INFINITY;
		else
			newDist =  f.getRangeReading().getRange();
	
		float reward = newDist - lastDistance;
		lastDistance = newDist;
		if (reward>0)
			Button.LEDPattern(1);
		else
			if (reward==0)
				Button.LEDPattern(3);
			else
				Button.LEDPattern(2);
		reward = (-2.0f*reward)+Arms.invalidAction-(float)0.1;
//		if (reward > 0)
//			reward = reward /2;
//		else
//			reward = reward *2;
		
		return reward;
	}

}
