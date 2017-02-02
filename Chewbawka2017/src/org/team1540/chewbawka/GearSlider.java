package org.team1540.chewbawka;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.EventCell;
import ccre.channel.EventOutput;
import ccre.channel.FloatCell;
import ccre.channel.FloatIO;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotor.OutputControlMode;
import ccre.ctrl.ExtendedMotorFailureException;
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
		
		gearSliderMotor.modEncoder().configureEncoderCodesPerRev(125 * 15);
		gearSliderMotor.modGeneralConfig().configureMaximumOutputVoltage(12f, -12f);
		FloatCell calibratingSpeed = new FloatCell();
		FloatOutput gearSliderSpeedControl = gearSliderMotor.asMode(OutputControlMode.SPEED_FIXED);
		FloatOutput gearSliderPositionControl = gearSliderMotor.asMode(OutputControlMode.POSITION_FIXED);
		FloatIO gearSliderPosition = gearSliderMotor.modEncoder().getEncoderPosition();
		
		FloatCell slow = new FloatCell(2f);
		BooleanInput tooSlow = gearSliderMotor.modEncoder().getEncoderVelocity().absolute().atMost(slow);
		BooleanCell backward1 = new BooleanCell();
		BooleanCell forward = new BooleanCell();
		BooleanCell backward2 = new BooleanCell();
		FloatCell slidingDistance = new FloatCell();
		BooleanCell calibrated = new BooleanCell();
		
		EventCell calibrate = new EventCell();
		EventOutput resetEncoder = gearSliderMotor.modEncoder().getEncoderPosition().eventSet(0f);
		EventOutput sliderBackward = gearSliderSpeedControl.eventSet(calibratingSpeed.negated()).combine(
				() -> {backward1.set(true); forward.set(false);});
		EventOutput sliderForward = gearSliderSpeedControl.eventSet(calibratingSpeed).combine(
				() -> {forward.set(true); backward1.set(false);});
		
		EventOutput recordDistance = slidingDistance.eventSet(gearSliderPosition);
		EventOutput sliderStop = gearSliderSpeedControl.eventSet(0f);
		
		calibrate.send(sliderBackward);
		tooSlow.onPress().and(backward1).send(resetEncoder.combine(sliderForward));
		tooSlow.onPress().and(forward).send(recordDistance);
		/* 
		 * backward
		 * until going too slowly
		 * then reset encoder, go forward
		 * until going too slowly
		 * then record encoder position (= sliding distance), go backward
		 * until halfway
		 * then stop
		 */
		
		Cluck.publish("Gear Servo Left Output", servoLeft);
		Cluck.publish("Gear Servo Right Output", servoRight);
		Cluck.publish("Gear Depositing", depositingGear);
		Cluck.publish("Gear Slider Calibrate", calibrate);
		Cluck.publish("Gear Slider Calibrating Speed", calibratingSpeed);
		Cluck.publish("Gear Slider Speed Control", gearSliderSpeedControl);
		Cluck.publish("Gear Slider Position Control", gearSliderPositionControl);
		Cluck.publish("Gear Slider Too Slow Speed", slow);
		
	}
	
}
