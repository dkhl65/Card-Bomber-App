package com.example.cardbomber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener{
    private Button[][] buttons = new Button[9][9]; //values of cards
    private Button nextLevelButton; //the button for next level or play again
    private Resources res; //resources used throughout the activity
    private MediaPlayer click, cardSound, bombSound; //sounds

    //internal and external game data
    private int level = 1, score = 0, lives = 6, highScore; //main game values
    private int dim; //dimension of array
    private int[][] values; //card values
    private boolean flipped[][] = new boolean[9][9]; //flipped cards
    private int rowBombs[] = new int[9]; //bombs per row
    private int columnBombs[] = new int[9]; //bombs per column
    private int rowClear[] = new int[9]; //cleared rows
    private int columnClear[] = new int[9]; //cleared columns
    private int clear = 0; //number of cleared rows and columns

    @Override
    @SuppressLint("StringFormatInvalid")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        res = getResources();

        //load sound files
        click = MediaPlayer.create(this, R.raw.button_sound);
        cardSound = MediaPlayer.create(this, R.raw.card_sound);
        bombSound = MediaPlayer.create(this, R.raw.bomb_sound);

        //load high score
        SharedPreferences savedHighScore = this.getSharedPreferences("savedHighScore", Context.MODE_PRIVATE);
        highScore = savedHighScore.getInt("highScore", 0);
        ((TextView)findViewById(R.id.highScoreTextView)).setText(res.getString(R.string.highScore, highScore));


        //attach listener to quit button
        Button quitButton = (Button)findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(click);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        //attach listener to next level button
        nextLevelButton = (Button)findViewById(R.id.nextLevelButton);
        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(click);
                if(lives > 0) {
                    level++;
                    initialize();
                } else {
                    recreate();
                }
            }
        });

        //attach listeners for the card buttons
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                String buttonID = "button" + i + j;
                int resID = res.getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }
        initialize();
    }

    @Override
    public void onClick(View view) {
        int buttonID = view.getId();
        int[] colors = {res.getColor(R.color.oneYellow), res.getColor(R.color.twoOrange), res.getColor(R.color.threeRed)};

        //flip the card over
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                if(buttonID == res.getIdentifier("button" + i + j, "id", getPackageName())) {
                    if(flipped[i][j] || lives == 0 || clear == 2 * dim) {
                        return;
                    }
                    flipped[i][j] = true;
                    if(values[i][j] > 0) {
                        playSound(cardSound);
                        ((Button)view).setText("" + values[i][j]);
                        view.setBackgroundColor(colors[values[i][j]-1]);

                        //handle scoring
                        rowClear[i]++;
                        columnClear[j]++;
                        if(rowClear[i] == dim - rowBombs[i]) {
                            score += level;
                            clear++;
                        }
                        if(columnClear[j] == dim - columnBombs[j]) {
                            score += level;
                            clear++;
                        }
                        if(clear == 2 * dim) { //leveling up
                            toastText(res.getString(R.string.levelComplete));
                            nextLevelButton.setText(res.getString(R.string.nextLevel));
                            nextLevelButton.setVisibility(View.VISIBLE);
                        }
                        if(score > highScore) { //new high score
                            setNewHighScore();
                        }
                    } else { //bomb is clicked
                        playSound(bombSound);
                        ((Button)view).setText("\uD83D\uDCA3");
                        view.setBackgroundColor(res.getColor(R.color.bombBlue));

                        //handle the deduction of lives and game over
                        lives--;
                        if(lives == 0) {
                            toastText(res.getString(R.string.gameOver));
                            nextLevelButton.setText(res.getString(R.string.playAgain));
                            nextLevelButton.setVisibility(View.VISIBLE);
                        }
                    }
                    setGameValues();
                    return;
                }
            }
        }
    }

    //sets the game up every level
    private void initialize() {
        int value; //used to store a random number and determining the card value
        int rowValues[] = new int[9];
        int columnValues[] = new int[9];

        //reset game data
        setGameValues();
        nextLevelButton.setVisibility(View.INVISIBLE);
        clear = 0;
        for(int i = 0; i < 9; i++) {
            rowBombs[i] = 0;
            columnBombs[i] = 0;
            rowClear[i] = 0;
            columnClear[i] = 0;
        }
        
        //set the correct cards visible
        if (level < 6) {
            dim = level + 4;
        } else {
            dim = 9;
        }
        for(int i = 5; i < dim; i++) {
            findViewById(res.getIdentifier("tableRow" + i, "id",getPackageName())).setVisibility(View.VISIBLE);
            findViewById(res.getIdentifier("column" + i + "ValuesTextView", "id",getPackageName())).setVisibility(View.VISIBLE);
            findViewById(res.getIdentifier("column" + i + "BombsTextView", "id",getPackageName())).setVisibility(View.VISIBLE);
            for(int j = 0; j < dim; j++) {
                findViewById(res.getIdentifier("button" + j + i, "id", getPackageName())).setVisibility(View.VISIBLE);
            }
        }
        
        //assign all card values
        values = new int[9][9];
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                //face down all cards
                buttons[i][j].setBackgroundColor(res.getColor(R.color.defaultGreen));
                buttons[i][j].setText("");

                //determine card values
                value = (int)(100 * Math.random()); //random number from 0 to 99
                //probabilities depend on the size of the grid
                switch(dim){
                    case 5:
                        if(value < 20) values[i][j] = 0; //1/5 chance for bomb
                        else if(value >=20 && value < 60) values[i][j] = 1;
                        else if(value >=60 && value < 80) values[i][j] = 2;
                        else if(value >=80 && value < 100) values[i][j] = 3;
                        break;
                    case 6:
                        if(value < 17) values[i][j] = 0; //1/6 chance for bomb
                        else if(value >=17 && value < 59) values[i][j] = 1;
                        else if(value >=59 && value < 80) values[i][j] = 2;
                        else if(value >=80 && value < 100) values[i][j] = 3;
                        break;
                    case 7:
                        if(value < 14) values[i][j] = 0; //1/7 chance for bomb
                        else if(value >=14 && value < 57) values[i][j] = 1;
                        else if(value >=57 && value < 79) values[i][j] = 2;
                        else if(value >=79 && value < 100) values[i][j] = 3;
                        break;
                    case 8:
                        if(value < 13) values[i][j] = 0; //1/8 chance for bomb
                        else if(value >=13 && value < 57) values[i][j] = 1;
                        else if(value >=57 && value < 79) values[i][j] = 2;
                        else if(value >=79 && value < 100) values[i][j] = 3;
                        break;
                    case 9:
                        if(value < 11) values[i][j] = 0; //1/9 chance for bomb
                        else if(value >=11 && value < 56) values[i][j] = 1;
                        else if(value >=56 && value < 78) values[i][j] = 2;
                        else if(value >=78 && value < 100) values[i][j] = 3;
                        break;
                }//end switch

                //set clues
                flipped[i][j] = false;
                rowValues[i] += values[i][j];
                columnValues[j] += values[i][j];
                if(values[i][j] == 0) {
                    rowBombs[i]++;
                    columnBombs[j]++;
                }
            }//end for
        }//end for

        //set all the clue text views
        for(int i = 0; i < dim; i++) {
            int rowValuesID = res.getIdentifier("row" + i + "ValuesTextView", "id", getPackageName());
            int rowBombsID = res.getIdentifier("row" + i + "BombsTextView", "id", getPackageName());
            int columnValuesID = res.getIdentifier("column" + i + "ValuesTextView", "id", getPackageName());
            int columnBombsID = res.getIdentifier("column" + i + "BombsTextView", "id", getPackageName());

            ((TextView)findViewById(rowValuesID)).setText("" + rowValues[i]);
            ((TextView)findViewById(rowBombsID)).setText("" + rowBombs[i]);
            ((TextView)findViewById(columnValuesID)).setText("" + columnValues[i]);
            ((TextView)findViewById(columnBombsID)).setText("" + columnBombs[i]);
        }

    }//end initialize

    //Sets the text views with numbers
    @SuppressLint("StringFormatInvalid")
    private void setGameValues() {
        TextView levelTextView, scoreTextView, livesTextView;
        levelTextView = (TextView)findViewById(R.id.levelTextView);
        levelTextView.setText(res.getString(R.string.level, level));
        scoreTextView = (TextView)findViewById(R.id.scoreTextView);
        scoreTextView.setText(res.getString(R.string.score, score));
        livesTextView = (TextView)findViewById(R.id.livesTextView);
        livesTextView.setText(res.getString(R.string.lives, lives));
        if(lives == 0) {
            livesTextView.setTextColor(res.getColor(R.color.threeRed));
        }
    }

    private void setNewHighScore() {
        TextView highScoreTextView = (TextView)findViewById(R.id.highScoreTextView);
        highScore = score;
        SharedPreferences savedHighScore = getSharedPreferences("savedHighScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savedHighScore.edit();
        editor.putInt("highScore", highScore);
        editor.commit();
        highScoreTextView.setTextColor(res.getColor(R.color.defaultGreen));
        highScoreTextView.setText(res.getString(R.string.highScore, highScore));
    }

    //makes the appropriate noise if enabled
    private void playSound(MediaPlayer sound) {
        if(OptionsActivity.soundIsOn()) {
            sound.start();
        }
    }

    //prints a message at the bottom of the screen
    private void toastText(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
