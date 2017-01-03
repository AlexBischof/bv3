package de.bischinger.buchungstool.business.importer;

import de.bischinger.buchungstool.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by bischofa on 28/06/16.
 */
public class NameToUserFunction implements Function<String, List<User>> {

    @Override
    public List<User> apply(String originalName) {

        List<User> users = new ArrayList<>();

        NameNormalization nameNormalization = new NameNormalization();

        String[] split = originalName.split(" bei|für");
        if (split.length == 1) {
            String normedOriginalName = nameNormalization.apply(originalName);
            users.add(new User(normedOriginalName, originalName));
        } else {
            users.add(new User(nameNormalization.apply(split[0]), split[0]));

            //Kranker or PairingUser
            String secondUsername = split[1];
            if (secondUsername.contains("krank")) {
                secondUsername = secondUsername.substring(0, secondUsername.indexOf("krank")).trim();
            }
            secondUsername = secondUsername.replace(")","").trim();
            users.add(new User(nameNormalization.apply(secondUsername), secondUsername));
        }

        return users;
    }
}
