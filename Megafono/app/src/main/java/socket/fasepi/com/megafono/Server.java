package socket.fasepi.com.megafono;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Server {

    private String TAG = "Megafono";
    private final String ACTION_STRING_ACTIVITY = "ToActivity";
    private Context context;
    ServerActivity activity;
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPORT = 7938;


    BufferedReader reader;


    public Server(ServerActivity activity) {
        this.context = activity;
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(socketServerPORT);

                while (true) {
                    Socket socket = serverSocket.accept();
                    count++;


                   // message += "#" + count + " from "
                   //         + socket.getInetAddress() + ":"
                   //         + socket.getPort() + "\n";


                    message += "#" + count + " Se recibe Sonido desde " + socket.getInetAddress()  + "\n";


                    //InputStreamReader dataInputStream = new InputStreamReader(socket.getInputStream());

                    //String dataSocket = new BufferedReader(dataInputStream).readLine();



                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String buffer = "";
                    int cont;
                    while ((cont = in.read()) != -1) {
//                System.out.println("Car:" + cont + " - " + (char) cont);
                        buffer += (char) cont;
                    }


                    Log.i(TAG, "============Debe llegar esto " +  buffer );



                    Intent error = new Intent();
                    error.putExtra("CMD", "Audio");
                    error.putExtra("DATA", buffer);
                    error.setAction(ACTION_STRING_ACTIVITY);
                    context.sendBroadcast(error);


                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            activity.msg.setText(message);
                        }
                    });

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                            socket, count);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            InputStreamReader inputStream;
            String msgReply = "Hello from Server, you are #" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();



                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                //message += "replayed: " + msgReply + "\n";

                message += "Reproduciendo Sonido... "  + "\n";


                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.msg.setText(message);
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    activity.msg.setText(message);
                }
            });
        }

    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
