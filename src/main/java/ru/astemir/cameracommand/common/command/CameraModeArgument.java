package ru.astemir.cameracommand.common.command;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.astemir.cameracommand.common.camera.CameraMode;
import ru.astemir.cameracommand.data.ServerCameraModeManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CameraModeArgument implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("first_person", "minecraft:first_person");
    private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((obj) -> Component.translatable("argument.camera_mode.invalid", obj));

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return context.getSource() instanceof SharedSuggestionProvider ?SharedSuggestionProvider.suggestResource(ServerCameraModeManager.getCameraModes().stream().map(CameraMode::getLocation),builder) : Suggestions.empty();
    }

    public static CameraModeArgument mode() {
        return new CameraModeArgument();
    }

    public static CameraMode getMode(final CommandContext<?> context, final String name) throws CommandSyntaxException{
        ResourceLocation resourcelocation = context.getArgument(name, ResourceLocation.class);
        CameraMode cameraMode = ServerCameraModeManager.getCameraMode(resourcelocation);
        if (cameraMode == null) {
            throw ERROR_INVALID_VALUE.create(resourcelocation);
        } else {
            return cameraMode;
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}