package magnet.internal;

import magnet.Classifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RuntimeInstancesTest {

    @Mock private Factory1 factory1;
    @Mock private Factory2 factory2;

    @Mock private Interface1 instance1;
    @Mock private Interface1 instance2;

    @Test
    @SuppressWarnings("unchecked")
    public void test_getScopeDepth() {
        RuntimeInstances<String> instances = new RuntimeInstances(
            1, factory1, Interface1.class, instance1, Classifier.NONE
        );
        int depth = instances.getScopeDepth();
        assertThat(depth).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_getInstances_SingleItem() {
        RuntimeInstances<Interface1> instances = new RuntimeInstances(
            1, factory1, Interface1.class, instance1, Classifier.NONE
        );
        List<Interface1> result = instances.getInstances();
        assertThat(result).containsExactly(instance1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_getInstances_ManyItems() {
        RuntimeInstances<Interface1> instances = new RuntimeInstances(
            1, factory1, Interface1.class, instance1, Classifier.NONE
        );
        instances.registerInstance(factory2, Interface1.class, instance2, Classifier.NONE);
        List<Interface1> result = instances.getInstances();
        assertThat(result).containsExactly(instance1, instance2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_getSingleInstance() {
        RuntimeInstances<Interface1> instances = new RuntimeInstances(
            1, factory1, Interface1.class, instance1, Classifier.NONE
        );
        Interface1 result = instances.getSingleInstance();
        assertThat(result).isSameAs(instance1);
    }

    interface Interface1 {}
    abstract static class Factory1 extends InstanceFactory<Interface1> {}
    abstract static class Factory2 extends InstanceFactory<Interface1> {}
}