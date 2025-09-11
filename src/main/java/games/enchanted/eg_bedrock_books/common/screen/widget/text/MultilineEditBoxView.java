package games.enchanted.eg_bedrock_books.common.screen.widget.text;

import net.minecraft.client.gui.components.MultiLineEditBox;

public class MultilineEditBoxView implements TextAreaView<String> {
    private final MultiLineEditBox editBox;

    public MultilineEditBoxView(MultiLineEditBox editBox) {
        this.editBox = editBox;
    }

    @Override
    public void setValue(String value, boolean force) {
        this.editBox.setValue(value, force);
    }

    @Override
    public String getValue() {
        return this.editBox.getValue();
    }

    @Override
    public void setVisibility(boolean visibility) {
        this.editBox.visible = visibility;
    }
}
