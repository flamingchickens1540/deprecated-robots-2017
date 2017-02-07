package org.team1540.chewbawka;

import ccre.channel.BooleanCell;
import ccre.channel.FloatCell;
import ccre.channel.FloatInput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Intake {
	
	public static final TalonExtendedMotor intakeMotor = FRC.talonCAN(6);
	public static BooleanCell runIntakeShooting = new BooleanCell();
	
	public static void setup() throws ExtendedMotorFailureException {
		
		FloatInput intakeOutput = Robot.mainTuning.getFloat("Intake Output", 0.5f);
		BooleanCell runIntake = new BooleanCell();
		runIntake.toggleWhen(ControlBindings.intakeButton.onPress());
		
		runIntake.or(runIntakeShooting).toFloat(0f, 1f).send(intakeMotor.simpleControl());
		
		Cluck.publish("Intake Run By Shooter", runIntakeShooting);
		Cluck.publish("Intake Run Manually", runIntake);
		
	}
	
}
