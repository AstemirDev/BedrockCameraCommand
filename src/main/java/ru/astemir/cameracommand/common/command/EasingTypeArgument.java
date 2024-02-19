package ru.astemir.cameracommand.common.command;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.command.EnumArgument;
import ru.astemir.cameracommand.common.camera.CameraMode;
import ru.astemir.cameracommand.common.camera.EasingType;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class EasingTypeArgument implements ArgumentType<EasingType> {
    private static final Collection<String> EXAMPLES = Arrays.asList("linear", "spring","in_cubic");
    private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((obj) -> Component.translatable("argument.easing_type.invalid", obj));

    @Override
    public EasingType parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        try {
            return EasingType.valueOf(name.toUpperCase());
        } catch (ResourceLocationException resourcelocationexception) {
            throw ERROR_INVALID_VALUE.createWithContext(reader,name);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return context.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(EasingType.names(),builder) : Suggestions.empty();
    }

    public static EasingTypeArgument easing() {
        return new EasingTypeArgument();
    }

    public static EasingType getEasing(final CommandContext<?> context, final String name) throws CommandSyntaxException{
        EasingType easingType = context.getArgument(name, EasingType.class);
        if (easingType == null) {
            throw ERROR_INVALID_VALUE.create(name);
        } else {
            return easingType;
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}