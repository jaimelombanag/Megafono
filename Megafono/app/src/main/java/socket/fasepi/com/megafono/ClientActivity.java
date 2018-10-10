package socket.fasepi.com.megafono;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import at.markushi.ui.CircleButton;

public class ClientActivity extends AppCompatActivity {


    private String TAG = "Megafono";
    private Socket socket;
    protected PrintWriter dataOutputStream;
    protected InputStreamReader dataInputStream;
    private String mensajeEncriptado;
    private String ipServidor;
    private CircleButton audio;


    private MediaRecorder myAudioRecorder;
    private String outputFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Intent recibir = getIntent();
        ipServidor = recibir.getStringExtra("ip");



        audio = (CircleButton) findViewById(R.id.audioBtn);


        // Microphone button pressed/released
        audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN ) {


                    Log.i("JAIME", "==============OPRIMIO");

                    try {
                        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
                        myAudioRecorder = new MediaRecorder();
                        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                        myAudioRecorder.setOutputFile(outputFile);
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    } catch (IllegalStateException ise) {
                        // make something ...
                    } catch (IOException ioe) {
                        // make something
                    }



                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL ) {

                    Log.i("JAIME", "==============SOLTO");


                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                    Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();


                    FileInputStream fis = null;
                    BufferedInputStream bis = null;
                    OutputStream os = null;

                    File myFile = new File (outputFile);
                    byte [] mybytearray  = new byte [(int)myFile.length()];

                    try {
                        fis = new FileInputStream(myFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    bis = new BufferedInputStream(fis);
                    try {
                        bis.read(mybytearray,0,mybytearray.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String temp= Base64.encodeToString(mybytearray, Base64.DEFAULT);
                    sendData(temp);

                }
                return false;
            }
        });

        //sendData("Jaime Lombana\n");

    }

    public void Reproducir(View v){
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendData(final String data) {
        mensajeEncriptado = data;
        Log.i(TAG,  "================================Mensaje Enviado:      " + mensajeEncriptado);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String IP = ipServidor;
                    int Puerto = 7938;

                    Log.i(TAG,  "=============================IP :      " + IP);
                    socket = new Socket(IP, Puerto);
                    socket.setSoTimeout(20000);


                    dataOutputStream = new PrintWriter(socket.getOutputStream(), true);


                    //dataOutputStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_16), true);


                    dataInputStream = new InputStreamReader(socket.getInputStream());
                    Log.i(TAG,  "Socket y Flujos creados " + Puerto + "  "  +  IP);
                    dataOutputStream.println(mensajeEncriptado + "\n\r");


                    String dataSocket = new BufferedReader(dataInputStream).readLine();
                    String mensajeDesencriptado;
                    mensajeDesencriptado= dataSocket;
                    Log.i(TAG,  "========================= SE RECIBE: "+ mensajeDesencriptado+"\n");
                    if (mensajeDesencriptado != null) {

                    }

                } catch (UnknownHostException e) {
                    Log.e(TAG, "Error tipo: UnknownHostException");
                    e.printStackTrace();
                } catch (ConnectException e) {
                    Log.e(TAG,  "Error tipo: ConnectException");
                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
                    Log.e(TAG, "Error por SocketTimeoutException   " );
                    e.printStackTrace();

                } catch (IOException e) {
                    Log.e(TAG,  "Error tipo: IOException");
                    e.printStackTrace();
                } finally {
                    Log.i(TAG,  "Dando por terminada la tarea del Soket, se cierran los flujos y conexin");
                    if (socket != null) {
                        try {
                            if (dataOutputStream != null) {
                                dataOutputStream.close();
                            }
                            if (dataInputStream != null) {
                                dataInputStream.close();
                            }
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }).start();
    }
}
