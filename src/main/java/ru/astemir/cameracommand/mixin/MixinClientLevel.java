package ru.astemir.cameracommand.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.astemir.cameracommand.client.CameraManager;
import ru.astemir.cameracommand.client.ExtendedOptions;

@Mixin(ClientLevel.class)
public class MixinClientLevel {

    @Inject(method = "disconnect",at=@At("HEAD"),cancellable = true)
    public void onDisconnect(CallbackInfo ci){
        CameraManager.getInstance().clear();
        ExtendedOptions.disableFreecam();
    }
}
