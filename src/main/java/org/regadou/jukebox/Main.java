package org.regadou.jukebox;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.inject.Inject;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    DataInitializer initializer;

    @Override
    public int run(String... args) throws Exception {
        initializer.initialize();
        Quarkus.waitForExit();
        return 0;
    }    
}
