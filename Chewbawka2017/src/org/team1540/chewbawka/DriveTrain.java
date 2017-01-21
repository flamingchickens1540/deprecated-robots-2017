package org.team1540.chewbawka;

import ccre.channel.*;
import ccre.ctrl.Drive;
import ccre.frc.FRC;

public class DriveTrain {
	
	static FloatOutput leftDriveFront = FRC.talon(0);
	static FloatOutput leftDriveMiddle = FRC.talon(1);
	static FloatOutput leftDriveBack = FRC.talon(2);
	static FloatOutput rightDriveFront = FRC.talon(3);
	static FloatOutput rightDriveMiddle = FRC.talon(4);
	static FloatOutput rightDriveBack = FRC.talon(5);
	
	static FloatInput driveRampingConstant = Robot.mainTuning.getFloat("Drive Ramping Constant", .02f);
	public static FloatOutput leftDrive = leftDriveFront.combine(leftDriveMiddle).combine(leftDriveBack).negate().addRamping(driveRampingConstant.get(), FRC.constantPeriodic);
	public static FloatOutput rightDrive = rightDriveFront.combine(rightDriveMiddle).combine(rightDriveBack).addRamping(driveRampingConstant.get(),FRC.constantPeriodic);
	
	public static void setup() {
		
		leftDrive.setWhen(0, FRC.startTele);
    	rightDrive.setWhen(0, FRC.startTele);
		
		FloatInput leftDriveControls = Robot.controlBinding.addFloat("Drive Left Axis").deadzone(0.2f);
    	FloatInput rightDriveControls = Robot.controlBinding.addFloat("Drive Right Axis").deadzone(0.2f);
    	FloatInput extendedForwards = Robot.controlBinding.addFloat("Drive Forwards").deadzone(0.2f);
    	FloatInput extendedBackwards = Robot.controlBinding.addFloat("Drive Backwards").deadzone(0.2f);
    	
    	FloatInput extended = extendedForwards.minus(extendedBackwards);
    	
    	Drive.extendedTank(leftDriveControls, rightDriveControls, extended, leftDrive, rightDrive);
	}
}