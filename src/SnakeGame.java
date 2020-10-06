import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Random;

public class SnakeGame extends Application {

    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final int UNIT_SIZE = 40;
    static final int GAME_UNITS = (WIDTH * HEIGHT) / UNIT_SIZE;
    static int SPEED = 200;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int gainedPoints;
    int pointX;
    int pointY;
    static Direction direction = Direction.DOWN;
    boolean running = false;
    AnimationTimer timer;
    Random random;

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        random = new Random();

        VBox vbox = new VBox();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        vbox.getChildren().add(canvas);

        startGame(gc);

        Scene scene = new Scene(vbox, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake");
        primaryStage.show();

        scene.addEventFilter(KeyEvent.KEY_PRESSED, KeyAdapter(primaryStage));

    }


    public void startGame(final GraphicsContext gc) {
        newPoint();
        running = true;


        timer = new AnimationTimer() {
            long tick = 0;


            @Override
            public void handle(long now) {

                if (running) {
                    if (tick == 0) {
                        tick = now;
                        draw(gc);
                        move();
                        checkPoint();
                        checkCollisions();
                        return;
                    } else if (now - tick > 1000000 * SPEED) {

                        tick = now;
                        draw(gc);
                        move();
                        checkPoint();
                        checkCollisions();
                    }
                } else {
                    timer.stop();
                    gameOver(gc);
                    return;
                }

            }

        };
        timer.start();

    }

    public void draw(GraphicsContext gc) {

        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.BLUE);
        gc.fillOval(pointX, pointY, UNIT_SIZE, UNIT_SIZE);

        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                gc.setFill(Color.GREENYELLOW);
                gc.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                gc.setFill(Color.GREEN);
                gc.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

        }

        gc.setFill(Color.RED);
        gc.setFont(new Font("Tahoma", 40));
        String msg = "Score: " + gainedPoints;
        Text text = new Text(msg);
        text.setFont(gc.getFont());
        gc.fillText(msg, (WIDTH - text.getBoundsInLocal().getWidth()) / 2, UNIT_SIZE + (gc.getFont().getSize() / 2));

    }

    public void newPoint() {
        pointX = random.nextInt((int) WIDTH / UNIT_SIZE) * UNIT_SIZE;
        pointY = random.nextInt((int) HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case LEFT:
                x[0] = x[0] - UNIT_SIZE;
                break;
            case RIGHT:
                x[0] = x[0] + UNIT_SIZE;
                break;
            case UP:
                y[0] = y[0] - UNIT_SIZE;
                break;
            case DOWN:
                y[0] = y[0] + UNIT_SIZE;
                break;
        }

    }

    public void checkPoint() {
        if ((x[0] == pointX && y[0] == pointY)) {
            bodyParts++;
            gainedPoints++;
            newPoint();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        if (y[0] < 0) {
            running = false;
        } else if (x[0] < 0) {
            running = false;
        } else if (x[0] > WIDTH) {
            running = false;
        } else if (y[0] > HEIGHT) {
            running = false;
        }

    }

    public void gameOver(GraphicsContext gc) {
        gc.clearRect(0,0,WIDTH,HEIGHT);

        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.RED);
        gc.setFont(new Font("Tahoma", 40));
        String msgScore = "Score: " + gainedPoints;
        Text textScore = new Text(msgScore);
        textScore.setFont(gc.getFont());
        gc.fillText(msgScore, (WIDTH - textScore.getBoundsInLocal().getWidth()) / 2, UNIT_SIZE + (gc.getFont().getSize() / 2));

        gc.setFill(Color.RED);
        gc.setFont(new Font("Chiller", 90));
        String msg = "Game Over";
        Text text = new Text(msg);
        text.setFont(gc.getFont());
        gc.fillText(msg, (WIDTH - text.getBoundsInLocal().getWidth()) / 2, HEIGHT / 2);
    }

    private void resetGame(Stage primaryStage) {

        primaryStage.close();
        SPEED = 200;
        bodyParts = 6;
        gainedPoints = 0;
        direction = Direction.DOWN;
        SnakeGame app = new SnakeGame();
        app.start(primaryStage);

    }


    private EventHandler<KeyEvent> KeyAdapter(final Stage primaryStage) {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                switch (key.getCode()) {
                    case LEFT:
                        if (direction != Direction.RIGHT)
                            direction = Direction.LEFT;
                        break;
                    case RIGHT:
                        if (direction != Direction.LEFT)
                            direction = Direction.RIGHT;
                        break;
                    case UP:
                        if (direction != Direction.DOWN)
                            direction = Direction.UP;
                        break;
                    case DOWN:
                        if (direction != Direction.UP)
                            direction = Direction.DOWN;
                        break;
                    case SPACE:
                        resetGame(primaryStage);
                        break;
                    case Q:
                        SPEED -= 10;
                        System.out.println(SPEED);
                        break;
                    case W:
                        SPEED += 10;
                        System.out.println(SPEED);
                        break;
                }
            }
        };
    }

}
