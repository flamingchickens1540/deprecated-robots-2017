package org.team1540.chewbawka;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.ctrl.ExtendedMotor.OutputControlMode;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.ctrl.StateMachine;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Shooter {
	
	public static final TalonExtendedMotor flywheelMotorLeft = FRC.talonCAN(9); // has encoder
	public static final TalonExtendedMotor flywheelMotorRight = FRC.talonCAN(10);
	public static final TalonExtendedMotor beltMotor = FRC.talonCAN(12);
	public static final TalonExtendedMotor frontConveyerMotor = FRC.talonCAN(3);
	public static final TalonExtendedMotor funnelingRollerMotorLeft = FRC.talonCAN(7);
	public static final TalonExtendedMotor funnelingRollerMotorRight = FRC.talonCAN(1);
	
	public static void setup() throws ExtendedMotorFailureException {
		
		// set all motors to 0
		FloatOutput allShooterMotors = flywheelMotorLeft.simpleControl().combine(flywheelMotorRight.simpleControl()).combine(
				beltMotor.simpleControl()).combine(frontConveyerMotor.simpleControl()).combine(
				funnelingRollerMotorLeft.simpleControl()).combine(funnelingRollerMotorRight.simpleControl());
		allShooterMotors.setWhen(0f, FRC.startTele.or(FRC.startAuto).or(FRC.startTest).or(FRC.startDisabled));
		
		// flywheel
		flywheelMotorRight.modGeneralConfig().configureReversed(false, false);
		flywheelMotorRight.modGeneralConfig().activateFollowerMode(flywheelMotorLeft);
		flywheelMotorLeft.modEncoder().configureEncoderCodesPerRev(125 * 15);
		flywheelMotorLeft.modGeneralConfig().configureMaximumOutputVoltage(12f, -12f);
		FloatOutput flywheelSpeedControl = flywheelMotorLeft.asMode(OutputControlMode.SPEED_FIXED);
		FloatInput flywheelVelocity = flywheelMotorLeft.modEncoder().getEncoderVelocity();
		
		FloatInput flywheelTargetSpeed = Robot.mainTuning.getFloat("Shooter Flywheel Target Speed", 2400f);
		BooleanInput flywheelUpToSpeed = flywheelVelocity.atLeast(flywheelTargetSpeed);
		
		StateMachine shooterStates = new StateMachine(0,
				"inactive",
				"spinup",
				"firing");
		
		// switching states
		shooterStates.setStateWhen("spinup", ControlBindings.spinupButton.onPress());
		shooterStates.setStateWhen("firing", ControlBindings.fireButton.onPress().and(flywheelUpToSpeed));
		shooterStates.setStateWhen("inactive", ControlBindings.stopShootingButton.onPress());
		
		// flywheel
		flywheelSpeedControl.setWhen(flywheelTargetSpeed, shooterStates.onEnterState("spinup"));
		flywheelSpeedControl.setWhen(0f, shooterStates.onExitState("firing"));
		
		// hopper motors
		FloatOutput hopperMotors = beltMotor.simpleControl().combine(frontConveyerMotor.simpleControl());
		hopperMotors.setWhen(1f, shooterStates.onEnterState("firing"));
		hopperMotors.setWhen(0f, shooterStates.onExitState("firing"));
		Intake.runIntakeShooting.setWhen(true, shooterStates.onEnterState("firing"));
		Intake.runIntakeShooting.setWhen(false, shooterStates.onExitState("firing"));
		
	}
	
}
