package ru.astemir.cameracommand.common.camera;

public class Timeline {
    private double length;
    private double ticks = 0;
    private boolean enabled = false;

    public void tick(double speed){
        if (enabled) {
            if (ticks <= length) {
                ticks+=speed;
            }else{
                stop();
            }
        }
    }

    public void start(double length){
        this.length = length;
        start();
    }

    public void start(){
        enabled = true;
        ticks = 0;
    }

    public void stop(){
        enabled = false;
        ticks = 0;
    }

    public double getLength() {
        return length;
    }

    public double getTicks() {
        return ticks;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
