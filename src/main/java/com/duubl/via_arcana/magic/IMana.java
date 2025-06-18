package com.duubl.via_arcana.magic;

public interface IMana {
    int getMana();

    void setMana(int mana);

    void addMana(int amount);

    boolean consumeMana(int amount);

    int getMaxMana();

    float getRegenRate();
    void setRegenRate(float rate);
}
