package com.domain.filter;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private static final int VPN_REQUEST_CODE = 0x0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button startButton = new Button(this);
        startButton.setText("Start VPN");

        Button stopButton = new Button(this);
        stopButton.setText("Stop VPN");

        startButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = VpnService.prepare(MainActivity.this);
                    if (intent != null) {
                        startActivityForResult(intent, VPN_REQUEST_CODE);
                    } else {
                        onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
                    }
                }
            });

        stopButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent stopIntent = new Intent(MainActivity.this, MyVpnService.class);
                    stopIntent.setAction("STOP_VPN");
                    startService(stopIntent);
                }
            });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(startButton);
        layout.addView(stopButton);
        setContentView(layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            startService(new Intent(this, MyVpnService.class));
        }
    }
}

