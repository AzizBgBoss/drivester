package com.azizbgboss.game;

// Drivester - A simple 3D driving game for Java ME with late night cyberpunk vibes
// Made by AzizBgBoss
// https://github.com/AzizBgBoss/drivester

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

public class Game implements CommandListener {
    private drivester midlet;
    private Canvas canvas;

    private Command gameExit;

    public int[][] map = { // 0 for terrain, 1 for road, 2 building (32x32)
            { 2, 0, 2, 0, 2, 0, 1, 1, 1, 1, 1, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 1, 1, 1, 1, 1, 2, 0, 2, 0, 2, 0 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 0, 0, 2, 0, 1, 2, 2, 0, 0, 0, 1, 2, 2 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 2, 2, 2, 1, 2, 0, 2, 0, 2, 0 },
            { 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0, 2, 0, 1, 2, 0, 0, 2, 1, 2, 0, 2, 0, 2, 0 },
    };

    public Car playerCar = new Car(4, 5, map);

    private static final int[] STARS_X = new int[80];
    private static final int[] STARS_Y = new int[80];
    static {
        // LCG pseudo-random to scatter stars naturally across the sky
        int seed = 0xDEADBEEF;
        for (int i = 0; i < 80; i++) {
            seed = seed * 1664525 + 1013904223;
            STARS_X[i] = (seed >>> 23) & 0x1FF; // 0..511
            seed = seed * 1664525 + 1013904223;
            STARS_Y[i] = (seed >>> 25) & 0x7F; // 0..127 (clamped to horizonY in paint)
        }
    }

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
        if (d == canvas && c == gameExit)
            midlet.quit();
    }

    public class gameCanvas extends Canvas {
        final int screenW = getWidth();
        final int screenH = getHeight();
        final int horizonY = screenH / 2;
        final int fps = 15;
        final int frameTime = 1000 / fps;

        private int renderDistance = 10;

        // Preallocated sort buffers for visible tiles only.
        private int[] tileOrder = new int[map.length * map[0].length];
        private int[] tileDist = new int[map.length * map[0].length];
        private int[] sx = new int[4];
        private int[] sy = new int[4];
        private float[] fwdC = new float[4];
        private float[] sideC = new float[4];

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
            long startTime = System.currentTimeMillis();

            int mapH = map.length;
            int mapW = map[0].length;
            int halfW = screenW / 2;
            int halfH = screenH / 2;

            float cosA = playerCar.cosTable[playerCar.angle];
            float sinA = playerCar.sinTable[playerCar.angle];
            float camX = playerCar.x - cosA * 1.5f;
            float camY = playerCar.y - sinA * 1.5f;

            // sky
            g.setColor(0x08051A);
            g.fillRect(0, 0, screenW, horizonY);

            // stars (with angle
            // FIXME: when angles goes from 0 to 359, the stars change their whole positions
            // instead of just shifting a bit
            g.setColor(0xFFFFFF);
            int angleShift = playerCar.angle * 2; // pixels of scroll per degree
            for (int i = 0; i < 80; i++) {
                // parallax: stars at even indices scroll at full rate, odd at half
                int parallax = (i & 1) == 0 ? angleShift : angleShift / 2;
                int sx2 = ((STARS_X[i] - parallax) % screenW + screenW) % screenW;
                int sy2 = STARS_Y[i] % horizonY;
                // vary brightness: every 5th star is slightly bigger
                if (i % 5 == 0)
                    g.fillRect(sx2, sy2, 2, 2);
                else
                    g.fillRect(sx2, sy2, 1, 1);
            }

            // ground base
            g.setColor(0x05070D);
            g.fillRect(0, horizonY, screenW, screenH - horizonY);

            // Gather only visible nearby tiles, then sort them by camera depth.
            int maxDist = renderDistance * renderDistance;
            int visibleCount = 0;
            for (int my = 0; my < mapH; my++) {
                for (int mx = 0; mx < mapW; mx++) {
                    float cdx = (mx + 0.5f) - camX;
                    float cdy = (my + 0.5f) - camY;
                    if (cdx * cdx + cdy * cdy >= maxDist)
                        continue;

                    float fwdCenter = cdx * cosA + cdy * sinA;
                    if (fwdCenter <= 0.1f)
                        continue;

                    int encoded = my * mapW + mx;
                    int depthKey = (int) (fwdCenter * 1024);
                    int pos = visibleCount;
                    while (pos > 0 && tileDist[pos - 1] < depthKey) {
                        tileDist[pos] = tileDist[pos - 1];
                        tileOrder[pos] = tileOrder[pos - 1];
                        pos--;
                    }
                    tileDist[pos] = depthKey;
                    tileOrder[pos] = encoded;
                    visibleCount++;
                }
            }

            for (int t = 0; t < visibleCount; t++) {
                int encoded = tileOrder[t];
                int my = encoded / mapW;
                int mx = encoded % mapW;

                int cell = map[my][mx];
                // corners TL(0) TR(1) BR(2) BL(3)
                // 0=(mx, my ) 1=(mx+1,my )
                // 3=(mx, my+1) 2=(mx+1,my+1)
                boolean anyVisible = false;
                for (int i = 0; i < 4; i++) {
                    float wx = (i == 0 || i == 3) ? mx : mx + 1;
                    float wy = (i == 0 || i == 1) ? my : my + 1;
                    float dx = wx - camX;
                    float dy = wy - camY;
                    fwdC[i] = dx * cosA + dy * sinA;
                    sideC[i] = -dx * sinA + dy * cosA;
                    if (fwdC[i] > 0.1f) {
                        anyVisible = true;
                        sx[i] = halfW + (int) (sideC[i] * halfH / fwdC[i]);
                        sy[i] = horizonY + (int) ((float) halfH / fwdC[i]);
                    } else {
                        sx[i] = sideC[i] < 0 ? -9999 : 9999;
                        sy[i] = screenH + 9999;
                    }
                }
                if (!anyVisible)
                    continue;

                int minSx = Math.min(Math.min(sx[0], sx[1]), Math.min(sx[2], sx[3]));
                int maxSx = Math.max(Math.max(sx[0], sx[1]), Math.max(sx[2], sx[3]));
                if (maxSx < 0 || minSx > screenW)
                    continue;

                // floor
                int floorColor;
                switch (cell) {
                    case 1:
                        floorColor = 0x161B2F;
                        break;
                    case 2:
                        floorColor = 0x090C18;
                        break;
                    default:
                        floorColor = 0x07111A;
                        break;
                }
                g.setColor(floorColor);
                g.fillTriangle(sx[0], sy[0], sx[1], sy[1], sx[2], sy[2]);
                g.fillTriangle(sx[0], sy[0], sx[2], sy[2], sx[3], sy[3]);

                // building walls
                if (cell == 2) {
                    float wallH = 0.5f + 0.5f * ((mx + my)) % 3;

                    int ty0 = fwdC[0] > 0.1f ? sy[0] - (int) (wallH * halfH / fwdC[0]) : sy[0];
                    int ty1 = fwdC[1] > 0.1f ? sy[1] - (int) (wallH * halfH / fwdC[1]) : sy[1];
                    int ty2 = fwdC[2] > 0.1f ? sy[2] - (int) (wallH * halfH / fwdC[2]) : sy[2];
                    int ty3 = fwdC[3] > 0.1f ? sy[3] - (int) (wallH * halfH / fwdC[3]) : sy[3];

                    // TODO: fix colors that dont match the cyberpunk vibe
                    int colorVariant = (mx * 3 + my * 5) & 3;
                    int wallColor;
                    int wallColorSide;
                    switch (colorVariant) {
                        case 0:
                            wallColor = 0x24E6F5;
                            break;
                        case 1:
                            wallColor = 0xFF2FA3;
                            break;
                        case 2:
                            wallColor = 0xFFD166;
                            break;
                        default:
                            wallColor = 0x7CFF6B;
                            break;
                    }
                    wallColorSide = Math.max(0, wallColor - 0x202020); // simple shading for sides

                    // TODO: add some more details like windows (but be careful with performance and
                    // clarity)
                    // TODO: also fix face desicion, example: when cam is 0, show left face of
                    // buildings on the right, which is correct, but also left buildings show left
                    // side, somewhat incorrect

                    // north face: visible when cam is north of tile center
                    if (camY < my + 0.5f) {
                        g.setColor(wallColor);
                        g.fillTriangle(sx[0], sy[0], sx[1], sy[1], sx[1], ty1);
                        g.fillTriangle(sx[0], sy[0], sx[1], ty1, sx[0], ty0);
                    }
                    // south face: visible when cam is south of tile center
                    if (camY > my + 0.5f) {
                        g.setColor(wallColor);
                        g.fillTriangle(sx[3], sy[3], sx[2], sy[2], sx[2], ty2);
                        g.fillTriangle(sx[3], sy[3], sx[2], ty2, sx[3], ty3);
                    }
                    // west face: visible when cam is west of tile center
                    if (camX < mx + 0.5f) {
                        g.setColor(wallColorSide);
                        g.fillTriangle(sx[0], sy[0], sx[3], sy[3], sx[3], ty3);
                        g.fillTriangle(sx[0], sy[0], sx[3], ty3, sx[0], ty0);
                    }
                    // east face: visible when cam is east of tile center
                    if (camX > mx + 0.5f) {
                        g.setColor(wallColorSide);
                        g.fillTriangle(sx[1], sy[1], sx[2], sy[2], sx[2], ty2);
                        g.fillTriangle(sx[1], sy[1], sx[2], ty2, sx[1], ty1);
                    }
                }
            }

            // car sprite
            int carW = 60, carH = 20;
            int carX = screenW / 2 - carW / 2;
            int carY = screenH * 9 / 10 - carH - 10;
            g.setColor(0xAA0000);
            g.fillRect(carX, carY, carW, carH);

            // debug
            g.setColor(0xFFFFFF);
            TinyFont.drawString(g, "Pos:(" + (int) playerCar.x + "," + (int) playerCar.y + ")", 0, 0);
            TinyFont.drawString(g, "Ang:" + playerCar.angle, 0, TinyFont.GLYPH_H);
            long elapsed = System.currentTimeMillis() - startTime;
            TinyFont.drawString(g, "FPS:" + (elapsed > 0 ? 1000 / elapsed : 99), 0, TinyFont.GLYPH_H * 2);
        }

        public void keyPressed(int keyCode) {
            int a = getGameAction(keyCode);
            switch (a) {
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
                    renderDistance = Math.max(1, renderDistance - 1);
                    break;
                case GAME_B:
                    renderDistance = Math.min(20, renderDistance + 1);
                    break;
            }
        }

        public void keyRepeated(int keyCode) {
            keyPressed(keyCode);
        }
    }
}
