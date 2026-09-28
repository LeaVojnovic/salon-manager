package hr.salonmanager.repository;

/** Callback koji se izvodi unutar jedne JDBC transakcije. */
@FunctionalInterface
public interface TransactionCallback<T> {
    T execute();
}
