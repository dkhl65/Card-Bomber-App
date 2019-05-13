/*
 * GameGrid
 * Created by Daniel Liang
 * Date: 2016/06/12
 * Purpose: This class handles everything related to the grid the game is played on
*/

import hsa.Console;
import java.awt.*;

public class GameGrid
{
     //attributes
     //grid size and positioning
     private Console c; //console to output grid on
     private int size = 30; //size of card
     private int space = 10; //spacing between cards
     private int dim = 5; //size of grid
     private int devx = 130; //distance away from left of console
     private int devy = 100; //distance away from top of console
     private int x = 0; //current x position of cursor
     private int y = 0; //current y position of cursor
     
     //clues, card value tracking and game data
     private int[] sumx = new int[9]; //totals for each column
     private int[] sumy = new int[9]; //totals for each row
     private int[] bombsx = new int[9]; //number of bombs in each column
     private int[] bombsy = new int[9]; //number of bombs in each row
     private boolean[][] flipped = new boolean[9][9]; //is the card flipped?
     private int[][] values = new int[9][9]; //card values
     private int score = 0;
     private int lives = 6;
     private int level = 1;
     private int[] clearedx = new int[9]; //used to check if a column is clear
     private int[] clearedy = new int[9]; //used too check if a row is clear
     private int cleared = 0; //used to check if entire grid is clear
     
     //constructor with custom attributes
     public GameGrid(int xPos, int yPos, int size, int space, Console c)
     {    
          //assign all attributes
          this.c = c;
          this.space = space;
          this.size = size;
          this.devx = xPos;
          this.devy = yPos;
          
          //assign all game data
          initialize();
          
          //draw cursor at card (0,0)
          drawPos(0,0);
     }//end GameGrid
     
     //default constructor
     public GameGrid(Console c)
     {    
          //assign the console
          this.c = c;
          
          //initialize game data
          initialize();
          
          //draw cursor on card (0,0)
          drawPos(0,0);
     }//end GameGrid
     
     //assign values to all the game data
     private void initialize()
     {
          int value; //used to store a random number and determining the card value
          //assign all card values
          for(int i = 0; i < values.length; i++){
               for(int j = 0; j < values[i].length; j++){
                    //values[i][j] = (int)(Math.random()*4);
                    //determine card values
                    value = (int)(Math.random()*100); //random number from 0 to 99
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
                    }
                    
                    //all cards are by default face down
                    flipped[i][j] = false;
               }
          }
          
          //set all values to 0
          for(int i = 0; i < dim; i++){
               sumy[i] = 0;
               bombsy[i] = 0;
               sumx[i] = 0;
               bombsx[i] = 0;
               clearedx[i] = 0;
               clearedy[i] = 0;
          }
          cleared = 0;
          x = 0;
          y = 0;
          
