package ru.astemir.cameracommand.mixin;

import net.minecraft.client.CameraType;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.astemir.cameracommand.client.ExtendedOptions;
import ru.astemir.cameracommand.common.camera.CameraMode;
import ru.astemir.cameracommand.client.CameraManager;
import ru.astemir.cameracommand.client.ToggleableProperty;

@Mixin(Options.class)
public class MixinOptions implements ExtendedOptions {
    private final OptionInstance<Boolean> cameraCommand = OptionInstance.createBoolean("options.cameraCommand",true);
    private final OptionInstance<Boolean> cameraIsFree = OptionInstance.createBoolean("options.cameraFree",false);

    @Inject(method = "getCameraType",at = @At("HEAD"),cancellable = true)
    public void onGetCameraMode(CallbackInfoReturnable<CameraType> cir){
        CameraManager cameraManager = CameraManager.getInstance();
        if (cameraManager.isEnabled()) {
            ToggleableProperty<CameraMode> cameraModeProperty = cameraManager.optionCameraMode;
            if (cameraModeProperty.isEnabled()) {
                CameraMode cameraMode = cameraModeProperty.getValue();
                switch (cameraMode.getParent()) {
                    case FIRST_PERSON -> cir.setReturnValue(CameraType.FIRST_PERSON);
                    case THIRD_PERSON -> cir.setReturnValue(CameraType.THIRD_PERSON_FRONT);
                    case THIRD_PERSON_BACK, FREE -> cir.setReturnValue(CameraType.THIRD_PERSON_BACK);
                }
            }else{
                if (ExtendedOptions.isFreeCam()){
                    cir.setReturnValue(CameraType.THIRD_PERSON_BACK);
                }
            }
        }
    }

    @Inject(method = "processOptions",at = @At("HEAD"))
    public void onProcessOptions(Options.FieldAccess fieldAccess, CallbackInfo ci){
        fieldAccess.process("cameraCommand",cameraCommand);
    }

    @Override
    public OptionInstance<Boolean> getCameraCommandOption() {
        return cameraCommand;
    }

    @Override
    public OptionInstance<Boolean> getCameraIsFreeOption() {
        return cameraIsFree;
    }
}
