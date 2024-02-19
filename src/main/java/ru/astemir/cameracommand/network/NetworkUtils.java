package ru.astemir.cameracommand.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import ru.astemir.cameracommand.BedrockCameraCommand;

public class NetworkUtils {

    public static SimpleChannel createNetworkChannel(String modId, String name, String protocolVersion){
        return NetworkRegistry.ChannelBuilder.
                named(new ResourceLocation(modId,name)).
                clientAcceptedVersions(protocolVersion::equals).
                serverAcceptedVersions(protocolVersion::equals).
                networkProtocolVersion(()->protocolVersion).
                simpleChannel();
    }

    public static <MSG> void sendToServer(MSG message){
        BedrockCameraCommand.API_NETWORK.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(ServerPlayer player,MSG message){
        BedrockCameraCommand.API_NETWORK.sendTo(message,player.connection.connection,NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToAllPlayers(MSG message){
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendToPlayer(player,message);
        }
    }
}
