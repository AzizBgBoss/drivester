package com.azizbgboss.game;

import java.sql.Time;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

public class Game implements CommandListener {
    private drivester midlet;
    private Canvas canvas;

    private Command gameExit;

    public int[][] map = { // 0 for terrain, 1 for road, 2 building
            { 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 2, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 2, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, // -> North
            { 0, 0, 0, 0, 0, 0, 0, 2, 0, 1 },
            { 0, 0, 0, 0, 0, 1, 1, 1, 0, 1 },
            { 0, 0, 0, 0, 0, 1, 0, 1, 1, 1 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 2 },
    };

    public Car playerCar = new Car(4, 5, map); // Start on the road

    public Game(drivester midlet) {
        this.midlet = midlet;
        canvas = new gameCanvas();

        gameExit = new Command("Exit", Command.EXIT, 0);
        canvas.addCommand(gameExit);
        canvas.setCommandListener(this);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void commandAction(Command c, Displayable d) {
        if (d == canvas) {
            if (c == gameExit) {
                midlet.quit();
            }
        }
    }

    public class gameCanvas extends Canvas {
        final int screenW = getWidth();
        final int screenH = getHeight();
        final int horizonY = (int) (screenH * 0.5f);
        private int scale = 1; // how many pixels per map cell at depth=1
        final int fps = 15;
        final int frameTime = 1000 / fps;

        private void rebuildScreen() {
        }

        public gameCanvas() {
            setFullScreenMode(true);

            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        repaint();
                        playerCar.update();
                        try {
                            Thread.sleep(frameTime);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }).start();
        }

        protected void paint(Graphics g) {
            // Cursed technique: scanline rendering
            // It is used by skilled jujutsu programmers to create powerful illusions that
            // can deceive the senses of their opponents.
            // By manipulating the perception of reality, they can create pseudo 3D effects
            // and immersive environments that can disorient and overwhelm their opponents.
            // Such techniques require a high level of skill and mastery, but is easy on the
            // host machine as it doesn't require complex 3D rendering calculations.

            long startTime = System.currentTimeMillis();

            g.setColor(0x87CEEB); // sky blue
            g.fillRect(0, 0, screenW, horizonY);

            float cosA = playerCar.cosTable[playerCar.angle];
            float sinA = playerCar.sinTable[playerCar.angle];

            float camDist = 1.5f; // how far behind the car
            float camX = playerCar.x - cosA * camDist;
            float camY = playerCar.y - sinA * camDist;

            float screenWH = screenW / 2f; // Cheap way to perform better
            int lastColor = -1;

            for (int py = horizonY; py < screenH; py += scale) {
                float depth = (float) (screenH / 2) / (py - horizonY + 1);

                for (int px = 0; px < screenW; px += scale) {
                    // offset from screen center, scaled by depth
                    float offset = (px - screenWH) * depth / screenWH;

                    // strafe direction is perpendicular to angle
                    float worldX = camX
                            + cosA * depth
                            - sinA * offset;
                    float worldY = camY
                            + sinA * depth
                            + cosA * offset;

                    int mapX = (int) worldX;
                    int mapY = (int) worldY;

                    int color;
                    if (mapX < 0 || mapY < 0 || mapX >= map[0].length || mapY >= map.length) {
                        color = 0xFFFF00;
                    } else {
                        switch (map[mapY][mapX]) {
                            case 0:
                                color = 0x228B22; // terrain - forest green
                                break;
                            case 1:
                                color = 0x808080; // road - gray
                                break;
                            case 2:
                                color = 0x8B4513; // building - saddle brown
                                break;
                            default:
                                color = 0xFF00FF; // magenta for debugging
                        }
                    }

                    if (color != lastColor) {
                        g.setColor(color);
                        lastColor = color;
                    }

                    // Special drawing cases
                    if (mapX >= 0 && mapY >= 0 && mapX < map[0].length && mapY < map.length) {
                        if (map[mapY][mapX] == 2) {
                            int buildingHeight = (int) ((250) * (((mapX + mapY) % 4) + 1) / depth);
                            int roofHeight = buildingHeight / 10;
                            g.setColor(0x111111 + (0x222222) * ((mapX + mapY) % 5));
                            g.fillRect(px, py - buildingHeight - roofHeight, scale, roofHeight);
                            g.setColor((0x222222) * ((mapX + mapY) % 5));
                            g.fillRect(px, py - buildingHeight, scale, buildingHeight);
                        } else {
                            g.fillRect(px, py, scale, scale);
                        }
                    } else {
                        g.fillRect(px, py, scale, scale);
                    }
                }
            }

            int carW = 60, carH = 20;
            int carX = screenW / 2 - carW / 2;
            int carY = screenH * 9 / 10 - carH - 10;

            // body
            g.setColor(0xCC0000);
            g.fillRect(carX, carY, carW, carH);

            // Debug info
            g.setColor(0);
            TinyFont.drawString(g, "Pos: (" + playerCar.x + ", " + playerCar.y + ")", 0, 0);
            TinyFont.drawString(g, "Speed: " + playerCar.speed, 0, TinyFont.GLYPH_H * 1);
            TinyFont.drawString(g, "Angle: " + playerCar.angle, 0, TinyFont.GLYPH_H * 2);
            TinyFont.drawString(g, "Scale: " + scale, 0, TinyFont.GLYPH_H * 3);
            TinyFont.drawString(g, "FPS: " + (1000 / (System.currentTimeMillis() - startTime)), 0,
                    TinyFont.GLYPH_H * 4);
        }

        public void keyPressed(int keyCode) {
            int gameAction = getGameAction(keyCode);
            switch (gameAction) {
                case LEFT:
                    playerCar.turnLeft();
                    break;
                case RIGHT:
                    playerCar.turnRight();
                    break;
                case UP:
                    playerCar.accelerate();
                    break;
                case DOWN:
                    playerCar.decelerate();
                    break;
                case GAME_A:
                    scale += 1;
                    if (scale > 16)
                        scale = 16;
                    break;
                case GAME_B:
                    scale -= 1;
                    if (scale < 1)
                        scale = 1;
                    break;
            }
        }

        public void keyRepeated(int keyCode) {
            keyPressed(keyCode);
        }
    }
}