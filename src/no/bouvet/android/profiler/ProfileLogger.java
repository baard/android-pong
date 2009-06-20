package no.bouvet.android.profiler;


import android.os.SystemClock;
import android.util.Log;

public class ProfileLogger {
    private static final String PROFILE_LOGGER = "profiler";
    private static final int DEFAULT_LOG_FREQUENCY = 30000;
    private final ProfileRecorder mRecorder;
    private int mFrequency = DEFAULT_LOG_FREQUENCY;
    private long mLastCalled;
    public ProfileLogger(ProfileRecorder profiler) {
        mRecorder = profiler;
        mLastCalled = SystemClock.uptimeMillis();
    }
    
    public void allowLog() {
        long uptime = SystemClock.uptimeMillis();
        if (uptime - mLastCalled > mFrequency) {
            Log.d(PROFILE_LOGGER, formatProfilerStats("sim", ProfileRecorder.PROFILE_SIM));
            Log.d(PROFILE_LOGGER, formatProfilerStats("frame", ProfileRecorder.PROFILE_FRAME));
            Log.d(PROFILE_LOGGER, formatProfilerStats("draw", ProfileRecorder.PROFILE_DRAW));
            Log.d(PROFILE_LOGGER, formatProfilerStats("page-flip", ProfileRecorder.PROFILE_PAGE_FLIP));
            mLastCalled = uptime;
        }
    }

    private String formatProfilerStats(String profileName, int profileKey) {
        return String.format("%s avg: %s, max: %s, min: %s", profileName, mRecorder.getAverageTime(profileKey), mRecorder
                .getMaxTime(profileKey), mRecorder.getMinTime(profileKey));
    }

    void setFrequency(int frequency) {
        mFrequency = frequency;
    }

}
