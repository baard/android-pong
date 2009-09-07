package no.bouvet.android.pong;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

public class RESTClient implements CourtEventHandler {
    private static final String LOG_CATEGORY = "RESTClient";

    final Court court;
    final String baseUrl;
    String ballUrl;
    String playerId;
    String nextPlayerId;
    String ballId;
    Thread thread;
    volatile boolean runThread;
    
    public RESTClient(Court court, String url, String ballId, String playerId) {
        this.court = court;
        this.baseUrl = url;
        this.ballId = ballId;
        this.playerId = playerId;
        ballUrl =  baseUrl + "/superBall/%s.json";
    }

    public void ballLost() {
        // TODO notify server about lost ball
        // cheating..
        court.flipBallY();
        sendScore(false);
    }
    
    class HttpParameterCommand implements Runnable {
        final HttpEntityEnclosingRequestBase method;
        final String params;
        
        public HttpParameterCommand(HttpEntityEnclosingRequestBase method, String params) {
            this.method = method;
            this.params = params;
        }
        
        public void run() {
            try {
                executeRequest(method, params);
            } catch (Exception e) {
                Log.w(LOG_CATEGORY, "failed request to: " + method.getURI(), e);
            }
        }
    }

    private void executeRequest(HttpEntityEnclosingRequestBase method, String params) throws Exception {
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(params.getBytes()));
        entity.setContentType("application/x-www-form-urlencoded");
        method.setEntity(entity);
        method.setHeader("Connection", "close");
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.execute(method);
    }

    private void sendScore(boolean wasHit) {
        String params = "player.id=" + playerId + "&hit=" + wasHit + "&ball.id=" +  ballId;
        HttpPost post = new HttpPost(baseUrl + "/score");
        new Thread(new HttpParameterCommand(post, params)).start();
    }

    public void stopThread() {
        runThread = false;
        keepPlayerAlive = false;
    }
    
    public void startThread() {
        Log.i(LOG_CATEGORY, "starting thread");
        thread = new Thread(ballPoller);
        runThread = true;
        thread.start();
        startKeepPlayerAlive();
    }

    boolean keepPlayerAlive;

    private void startKeepPlayerAlive() {
        keepPlayerAlive = true;
        new Thread() {
            public void run() {
                while (keepPlayerAlive) {
                    try {
                        flipPlayer();
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        // try again
                        Log.e(LOG_CATEGORY, "failed to flip player", e);
                    }
                }
            };
        }.start();
    }
    
    public void ballToOpponent(final float x, final float dx, final float dy) {
        court.stopBall();
        updateBall(x, dx, dy);
        sendScore(true);
    }

    private void updateBall(float x, float dx, float dy) {
        // flip x-direction
        dx = -dx;
        final String params = "id=" + ballId + "&currentPlayer.id=" + nextPlayerId 
            + "&startsAtY=" + x + "&xVector=" + dx + "&yVector=" + dy;
        final HttpPut post = new HttpPut(baseUrl + "/superBall");
        new Thread(new Runnable() {
            public void run() {
                new HttpParameterCommand(post, params).run();
                // start listener thread again
                startThread();
            }
        }).start();
    }

    // copied from http://snippets.dzone.com/posts/show/555
    public static String slurp(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
    
    private boolean pollForBall() throws Exception {
        JSONObject object = getJSONBall(ballId);
        String ownerId = object.getJSONObject("currentPlayer").getString("id");
        if (ownerId.equals(playerId)) {
            nextPlayerId = object.getJSONObject("nextPlayer").getString("id");
            float dy = (float) object.getDouble("xVector");
            float dx = (float) object.getDouble("yVector");
            float x = (float) object.getDouble("startsAtY");
            court.dropBall(x, dy, dx);
            return true;
        }
        return false;
    }

    private JSONObject getJSONBall(String ballId) throws Exception {
        String urlString = baseUrl + "/superBall/" + ballId + ".json";
        HttpGet get = new HttpGet(urlString);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(get);
        StatusLine line = response.getStatusLine();
        if (line.getStatusCode() != 200) {
            throw new Exception("Expected status code 200, but was: " + line.getStatusCode());
        }
        InputStream in = response.getEntity().getContent();
        String data = slurp(in);
        return new JSONObject(data);
    }
    
    Runnable ballPoller = new Runnable() {
        public void run() {
            while (runThread) {
                try {
                    Thread.sleep(500);
                    if (pollForBall()) {
                        Log.i(LOG_CATEGORY, "got ball, stopping thread!");
                        return;
                    }
                } catch (Exception e) {
                    Log.w(LOG_CATEGORY, e.getMessage(), e);
                    //ignore error, we try again
                }
            }
            Log.i(LOG_CATEGORY, "thread stop requested, stopping thread!");
        }
    };

    boolean flipFlop = false;
    
    public void flipPlayer() throws Exception {
        flipFlop = !flipFlop;
        String params = "id=" + playerId + "&currentBall.id=" + ballId + "&flipFlop=" + flipFlop;
        HttpPut put = new HttpPut(baseUrl + "/player");
        executeRequest(put, params);
    }
}
