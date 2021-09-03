package br.com.oluizleme.ecommerce;

import br.com.oluizleme.ecommerce.consumer.ConsumerService;
import br.com.oluizleme.ecommerce.consumer.ServiceRunner;
import br.com.oluizleme.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerService<Order> {

    private final LocalDatabase database;

    FraudDetectorService() throws SQLException {
        this.database = new LocalDatabase("frauds_database");
        this.database.createIfNotExistis("create table Orders (" +
                "uuid varchar(200) primary key," +
                "is_fraud boolean)");
    }

    public static void main(String[] args) {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("--------------------------------------------------------");
        System.out.println("Processing new order, checking for fraud.");
        System.out.println("Key " + record.key());
        System.out.println("Value " + record.value());
        System.out.println("Partition " + record.partition());
        System.out.println("Offset " + record.offset());

        var message = record.value();
        var order = message.getPayload();

        if (wasProcessed(order)) {
            System.out.println("Order " + order.getOrderId() + " was already processed");
            return;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isFraud(order)) {
            database.update("insert into Orders(uuid,is_fraud) values(?,true)", order.getOrderId());
            //pretending thar the fraud happens when the amount is > = 4500
            System.out.println("Order is a fraud!!! " + order.toString());
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()),
                    order);
        } else {
            database.update("insert into Orders(uuid,is_fraud) values(?,false)", order.getOrderId());
            System.out.println("Order approved: " + order.toString());
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(),
                    new CorrelationId(FraudDetectorService.class.getSimpleName()), order);
        }
        System.out.println("Order processed");
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal(4500)) >= 0;
    }
}
