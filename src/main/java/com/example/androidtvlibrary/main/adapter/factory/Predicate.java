package com.example.androidtvlibrary.main.adapter.factory;

public interface Predicate<T> {

    /**
     * Evaluates an input.
     *
     * @param input The input to evaluate.
     * @return The evaluated result.
     */
    boolean evaluate(T input);

}
