package com.example.tmaniaci.openslots6;


public class Slot {
    private String slot_start;
    private String slot_end;
    private int slot_minutes;
    private int no_of_slots;

    public Slot(){
    }

    public Slot(String slot_start, String slot_end, int slot_minutes, int no_of_slots) {
        this.slot_start = slot_start;
        this.slot_minutes = slot_minutes;
        this.slot_end = slot_end;
        this.no_of_slots = no_of_slots;
    }

    public String getSlot_start() {
        return slot_start;
    }

    public void setSlot_start(String slot_start) {
        this.slot_start = slot_start;
    }

    public String getSlot_end() {
        return slot_end;
    }

    public void setSlot_end(String slot_end) {
        this.slot_end = slot_end;
    }

    public int getSlot_minutes() {
        return slot_minutes;
    }

    public void setSlot_minutes(int slot_minutes) {
        this.slot_minutes = slot_minutes;
    }

    public int getNo_of_slots() {
        return no_of_slots;
    }

    public void setNo_of_slots(int no_of_slots) {
        this.no_of_slots = no_of_slots;
    }

}//end class
