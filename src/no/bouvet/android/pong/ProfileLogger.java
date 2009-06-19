package no.bouvet.android.pong;

import android.os.SystemClock;
import android.util.Log;

public class ProfileLogger {
    private static final String PROFILE_LOGGER = "ProfileLogger";
    private static final int DEFAULT_LOG_FREQUENCY = 3000;
    ProfileRecorder recorder;
    int frequency = DEFAULT_LOG_FREQUENCY;
    long lastCalled;
    public ProfileLogger(ProfileRecorder profiler) {
        this.recorder = profiler;
        lastCalled = SystemClock.uptimeMillis();
    }
    
    public void logIfNeccessary() {
        long now = SystemClock.uptimeMillis();
        if (now - lastCalled > frequency) {
            Log.i(PROFILE_LOGGER, formatStats("sim", ProfileRecorder.PROFILE_SIM));
            Log.i(PROFILE_LOGGER, formatStats("frame", ProfileRecorder.PROFILE_FRAME));
            Log.i(PROFILE_LOGGER, formatStats("draw", ProfileRecorder.PROFILE_DRAW));
            Log.i(PROFILE_LOGGER, formatStats("page-flip", ProfileRecorder.PROFILE_PAGE_FLIP));
            lastCalled = now;
        }
    }

    private String formatStats(String prefix, int profileSim) {
        return String.format("Average %s: %s, max: %s, min: %s", prefix, recorder.getAverageTime(profileSim), recorder
                .getMaxTime(profileSim), recorder.getMinTime(profileSim));
    }

    public void setLogFrequency(int frequency) {
        this.frequency = frequency;
    }

}
