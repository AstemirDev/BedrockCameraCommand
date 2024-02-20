package ru.astemir.cameracommand.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import ru.astemir.cameracommand.data.ServerCameraModeManager;

public interface ExtendedOptions {
    OptionInstance<Boolean> getCameraCommandOption();

    OptionInstance<Boolean> getCameraIsFreeOption();

    static boolean isFreeCam(){
        return ((ExtendedOptions)Minecraft.getInstance().options).getCameraIsFreeOption().get();
    }

    static boolean isCameraCommandEnabled(){
        return ((ExtendedOptions)Minecraft.getInstance().options).getCameraCommandOption().get() && ServerCameraModeManager.isLoaded();
    }

    static void disableFreecam(){
        ((ExtendedOptions)Minecraft.getInstance().options).getCameraIsFreeOption().set(false);
    }
}
