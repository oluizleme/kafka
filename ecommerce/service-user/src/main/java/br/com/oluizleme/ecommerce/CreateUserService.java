package br.com.oluizleme.ecommerce;

import br.com.oluizleme.ecommerce.consumer.ConsumerService;
import br.com.oluizleme.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService implements ConsumerService<Order> {

    private final LocalDatabase database;

    CreateUserService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExistis("create table Users (" +
                "uuid varchar(200) primary key," +
                "email varchar(200))");
    }

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException, IOException {
        new ServiceRunner(CreateUserService::new).start(1);
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("--------------------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println("VALUE " + record.value());

        var message = record.value();
        var order = message.getPayload();

        if (isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
        }
    }

    private boolean isNewUser(String email) throws SQLException {
        var results = database.query("select uuid from Users " +
                "where email = ? limit 1", email);
        return !results.next();
    }

    private void insertNewUser(String email) throws SQLException {
        var uuid = UUID.randomUUID().toString();
        database.update("insert into Users(uuid,email) " +
                "values (?,?)", uuid, email);

        System.out.println("User " + uuid + " and " + email + " added.");
    }

}
