package com.example.androidtvlibrary.main.adapter.wow;

import android.os.Handler;

import com.example.androidtvlibrary.main.adapter.Assertions;

import java.util.concurrent.CopyOnWriteArrayList;

public final class EventDispatcher<T> {

    /** Functional interface to send an event. */
    public interface Event<T> {

        /**
         * Sends the event to a listener.
         *
         * @param listener The listener to send the event to.
         */
        void sendTo(T listener);
    }

    /** The list of listeners and handlers. */
    private final CopyOnWriteArrayList<HandlerAndListener<T>> listeners;

    /** Creates an event dispatcher. */
    public EventDispatcher() {
        listeners = new CopyOnWriteArrayList<>();
    }

    /** Adds a listener to the event dispatcher. */
    public void addListener(Handler handler, T eventListener) {
        Assertions.checkArgument(handler != null && eventListener != null);
        removeListener(eventListener);
        listeners.add(new HandlerAndListener<>(handler, eventListener));
    }

    /** Removes a listener from the event dispatcher. */
    public void removeListener(T eventListener) {
        for (HandlerAndListener<T> handlerAndListener : listeners) {
            if (handlerAndListener.listener == eventListener) {
                handlerAndListener.release();
                listeners.remove(handlerAndListener);
            }
        }
    }

    /**
     * Dispatches an event to all registered listeners.
     *
     * @param event The {@link Event}.
     */
    public void dispatch(Event<T> event) {
        for (HandlerAndListener<T> handlerAndListener : listeners) {
            handlerAndListener.dispatch(event);
        }
    }

    private static final class HandlerAndListener<T> {

        private final Handler handler;
        private final T listener;

        private boolean released;

        public HandlerAndListener(Handler handler, T eventListener) {
            this.handler = handler;
            this.listener = eventListener;
        }

        public void release() {
            released = true;
        }

        public void dispatch(Event<T> event) {
            handler.post(
                    () -> {
                        if (!released) {
                            event.sendTo(listener);
                        }
                    });
        }
    }
}
