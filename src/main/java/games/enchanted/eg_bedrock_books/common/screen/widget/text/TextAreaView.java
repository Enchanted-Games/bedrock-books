package games.enchanted.eg_bedrock_books.common.screen.widget.text;

import net.minecraft.client.gui.components.AbstractWidget;

public interface TextAreaView<V> {
    void setValue(V value, boolean force);
    V getValue();

    void setVisibility(boolean visibility);
}
