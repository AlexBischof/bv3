package de.bischinger.buchungstool.business;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * Created by bischofa on 28/06/16.
 */
public class NettoDurationFunction implements Function<Integer, Integer> {

    private final BooleanSupplier isSommerSupplier;

    public NettoDurationFunction(BooleanSupplier isSommerSupplier) {
        this.isSommerSupplier = isSommerSupplier;
    }

    @Override
    public Integer apply(Integer bruttoDuration) {
        return bruttoDuration > (isSommerSupplier.getAsBoolean() ? 240 : 300) ? bruttoDuration - 30 : bruttoDuration;
    }
}
