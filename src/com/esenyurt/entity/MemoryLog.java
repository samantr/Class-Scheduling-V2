package com.esenyurt.entity;

public class MemoryLog {

    public int id;
    public int usedMemMb;
    public int freeMemMb;
    public int maxMemMb;

    public MemoryLog() {
    }

    public MemoryLog(int id, int usedMemMb, int freeMemMb, int maxMemMb) {
        this.id = id;
        this.usedMemMb = usedMemMb;
        this.freeMemMb = freeMemMb;
        this.maxMemMb = maxMemMb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsedMemMb() {
        return usedMemMb;
    }

    public void setUsedMemMb(int usedMemMb) {
        this.usedMemMb = usedMemMb;
    }

    public int getFreeMemMb() {
        return freeMemMb;
    }

    public void setFreeMemMb(int freeMemMb) {
        this.freeMemMb = freeMemMb;
    }

    public int getMaxMemMb() {
        return maxMemMb;
    }

    public void setMaxMemMb(int maxMemMb) {
        this.maxMemMb = maxMemMb;
    }
}
