package games.enchanted.eg_bedrock_books.common.screen.widget.text;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ComponentTextAreaView implements TextAreaView<Component> {
    private final Consumer<Component> valueSetter;
    private Component value;
    private boolean visible = true;

    public ComponentTextAreaView(Consumer<Component> valueSetter) {
        this.valueSetter = valueSetter;
    }

    @Override
    public void setValue(Component value, boolean force) {
        this.value = value;
        this.valueSetter.accept(value);
    }

    @Override
    public Component getValue() {
        return this.visible ? this.value : CommonComponents.EMPTY;
    }

    @Override
    public void setVisibility(boolean visibility) {
        this.visible = visibility;
        this.valueSetter.accept(getValue());
    }
}
