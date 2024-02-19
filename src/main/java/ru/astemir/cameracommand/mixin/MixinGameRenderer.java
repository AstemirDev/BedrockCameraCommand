package ru.astemir.cameracommand.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.astemir.cameracommand.client.CameraFade;
import ru.astemir.cameracommand.client.CameraManager;
import ru.astemir.cameracommand.client.ExtendedCamera;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow @Final private RenderBuffers renderBuffers;

    @Inject(method = "bobView",at=@At("HEAD"),cancellable = true)
    public void onBobView(PoseStack poseStack, float partialTick, CallbackInfo ci){
        if (CameraManager.getInstance().isEnabled()){
            if (!ExtendedCamera.getCameraMode().isBobView()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;F)V",shift = At.Shift.AFTER))
    public void onGuiRender(float partialTicks, long nanoTime, boolean renderWorldIn, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics guigraphics = new GuiGraphics(minecraft, renderBuffers.bufferSource());
        CameraFade.getInstance().render(guigraphics,partialTicks);
    }

    @Inject(method = "tick",at = @At("HEAD"))
    public void onGuiTick(CallbackInfo ci){
        CameraFade.getInstance().tick();
    }
}
