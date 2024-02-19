package ru.astemir.cameracommand.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.astemir.cameracommand.BedrockCameraCommand;
import ru.astemir.cameracommand.common.camera.CameraLookTarget;
import ru.astemir.cameracommand.common.camera.CameraMode;
import ru.astemir.cameracommand.common.camera.EasingType;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = BedrockCameraCommand.MODID)
public class CameraManager {
    public Tweener positionTweener = new Tweener();
    private Tweener rotationTweener = new Tweener();
    private EasingType easingType = EasingType.LINEAR;
    public ToggleableProperty<CameraMode> optionCameraMode = new ToggleableProperty();
    public ToggleableProperty<Vec3> optionCameraPosition = new ToggleableProperty<>();
    public ToggleableProperty<Vec2> optionCameraRotation = new ToggleableProperty<>();
    public ToggleableProperty<CameraLookTarget> optionCameraLookAt = new ToggleableProperty<>();
    public ToggleableProperty<Float> optionCameraEasingTime = new ToggleableProperty<Float>().
            onActivated((f)->{
                if (optionCameraPosition.isEnabled()) {
                    positionTweener.tween(getPosition(), optionCameraPosition.getValue(),easingType, f);
                }
                if (optionCameraRotation.isEnabled()) {
                    rotationTweener.tween(getRotation(), optionCameraRotation.getValue(),easingType, f);
                }
            });
    public Vec3 position;
    public Vec2 rotation;
    public Vec2 lookAtRotation;

    public void setup(Camera camera, BlockGetter level, Entity cameraEntity, float partialTick) {
        double deltaFrame = Minecraft.getInstance().getDeltaFrameTime();
        if (this.position == null){
            this.position = camera.getPosition();
        }
        if (this.rotation == null){
            this.rotation = new Vec2(0,0);
        }
        if (this.lookAtRotation == null){
            this.lookAtRotation = new Vec2(0,0);
        }
        if (this.optionCameraPosition.isEnabled()) {
            Vec3 newPos = optionCameraPosition.getValue();
            if (this.positionTweener.isEnabled()) {
                this.position = positionTweener.update();
            } else {
                this.position = new Vec3(Mth.lerp(deltaFrame, position.x, newPos.x), Mth.lerp(deltaFrame, position.y, newPos.y), Mth.lerp(deltaFrame, position.z, newPos.z));
            }
        }
        if (this.optionCameraLookAt.isEnabled()){
            Vec2 newRot = calculateLookAtRot();
            this.lookAtRotation = new Vec2(Mth.rotLerp((float) deltaFrame, lookAtRotation.x, newRot.x), Mth.rotLerp((float) deltaFrame, lookAtRotation.y, newRot.y));
        }
        if (this.optionCameraRotation.isEnabled()) {
            if (this.rotationTweener.isEnabled()) {
                this.rotation = rotationTweener.update();
            } else {
                Vec2 newRot = optionCameraRotation.getValue();
                this.rotation = new Vec2(Mth.rotLerp((float) deltaFrame, rotation.x, newRot.x), Mth.rotLerp((float) deltaFrame, rotation.y, newRot.y));
            }
        }
    }

    public Vec2 calculateLookAtRot(){
        if (optionCameraLookAt.isEnabled()) {
            float partialTick = Minecraft.getInstance().getPartialTick();
            CameraLookTarget lookTarget = optionCameraLookAt.getValue();
            if (lookTarget.isLookingAtEntity()) {
                Entity lookAtEntity = Minecraft.getInstance().level.getEntity(lookTarget.getLookAtEntityId());
                if (lookAtEntity == null){
                    optionCameraLookAt.deactivate();
                }else {
                    float deltaX = (float) (Mth.lerp(partialTick, lookAtEntity.xo, lookAtEntity.getX()) - position.x);
                    float deltaY = (float) (Mth.lerp(partialTick, lookAtEntity.yo, lookAtEntity.getY())+lookAtEntity.getEyeHeight()/2 - position.y);
                    float deltaZ = (float) (Mth.lerp(partialTick, lookAtEntity.zo, lookAtEntity.getZ()) - position.z);
                    float xRot = (float)Mth.wrapDegrees((-(Mth.atan2(deltaY, (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * (double)(180F / (float)Math.PI))));
                    float yRot = (float)Mth.wrapDegrees((Mth.atan2(deltaZ, deltaX) * (double)(180F / (float)Math.PI))) - 90.0F;
                    return new Vec2(xRot, yRot);
                }
            }else
            if (lookTarget.isLookingAtPos()){
                Vec3 lookAtPos = lookTarget.getLookAtPos();
                float deltaX = (float) (lookAtPos.x - position.x);
                float deltaY = (float) (lookAtPos.y - position.y);
                float deltaZ = (float) (lookAtPos.z - position.z);
                float xRot = (float)Mth.wrapDegrees((-(Mth.atan2(deltaY, (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * (double)(180F / (float)Math.PI))));
                float yRot = (float)Mth.wrapDegrees((Mth.atan2(deltaZ, deltaX) * (double)(180F / (float)Math.PI))) - 90.0F;
                return new Vec2(xRot, yRot);
            }
        }
        return new Vec2(0,0);
    }

    public void clear(){
        this.lookAtRotation = null;
        this.position = null;
        this.rotation = null;
        this.easingType = EasingType.LINEAR;
        this.positionTweener = new Tweener();
        this.rotationTweener = new Tweener();
        this.optionCameraMode.deactivate();
        this.optionCameraPosition.deactivate();
        this.optionCameraRotation.deactivate();
        this.optionCameraEasingTime.deactivate();
        this.optionCameraLookAt.deactivate();
        CameraFade.getInstance().clear();
    }


    public boolean isEnabled() {
        if (!ExtendedOptions.isCameraCommandEnabled()){
            return false;
        }
        return true;
    }

    public void setEasingType(EasingType easingType) {
        this.easingType = easingType;
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec2 getRotation() {
        return rotation;
    }

    public Vec2 getGlobalRotation(){
        float xRot = Mth.wrapDegrees(rotation.x+lookAtRotation.x);
        float yRot = Mth.wrapDegrees(rotation.y+lookAtRotation.y);
        return new Vec2(xRot,yRot);
    }

    public EasingType getEasingType() {
        return easingType;
    }

    public static CameraManager getInstance(){
        return BedrockCameraCommand.getCameraProperties();
    }

    @SubscribeEvent
    public static void onCameraFov(ComputeFovModifierEvent e){
        if (CameraManager.getInstance().isEnabled()) {
            if (ExtendedCamera.getCameraMode().isFree()) {
                e.setNewFovModifier(1.0f);
            }
        }
    }
}
