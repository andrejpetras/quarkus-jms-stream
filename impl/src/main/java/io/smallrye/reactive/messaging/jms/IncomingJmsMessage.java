package io.smallrye.reactive.messaging.jms;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.reactive.messaging.Metadata;

public class IncomingJmsMessage<T> implements org.eclipse.microprofile.reactive.messaging.Message<T> {
    private final Message delegate;
    private final Executor executor;
    private final Class<T> clazz;
    private final ObjectMapper json;
    private final IncomingJmsMessageMetadata jmsMetadata;
    private final Metadata metadata;
    private final boolean transaction;

    IncomingJmsMessage(boolean transaction, Message message, Executor executor, ObjectMapper json) {
        this.delegate = message;
        this.json = json;
        this.executor = executor;
        this.transaction = transaction;
        String cn = null;
        try {
            cn = message.getStringProperty("_classname");
            if (cn == null) {
                cn = message.getJMSType();
            }
        } catch (JMSException e) {
            // ignore it
        }
        try {
            this.clazz = cn != null ? load(cn) : null;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to load the class " + e);
        }

        this.jmsMetadata = new IncomingJmsMessageMetadata(message);
        this.metadata = Metadata.of(this.jmsMetadata);
    }

    @SuppressWarnings("unchecked")
    private Class<T> load(String cn) throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                return (Class<T>) loader.loadClass(cn);
            } catch (ClassNotFoundException e) {
                // Will try with the current class classloader
            }
        }
        return (Class<T>) IncomingJmsMessage.class.getClassLoader().loadClass(cn);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getPayload() {
        try {
            if (clazz != null) {
                return convert(delegate.getBody(String.class));
            } else {
                return (T) delegate.getBody(Object.class);
            }
        } catch (JMSException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private T convert(String value) {
        if (clazz.equals(Integer.class)) {
            return (T) Integer.valueOf(value);
        }
        if (clazz.equals(Long.class)) {
            return (T) Long.valueOf(value);
        }
        if (clazz.equals(Double.class)) {
            return (T) Double.valueOf(value);
        }
        if (clazz.equals(Float.class)) {
            return (T) Float.valueOf(value);
        }
        if (clazz.equals(Boolean.class)) {
            return (T) Boolean.valueOf(value);
        }
        if (clazz.equals(Short.class)) {
            return (T) Short.valueOf(value);
        }
        if (clazz.equals(Byte.class)) {
            return (T) Byte.valueOf(value);
        }
        if (clazz.equals(String.class)) {
            return (T) value;
        }

        try {
            return json.readerFor(clazz).readValue(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    @Override
    public CompletionStage<Void> ack() {
        return CompletableFuture.runAsync(() -> {
            if (!transaction) {
                try {
                    delegate.acknowledge();
                } catch (JMSException e) {
                    throw new IllegalArgumentException();
                }
            }
        }, executor);
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <C> C unwrap(Class<C> unwrapType) {
        if (Message.class.equals(unwrapType)) {
            return (C) delegate;
        }
        if (IncomingJmsMessageMetadata.class.equals(unwrapType)) {
            return (C) jmsMetadata;
        }
        throw new IllegalArgumentException("Unable to unwrap message to " + unwrapType);
    }

}
