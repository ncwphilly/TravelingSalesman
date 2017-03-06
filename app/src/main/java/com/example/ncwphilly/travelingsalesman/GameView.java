package com.example.ncwphilly.travelingsalesman;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
/**
 * Created by ncwphilly on 2/2/17.
 */

public class GameView extends View {

    protected boolean actionDown = false;
    protected int drawX1, drawY1, drawX2, drawY2;
    private int optionInt;
    private BufferedReader reader = new BufferedReader(new InputStreamReader
            (getResources().getAssets().open("points.txt")));
    Spinner spinner = (Spinner) findViewById(R.id.spinner);
    private ArrayList<Point> tempList;
    private Point[] points;
    private Point[] drawPoints;
    ArrayList<PointLine> PointLines = new ArrayList<PointLine>();
    ArrayList<Point> answer;
    double answerDist = 0;
    int tryCounter = 0;

    /*
    Parse the points from points.txt, and store them into a temporary list.
     */

    public GameView(Context context, String option) throws IOException {
        super(context);
        optionInt = Integer.parseInt(option);
        setBackgroundResource(R.drawable.campus);
        points = new Point[optionInt];
        tempList = new ArrayList<Point>();
        Random random = new Random();
        while(reader.ready()) {
            String coordinates = reader.readLine();
            String[] sub = coordinates.split(" ", 2);
            int xC = (int) Double.parseDouble(sub[0]);
            int yC = (int) Double.parseDouble(sub[1]);
            Point p = new Point(xC, yC);
            tempList.add(p);
        }
        points = tempList.toArray(new Point[tempList.size()]);

        /*
        Put random points from the points array into drawPoints array according to the optionInt.
        And gets the correct TravelingSalesman path answer from ShortestPath.java.
         */
        drawPoints = new Point[optionInt];
        for (int i = 0; i < optionInt; i++) {
            int r = random.nextInt(points.length);
            Point point = points[r];
            while (point == null) {
                r = random.nextInt(points.length);
                point = points[r];
            }
            points[r] = null;
            drawPoints[i] = new Point(point);
        }
        answer = ShortestPath.shortestPath(drawPoints);
        Iterator<Point> answerIt = answer.iterator();
        Point pBegin = null;
        Point p1 = null;
        Point p2;
        if (answerIt.hasNext()) {
            pBegin = answerIt.next();
            p1 = pBegin;
        }
        while (answerIt.hasNext()) {
            p2  = answerIt.next();
            answerDist += ShortestPath.dist(p1, p2);
            p1 = p2;
        }
        answerDist += ShortestPath.dist(p1, pBegin);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setStrokeWidth(30);
        p.setColor(Color.GREEN);

        //DRAWS THE POINTS
        for (int i = 0; i < drawPoints.length; i++) {
            Point pb = drawPoints[i];
            canvas.drawPoint((float) pb.x, (float) pb.y, p);
        }

        //DRAWS THE PREVIEW LINE
        if (actionDown) {
            //      preview = (drawX1, drawY1, drawX2, drawY2);
            p.setColor(Color.CYAN);
            p.setStrokeWidth(10);
            canvas.drawLine(drawX1, drawY1, drawX2, drawY2, p);

            //IF THE PREVIEW LINE POINTS ARE "AROUND" 2 MAP POINTS, DRAW A LINE BETWEEN THEM
        } else {
            Point p1 = null;
            Point p2 = null;
            for (int i = 0; i < drawPoints.length; i++) {
                if ((Math.abs(drawPoints[i].x - drawX1) < 30)
                        && Math.abs(drawPoints[i].y - drawY1) < 30) {
                    p1 = drawPoints[i];
                }
            }
            for (int i = 0; i < drawPoints.length; i++) {
                if ((Math.abs(drawPoints[i].x - drawX2) < 30)
                        && Math.abs(drawPoints[i].y - drawY2) < 30) {
                    p2 = drawPoints[i];
                }
            }

            drawX2 = Integer.MAX_VALUE;
            drawY1 = Integer.MAX_VALUE;

            //Add the PointLine to the list of PointLines.
            if (p1 != p2 && p1 != null && p2 != null) {
                PointLines.add(new PointLine(p1, p2));
                if (PointLines.size() == optionInt) {
                    double yourDist = 0;
                    Iterator<PointLine> PointLineIterator = PointLines.iterator();
                    if (PointLineIterator.hasNext()) {
                        PointLine PointLine = PointLineIterator.next();
                        p1 = PointLine.getP1();
                        p2 = PointLine.getP2();
                        yourDist += ShortestPath.dist(p1, p2);
                    }

                    //Calculate total distance and check if it is identical to the answerDist.
                    while (PointLineIterator.hasNext()) {
                        PointLine PointLine = PointLineIterator.next();
                        p1 = PointLine.getP1();
                        p2 = PointLine.getP2();
                        yourDist += ShortestPath.dist(p1, p2);
                    }

                    /*
                      A winning case. Distance is adjusted for accuracy since some
                      accuracy is lost.
                    */
                    if ((Math.abs(answerDist - yourDist) < .00001)) {
                        Toast.makeText(getContext(), "You Win!", Toast.LENGTH_LONG).show();
                    } else {
                        if (tryCounter < 2) {
                            Toast.makeText(getContext(),
                                    "This is not the right path. Off by " + Math.round((100 * (yourDist - answerDist) / answerDist)) + "%." + " Clear the map and try again.",
                                    Toast.LENGTH_LONG).show();
                            tryCounter++;
                        } else {
                            Iterator<Point> answerIt = answer.iterator();
                            Point pb1 = null;
                            p.setColor(Color.CYAN);
                            p.setStrokeWidth(10);
                            PointLines.clear();
                            if (answerIt.hasNext()) {
                                pb1 = answerIt.next();
                                drawX1 = pb1.x;
                                drawY1 = pb1.y;
                            }
                            while (answerIt.hasNext()) {
                                Point pb2 = answerIt.next();
                                drawX2 = pb2.x;
                                drawY2 = pb2.y;
                                canvas.drawLine(drawX1, drawY1, drawX2, drawY2, p);
                                drawX1 = drawX2;
                                drawY1 = drawY2;
                            }
                            canvas.drawLine(drawX1, drawY1, pb1.x, pb1.y, p);
                            Toast.makeText(getContext(),
                                    "This is the correct answer. Go to the menu to quit.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
        p.setColor(Color.RED);
        p.setStrokeWidth(10);
        Iterator<PointLine> PointLineIterator = PointLines.iterator();
        while (PointLineIterator.hasNext()) {
            PointLine pbl = PointLineIterator.next();

            canvas.drawLine(pbl.getP1().x, pbl.getP1().y,
                    pbl.getP2().x, pbl.getP2().y, p);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (e.getAction() == MotionEvent.ACTION_DOWN) {

            drawX1 = (int) e.getX();
            drawY1 = (int) e.getY();
            actionDown = true;
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            drawX2 = (int) e.getX();
            drawY2 = (int) e.getY();
            invalidate();
            return false;
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            actionDown = false;
            invalidate();
        }
        return true;
    }

    public ArrayList<PointLine> getPointLines() {
        return PointLines;
    }

}
