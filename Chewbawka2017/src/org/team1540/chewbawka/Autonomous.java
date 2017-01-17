package org.team1540.chewbawka;

import ccre.channel.FloatInput;
import ccre.instinct.InstinctMultiModule;
import ccre.tuning.TuningContext;

public class Autonomous {
    public static final TuningContext autoTuning = new TuningContext("AutonomousTuning").publishSavingEvent();
    public static final InstinctMultiModule mainModule = new InstinctMultiModule(autoTuning);
    
    public static void setup() {
    	
    }
}
