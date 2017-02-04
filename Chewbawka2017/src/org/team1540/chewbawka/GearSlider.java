package org.team1540.chewbawka;
import ccre.channel.BooleanCell;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class GearSlider {
	
	public static final TalonExtendedMotor gearSliderTEM = FRC.talonCAN(17);
	
	private static final FloatOutput gearServoLeft = FRC.servo(4, 40, 80);
	private static final FloatOutput gearServoRight = FRC.servo(5, 80, 40);
	public static final FloatOutput gearServos = gearServoLeft.combine(gearServoRight);
	
	
	
	// Create a boolean cell that switches the gear servos
	private static final BooleanCell gearLock = new BooleanCell(false);
    
	public static void setup() throws ExtendedMotorFailureException {
		
		// --- This is just for testing purposes, and will be changed to use vision. --- 
		
		// Make a FloatOutput that controls the gear slider speed
		FloatOutput gearSlider = PowerManager.managePower(2, gearSliderTEM.simpleControl().addRamping(.02f, FRC.constantPeriodic));
		
		FloatInput gearSliderSpeed = Robot.mainTuning.getFloat("Gear Slider Constant", 1f);
		
		ControlBindings.gearSliderControls.multipliedBy(gearSliderSpeed).send(gearSlider);
		
		// Close the gear lock when enabling
		gearLock.setWhen(true, FRC.startDisabled.or(FRC.startTele).or(FRC.startAuto).or(FRC.startTest));
		
		// Setup climber logic
		gearLock.onPress(gearServos.eventSet(40));
		gearLock.onRelease(gearServos.eventSet(80));

		ControlBindings.gearLockButton.onPress(gearLock.eventToggle());
		
	}
}