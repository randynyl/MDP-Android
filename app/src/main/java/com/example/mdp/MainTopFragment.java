package com.example.mdp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainTopFragment extends Fragment {
    private IMainActivity mIMainActivity;
    MazeCell[][] mazeGrid = new MazeCell[20][15];
    ImageView[][] mazeImage = new ImageView[20][15];
    private static final int UNEXPLORED = 0, EXPLORED = 1, OBSTACLE = 2, ROBOT_MIDPOINT = 3;
    private static final String UP = "up", DOWN = "down", LEFT = "left", RIGHT = "right";
    //for robotMidPoint, y = row, x = col
    MazeCell robotMidPoint = new MazeCell(18,1,ROBOT_MIDPOINT);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_top, container, false);

        for (int row=0; row<20; row++) {
            for (int col=0; col<15; col++) {
                mazeGrid[row][col] = new MazeCell(row, col, UNEXPLORED);

                int viewId = this.getResources().getIdentifier("r" + row + "c" + col, "id", getActivity().getPackageName());
                mazeImage[row][col] = view.findViewById(viewId);
            }
        }

        int robotMidPointX = robotMidPoint.getxCoord();
        int robotMidPointY = robotMidPoint.getyCoord();
        mazeGrid[robotMidPointY][robotMidPointX].setState(ROBOT_MIDPOINT);

        int gridNumbering = 1;

        for(int y=robotMidPointY-1; y<=robotMidPointY+1; y++) {
            for(int x=robotMidPointX-1; x<=robotMidPointX+1; x++) {
                if(x==robotMidPointX && y==robotMidPointY){
                    int imgId = getResources().getIdentifier("robot_" + gridNumbering, "drawable", getActivity().getPackageName());
                    mazeImage[y][x].setImageResource(imgId);
                }
                else {
                    mazeGrid[y][x].setState(EXPLORED);
                    int imgId = getResources().getIdentifier("robot_" + gridNumbering, "drawable", getActivity().getPackageName());
                    mazeImage[y][x].setImageResource(imgId);
                }
                gridNumbering++;
            }
        }

        return view;
    }

    public void moveRobot (String direction) {
        if(direction == UP) {
            if (robotMidPoint.getyCoord() != 1) {
                int oldxCoord = robotMidPoint.getxCoord();
                int oldyCoord = robotMidPoint.getyCoord();
                robotMidPoint.setyCoord(oldyCoord - 1);
                updateRobotImages(UP, oldxCoord, oldyCoord);
                mazeGrid[robotMidPoint.getyCoord()][robotMidPoint.getxCoord()].setState(ROBOT_MIDPOINT);
            }
        }

        if(direction == DOWN) {
            if(robotMidPoint.getyCoord()!=18) {
                int oldxCoord = robotMidPoint.getxCoord();
                int oldyCoord = robotMidPoint.getyCoord();
                robotMidPoint.setyCoord(oldyCoord+1);
                updateRobotImages(DOWN, oldxCoord, oldyCoord);
                mazeGrid[robotMidPoint.getyCoord()][robotMidPoint.getxCoord()].setState(ROBOT_MIDPOINT);
            }
        }

        if(direction == LEFT) {
            if(robotMidPoint.getxCoord()!=1) {
                int oldxCoord = robotMidPoint.getxCoord();
                int oldyCoord = robotMidPoint.getyCoord();
                robotMidPoint.setxCoord(oldxCoord-1);
                updateRobotImages(LEFT, oldxCoord, oldyCoord);
                mazeGrid[robotMidPoint.getyCoord()][robotMidPoint.getxCoord()].setState(ROBOT_MIDPOINT);
            }
        }

        if(direction == RIGHT) {
            if(robotMidPoint.getxCoord()!=13) {
                int oldxCoord = robotMidPoint.getxCoord();
                int oldyCoord = robotMidPoint.getyCoord();
                robotMidPoint.setxCoord(oldxCoord+1);
                updateRobotImages(RIGHT, oldxCoord, oldyCoord);
                mazeGrid[robotMidPoint.getyCoord()][robotMidPoint.getxCoord()].setState(ROBOT_MIDPOINT);
            }
        }
    }
    public void updateRobotImages(String direction, int xCoordOld, int yCoordOld) {
        int robotMidPointX = robotMidPoint.getxCoord();
        int robotMidPointY = robotMidPoint.getyCoord();
        int gridNumbering = 1;

        for(int y=yCoordOld-1; y<=yCoordOld+1; y++) {
            for (int x=xCoordOld-1; x<=xCoordOld+1; x++) {
                mazeGrid[y][x].setState(EXPLORED);
                mazeImage[y][x].setImageResource(R.drawable.explored);
            }
        }

        for(int y=robotMidPointY-1; y<=robotMidPointY+1; y++) {
            for(int x=robotMidPointX-1; x<=robotMidPointX+1; x++) {
                if(x==robotMidPointX && y==robotMidPointY){
                    mazeGrid[y][x].setState(ROBOT_MIDPOINT);
                    int imgId = getResources().getIdentifier("robot_" + direction + "_" + gridNumbering, "drawable", getActivity().getPackageName());
                    mazeImage[y][x].setImageResource(imgId);
                }
                else {
                    mazeGrid[y][x].setState(EXPLORED);
                    //int resourceIdRobot = this.getResources().getIdentifier("robot_" + gridNumbering, "string", this.getPackageName());
                    int imgId = getResources().getIdentifier("robot_" + direction + "_" + gridNumbering, "drawable", getActivity().getPackageName());
                    mazeImage[y][x].setImageResource(imgId);
                    //mazeImage[y][x].setImageResource(R.drawable.robot_5);
                }
                gridNumbering++;
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
    }
}

