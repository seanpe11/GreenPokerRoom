package com.mobdeve.machineproj.greenpokerroom;

public class Card {
    private int val, image, suit, numval;
    public Card(int val, int suit, int numval, int image){
        this.val=val;
        this.suit=suit;
        this.numval=numval;
        this.image=image;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getSuit() {
        return suit;
    }

    public void setSuit(int suit) {
        this.suit = suit;
    }

    public int getNumval() {
        return numval;
    }

    public void setNumval(int numval) {
        this.numval = numval;
    }
}
