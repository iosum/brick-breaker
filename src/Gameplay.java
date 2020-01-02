import java.util.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.*;

import javax.swing.*;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;

    private int totalBricks = 48;

    private Timer timer;
    private int delay = 8;

    private int slider = 310;

    private int ballPositionX = 120;
    private int ballPositionY = 350;
    private int ballXDirection = -1;
    private int ballYDirection = -2;

    private MapGenerator map;

    public Gameplay() {
        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // drawing map
        // convert Graphics g to Graphics2D g
        map.draw((Graphics2D) g);

        // borders
        g.setColor(Color.black);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // the scores
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score = " + score, 500, 30);

        // the paddle
        g.setColor(Color.green);
        g.fillRect(slider, 550, 100, 8);

        // the ball
        g.setColor(Color.yellow);
        g.fillOval(ballPositionX, ballPositionY, 20, 20);

        // when you won the game
        if (totalBricks <= 0) {
            play = false;
            ballXDirection = 0;
            ballYDirection = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won", 260, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press (Enter) to Restart", 230, 350);
        }

        // when you lose the game
        // the ball falls out of window
        if (ballPositionY > 570) {
            play = false;
            ballXDirection = 0;
            ballYDirection = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over, Scores: " + score, 190, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press (Enter) to Restart", 230, 350);
        }

        g.dispose();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (slider >= 600) {
                slider = 600;
            } else {
                moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (slider < 10) {
                slider = 10;
            } else {
                moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballPositionX = 120;
                ballPositionY = 350;
                ballXDirection = -1;
                ballYDirection = -2;
                slider = 310;
                score = 0;
                totalBricks = 21;
                map = new MapGenerator(3, 7);

                // It controls the update() -> paint() cycle
                // when increasing / decreasing the value of slider (pressing the right or left keyboard to move the slider)
                // we need to redraw it
                // call repaint() to get a component to repaint itself.
                repaint();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void moveRight() {
        play = true;
        slider += 20;
    }

    public void moveLeft() {
        play = true;
        slider -= 20;
    }

    public void actionPerformed(ActionEvent e) {
        timer.start();

        // if the user are playing
        if (play) {

            // interacting with the slider
            // create a new rectangle object for the ball (because the ball is round) to detect if there is a slider
            if (new Rectangle(ballPositionX, ballPositionY, 20, 20).intersects(new Rectangle(slider, 550, 30, 8))) {
                ballYDirection = -ballYDirection;
                ballXDirection = -2;
            }


            else if (new Rectangle(ballPositionX, ballPositionY, 20, 20).intersects(new Rectangle(slider + 70, 550, 30, 8))) {
                ballYDirection = -ballYDirection;
                ballXDirection = ballXDirection + 1;
            }

            else if (new Rectangle(ballPositionX, ballPositionY, 20, 20).intersects(new Rectangle(slider + 30, 550, 40, 8))) {
                ballYDirection = -ballYDirection;
            }

            // check map collision with the ball
            // the first map is the object we created in GamePlay
            // the second map is the variable that is in the MapGenerator class
            A:
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        // the position of the balls and bricks
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        // create the rectangle around the brick for detection
                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        // create the rectangle around the ball for detection
                        Rectangle rectangleAroundBall = new Rectangle(ballPositionX, ballPositionY, 20, 20);
                        Rectangle rectangleAroundBrick = rect;

                        if (rectangleAroundBall.intersects(rectangleAroundBrick)) {
                            map.setBrickValue(0, i, j);
                            score += 5;
                            totalBricks--;

                            // when ball hit right or left of brick
                            if (ballPositionX + 19 <= rectangleAroundBrick.x || ballPositionX + 1 >= rectangleAroundBrick.x + rectangleAroundBrick.width) {
                                ballXDirection = -ballXDirection;
                            }
                            // when ball hits top or bottom of brick
                            else {
                                ballYDirection = -ballYDirection;
                            }

                            // add a label A to break the loop that has a label A
                            break A;
                        }
                    }
                }
            }

            ballPositionX += ballXDirection;
            ballPositionY += ballYDirection;

            // if the ball hits the left border
            if (ballPositionX < 0) {
                ballXDirection = -ballXDirection;
            }

            // if the ball hits the top border
            if (ballPositionY < 0) {
                ballYDirection = -ballYDirection;
            }

            // if the ball hits the right border
            if (ballPositionX > 670) {
                ballXDirection = -ballXDirection;
            }

            // update the component
            repaint();
        }
    }
}
