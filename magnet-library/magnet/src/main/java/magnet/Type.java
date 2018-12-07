package magnet;

public @interface Type {

    Class<?> type() default void.class;

    String classifier() default Classifier.NONE;

}
