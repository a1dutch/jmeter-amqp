package com.zeroclue.jmeter.protocol.amqp.gui;

import com.zeroclue.jmeter.protocol.amqp.AMQPSampler;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AMQPSamplerGui extends AbstractSamplerGui {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(AMQPSamplerGui.class);

    private JLabeledTextField exchange = new JLabeledTextField("Exchange");
    private JCheckBox exchangeRedeclare = new JCheckBox("Redeclare", AMQPSampler.DEFAULT_EXCHANGE_REDECLARE);
    private JLabeledTextField queue = new JLabeledTextField("Queue");
    private JLabeledTextField routingKey = new JLabeledTextField("Routing Key");
    private JLabeledTextField virtualHost = new JLabeledTextField("Virtual Host");
    private JLabeledTextField messageTTL = new JLabeledTextField("Message TTL");
    private JLabeledTextField messageExpires = new JLabeledTextField("Expires");
    private JLabeledChoice exchangeType = new JLabeledChoice("Exchange Type",
        new String[] { "direct", "topic", "headers", "fanout" });
    private JCheckBox exchangeDurable = new JCheckBox("Durable", AMQPSampler.DEFAULT_EXCHANGE_DURABLE);
    private JCheckBox exchangeAutoDelete = new JCheckBox("Auto Delete",
        AMQPSampler.DEFAULT_EXCHANGE_AUTO_DELETE);
    private JCheckBox queueDurable = new JCheckBox("Durable", true);
    private JCheckBox queueRedeclare = new JCheckBox("Redeclare", AMQPSampler.DEFAULT_QUEUE_REDECLARE);
    private JCheckBox queueExclusive = new JCheckBox("Exclusive", true);
    private JCheckBox queueAutoDelete = new JCheckBox("Auto Delete", true);

    private JLabeledTextField host = new JLabeledTextField("Host");
    private JLabeledTextField port = new JLabeledTextField("Port");
    private JLabeledTextField timeout = new JLabeledTextField("Timeout");
    private JLabeledTextField username = new JLabeledTextField("Username");
    private JLabeledTextField password = new JLabeledTextField("Password");

    private JCheckBox ssl = new JCheckBox("Enabled", false);
    private JLabeledTextField sslAlgorithm = new JLabeledTextField("Algorithm");
    private JLabeledTextField sslKeyStore = new JLabeledTextField("Key Store");
    private JLabeledTextField sslKeyStoreType = new JLabeledTextField("Key Store Type");
    private JLabeledTextField sslKeyStorePassword = new JLabeledTextField("Key Store Password");
    private JLabeledTextField sslTrustStore = new JLabeledTextField("Trust Store");
    private JLabeledTextField sslTrustStoreType = new JLabeledTextField("Trust Store Type");
    private JLabeledTextField sslTrustStorePassword = new JLabeledTextField("Trust Store Password");

    private JLabeledTextField iterations = new JLabeledTextField("Number of samples to Aggregate");

    protected abstract void setMainPanel(JPanel panel);

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof AMQPSampler)) {
            return;
        }
        AMQPSampler sampler = (AMQPSampler) element;

        exchange.setText(sampler.getExchange());
        exchangeType.setText(sampler.getExchangeType());
        exchangeDurable.setSelected(sampler.isExchangeDurable());
        exchangeAutoDelete.setSelected(sampler.isExchangeAutoDelete());
        exchangeRedeclare.setSelected(sampler.getExchangeRedeclare());
        queue.setText(sampler.getQueue());
        routingKey.setText(sampler.getRoutingKey());
        virtualHost.setText(sampler.getVirtualHost());
        messageTTL.setText(sampler.getMessageTTL());
        messageExpires.setText(sampler.getMessageExpires());
        queueDurable.setSelected(sampler.isQueueDurable());
        queueExclusive.setSelected(sampler.isQueueExclusive());
        queueAutoDelete.setSelected(sampler.isQueueAutoDelete());
        queueRedeclare.setSelected(sampler.getQueueRedeclare());

        timeout.setText(sampler.getTimeout());
        iterations.setText(sampler.getIterations());

        host.setText(sampler.getHost());
        port.setText(sampler.getPort());
        username.setText(sampler.getUsername());
        password.setText(sampler.getPassword());

        ssl.setSelected(sampler.isConnectionSSL());

        sslAlgorithm.setText(sampler.getSslAlgorithm());
        sslKeyStore.setText(sampler.getSslKeyStore());
        sslKeyStoreType.setText(sampler.getSslKeyStoreType());
        sslKeyStorePassword.setText(sampler.getSslKeyStorePassword());
        sslTrustStore.setText(sampler.getSslTrustStore());
        sslTrustStoreType.setText(sampler.getSslTrustStoreType());
        sslTrustStorePassword.setText(sampler.getSslTrustStorePassword());

        log.info("AMQPSamplerGui.configure() called");
    }

    @Override
    public void clearGui() {
        exchange.setText("jmeterExchange");
        queue.setText("jmeterQueue");
        exchangeDurable.setSelected(AMQPSampler.DEFAULT_EXCHANGE_DURABLE);
        exchangeAutoDelete.setSelected(AMQPSampler.DEFAULT_EXCHANGE_AUTO_DELETE);
        exchangeRedeclare.setSelected(AMQPSampler.DEFAULT_EXCHANGE_REDECLARE);
        routingKey.setText("jmeterRoutingKey");
        virtualHost.setText("/");
        messageTTL.setText("");
        messageExpires.setText("");
        exchangeType.setText("direct");
        queueDurable.setSelected(true);
        queueExclusive.setSelected(false);
        queueAutoDelete.setSelected(false);
        queueRedeclare.setSelected(AMQPSampler.DEFAULT_QUEUE_REDECLARE);

        timeout.setText(AMQPSampler.DEFAULT_TIMEOUT_STRING);
        iterations.setText(AMQPSampler.DEFAULT_ITERATIONS_STRING);

        host.setText("localhost");
        port.setText(AMQPSampler.DEFAULT_PORT_STRING);
        username.setText("guest");
        password.setText("guest");

        ssl.setSelected(false);
        sslAlgorithm.setText("TLS1.2");
        sslKeyStore.setText("");
        sslKeyStoreType.setText("PKCS12");
        sslKeyStorePassword.setText("");
        sslTrustStore.setText("");
        sslTrustStoreType.setText("JKS");
        sslTrustStorePassword.setText("");
    }

    @Override
    public void modifyTestElement(TestElement element) {
        AMQPSampler sampler = (AMQPSampler) element;
        sampler.clear();
        configureTestElement(sampler);

        sampler.setExchange(exchange.getText());
        sampler.setExchangeDurable(exchangeDurable.isSelected());
        sampler.setExchangeAutoDelete(exchangeAutoDelete.isSelected());
        sampler.setExchangeRedeclare(exchangeRedeclare.isSelected());
        sampler.setQueue(queue.getText());
        sampler.setRoutingKey(routingKey.getText());
        sampler.setVirtualHost(virtualHost.getText());
        sampler.setMessageTTL(messageTTL.getText());
        sampler.setMessageExpires(messageExpires.getText());
        sampler.setExchangeType(exchangeType.getText());
        sampler.setQueueDurable(queueDurable.isSelected());
        sampler.setQueueExclusive(queueExclusive.isSelected());
        sampler.setQueueAutoDelete(queueAutoDelete.isSelected());
        sampler.setQueueRedeclare(queueRedeclare.isSelected());

        sampler.setTimeout(timeout.getText());
        sampler.setIterations(iterations.getText());

        sampler.setHost(host.getText());
        sampler.setPort(port.getText());
        sampler.setUsername(username.getText());
        sampler.setPassword(password.getText());

        sampler.setConnectionSSL(ssl.isSelected());
        sampler.setSslAlgorithm(sslAlgorithm.getText());
        sampler.setSslKeyStore(sslKeyStore.getText());
        sampler.setSslKeyStoreType(sslKeyStoreType.getText());
        sampler.setSslKeyStorePassword(sslKeyStorePassword.getText());
        sampler.setSslTrustStore(sslTrustStore.getText());
        sampler.setSslTrustStoreType(sslTrustStoreType.getText());
        sampler.setSslTrustStorePassword(sslTrustStorePassword.getText());

        log.info("AMQPSamplerGui.modifyTestElement() called, set user/pass to " + username.getText() + "/" +
            password.getText() + " on sampler " + sampler);
    }

    protected void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH); // Add the standard title

        JPanel mainPanel = new VerticalPanel();

        mainPanel.add(makeCommonPanel());

        iterations.setPreferredSize(new Dimension(50, 25));
        mainPanel.add(iterations);

        add(mainPanel);

        setMainPanel(mainPanel);
    }

    private Component makeCommonPanel() {

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;

        JPanel exchangeSettings = new JPanel(new GridBagLayout());
        exchangeSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Exchange"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        exchangeSettings.add(exchange, gridBagConstraints);

        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        exchangeSettings.add(exchangeType, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        exchangeSettings.add(exchangeDurable, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        exchangeSettings.add(exchangeAutoDelete, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        exchangeSettings.add(exchangeRedeclare, gridBagConstraints);

        JPanel queueSettings = new JPanel(new GridBagLayout());
        queueSettings.setBorder(newBorder("Queue"));

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        queueSettings.add(queue, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        queueSettings.add(routingKey, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        queueSettings.add(messageTTL, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        queueSettings.add(messageExpires, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        queueSettings.add(queueDurable, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        queueSettings.add(queueExclusive, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        queueSettings.add(queueAutoDelete, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        queueSettings.add(queueRedeclare, gridBagConstraints);

        JPanel sslSettings = new JPanel(new GridBagLayout());
        sslSettings.setBorder(newBorder("SSL"));

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        sslSettings.add(ssl, gridBagConstraints);

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 1;
        sslSettings.add(sslAlgorithm, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        sslSettings.add(sslKeyStore, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 1;
        sslSettings.add(sslKeyStorePassword, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 2;
        sslSettings.add(sslKeyStoreType, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 0;
        sslSettings.add(sslTrustStore, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 1;
        sslSettings.add(sslTrustStorePassword, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 2;
        sslSettings.add(sslTrustStoreType, gridBagConstraints);

        JPanel serverSettings = new JPanel(new GridBagLayout());
        serverSettings.setBorder(newBorder("Connection"));

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        serverSettings.add(host, gridBagConstraints);

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 1;
        serverSettings.add(port, gridBagConstraints);

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 2;
        serverSettings.add(timeout, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        serverSettings.add(virtualHost, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 0;
        serverSettings.add(username, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 1;
        serverSettings.add(password, gridBagConstraints);

        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        serverSettings.add(sslSettings, gridBagConstraints);

        JPanel exchangeQueueSettings = new VerticalPanel();
        exchangeQueueSettings.add(exchangeSettings);
        exchangeQueueSettings.add(queueSettings);

        JPanel commonPanel = new JPanel(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        commonPanel.add(serverSettings, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        commonPanel.add(exchangeQueueSettings, gridBagConstraints);

        return commonPanel;
    }

    private TitledBorder newBorder(String name) {
        return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), name);
    }

}
