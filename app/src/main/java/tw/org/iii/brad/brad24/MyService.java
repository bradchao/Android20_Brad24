package tw.org.iii.brad.brad24;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    /*
    * Start Service <---
    * Bind Service
     */
    private MediaPlayer mediaPlayer;
    private int musicLen;
    private Timer timer;
    private File sdroot;

    public MyService(){
        sdroot = Environment.getExternalStorageDirectory();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("brad", "onCreate");

        timer = new Timer();
        timer.schedule(new MyTask(), 0, 100);

        //mediaPlayer = MediaPlayer.create(this, R.raw.brad);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(sdroot.getAbsolutePath() + "/Music/ADJ03.mp3");
            mediaPlayer.prepare();
        }catch (Exception e){
            Log.v("brad", e.toString());
        }


        musicLen = mediaPlayer.getDuration();
        Intent intent = new Intent("fromService");
        intent.putExtra("len", musicLen);
        sendBroadcast(intent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("brad", "onStartCommand");
        String act = intent.getStringExtra("ACTION");

        if (act.equals("start")){
            mediaPlayer.start();
        }else if (act.equals("pause") && mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else if (act.equals("seekto") && mediaPlayer != null){
            int where = intent.getIntExtra("where", -1);
            if (where >= 0){
                mediaPlayer.seekTo(where);
            }
        }else if(act.equals("restart") && mediaPlayer != null){
            musicLen = mediaPlayer.getDuration();
            Intent intent2 = new Intent("fromService");
            intent2.putExtra("len", musicLen);
            sendBroadcast(intent2);
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private class MyTask extends TimerTask {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()){
                int now = mediaPlayer.getCurrentPosition();
                Intent intent = new Intent("fromService");
                intent.putExtra("now", now);
                sendBroadcast(intent);
            }
        }
    }


    @Override
    public void onDestroy() {
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }


        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }

        Log.v("brad", "onDestroy");
        super.onDestroy();
    }
}
