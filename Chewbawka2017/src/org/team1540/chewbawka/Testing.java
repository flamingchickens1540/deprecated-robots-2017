package org.team1540.chewbawka;

import ccre.channel.FloatInput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Testing {
	
	public static void setup() throws ExtendedMotorFailureException {
		
		FloatInput input1 = Robot.controlBinding.addBoolean("Test Button 1").toFloat(0f, 0.8f);
		FloatInput input2 = Robot.controlBinding.addBoolean("Test Button 2").toFloat(0f, 0.8f);
		
		// shooter
//		FloatOutput shooterTestOutput = FRC.talonCAN(9).simpleControl().combine(FRC.talonCAN(10).simpleControl().negate());
		TalonExtendedMotor talon9 = FRC.talonCAN(9);
		TalonExtendedMotor talon10 = FRC.talonCAN(10);
		input1.send(talon9.simpleControl().combine(talon10.simpleControl().negate()));
		talon9.modEncoder().configureEncoderCodesPerRev(125 * 15);
		
//		Cluck.publish("SHOOTER TEST OUTPUT", shooterTestOutput);
		Cluck.publish("_input1", input1);
		Cluck.publish("_position9", talon9.modEncoder().getEncoderPosition());
//		Cluck.publish("_velocity10", talon10.modEncoder().getEncoderVelocity());
		
		// some drive motor
		TalonExtendedMotor talon16 = FRC.talonCAN(16);
		FRC.talonCAN(1).enable();
		input2.send(talon16.simpleControl());
		Cluck.publish("_input2", input2);
		Cluck.publish("_velocity16", talon16.modEncoder().getEncoderVelocity());
		Cluck.publish("_position16", talon16.modEncoder().getEncoderPosition());
		
	}
	
}
