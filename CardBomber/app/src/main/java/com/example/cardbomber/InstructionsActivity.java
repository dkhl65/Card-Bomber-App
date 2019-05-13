package com.example.cardbomber;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InstructionsActivity extends AppCompatActivity {
    private int page = 1;
    private String page2Text;
    private String mainMenu;
    private TextView instructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        instructions();
    }

    //the main body of the code, as a method to allow orientation changes
    private void instructions() {
        Resources resources = getResources();
        page2Text = resources.getString(R.string.instructions2);
        mainMenu = resources.getString(R.string.returnToMainMenu);
        final MediaPlayer click = MediaPlayer.create(this, R.raw.button_sound);

        Button nextPageButton = (Button)findViewById(R.id.nextPageButton);
        instructionsTextView = (TextView)findViewById(R.id.instructionsTextView);

        //if orientation changes on the second page
        if(page == 2) {
            instructionsTextView.setText(page2Text);
            nextPageButton.setText(mainMenu);
        }

        //listener to advance to page 2 or back to menu
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OptionsActivity.soundIsOn()) {
                    click.start();
                }
                if(page == 1) {
                    instructionsTextView.setText(page2Text);
                    ((Button)view).setText(mainMenu);
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                page++;
            }
        });
    }

    //handle orientation changes
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page", page);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        page = savedInstanceState.getInt("page");
        instructions();
    }
}
