package domain;

public interface BeginnerFriendly {
    int MIN_SCORE_FOR_BEGINNERS = 6;

    int getBeginnerFriendliness();

    default boolean isSuitableForBeginners() {
        return getBeginnerFriendliness() >= MIN_SCORE_FOR_BEGINNERS;
    }
}