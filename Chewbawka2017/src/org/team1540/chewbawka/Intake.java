package org.team1540.chewbawka;

import ccre.channel.*;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Intake {
	
	private static final TalonExtendedMotor intakeTEM = FRC.talonCAN(12);
	
	private static final BooleanInput intakeButton = Robot.controlBinding.addBoolean("Toggle Intake");
    
	public static void setup() throws ExtendedMotorFailureException {
		
		// Make a FloatOutput that controls the intake speed
		FloatOutput intakeMotor = intakeTEM.simpleControl();
		
		// Create a boolean cell that switches the intake
		BooleanCell intake = new BooleanCell(false);
		
		// Set the speed to zero when enabling
		intake.setWhen(false, FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));
		
		// Setup intake logic
		FloatInput intakeSpeed = Robot.mainTuning.getFloat("Main Intake Speed", 1f);
		
		intake.onPress(intakeMotor.eventSet(intakeSpeed));
		intake.onRelease(intakeMotor.eventSet(0f));
		
		intakeButton.onPress(intake.eventToggle());
		
	}
}
