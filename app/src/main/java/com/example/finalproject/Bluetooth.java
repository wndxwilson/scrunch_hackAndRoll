package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Bluetooth extends AppCompatActivity {

    TextView t1;
    //Button b1;
    String address = null, name = null;

    final int handlerState = 0;

    private boolean flag = false;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Handler bluetoothIn;

    private  ConnectedThread mConnectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        t1 = findViewById(R.id.t1);
       // b1 = findViewById(R.id.bt);
       // b1.performClick();

      //  try {
      //      bluetooth_connect_device();
      //  } catch (IOException e) {
       //     e.printStackTrace();
        //}


    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onPause(){
        super.onPause();
        try {
            bluetooth_connect_device();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothIn = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == handlerState){
                    String readMessage = (String)msg.obj;
                    //t1.setText(readMessage);
                    if(readMessage.contains("a")&&!flag) {
                        Intent intent = new Intent(Bluetooth.this, Alert.class);
                        startActivity(intent);
                        flag = true;
                    }

                }
            }
        };
    }

    public void Contact(View v){
        t1.setText("Connecting...");
        Intent intent = new Intent(this, listContact.class);
        startActivity(intent);
    }

    private void bluetooth_connect_device() throws IOException {
        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            address = myBluetooth.getAddress();
            pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice bt : pairedDevices) {
                    address = bt.getAddress().toString();
                    name = bt.getName().toString();
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

                }
            }
        } catch (Exception e) {
        }
        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
            btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
            btSocket.connect();
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
            t1.setText("Bt Name: " + name + "\nBT Address: " + address);
        }catch (Exception e){
            Log.e("error","connection");
        }


    }

    private class ConnectedThread extends Thread{
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
