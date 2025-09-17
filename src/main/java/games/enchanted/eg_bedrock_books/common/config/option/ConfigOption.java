package games.enchanted.eg_bedrock_books.common.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigOption<T> {
    private @Nullable T pendingValue;
    protected T value;
    private final T defaultValue;

    private final String jsonKey;

    ConfigOption(T initialValue, T defaultValue, String jsonKey) {
        this.value = initialValue;
        this.defaultValue = defaultValue;
        this.jsonKey = jsonKey;
    }

    public String getJsonKey() {
        return this.jsonKey;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public T getValue() {
        return this.value;
    }

    public @Nullable T getPendingValue() {
        return pendingValue;
    }

    public void setPendingValue(@NotNull T value) {
        this.pendingValue = value;
    }

    protected void setValueOrPending(T value) {
        if(isDirty()) {
            this.setPendingValue(value);
        } else {
            this.value = value;
        }
    }

    public void clearPendingValue() {
        this.pendingValue = null;
    }

    public void applyPendingValue() {
        if(this.pendingValue == null) return;
        this.value = this.pendingValue;
        this.pendingValue = null;
    }

    public abstract JsonElement toJson();

    public abstract void fromJson(JsonObject json);

    public boolean isDirty() {
        return this.pendingValue != null;
    }
}
