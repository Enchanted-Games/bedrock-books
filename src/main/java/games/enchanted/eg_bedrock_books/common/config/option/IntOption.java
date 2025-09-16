package games.enchanted.eg_bedrock_books.common.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class IntOption extends ConfigOption<Integer> {
    public IntOption(Integer initialValue, Integer defaultValue, String jsonKey) {
        super(initialValue, defaultValue, jsonKey);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonObject json) {
        Integer value = json.has(getJsonKey()) ? json.get(getJsonKey()).getAsInt() : getDefaultValue();
        if(isDirty()) {
            this.setPendingValue(value);
        } else {
            this.value = value;
        }
    }
}
