package org.team1540.chewbawka;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.EventCell;
import ccre.channel.EventOutput;
import ccre.channel.FloatCell;
import ccre.channel.FloatIO;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotor.OutputControlMode;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.ctrl.StateMachine;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;

public class GearSlider {
	
	public static final TalonExtendedMotor gearSliderMotor = FRC.talonCAN(17);
	public static final FloatOutput servoLeft = FRC.servo(4, 0, 120);
	public static final FloatOutput servoRight = FRC.servo(5, -120, 0);
	// servo assumptions: positive = clockwise, 0 = down
	
	public static void setup() throws ExtendedMotorFailureException {
		
		BooleanCell depositingGear = new BooleanCell();
		ControlBindings.gearServoButton.onPress(() -> depositingGear.invert());
		depositingGear.onPress(() -> {servoLeft.set(0); servoRight.set(120);});
		depositingGear.onRelease(() -> {servoLeft.set(120); servoLeft.set(0);});
		
		StateMachine gearSliderStates = new StateMachine(0,
				"not calibrated",
				"calibrating 0 end",
				"calibrating distance",
				"returning to center",
				"calibrated");
		
		gearSliderMotor.modEncoder().configureEncoderCodesPerRev(125 * 15);
		gearSliderMotor.modGeneralConfig().configureMaximumOutputVoltage(12f, -12f);
		FloatCell calibratingSpeed = new FloatCell();
		FloatOutput gearSliderSpeedControl = gearSliderMotor.asMode(OutputControlMode.SPEED_FIXED);
		FloatOutput gearSliderPositionControl = gearSliderMotor.asMode(OutputControlMode.POSITION_FIXED);
		FloatIO gearSliderPosition = gearSliderMotor.modEncoder().getEncoderPosition();
		FloatInput gearSliderVelocity = gearSliderMotor.modEncoder().getEncoderVelocity();
		
		FloatCell calibrationSpeed = new FloatCell(10f);
		FloatCell slow = new FloatCell(2f);
		BooleanInput tooSlow = gearSliderVelocity.absolute().atMost(slow);
		FloatCell slidingDistance = new FloatCell();
		BooleanCell calibrated = new BooleanCell();
		FloatCell positionError = new FloatCell(10f);
		BooleanInput atMiddle = gearSliderPosition.inRange(slidingDistance.dividedBy(2f).minus(positionError), 
				slidingDistance.dividedBy(2f).plus(positionError));
		EventOutput resetEncoder = gearSliderPosition.eventSet(0f);
		EventOutput recordDistance = slidingDistance.eventSet(gearSliderPosition);
		
		EventCell calibrate = new EventCell();
		gearSliderStates.setStateWhen("calibrating 0 end", calibrate);
		gearSliderStates.setStateWhen("calibrating distance", 
				tooSlow.onPress().and(gearSliderStates.getIsState("calibrating 0 end")));
		gearSliderStates.setStateWhen("returning to center", 
				tooSlow.onPress().and(gearSliderStates.getIsState("calibrating distance")));
		gearSliderStates.setStateWhen("calibrated", 
				atMiddle.onPress().and(gearSliderStates.getIsState("returning to center")));
		
		calibrate.on(FRC.startTele);
		gearSliderSpeedControl.setWhen(calibrationSpeed.negated(), 
				gearSliderStates.onEnterState("calibrating 0 end").or(gearSliderStates.onEnterState("returning to center")));
		gearSliderSpeedControl.setWhen(calibrationSpeed, gearSliderStates.onEnterState("calibrating distance"));
		gearSliderSpeedControl.setWhen(0f, gearSliderStates.onEnterState("calibrated"));
		resetEncoder.on(gearSliderStates.onExitState("calibrating 0"));
		recordDistance.on(gearSliderStates.onExitState("calibrating distance"));
		calibrated.setWhen(true, gearSliderStates.onExitState("calibrating distance"));
		
		BooleanInput sliderTooFarF = gearSliderPosition.atLeast(slidingDistance);
		BooleanInput sliderTooFarB = gearSliderPosition.atMost(0f);
		sliderTooFarF.onPress(gearSliderPositionControl.eventSet(slidingDistance));
		sliderTooFarB.onPress(gearSliderPositionControl.eventSet(0f));
		FloatCell slidingControlScaling = new FloatCell(1f);
		ControlBindings.gearSliderControls.onChange().and(calibrated).send(gearSliderPositionControl.eventSet(
				gearSliderPosition.plus(ControlBindings.gearSliderControls.multipliedBy(slidingControlScaling))));
		
		Cluck.publish("Gear Servo Left Output", servoLeft);
		Cluck.publish("Gear Servo Right Output", servoRight);
		Cluck.publish("Gear Depositing", depositingGear);
		Cluck.publish("Gear Slider Calibrate", calibrate);
		Cluck.publish("Gear Slider Calibrated", calibrated);
		Cluck.publish("Gear Slider Calibrating Speed", calibratingSpeed);
		Cluck.publish("Gear Slider Speed Control", gearSliderSpeedControl);
		Cluck.publish("Gear Slider Position Control", gearSliderPositionControl);
		Cluck.publish("Gear Slider Too Slow Threshold", slow);
		Cluck.publish("Gear Slider Position Error", positionError);
		Cluck.publish("Gear Slider Control Scaling", slidingControlScaling);
		
	}
	
}
