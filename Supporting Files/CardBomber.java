/*
 * CardBomber
 * Created by Daniel Liang
 * Date: 2016/06/11
 * Purpose: Summative - the main file for the computer game. The main menu features are here.
 */

import hsa.Console;
import java.awt.*;
import java.io.*;

public class CardBomber 
{
     static Console c;
     static PrintWriter write;
     static BufferedReader read;
     static int highScore;
     public static void main(String args[]) throws IOException, InterruptedException
     {    
          c = new Console("Card Bomber"); //construct the console
          read = new BufferedReader(new FileReader("HighScore.txt")); //open the file for reading
          highScore = Integer.parseInt(read.readLine()); //read the previous high score
          
          //display the main menu
          drawMenu();
          
          //begin the game
          GameGrid grid = new GameGrid(c); //construct the grid
          while(true){ //main game loop
               //print game stats
               c.setCursor(1,1);
               c.println("Level " + grid.getLevel());
               c.println("Score: " + grid.getScore());
               if(grid.getScore() > highScore){
                    highScore = grid.getScore();
                    c.setTextColor(Color.blue);
               }
               c.println("High Score: " + highScore);
               c.setTextColor(Color.black);
               if(grid.getLives() < 3) c.setTextColor(Color.red);
               c.println("Lives: " + grid.getLives());
               c.setTextColor(Color.black);
               
               //user selects card, continue to execute game
               grid.select();
               
               //player has run out of lives. End game
               if(grid.getLives() == 0){
                    c.setCursor(4,1);
                    c.setTextColor(Color.red);
                    c.print("Lives: " + grid.getLives());
                    c.setTextColor(Color.black);
                    if(endGame()) break; //break out of game loop
                    else grid.reset(); //play again
               }
          }//end while
          
          //write new high score, close files and console
          read.close();
          write = new PrintWriter(new FileWriter("HighScore.txt")); //open writer
          write.print(highScore); //save high score
          write.close();
          c.close();
     }//end main
     
     //Used to constrain the value of a variable so that is does not exceed the acceptable range and cause an error
     public static int constrain(int val, int min, int max)
     {
          if(val < min) return min;
          else if(val > max) return max;
          else return val;
     }//end constrain
     
     //Draws the title of the game, game logo and menu
     public static void drawMenu()
     {
          //output title top centered and company name bottom centered
          c.setCursor(1,34);
          c.print("Card Bomber");
          c.setCursor(25,24);
          c.print("Presented by Grid Gaming Company");
          
          //draw game logo
          //variables for positioning of logo
          int width = 100;
          int height = 140;
          int x = 235;
          int y = 80;
          int xspace = 14;
          int yspace = 10;
          
          //Colors of the cards
          Color[] color = new Color[3];
          color[0] = new Color(255,0,0);
          color[1] = new Color(255,128,0);
          color[2] = new Color(255,255,0);
          
          //draw normal cards
          for(int i = 0; i < 5; i++){
               c.setColor(color[i%3]);
               c.fillRect(x+i*xspace,y+i*yspace,width,height);
          }
          
          //draw bomb card
          c.setColor(Color.gray);
          c.fillRect(325,145,width,height);
          c.setColor(Color.black);
          c.fillOval(340,180,70,70);
          
          //draw menu and begin menu selection
          menuSelect();
     }//end drawMenu
     
     //draws the menu and allows user to interact with the main menu
     public static void menuSelect()
     {
          //variables for the positioning of the menu
          int y = 0;
          int dim = 3;
          int devx = 270;
          int devy = 300;
          int sizex = 100;
          int sizey = 20;
          int space = 10;
          char input = '0'; //stores the keystroke of the user
          
          //draw the menu
          for(int i = 0; i < dim; i++){
               c.drawRect(devx, devy+i*space+i*sizey, sizex, sizey);
          }
          c.drawString("Start", devx+37, devy+15);
          c.drawString("Instructions", devx+21, devy+sizey+space+15);
          c.drawString("High Score", devx+24, devy+2*(sizey+space)+15);
          
          //draw the cursor
          c.setColor(Color.green);
          c.drawRect(devx, devy, sizex, sizey);
          c.setColor(Color.black);
          
          //allow cursor movement until user chooses an option
          while(input != '\n'){ 
               input = c.getChar(); //input user's keystroke
               switch(input){
                    case 'w':
                         //move the cursor up
                         c.drawRect(devx, devy+y*space+y*sizey, sizex, sizey);
                         c.setColor(Color.green);
                         y--;
                         y = constrain(y,0,2);
                         c.drawRect(devx, devy+y*space+y*sizey, sizex, sizey);
                         c.setColor(Color.black);
                         break;
                    case 's':
                         //move the cursor down
                         c.drawRect(devx, devy+y*space+y*sizey, sizex, sizey);
                         c.setColor(Color.green);
                         y++;
                         y = constrain(y,0,2);
                         c.drawRect(devx, devy+y*space+y*sizey, sizex, sizey);
                         c.setColor(Color.black);
                         break;
                    case '\n':
                         //select an option
                         c.clear();
                         if(y == 1) drawInstructions();
                         else if(y == 2) drawHighScore();
                         break;
               }//end switch
          }//end while
     }//end menuSelect
     
