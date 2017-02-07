package uk.co.psykzz.shufflingtones;

import android.app.Activity;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private String TAG = "ShufflingTones";

    private Ringtone lastSound;
    private ListView toneView ;


    private int playLength = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Map<String, String> tones = getRingtones();
        List<String> toneList = new ArrayList<>(tones.keySet());

        Log.d(TAG, String.format("Tones found, %s", tones));

        toneView = (ListView) findViewById(R.id.tone_list);
        toneView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toneList));

        toneView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = toneView.getItemAtPosition(position);
                Uri soundUri = Uri.parse( tones.get(item));

                Ringtone sound = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
                lastSound.stop();
                playTone(sound);

            }
        });


        playDefaultTone();
    }
    @Override
    protected void onPause() {
        super.onPause();
        lastSound.stop();
    }

    private void playDefaultTone() {
        playDefaultTone(RingtoneManager.TYPE_RINGTONE);
    }

    private void playDefaultTone(int toneType) {
        Uri notification = RingtoneManager.getDefaultUri(toneType);
        Ringtone sound = RingtoneManager.getRingtone(getApplicationContext(), notification);
        playTone(sound);
    }

    private void playTone(Ringtone tone) {
        playTone(tone, true, playLength);
    }

    private void playTone(Ringtone tone, boolean shouldStop, int timeout) {
        lastSound = tone;
        lastSound.play();
        if (shouldStop) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    lastSound.stop();
                }
            }, timeout);
        }
    }

    private Map<String, String> getRingtones() {
        return getRingtones(RingtoneManager.TYPE_RINGTONE);
    }

    private Map<String, String> getRingtones(int toneType) {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(toneType);
        Cursor cursor = manager.getCursor();

        Map<String, String> list = new HashMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = String.format("%s/%s", cursor.getString(RingtoneManager.URI_COLUMN_INDEX), cursor.getString(RingtoneManager.ID_COLUMN_INDEX));
            list.put(notificationTitle, notificationUri);
        }

        return list;

    }
}
