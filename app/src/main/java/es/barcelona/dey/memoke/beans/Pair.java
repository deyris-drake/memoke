package es.barcelona.dey.memoke.beans;

import android.content.Context;

import es.barcelona.dey.memoke.beans.Tab;

/**
 * Created by deyris.drake on 16/2/16.
 */
public class Pair {
    public enum State {
        IN_PROCESS, SAVED
    }

    private Tab[] tabs = new Tab[2];
    private int number;
    private State state;
    private boolean simetric;


    public Tab[] getTabs() {
        return tabs;
    }

    public void setTabs(Tab[] tabs) {
        this.tabs = tabs;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isSimetric() {
        return simetric;
    }

    public void setSimetric(boolean simetric) {
        this.simetric = simetric;
    }


}
