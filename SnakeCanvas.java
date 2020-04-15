

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.LinkedList;
import java.util.Random;
/** klasa wyswietlajaca plotno gry*/
public class SnakeCanvas extends Canvas implements Runnable, KeyListener {


    public static int BoxHeight = 20;
    public static int BoxWidth = 20;
    public static int GridHeight = 30;
    public static int GridWidth = 30;

    private LinkedList<Point> snake;
    private Point fruit;
    private int direction = Direction.noDirection;
    private int score = 0;
    public String highScore = "empty:0";
    private Thread runThread;
    private Graphics globalGraphics;
    private boolean isPaused = false;
    private boolean isShowedGrid = true;

    /** metoda przywracajaca aplikacje do stanu domyslnego*/
    public void reset() {
        snake.clear();
        snake.add(new Point(3, 4));
        snake.add(new Point(3, 3));
        snake.add(new Point(3, 2));
        snake.add(new Point(3, 1));
        direction = Direction.noDirection;
        score = 0;
    }
    /** metoda rysujaca siatke*/
    public void drawGrid(Graphics g) {
        // rysowanie zewnetrnego prostokata
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0, 0, GridWidth * BoxWidth, GridHeight * BoxHeight);
        //rysowanie wewnetrznych lini pionowych duzego prostokata

        for (int i = BoxWidth; i < GridWidth * BoxWidth; i += BoxWidth) {
            g.drawLine(i, 0, i, BoxHeight * GridHeight);
        }

