package com.itzap.rxjava.command;

public abstract class RunnableCommand<T> extends AbstractCommand<T, RunnableCommand<T>> {

    public RunnableCommand(String name) {
        super(name);
    }

    @Override
    protected RunnableCommand<T> getThis() {
        return this;
    }
}
