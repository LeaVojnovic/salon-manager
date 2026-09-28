package hr.salonmanager.repository;

import java.sql.Connection;
import java.sql.SQLException;

/** Funkcija koja radi nad JDBC konekcijom. */
@FunctionalInterface
public interface SqlFunction<T> {
    T apply(Connection connection) throws SQLException;
}
