package ru.astemir.cameracommand.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.astemir.cameracommand.BedrockCameraCommand;
import ru.astemir.cameracommand.common.camera.CameraLookTarget;
import ru.astemir.cameracommand.common.camera.CameraMode;
import ru.astemir.cameracommand.common.camera.EasingType;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = BedrockCameraCommand.MODID,value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class CameraManager {
    public Tweener positionTweener = new Tweener();
    public Tweener rotationTweener = new Tweener();
    private EasingType easingType = EasingType.LINEAR;
    public ToggleableProperty<CameraMode> optionCameraMode = new ToggleableProperty();
    public ToggleableProperty<Vec3> optionCameraPosition = new ToggleableProperty<>();
    public ToggleableProperty<Vec2> optionCameraRotation = new ToggleableProperty<>();
    public ToggleableProperty<CameraLookTarget> optionCameraLookAt = new ToggleableProperty<>();
    public ToggleableProperty<Float> optionCameraEasingTime = new ToggleableProperty<Float>().
            onActivated((f)->{
                Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
                if (this.optionCameraPosition.isEnabled()){
                    Vec3 pos = getPosition();
                    if (pos == null){
                        pos = camera.getPosition();
                    }
                    positionTweener.tween(pos,this.optionCameraPosition.getValue(),easingType,f);
                }
                if (this.optionCameraRotation.isEnabled()){
                    Vec2 rot = getRotation();
                    if (rot == null){
                        rot = new Vec2(camera.getXRot(),camera.getYRot());
                    }
                    rotationTweener.tween(rot,this.optionCameraRotation.getValue(),easingType,f);
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
            Vec3 lookAtPos = new Vec3(0,0,0);
            if (lookTarget.isLookingAtEntity()) {
                Entity lookAtEntity = Minecraft.getInstance().level.getEntity(lookTarget.getLookAtEntityId());
                if (lookAtEntity == null){
                    optionCameraLookAt.deactivate();
                }else {
                    lookAtPos = new Vec3(Mth.lerp(partialTick,lookAtEntity.xo,lookAtEntity.getX()),Mth.lerp(partialTick,lookAtEntity.yo,lookAtEntity.getY())+lookAtEntity.getBbHeight()/2,Mth.lerp(partialTick,lookAtEntity.zo,lookAtEntity.getZ()));
                }
            }else
            if (lookTarget.isLookingAtPos()){
                lookAtPos = lookTarget.getLookAtPos();
            }
            Vec3 diff = new Vec3((float) (lookAtPos.x - position.x),(float) (lookAtPos.y - position.y),(float) (lookAtPos.z - position.z));
            float xRot = (float)Mth.wrapDegrees((-(Mth.atan2(diff.y, (float) Math.sqrt(diff.x * diff.x + diff.z * diff.z)) * (double)(180F / (float)Math.PI))));
            float yRot = (float)Mth.wrapDegrees((Mth.atan2(diff.z, diff.x) * (double)(180F / (float)Math.PI))) - 90.0F;
            return new Vec2(xRot, yRot);
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
        float xRot;
        float yRot;
        if (optionCameraLookAt.isEnabled()) {
            xRot = Mth.wrapDegrees(lookAtRotation.x);
            yRot = Mth.wrapDegrees(lookAtRotation.y);
        }else{
            xRot = Mth.wrapDegrees(rotation.x);
            yRot = Mth.wrapDegrees(rotation.y);
        }
        return new Vec2(xRot,yRot);
    }

    public EasingType getEasingType() {
        return easingType;
    }

    public static CameraManager getInstance(){
        return BedrockCameraCommand.getCameraManager();
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