        //rysowanie wewnetrznych lini poziomych duzego prostokata
        for (int j = BoxHeight; j < GridHeight * BoxHeight; j += BoxHeight) {
            g.drawLine(0, j, BoxWidth * GridWidth, j);
        }
        g.setColor(Color.LIGHT_GRAY);
    }
    /** metoda rysujaca ramke obszaru gry*/
    public void drawFrame(Graphics g){
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0, 0, GridWidth * BoxWidth, GridHeight * BoxHeight);
    }
    /** metoda rysujaca weza*/
    public void drawSnake(Graphics g) {
        g.setColor(Color.BLACK);//kolor węża
        for (Point pkt : snake) {
            g.fillRect(pkt.x * BoxWidth, pkt.y * BoxHeight, BoxWidth, BoxHeight);
        }
        g.setColor(Color.CYAN);
    }
    /** metoda rysujaca owoc*/
    public void drawFruit(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillRect(fruit.x * BoxWidth, fruit.y * BoxHeight, BoxWidth, BoxHeight);
        g.setColor(Color.BLACK);
    }
    /** metoda grupujaca elementy w jedna metode wraz z rysowana siatka*/
    public void draw(Graphics g) {
        //czyszczenie sladow
        g.clearRect(0, 0, BoxWidth * GridWidth + 20, BoxHeight * GridHeight + 20);
        drawGrid(g);
        drawSnake(g);
        drawFruit(g);
        returnScore(g);
    }
    /** metoda grupujaca elementy w jedna metode bez siatki*/
    public void draw2(Graphics g) {
        //czyszczenie sladow
        g.clearRect(0, 0, BoxWidth * GridWidth + 20, BoxHeight * GridHeight + 20);
        drawFrame(g);
        drawSnake(g);
        drawFruit(g);
        returnScore(g);
    }
    /** impelementacja metody paint na srodowysku graficznym, wykorzystujaca watek do odswiezania ekranu*/
    public void paint(Graphics g) {
        this.setPreferredSize(new Dimension(640, 400));
        snake = new LinkedList<Point>();
        reset();
        replaceFruit();
        if (isPaused){

        }
        else {


            globalGraphics = g.create();
            this.addKeyListener(this);
            if (runThread == null) {
                runThread = new Thread(this);
                runThread.start();
            }
            if (highScore.equals("empty:0")) {
                //dodanie highscora
                highScore = this.getHighScore();
            }
        }
    }
    /**klasa kierunku*/
    public class Direction {
        public static final int noDirection =0;
        public static final int north =1;
        public static final int south =2;
        public static final int west =3;
        public static final int east =4;
    }
    /** metoda obslugi kierunku weza*/
    public void move() {
        Point head = snake.peekFirst();
        Point newPoint = head;
        switch (direction) {
            case Direction.north:
                newPoint = new Point(head.x, head.y - 1);
                break;
            case Direction.south:
                newPoint = new Point(head.x, head.y + 1);
                break;
            case Direction.west:
                newPoint = new Point(head.x - 1, head.y);
                break;
            case Direction.east:
                newPoint = new Point(head.x + 1, head.y);
                break;
        }
        snake.remove(snake.peekLast());
        if (newPoint.equals(fruit)) {
            // zjedzeinie owoca
            Point addPoint = (Point) newPoint.clone();
            snake.push(addPoint);
            replaceFruit();
            score = score + 10;
        } else if (newPoint.x < 0 || newPoint.x > (GridWidth - 1)) {
            // wyjscie poza obszar, zakladamy rest gry
            checkHighScore();
            reset();
            return;
        } else if (newPoint.y < 0 || newPoint.y > (GridHeight - 1)) {
            // wyjscie poza obszar, zakladamy rest gry
            checkHighScore();
            reset();
            return;
        } else if (snake.contains(newPoint)) {
            // wyjscie poza obszar, zakladamy rest gry
            checkHighScore();
            reset();
            return;
        }

        snake.push(newPoint);

    }
    /**metoda nowej lokalizacji owocu*/
    public void replaceFruit() {
        Random random = new Random();
        int X = random.nextInt(GridWidth);
        int Y = random.nextInt(GridHeight);
        Point fruitPoint = new Point(X, Y);
        while (snake.contains(fruitPoint)) {
            X = random.nextInt(GridWidth);
            Y = random.nextInt(GridHeight);
            fruitPoint = new Point(X, Y);
        }
        fruit = fruitPoint;
    }


    /**metoda obslugi watku*/
    public void run() {
        while (true) {
            if(!isPaused){
                move();
            }
            if(isShowedGrid) {
                draw(globalGraphics);}
            else {
                draw2(globalGraphics);
            }

// dodanie bufora w celu spowolnienia aplikacji
            try {
                Thread.currentThread();
                Thread.sleep(100);
            }
            // lap kazdy wyjatek
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    /** metoda czytania najwyzszego wyniku*/
    public String getHighScore() {
        FileReader readFile = null;
        BufferedReader reader = null;
        try {
            readFile = new FileReader("highscore.dat");
            reader = new BufferedReader(readFile);
            return reader.readLine();

        } catch (Exception e) {
            return "Master:0";
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /** metoda wyswietlania najwyzszego wyniku*/
    public void returnScore(Graphics g) {
        g.drawString("Score : " + score, 0, BoxHeight * GridHeight + 10);
        g.drawString("Highscore : "+ highScore,0, BoxHeight*GridHeight+20);

    }
    /** metoda sprawdzania najwyzszego wyniku, ewentualnego zapisu*/
    public void checkHighScore() {
        if (highScore.equals("")){
            return;
        }
        if (score > Integer.parseInt((highScore.split(":")[1]))) {
            String name = JOptionPane.showInputDialog("Yeah, You win a new higscore. What is your name ?");
            highScore = name + ':' + score;

            File scoreFile = new File("highscore.dat");
            if (!scoreFile.exists()) {
                try {
                    scoreFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileWriter writeFile = null;
            BufferedWriter writer = null;

            try {
                writeFile = new FileWriter(scoreFile);
                writer = new BufferedWriter(writeFile);

                writer.write(this.highScore);
            }
            catch (Exception e) {
            }
            finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Exception e) {

                }
            }


        }
    }
    /** metoda obslugi zdarzen pusta */
    public void keyTyped(KeyEvent e) {
    }
    /** metoda obslugi zdarzen*/
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if(isPaused)
                    isPaused=false;
                else{
                    isPaused=true;
                }
                break;
            case KeyEvent.VK_G:
                if(isShowedGrid)
                    isShowedGrid=false;
                else{
                    isShowedGrid=true;
                }
                break;
            case KeyEvent.VK_UP:
                if (direction != Direction.south) {
                    direction = Direction.north;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != Direction.north) {
                    direction = Direction.south;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (direction != Direction.east) {
                    direction = Direction.west;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != Direction.west) {
                    direction = Direction.east;
                }
                break;

        }
    }
    /** metoda obslugi zdarzen pusta */
    public void keyReleased(KeyEvent e) {
    }
}

