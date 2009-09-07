package no.bouvet.android.pong;


public class SolitareCourtHandler implements CourtEventHandler {
    private Court court;
    
    public SolitareCourtHandler(Court court) {
        this.court = court;
    }

    public void ballLost() {
        court.dropRandomNewBall();
        court.resetPaddle();
    }

    public void ballToOpponent(float x, float dx, float dy) {
        court.flipBallY();
    }

    public void startThread() {
        court.dropRandomNewBall();
    }

    public void stopThread() {
        // do nothing
    }
}
