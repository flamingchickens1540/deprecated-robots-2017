package org.team1540.chewbawka;

import ccre.channel.BooleanInput;
import ccre.channel.EventInput;
import ccre.channel.FloatCell;
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
		
		// at start
		EventInput start = FRC.startTele.or(FRC.startAuto).or(FRC.startTest).or(FRC.startDisabled);
		FloatOutput allShooterMotors = flywheelMotorLeft.simpleControl().combine(flywheelMotorRight.simpleControl()).combine(
				beltMotor.simpleControl()).combine(frontConveyerMotor.simpleControl()).combine(
				funnelingRollerMotorLeft.simpleControl()).combine(funnelingRollerMotorRight.simpleControl());
		allShooterMotors.setWhen(0f, start);
		Intake.runIntakeShooting.setWhen(false, start);
		shooterStates.setStateWhen("inactive", start);
		
		// switching states
		shooterStates.setStateWhen("spinup", ControlBindings.spinupButton.onPress());
		shooterStates.setStateWhen("firing", ControlBindings.fireButton.onPress().and(flywheelUpToSpeed));
		shooterStates.setStateWhen("inactive", ControlBindings.stopShootingButton.onPress());
		
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
		belt.setWhen(frontConveyerOutput, shooterStates.onEnterState("firing"));
		belt.setWhen(0f, shooterStates.onEnterState("inactive"));
		
		// intake
		Intake.runIntakeShooting.setWhen(true, shooterStates.onEnterState("firing"));
		Intake.runIntakeShooting.setWhen(false, shooterStates.onEnterState("inactive"));
		
		// funneling roller left
		FloatInput funnelingRollerLeftOutput = Robot.mainTuning.getFloat("Shooter Output Funneling Roller Left", 0.7f);
		FloatOutput rollerLeft = funnelingRollerMotorLeft.simpleControl();
		rollerLeft.setWhen(funnelingRollerLeftOutput, shooterStates.onEnterState("firing"));
		rollerLeft.setWhen(0f, shooterStates.onEnterState("inactive"));
		
		// funneling roller right
		FloatInput funnelingRollerRightOutput = Robot.mainTuning.getFloat("Shooter Output Funneling Roller Right", 0.4f);
		FloatOutput rollerRight = funnelingRollerMotorLeft.simpleControl();
		rollerRight.setWhen(funnelingRollerRightOutput, shooterStates.onEnterState("firing"));
		rollerRight.setWhen(0f, shooterStates.onEnterState("inactive"));
		
		// publishing
		
		
	}
	
}
