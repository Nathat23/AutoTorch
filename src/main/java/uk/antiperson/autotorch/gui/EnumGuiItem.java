package uk.antiperson.autotorch.gui;

public class EnumGuiItem extends DynamicGuiItem {

    private final Enum<?>[] values;

    public EnumGuiItem(Enum<?>[] values) {
        this.values = values;
    }

    public Enum<?> getValue() {
        return values[getShowing()];
    }

    public void populate(GuiItemStack guiItemStack) {
        for (Enum<?> value : values) {
            GuiItemStack guiItemStack1 = guiItemStack.createItemStack("%enum%", value.toString());
            addItem(value.ordinal(), guiItemStack1);
        }
    }

    public void setShowing(Enum<?> value) {
        setShowing(value.ordinal());
    }
}
