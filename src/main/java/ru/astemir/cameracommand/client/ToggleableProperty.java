package ru.astemir.cameracommand.client;

import java.util.function.Consumer;

public class ToggleableProperty<T> {
    private T value;
    private boolean enabled;
    private Consumer<T> onActivated;
    private Consumer<T> onDeactivated;
    public void activate(T value) {
        this.value = value;
        this.enabled = true;
        if (this.onActivated != null) {
            this.onActivated.accept(value);
        }
    }

    public void deactivate(){
        if (this.enabled) {
            this.enabled = false;
            if (this.onDeactivated != null) {
                this.onDeactivated.accept(this.value);
            }
        }
    }

    public ToggleableProperty<T> onActivated(Consumer<T> onActivated) {
        this.onActivated = onActivated;
        return this;
    }

    public ToggleableProperty<T> onDeactivated(Consumer<T> onDeactivated) {
        this.onDeactivated = onDeactivated;
        return this;
    }

    public T getValue() {
        return value;
    }
    public boolean isEnabled() {
        return enabled;
    }
}
