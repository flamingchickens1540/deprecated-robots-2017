package org.team1540.chewbawka;

import ccre.channel.BooleanCell;
import ccre.channel.FloatInput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotorFailureException;

public class Intake {
	
	public static BooleanCell runIntakeShooting = new BooleanCell();
	
	public static void setup() throws ExtendedMotorFailureException {
		
		FloatInput intakeOutput = Robot.mainTuning.getFloat("Intake Output", 0.5f);
		BooleanCell runIntake = new BooleanCell();
		runIntake.toggleWhen(ControlBindings.intakeButton.onPress());
		runIntake.setWhen(false, Robot.start);
		
		runIntake.or(runIntakeShooting).toFloat(0f, intakeOutput).send(Talons.intakeMotor.simpleControl());
		
		Cluck.publish("Intake Run By Shooter", runIntakeShooting);
		Cluck.publish("Intake Run", runIntake);
		
	}
	
}
