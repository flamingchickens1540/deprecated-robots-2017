package org.team1540.chewbawka;

import ccre.channel.*;
import ccre.cluck.Cluck;
import ccre.ctrl.*;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Shooter {
	private static final TalonExtendedMotor flywheelLeft = FRC.talonCAN(10);
	private static final TalonExtendedMotor flywheelRight = FRC.talonCAN(11);
	
	private static final TalonExtendedMotor shooterBelt = FRC.talonCAN(13);
	private static final TalonExtendedMotor shooterFrontConveyor = FRC.talonCAN(14);
	private static final TalonExtendedMotor shooterFunnelingRollerLeft = FRC.talonCAN(15);
	private static final TalonExtendedMotor shooterFunnelingRollerRight = FRC.talonCAN(16);

	private static final BooleanInput fireButton = Robot.controlBinding.addBoolean("Shooter Fire");

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

		// Set the target velocity of the belt
		FloatInput beltTargetShootingVelocity = Robot.mainTuning.getFloat("Shooter Belt Target Velocity", 1000f);

		FloatInput beltTargetVelocity = shooterStates.selectByState(
				FloatInput.zero, // passive
				FloatInput.zero, // spinup
				beltTargetShootingVelocity, // firing
				FloatInput.zero); // compensate

		// Control bindings
		fireButton.onRelease().send(shooterStates.getStateSetEvent("passive"));

		// Setup flywheel PID controller
		
		flywheelLeft.modGeneralConfig().configureReversed(true, true);
		flywheelLeft.modGeneralConfig().activateFollowerMode(flywheelRight);

		PIDTalon flywheelTalon = new PIDTalon(flywheelRight, "Shooter Flywheel", flywheelTargetVelocity);
		flywheelTalon.setup();
		
		// Setup belt PID controller 
		PIDTalon beltTalon = new PIDTalon(shooterBelt, "Shooter Belt", beltTargetVelocity);
		beltTalon.setup();

		// Set tunable variables
		FloatInput shooterSlowThreshold = Robot.mainTuning.getFloat("Shooter Slowdown Threshold", 2100f);

		// Start switching logic
		fireButton.onPress().and(shooterStates.getIsState("passive")).send(shooterStates.getStateSetEvent("spinup"));
		flywheelTalon.isUpToSpeed.onPress().and(shooterStates.getIsState("spinup")).send(shooterStates.getStateSetEvent("firing"));
		flywheelTalon.velocity.atMost(shooterSlowThreshold).onPress().and(shooterStates.getIsState("firing")).send(shooterStates.getStateSetEvent("compensate"));
		flywheelTalon.velocity.atLeast(flywheelShootingVelocity).onPress().and(shooterStates.getIsState("compensate")).send(shooterStates.getStateSetEvent("firing"));

		// Reset the state machine when switching modes
		shooterStates.setStateWhen("passive", FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));

		// Send intake speeds
		FloatInput shooterFrontConveyorConstant = Robot.mainTuning.getFloat("Shooter Front Conveyor Constant", .5f);
		FloatInput shooterFunnelingRollerConstant = Robot.mainTuning.getFloat("Shooter Funneling Roller Constant", 1f);
		
		FloatOutput shooterFunnelingRoller = shooterFunnelingRollerLeft.simpleControl().combine(shooterFunnelingRollerRight.simpleControl().negate());
		
		intakeSpeed.multipliedBy(shooterFrontConveyorConstant).send(shooterFrontConveyor.simpleControl().addRamping(.02f, FRC.constantPeriodic));
		intakeSpeed.multipliedBy(shooterFunnelingRollerConstant).send(shooterFunnelingRoller.addRamping(.02f, FRC.constantPeriodic));

		// Publish
		Cluck.publish("flywheelTargetVelocity", flywheelTargetVelocity);
		Cluck.publish("shooterBeltTargetVelocity", beltTargetVelocity);
		Cluck.publish("intakeSpeed", intakeSpeed);
	}
}
