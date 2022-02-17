package uk.co.a1dutch.jmeter.amqp;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.ThreadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AMQPSampler extends AbstractSampler implements ThreadListener {

    public static final boolean DEFAULT_EXCHANGE_DURABLE = true;
    public static final boolean DEFAULT_EXCHANGE_AUTO_DELETE = true;
    public static final boolean DEFAULT_EXCHANGE_REDECLARE = false;
    public static final boolean DEFAULT_QUEUE_REDECLARE = false;

    public static final int DEFAULT_PORT = 5672;
    public static final String DEFAULT_PORT_STRING = Integer.toString(DEFAULT_PORT);

    public static final int DEFAULT_TIMEOUT = 1000;
    public static final String DEFAULT_TIMEOUT_STRING = Integer.toString(DEFAULT_TIMEOUT);

    public static final int DEFAULT_ITERATIONS = 1;
    public static final String DEFAULT_ITERATIONS_STRING = Integer.toString(DEFAULT_ITERATIONS);

    // ++ These are JMX names, and must not be changed
    protected static final String EXCHANGE = "AMQPSampler.Exchange";
    protected static final String EXCHANGE_TYPE = "AMQPSampler.ExchangeType";
    protected static final String EXCHANGE_DURABLE = "AMQPSampler.ExchangeDurable";
    protected static final String EXCHANGE_AUTO_DELETE = "AMQPSampler.ExchangeAutoDelete";
    protected static final String EXCHANGE_REDECLARE = "AMQPSampler.ExchangeRedeclare";
    protected static final String QUEUE = "AMQPSampler.Queue";
    protected static final String ROUTING_KEY = "AMQPSampler.RoutingKey";
    protected static final String VIRUTAL_HOST = "AMQPSampler.VirtualHost";
    protected static final String HOST = "AMQPSampler.Host";
    protected static final String PORT = "AMQPSampler.Port";
    protected static final String USERNAME = "AMQPSampler.Username";
    protected static final String PASSWORD = "AMQPSampler.Password";

    protected static final String SSL = "AMQPSampler.SSL";
    protected static final String SSL_ALORITHM = "AMQPSampler.SSL.Algorithm";
    protected static final String SSL_KEYSTORE = "AMQPSampler.SSL.KeyStore";
    protected static final String SSL_KEYSTORE_TYPE = "AMQPSampler.SSL.KeyStoreType";
    protected static final String SSL_KEYSTORE_PASSWORD = "AMQPSampler.SSL.KeyStorePassword";
    protected static final String SSL_TRUST_STORE = "AMQPSampler.SSL.TrustStore";
    protected static final String SSL_TRUST_STORE_TYPE = "AMQPSampler.SSL.TrustStoreType";
    protected static final String SSL_TRUST_STORE_PASSWORD = "AMQPSampler.SSL.TrustStorePassword";

    private static final String TIMEOUT = "AMQPSampler.Timeout";
    private static final String MESSAGE_TTL = "AMQPSampler.MessageTTL";
    private static final String MESSAGE_EXPIRES = "AMQPSampler.MessageExpires";
    private static final String QUEUE_DURABLE = "AMQPSampler.QueueDurable";
    private static final String QUEUE_REDECLARE = "AMQPSampler.Redeclare";
    private static final String QUEUE_EXCLUSIVE = "AMQPSampler.QueueExclusive";
    private static final String QUEUE_AUTO_DELETE = "AMQPSampler.QueueAutoDelete";
    private static final int DEFAULT_HEARTBEAT = 1;

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    protected AMQPSampler() {
        factory = new ConnectionFactory();
        factory.setRequestedHeartbeat(DEFAULT_HEARTBEAT);
    }

//        if (channel != null && !channel.isOpen()) {
//            log.warn("channel " + channel.getChannelNumber() + " closed unexpectedly: ", channel.getCloseReason());
//            channel = null; // so we re-open it below
//        }
//
//        if (channel == null) {
//            return createChannel();

//            // TODO: Break out queue binding
//            boolean queueConfigured = StringUtils.isNotBlank(getQueue());
//
//            if (queueConfigured) {
//                if (getQueueRedeclare()) {
//                    deleteQueue();
//                }
//
//                AMQP.Queue.DeclareOk declareQueueResp = channel.queueDeclare(getQueue(), isQueueDurable(),
//                    isQueueExclusive(), isQueueAutoDelete(), getQueueArguments());
//            }
//
//            if (StringUtils.isNotBlank(getExchange())) { // Use a named exchange
//                if (getExchangeRedeclare()) {
//                    deleteExchange();
//                }
//
//                AMQP.Exchange.DeclareOk declareExchangeResp = channel.exchangeDeclare(getExchange(), getExchangeType(),
//                    isExchangeDurable(), isExchangeAutoDelete(), Collections.<String, Object>emptyMap());
//
//                if (queueConfigured) {
//                    channel.queueBind(getQueue(), getExchange(), getRoutingKey());
//                }
//            }
//
//            log.info("bound to:" + "\n\t queue: " + getQueue() + "\n\t exchange: " + getExchange() +
//                "\n\t exchange(D)? " + isExchangeDurable() + "\n\t routing key: " + getRoutingKey() +
//                "\n\t arguments: " + getQueueArguments());

//    }

    /**
     * @return a string for the sampleResult Title
     */
    protected String getTitle() {
        return this.getName();
    }

    protected int getTimeoutAsInt() {
        if (getPropertyAsInt(TIMEOUT) < 1) {
            return DEFAULT_TIMEOUT;
        }
        return getPropertyAsInt(TIMEOUT);
    }

    public String getTimeout() {
        return getPropertyAsString(TIMEOUT, DEFAULT_TIMEOUT_STRING);
    }

    public void setTimeout(String s) {
        setProperty(TIMEOUT, s);
    }

    public String getExchange() {
        return getPropertyAsString(EXCHANGE);
    }

    public void setExchange(String name) {
        setProperty(EXCHANGE, name);
    }

    public boolean isExchangeDurable() {
        return getPropertyAsBoolean(EXCHANGE_DURABLE);
    }

    public void setExchangeDurable(boolean durable) {
        setProperty(EXCHANGE_DURABLE, durable);
    }

    public boolean isExchangeAutoDelete() {
        return getPropertyAsBoolean(EXCHANGE_AUTO_DELETE);
    }

    public void setExchangeAutoDelete(boolean autoDelete) {
        setProperty(EXCHANGE_AUTO_DELETE, autoDelete);
    }

    public String getExchangeType() {
        return getPropertyAsString(EXCHANGE_TYPE);
    }

    public void setExchangeType(String name) {
        setProperty(EXCHANGE_TYPE, name);
    }

    public Boolean getExchangeRedeclare() {
        return getPropertyAsBoolean(EXCHANGE_REDECLARE);
    }

    public void setExchangeRedeclare(Boolean content) {
        setProperty(EXCHANGE_REDECLARE, content);
    }

    public String getQueue() {
        return getPropertyAsString(QUEUE);
    }

    public void setQueue(String name) {
        setProperty(QUEUE, name);
    }

    public String getRoutingKey() {
        return getPropertyAsString(ROUTING_KEY);
    }

    public void setRoutingKey(String name) {
        setProperty(ROUTING_KEY, name);
    }

    public String getVirtualHost() {
        return getPropertyAsString(VIRUTAL_HOST);
    }

    public void setVirtualHost(String name) {
        setProperty(VIRUTAL_HOST, name);
    }

    public String getMessageTTL() {
        return getPropertyAsString(MESSAGE_TTL);
    }

    public void setMessageTTL(String name) {
        setProperty(MESSAGE_TTL, name);
    }

    protected Integer getMessageTTLAsInt() {
        if (getPropertyAsInt(MESSAGE_TTL) < 1) {
            return null;
        }
        return getPropertyAsInt(MESSAGE_TTL);
    }

    public String getMessageExpires() {
        return getPropertyAsString(MESSAGE_EXPIRES);
    }

    public void setMessageExpires(String name) {
        setProperty(MESSAGE_EXPIRES, name);
    }

    protected Integer getMessageExpiresAsInt() {
        if (getPropertyAsInt(MESSAGE_EXPIRES) < 1) {
            return null;
        }
        return getPropertyAsInt(MESSAGE_EXPIRES);
    }

    public String getHost() {
        return getPropertyAsString(HOST);
    }

    public void setHost(String name) {
        setProperty(HOST, name);
    }

    public String getPort() {
        return getPropertyAsString(PORT);
    }

    public void setPort(String name) {
        setProperty(PORT, name);
    }

    protected int getPortAsInt() {
        if (getPropertyAsInt(PORT) < 1) {
            return DEFAULT_PORT;
        }
        return getPropertyAsInt(PORT);
    }

    public void setConnectionSSL(String content) {
        setProperty(SSL, content);
    }

    public void setConnectionSSL(Boolean value) {
        setProperty(SSL, value.toString());
    }

    public boolean isConnectionSSL() {
        return getPropertyAsBoolean(SSL);
    }

    public String getSslAlgorithm() {
        return getPropertyAsString(SSL_ALORITHM);
    }

    public void setSslAlgorithm(String algorithm) {
        setProperty(SSL_ALORITHM, algorithm);
    }

    public String getSslKeyStore() {
        return getPropertyAsString(SSL_KEYSTORE);
    }

    public void setSslKeyStore(String keystore) {
        setProperty(SSL_KEYSTORE, keystore);
    }

    public String getSslKeyStoreType() {
        return getPropertyAsString(SSL_KEYSTORE_TYPE);
    }

    public void setSslKeyStoreType(String keystore) {
        setProperty(SSL_KEYSTORE_TYPE, keystore);
    }

    public String getSslKeyStorePassword() {
        return getPropertyAsString(SSL_KEYSTORE_PASSWORD);
    }

    public void setSslKeyStorePassword(String keystore) {
        setProperty(SSL_KEYSTORE_PASSWORD, keystore);
    }

    public String getSslTrustStore() {
        return getPropertyAsString(SSL_TRUST_STORE);
    }

    public void setSslTrustStore(String keystore) {
        setProperty(SSL_TRUST_STORE, keystore);
    }

    public String getSslTrustStoreType() {
        return getPropertyAsString(SSL_TRUST_STORE_TYPE);
    }

    public void setSslTrustStoreType(String keystore) {
        setProperty(SSL_TRUST_STORE_TYPE, keystore);
    }

    public String getSslTrustStorePassword() {
        return getPropertyAsString(SSL_TRUST_STORE_PASSWORD);
    }

    public void setSslTrustStorePassword(String keystore) {
        setProperty(SSL_TRUST_STORE_PASSWORD, keystore);
    }

    public String getUsername() {
        return getPropertyAsString(USERNAME);
    }

    public void setUsername(String name) {
        setProperty(USERNAME, name);
    }

    public String getPassword() {
        return getPropertyAsString(PASSWORD);
    }

    public void setPassword(String name) {
        setProperty(PASSWORD, name);
    }

    /**
     * @return the whether or not the queue is durable
     */
    public String getQueueDurable() {
        return getPropertyAsString(QUEUE_DURABLE);
    }

    public void setQueueDurable(String content) {
        setProperty(QUEUE_DURABLE, content);
    }

    public void setQueueDurable(Boolean value) {
        setProperty(QUEUE_DURABLE, value.toString());
    }

    public boolean isQueueDurable() {
        return getPropertyAsBoolean(QUEUE_DURABLE);
    }

    /**
     * @return the whether or not the queue is exclusive
     */
    public String getQueueExclusive() {
        return getPropertyAsString(QUEUE_EXCLUSIVE);
    }

    public void setQueueExclusive(String content) {
        setProperty(QUEUE_EXCLUSIVE, content);
    }

    public void setQueueExclusive(Boolean value) {
        setProperty(QUEUE_EXCLUSIVE, value.toString());
    }

    public boolean isQueueExclusive() {
        return getPropertyAsBoolean(QUEUE_EXCLUSIVE);
    }

    /**
     * @return the whether or not the queue should auto delete
     */
    public String getQueueAutoDelete() {
        return getPropertyAsString(QUEUE_AUTO_DELETE);
    }

    public void setQueueAutoDelete(String content) {
        setProperty(QUEUE_AUTO_DELETE, content);
    }

    public void setQueueAutoDelete(Boolean value) {
        setProperty(QUEUE_AUTO_DELETE, value.toString());
    }

    public boolean isQueueAutoDelete() {
        return getPropertyAsBoolean(QUEUE_AUTO_DELETE);
    }

    public Boolean getQueueRedeclare() {
        return getPropertyAsBoolean(QUEUE_REDECLARE);
    }

    public void setQueueRedeclare(Boolean content) {
        setProperty(QUEUE_REDECLARE, content);
    }

    @Override
    public final SampleResult sample(Entry e) {
        try {
            init();
            return doSample(e, channel);
        } catch (Exception e1) {
            SampleResult result = new SampleResult();
            result.setSuccessful(false);
            result.setResponseData(stackTrace(e1));
            return result;
        }
    }

    protected abstract SampleResult doSample(Entry e, Channel channel);

    @Override
    public void threadStarted() {

    }

    @Override
    public void threadFinished() {
        try {
            if (channel.isOpen()) {
                channel.close();
            }

            if (connection.isOpen()) {
                connection.close();
            }
        } catch (IOException | TimeoutException e) {
            LoggerFactory.getLogger(getClass()).warn("failed to cleanup", e);
        }
    }

    byte[] stackTrace(Exception ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ex.printStackTrace(new PrintStream(baos));
        return baos.toByteArray();
    }

    private void init() {
        if (connection != null) {
            return;
        }

        Logger log = LoggerFactory.getLogger(getClass());
        log.info("Creating connection " + getVirtualHost() + ":" + getPortAsInt());

        factory.setConnectionTimeout(getTimeoutAsInt());
        factory.setVirtualHost(getVirtualHost());
        factory.setUsername(getUsername());
        factory.setPassword(getPassword());

        if (isConnectionSSL()) {
            factory.useSslProtocol(createSslContext());
        }

        log.debug("RabbitMQ ConnectionFactory using:" + "\n\t virtual host: " + getVirtualHost() + "\n\t host: " +
            getHost() + "\n\t port: " + getPort() + "\n\t username: " + getUsername() + "\n\t password: " +
            getPassword() + "\n\t timeout: " + getTimeout() + "\n\t heartbeat: " + factory.getRequestedHeartbeat() +
            "\nin " + this);

        String[] hosts = getHost().split(",");
        Address[] addresses = new Address[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            addresses[i] = new Address(hosts[i], getPortAsInt());
        }

        try {
            this.connection = factory.newConnection(addresses);
            this.channel = connection.createChannel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        init(connection, channel);
    }

    protected void init(Connection connection, Channel channel) {
    }

    private SSLContext createSslContext() {
        try {
            KeyStore keystore = KeyStore.getInstance(getSslKeyStoreType());
            keystore.load(new FileInputStream(getSslKeyStore()), getSslTrustStorePassword().toCharArray());

            KeyStore trustStore = KeyStore.getInstance(getSslTrustStoreType());
            trustStore.load(new FileInputStream(getSslTrustStore()), getSslTrustStorePassword().toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keystore, getSslKeyStorePassword().toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            SSLContext context = SSLContext.getInstance(getSslAlgorithm());
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
            return context;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("could not create ssl context", e);
            throw new RuntimeException("could not create ssl context: " + e.getMessage(), e);
        }
    }

//    private void deleteQueue() throws IOException, TimeoutException {
//        // use a different channel since channel closes on exception.
//        Channel channel = newConnection();
//        try {
//            log.info("Deleting queue " + getQueue());
//            channel.queueDelete(getQueue());
//        } catch (Exception ex) {
//            log.debug("failed to delete queue", ex);
//            // ignore it.
//        } finally {
//            if (channel.isOpen()) {
//                channel.close();
//            }
//        }
//    }
//
//    private void deleteExchange() throws IOException, TimeoutException {
//        // use a different channel since channel closes on exception.
//        Channel channel = newConnection();
//        try {
//            log.info("Deleting exchange " + getExchange());
//            channel.exchangeDelete(getExchange());
//        } catch (Exception ex) {
//            log.debug("failed to delete exchange", ex);
//            // ignore it.
//        } finally {
//            if (channel.isOpen()) {
//                channel.close();
//            }
//        }
//    }
}
