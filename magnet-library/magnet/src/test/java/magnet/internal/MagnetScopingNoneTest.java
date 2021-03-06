/*
 * Copyright (C) 2018 Sergej Shafarenka, www.halfbit.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package magnet.internal;

import magnet.Scope;
import magnet.Scoping;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class MagnetScopingNoneTest {

    private InstrumentedInstanceScope scope1;
    private InstrumentedInstanceScope scope2;
    private InstrumentedInstanceScope scope3;

    @Before
    public void before() {
        InstanceManager instanceManager = new StubInstanceManager();
        scope1 = (InstrumentedInstanceScope) new InstrumentedInstanceScope(
            new MagnetScope(null, instanceManager))
            .bind(Dependency1.class, new Dependency1());

        scope2 = (InstrumentedInstanceScope) scope1
            .createSubscope()
            .bind(Dependency2.class, new Dependency2());

        scope3 = (InstrumentedInstanceScope) scope2
            .createSubscope()
            .bind(Dependency3.class, new Dependency3());
    }

    @Test
    public void itemOne_requestedWithinScope1() {
        // when
        MenuItem menuItem = scope1.getSingle(MenuItem.class, "one");

        // then
        assertThat(menuItem).isNotNull();
        assertThat(scope3.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope2.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope1.getOptionalInScope(MenuItem.class, "one")).isNull();
    }

    @Test
    public void itemOne_requestedWithinScope2() {
        // when
        MenuItem menuItem = scope2.getSingle(MenuItem.class, "one");

        // then
        assertThat(menuItem).isNotNull();
        assertThat(scope3.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope2.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope1.getOptionalInScope(MenuItem.class, "one")).isNull();
    }

    @Test
    public void itemOne_requestedWithinScope3() {
        // when
        MenuItem menuItem = scope3.getSingle(MenuItem.class, "one");

        // then
        assertThat(menuItem).isNotNull();
        assertThat(scope3.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope2.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope1.getOptionalInScope(MenuItem.class, "one")).isNull();
    }

    @Test(expected = IllegalStateException.class)
    public void itemTwo_requestedWithinScope1() {
        // when
        scope1.getSingle(MenuItem.class, "two");
    }

    @Test
    public void itemTwo_requestedWithinScope2() {
        // when
        MenuItem menuItem = scope2.getSingle(MenuItem.class, "two");

        // then
        assertThat(menuItem).isNotNull();
        assertThat(scope3.getOptionalInScope(MenuItem.class, "two")).isNull();
        assertThat(scope2.getOptionalInScope(MenuItem.class, "two")).isEqualTo(menuItem);
        assertThat(scope1.getOptionalInScope(MenuItem.class, "two")).isNull();

        assertThat(scope3.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope2.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope1.getOptionalInScope(MenuItem.class, "one")).isNull();
    }

    @Test
    public void itemTwo_requestedWithinScope3() {
        // when
        MenuItem menuItem = scope3.getSingle(MenuItem.class, "two");

        // then
        assertThat(menuItem).isNotNull();
        assertThat(scope3.getOptionalInScope(MenuItem.class, "two")).isNull();
        assertThat(scope2.getOptionalInScope(MenuItem.class, "two")).isEqualTo(menuItem);
        assertThat(scope1.getOptionalInScope(MenuItem.class, "two")).isNull();

        assertThat(scope3.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope2.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope1.getOptionalInScope(MenuItem.class, "one")).isNull();
    }

    @Test(expected = IllegalStateException.class)
    public void itemThree_requestedWithinScope1() {
        // when
        scope1.getSingle(MenuItem.class, "three");
    }

    @Test(expected = IllegalStateException.class)
    public void itemThree_requestedWithinScope2() {
        // when
        scope2.getSingle(MenuItem.class, "three");
    }

    @Test
    public void itemThree_requestedWithinScope3() {
        // when
        MenuItem menuItem = scope3.getSingle(MenuItem.class, "three");

        // then
        assertThat(menuItem).isNotNull();
        assertThat(scope3.getOptionalInScope(MenuItem.class, "two")).isNull();
        assertThat(scope2.getOptionalInScope(MenuItem.class, "two")).isNotNull();
        assertThat(scope1.getOptionalInScope(MenuItem.class, "two")).isNull();

        assertThat(scope3.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope2.getOptionalInScope(MenuItem.class, "one")).isNull();
        assertThat(scope1.getOptionalInScope(MenuItem.class, "one")).isNull();
    }

    private static class MenuItemOneFactory extends InstanceFactory<MenuItem> {
        @Override public MenuItem create(Scope scope) {
            scope.getSingle(Dependency1.class);
            return new MenuItemOne();
        }
        @Override public Scoping getScoping() { return Scoping.UNSCOPED; }
    }

    private static class MenuItemTwoFactory extends InstanceFactory<MenuItem> {
        @Override public MenuItem create(Scope scope) {
            scope.getSingle(Dependency1.class);
            scope.getSingle(Dependency2.class);
            scope.getSingle(MenuItem.class, "one");
            return new MenuItemTwo();
        }
        @Override public Scoping getScoping() { return Scoping.TOPMOST; }
    }

    private static class MenuItemThreeFactory extends InstanceFactory<MenuItem> {
        @Override public MenuItem create(Scope scope) {
            scope.getSingle(Dependency1.class);
            scope.getSingle(Dependency3.class);
            scope.getSingle(MenuItem.class, "one");
            scope.getSingle(MenuItem.class, "two");
            return new MenuItemThree();
        }
        @Override public Scoping getScoping() { return Scoping.UNSCOPED; }
    }

    private static class StubInstanceManager implements InstanceManager {
        private final Map<String, InstanceFactory<MenuItem>> factories;

        StubInstanceManager() {
            factories = new HashMap<>();
            factories.put("one", new MenuItemOneFactory());
            factories.put("two", new MenuItemTwoFactory());
            factories.put("three", new MenuItemThreeFactory());
        }

        @Override
        public <T> InstanceFactory getInstanceFactory(
            Class<T> instanceType, String classifier, Class<InstanceFactory<T>> factoryType
        ) {
            throw new UnsupportedOperationException();
        }
        @Override public <T> InstanceFactory<T> getFilteredInstanceFactory(
            Class<T> type, String classifier, FactoryFilter factoryFilter
        ) {
            //noinspection unchecked
            return (InstanceFactory<T>) factories.get(classifier);
        }
        @Override public <T> List<InstanceFactory<T>> getManyInstanceFactories(
            Class<T> type, String classifier, FactoryFilter factoryFilter
        ) {
            throw new UnsupportedOperationException();
        }
    }

    private interface MenuItem {}
    private static class MenuItemOne implements MenuItem {}
    private static class MenuItemTwo implements MenuItem {}
    private static class MenuItemThree implements MenuItem {}

    private static class Dependency1 {}
    private static class Dependency2 {}
    private static class Dependency3 {}
}
