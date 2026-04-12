package com.azizbgboss.game;

// Drivester - A simple car driving game for J2ME devices
// Made by AzizBgBoss
// Uses a pseudo 3D rendering engine made by me
// https://github.com/AzizBgBoss/drivester

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;

public class drivester extends MIDlet {
    private Display display;
    public Game game;

    public void startApp() {
        display = Display.getDisplay(this);
        game = new Game(this);
        display.setCurrent(game.getCanvas());
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

    public void quit() {
        notifyDestroyed();
    }

    public Display getDisplay() {
        return display;
    }
}