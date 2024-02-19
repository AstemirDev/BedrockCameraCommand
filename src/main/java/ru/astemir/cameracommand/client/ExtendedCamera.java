package ru.astemir.cameracommand.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec2;
import ru.astemir.cameracommand.common.camera.CameraMode;

public interface ExtendedCamera {
    void onCustomSetup(CameraManager cameraManager, CameraMode cameraMode, BlockGetter level, Entity cameraEntity, float partialTick);

    void setRotation(Vec2 rotation, boolean mirrored);

    static CameraMode getCameraMode(){
        ToggleableProperty<CameraMode> optionCameraMode = CameraManager.getInstance().optionCameraMode;
        if (optionCameraMode.isEnabled()){
            return optionCameraMode.getValue();
        }else{
            if (ExtendedOptions.isFreeCam()){
                return CameraMode.FREE.get();
            }
            switch (Minecraft.getInstance().options.getCameraType()){
                case FIRST_PERSON -> {
                    return CameraMode.FIRST_PERSON.get();
                }
                case THIRD_PERSON_FRONT -> {
                    return CameraMode.THIRD_PERSON.get();
                }
                case THIRD_PERSON_BACK -> {
                    return CameraMode.THIRD_PERSON_BACK.get();
                }
            }
        }
        throw new RuntimeException("Illegal camera mode");
    }
}