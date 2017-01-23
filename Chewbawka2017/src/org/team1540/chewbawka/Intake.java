package org.team1540.chewbawka;

import ccre.channel.*;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Intake {
	
	private static final TalonExtendedMotor intakeTEM = FRC.talonCAN(12);
	
	private static final BooleanInput intakeButton = Robot.controlBinding.addBoolean("Toggle Intake");
	
	// Create a boolean cell that switches the intake
	private static final BooleanCell intake = new BooleanCell(false);
    
	public static void setup() throws ExtendedMotorFailureException {
		
		// Make a FloatOutput that controls the intake speed
		FloatOutput intakeMotor = intakeTEM.simpleControl();
		
		// Set the speed to zero when enabling
		intake.setWhen(false, FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));
		
		// Setup intake logic
		FloatInput intakeSpeed = Robot.mainTuning.getFloat("Main Intake Speed", 1f);
		
		intake.toFloat(0f, intakeSpeed).send(intakeMotor);

		intakeButton.onPress(intake.eventToggle());
		
	}
}
