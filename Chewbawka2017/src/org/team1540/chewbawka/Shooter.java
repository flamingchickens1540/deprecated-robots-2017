package org.team1540.chewbawka;

import ccre.channel.*;
import ccre.cluck.Cluck;
import ccre.ctrl.*;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;
import ccre.log.Logger;
import ccre.time.Time;

public class Shooter {
	public static final TalonExtendedMotor flywheelLeft = FRC.talonCAN(9);
	public static final TalonExtendedMotor flywheelRight = FRC.talonCAN(7);
	
	public static final TalonExtendedMotor shooterBelt = FRC.talonCAN(1);
	public static final TalonExtendedMotor shooterFrontConveyor = FRC.talonCAN(15);
	public static final TalonExtendedMotor shooterFunnelingRollerLeft = FRC.talonCAN(10);
	public static final TalonExtendedMotor shooterFunnelingRollerRight = FRC.talonCAN(12);

	public static final FloatCell sinTime = new FloatCell();

	public static void setup() throws ExtendedMotorFailureException {
		long currentTime = System.currentTimeMillis();
		
		FloatInput funnelingReverseAmount = Robot.mainTuning.getFloat("Funneling Reverse Amount", .8f);
		FloatInput funnelingFreq = Robot.mainTuning.getFloat("Funneling Frequency", 1000f);
		
		FRC.constantPeriodic.send(() -> {
			sinTime.set((-1.0f/funnelingReverseAmount.get())*(float) Math.pow(Math.sin(((float) (System.currentTimeMillis() - currentTime)) / funnelingFreq.get()),2)+1.0f);
		});

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


		FloatInput intakeSpeed = shooterStates.selectByState(
				FloatInput.zero, // passive
				FloatInput.zero, // spinup
				FloatInput.always(1f), // firing
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
		shooterStates.onExitState("passive").send(Intake.intake.eventSet(true));
		shooterStates.onEnterState("passive").send(Intake.intake.eventSet(false));
		
		ControlBindings.fireButton.onRelease().send(shooterStates.getStateSetEvent("passive"));
		// Reset the state machine when switching modes
		shooterStates.setStateWhen("passive", FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));

		// Send intake speeds
		FloatInput shooterFrontConveyorConstant = Robot.mainTuning.getFloat("Shooter Front Conveyor Constant", .5f);
		FloatInput shooterFunnelingRollerConstant = Robot.mainTuning.getFloat("Shooter Funneling Roller Constant", 1f);
		
		
		intakeSpeed.multipliedBy(shooterFrontConveyorConstant).send(shooterFrontConveyor.simpleControl().addRamping(.02f, FRC.constantPeriodic));
		intakeSpeed.multipliedBy(shooterFunnelingRollerConstant).send(shooterFunnelingRollerLeft.simpleControl().addRamping(.02f, FRC.constantPeriodic));
		intakeSpeed.multipliedBy(sinTime).multipliedBy(shooterFunnelingRollerConstant).send(shooterFunnelingRollerRight.simpleControl());

		
		// Publish the velocity of the PIDs and the intake speed.
		Cluck.publish("flywheelTargetVelocity", flywheelTargetVelocity);
		Cluck.publish("shooterBeltTargetVelocity", beltTargetVelocity);
		Cluck.publish("intakeSpeed", intakeSpeed);
	}
}
