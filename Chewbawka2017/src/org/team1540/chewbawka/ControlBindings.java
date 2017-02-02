package org.team1540.chewbawka;

import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;

public class ControlBindings {
	
	public static final FloatInput gearSliderControls = Robot.controlBinding.addFloat("Gear Slider Axis").deadzone(0.2f);
	public static final BooleanInput gearServoButton = Robot.controlBinding.addBoolean("Gear Servo Button");
	
}
