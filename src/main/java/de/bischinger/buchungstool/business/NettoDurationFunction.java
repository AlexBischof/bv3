package de.bischinger.buchungstool.business;

import java.util.function.Function;

/**
 * Created by bischofa on 28/06/16.
 */
public class NettoDurationFunction implements Function<Integer, Integer> {

    //TODO
    private boolean winter = false;

    @Override
    public Integer apply(Integer bruttoDuration) {
        return bruttoDuration > (winter ? 300 : 240)  ? bruttoDuration - 30 : bruttoDuration;
    }
}
