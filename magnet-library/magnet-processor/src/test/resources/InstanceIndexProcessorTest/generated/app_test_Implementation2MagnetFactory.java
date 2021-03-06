package magnet.index;

import app.test.Implementation2MagnetFactory;
import magnet.internal.Index;
import magnet.internal.InstanceFactory;

@Index(
    factoryType = InstanceFactory.class,
    factoryClass = Implementation2MagnetFactory.class,
    instanceType = "app.Interface2",
    classifier = ""
)
public final class app_test_Implementation2MagnetFactory {}