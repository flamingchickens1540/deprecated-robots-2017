package org.team1540.chewbawka;

import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;
import ccre.ctrl.ExtendedMotorFailureException;

public class ControlBindings {

	// DriveTrain
	public static final FloatInput leftDriveControls = Robot.controlBinding.addFloat("Drive Left Axis").deadzone(0.2f);
	public static final FloatInput rightDriveControls = Robot.controlBinding.addFloat("Drive Right Axis").deadzone(0.2f);
	public static final FloatInput extendedForwards = Robot.controlBinding.addFloat("Drive Forwards").deadzone(0.2f);
	public static final FloatInput extendedBackwards = Robot.controlBinding.addFloat("Drive Backwards").deadzone(0.2f);
	
	// Shooter
	public static final BooleanInput fireButton = Robot.controlBinding.addBoolean("Shooter Fire");
	
	// Intake
	public static final BooleanInput floorIntakeButton = Robot.controlBinding.addBoolean("Toggle Floor Intake");
	
	// Hopper
	
	// GearSlider
	public static final FloatInput gearSliderControls = Robot.controlBinding.addFloat("Gear Slider Axis").deadzone(0.2f);
	public static final BooleanInput gearLockButton = Robot.controlBinding.addBoolean("Toggle Gear Lock");
	
	// Climber
	public static final BooleanInput climberButton = Robot.controlBinding.addBoolean("Toggle Climber");

}
