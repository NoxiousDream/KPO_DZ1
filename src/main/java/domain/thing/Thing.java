package domain.thing;

import domain.Asset;

public abstract class Thing extends Asset {
    protected Thing(String name, String inventoryNumber) {
        super(name, inventoryNumber);
    }
}