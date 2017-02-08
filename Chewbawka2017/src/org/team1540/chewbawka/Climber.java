package org.team1540.chewbawka;

import ccre.channel.BooleanCell;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Climber {
	
	public static final TalonExtendedMotor climberMotor1 = FRC.talonCAN(2);
	public static final TalonExtendedMotor climberMotor2 = FRC.talonCAN(5);
	
	public static void setup() throws ExtendedMotorFailureException {
		
		BooleanCell climbing = new BooleanCell();
		FloatOutput climberMotorOutput = climberMotor1.simpleControl().combine(climberMotor2.simpleControl().negate());
		FloatInput climberOutput = Robot.mainTuning.getFloat("Climber Output", 0.8f);
		
		climberMotorOutput.setWhen(0f, Robot.start);
		climbing.setWhen(false, Robot.start);
		
		climbing.toggleWhen(ControlBindings.climberButton.onPress());
		climbing.toFloat(0f, climberOutput).send(climberMotorOutput);
		climbing.send(Shooter.disableFlywheel); // disable shooter flywheel while climbing
		
		Cluck.publish("Climbing", climbing);
		Cluck.publish("Climber Motor Output", climberMotorOutput);
		
	}
	
}
