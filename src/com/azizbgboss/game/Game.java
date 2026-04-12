package com.azizbgboss.game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class Game {
    private drivester midlet;
    private Canvas canvas;

    public int[][] map = { // 0 for terrain, 1 for road
            { 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, // -> North
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 0, 0, 0, 0, 0, 1, 1, 1, 0, 1 },
            { 0, 0, 0, 0, 0, 1, 0, 1, 1, 1 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
    };

    public Car playerCar = new Car(4, 5); // Start on the road

    public Game(drivester midlet) {
        this.midlet = midlet;
        canvas = new gameCanvas();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public class gameCanvas extends Canvas {
        final int screenW = getWidth();
        final int screenH = getHeight();
        final int horizonY = screenH / 2;

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
                            Thread.sleep(1000 / 15);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }).start();
        }

        protected void paint(Graphics g) {
            // Cursed technique: scanline rendering
            // It is used by skilled jujutsu sorcerers to create powerful illusions that can
            // deceive the senses of their opponents.
            // By manipulating the perception of reality, they can create pseudo 3D effects
            // and immersive environments that can disorient and overwhelm their opponents.
            // Such techniques require a high level of skill and mastery, but is easy on the
            // host machine as it doesn't require complex 3D rendering calculations.

            g.setColor(0x87CEEB); // sky blue
            g.fillRect(0, 0, screenW, horizonY);

            for (int py = horizonY; py < screenH; py++) {
                float depth = (float) (screenH / 2) / (py - horizonY + 1);

                for (int px = 0; px < screenW; px++) {
                    // offset from screen center, scaled by depth
                    float offset = (px - screenW / 2f) * depth / (screenW / 2f);

                    // strafe direction is perpendicular to angle
                    float worldX = playerCar.x
                            + (float) Math.cos(Math.toRadians(playerCar.angle)) * depth
                            - (float) Math.sin(Math.toRadians(playerCar.angle)) * offset;
                    float worldY = playerCar.y
                            + (float) Math.sin(Math.toRadians(playerCar.angle)) * depth
                            + (float) Math.cos(Math.toRadians(playerCar.angle)) * offset;

                    int mapX = (int) worldX;
                    int mapY = (int) worldY;

                    int color;
                    if (mapX < 0 || mapY < 0 || mapX >= map[0].length || mapY >= map.length) {
                        color = 0xFFFF00;
                    } else {
                        color = map[mapY][mapX] == 1 ? 0x111111 : 0xFFFF00;
                    }

                    g.setColor(color);
                    g.drawLine(px, py, px, py);
                }
            }

            // Debug info
            g.setColor(0);
            TinyFont.drawString(g, "Pos: (" + playerCar.x + ", " + playerCar.y + ")", 0, 0);
            TinyFont.drawString(g, "Speed: " + playerCar.speed, 0, TinyFont.GLYPH_H * 1);
            TinyFont.drawString(g, "Angle: " + playerCar.angle, 0, TinyFont.GLYPH_H * 2);
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
            }
        }

        public void keyRepeated(int keyCode) {
            keyPressed(keyCode);
        }
    }
}