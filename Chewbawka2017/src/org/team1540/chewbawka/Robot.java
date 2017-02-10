package org.team1540.chewbawka;

import ccre.channel.EventInput;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.ctrl.binding.ControlBindingCreator;
import ccre.frc.FRC;
import ccre.frc.FRCApplication;
import ccre.tuning.TuningContext;

/**
 * This is the core class of a CCRE project. The CCRE launching system will make
 * sure that this class is loaded, and will have set up everything else before
 * loading it. If you change the name, use Eclipse's rename functionality. If
 * you don't, you will have to change the name in Deployment.java.
 *
 * Make sure to set {@link #TEAM_NUMBER} to your team number.
 */
public class Robot implements FRCApplication {
	
	public static final ControlBindingCreator controlBinding = FRC.controlBinding();
	public static final TuningContext mainTuning = new TuningContext("MainTuning").publishSavingEvent();
	public static final EventInput start = FRC.startTele.or(FRC.startAuto).or(FRC.startTest).or(FRC.startDisabled);
	
    /**
     * This is where you specify your team number. It is used to find your
     * roboRIO when you download code.
     * @throws ExtendedMotorFailureException 
     */ 
	public static final int TEAM_NUMBER = 1540;
    
    @Override
    public void setupRobot() throws ExtendedMotorFailureException {
//        Climber.setup();
//        DriveTrain.setup();
//        GearSlider.setup();
        Intake.setup();
        Shooter.setup();
//        Testing.setup();
//        Autonomous.setup();
//    	FRC.talonCAN(1).enable();
//    	FRC.talonCAN(2).enable();
//    	FRC.talonCAN(3).enable();
//    	FRC.talonCAN(4).enable();
//    	FRC.talonCAN(5).enable();
//    	FRC.talonCAN(6).enable();
//    	FRC.talonCAN(7).enable();
//    	FRC.talonCAN(8).enable();
//    	FRC.talonCAN(9).enable();
//    	FRC.talonCAN(10).enable();
//    	FRC.talonCAN(11).enable();
//    	FRC.talonCAN(12).enable();
//    	FRC.talonCAN(13).enable();
//    	FRC.talonCAN(14).enable();
//    	FRC.talonCAN(15).enable();
//    	FRC.talonCAN(16).enable();
        
    }
    
}
