package net.ptidej.wimpapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketClient extends Activity {
    SharedPreferences sharedpreferences;
    private WebSocketClient mWebSocketClient;
    public static final String mypreference = "mypref";
    public static final String incomingMessage = "incomingMessage";
    public void connect2WIMPt(){
        URI uri;
        String TAG="WimpApp";
        String websocketEndPointUrl;
        try {
            websocketEndPointUrl="ws://192.168.2.39:8181"; // SocketServer's IP and port
            uri = new URI(websocketEndPointUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i(TAG, "WIMP connection is opened");
                mWebSocketClient.send("I am  " + Build.MANUFACTURER + " " + Build.MODEL);
                Log.i(TAG,"I am  " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String message) {
                sharedpreferences = MainActivity.getContext().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(incomingMessage,message);
                editor.commit();
              //  MainActivity.updateTextView();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(TAG, "Closed, code= " + code+", reason="+reason+", remote="+remote);
            }

            @Override
            public void onError(Exception e) {

                Log.i(TAG, "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();

    }

}
