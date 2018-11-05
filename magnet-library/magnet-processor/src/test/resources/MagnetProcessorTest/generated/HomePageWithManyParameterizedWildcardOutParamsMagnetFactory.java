package app.extension;

import app.Page;
import app.WorkProcessor;
import java.util.List;
import magnet.ScopeContainer;
import magnet.internal.InstanceFactory;

public final class HomePageWithManyParameterizedWildcardOutParamsMagnetFactory extends InstanceFactory<Page> {
    @Override
    @SuppressWarnings("unchecked")
    public Page create(ScopeContainer scope) {
        List variant1 = scope.getMany(WorkProcessor.class);
        List variant2 = scope.getMany(WorkProcessor.class, "global");
        List variant3 = scope.getMany(WorkProcessor.class);
        List variant4 = scope.getMany(WorkProcessor.class, "global");
        return new HomePageWithManyParameterizedWildcardOutParams(variant1, variant2, variant3, variant4);
    }

    public static Class getType() {
        return Page.class;
    }
}
