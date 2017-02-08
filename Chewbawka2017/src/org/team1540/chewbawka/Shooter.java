package org.team1540.chewbawka;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.EventInput;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
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
	
	public static final BooleanCell disableFlywheel = new BooleanCell();
	
	public static void setup() throws ExtendedMotorFailureException {
		
		// flywheel
		flywheelMotorRight.modGeneralConfig().configureReversed(false, false);
		flywheelMotorRight.modGeneralConfig().activateFollowerMode(flywheelMotorLeft);
		flywheelMotorLeft.modEncoder().configureEncoderCodesPerRev(125 * 15);
		flywheelMotorLeft.modGeneralConfig().configureMaximumOutputVoltage(12f, -12f);
		FloatOutput flywheelSpeedControl = flywheelMotorLeft.asMode(OutputControlMode.SPEED_FIXED);
		FloatInput flywheelVelocity = flywheelMotorLeft.modEncoder().getEncoderVelocity();
		FloatInput flywheelTargetSpeed = Robot.mainTuning.getFloat("Shooter Flywheel Target Speed", 3600f);
		BooleanInput flywheelUpToSpeed = flywheelVelocity.atLeast(flywheelTargetSpeed);
		
		StateMachine shooterStates = new StateMachine(0,
				"inactive",
				"spinup",
				"firing");
		
		// at start
		FloatOutput allShooterMotors = flywheelMotorLeft.simpleControl().combine(flywheelMotorRight.simpleControl()).combine(
				beltMotor.simpleControl()).combine(frontConveyerMotor.simpleControl()).combine(
				funnelingRollerMotorLeft.simpleControl()).combine(funnelingRollerMotorRight.simpleControl());
		allShooterMotors.setWhen(0f, Robot.start);
		Intake.runIntakeShooting.setWhen(false, Robot.start);
		shooterStates.setStateWhen("inactive", Robot.start);
		disableFlywheel.setWhen(false, Robot.start);
		
		// switching states
		shooterStates.setStateWhen("spinup", ControlBindings.spinupButton.onPress().andNot(disableFlywheel));
		shooterStates.setStateWhen("firing", ControlBindings.fireButton.onPress().and(flywheelUpToSpeed));
		shooterStates.setStateWhen("inactive", ControlBindings.stopShootingButton.onPress().or(disableFlywheel.onPress()));
		
		// flywheel
		flywheelSpeedControl.setWhen(flywheelTargetSpeed, shooterStates.onEnterState("spinup"));
		flywheelSpeedControl.setWhen(0f, shooterStates.onEnterState("inactive"));
		
		// belt
		FloatInput beltOutput = Robot.mainTuning.getFloat("Shooter Output Belt", 0.5f);
		FloatOutput belt = beltMotor.simpleControl();
		belt.setWhen(beltOutput, shooterStates.onEnterState("firing"));
		belt.setWhen(0f, shooterStates.onEnterState("inactive"));
		
		// front conveyer
		FloatInput frontConveyerOutput = Robot.mainTuning.getFloat("Shooter Output Front Conveyer", 0.5f);
		FloatOutput frontConveyer = beltMotor.simpleControl();
		frontConveyer.setWhen(frontConveyerOutput, shooterStates.onEnterState("firing"));
		frontConveyer.setWhen(0f, shooterStates.onEnterState("inactive"));
		
		// intake
		Intake.runIntakeShooting.setWhen(true, shooterStates.onEnterState("firing"));
		Intake.runIntakeShooting.setWhen(false, shooterStates.onEnterState("inactive"));
		
		// funneling roller left
		FloatInput funnelingRollerLeftOutput = Robot.mainTuning.getFloat("Shooter Output Funneling Roller Left", 0.7f);
		FloatOutput rollerLeft = funnelingRollerMotorLeft.simpleControl().negate();
		rollerLeft.setWhen(funnelingRollerLeftOutput, shooterStates.onEnterState("firing"));
		rollerLeft.setWhen(0f, shooterStates.onEnterState("inactive"));
		
		// funneling roller right
		FloatInput funnelingRollerRightOutput = Robot.mainTuning.getFloat("Shooter Output Funneling Roller Right", 0.4f);
		FloatOutput rollerRight = funnelingRollerMotorLeft.simpleControl();
		rollerRight.setWhen(funnelingRollerRightOutput, shooterStates.onEnterState("firing"));
		rollerRight.setWhen(0f, shooterStates.onEnterState("inactive"));
		
		// publishing
		Cluck.publish("Shooter Flywheel Disabled", disableFlywheel);
		Cluck.publish("Shooter Flywheel Velocity", flywheelVelocity);
		Cluck.publish("Shooter Flywheel Target Velocity", flywheelTargetSpeed);
		Cluck.publish("Shooter Flywheel Encoder Position", flywheelMotorLeft.modEncoder().getEncoderPosition());
		Cluck.publish("Shooter Flywheel Encoder Velocity", flywheelMotorLeft.modEncoder().getEncoderVelocity());
		Cluck.publish("Shooter Flywheel PID P", flywheelMotorLeft.modPID().getP());
		Cluck.publish("Shooter Flywheel PID I", flywheelMotorLeft.modPID().getI());
		Cluck.publish("Shooter Flywheel PID D", flywheelMotorLeft.modPID().getD());
		Cluck.publish("Shooter State is Inactive", shooterStates.getIsState("inactive"));
		Cluck.publish("Shooter State is Spinup", shooterStates.getIsState("spinup"));
		Cluck.publish("Shooter State is Firing", shooterStates.getIsState("firing"));
		
	}
	
}
