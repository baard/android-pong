package no.bouvet.android.pong;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RESTClient implements CourtEventHandler {
    private static final String LOG_CATEGORY = "RESTClient";

    final Court court;
    // 85.19.184.33
    //String baseUrl = "http://javazone.brylex.org/grails-server";
    final String baseUrl;
    String ballUrl;
    String myOwnerId;
    String ballId;
    Thread thread;
    boolean runThread;
    
    public RESTClient(Court court, String url, String ballId, String playerId) {
        this.court = court;
        this.baseUrl = url;
        this.ballId = ballId;
        this.myOwnerId = playerId;
        init();
    }
    
    private void init() {
        ballUrl =  baseUrl + "/superBall/%s.json";
    }


    public void ballLost() {
        // TODO notify server about lost ball
        // cheating..
        court.flipBallY();
    }

    void stopThread() {
        runThread = false;
    }
    
    void startThread() {
        Log.i(LOG_CATEGORY, "starting thread");
        thread = new Thread(runnable);
        runThread = true;
        thread.start();
    }
    
    public void ballToOpponent(final float x, final float dx, final float dy) {
        court.stopBall();
        new Thread(new Runnable() {
            public void run() {
                try {
                    // notify server of ball passed back
                    String urlString = baseUrl + "/superBall";
                    Log.i(LOG_CATEGORY, "PUT " + urlString);
                    HttpPut put = new HttpPut(urlString);
                    put.setHeader("Connection", "close");
                    float dx2 = -dx;
                    JSONObject ball = getBall(ballId);
                    JSONObject nextPlayer = ball.getJSONObject("nextPlayer");
                    String nextPlayerId = nextPlayer.getString("id");
                    String params = "id=" + ballId + "&currentPlayer.id=" + nextPlayerId + "&startsAtY=" + x + "&xVector=" + dx2 + "&yVector="
                            + dy;
                    BasicHttpEntity entity = new BasicHttpEntity();
                    entity.setContent(new ByteArrayInputStream(params.getBytes()));
                    entity.setContentType("application/x-www-form-urlencoded");
                    put.setEntity(entity);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(put);
                    StatusLine line = response.getStatusLine();
                    if (line.getStatusCode() != 200) {
                        throw new Exception("not 200, but was: " + line.getStatusCode());
                    }
                } catch (Exception e) {
                    Log.w(LOG_CATEGORY, "ex: " + e.getMessage());
                }
                startThread();
            }
        }).start();
    }

    // copied from http://snippets.dzone.com/posts/show/555
    public static String slurp (InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
    
    private boolean pollForBall() throws MalformedURLException, IOException, JSONException {
        JSONObject object = getBall(ballId);
        JSONObject owner = object.getJSONObject("currentPlayer");
        String ownerId = owner.getString("id");
        if (ownerId.equals(myOwnerId)) {
            float dy = (float) object.getDouble("xVector");
            float dx = (float) object.getDouble("yVector");
            float x = (float) object.getDouble("startsAtY");
            court.dropBall(x, dy, dx);
            return true;
        }
        return false;
    }

    private JSONObject getBall(String ballId) throws IOException, ClientProtocolException, JSONException {
        String urlString = String.format(ballUrl, ballId);
        Log.i(LOG_CATEGORY, "GET " + urlString);
        HttpGet get = new HttpGet(urlString);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(get);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != 200) {
            throw new RuntimeException("error code != 200, was: " + statusLine.getStatusCode());
        }
        InputStream in = response.getEntity().getContent();
        String data = slurp(in);
        return new JSONObject(data);
    }
    
    Runnable runnable = new Runnable() {
        public void run() {
            while (runThread) {
                try {
                    Thread.sleep(500);
                    if (pollForBall()) {
                        Log.i(LOG_CATEGORY, "got ball, stopping thread!");
                        return;
                    }
                } catch (Exception e) {
                    Log.w(LOG_CATEGORY, e.getMessage());
                    //ignore, try again
                }
            }
            Log.i(LOG_CATEGORY, "thread stop requested, stopping thread!");
        }
    };

    boolean flipFlop = false;
    public void flipPlayer() {
        try {
            // notify server of ball passed back
            String urlString = baseUrl + "/player";
            Log.i(LOG_CATEGORY, "PUT " + urlString);
            HttpPut put = new HttpPut(urlString);
            put.setHeader("Connection", "close");
            String params = "id=" + myOwnerId + "&currentBall.id=" + ballId + "&flipFlop=" + flipFlop;
            flipFlop = !flipFlop;
            BasicHttpEntity entity = new BasicHttpEntity();
            entity.setContent(new ByteArrayInputStream(params.getBytes()));
            entity.setContentType("application/x-www-form-urlencoded");
            put.setEntity(entity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(put);
            StatusLine line = response.getStatusLine();
            if (line.getStatusCode() != 200) {
                throw new Exception("not 200, but was: " + line.getStatusCode());
            }
        } catch (Exception e) {
            Log.w(LOG_CATEGORY, "ex: " + e.getMessage());
        }
    }
}
