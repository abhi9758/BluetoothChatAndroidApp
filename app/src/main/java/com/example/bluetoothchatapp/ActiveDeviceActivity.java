package com.example.bluetoothchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class ActiveDeviceActivity extends AppCompatActivity {
    private ListView l1 ,l2;
    private ProgressBar progressBar;
    private ArrayAdapter<String>adp1 ,adp2;
    private Context context;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_device);
        context = this;
        init();
    }
    private  void init()
    {
        l1 = findViewById(R.id.ListPairedDevices);
        l2 = findViewById(R.id.ListAvailableDevices);
        progressBar = findViewById(R.id.prog_bar);

        adp1 = new ArrayAdapter<String>(context , R.layout.device_list_item);
        adp2 = new ArrayAdapter<String>(context , R.layout.device_list_item);

        l1.setAdapter(adp1);
        l2.setAdapter(adp2);

        l2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length()-17);

                Intent intent = new Intent();
                intent.putExtra("deviceAddress",address);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices != null && pairedDevices.size() > 0)
        {
            for(BluetoothDevice device: pairedDevices)
            {
                adp1.add(device.getName() + "\n" +device.getAddress());
            }
        }
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothDeviceListener,intentFilter);
        IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothDeviceListener,intentFilter1);

        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length()-17);

                Intent intent = new Intent();
                intent.putExtra("deviceAddress",address);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    private BroadcastReceiver  bluetoothDeviceListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    adp2.add(device.getName() + "\n" + device.getAddress());
                }
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                progressBar.setVisibility(View.GONE);
                if(adp2.getCount() == 0)
                {
                    Toast.makeText(context, "No new Device Found", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, "Click on device to start the chat", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_active_device,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_device:
                scanDevices();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }
    private void scanDevices()
    {
        progressBar.setVisibility(View.VISIBLE);
        adp2.clear();

        Toast.makeText(context, "Scan Started", Toast.LENGTH_SHORT).show();

        if(bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothDeviceListener != null) {
            unregisterReceiver(bluetoothDeviceListener);
        }
    }
}