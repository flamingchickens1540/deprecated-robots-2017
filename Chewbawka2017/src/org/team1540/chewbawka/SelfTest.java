package org.team1540.chewbawka;

import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;
import ccre.log.LogLevel;
import ccre.log.Logger;
import ccre.rconf.RConf.Entry;
import ccre.rconf.RConfable;

public class SelfTest {
	
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
