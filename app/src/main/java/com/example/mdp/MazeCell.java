package com.example.mdp;

public class MazeCell {
    private int yCoord, xCoord, state;

    public MazeCell(int y, int x, int status) {
        this.yCoord = y;
        this.xCoord = x;
        this.state = status;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() { return yCoord; }

    public int getState() {
        return state;
    }

    public void setState(int status) {
        this.state = status;
    }

    public void resetCell(){
        this.state = 0;
    }

    public void setxCoord(int x) { this.xCoord = x; }

    public void setyCoord(int y) { this.yCoord = y; }
}