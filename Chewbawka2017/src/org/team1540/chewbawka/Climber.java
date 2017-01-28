package org.team1540.chewbawka;
import ccre.channel.BooleanCell;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Climber {
	
	private static final TalonExtendedMotor climberTEM = FRC.talonCAN(10);
	
	
	
	// Create a boolean cell that switches the climber
	private static final BooleanCell climb = new BooleanCell(false);
    
	public static void setup() throws ExtendedMotorFailureException {
		
		// Make a FloatOutput that controls the climber speed
		FloatOutput climberMotor = climberTEM.simpleControl().addRamping(.02f, FRC.constantPeriodic);
		
		// Set the speed to zero when enabling
		climb.setWhen(false, FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));
		
		// Setup climber logic
		FloatInput climberSpeed = Robot.mainTuning.getFloat("Climber Speed", 1f);
		
		climb.toFloat(0f, climberSpeed).send(climberMotor);

		ControlBindings.climberButton.onPress(climb.eventToggle());
		
	}
}