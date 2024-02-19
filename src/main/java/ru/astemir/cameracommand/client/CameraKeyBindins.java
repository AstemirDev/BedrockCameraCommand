package ru.astemir.cameracommand.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.astemir.cameracommand.BedrockCameraCommand;

@Mod.EventBusSubscriber(modid = BedrockCameraCommand.MODID,bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class CameraKeyBindins {
    public static final KeyMapping keyToggleFreeCamera = new KeyMapping("key.toggleFreeCamera", InputConstants.KEY_B, "key.categories.misc");

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent e){
        e.register(keyToggleFreeCamera);
    }
}
