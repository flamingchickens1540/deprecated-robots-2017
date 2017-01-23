package org.team1540.chewbawka;

import ccre.channel.*;
import ccre.ctrl.Drive;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class DriveTrain {

	private static final TalonExtendedMotor[] rightCANs = new TalonExtendedMotor[] { FRC.talonCAN(4), FRC.talonCAN(5), FRC.talonCAN(6) };
	private static final TalonExtendedMotor[] leftCANs = new TalonExtendedMotor[] { FRC.talonCAN(1), FRC.talonCAN(2), FRC.talonCAN(3) };

	static FloatInput driveRampingConstant = Robot.mainTuning.getFloat("Drive Ramping Constant", .02f);

	public static void setup() throws ExtendedMotorFailureException {


		FloatOutput rightMotors = rightCANs[0].simpleControl().combine(rightCANs[1].simpleControl()).combine(rightCANs[2].simpleControl());
		FloatOutput leftMotors = leftCANs[0].simpleControl().combine(leftCANs[1].simpleControl()).combine(leftCANs[2].simpleControl()).negate();

		rightMotors.setWhen(0, FRC.startTele);
		leftMotors.setWhen(0, FRC.startTele);

		FloatInput leftDriveControls = Robot.controlBinding.addFloat("Drive Left Axis").deadzone(0.2f);
		FloatInput rightDriveControls = Robot.controlBinding.addFloat("Drive Right Axis").deadzone(0.2f);
		FloatInput extendedForwards = Robot.controlBinding.addFloat("Drive Forwards").deadzone(0.2f);
		FloatInput extendedBackwards = Robot.controlBinding.addFloat("Drive Backwards").deadzone(0.2f);

		FloatInput extended = extendedForwards.minus(extendedBackwards);

		Drive.extendedTank(leftDriveControls, rightDriveControls, extended, leftMotors.addRamping(driveRampingConstant.get(), FRC.constantPeriodic), rightMotors.addRamping(driveRampingConstant.get(), FRC.constantPeriodic));
	}
}