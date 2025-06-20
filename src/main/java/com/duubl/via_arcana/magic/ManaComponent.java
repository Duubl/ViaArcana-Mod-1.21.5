package com.duubl.via_arcana.magic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ManaComponent implements IMana {
    private int mana;
    private int maxMana;
    private double regenRate;
    private double fractionalMana = 0.0f;  // Add this field to track fractional mana
    private static int manaCap = 200;  // Default cap, can be changed by items later
    private int regenCooldown = 0;  // Tracks ticks until mana regen can start
    private static final int REGEN_COOLDOWN_DURATION = 40;  // 2 seconds (40 ticks)

    // --- NEW: Codec for saving/loading to NBT (disk persistence) ---
    public static final Codec<ManaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("mana").forGetter(ManaComponent::getMana),
            Codec.INT.fieldOf("maxMana").forGetter(ManaComponent::getMaxMana),
            Codec.DOUBLE.fieldOf("regenRate").forGetter(ManaComponent::getRegenRate)
    ).apply(instance, ManaComponent::new));

    // --- NEW: StreamCodec for network synchronization ---
    public static final StreamCodec<FriendlyByteBuf, ManaComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ManaComponent::getMana,
            ByteBufCodecs.INT, ManaComponent::getMaxMana,
            ByteBufCodecs.DOUBLE, ManaComponent::getRegenRate,
            ManaComponent::new
    );

    // Constructor used by Codec/StreamCodec
    public ManaComponent(int mana, int maxMana, double regenRate) {
        this.mana = mana;
        this.maxMana = maxMana;
        this.regenRate = regenRate;
    }

    // Default values - now starts with 0 mana and 5 mana per second regen
    public ManaComponent() {
        this(0, 0, 5f);
    }

    // Implement all methods from IMana interface
    @Override
    public int getMana() { return mana; }

    @Override
    public int getMaxMana() { return maxMana; }

    @Override
    public void setMana(int mana) { 
        this.mana = Math.max(0, Math.min(mana, Math.min(maxMana, manaCap))); 
    }

    @Override
    public void addMana(int amount) { 
        setMana(this.mana + amount); 
    }

    public void addFractionalMana(double amount) {
            fractionalMana += amount;
        if (fractionalMana >= 1.0f) {
            int wholeMana = (int) fractionalMana;
            fractionalMana -= wholeMana;
            addMana(wholeMana);
        }
    }

    @Override
    public boolean consumeMana(double amount) {
        if (this.mana >= amount) {
            this.mana -= amount;
            this.regenCooldown = REGEN_COOLDOWN_DURATION;
            return true;
        }
        return false;
    }

    public void tick() {
        if (regenCooldown > 0) {
            regenCooldown--;
        } else if (mana < maxMana) {
            // Only regenerate if cooldown is 0 and we're not at max mana
            addFractionalMana(regenRate / 20.0f);  // Convert per-second rate to per-tick
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Mana", mana);
        tag.putInt("MaxMana", maxMana);
        tag.putDouble("RegenRate", regenRate);
        return tag;
    }

    public static ManaComponent load(CompoundTag tag) {
        ManaComponent component = new ManaComponent();
        component.setMana(tag.getInt("Mana").get());
        return component;
    }

    @Override
    public double getRegenRate() { return regenRate; }

    @Override
    public void setRegenRate(double rate) { this.regenRate = rate; }

    public ManaComponent copy() {
        return new ManaComponent(this.mana, this.maxMana, this.regenRate);
    }

    public static void setManaCap(int newCap) {
        manaCap = newCap;
    }

    public static int getManaCap() {
        return manaCap;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = Math.min(maxMana, manaCap);
        // Ensure current mana doesn't exceed new max
        this.mana = Math.min(this.mana, this.maxMana);
    }
}
