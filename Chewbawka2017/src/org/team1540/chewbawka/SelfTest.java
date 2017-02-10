package org.team1540.chewbawka;

import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;
import ccre.log.LogLevel;
import ccre.log.Logger;

public class SelfTest {
	
	public static void testAllMotors() throws ExtendedMotorFailureException, InterruptedException {
		
		// climber
		testMotor(Talons.climberMotor1, "Climber Motor 1", 2, false);
		testMotor(Talons.climberMotor2, "Climber Motor 2", 0, false);
		
		// intake
		testMotor(Talons.intakeMotor, "Intake Motor", 6, false);
		
		// shooter
		
		// gear slider
		testMotor(Talons.gearSliderMotor, "Gear Slider Motor", 11, true);
		// servos
		
	}
	
	public static void testMotor(TalonExtendedMotor talon, String name, int PDPchannel, boolean hasEncoder) throws ExtendedMotorFailureException, InterruptedException {
		FloatOutput motor = talon.simpleControl();
		boolean failed = false;
		FloatInput encoderStart = null;
		if (hasEncoder) {
			encoderStart = talon.modEncoder().getEncoderPosition();
		}
		motor.set(1f);
		Thread.sleep(2000);
		if (FRC.channelCurrentPDP(PDPchannel).get() <= 0) {
			failed = true;
		}
		motor.set(0f);
		if (hasEncoder) {
			if (talon.modEncoder().getEncoderPosition().equals(encoderStart)) {
				failed = true;
			}
		}
		if (failed) {
			Logger.log(LogLevel.FINE, name + ": FAILED");
		} else {
			Logger.log(LogLevel.FINE, name + ": SUCCEEDED");
		}
	}
	
	public static void testServo(FloatOutput servo, String name, int PDPchannel, int degrees) {
		boolean failed = false;
		servo.set(degrees);
		if (FRC.channelCurrentPDP(PDPchannel).get() <= 0) {
			failed = true;
		}
		if (failed) {
			Logger.log(LogLevel.FINE, name + ": FAILED");
		} else {
			Logger.log(LogLevel.FINE, name + ": SUCCEEDED");
		}
	}
	
}
