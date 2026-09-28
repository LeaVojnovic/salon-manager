package hr.salonmanager.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** JDBC infrastruktura i granica transakcije za Repository sloj. */
public class Database {
    private final DatabaseConfig config;
    private final ThreadLocal<Connection> transactionConnection = new ThreadLocal<>();

    public Database(DatabaseConfig config) {
        this.config = config;
    }

    public DatabaseConfig getConfig() {
        return config;
    }

    /** Izvršava schema.sql pri pokretanju aplikacije. */
    public void initializeSchema() {
        try (Connection connection = DriverManager.getConnection(config.url(), config.username(), config.password());
             InputStream input = getClass().getClassLoader().getResourceAsStream("db/schema.sql")) {
            if (input == null) {
                throw new RepositoryException("Nije pronađena schema.sql datoteka.");
            }
            String script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement statement = connection.createStatement()) {
                for (String sql : script.split(";")) {
                    if (!sql.isBlank()) {
                        statement.execute(sql);
                    }
                }
            }
        } catch (SQLException | IOException exception) {
            throw new RepositoryException("Inicijalizacija baze nije uspjela.", exception);
        }
    }

    public <T> T withConnection(SqlFunction<T> function) {
        Connection current = transactionConnection.get();
        if (current != null) {
            try {
                return function.apply(current);
            } catch (SQLException exception) {
                throw new RepositoryException("JDBC operacija nije uspjela.", exception);
            }
        }

        try (Connection connection = DriverManager.getConnection(config.url(), config.username(), config.password())) {
            return function.apply(connection);
        } catch (SQLException exception) {
            throw new RepositoryException("JDBC operacija nije uspjela.", exception);
        }
    }

    /** Izvršava više Repository operacija u istoj transakciji. */
    public <T> T inTransaction(TransactionCallback<T> callback) {
        if (transactionConnection.get() != null) {
            return callback.execute();
        }

        try (Connection connection = DriverManager.getConnection(config.url(), config.username(), config.password())) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            transactionConnection.set(connection);
            try {
                T result = callback.execute();
                connection.commit();
                return result;
            } catch (RuntimeException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            } catch (SQLException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw new RepositoryException("Transakcija nije uspjela.", exception);
            } finally {
                transactionConnection.remove();
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Otvaranje transakcije nije uspjelo.", exception);
        }
    }
}
