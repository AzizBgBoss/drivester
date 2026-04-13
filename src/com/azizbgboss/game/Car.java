package com.azizbgboss.game;

public class Car {
    public float x, y;
    public float speed;
    public int angle; // 0-359 degrees, where 0 is north
    public float maxSpeed = 1;

    public float[] cosTable = new float[360];
    public float[] sinTable = new float[360];
    
    private int[][] map;

    public Car(int x, int y, int[][] map) {
        this.x = x;
        this.y = y;
        this.speed = 0;
        this.angle = 0;
        this.map = map;

        for (int i = 0; i < 360; i++) {
            cosTable[i] = (float) (Math.cos(Math.toRadians(i)));
            sinTable[i] = (float) (Math.sin(Math.toRadians(i)));
        }

    }

    public void accelerate() {
        speed += 0.01f;
        if (speed > maxSpeed)
            speed = maxSpeed;
    }

    public void decelerate() {
        speed -= 0.01f;
        if (speed < -maxSpeed)
            speed = -maxSpeed;
    }

    public void turnLeft() {
        angle -= (int) (100 * speed);
        if (angle < 0)
            angle += 360;
    }

    public void turnRight() {
        angle += (int) (100 * speed);
        if (angle >= 360)
            angle -= 360;
    }

    public void update() {
        // Update the car's position based on its speed and angle and check for collisions with the map (2: buildings)
        float futureY = y + speed * sinTable[angle];
        float futureX = x + speed * cosTable[angle];
        if (futureY >= 0 && futureY < map.length && map[(int) futureY][(int) x] != 2) {
            y = futureY;
        }
        if (futureX >= 0 && futureX < map[0].length && map[(int) y][(int) futureX] != 2) {
            x = futureX;
        }
    }
}