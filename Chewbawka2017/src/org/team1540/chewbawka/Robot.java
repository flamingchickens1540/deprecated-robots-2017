package org.team1540.chewbawka;

import ccre.channel.FloatCell;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotor.OutputControlMode;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;
import ccre.frc.FRCApplication;

/**
 * This is the core class of a CCRE project. The CCRE launching system will make
 * sure that this class is loaded, and will have set up everything else before
 * loading it. If you change the name, use Eclipse's rename functionality. If
 * you don't, you will have to change the name in Deployment.java.
 *
 * Make sure to set {@link #TEAM_NUMBER} to your team number.
 */
public class Robot implements FRCApplication {

    /**
     * This is where you specify your team number. It is used to find your
     * roboRIO when you download code.
     */
    public static final int TEAM_NUMBER = 1540;

    @Override
    public void setupRobot() throws ExtendedMotorFailureException {
        
    	TalonExtendedMotor motor0 = FRC.talonCAN(0);
    	motor0.modEncoder().configureEncoderCodesPerRev(125 * 15);
    	motor0.modGeneralConfig().configureMaximumOutputVoltage(12.0f, -12.0f);
    	
    	FloatCell speed = new FloatCell();
    	speed.send(motor0.asMode(OutputControlMode.SPEED_FIXED));
    	
    	Cluck.publish("P", motor0.modPID().getP());
    	Cluck.publish("I", motor0.modPID().getI());
    	Cluck.publish("D", motor0.modPID().getD());
    	Cluck.publish("F", motor0.modPID().getF());
    	Cluck.publish("Speed", speed);
    	Cluck.publish("Actual Speed", motor0.modEncoder().getEncoderVelocity());
    	
    }
}
