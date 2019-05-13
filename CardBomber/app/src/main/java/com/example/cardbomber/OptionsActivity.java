package com.example.cardbomber;

import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class OptionsActivity extends AppCompatActivity {
    private static boolean soundOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //configure sound for return button
        final MediaPlayer click = MediaPlayer.create(this, R.raw.button_sound);
        Button returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(click);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        click.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });

        RadioGroup soundRadioGroup = (RadioGroup)findViewById(R.id.soundRadioGroup);
        RadioButton onRadioButton = (RadioButton)findViewById(R.id.onRadioButton);
        RadioButton offRadioButton = (RadioButton)findViewById(R.id.offRadioButton);

        //set the position of the previously selected option
        if(!soundOn) {
            soundRadioGroup.check(offRadioButton.getId());
        } else {
            soundRadioGroup.check(onRadioButton.getId());
        }

        //set listeners for radio buttons to turn sound on or off
        onRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundOn = true;
            }
        });
        offRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundOn = false;
            }
        });
    }

    //for other activities to check if sound is enabled
    public static boolean soundIsOn() {
        return soundOn;
    }

    //play sound if enabled
    private void playSound(MediaPlayer sound) {
        if(soundOn) {
            sound.start();
        }
    }
}
