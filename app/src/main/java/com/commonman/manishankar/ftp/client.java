package com.commonman.manishankar.ftp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class client extends AppCompatActivity {
    ListView lv;
    String[] items;
  EditText selected;
    File newfile1;
    String path;
   File maindirectory;
   File fromfile;
    TextView textResponse;
  //  EditText editTextAddress, editTextPort;
    Button buttonConnect,chooseimage;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        lv=(ListView) findViewById(R.id.listView);
        selected=(EditText)findViewById(R.id.selected);
        maindirectory=getDir();
        final ArrayList<File> myfiles=findfiles(maindirectory);
        items=new String[myfiles.size()];
        for(int i=0;i<myfiles.size();i++){

            items[i]=myfiles.get(i).getName().toString();
        }
        //Arrays.sort(items);
        ArrayAdapter<String> adp=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,items);
        lv.setAdapter(adp);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    fromfile=myfiles.get(position);
                    path=fromfile.toString();
                selected.setText(path);
            }
        });

    //    editTextAddress = (EditText)findViewById(R.id.address);
     //  editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
            //buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);
        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
      /*  buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textResponse.setText("");
            }
        });*/
    }


    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener(){
                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask();
                    myClientTask.execute();
                }};
    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String dstAddress="192.168.43.1";
    /* final*/ int dstPort=8080;
        String response = "";
        MyClientTask(){
            //dstAddress=add;
            //dstPort=Integer.parseInt(port);

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            //fromfile=getDir();
            Context context=getApplicationContext();
            wifiManager=(WifiManager) context.getSystemService(WIFI_SERVICE);
            wifiInfo=wifiManager.getConnectionInfo();
            int ip=wifiInfo.getIpAddress();
           //dstAddress=getip(ip);
            //Toast.makeText(getApplicationContext(),ipadd,Toast.LENGTH_SHORT);
           // fromfile=new File(maindirectory,"te.jpg");
            //filePath.
            Socket socket = null;
            try {
                socket = new Socket(dstAddress, dstPort);

                File newfile=new File(String.valueOf(fromfile));
                //ByteArrayOutputStream byteArrayOutputStream =
               //new ByteArrayOutputStream(1024);
                OutputStream os;
                os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF(newfile.getName());

                /*byte[] bytes = new byte[(int)newfile.length()];
                BufferedInputStream bis;
                OutputStream os;
                os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF(newfile.getName());
              //  dos.writeLong(mybytearray.length);
             //     bis = new BufferedInputStream(new FileInputStream(newfile));
               // bis.read(bytes, 0, bytes.length);
                //OutputStream os = socket.getOutputStream();*/
                FileInputStream in = new FileInputStream(newfile);
                int len;
                byte[] buf=new byte[1024];

                while((len=in.read(buf))!= -1)
                {
                    os.write(buf, 0, len);
                }

                //os.write(bytes, 0, bytes.length);
                os.close();
                    os.flush();
                    socket.close();
                    final String sentMsg = "File sent to: " + socket.getInetAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }

        public String getip(int i){
            String ipad;
            if(ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)){
                i=Integer.reverseBytes(i);
            }
            byte[] ipbyteArray= BigInteger.valueOf(i).toByteArray();

            try {
                ipad= InetAddress.getByAddress(ipbyteArray).getHostAddress();
            } catch (UnknownHostException e) {
                ipad=null;
                e.printStackTrace();
            }
            return ipad;
        }
    }
    private File getDir() {

        File directory = null;

        if (Environment.getExternalStorageState() == null) {
            //create new file directory object
            directory = new File(Environment.getDataDirectory()
                    + "/ManiFTP/");

            // if no directory exists, create new directory
            if (!directory.exists()) {
                directory.mkdir();
            }

            // if phone DOES have sd card
        } else if (Environment.getExternalStorageState() != null) {
            // search for directory on SD card
            directory = new File(Environment.getExternalStorageDirectory()
                    + "/ManiFTP/");
            Toast.makeText(client.this, "folder available", Toast.LENGTH_SHORT).show();
            if (!directory.exists()) {
                directory.mkdir();
            }
        }

        return directory;
    }
    public ArrayList<File> findfiles(File root){
        ArrayList<File>al=new ArrayList<File>();
        File[] files=root.listFiles();
        for(File singlefile : files){
                    al.add(singlefile);
            }
        Toast.makeText(client.this, "files selected", Toast.LENGTH_SHORT).show();
        return al;
    }

}
