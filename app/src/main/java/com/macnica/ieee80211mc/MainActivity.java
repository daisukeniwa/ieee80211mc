package com.macnica.ieee80211mc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
//import android.net.wifi.rtt.WifiRttManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnClickListener, OnItemClickListener {

    private static final String TAG = "MainActivity";
    private static final int UPDATE_LIST = 0;
    private static final int START_SCAN = 1;

    private static final int WC	= LinearLayout.LayoutParams.WRAP_CONTENT;
    private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;

    private WifiManager mWifiManager;
//    private WifiRttManager mRttManager;
    private Handler mHandler;

    private ListView mListView;

    private List<ScanResult> mList;

    private int failureCnt = 0;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive START");
            mHandler.sendEmptyMessage(UPDATE_LIST);
        }
    };

    private void updateListView() {
        Log.d(TAG, "updateListView START");
        int size = mList.size();
        for (ScanResult result : mList) {
            
        }
    }

    private void updateScanResult() {
        Log.d(TAG, "updateScanResult START");
        mList = mWifiManager.getScanResults();
        updateListView();
    }

    private boolean startScan() throws SecurityException {
        Log.d(TAG, "startScan START");
        return mWifiManager.startScan();
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id ) {

    }

    public void onClick(View view) {
        Log.d(TAG, "onClick START");
            if (failureCnt <= 3 && !startScan()) {
                Log.d(TAG, "startScan fail");
                mHandler.sendEmptyMessageDelayed(START_SCAN, 10000);
                failureCnt++;
            } else {
                Log.d(TAG, "startScan success");
                failureCnt = 0;
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "MainActivity START");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_LIST:
                        updateScanResult();
                        break;
                    case START_SCAN:
                        if (failureCnt <= 3 && !startScan()) {
                            Log.d(TAG, "startScan fail");
                            mHandler.sendEmptyMessageDelayed(START_SCAN, 10000);
                            failureCnt++;
                        } else {
                            Log.d(TAG, "startScan success");
                            failureCnt = 0;
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        mRttManager = (WifiRttManager) getSystemService(Context.WIFI_RTT_RANGING_SERVICE);

        LinearLayout layout = new LinearLayout( this );
        layout.setOrientation( LinearLayout.VERTICAL );
        setContentView( layout );

        mListView = new ListView( this );
        mListView.setScrollingCacheEnabled( false );
        mListView.setOnItemClickListener( this );
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams( FP, 0 );
        layoutparams.weight = 1;
        mListView.setLayoutParams( layoutparams );
        layout.addView( mListView );
/*
        Button exitBtn = new Button(this);
        exitBtn.setText( "Exit" );
        exitBtn.setOnClickListener(this);
        layoutparams = new LinearLayout.LayoutParams( FP, WC );
        exitBtn.setLayoutParams( layoutparams );
        exitBtn.setPadding( 10, 10, 10, 10 );
        layout.addView( exitBtn );
 */
        Button startBtn = new Button(this);
        startBtn.setText( "Start SCAN" );
        startBtn.setOnClickListener(this);
        layoutparams = new LinearLayout.LayoutParams( FP, WC );
        startBtn.setLayoutParams( layoutparams );
        startBtn.setPadding( 10, 10, 10, 10 );
        layout.addView( startBtn );
    }

}
