package domain.transport;

import domain.Asset;
import domain.BeginnerFriendly;


public abstract class Transport extends Asset implements BeginnerFriendly {
    private final int beginnerFriendliness;

    protected Transport(String name, String inventoryNumber, int beginnerFriendliness) {
        super(name, inventoryNumber);

        if (beginnerFriendliness < 1 || beginnerFriendliness > 10) {
            throw new IllegalArgumentException("Простота для новичка должна быть в диапазоне 1..10");
        }

        this.beginnerFriendliness = beginnerFriendliness;
    }

    @Override
    public int getBeginnerFriendliness() {
        return beginnerFriendliness;
    }
}
