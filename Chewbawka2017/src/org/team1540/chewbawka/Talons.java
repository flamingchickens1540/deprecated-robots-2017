package org.team1540.chewbawka;

import ccre.channel.FloatOutput;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Talons {
	
	// gears
	public static final TalonExtendedMotor gearSliderMotor = FRC.talonCAN(17);
	public static final FloatOutput servoLeft = FRC.servo(4, 0, 45);
	public static final FloatOutput servoRight = FRC.servo(5, 0, 45);
	// servo assumptions: positive = clockwise, 45 = up
	
	// intake
	public static final TalonExtendedMotor intakeMotor = FRC.talonCAN(6);
	
	// shooter
	
	
	// climber
	public static final TalonExtendedMotor climberMotor1 = FRC.talonCAN(2);
	public static final TalonExtendedMotor climberMotor2 = FRC.talonCAN(5);
	
}
