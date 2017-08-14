package robot;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

import lejos.hardware.lcd.LCD;

public class Policy {
	public static float[][] Q; 
	public static int[][] A;
	public static float alpha;
	public static float gamma;
	public static float epsilon;
	public static int nStates;
	public static int nActions;
	public static WriteFile wf;
	//traceability
	public static float[][] e;
	public static float lmbda;
//	public static int hipStates;
//	public static int kneeStates;
	Policy(int s, int a,float alf,float gam,float eps, float lm){
		// s is the state space size
		// a is the action space size
		// do we need to initialize?
		alpha = alf;
		gamma = gam;
		epsilon = eps;
	//	hipStates = Arms.MaxA/Arms.MA;
	//	kneeStates = Arms.MaxD/Arms.MD;
		nStates = s;
		nActions = a;
		Q = new float[s][a]; // temp solution make Q bigger 
		A = new int[a][2];
		//trace
		e = new float[s][a];
		lmbda = lm;
		wf = new WriteFile("./Q.txt",true);
		for (int a1=-1;a1<=1;a1++)
			for (int a2=-1;a2<=1;a2++){
			A[3*(a1+1)+a2+1][0]=a1;
			A[3*(a1+1)+a2+1][1]=a2;
		}
		for (int i=0;i<s;i++){
			for(int j=0;j<a;j++){
				Q[i][j] = 0f; //(float) Math.random();
				//initialize eligiability trace
				e[i][j] = 0f;
			}
		}

	}

	public int[] getBestAction(int state){
		return findActions(greedy_behavior_policy(state));
	}
	//Find the index of greedy action 
	public int greedy_behavior_policy(int s){
		int stateIndex = s;
		float[] Q_part = Q[stateIndex];
//		System.out.println("State: "+stateIndex);
//		System.out.print("Q = ");
//		for (int idx=0;idx<Q_part.length;idx++)
//			System.out.print("("+10*(int)Q_part[idx]+")");
//		System.out.println("");
		int maxIndex = findMax(Q_part);
		LCD.drawString("Best"+maxIndex+"="+Q_part[maxIndex], 1, 6);
		LCD.refresh();
		double chance = Math.random();
		Random rand = new Random();
		
		if (chance < epsilon ){
			return rand.nextInt(9);
		}
		else
			return maxIndex;
	}
	//Find the greedy action pair
	public int[] findActions(int actionIndex){
		int[] actions = new int[2];
		actions[0] = A[actionIndex][0];
		actions[1] = A[actionIndex][1];
		return actions;
	}
	
	//Return the index of the maximum value of an Array
	public int findMax(float[] Q_part){
		//float[] array = Q_part;
		int max = 0;
		float maxValue = Q_part[0];
		for (int i = 0; i < Q_part.length; i++) {
		    if (Q_part[i] > maxValue) {
		      max = i;
		    }
		}
		return max;
	}
	public float maxQ(int s){
		float[] Q_part = Q[s];
		int max = findMax(Q_part);
		float maxValue = Q_part[max];
		return maxValue;
	}
	public void Q_learning(int state, int S_1, int action, float reward){
		// update Q matrix
		// Q[s][a]=Q[s][a]+alpha*(R + gamma max Qt - Qt)
		//LCD.drawString("S_1 "+S_1+"  ", 1, 6);
		//LCD.refresh();
		float update = reward + gamma * maxQ(S_1)-Q[state][action];
		Q[state][action] = Q[state][action]+alpha*update;
		//epsilon=(float) (epsilon*0.9);
	}

	//Q_lambda_learning program
	public void Q_lambda_learning(int state, int S_1, int action, float reward, int[] A_1){
				
		
		float update = reward + gamma *maxQ(S_1)-Q[state][action];
		int greedyAction = findMax(Q[S_1]);
		int realAction = (1+A_1[0])*3+(A_1[1]+1);
		e[state][action] = 1.00f;//replacing traces
		Q[state][action] = Q[state][action]+alpha*update*e[state][action];
		

		
		for (int i=0;i<nStates;i++){
		    for(int j=0;j<nActions;j++){
			Q[i][j] = Q[i][j]+alpha*update*e[i][j];
			if(greedyAction == realAction){
			    e[i][j] = gamma*lmbda*e[i][j];
			}
			else
			    e[i][j] = 0;
		    }
		}
	}

	public void dumpQ(){
		for (int i=0;i<nStates;i++){
			for (int j=0;j<nActions;j++)
			{
				try {
					wf.writeToFile(""+Q[i][j]+",");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				wf.writeToFile("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
