package ru.astemir.cameracommand.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.astemir.cameracommand.client.CameraManager;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {

    @Inject(method = "handleDisconnect",at = @At("HEAD"))
    public void onDisconnect(ClientboundDisconnectPacket packet, CallbackInfo ci){
        CameraManager.getInstance().clear();
    }
}
