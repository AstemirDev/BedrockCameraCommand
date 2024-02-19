package ru.astemir.cameracommand.mixin;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.astemir.cameracommand.client.ExtendedOptions;

@Mixin(VideoSettingsScreen.class)
public abstract class MixinVideoSettingsScreen extends OptionsSubScreen {
    @Shadow private OptionsList list;

    public MixinVideoSettingsScreen(Screen p_96284_, Options p_96285_, Component p_96286_) {
        super(p_96284_, p_96285_, p_96286_);
    }

    @Inject(method = "init",at=@At("TAIL"))
    public void onInit(CallbackInfo ci){
        this.list.addSmall(new OptionInstance[]{((ExtendedOptions)options).getCameraCommandOption()});
    }
}
