package ru.astemir.cameracommand.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.astemir.cameracommand.client.CameraManager;
import ru.astemir.cameracommand.client.CameraKeyBindins;
import ru.astemir.cameracommand.client.ExtendedOptions;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow @Final public LevelRenderer levelRenderer;
    @Shadow @Final public Options options;

    @Shadow @Nullable public LocalPlayer player;

    @Shadow @Final public Gui gui;

    @Inject(method = "handleKeybinds",at = @At(value = "HEAD"))
    public void onHandleKeyBinds(CallbackInfo ci){
        if (CameraManager.getInstance().isEnabled()){
            if (!CameraManager.getInstance().optionCameraMode.isEnabled()) {
                OptionInstance<Boolean> freeCamOption = ((ExtendedOptions) options).getCameraIsFreeOption();
                for (; CameraKeyBindins.keyToggleFreeCamera.consumeClick(); this.levelRenderer.needsUpdate()) {
                    boolean newValue = !freeCamOption.get();
                    if (newValue) {
                        gui.setOverlayMessage(Component.translatable("freecam.enabled"),false);
                    } else {
                        gui.setOverlayMessage(Component.translatable("freecam.disabled"),false);
                    }
                    freeCamOption.set(newValue);
                }
            }
        }
    }

    @Inject(method = "handleKeybinds",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V",shift = At.Shift.BEFORE),cancellable = true)
    public void onCancelKeyTogglePerspective(CallbackInfo ci){
        if (CameraManager.getInstance().isEnabled()){
            if (CameraManager.getInstance().optionCameraMode.isEnabled() || ExtendedOptions.isFreeCam()) {
                ci.cancel();
            }
        }
    }
}
