package de.bischinger.buchungstool.jsf;

import de.bischinger.buchungstool.business.ConfigRepository;
import de.bischinger.buchungstool.model.Config;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by bischofa on 25/01/17.
 */
@ApplicationScoped
@Named
public class ConfigDetailBean {
    @Inject
    private ConfigRepository configRepository;

    private Config config;

    @PostConstruct
    public void init() {
        config = configRepository.getConfig();
    }

    public Config getConfig() {
        return config;
    }

    public void submit() {
        configRepository.save(config);
    }
}
