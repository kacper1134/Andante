package org.andante.activity.utils;

import org.andante.activity.configuration.ActivityTestConfiguration;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Import(ActivityTestConfiguration.class)
public class ActivityTestUtils {

    private final EasyRandom generator;

    @Autowired
    public ActivityTestUtils(@Qualifier("Activities") EasyRandom generator) {
        this.generator = generator;
    }

    public <T> T generate(Class<T> type) {
        return generator.nextObject(type);
    }

    public <T> Set<T> generate(Class<T> type, int count) {
        return generator.objects(type, count).collect(Collectors.toSet());
    }
}
