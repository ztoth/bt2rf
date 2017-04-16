package net.thetothfamily.home.outletswitch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView text;
    BluetoothAdapter bt;
    BluetoothDevice bt_dev;
    BluetoothSocket bt_sock;
    String bt_address;
    OutputStream bt_out;
    boolean bt_opened_now = false;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int BT_START_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = (Button)findViewById(R.id.button1);
        b.setEnabled(false);
        b = (Button)findViewById(R.id.button2);
        b.setEnabled(false);
        b = (Button)findViewById(R.id.button3);
        b.setEnabled(false);
        b = (Button)findViewById(R.id.button4);
        b.setEnabled(false);
        b = (Button)findViewById(R.id.button5);
        b.setEnabled(false);

        text = (TextView)findViewById(R.id.textView);
        text.setText("connecting...");

        bt = BluetoothAdapter.getDefaultAdapter();
        if (!bt.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BT_START_ACTIVITY);
            bt_opened_now = true;
        } else {
            text.setText("connecting...");
            connectSwitch();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BT_START_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                connectSwitch();
            }
        }
    }

    /** Called when the user touches the 1 button */
    public void sendOne(View view) {
        sendMessage('1');
    }

    /** Called when the user touches the 2 button */
    public void sendTwo(View view) {
        sendMessage('2');
    }

    /** Called when the user touches the 3 button */
    public void sendThree(View view) {
        sendMessage('3');
    }

    /** Called when the user touches the 4 button */
    public void sendFour(View view) {
        sendMessage('4');
    }

    /** Called when the user touches the 5 button */
    public void sendFive(View view) {
        sendMessage('5');
    }

    /** Send message via bluetooth */
    public void sendMessage(char b) {
        if (!bt.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            try {
                bt_out.write(b);
            } catch (IOException e) { }
        }
    }

    /** connect to the device */
    public void connectSwitch() {
        Set<BluetoothDevice> pairedDevices = bt.getBondedDevices();
        if (pairedDevices.size() <= 0) {
            this.finish();
        }

        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("RNBT-57AF")) {
                bt_dev = device;
                bt_address = device.getAddress();
                break;
            }
        }

        if ((bt_address == null) || (bt_dev == null) || (MY_UUID == null)) {
            this.finish();
        }

        bt.cancelDiscovery();

        try {
            bt_sock = bt_dev.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }

        if (bt_sock == null) {
            this.finish();
        }

        while (!bt_sock.isConnected()) {
            try {
                bt_sock.connect();
            } catch (IOException connectException) { }
        }

        try {
            bt_out = bt_sock.getOutputStream();
        } catch (IOException e) { }

        text.setText("connected to " + bt_dev.getName() + " (" + bt_address + ")");

        Button b = (Button)findViewById(R.id.button1);
        b.setEnabled(true);
        b = (Button)findViewById(R.id.button2);
        b.setEnabled(true);
        b = (Button)findViewById(R.id.button3);
        b.setEnabled(true);
        b = (Button)findViewById(R.id.button4);
        b.setEnabled(true);
        b = (Button)findViewById(R.id.button5);
        b.setEnabled(true);
    }

    public void quitSwitch(View view) {
        if (bt_sock.isConnected()) {
            try {
                bt_sock.close();
            } catch (IOException e) { }
        }

        if (bt_opened_now) {
            bt.disable();
        }
        this.finish();
    }
}
