package com.azizbgboss.game;

public class Car {
    public float x, y;
    public float speed;
    public int angle; // 0-359 degrees, where 0 is north
    public float maxSpeed = 1;

    public Car(int x, int y) {
        this.x = x;
        this.y = y;
        this.speed = 0;
        this.angle = 0;
    }

    public void accelerate() {
        speed += 0.01;
        if (speed > maxSpeed) speed = maxSpeed;
    }

    public void decelerate() {
        speed -= 0.01;
        if (speed < 0) speed = 0;
    }

    public void turnLeft() {
        angle -= 1;
        if (angle < 0) angle += 360;
    }

    public void turnRight() {
        angle += 1;
        if (angle >= 360) angle -= 360;
    }

    public void update() {
        // Update the car's position based on its speed and angle
        x += speed * Math.cos(Math.toRadians(angle));
        y += speed * Math.sin(Math.toRadians(angle));
    }
}