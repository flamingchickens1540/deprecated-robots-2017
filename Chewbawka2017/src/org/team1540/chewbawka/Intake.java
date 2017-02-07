package org.team1540.chewbawka;

import ccre.channel.BooleanCell;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Intake {
	
	public static final TalonExtendedMotor intakeMotor = FRC.talonCAN(6);
	public static BooleanCell runIntakeShooting = new BooleanCell();
	
	public static void setup() throws ExtendedMotorFailureException {
		
		BooleanCell runIntake = new BooleanCell();
		runIntake.toggleWhen(ControlBindings.intakeButton.onPress());
		
		runIntake.or(runIntakeShooting).toFloat(0f, 1f).send(intakeMotor.simpleControl());
		
	}
	
}
