package org.team1540.chewbawka;

import ccre.channel.*;
import ccre.cluck.Cluck;
import ccre.ctrl.*;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Shooter {
	public static final TalonExtendedMotor flywheelLeft = FRC.talonCAN(9);
	public static final TalonExtendedMotor flywheelRight = FRC.talonCAN(7);
	
	public static final TalonExtendedMotor shooterBelt = FRC.talonCAN(1);
	public static final TalonExtendedMotor shooterFrontConveyor = FRC.talonCAN(15);
	public static final TalonExtendedMotor shooterFunnelingRollerLeft = FRC.talonCAN(10);
	public static final TalonExtendedMotor shooterFunnelingRollerRight = FRC.talonCAN(12);



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
		

		// Setup flywheel PID controller
		
		flywheelLeft.modGeneralConfig().configureReversed(true, true);
		flywheelLeft.modGeneralConfig().activateFollowerMode(flywheelRight);
 
		PIDTalon flywheelTalon = new PIDTalon(flywheelRight, "Shooter Flywheel", flywheelTargetVelocity, 5);
		flywheelTalon.setup();
		
		// Setup belt PID controller 
		PIDTalon beltTalon = new PIDTalon(shooterBelt, "Shooter Belt", beltTargetVelocity, 4);
		beltTalon.setup();

		// Set tunable variables
		FloatInput shooterSlowThreshold = Robot.mainTuning.getFloat("Shooter Slowdown Threshold", 2100f);

		// -Start switching logic-
		
		// When the fire button is pressed, and the shooter is passive, then start spinning up the flywheel
		ControlBindings.fireButton.onPress().and(FRC.inTeleopMode()).and(shooterStates.getIsState("passive")).send(shooterStates.getStateSetEvent("spinup"));
		// Once the flywheel PID is up to speed, start the shooter intake
		flywheelTalon.velocity.atLeast(flywheelShootingVelocity).onPress().and(shooterStates.getIsState("spinup")).send(shooterStates.getStateSetEvent("firing"));
		// When the flywheel slows down as it shoots, set the speed to full to compensate
		//flywheelTalon.velocity.atMost(shooterSlowThreshold).onPress().and(shooterStates.getIsState("firing")).send(shooterStates.getStateSetEvent("compensate"));
		// After the flywheel velocity has been compensated, set the mode back to firing
		//flywheelTalon.velocity.atLeast(flywheelShootingVelocity).onPress().and(shooterStates.getIsState("compensate")).send(shooterStates.getStateSetEvent("firing"));
		shooterStates.getIsState("firing").onPress().send(Intake.intake.eventSet(true));
		shooterStates.getIsState("passive").onPress().send(Intake.intake.eventSet(false));
		
		ControlBindings.fireButton.onRelease().send(shooterStates.getStateSetEvent("passive"));
		// Reset the state machine when switching modes
		shooterStates.setStateWhen("passive", FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));

		// Send intake speeds
		FloatInput shooterFrontConveyorConstant = Robot.mainTuning.getFloat("Shooter Front Conveyor Constant", .5f);
		FloatInput shooterFunnelingRollerConstant = Robot.mainTuning.getFloat("Shooter Funneling Roller Constant", 1f);
		
		FloatOutput shooterFunnelingRoller = PowerManager.managePower(3, shooterFunnelingRollerLeft.simpleControl().combine(shooterFunnelingRollerRight.simpleControl().negate()));
		
		intakeSpeed.multipliedBy(shooterFrontConveyorConstant).send(shooterFrontConveyor.simpleControl().addRamping(.02f, FRC.constantPeriodic));
		intakeSpeed.multipliedBy(shooterFunnelingRollerConstant).send(shooterFunnelingRollerLeft.simpleControl().addRamping(.02f, FRC.constantPeriodic));
		intakeSpeed.multipliedBy((float) (Math.pow(Math.sin(System.currentTimeMillis()/1000), 2))).send(shooterFunnelingRollerRight.simpleControl());

		
		// Publish the velocity of the PIDs and the intake speed.
		Cluck.publish("flywheelTargetVelocity", flywheelTargetVelocity);
		Cluck.publish("shooterBeltTargetVelocity", beltTargetVelocity);
		Cluck.publish("intakeSpeed", intakeSpeed);
	}
}
