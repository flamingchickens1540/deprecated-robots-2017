package org.team1540.chewbawka;

import ccre.ctrl.ExtendedMotorFailureException;
import ccre.ctrl.binding.ControlBindingCreator;
import ccre.frc.FRC;
import ccre.frc.FRCApplication;
import ccre.tuning.TuningContext;

public class Robot implements FRCApplication {

	public static final ControlBindingCreator controlBinding = FRC.controlBinding();
	public static final TuningContext mainTuning = new TuningContext("MainTuning").publishSavingEvent();

	public static final int TEAM_NUMBER = 1540;

	@Override
	public void setupRobot() throws ExtendedMotorFailureException {
		Climber.setup();
		DriveTrain.setup();
		//GearSlider.setup();
		Hopper.setup();
		Intake.setup();
		Shooter.setup();

		Autonomous.setup();
	}
}
