package com.azizbgboss.game;

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
            { 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 2, 0, 1 },
            { 0, 0, 0, 0, 0, 1, 1, 1, 0, 1 },
            { 0, 0, 0, 0, 0, 1, 0, 1, 1, 1 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 2 },
    };

    public Car playerCar = new Car(4, 5, map);

    public Game(drivester midlet) {
        this.midlet = midlet;
        canvas = new gameCanvas();
        gameExit = new Command("Exit", Command.EXIT, 0);
        canvas.addCommand(gameExit);
        canvas.setCommandListener(this);
    }

    public Canvas getCanvas() { return canvas; }

    public void commandAction(Command c, Displayable d) {
        if (d == canvas && c == gameExit) midlet.quit();
    }

    public class gameCanvas extends Canvas {
        final int screenW = getWidth();
        final int screenH = getHeight();
        final int horizonY = screenH / 2;
        final int fps = 15;
        final int frameTime = 1000 / fps;

        // preallocated sort buffers
        private int[] tileOrder = new int[map.length * map[0].length];
        private int[] tileDist  = new int[map.length * map[0].length];
        private int[] sx = new int[4];
        private int[] sy = new int[4];
        private float[] fwdC  = new float[4];
        private float[] sideC = new float[4];

        public gameCanvas() {
            setFullScreenMode(true);
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        repaint();
                        playerCar.update();
                        try { Thread.sleep(frameTime); } catch (InterruptedException e) {}
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
            g.setColor(0x87CEEB);
            g.fillRect(0, 0, screenW, horizonY);

            // ground base
            g.setColor(0x228B22);
            g.fillRect(0, horizonY, screenW, screenH - horizonY);

            // --- Painter's algorithm: sort far-to-near ---
            int tileCount = mapH * mapW;
            for (int i = 0; i < tileCount; i++) {
                int mx = i % mapW;
                int my = i / mapW;
                tileOrder[i] = i;
                float dx = (mx + 0.5f) - camX;
                float dy = (my + 0.5f) - camY;
                tileDist[i] = (int)(dx * dx + dy * dy);
            }
            // insertion sort descending (far first)
            for (int i = 1; i < tileCount; i++) {
                int key = tileOrder[i];
                int keyDist = tileDist[key];
                int j = i - 1;
                while (j >= 0 && tileDist[tileOrder[j]] < keyDist) {
                    tileOrder[j + 1] = tileOrder[j];
                    j--;
                }
                tileOrder[j + 1] = key;
            }

            // --- Draw tiles far to near ---
            for (int t = 0; t < tileCount; t++) {
                int idx = tileOrder[t];
                int mx = idx % mapW;
                int my = idx / mapW;
                int cell = map[my][mx];

                // corners TL(0) TR(1) BR(2) BL(3)
                // 0=(mx,  my  ) 1=(mx+1,my  )
                // 3=(mx,  my+1) 2=(mx+1,my+1)
                boolean anyVisible = false;
                for (int i = 0; i < 4; i++) {
                    float wx = (i == 0 || i == 3) ? mx : mx + 1;
                    float wy = (i == 0 || i == 1) ? my : my + 1;
                    float dx = wx - camX;
                    float dy = wy - camY;
                    fwdC[i]  = dx * cosA + dy * sinA;
                    sideC[i] = -dx * sinA + dy * cosA;
                    if (fwdC[i] > 0.1f) {
                        anyVisible = true;
                        sx[i] = halfW    + (int)(sideC[i] * halfH / fwdC[i]);
                        sy[i] = horizonY + (int)((float)halfH / fwdC[i]);
                    } else {
                        sx[i] = sideC[i] < 0 ? -9999 : 9999;
                        sy[i] = screenH + 9999;
                    }
                }
                if (!anyVisible) continue;

                int minSx = Math.min(Math.min(sx[0], sx[1]), Math.min(sx[2], sx[3]));
                int maxSx = Math.max(Math.max(sx[0], sx[1]), Math.max(sx[2], sx[3]));
                if (maxSx < 0 || minSx > screenW) continue;

                // floor
                int floorColor;
                switch (cell) {
                    case 1:  floorColor = 0x808080; break;
                    case 2:  floorColor = 0x333333; break;
                    default: floorColor = 0x228B22; break;
                }
                g.setColor(floorColor);
                g.fillTriangle(sx[0], sy[0], sx[1], sy[1], sx[2], sy[2]);
                g.fillTriangle(sx[0], sy[0], sx[2], sy[2], sx[3], sy[3]);

                // building walls
                if (cell == 2) {
                    float dx0 = (mx + 0.5f) - camX;
                    float dy0 = (my + 0.5f) - camY;
                    float fwdCenter = dx0 * cosA + dy0 * sinA;
                    if (fwdCenter <= 0.1f) continue;

                    int wallH = (int)((0.5f + 0.5f * ((mx + my)) % 4) * halfH / fwdCenter);
                    if (wallH <= 0 || wallH > screenH * 4) continue;

                    int ty0 = sy[0] - wallH;
                    int ty1 = sy[1] - wallH;
                    int ty2 = sy[2] - wallH;
                    int ty3 = sy[3] - wallH;

                    int wallColor     = 0x666666 + 0x111111 * ((mx + my) % 3);
                    int wallColorSide = wallColor - 0x222222;

                    // north face: visible when cam is north of tile center
                    if (camY < my + 0.5f) {
                        g.setColor(wallColor);
                        g.fillTriangle(sx[0], sy[0], sx[1], sy[1], sx[1], ty1);
                        g.fillTriangle(sx[0], sy[0], sx[1], ty1,   sx[0], ty0);
                    }
                    // south face: visible when cam is south of tile center
                    if (camY > my + 0.5f) {
                        g.setColor(wallColor);
                        g.fillTriangle(sx[3], sy[3], sx[2], sy[2], sx[2], ty2);
                        g.fillTriangle(sx[3], sy[3], sx[2], ty2,   sx[3], ty3);
                    }
                    // west face: visible when cam is west of tile center
                    if (camX < mx + 0.5f) {
                        g.setColor(wallColorSide);
                        g.fillTriangle(sx[0], sy[0], sx[3], sy[3], sx[3], ty3);
                        g.fillTriangle(sx[0], sy[0], sx[3], ty3,   sx[0], ty0);
                    }
                    // east face: visible when cam is east of tile center
                    if (camX > mx + 0.5f) {
                        g.setColor(wallColorSide);
                        g.fillTriangle(sx[1], sy[1], sx[2], sy[2], sx[2], ty2);
                        g.fillTriangle(sx[1], sy[1], sx[2], ty2,   sx[1], ty1);
                    }

                    // roof
                    g.setColor(0x999999);
                    g.fillTriangle(sx[0], ty0, sx[1], ty1, sx[2], ty2);
                    g.fillTriangle(sx[0], ty0, sx[2], ty2, sx[3], ty3);
                }
            }

            // car sprite
            int carW = 60, carH = 20;
            int carX = screenW / 2 - carW / 2;
            int carY = screenH * 9 / 10 - carH - 10;
            g.setColor(0xCC0000);
            g.fillRect(carX, carY, carW, carH);

            // debug
            g.setColor(0xFFFFFF);
            TinyFont.drawString(g, "Pos:(" + (int)playerCar.x + "," + (int)playerCar.y + ")", 0, 0);
            TinyFont.drawString(g, "Ang:" + playerCar.angle, 0, TinyFont.GLYPH_H);
            long elapsed = System.currentTimeMillis() - startTime;
            TinyFont.drawString(g, "FPS:" + (elapsed > 0 ? 1000 / elapsed : 99), 0, TinyFont.GLYPH_H * 2);
        }

        public void keyPressed(int keyCode) {
            int a = getGameAction(keyCode);
            switch (a) {
                case LEFT:  playerCar.turnLeft();   break;
                case RIGHT: playerCar.turnRight();  break;
                case UP:    playerCar.accelerate(); break;
                case DOWN:  playerCar.decelerate(); break;
            }
        }

        public void keyRepeated(int keyCode) { keyPressed(keyCode); }
    }
}