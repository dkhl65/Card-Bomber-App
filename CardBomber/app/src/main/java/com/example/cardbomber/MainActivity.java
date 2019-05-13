package com.example.cardbomber;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //click sound for buttons
        final MediaPlayer click = MediaPlayer.create(this, R.raw.button_sound);
        click.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });

        //attach the listener for each button to go to each page
        Button instructionsButton = (Button)findViewById(R.id.instructionsButton);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OptionsActivity.soundIsOn()) {
                    click.start();
                }
                startActivity(new Intent(getApplicationContext(), InstructionsActivity.class));
            }
        });

        Button playButton = (Button)findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OptionsActivity.soundIsOn()) {
                    playSound(click);
                }
                startActivity(new Intent(getApplicationContext(), PlayActivity.class));
            }
        });

        Button optionsButton = (Button)findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(click);
                startActivity(new Intent(getApplicationContext(), OptionsActivity.class));
            }
        });
    }

    //play sound if enabled
    private void playSound(MediaPlayer sound) {
        if(OptionsActivity.soundIsOn()) {
            sound.start();
        }
    }
}
