package net.ptidej.wimpapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends Activity {
    private SharedPreferences sharedpreferences;
    private TextView incomingMessageTextview;
    public static final String mypreference = "mypref";
    public static final String incomingMessage = "incomingMessage";
    private static Context context;
    private Handler handler = new Handler();
    private Runnable runnable;
    private WebSocketClient mWebSocketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        connect2WIMPt();
    }
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
                sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(incomingMessage,message);
                editor.commit();
                updateTextView();
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


    public static Context getContext() {
        return context;
    }
    public void updateTextView(){
        incomingMessageTextview = findViewById(R.id.incomingMessageTextView);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if(sharedpreferences.contains(incomingMessage)){
            String oldMsg=incomingMessageTextview.getText().toString();
            String incomingMsg=sharedpreferences.getString(incomingMessage,"");
            String newMsg=oldMsg+"\n"+incomingMsg;
            incomingMessageTextview.setText(newMsg);
            Log.i("TAG....",newMsg);
        }
        else {
            incomingMessageTextview.setText("No updates from WIMP");
        }
    }
}
