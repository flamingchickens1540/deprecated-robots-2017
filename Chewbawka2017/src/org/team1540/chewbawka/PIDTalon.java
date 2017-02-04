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
	private final int priority;

	public PIDTalon(TalonExtendedMotor tem, String name, FloatInput targetSpeed, int priority) {
		this.tem = tem;
		this.targetSpeed = targetSpeed;
		this.priority = priority;
		Cluck.publish(name + " Target", targetSpeed);

		velocity = tem.modEncoder().getEncoderVelocity();

		Cluck.publish(name + " Velocity", velocity);
		speed = velocity.absolute();

		isStopped = this.speed.atMost(Robot.mainTuning.getFloat(name + " Maximum Stop Speed", 0.1f));
		Cluck.publish(name + " Is Stopped", isStopped);

		FloatInput allowedVariance = Robot.mainTuning.getFloat(name + " Allowed Variance", 500f);
		isUpToSpeed = velocity.atLeast(targetSpeed).and(velocity.atMost(targetSpeed.minus(allowedVariance.absolute())));
		Cluck.publish(name + " Is Up To Speed", isUpToSpeed);


		Cluck.publish(name + " PID P", tem.modPID().getP());
		Cluck.publish(name + " PID I", tem.modPID().getI());
		Cluck.publish(name + " PID D", tem.modPID().getD());
		Cluck.publish(name + " PID F", tem.modPID().getF());
        Cluck.publish(name + " PID I Bounds", tem.modPID().getIntegralBounds());
        Cluck.publish(name + " PID I Accum", tem.modPID().getIAccum());
	}
	
	public static BooleanInput getThreeState(BooleanInput forceTrue, BooleanInput forceFalse) {
        return new DerivedBooleanInput(forceTrue, forceFalse) {
            private boolean state;

            @Override
            protected synchronized boolean apply() {
                if (forceTrue.get()) {
                    state = true;
                }
                if (forceFalse.get()) {
                    state = false;
                }
                return state;
            }
        };
    }


	public void setup() throws ExtendedMotorFailureException {
		FloatInput control = this.targetSpeed;
		FloatOutput speed = tem.asMode(OutputControlMode.SPEED_FIXED);
		speed = PowerManager.managePower(this.priority, speed);
		tem.enable();
		control.send(speed);
	}
}
