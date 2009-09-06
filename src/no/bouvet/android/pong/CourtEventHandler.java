/**
 * 
 */
package no.bouvet.android.pong;

public interface CourtEventHandler {
    void ballToOpponent(float x, float dx, float dy);
    void ballLost();
}