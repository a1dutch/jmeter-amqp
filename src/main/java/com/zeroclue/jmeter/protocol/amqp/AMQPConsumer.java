package com.zeroclue.jmeter.protocol.amqp;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPConsumer extends AMQPSampler {

    public static final int DEFAULT_PREFETCH_COUNT = 0;
    public static final boolean DEFAULT_READ_RESPONSE = true;
    public static final boolean DEFAULT_USE_TX = false;
    public static final String DEFAULT_PREFETCH_COUNT_STRING = Integer.toString(DEFAULT_PREFETCH_COUNT);

    private static final long serialVersionUID = 7480863561320459091L;

    private static final Logger log = LoggerFactory.getLogger(AMQPConsumer.class);

    // ++ These are JMX names, and must not be changed
    private static final String PREFETCH_COUNT = "AMQPConsumer.PrefetchCount";
    private static final String PURGE_QUEUE = "AMQPConsumer.PurgeQueue";
    private static final String AUTO_ACK = "AMQPConsumer.AutoAck";
    private static final String RECEIVE_TIMEOUT = "AMQPConsumer.ReceiveTimeout";
    private static final String USE_TX = "AMQPConsumer.UseTx";

    private static final String TIMESTAMP_PARAMETER = "Timestamp";
    private static final String EXCHANGE_PARAMETER = "Exchange";
    private static final String ROUTING_KEY_PARAMETER = "Routing Key";
    private static final String DELIVERY_TAG_PARAMETER = "Delivery Tag";

    private QueueingConsumer consumer;

    public AMQPConsumer() {
        super();
    }

    @Override
    protected void init(Connection connection, Channel channel) {
        try {
            channel.basicQos(getPrefetchCountAsInt());
            if (isUseTx()) {
                channel.txSelect();
            }
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }

        this.consumer = new QueueingConsumer(channel);

        try {
            String consumerTag = channel.basicConsume(getQueue(), isAutoAck(), consumer);
            log.info("Started consumer with tag: {}", consumerTag);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public SampleResult doSample(Entry entry, Channel channel) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setSuccessful(false);
        result.setResponseCode("500");

        result.sampleStart();
        try {
            Delivery delivery = consumer.nextDelivery(getReceiveTimeoutAsInt());

            if (delivery == null) {
                result.setIgnore();
                return result;
            }

            result.setResponseData(delivery.getBody());

            if (!isAutoAck()) {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }

            if (isUseTx()) {
                channel.txCommit();
            }

            result.setDataType(SampleResult.TEXT);
            result.setResponseHeaders(formatHeaders(delivery));
            result.setResponseCodeOK();
            result.setSuccessful(true);
        } catch (ShutdownSignalException e) {
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("400");
            result.setResponseMessage(e.getMessage());
            result.setResponseData(stackTrace(e));
        } catch (ConsumerCancelledException e) {
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("300");
            result.setResponseMessage(e.getMessage());
            result.setResponseData(stackTrace(e));
        } catch (InterruptedException e) {
            log.info("interupted while attempting to consume");
            result.setResponseCode("200");
            result.setResponseMessage(e.getMessage());
            result.setResponseData(stackTrace(e));
        } catch (IOException e) {
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("100");
            result.setResponseMessage(e.getMessage());
            result.setResponseData(stackTrace(e));
        } finally {
            result.sampleEnd(); // End timing
        }

        return result;
    }

    /**
     * @return the whether or not to purge the queue
     */
    public String getPurgeQueue() {
        return getPropertyAsString(PURGE_QUEUE);
    }

    public void setPurgeQueue(Boolean purgeQueue) {
        setProperty(PURGE_QUEUE, purgeQueue.toString());
    }

    public boolean isPurgeQueue() {
        return Boolean.parseBoolean(getPurgeQueue());
    }

    public void setAutoAck(Boolean autoAck) {
        setProperty(AUTO_ACK, autoAck.toString());
    }

    public boolean isAutoAck() {
        return getPropertyAsBoolean(AUTO_ACK);
    }

    protected int getReceiveTimeoutAsInt() {
        if (getPropertyAsInt(RECEIVE_TIMEOUT) < 1) {
            return DEFAULT_TIMEOUT;
        }
        return getPropertyAsInt(RECEIVE_TIMEOUT);
    }

    public String getReceiveTimeout() {
        return getPropertyAsString(RECEIVE_TIMEOUT, DEFAULT_TIMEOUT_STRING);
    }

    public void setReceiveTimeout(String s) {
        setProperty(RECEIVE_TIMEOUT, s);
    }

    public String getPrefetchCount() {
        return getPropertyAsString(PREFETCH_COUNT, DEFAULT_PREFETCH_COUNT_STRING);
    }

    public void setPrefetchCount(String prefetchCount) {
        setProperty(PREFETCH_COUNT, prefetchCount);
    }

    public int getPrefetchCountAsInt() {
        return getPropertyAsInt(PREFETCH_COUNT);
    }

    public Boolean isUseTx() {
        return getPropertyAsBoolean(USE_TX, DEFAULT_USE_TX);
    }

    public void setUseTx(Boolean tx) {
        setProperty(USE_TX, tx);
    }

    private String formatHeaders(QueueingConsumer.Delivery delivery) {
        BasicProperties properties = delivery.getProperties();
        Map<String, Object> headers = properties.getHeaders();
        Envelope envelope = delivery.getEnvelope();
        Date timestamp = properties.getTimestamp();

        StringBuilder sb = new StringBuilder();
        sb.append(TIMESTAMP_PARAMETER).append(": ").append(timestamp != null ? timestamp.getTime() : "").append("\n");
        sb.append(EXCHANGE_PARAMETER).append(": ").append(envelope.getExchange()).append("\n");
        sb.append(ROUTING_KEY_PARAMETER).append(": ").append(envelope.getRoutingKey()).append("\n");
        sb.append(DELIVERY_TAG_PARAMETER).append(": ").append(envelope.getDeliveryTag()).append("\n");
        for (String key : headers.keySet()) {
            sb.append(key).append(": ").append(headers.get(key)).append("\n");
        }
        return sb.toString();
    }

}
