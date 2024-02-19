package ru.astemir.cameracommand.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import oshi.util.tuples.Pair;
import ru.astemir.cameracommand.common.camera.CameraLookTarget;
import ru.astemir.cameracommand.common.camera.CameraMode;
import ru.astemir.cameracommand.common.camera.EasingType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ClientCameraPacket {
    private List<Pair<CameraMessageType, CameraMessageHandle>> handles = new ArrayList<>();

    public ClientCameraPacket with(Pair<CameraMessageType, CameraMessageHandle> pair){
        this.handles.add(pair);
        return this;
    }

    public ClientCameraPacket mode(CameraMode cameraMode){
        return with(new Pair(CameraMessageType.MODE,new CameraMessageHandle.SetMode(cameraMode)));
    }

    public ClientCameraPacket easing(EasingType easingType, float easeTime){
        return with(new Pair(CameraMessageType.EASING,new CameraMessageHandle.SetEasing(easingType,easeTime)));
    }

    public ClientCameraPacket position(Vec3 position){
        return with(new Pair(CameraMessageType.POSITION,new CameraMessageHandle.SetPosition(position)));
    }

    public ClientCameraPacket rotation(Vec2 rotation){
        return with(new Pair(CameraMessageType.ROTATION,new CameraMessageHandle.SetRotation(rotation)));
    }

    public ClientCameraPacket clear(){
        return with(new Pair(CameraMessageType.CLEAR,new CameraMessageHandle.Clear()));
    }

    public ClientCameraPacket lookAt(CameraLookTarget lookTarget){
        return with(new Pair(CameraMessageType.LOOK_AT,new CameraMessageHandle.SetLookAt(lookTarget)));
    }

    public ClientCameraPacket defaultRotPos(){
        return with(new Pair(CameraMessageType.DEFAULT,new CameraMessageHandle.Default()));
    }

    public ClientCameraPacket fadeTimings(int fadeInSeconds,int holdSeconds,int fadeOutSeconds){
        return with(new Pair(CameraMessageType.FADE_TIMINGS,new CameraMessageHandle.FadeTimings(fadeInSeconds,holdSeconds,fadeOutSeconds)));
    }

    public ClientCameraPacket fadeColor(int r,int g,int b){
        return with(new Pair(CameraMessageType.FADE_COLOR,new CameraMessageHandle.FadeColor(r,g,b)));
    }

    public static void encode(ClientCameraPacket message, FriendlyByteBuf buf) {
        buf.writeInt(message.handles.size());
        for (Pair<CameraMessageType, CameraMessageHandle> handle : message.handles) {
            buf.writeEnum(handle.getA());
            handle.getB().encode(buf);
        }
    }

    public static ClientCameraPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        ClientCameraPacket message = new ClientCameraPacket();
        for (int i = 0;i<size;i++){
            CameraMessageType messageType = buf.readEnum(CameraMessageType.class);
            message = message.with(new Pair<>(messageType,messageType.getDecoder().apply(buf)));
        }
        return message;
    }

    public static class Handler implements BiConsumer<ClientCameraPacket, Supplier<NetworkEvent.Context>> {

        @OnlyIn(Dist.CLIENT)
        @Override
        public void accept(ClientCameraPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> message.handles.forEach((pair)->pair.getB().accept(contextSupplier.get())));
            context.setPacketHandled(true);
        }
    }
}
