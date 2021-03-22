package org.regadou.jukebox;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

@QuarkusTest
public class JukeboxRepositoryTest {
    
    @Inject
    SettingRepository settingRepo;
    @Inject
    JukeboxRepository jukeboxRepo;
    @Inject
    ComponentRepository componentRepo;

    private Jukebox j1, j2, j3;
    private JukeboxComponent c0, c1, c2, c3;
    private Setting s0, s1, s2, s3;
    
    @BeforeEach
    @Transactional
    public void setup() {
        componentRepo.persist(c0 = new JukeboxComponent("c0"));
        componentRepo.persist(c1 = new JukeboxComponent("c1"));
        componentRepo.persist(c2 = new JukeboxComponent("c2"));
        componentRepo.persist(c3 = new JukeboxComponent("c3"));
        jukeboxRepo.persist(j1 = new Jukebox("j1", "m1", c1));
        jukeboxRepo.persist(j2 = new Jukebox("j2", "m2", c1, c2));
        jukeboxRepo.persist(j3 = new Jukebox("j3", "m3", c2, c3));
        settingRepo.persist(s0 = new Setting("s0", c0));
        settingRepo.persist(s1 = new Setting("s1", c1));
        settingRepo.persist(s2 = new Setting("s2", c2));
        settingRepo.persist(s3 = new Setting("s3", c2, c3));
    }

    @AfterEach
    @Transactional
    public void cleanup() {
        jukeboxRepo.deleteAll();
        settingRepo.deleteAll();
        componentRepo.deleteAll();
    }

    @Test
    public void testAbsentSettingFindsNoJukebox() {
        List<Jukebox> jukesFound = jukeboxRepo.findModelAndSetting(null, s0, null, null);
        Assertions.assertEquals(0, jukesFound.size());
    }
    
    @Test
    public void testSingleSettingFindsBothJukebox() {
        List<Jukebox> jukesFound = jukeboxRepo.findModelAndSetting(null, s1, null, null);
        Assertions.assertEquals(2, jukesFound.size());
    }
    
    @Test
    public void testSingleSetting2FindsBothJukebox() {
        List<Jukebox> jukesFound = jukeboxRepo.findModelAndSetting(null, s2, null, null);
        Assertions.assertEquals(2, jukesFound.size());
    }
    
    @Test
    public void testDualSettingFindsSingleJukebox() {        
        List<Jukebox> jukesFound = jukeboxRepo.findModelAndSetting(null, s3, null, null);
        Assertions.assertEquals(1, jukesFound.size());
    }
    
    @Test
    public void testLimitOneReturnsFirstJukebox() {        
        List<Jukebox> jukesFound = jukeboxRepo.findModelAndSetting(null, s1, 1, null);
        Assertions.assertEquals(1, jukesFound.size());
        Assertions.assertEquals("j1", jukesFound.get(0).getId());
    }
    
    @Test
    public void testLimitOneOffset1ReturnsSecondJukebox() {        
        List<Jukebox> jukesFound = jukeboxRepo.findModelAndSetting(null, s1, 1, 1);
        Assertions.assertEquals(1, jukesFound.size());
        Assertions.assertEquals("j2", jukesFound.get(0).getId());
    }
    
    @Test
    public void testTooHighOffsetReturnsNoJukebox() {        
        List<Jukebox> jukesFound = jukeboxRepo.findModelAndSetting(null, s1, null, 3);
        Assertions.assertEquals(0, jukesFound.size());
    }
    
    @Test
    public void testMdelFilterReturnsAppropriateJukebox() {        
        List<Jukebox> jukesFound = jukeboxRepo.findModelAndSetting("m1", s1, null, null);
        Assertions.assertEquals(1, jukesFound.size());
        Assertions.assertEquals("m1", jukesFound.get(0).getModel());
    }
}
