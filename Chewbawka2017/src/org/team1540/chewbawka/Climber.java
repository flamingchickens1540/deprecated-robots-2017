package org.team1540.chewbawka;

import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Climber {
	
	public static void setup() throws ExtendedMotorFailureException {
		
//		BooleanCell climbing = new BooleanCell();
		FloatOutput climberMotorOutput = Talons.climberMotor1.simpleControl().combine(Talons.climberMotor2.simpleControl().negate());
		FloatInput climberOutput = Robot.mainTuning.getFloat("Climber Output", 1.0f);
		
		climberMotorOutput.setWhen(0f, Robot.start);
//		climbing.setWhen(false, Robot.start);
		
//		climbing.toggleWhen(ControlBindings.climberButton.onPress());
//		climbing.toFloat(0f, climberOutput).send(climberMotorOutput);
//		climbing.send(Shooter.disableFlywheel); // disable shooter flywheel while climbing
//		
//		Cluck.publish("Climbing", climbing);
		
		ControlBindings.climberControls.multipliedBy(climberOutput).absolute().send(climberMotorOutput);
		
		Cluck.publish("Climber Motor Output", climberMotorOutput);
		Cluck.publish("Climber Input", ControlBindings.climberControls.multipliedBy(climberOutput).absolute());
		
	}
	
}
