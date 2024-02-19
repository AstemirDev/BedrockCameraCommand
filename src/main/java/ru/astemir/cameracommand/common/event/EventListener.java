package ru.astemir.cameracommand.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.astemir.cameracommand.BedrockCameraCommand;
import ru.astemir.cameracommand.data.ServerCameraModeManager;
import ru.astemir.cameracommand.network.ClientCameraPacket;
import ru.astemir.cameracommand.network.NetworkUtils;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = BedrockCameraCommand.MODID)
public class EventListener {

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent e){
        e.addListener(new ServerCameraModeManager());
    }

    @SubscribeEvent
    public static void onDie(LivingDeathEvent e){
        if (e.getEntity() instanceof ServerPlayer player) {
            NetworkUtils.sendToPlayer(player, new ClientCameraPacket().clear());
        }
    }
}
