package tw.org.iii.brad.brad24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private MyReceiver myReceiver;
    private boolean isReadSD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
        }else{
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){
        isReadSD = true;
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        init2();


    }

    private void seekTo(int seekto){
        Intent intent = new Intent(MainActivity.this, MyService.class);
        intent.putExtra("ACTION", "seekto");
        intent.putExtra("where", seekto);
        startService(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v("brad", "onStart");
        init2();
    }

    private void init2(){
        if (isReadSD) {
            myReceiver = new MyReceiver();
            IntentFilter filter = new IntentFilter("fromService");
            registerReceiver(myReceiver, filter);

            Intent intent = new Intent(this, MyService.class);
            intent.putExtra("ACTION", "restart");
            startService(intent);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
    }

    // Start
    public void test1(View view) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("ACTION", "start");
        startService(intent);
    }

    // Stop
    public void test2(View view) {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }


    // Pause
    public void test3(View view) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("ACTION", "pause");
        startService(intent);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent.getAction();
            Log.v("brad", act);
            if (seekBar != null) {
                int len = intent.getIntExtra("len", -1);
                if (len > 0) seekBar.setMax(len);

                int now = intent.getIntExtra("now", -1);
                if (now > 0) seekBar.setProgress(now);
            }


        }
    }

}
