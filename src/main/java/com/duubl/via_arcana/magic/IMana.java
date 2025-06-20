package com.duubl.via_arcana.magic;

public interface IMana {
    int getMana();

    void setMana(int mana);

    void addMana(int amount);

    boolean consumeMana(double amount);

    int getMaxMana();

    double getRegenRate();
    void setRegenRate(double rate);
}
