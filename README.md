# SalonManager

SalonManager je Java Swing desktop aplikacija za upravljanje frizerskim salonom.
Implementacija prati odobreni koncept projekta:

- MVC organizacija: `view`, `controller` odgovornost kroz događaje prikaza, `service` i `repository` sloj
- JDBC Repository implementacije s `PreparedStatement` i try-with-resources
- trajno spremanje u relacijsku bazu; zadana baza je H2 datoteka `./data/salonmanager`
- klijenti, djelatnici, usluge, termini i postavke radnog vremena
- provjera preklapanja termina i radnog vremena u istoj transakciji u kojoj se termin sprema
- Command + Memento za poništavanje posljednje promjene termina u trenutnoj sesiji
- jedinični/integracijski testovi ključnih poslovnih pravila

## Pokretanje

Projekt koristi Java 17 i Maven. Pokretna klasa je `hr.salonmanager.Main`.

```text
mvn clean test
mvn exec:java -Dexec.mainClass=hr.salonmanager.Main
```

Za drugu JDBC bazu mogu se zadati:

```text
-Dsalon.db.url=jdbc:h2:tcp://localhost/~/salonmanager
-Dsalon.db.username=sa
-Dsalon.db.password=
```

Iste vrijednosti moguće je zadati environment varijablama `SALON_DB_URL`,
`SALON_DB_USERNAME` i `SALON_DB_PASSWORD`.

## Organizacija

`model` sadrži domenske klase i enum tipove, `service` poslovna pravila,
`repository` sučelja i JDBC implementacije, `command` i `memento` obrasce,
a `view` Swing prozore i forme. SQL shema nalazi se u
`src/main/resources/db/schema.sql`.

Javadoc se može generirati naredbom:

```text
mvn javadoc:javadoc
```
