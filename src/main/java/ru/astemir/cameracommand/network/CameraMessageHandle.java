package ru.astemir.cameracommand.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import ru.astemir.cameracommand.client.CameraFade;
import ru.astemir.cameracommand.client.CameraManager;
import ru.astemir.cameracommand.client.ExtendedOptions;
import ru.astemir.cameracommand.common.camera.CameraLookTarget;
import ru.astemir.cameracommand.common.camera.CameraMode;
import ru.astemir.cameracommand.common.camera.EasingType;

public abstract class CameraMessageHandle {
    public abstract void encode(FriendlyByteBuf buffer);
    public abstract void accept(NetworkEvent.Context context);

    public static class Clear extends CameraMessageHandle {
        private boolean disableFreeCam;

        public Clear(boolean disableFreeCam) {
            this.disableFreeCam = disableFreeCam;
        }

        @Override
        public void encode(FriendlyByteBuf buffer) {
            buffer.writeBoolean(disableFreeCam);
        }
        @Override
        public void accept(NetworkEvent.Context context) {
            CameraManager.getInstance().clear();
            if (disableFreeCam) {
                ExtendedOptions.disableFreecam();
            }
        }
    }

    public static class FadeTimings extends CameraMessageHandle {
        private int fadeInSeconds;
        private int holdSeconds;
        private int fadeOutSeconds;

        public FadeTimings(int fadeInSeconds, int holdSeconds, int fadeOutSeconds) {
            this.fadeInSeconds = fadeInSeconds;
            this.holdSeconds = holdSeconds;
            this.fadeOutSeconds = fadeOutSeconds;
        }

        @Override
        public void encode(FriendlyByteBuf buffer) {
            buffer.writeInt(fadeInSeconds);
            buffer.writeInt(holdSeconds);
            buffer.writeInt(fadeOutSeconds);
        }

        @Override
        public void accept(NetworkEvent.Context context) {
            CameraFade.getInstance().timings(fadeInSeconds,holdSeconds,fadeOutSeconds);
        }
    }

    public static class FadeColor extends CameraMessageHandle {
        private int r;
        private int g;
        private int b;

        public FadeColor(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @Override
        public void encode(FriendlyByteBuf buffer) {
            buffer.writeInt(r);
            buffer.writeInt(g);
            buffer.writeInt(b);
        }

        @Override
        public void accept(NetworkEvent.Context context) {
            CameraFade.getInstance().color(r,g,b);
        }
    }


    public static class SetMode extends CameraMessageHandle {
        private CameraMode cameraMode;
        public SetMode(CameraMode cameraMode) {
            this.cameraMode = cameraMode;
        }
        @Override
        public void encode(FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(cameraMode.getLocation());
        }

        @Override
        public void accept(NetworkEvent.Context context) {
            CameraManager.getInstance().optionCameraMode.activate(cameraMode);
        }
    }

    public static class Default extends CameraMessageHandle {
        @Override
        public void encode(FriendlyByteBuf buffer) {}
        @Override
        public void accept(NetworkEvent.Context context) {
            CameraManager.getInstance().optionCameraRotation.deactivate();
            CameraManager.getInstance().optionCameraPosition.deactivate();
        }
    }

    public static class SetEasing extends CameraMessageHandle {
        private EasingType easingType;
        private float easeTime;

        public SetEasing(EasingType easingType,float easeTime) {
            this.easingType = easingType;
            this.easeTime = easeTime;
        }
        @Override
        public void encode(FriendlyByteBuf buffer) {
            buffer.writeEnum(easingType);
            buffer.writeFloat(easeTime);
        }

        @Override
        public void accept(NetworkEvent.Context context) {
            CameraManager cameraManager = CameraManager.getInstance();
            cameraManager.setEasingType(easingType);
            cameraManager.optionCameraEasingTime.activate(easeTime);
        }
    }

    public static class SetPosition extends CameraMessageHandle {
        private Vec3 position;
        public SetPosition(Vec3 position) {
            this.position = position;
        }
        @Override
        public void encode(FriendlyByteBuf buffer) {
            buffer.writeFloat((float) position.x);
            buffer.writeFloat((float) position.y);
            buffer.writeFloat((float) position.z);
        }

        @Override
        public void accept(NetworkEvent.Context context) {
            CameraManager properties = CameraManager.getInstance();
            properties.optionCameraPosition.activate(position);
        }
    }

    public static class SetRotation extends CameraMessageHandle {
        private Vec2 rotation;
        public SetRotation(Vec2 rotation) {
            this.rotation = rotation;
        }
        @Override
        public void encode(FriendlyByteBuf buffer) {
            buffer.writeFloat(rotation.x);
            buffer.writeFloat(rotation.y);
        }

        @Override
        public void accept(NetworkEvent.Context context) {
            CameraManager.getInstance().optionCameraRotation.activate(rotation);
        }
    }

    public static class SetLookAt extends CameraMessageHandle {
        private CameraLookTarget lookTarget;
        public SetLookAt(CameraLookTarget lookTarget) {
            this.lookTarget = lookTarget;
        }
        @Override
        public void encode(FriendlyByteBuf buffer) {
            lookTarget.encode(buffer);
        }

        @Override
        public void accept(NetworkEvent.Context context) {
            CameraManager.getInstance().optionCameraLookAt.activate(lookTarget);
        }
    }
}