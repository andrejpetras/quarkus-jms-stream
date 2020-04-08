package org.lorislab.quarkus.reactive.jms;

import io.smallrye.reactive.messaging.jms.JmsProperties;
import org.eclipse.microprofile.reactive.messaging.Metadata;

public class OutputJmsMessageMetadata {

    private final String correlationId;
    private final String replyTo;
    private final String destination;
    private final int deliveryMode;
    private final String type;
    private final JmsProperties properties;

    public OutputJmsMessageMetadata(String correlationId, String replyTo, String destination,
                                    int deliveryMode, String type, JmsProperties properties) {
        this.correlationId = correlationId;
        this.replyTo = replyTo;
        this.destination = destination;
        this.deliveryMode = deliveryMode;
        this.type = type;
        this.properties = properties;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public String getDestination() {
        return destination;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public String getType() {
        return type;
    }

    public JmsProperties getProperties() {
        return properties;
    }

    public Metadata of() {
        return Metadata.of(this);
    }

    public static OutputJmsMessageMetadataBuilder builder() {
        return new OutputJmsMessageMetadataBuilder();
    }

    public static final class OutputJmsMessageMetadataBuilder {

        public static final String TOPIC = "topic";

        public static final String QUEUE = "queue";

        private String correlationId;
        private String replyTo;
        private String destination;
        private int deliveryMode = -1;
        private String type;
        private JmsProperties properties;

        public OutputJmsMessageMetadataBuilder withCorrelationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public OutputJmsMessageMetadataBuilder withReplyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public OutputJmsMessageMetadataBuilder withDestination(String destination) {
            this.destination = destination;
            return this;
        }

        public OutputJmsMessageMetadataBuilder withDeliveryMode(int deliveryMode) {
            this.deliveryMode = deliveryMode;
            return this;
        }

        public OutputJmsMessageMetadataBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public OutputJmsMessageMetadataBuilder withTypeQueue() {
            this.type = QUEUE;
            return this;
        }

        public OutputJmsMessageMetadataBuilder withTypeTopic() {
            this.type = TOPIC;
            return this;
        }

        public OutputJmsMessageMetadataBuilder withProperties(JmsProperties properties) {
            this.properties = properties;
            return this;
        }

        public OutputJmsMessageMetadata build() {
            return new OutputJmsMessageMetadata(correlationId, replyTo, destination, deliveryMode, type, properties);
        }

    }
}
