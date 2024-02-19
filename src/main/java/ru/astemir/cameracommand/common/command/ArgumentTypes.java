package ru.astemir.cameracommand.common.command;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import ru.astemir.cameracommand.BedrockCameraCommand;

public class ArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, BedrockCameraCommand.MODID);
    public static final RegistryObject<SingletonArgumentInfo<CameraModeArgument>> CAMERA_MODE_ARGUMENT = COMMAND_ARGUMENT_TYPES.register("camera_mode", () ->
            ArgumentTypeInfos.registerByClass(CameraModeArgument.class,
                    SingletonArgumentInfo.contextFree(CameraModeArgument::mode)));
    public static final RegistryObject<SingletonArgumentInfo<EasingTypeArgument>> EASING_TYPE_ARGUMENT = COMMAND_ARGUMENT_TYPES.register("easing_type", () ->
            ArgumentTypeInfos.registerByClass(EasingTypeArgument.class,
                    SingletonArgumentInfo.contextFree(EasingTypeArgument::easing)));
}
