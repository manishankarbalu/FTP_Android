package com.commonman.manishankar.ftp;

import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;

public class server extends AppCompatActivity {

    private static int filesize = 100000;
    File maindirectory;
    File targetfile;
    TextView info, infoip, msg;
    String message = "";
    ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        maindirectory=getDir();
        server.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ApManager apManager=new ApManager();
                apManager.isApOn(getApplicationContext());
                apManager.configApState(getApplicationContext());

            }
        });
        /*ApManager apManager=new ApManager();
        apManager.isApOn(getApplicationContext());
        apManager.configApState(getApplicationContext());
        */Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class SocketServerThread extends Thread {
        static final int SocketServerPORT = 8080;
        int count = 0;
        @Override
        public void run() {
            try {
                sleep(3500);
                serverSocket = new ServerSocket(SocketServerPORT);
                server.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        info.setText("I'm waiting here: "
                                + serverSocket.getLocalPort());
                        infoip.setText(getIpAddress());

                    }
                });
                while (true) {
                    Socket socket = serverSocket.accept();
           //         count++;
                    message += "file from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n";
                    server.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg.setText(message);
                        }
                    });
                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                            socket);
                    socketServerReplyThread.run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private class SocketServerReplyThread extends Thread {
        private Socket hostThreadSocket;
       // int cnt;
        SocketServerReplyThread(Socket socket) throws IOException {
            hostThreadSocket = socket;

         //   cnt = c;
        }
        @Override
        public void run() {
            OutputStream outputStream;
            //String msgReply = "Hello from Android, you are #" + cnt;
            //targetfile=new File(getDir(),"cpy.jpg");
            try {
                byte[] bytes = new byte[1024];
                InputStream is = hostThreadSocket.getInputStream();
                //is=socket.getInputStream();
                DataInputStream clientData = new DataInputStream(is);
                String fileName = clientData.readUTF();
                targetfile=new File(getDir(),fileName);
               targetfile.createNewFile();
                FileOutputStream fos = new FileOutputStream(targetfile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);


                int bytesRead;// = is.read(bytes, 0, bytes.length);
                //int current=bytesRead;
                while(true) {
                    bytesRead = is.read(bytes,0, bytes.length);
                    if (bytesRead == -1) {
                        break;
                    }
                    bos.write(bytes, 0, bytesRead);
                    bos.flush();
                    //c++;

                } //while (bytesRead >-1);


                bos.close();
                hostThreadSocket.close();
                server.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText(message);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }
            server.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msg.setText(message);
                }
            });
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
    private File getDir() {

        File directory = null;

        if (Environment.getExternalStorageState() == null) {
            //internal memory
            directory = new File(Environment.getDataDirectory()
                    + "/ManiFTP/");


            if (!directory.exists()) {
                directory.mkdir();
                Toast.makeText(getApplicationContext(),"making dir",Toast.LENGTH_SHORT).show();
            }

        } else if (Environment.getExternalStorageState() != null) {
            //sd card
            directory = new File(Environment.getExternalStorageDirectory()
                    + "/ManiFTP/");

            if (!directory.exists()) {
                directory.mkdir();
                Toast.makeText(getApplicationContext(),"making dir",Toast.LENGTH_SHORT).show();
            }
        }
       // Toast.makeText(getApplicationContext(),"dir available",Toast.LENGTH_SHORT).show();
        return directory;
    }

}
