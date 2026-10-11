package domain;

public abstract class Asset implements InventoryItem {
    private final String name;
    private final String inventoryNumber;

    protected Asset(String name, String inventoryNumber) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Наименование не может быть пустым");
        }

        if (inventoryNumber == null || inventoryNumber.isBlank()) {
            throw new IllegalArgumentException("Инвентарный номер не может быть пустым");
        }

        this.name = name;
        this.inventoryNumber = inventoryNumber;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInventoryNumber() {
        return inventoryNumber;
    }

    @Override
    public String toString() {
        return name + " (" + inventoryNumber + ")";
    }
}