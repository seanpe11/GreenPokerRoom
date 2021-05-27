package com.mobdeve.machineproj.greenpokerroom;

public class Player {
    private String name, action;
    int stack;

    public Player(String name, int stack, String action){
        this.name=name;
        this.stack=stack;
        this.action=action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStack() {
        return stack;
    }

    public void setStack(int stack) {
        this.stack = stack;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
