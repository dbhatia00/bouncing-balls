package bouncingballs;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private Pane canvas = new Pane();
    private Scene scene = new Scene(canvas, 1300, 800);
    private List<Circle> activeObjects = Collections.synchronizedList(new ArrayList<Circle>());
    private List<Boolean> toFlip = Collections.synchronizedList(new ArrayList<Boolean>());
    private int numObjects = 50;
    private double mouseX;
    private double mouseY;
    private List<Timeline> timelines = Collections.synchronizedList(new ArrayList<>());
    private boolean circleOrConfetti = false;


    /* 
     * A function to handle creating the timeline of a single Circle
     * IN:  Circle object
     *      double dx
     *      double dy
     *      int index
     * OUT: void
    */
    public Timeline handleBallTimeline(Circle ball, final double dx, final double dy, final int index){
        
        Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            double deltaX = dx;
            double deltaY = dy;
            
            @Override
            public void handle(final ActionEvent t) {
                // Update position
                ball.setLayoutX(ball.getLayoutX() + deltaX);
                ball.setLayoutY(ball.getLayoutY() + deltaY);

                // Set bounds variables
                final Bounds bounds = canvas.getBoundsInLocal();
                final boolean atRightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius());
                final boolean atLeftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius());
                final boolean atBottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius() - 20);
                final boolean atTopBorder = ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius() + 20);

                // Do border logic
                if (atRightBorder || atLeftBorder) {
                    deltaX *= -1;
                }
                if (atBottomBorder || atTopBorder) {
                    deltaY *= -1;
                }

                // Do mouseclick logic
                if (toFlip.get(index)){
                    deltaX = doMouseclickMathBalls_X(ball.getLayoutX(), ball.getLayoutY(), Math.sqrt(Math.pow(deltaY, 2) + Math.pow(deltaX, 2)));
                    deltaY = doMouseclickMathBalls_Y(ball.getLayoutX(), ball.getLayoutY(), Math.sqrt(Math.pow(deltaY, 2) + Math.pow(deltaX, 2)));
                    toFlip.set(index, false);
                }
            }
        }));

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
        return loop;
    }

        /* 
     * A function to handle creating the timeline of a single Circle
     * IN:  Circle object
     *      double dx
     *      double dy
     *      int index
     * OUT: void
    */
    public Timeline handleConfettiTimeline(Circle ball, final double dx, final double dy, final int index){
        
        Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            double deltaX = dx;
            double deltaY = dy;
            
            @Override
            public void handle(final ActionEvent t) {
                // Update position
                ball.setLayoutX(ball.getLayoutX() + deltaX);
                ball.setLayoutY(ball.getLayoutY() + deltaY);

                // Set bounds variables
                final Bounds bounds = canvas.getBoundsInLocal();
                final boolean atRightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius() - 20);
                final boolean atLeftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius() + 20);
                final boolean atBottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius() - 20);
                final boolean atTopBorder = ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius() + 20);
                
                // Slow down the confetti until it hits zero
                if(deltaX > 0){
                    deltaX -= 0.1;
                } else {
                    deltaX += 0.1;
                }

                if(deltaY > 0){
                    deltaY -= 0.1;
                } else {
                    deltaY += 0.1;
                }

                // Border switch logic
                if (atRightBorder || atLeftBorder) {
                    deltaX *= -1;
                }
                if (atBottomBorder || atTopBorder) {
                    deltaY *= -1;
                }

                // Do the mouseclick away operation
                if (toFlip.get(index)){
                    deltaX = doMouseclickMathConfetti_X(ball.getLayoutX(), ball.getLayoutY());
                    deltaY = doMouseclickMathConfetti_Y(ball.getLayoutX(), ball.getLayoutY());
                    toFlip.set(index, false);
                }
            }
        }));

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
        return loop;
    }

    /* 
     * A function to do the math on the mouseclick for the X component (Confetti)
     * IN:  double currentX
     *      double currentY
     * OUT: double deltaX 
    */
    private double doMouseclickMathConfetti_X(double currentX, double currentY){
        double angle = Math.atan(Math.abs(currentY - mouseY) / Math.abs(currentX - mouseX));
        if (currentX > mouseX){
            return 10 * Math.sin(angle);
        }
        else {
            return -10 * Math.sin(angle);
        }
    }

    /* 
     * A function to do the math on the mouseclick for the Y component (Confetti)
     * IN:  double currentX
     *      double currentY
     * OUT: double deltaY 
    */
    private double doMouseclickMathConfetti_Y(double currentX, double currentY){
        double angle = Math.atan(Math.abs(currentY - mouseY) / Math.abs(currentX - mouseX));
        if (currentY > mouseY){
            return 10 * Math.cos(angle);
        }
        else {
            return -10 * Math.cos(angle);
        }
    }

    /* 
     * A function to do the math on the mouseclick for the X component (Balls)
     * IN:  double currentX
     *      double currentY
     *      double magnitude
     * OUT: double deltaX 
    */
    private double doMouseclickMathBalls_X(double currentX, double currentY, double magnitude){
        double angle = Math.atan(Math.abs(currentY - mouseY) / Math.abs(currentX - mouseX));
        if (currentX > mouseX){
            return magnitude * Math.sin(angle);
        }
        else {
            return -magnitude * Math.sin(angle);
        }
    }


    /* 
     * A function to do the math on the mouseclick for the Y component
     * IN:  double currentX
     *      double currentY
     *      double magnitude
     * OUT: double deltaY 
    */
    private double doMouseclickMathBalls_Y(double currentX, double currentY, double magnitude){
        double angle = Math.atan(Math.abs(currentY - mouseY) / Math.abs(currentX - mouseX));
        if (currentY > mouseY){
            return magnitude * Math.cos(angle);
        }
        else {
            return -magnitude * Math.cos(angle);
        }
    }


    /* 
     * A function to randomly generate a ball's radius. 
     * Restricted to being between 40 and 10.
     * IN:  Void
     * OUT: double radius 
    */
    private double generateRadius(){
        Random rand = new Random();
        return rand.nextInt(40 - 10) + 10;
    }


    /* 
     * A function to randomly generate a ball's color
     * IN:  Void
     * OUT: Color object
    */
    private Color generateColor(){
        Random rand = new Random();
        return Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    }


    /* 
     * A function to calculate the speed of an object based on its radius
     * IN:  Circle object
     * OUT: double delta 
    */
    private double calculateInitialDelta(Circle ball){
        return (1 / ball.getRadius()) * 30 ;
    }


    /* 
     * A function to randomly calculate a start position for a ball
     * IN:  void
     * OUT: int
    */
    private int calculateInitialStart(){
        Random rand = new Random();
        return rand.nextInt((int) scene.getHeight());
    }

    /* 
     * A function to put the balls on the canvas and start their timelines
     * IN:  void
     * OUT: void
    */
    private void addBalls() {
        for (int i = 0; i < numObjects; i++) {
            Circle ball = new Circle(generateRadius(), generateColor());
            ball.relocate(calculateInitialStart(), calculateInitialStart());
            activeObjects.add(ball);
            toFlip.add(false);
            canvas.getChildren().add(ball); 
        }
    
        ExecutorService executorService = Executors.newFixedThreadPool(numObjects);
        for (int i = 0; i < numObjects; i++) {
            final int index = i;
            executorService.execute(() -> {
                Timeline timeline;
                synchronized (timelines) {
                    timeline = handleBallTimeline(activeObjects.get(index), calculateInitialDelta(activeObjects.get(index)), calculateInitialDelta(activeObjects.get(index)), index);
                    timelines.add(timeline);
                }
            });
        }
        executorService.shutdown();
    }

    /* 
     * A function to put the balls on the canvas and start their timelines
     * IN:  void
     * OUT: void
    */
    private void addConfetti() {
        for (int i = 0; i < numObjects; i++) {
            Circle ball = new Circle(5, generateColor());
            ball.relocate(calculateInitialStart(), calculateInitialStart());
            activeObjects.add(ball);
            toFlip.add(false);
            canvas.getChildren().add(ball); // Adding to the canvas
        }
    
        ExecutorService executorService = Executors.newFixedThreadPool(numObjects);
        for (int i = 0; i < numObjects; i++) {
            final int index = i;
            executorService.execute(() -> {
                Timeline timeline;
                synchronized (timelines) {
                    timeline = handleConfettiTimeline(activeObjects.get(index), 0, 0, index);
                    timelines.add(timeline);
                }
            });
        }
        executorService.shutdown();
    }
    
    /* 
     * A function to generate the number of objects from processes
     * IN:  void
     * OUT: void
    */
    private void numObjects(){
        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " + 
        Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " + 
            Runtime.getRuntime().freeMemory());

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " + 
            (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently available to the JVM */
        System.out.println("Total memory available to JVM (bytes): " + 
            Runtime.getRuntime().totalMemory());

        // Tether the number of balls to the amount of free memory available (in MB)
        numObjects = (int)(Runtime.getRuntime().freeMemory() / (1e6) / 3);
    }

    /* 
     * A function to initialize all of the variables and kick off the timelines
     * IN:  Stage object
     * OUT: void
    */
    @Override
    public void start(Stage stage) {        
        
        // Figure out how many objects to generate
        numObjects();

        // Handle the initial ball setup logic
        addBalls();

        // Add swap button to scene
        Button swapButton = new Button();
        swapButton.setText("Swap Objects!");
        swapButton.setOnAction( event -> {
            // Stop each timeline
            synchronized (timelines) {
                for (Timeline timeline : timelines) {
                    timeline.stop();
                }
                timelines.clear();
            }

            //Remove each circle from the canvas
            for (Circle circle : activeObjects) {
                canvas.getChildren().remove(circle);
            }

            //clear objects lists for efficiency
            activeObjects.clear();
            toFlip.clear();
            timelines.clear();

            //Switch object types
            if (!circleOrConfetti) {
                circleOrConfetti = true;
                addConfetti();
            } else {
                circleOrConfetti = false;
                addBalls();
            }
        });
        swapButton.setLayoutX(150);
        swapButton.setLayoutY(120);
        canvas.getChildren().add(swapButton);

        // Set scene parameters and show screen
        stage.setTitle("Moving Ball");
        stage.setScene(scene);
        stage.show();

        // Handle mouseclick logic
        scene.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                mouseX = event.getX();
                mouseY = event.getY();
                // Move circles away from the mouse click
                for(int i = 0; i < numObjects; i++){
                    toFlip.set(i, true);
                }
            }
        });
    }
    
    public static void main(String[] args) {
        launch();
    }

}