     //Displays instructions on how to use the program
     public static void drawInstructions()
     {
          //step 1 text
          c.println("1. Moving the Cursor and selecting\n");
          c.println("Make sure caps lock is off");
          c.println("The cursor on the game grid or menu is represented by green rectangle");
          c.println("Press W to move the cursor up");
          c.println("Press S to move the cursor down");
          c.println("Press A to move the cursor left");
          c.println("Press D to move the cursor right");
          c.println("Press enter to flip the card\n");
          c.print("Press any key to continue");
          
          //step 1 images
          c.drawRect(100,300,50,50);
          c.drawRect(100,350,50,50);
          c.drawRect(50,350,50,50);
          c.drawRect(150,350,50,50);
          c.drawRect(400,350,120,50);
          c.drawString("W",110,320);
          c.drawString("S",110,370);
          c.drawString("A",60,370);
          c.drawString("D",160,370);
          c.drawString("enter",410,370);
          int[][] xPoints = new int[][]{
               {125,115,135},{115,135,125},{75,75,65},{175,175,185}
          };
          int[][] yPoints = new int[][]{
               {325,335,335},{375,375,385},{365,385,375},{365,385,375}
          };
          for(int i = 0; i < xPoints.length; i++){
               c.fillPolygon(xPoints[i], yPoints[i], xPoints[i].length);
          }
          c.getChar();
          
          //step 2 text
          c.clear();
          c.println("2. Clues\n");
          c.println("Each card can have a value of 1, 2, 3 or a bomb (0)");
          c.println("Black numbers show the total of card values in that row or column");
          c.println("Gray numbers show the number of bombs in that row or column\n");
          c.print("Press any key to continue");
          
          //step 2 image
          GameGrid example = new GameGrid(0,200,30,10,c); //draw an example GameGrid
          for(int i = 0; i < 5; i++){
               for(int j = 0; j < 5; j++){
                    example.drawNumber(i,j,false);
               }
          }
          c.getChar();
          
          //step 3 text
          c.clear();
          c.println("3. Basics of the game\n");
          c.println("The main objective of the game is to flip all cards except for bombs");
          c.println("When all cards except bombs in a row or column is flipped, a point is scored");
          c.println("The point is multiplied by the level number (e.g. 2 points for level 2)");
          c.println("There are 6 lives at the beginning of the game");
          c.println("When a bomb is flipped, a life is deducted");
          c.println("When the entire grid is clear, the game advances to the next level");
          c.println("Each increase in level will add a row and column to the grid, capped at 9x9");
          c.println("The game is over when there are no lives remaining\n");
          c.print("Press any key to return to the menu");
          c.getChar();
          
          //return to the main menu
          c.clear();
          drawMenu();
     }//end drawInstructions
     
     //Displays high score on screen
     public static void drawHighScore()
     {
          //display high score
          c.println("High Score: " + highScore);
          c.print("\nPress any key to return to the menu");
          c.getChar();
          
          //return to menu
          c.clear();
          drawMenu();
     }//end drawHighScore
     
     //provides options to quit or restart game
     public static boolean endGame() throws InterruptedException
     {
          //variables for the end game menu (positioning and dimensions)
          int y = 0;
          int devx = 270;
          int devy = 100;
          int sizex = 100;
          int sizey = 20;
          int space = 10;
          char input = '0'; //stores the keystroke of the user
          boolean quit = false; //if user wants to quit
          
          //display menu text
          c.setCursor(1,37);
          c.print("Game Over");
          c.setCursor(25,37);
          c.print("Game Over");
          c.setCursor(12,1);
          c.print("Game Over");
          c.setCursor(12,71);
          c.print("Game Over");
          Thread.sleep(2000); //wait 2 seconds
          c.clearRect(0,100,c.getWidth(),c.getHeight()); //clear the game grid
          c.setCursor(3,36);
          c.print("Play again?");
          c.drawString("Yes", devx+39, devy+15);
          c.drawString("No", devx+40, devy+sizey+space+15);
          
          //draw the options
          c.drawRect(devx, devy, sizex, sizey);
          c.drawRect(devx, devy+space+sizey, sizex, sizey);
          
          //draw the cursor
          c.setColor(Color.green);
          c.drawRect(devx, devy, sizex, sizey);
          c.setColor(Color.black);
          
          //begin reading user's input until an option is selected
          while(input != '\n'){ 
               input = c.getChar(); //input user's keystroke
               switch(input){
                    case 'w':
                         //move the cursor up
                         c.drawRect(devx, devy+y*space+y*sizey, sizex, sizey);
                         c.setColor(Color.green);
                         y = 0;
                         c.drawRect(devx, devy+y*space+y*sizey, sizex, sizey);
                         c.setColor(Color.black);
                         break;
                    case 's':
                         //move the cursor down
                         c.drawRect(devx, devy+y*space+y*sizey, sizex, sizey);
                         c.setColor(Color.green);
                         y = 1;
                         c.drawRect(devx, devy+y*space+y*sizey, sizex, sizey);
                         c.setColor(Color.black);
                         break;
                    case '\n':
                         //select an option
                         c.clear(); //clear screen, stay in main game loop
                         if(y == 1) quit = true;
                         break;
               }//end switch
          }//end while
          return quit;
     }//end endGame
}//end class