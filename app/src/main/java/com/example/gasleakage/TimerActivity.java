package com.example.gasleakage;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class TimerActivity extends AppCompatActivity {

    private final String DEVICE_ADDRESS= "98:D3:71:F5:B8:BB";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    boolean deviceConnected=false;
    private EditText Seconds;
    private EditText Minutes;
    private OutputStream outputStream;
    private InputStream inputStream;
    byte buffer[];
    TextView textView;
    boolean stopThread;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        onClickStart();

        Minutes=findViewById(R.id.MinuteEdittext);
        Seconds=findViewById(R.id.SecondsEditText);
        button=findViewById(R.id.OkButton);

        findViewById(R.id.OkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndShowAlertDialog();
            }
        });

        Minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String string = Minutes.getText().toString();

                if(string.isEmpty()) {
                    Minutes.setError("enter any value");
                    Minutes.requestFocus();
                    return;
                }

                if (!string.isEmpty()) {

                    int num = Integer.parseInt(string);

                    if (num > 60) {
                        Minutes.setError("Value should be less than 60");
                        Minutes.requestFocus();
                        return;
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Seconds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String string = Seconds.getText().toString();

                if(string.isEmpty()) {
                    Seconds.setError("enter any value");
                    Seconds.requestFocus();
                    return;
                }

                if (!string.isEmpty()) {

                    int num = Integer.parseInt(string);

                    if (num > 60) {
                        Seconds.setError("Value should be less than 60");
                        Seconds.requestFocus();
                        return;
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public void onClickStart() {
        if (BTinit()) {
            if (BTconnect()) {
                deviceConnected = true;
            }

        }
    }



    public boolean BTinit() {
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTconnect() {
        boolean connected = true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }
        if (connected) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }





    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure ?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String string = Minutes.getText().toString();

                String string2 = Seconds.getText().toString();

                if(string.isEmpty() || string2.isEmpty()) {

                }
                else {

                    int num = Integer.parseInt(string);
                    int num2 = Integer.parseInt(string2);

                    if (num <= 60 && num2 <= 60) {
                        onClickSend();
                        Intent i = new Intent(getApplicationContext(), RealtimeActivity.class);
                        i.putExtra("min", num);
                        i.putExtra("sec",num2);
                        Log.d("value of time is ", string);
                        startActivity(i);
                        finish();
                    }

                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void onClickSend() {
        String string = Minutes.getText().toString();
        String string2= Seconds.getText().toString();
        Log.d("string2 value is",string2);
        string = string.concat(string2);
        Log.d("string3 value is",string);
         try {
            outputStream.write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
