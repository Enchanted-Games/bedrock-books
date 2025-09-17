package games.enchanted.eg_bedrock_books.common.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.platform.InputConstants;

public class KeyOption extends ConfigOption<InputConstants.Key>{
    public KeyOption(InputConstants.Key initialValue, InputConstants.Key defaultValue, String jsonKey) {
        super(initialValue, defaultValue, jsonKey);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue().getName());
    }

    @Override
    public void fromJson(JsonObject json) {
        InputConstants.Key value = json.has(getJsonKey()) ? InputConstants.getKey(json.get(getJsonKey()).getAsString()) : getDefaultValue();
        this.setValueOrPending(value);
    }
}