          //find clues for each column and row
          for(int i = 0; i < dim; i++){
               for(int j = 0; j < dim; j++){
                    sumy[i] += values[i][j];
                    if(values[i][j] == 0){
                         bombsy[i]++;
                         bombsx[j]++;
                    }
                    sumx[j] += values[i][j];
               }
          }
     }//end initialize
     
     //draws the game grid with clues
     private void drawGrid()
     {
          //draw cards
          for(int x = 0; x < dim; x++){
               for(int y = 0; y < dim; y++){
                    c.setColor(Color.blue);
                    c.fillRect(x*size+x*space+devx, y*size+space*y+devy, size, size);
                    c.setColor(Color.white);
                    //draw card value if it has been flipped
                    if(!flipped[x][y]) c.drawString("", x*size+x*space+size/2-2+devx, y*size+space*y+size/2+3+devy);
                    else drawNumber(x,y,false);
                    c.setColor(Color.black);
               }
          }
          
          //draw clues
          for(int x = 0; x < dim; x++){
                c.drawString(Integer.toString(sumy[x]), x*size+x*space+size/2-3+devx, dim*size+space*dim+size/2-3+devy);
                c.setColor(Color.gray);
                c.drawString(Integer.toString(bombsy[x]), x*size+x*space+size/2-3+devx, dim*size+space*dim+size/2+10+devy);
                c.setColor(Color.black);
          }
          for(int y = 0; y < dim; y++){
                c.drawString(Integer.toString(sumx[y]), dim*size+dim*space+size/2-8+devx, y*size+space*y+size/2+3+devy);
                c.setColor(Color.gray);
                c.drawString(Integer.toString(bombsx[y]), dim*size+dim*space+size/2+12+devx, y*size+space*y+size/2+3+devy);
                c.setColor(Color.black);
          }
     }//end drawGridGrid
     
     //draws the grid with the cursor
     private void drawPos(int x, int y)
     {
          drawGrid();
          c.setColor(Color.green);
          c.drawRect(x*size+x*space+1+devx, y*size+space*y+1+devy, size-3, size-3);
          c.setColor(Color.black);
     }//end drawPos
     
     //draws the card value on the given location
     public void drawNumber(int x, int y, boolean selected)
     {
          if(selected){ //if the cursor is on that card
               c.setColor(Color.blue);
               c.fillRect(x*size+x*space+devx, y*size+space*y+devy, size, size);
               c.setColor(Color.green);
               c.drawRect(x*size+x*space+1+devx, y*size+space*y+1+devy, size-3, size-3);
          }
          if(values[x][y] == 0) c.setColor(Color.gray);
          if(values[x][y] == 1) c.setColor(Color.yellow);
          if(values[x][y] == 2) c.setColor(Color.orange);
          if(values[x][y] == 3) c.setColor(Color.red);
          if(values[x][y] > 0) //draw a dot if the card is a bomb
               c.drawString(Integer.toString(values[x][y]), x*size+x*space+size/2-2+devx, y*size+space*y+size/2+3+devy);
          else c.fillOval(x*size+x*space+devx+size/2-5, y*size+space*y+devy+size/2-5, 10, 10);
          c.setColor(Color.black);
     }//end drawNumber
     
     //receives user input the move cursor and select cards
     public void select() throws InterruptedException
     {
          char input;
          input = c.getChar(); //receive input
          switch(input){
               case 'w':
                    //move cursor up
                    y--;
                    y = constrain(y,0,dim-1);
                    drawPos(x,y);
                    break;
               case 's':
                    //move cursor down
                    y++;
                    y = constrain(y,0,dim-1);
                    drawPos(x,y);
                    break;
               case 'a':
                    //move cursor left
                    x--;
                    x = constrain(x,0,dim-1);
                    drawPos(x,y);
                    break;
               case 'd':
                    //move cursor right
                    x++;
                    x = constrain(x,0,dim-1);
                    drawPos(x,y);
                    break;
               case '\n':
                    //select card
                    drawPos(x,y);
                    drawNumber(x,y,true);
                    
                    //check if bomb was flipped
                    if(flipped[x][y] == false && values[x][y] == 0) lives--;
                    
                    //check if row or column is cleared
                    if(flipped[x][y] == false && values[x][y] > 0){
                         clearedx[y]++;
                         clearedy[x]++;
                    }
                    flipped[x][y] = true; //flip card
                    if(clearedx[y] == dim-bombsx[y]){
                         cleared++;
                         score += level;
                         clearedx[y] = 0;
                    }
                    if(clearedy[x] == dim-bombsy[x]){
                         cleared++;
                         score += level;
                         clearedy[x] = 0;
                    }
                    
                    //check if entire grid is clear
                    if(cleared == 2*dim) setLevel(level+1, true);  
                    break;
          }//end switch
     }//end select
     
     //sets the level re-initializes card values and redraws the grid
     public void setLevel(int lvl, boolean levelUp) throws InterruptedException
     {
          if(levelUp){ //if level is increasing
               c.setCursor(1,33);
               c.print("Level Cleared!");
               c.setCursor(25,33);
               c.print("Level Cleared!");
               c.setCursor(12,1);
               c.print("Level Cleared!");
               c.setCursor(12,66);
               c.print("Level Cleared!");
               Thread.sleep(2000); //wait for 2 seconds
          }
          c.clearRect(0, devy, c.getWidth(), c.getHeight()); //clear grid area
          level = constrain(lvl, 1, lvl); //update level
          dim = level+4; //update grid dimensions
          dim = constrain(dim, 5, 9); 
          initialize(); //re-initialize game values
          drawPos(0,0); //re-draw grid
     }//end setLevel
     
     //restarts the game from beginning
     public void reset() throws InterruptedException
     {
          score = 0;
          lives = 6;
          setLevel(1, false);
     }//end reset
     
     //returns the current level
     public int getLevel()
     {
          return level;
     }//end getPoints
     
     //returns the number of points
     public int getScore()
     {
          return score;
     }//end getPoints
     
     //returns the number of lives remaining
     public int getLives()
     {
          return lives;
     }//end getPoints
     
     //used to stop the cursor from going out of index
     private int constrain(int val, int min, int max)
     {
          if(val < min) return min;
          else if(val > max) return max;
          else return val;
     }//end constrain
}//end class