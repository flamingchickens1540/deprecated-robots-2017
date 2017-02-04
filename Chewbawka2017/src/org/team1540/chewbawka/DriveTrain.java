package org.team1540.chewbawka;

import ccre.channel.*;
import ccre.ctrl.Drive;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class DriveTrain {

	public static final TalonExtendedMotor[] rightCANs = new TalonExtendedMotor[] { FRC.talonCAN(3), FRC.talonCAN(16), FRC.talonCAN(14) };
	public static final TalonExtendedMotor[] leftCANs = new TalonExtendedMotor[] { FRC.talonCAN(2), FRC.talonCAN(6), FRC.talonCAN(8) };

	static FloatInput driveRampingConstant = Robot.mainTuning.getFloat("Drive Ramping Constant", .02f);

	public static void setup() throws ExtendedMotorFailureException {

		// Combine right and left motors into FloatOutputs using simple control
		FloatOutput rightMotors = PowerManager.managePower(2, rightCANs[0].simpleControl()
				.combine(rightCANs[1].simpleControl())
				.combine(rightCANs[2].simpleControl()));
		
		FloatOutput leftMotors = PowerManager.managePower(2, leftCANs[0].simpleControl()
				.combine(leftCANs[1].simpleControl())
				.combine(leftCANs[2].simpleControl()).negate());

		// Set the speed to zero when when enabling
		rightMotors.setWhen(0, FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));
		leftMotors.setWhen(0, FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));

		// Send the left and right motors and setup extended drive
		FloatInput extended = ControlBindings.extendedForwards.minus(ControlBindings.extendedBackwards);

		Drive.extendedTank(ControlBindings.leftDriveControls, 
				ControlBindings.rightDriveControls, 
				extended, 
				leftMotors.addRamping(driveRampingConstant.get(), FRC.constantPeriodic), 
				rightMotors.addRamping(driveRampingConstant.get(), FRC.constantPeriodic));
	}
}