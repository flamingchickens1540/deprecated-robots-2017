package org.team1540.chewbawka;

import ccre.channel.*;
import ccre.cluck.Cluck;
import ccre.ctrl.*;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Shooter {
	private static final TalonExtendedMotor shooterBelt = FRC.talonCAN(7);
	private static final TalonExtendedMotor shooterIntake = FRC.talonCAN(8);
	private static final TalonExtendedMotor hopperAgitator = FRC.talonCAN(9);
	
	private static final BooleanInput fireButton = Robot.controlBinding.addBoolean("Shooter Fire");
	private static final BooleanInput cancelButton = Robot.controlBinding.addBoolean("Shooter eStop");

	private static EventOutput split(BooleanInput cond, EventOutput t, EventOutput f) {
		return () -> {
			if (cond.get()) {
				t.event();
			} else {
				f.event();
			}
		};
	}
	public static void setup() throws ExtendedMotorFailureException {

		// State machine for setting various motor speeds and the target flywheel velocity
		StateMachine shooterStates = new StateMachine(0,
				"passive", // do nothing
				"spinup",  // when the flywheel is spinning up to target speed
				"firing", // when the ball is firing
				"compensate"); // set the motors to full

		// Set the target velocity of the flywheel
		FloatInput flywheelShootingVelocity = Robot.mainTuning.getFloat("Shooter Flywheel Target Shooting Velocity", 2400.0f);
		FloatInput flywheelCompensateVelocity = Robot.mainTuning.getFloat("Shooter Flywheel Target Compensation Velocity", 10000.0f);

		FloatInput flywheelTargetVelocity = shooterStates.selectByState(
				FloatInput.zero, // passive
				flywheelShootingVelocity, // spinup
				flywheelShootingVelocity, // firing
				flywheelCompensateVelocity); // compensate

		// Set the speed of the intake
		FloatInput intakeShootingSpeed = Robot.mainTuning.getFloat("Shooter Intake Speed", 1f);

		FloatInput intakeSpeed = shooterStates.selectByState(
				FloatInput.zero, // passive
				FloatInput.zero, // spinup
				intakeShootingSpeed, // firing
				FloatInput.zero); // compensate

		// Control bindings
		
		BooleanCell cancel = new BooleanCell(false);
		fireButton.onRelease().send(cancel.eventSet(true));

		// Setup flywheel PID controller
		TalonExtendedMotor flywheelRight = FRC.talonCAN(10);
		TalonExtendedMotor flywheelLeft = FRC.talonCAN(11);
		flywheelLeft.modGeneralConfig().configureReversed(false, false);
		flywheelLeft.modGeneralConfig().activateFollowerMode(flywheelRight);

		PIDTalon flywheelTalon = new PIDTalon(flywheelRight, "Shooter Flywheel", flywheelTargetVelocity);
		flywheelTalon.setup();

		// Set tunable variables
		FloatInput shooterSlowThreshold = Robot.mainTuning.getFloat("Shooter Slowdown Threshold", 2100f);

		// Start switching logic
		fireButton.onPress().and(shooterStates.getIsState("passive")).send(shooterStates.getStateSetEvent("spinup"));
		flywheelTalon.isUpToSpeed.onPress().and(shooterStates.getIsState("spinup")).send(shooterStates.getStateSetEvent("firing"));
		flywheelTalon.velocity.atMost(shooterSlowThreshold).onPress().and(shooterStates.getIsState("firing")).send(shooterStates.getStateSetEvent("compensate"));
		flywheelTalon.velocity.atLeast(flywheelShootingVelocity).onPress().and(shooterStates.getIsState("compensate")).send(shooterStates.getStateSetEvent("firing"));
		shooterStates.onEnterState("spinup").and(cancel).send(cancel.eventSet(false));
		shooterStates.onEnterState("compensate").and(cancel).send(cancel.eventSet(false));
		cancel.onRelease().send(shooterStates.getStateSetEvent("passive"));
		cancelButton.onPress(shooterStates.getStateSetEvent("passive"));

		// Reset the state machine when switching modes
		shooterStates.setStateWhen("passive", FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));

		// Send intake speeds
		FloatInput shooterBeltConstant = Robot.mainTuning.getFloat("Shooter Belt Constant", .5f);
		FloatInput shooterIntakeConstant = Robot.mainTuning.getFloat("Shooter Intake Constant", .5f);
		FloatInput hopperAgitatorConstant = Robot.mainTuning.getFloat("Shooter Agitator Constant", 1f);
		
		intakeSpeed.withRamping(.02f, FRC.constantPeriodic).multipliedBy(shooterBeltConstant).send(shooterBelt.simpleControl());
		intakeSpeed.withRamping(.02f, FRC.constantPeriodic).multipliedBy(shooterIntakeConstant).send(shooterIntake.simpleControl());
		intakeSpeed.withRamping(.02f, FRC.constantPeriodic).multipliedBy(hopperAgitatorConstant).send(hopperAgitator.simpleControl());

		// Publish
		Cluck.publish("flywheelTargetVelocity", flywheelTargetVelocity);
		Cluck.publish("intakeTargetSpeed", intakeSpeed);
	}
}
