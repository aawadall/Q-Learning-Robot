# Q-Learning-Robot
Q Learning EV3 Robot Learning How to Walk

_This was a Course Project for the CMPUT609 - Reinforcement Learning_

## Dependency 
This project utilizes the `lejos` plugin in `eclipse` IDE

# Report 
## Task:

Our task is to train a two degrees of freedom armed robot with a passive wheel base, to learn to moveforward or backward. 
The arm consists of two joints (Hip and Knee) each powered by an interactive servo motor.
The robot is built using a Lego Mindstorms EV3 system. 

## Implementation:
The hardware component of the problem was made of a Lego Mindstorms EV3 kit, with a passive wheelbase, and a 2DOF arm powered by two interactive servo motors. The reward signal utilized an onboard infrared sensor used to measure the distance between the robot and a nearby wall, and using the difference in distance among time steps we calculated the velocity of the robot as a reward signal. 


We used three different types of end effectors on the arm:
* a plastic end effector
* a large rubber wheel
* a small rubber wheel

For the software part, we used `lejos` for EV3, where we wrote three `java` classes, one for the Robot, one for the Policy and one for the Arm. 
Our action space was the same among all states

We tried different state space sizes, based on the unit angle used to rotate each joint and the valid range of angles for each joint.
We used Q Learning

We added punishment terms to the reward signal as follows:
Attempt to move beyond valid range -10
Not moving -0.1
No Action -1

## State Space
Our state space is a two dimensional space made of different angles for each of the joints.
We have experimented over different granularities and ranges of angles, and found that a simple 11 by 11 angles is more than enough with a range from /2to 3/4.
Both dimensions of states are then flattened into a state vector.

## Action Space
Our action space is a two dimensional space as well. Both hip and knee actions includes up hold and down. Both dimensions of actions are then flattened into an action vector as well.

## State/Action Value
A Q matrix is created with rows representing the states and columns representing the actions. Using Q Learning we managed to converge to good result.

## Challenge:
When the robot is taking an action, it shakes which adds noise to the reward signal. One approach we are going to use to solve the problem is to take the mean distance change during one time step. 
The stationary environment is not guaranteed. The end effector traction is not constant over different parts of the carpet or hardwood floor


## Program:
Our program consists of three parts: control part, sample part and learning part.
Control part is used to contact and control the robot. `Robot.java`
Sample part is used to sample state and actions which is generated in the learning part and obtained by the control part. `Arm.java`
Learning part is used to let our robot learn the optimal policy to reach the goal. `Policy.java`

## Result:
A video record is used to show the result.
So far the robot seems to prefer the use of inertia rather than the use of traction of the end effector on the ground in case we use large step angles and a wheel (large or small) 
We also noticed that a large state space is not fully learned before the robot runs out of battery.
Finally, the robot manages to solve the reverse problem (Move backward) faster than the original problem (Move forward). 
 
We found over multiple trials that the Hip state is restricted to two states only where the rest is zeros while the knee states are mainly populated with values. Most of the time the robot exploited in these states and explored other states barely. One possible reason is that the robot can only move by taking actions in those states.
