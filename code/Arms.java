package robot;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;

public class Arms {
	public static final int MA = 15;    // angle steps of Motor A knee
	public static final int MaxA = 135; // Max angle for Motor A
	public static final int boundA = 90;
	public static final int MD = 15;	// angle steps of Motor D hip
	public static final int MaxD = 135; // Max angle for Motor D
	public static final int boundD =90;
	public static final float INVALID = -10;
	public static float invalidAction;
	public static void act(int a1, int a2){	
		//float 
		invalidAction=0;
		if (a1==0 && a2==0)
			invalidAction -=1;
		// Hip
		switch (a1){
		case -1: // Move backward
			if (Motor.D.getTachoCount()>=boundA+MA)
				Motor.D.rotate(-MD);
			else
				invalidAction = INVALID;
			break;
		case 0: // Do nothing 
			Motor.D.stop();
			break;
		case 1: // Move forward
			if (Motor.D.getTachoCount()<=MaxD-MD)
				Motor.D.rotate(MD);
			else
				invalidAction = INVALID;
			break;
		}
		// Knee
		switch (a2){
		case -1: // Move backward
			if (Motor.A.getTachoCount()>=boundD+MD)
				Motor.A.rotate(-MA);
			else
				invalidAction = INVALID;
			break;
		case 0: // Do nothing 
			Motor.A.stop();
			break;
		case 1: // Move forward
			if (Motor.A.getTachoCount()<=MaxA-MA)
				Motor.A.rotate(MA);
			else
				invalidAction = INVALID;
			break;
			default:
				break;
		}
		LCD.clear();
		LCD.drawString("St #"+Arms.getState()+" MA"+a1+" MB"+a2,1, 0);
		int angle_A = Motor.A.getTachoCount();
		int angle_D = Motor.D.getTachoCount();
		LCD.drawString("A "+angle_A+" St "+(int)(angle_A/Arms.MA)+"  ",1,1);
		LCD.drawString("D "+angle_D+" St "+(int)(angle_D/Arms.MD)+"  ",1,2);
		//LCD.drawString("Reward "+(int)getReward(detector), 1, 4);
		LCD.refresh();
		//return invalidAction;
	}
	public static int getState(){
		// Tilecoder style 
		// Hip * number of states + knee
		int MAState=(int)(Motor.A.getTachoCount()-boundA)/MA;
		int MDState=(int)(Motor.D.getTachoCount()-boundD)/MD;
//		int Asteps = (int) MaxA/MA;
//		LCD.clear();
//		LCD.drawString("MA St"+MAState, 1, 1);
//		LCD.drawString("MD St"+MDState, 1, 2);
//		int finalSt=MDState*Asteps+MAState;
//		LCD.drawString("Final St"+finalSt,1,3);
//		LCD.drawString("A Step "+Asteps, 1, 4);
//		LCD.refresh();
		return MDState*((MaxA-boundA)/MA)+MAState;
	}
	
	public static int getActionIndex(int a1, int a2){
		return 3*(a1+1) + a2 + 1;
	}
}
