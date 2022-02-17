package uk.co.a1dutch.jmeter.amqp.gui;

import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;
import uk.co.a1dutch.jmeter.amqp.AMQPConsumer;

public class AMQPConsumerGui extends AMQPSamplerGui {

    private static final long serialVersionUID = 1L;

    protected JLabeledTextField receiveTimeout = new JLabeledTextField("Receive Timeout");
    protected JLabeledTextField prefetchCount = new JLabeledTextField("Prefetch Count");

    private final JCheckBox purgeQueue = new JCheckBox("Purge Queue", false);
    private final JCheckBox autoAck = new JCheckBox("Auto ACK", true);
    private final JCheckBox useTx = new JCheckBox("Transactional", AMQPConsumer.DEFAULT_USE_TX);

    private JPanel mainPanel;

    public AMQPConsumerGui() {
        init();
    }

    @Override
    protected void init() {
        super.init();
        prefetchCount.setPreferredSize(new Dimension(100, 25));
        useTx.setPreferredSize(new Dimension(100, 25));

        mainPanel.add(receiveTimeout);
        mainPanel.add(prefetchCount);
        mainPanel.add(purgeQueue);
        mainPanel.add(autoAck);
        mainPanel.add(useTx);
    }

    @Override
    public String getStaticLabel() {
        return "AMQP Consumer";
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof AMQPConsumer)) {
            return;
        }
        AMQPConsumer sampler = (AMQPConsumer) element;

        prefetchCount.setText(sampler.getPrefetchCount());
        receiveTimeout.setText(sampler.getReceiveTimeout());
        purgeQueue.setSelected(sampler.isPurgeQueue());
        autoAck.setSelected(sampler.isAutoAck());
        useTx.setSelected(sampler.isUseTx());
    }

    @Override
    public void clearGui() {
        super.clearGui();
        prefetchCount.setText(AMQPConsumer.DEFAULT_PREFETCH_COUNT_STRING);
        receiveTimeout.setText("");
        purgeQueue.setSelected(false);
        autoAck.setSelected(true);
        useTx.setSelected(AMQPConsumer.DEFAULT_USE_TX);
    }

    @Override
    public TestElement createTestElement() {
        AMQPConsumer sampler = new AMQPConsumer();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement te) {
        AMQPConsumer sampler = (AMQPConsumer) te;
        sampler.clear();
        configureTestElement(sampler);

        super.modifyTestElement(sampler);

        sampler.setPrefetchCount(prefetchCount.getText());

        sampler.setReceiveTimeout(receiveTimeout.getText());
        sampler.setPurgeQueue(purgeQueue.isSelected());
        sampler.setAutoAck(autoAck.isSelected());
        sampler.setUseTx(useTx.isSelected());
    }

    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void setMainPanel(JPanel panel) {
        mainPanel = panel;
    }

}
