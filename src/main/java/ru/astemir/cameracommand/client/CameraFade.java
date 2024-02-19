package ru.astemir.cameracommand.client;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import ru.astemir.cameracommand.BedrockCameraCommand;

public class CameraFade {
    private int fadeInTime;
    private int holdTime;
    private int fadeOutTime;
    private int totalFadeTime;
    private int red = 255;
    private int green = 255;
    private int blue = 255;
    private boolean render;

    public void render(GuiGraphics guiGraphics,float partialTicks){
        if (ExtendedOptions.isCameraCommandEnabled()) {
            Window window = Minecraft.getInstance().getWindow();
            if (render) {
                int alpha = 255;
                float totalTick = (float)this.totalFadeTime - partialTicks;
                if (totalFadeTime > fadeOutTime + holdTime) {
                    float f6 = (float) (fadeInTime + holdTime + fadeOutTime) - totalTick;
                    alpha = (int) (f6 * 255.0F / (float) fadeInTime);
                }
                if (totalFadeTime <= fadeOutTime) {
                    alpha = (int) (totalTick * 255.0F / (float) fadeOutTime);
                }
                alpha = Mth.clamp(alpha, 0, 255);
                guiGraphics.fill(0, 0, window.getScreenWidth(), window.getScreenHeight(), 1000, FastColor.ARGB32.color(alpha, this.red, this.green, this.blue));
            }
        }
    }

    public void tick(){
        if (this.totalFadeTime > 0) {
            --this.totalFadeTime;
            if (this.totalFadeTime <= 0) {
                this.render = false;
                this.red = 255;
                this.green = 255;
                this.blue = 255;
            }
        }
    }

    public void color(int red,int green,int blue) {
        if (this.render) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }

    public void timings(int fadeInTime, int holdTime, int fadeOutTime) {
        if (fadeInTime >= 0) {
            this.fadeInTime = fadeInTime;
        }
        if (holdTime >= 0) {
            this.holdTime = holdTime;
        }
        if (fadeOutTime >= 0) {
            this.fadeOutTime = fadeOutTime;
        }
        this.totalFadeTime = fadeInTime + holdTime + fadeOutTime;
        this.render = true;
    }

    public void clear(){
        this.render = false;
        this.red = 255;
        this.green = 255;
        this.blue = 255;
        this.fadeInTime = 0;
        this.fadeOutTime = 0;
        this.holdTime = 0;
        this.totalFadeTime = 0;
    }

    public static CameraFade getInstance(){
        return BedrockCameraCommand.getCameraFade();
    }
}
