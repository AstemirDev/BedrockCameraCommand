package ru.astemir.cameracommand;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import ru.astemir.cameracommand.client.CameraFade;
import ru.astemir.cameracommand.client.CameraManager;
import ru.astemir.cameracommand.common.command.ArgumentTypes;
import ru.astemir.cameracommand.network.ClientCameraPacket;
import ru.astemir.cameracommand.network.NetworkUtils;

@Mod(BedrockCameraCommand.MODID)
public class BedrockCameraCommand {
    public static final String MODID = "cameracommand";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final SimpleChannel API_NETWORK = NetworkUtils.createNetworkChannel(MODID,"main_channel","1.0");
    @OnlyIn(Dist.CLIENT)
    private static CameraManager cameraManager;
    @OnlyIn(Dist.CLIENT)
    private static CameraFade cameraFade;
    public static volatile boolean INITIALIZED = false;
    public BedrockCameraCommand() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ArgumentTypes.COMMAND_ARGUMENT_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        this.cameraManager = new CameraManager();
        this.cameraFade = new CameraFade();
    }

    synchronized public static void initialize(){
        if (!INITIALIZED) {
            int id = 0;
            API_NETWORK.registerMessage(id++, ClientCameraPacket.class, ClientCameraPacket::encode, ClientCameraPacket::decode, new ClientCameraPacket.Handler());
        }
        INITIALIZED = true;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        initialize();
    }

    public static CameraManager getCameraProperties() {
        return cameraManager;
    }

    public static CameraFade getCameraFade(){
        return cameraFade;
    }
}
