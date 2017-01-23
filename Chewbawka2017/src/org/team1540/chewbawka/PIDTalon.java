package org.team1540.chewbawka;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.DerivedBooleanInput;
import ccre.channel.FloatCell;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotor.OutputControlMode;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class PIDTalon {
	private final TalonExtendedMotor tem;
	public final FloatInput velocity, speed;
	public final BooleanInput isStopped, isUpToSpeed;
	private final FloatInput targetSpeed;

	public PIDTalon(TalonExtendedMotor tem, String name, FloatInput targetSpeed) {
		this.tem = tem;
		this.targetSpeed = targetSpeed;
		Cluck.publish(name + " Target", targetSpeed);

		velocity = tem.modEncoder().getEncoderVelocity();

		Cluck.publish(name + " Velocity", velocity);
		speed = velocity.absolute();

		isStopped = this.speed.atMost(Robot.mainTuning.getFloat(name + " Maximum Stop Speed", 0.1f));
		Cluck.publish(name + " Is Stopped", isStopped);

		FloatInput allowedVariance = Robot.mainTuning.getFloat(name + " Allowed Variance", 500f);
		isUpToSpeed = velocity.atLeast(targetSpeed).and(velocity.atMost(targetSpeed.minus(allowedVariance.absolute())));
		Cluck.publish(name + " At Speed", isUpToSpeed);


		Cluck.publish(name + " PID P", tem.modPID().getP());
		Cluck.publish(name + " PID I", tem.modPID().getI());
		Cluck.publish(name + " PID D", tem.modPID().getD());
		Cluck.publish(name + " speed", speed);
	}

	public void setup() throws ExtendedMotorFailureException {
		FloatInput control = this.targetSpeed;
		FloatOutput speed = tem.asMode(OutputControlMode.SPEED_FIXED);
		tem.enable();
		control.send(speed);
	}
}
