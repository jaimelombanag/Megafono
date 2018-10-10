package socket.fasepi.com.megafono;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ServerActivity extends Activity {


    private String TAG = "Megafono";
    private final String ACTION_STRING_ACTIVITY = "ToActivity";
    Server server;
    TextView infoip, msg;



    /*============================================================================================*/
    /*============================================================================================*/
    /*============================================================================================*/
    private final BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.i(TAG, "received message in MainActivity..!");
            String cmd = intent.getStringExtra("CMD");
            String datos = intent.getStringExtra("DATA");

            if(cmd.equalsIgnoreCase("Audio")){
                Log.i(TAG, "Debe llegar esto " +  datos );
                ReproduceAudio(datos);
            }



        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        /*=======================================================================*/
        if (activityReceiver != null) {
            try {
                registerReceiver(activityReceiver, new IntentFilter(ACTION_STRING_ACTIVITY));
            } catch (Exception e) {
            }
        }
        /*=================*/

        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        server = new Server(this);
        infoip.setText(server.getIpAddress()+":"+server.getPort());
    }



    public void ReproduceAudio(String audio){

        audio = audio.replace("\n", "");
        byte[] soundBytes = Base64.decode(audio, Base64.DEFAULT);


        String outputFile= Environment.getExternalStorageDirectory().getAbsolutePath() + "/output.3gp";
        File path = new File(outputFile);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(soundBytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "resumeeeen");
        if (activityReceiver != null) {
            registerReceiver(activityReceiver, new IntentFilter(ACTION_STRING_ACTIVITY));
        }
    }

    @Override
    public void finish() {
        super.finish();
        unregisterReceiver(activityReceiver);
        Intent data = new Intent();
        setResult(Activity.RESULT_CANCELED, data);
    }
}
