package org.team1540.chewbawka;

import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;

public class ControlBindings {
	
	// gears
	public static final FloatInput gearSliderControls = Robot.controlBinding.addFloat("Gear Slider Axis").deadzone(0.2f);
	public static final BooleanInput gearServoButton = Robot.controlBinding.addBoolean("Gear Servo Button");
	
	// intake
	public static final BooleanInput intakeButton = Robot.controlBinding.addBoolean("Intake Button");
	
	// shooter
//	public static final BooleanInput spinupButton = Robot.controlBinding.addBoolean("Shooter Spinup Button");
//	public static final BooleanInput fireButton = Robot.controlBinding.addBoolean("Shooter Fire Button");
	public static final BooleanInput spinFireButton = Robot.controlBinding.addBoolean("Shooter Spinup/Fire Button");
	public static final BooleanInput stopShootingButton = Robot.controlBinding.addBoolean("Shooter Stop Shooting Button");
	
	// climber
//	public static final BooleanInput climberButton = Robot.controlBinding.addBoolean("Climber Button");
	public static final FloatInput climberControls = Robot.controlBinding.addFloat("Climber Controls");
	
}
