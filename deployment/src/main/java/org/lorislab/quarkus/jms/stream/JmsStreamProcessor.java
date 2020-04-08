package org.lorislab.quarkus.jms.stream;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class JmsStreamProcessor {

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("lorislab-jms");
    }
}
