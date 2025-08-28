package io.github.remmerw.thor.cobra.js;

public interface JavaInstantiator {
    Object newInstance(Object[] args) throws InstantiationException, IllegalAccessException;
}
