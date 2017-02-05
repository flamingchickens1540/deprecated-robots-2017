package org.team1540.chewbawka;

import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class TEMotors {
	
	// DriveTrain
	public static final TalonExtendedMotor[] rightCANs = new TalonExtendedMotor[] { FRC.talonCAN(3), FRC.talonCAN(16), FRC.talonCAN(14) };
	public static final TalonExtendedMotor[] leftCANs = new TalonExtendedMotor[] { FRC.talonCAN(2), FRC.talonCAN(6), FRC.talonCAN(8) };
	
	// Shooter
	public static final TalonExtendedMotor flywheelLeft = FRC.talonCAN(9);
	public static final TalonExtendedMotor flywheelRight = FRC.talonCAN(7);
	
	public static final TalonExtendedMotor shooterBelt = FRC.talonCAN(1);
	public static final TalonExtendedMotor shooterFrontConveyor = FRC.talonCAN(15);
	public static final TalonExtendedMotor shooterFunnelingRollerLeft = FRC.talonCAN(10);
	public static final TalonExtendedMotor shooterFunnelingRollerRight = FRC.talonCAN(12);
	
	// Intake
	public static final TalonExtendedMotor floorIntakeTEM = FRC.talonCAN(18);
	
	// Hopper
	
	// GearSlider
	
	// Climber
	public static final TalonExtendedMotor climberTEMleft = FRC.talonCAN(11);
	public static final TalonExtendedMotor climberTEMright = FRC.talonCAN(13);

}
