package org.team1540.chewbawka;

import ccre.channel.BooleanInput;
import ccre.channel.FloatCell;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotor.OutputControlMode;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class Testing {
	
	public static void setup() throws ExtendedMotorFailureException {
		
		// GEAR SERVO TEST
		
		
		// SHOOTER PID TEST
//		BooleanInput input1 = Robot.controlBinding.addBoolean("Test Button 1");
//		TalonExtendedMotor talon = FRC.talonCAN(9);
//		
//		talon.modEncoder().configureEncoderCodesPerRev(125 * 15);
//		talon.modGeneralConfig().configureMaximumOutputVoltage(12f, -12f);
//		talon.modPID().getP().set(1.0f);
//		talon.modPID().getI().set(1.0f);
//		talon.modPID().getD().set(1.0f);
//		FloatOutput motorSpeedControl = talon.asMode(OutputControlMode.SPEED_FIXED);
//		FloatCell targetSpeed = new FloatCell(3600);
//		input1.toFloat(0f, targetSpeed).send(motorSpeedControl);
//		
//		Cluck.publish("_Flywheel Target Speed", targetSpeed);
//		Cluck.publish("_P", talon.modPID().getP());
//		Cluck.publish("_I", talon.modPID().getI());
//		Cluck.publish("_D", talon.modPID().getD());
		
//		FloatInput input2 = Robot.controlBinding.addBoolean("Test Button 2").toFloat(0f, 0.8f);
		
		// SHOOTER SIMPLECONTROL TEST
//		FloatOutput shooterTestOutput = FRC.talonCAN(9).simpleControl().combine(FRC.talonCAN(10).simpleControl().negate());
//		TalonExtendedMotor talon9 = FRC.talonCAN(9);
//		TalonExtendedMotor talon10 = FRC.talonCAN(10);
//		input1.send(talon9.simpleControl().combine(talon10.simpleControl().negate()));
//		talon9.modEncoder().configureEncoderCodesPerRev(125 * 15);
//		
////		Cluck.publish("SHOOTER TEST OUTPUT", shooterTestOutput);
//		Cluck.publish("_input1", input1);
//		Cluck.publish("_position9", talon9.modEncoder().getEncoderPosition());
////		Cluck.publish("_velocity10", talon10.modEncoder().getEncoderVelocity());
//		
//		// some drive motor
//		TalonExtendedMotor talon16 = FRC.talonCAN(16);
//		FRC.talonCAN(1).enable();
//		input2.send(talon16.simpleControl());
//		Cluck.publish("_input2", input2);
//		Cluck.publish("_velocity16", talon16.modEncoder().getEncoderVelocity());
//		Cluck.publish("_position16", talon16.modEncoder().getEncoderPosition());
		
	}
	
}
