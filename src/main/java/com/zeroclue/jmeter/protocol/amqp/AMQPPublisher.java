package com.zeroclue.jmeter.protocol.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JMeter creates an instance of a sampler class for every occurrence of the
 * element in every thread. [some additional copies may be created before the
 * test run starts]
 *
 * Thus each sampler is guaranteed to be called by a single thread - there is no
 * need to synchronize access to instance variables.
 *
 * However, access to class fields must be synchronized.
 */
public class AMQPPublisher extends AMQPSampler {

    private static final long serialVersionUID = -8420658040465788497L;

    private static final Logger log = LoggerFactory.getLogger(AMQPPublisher.class);

    // ++ These are JMX names, and must not be changed
    private final static String MESSAGE = "AMQPPublisher.Message";
    private final static String MESSAGE_ROUTING_KEY = "AMQPPublisher.MessageRoutingKey";
    private final static String MESSAGE_TYPE = "AMQPPublisher.MessageType";
    private final static String REPLY_TO_QUEUE = "AMQPPublisher.ReplyToQueue";
    private final static String CONTENT_TYPE = "AMQPPublisher.ContentType";
    private final static String CORRELATION_ID = "AMQPPublisher.CorrelationId";
    private final static String MESSAGE_ID = "AMQPPublisher.MessageId";
    private final static String HEADERS = "AMQPPublisher.Headers";

    public static boolean DEFAULT_PERSISTENT = false;
    private final static String PERSISTENT = "AMQPPublisher.Persistent";

    public static boolean DEFAULT_USE_TX = false;
    private final static String USE_TX = "AMQPPublisher.UseTx";

    @Override
    protected void init(Connection connection, Channel channel) {
        if (isUseTx()) {
            try {
                channel.txSelect();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    @Override
    public SampleResult doSample(Entry e, Channel channel) {

        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setSuccessful(false);
        result.setResponseCode("500");

        result.setSampleLabel(getTitle());

        result.sampleStart();
        try {
            AMQP.BasicProperties messageProperties = getProperties();
            byte[] messageBytes = getMessageBytes();

            channel.basicPublish(getExchange(), getMessageRoutingKey(), messageProperties, messageBytes);

            if (isUseTx()) {
                channel.txCommit();
            }

            result.setSentBytes(messageBytes.length);
            result.setSamplerData(getMessage());

            result.setRequestHeaders(messageProperties.getHeaders().toString());

            result.setResponseCodeOK();
            result.setResponseMessage("OK");
            result.setSuccessful(true);
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
            result.setResponseCode("000");
            result.setResponseMessage(ex.toString());
            result.setResponseData(stackTrace(ex));
        } finally {
            result.sampleEnd();
        }

        return result;
    }

    private byte[] getMessageBytes() {
        return getMessage().getBytes();
    }

    /**
     * @return the message routing key for the sample
     */
    public String getMessageRoutingKey() {
        return getPropertyAsString(MESSAGE_ROUTING_KEY);
    }

    public void setMessageRoutingKey(String content) {
        setProperty(MESSAGE_ROUTING_KEY, content);
    }

    /**
     * @return the message for the sample
     */
    public String getMessage() {
        return getPropertyAsString(MESSAGE);
    }

    public void setMessage(String content) {
        setProperty(MESSAGE, content);
    }

    /**
     * @return the message type for the sample
     */
    public String getMessageType() {
        return getPropertyAsString(MESSAGE_TYPE);
    }

    public void setMessageType(String content) {
        setProperty(MESSAGE_TYPE, content);
    }

    /**
     * @return the reply-to queue for the sample
     */
    public String getReplyToQueue() {
        return getPropertyAsString(REPLY_TO_QUEUE);
    }

    public void setReplyToQueue(String content) {
        setProperty(REPLY_TO_QUEUE, content);
    }

    public String getContentType() {
        return getPropertyAsString(CONTENT_TYPE);
    }

    public void setContentType(String contentType) {
        setProperty(CONTENT_TYPE, contentType);
    }

    /**
     * @return the correlation identifier for the sample
     */
    public String getCorrelationId() {
        return getPropertyAsString(CORRELATION_ID);
    }

    public void setCorrelationId(String content) {
        setProperty(CORRELATION_ID, content);
    }

    /**
     * @return the message id for the sample
     */
    public String getMessageId() {
        return getPropertyAsString(MESSAGE_ID);
    }

    public void setMessageId(String content) {
        setProperty(MESSAGE_ID, content);
    }

    public Arguments getHeaders() {
        return (Arguments) getProperty(HEADERS).getObjectValue();
    }

    public void setHeaders(Arguments headers) {
        setProperty(new TestElementProperty(HEADERS, headers));
    }

    public Boolean isPersistent() {
        return getPropertyAsBoolean(PERSISTENT, DEFAULT_PERSISTENT);
    }

    public void setPersistent(Boolean persistent) {
        setProperty(PERSISTENT, persistent);
    }

    public Boolean isUseTx() {
        return getPropertyAsBoolean(USE_TX, DEFAULT_USE_TX);
    }

    public void setUseTx(Boolean tx) {
        setProperty(USE_TX, tx);
    }

    protected AMQP.BasicProperties getProperties() {
        final AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

        final int deliveryMode = isPersistent() ? 2 : 1;
        final String contentType = StringUtils.defaultIfEmpty(getContentType(), "text/plain");

        builder.contentType(contentType)
            .deliveryMode(deliveryMode)
            .priority(0)
            .correlationId(getCorrelationId())
            .replyTo(getReplyToQueue())
            .type(getMessageType())
            .headers(prepareHeaders())
            .build();
        if (getMessageId() != null && !getMessageId().isEmpty()) {
            builder.messageId(getMessageId());
        }
        return builder.build();
    }

    private Map<String, Object> prepareHeaders() {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> source = getHeaders().getArgumentsAsMap();
        for (Map.Entry<String, String> item : source.entrySet()) {
            result.put(item.getKey(), item.getValue());
        }
        return result;
    }
}
