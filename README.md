# Finance Manager

A desktop finance management application for small businesses built with JavaFX. It allows a company director to manage employees, clients, projects, transactions, and budgets — all within a single role-aware interface.

## Features

- **Dashboard** — overview charts showing income, expenses, and monthly trends
- **Transactions** — record and filter income/expense transactions with XML import
- **Employees** — manage staff with salary, status, and role tracking
- **Clients** — maintain a client directory
- **Projects** — track projects with budget limits and spend monitoring
- **Budget** — monitor account balances across bank, cash, credit, and savings accounts
- **Reports** — visual breakdowns by category, project, and employee with XML export
- **Settings** — switch language (English / Slovak), manage company users (Director only)

Access to sensitive screens is role-restricted — only the **Director** role can manage users and see full financial data.

## Tech Stack

| Layer | Technology |
|---|---|
| UI | JavaFX 21 (pure Java, no FXML) |
| Database | PostgreSQL |
| Migrations | Flyway |
| Logging | SLF4J + Logback |
| Build | Gradle (Kotlin DSL) |
| Java | JDK 25 |
| Security | BCrypt password hashing |
| i18n | English / Slovak |

## Prerequisites

- **JDK 25** — [Download](https://openjdk.org/)
- **PostgreSQL** — running instance (local or remote)
- **Gradle** — bundled via `./gradlew`, no separate install needed

## Setup

### 1. Clone the repository

```bash
git clone <repository-url>
cd VAVA-Finance-Manager
```

### 2. Create the PostgreSQL database

Open a terminal and connect to PostgreSQL as a superuser:

```bash
psql -U postgres
```

Create a dedicated database and user for the application:

```sql
CREATE DATABASE finance_manager;
CREATE USER finance_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE finance_manager TO finance_user;

-- PostgreSQL 15+ also requires this:
\c finance_manager
GRANT ALL ON SCHEMA public TO finance_user;
```

Exit psql with `\q`.

> If you already have a local user with superuser rights you can skip creating a new user and just use that one.

### 3. Configure the connection

`application.properties` is excluded from the repository for security reasons.
Copy the template and fill in your values:

```bash
cp src/main/resources/application.properties.template src/main/resources/application.properties
```

Then open `src/main/resources/application.properties` and set your values:

```properties
db.url=jdbc:postgresql://localhost:5432/finance_manager
db.user=finance_user
db.password=your_password
```

The three properties map directly to the JDBC connection — change the host, port, or database name if your PostgreSQL instance is not on the default `localhost:5432`.

Flyway runs all migration scripts automatically the first time the application starts, so **no manual schema creation is needed** — just an empty database.

### 4. Run the application

```bash
./gradlew run
```

On Windows:

```bash
gradlew.bat run
```

## First Launch

1. On the **Login** screen, click **Register** to create your account.
2. After registration you will be prompted to **create a company** — enter a name and industry.
3. You are now the **Director** of your company and have full access to all screens.
4. To add more users, go to **Settings → User Management** and use the **Add User** button.


## XML Import / Export

- **Import transactions** — Transactions screen → Import XML button. Expected format:

```xml
<transactions>
  <transaction>
    <description>Office supplies</description>
    <amount>150.00</amount>
    <type>PURCHASE</type>
    <date>2025-03-15</date>
    <category>EXPENSES</category>
  </transaction>
</transactions>
```

- **Export reports** — Reports screen → Export XML button. Exports the currently displayed report data.

## Running Tests

```bash
./gradlew test
```

Test results are saved to `build/reports/tests/test/index.html`.

## Project Structure

```
src/
  main/
    java/org/example/
      view/             # UI layer (JavaFX screens, dialogs, templates)
      viewModel/        # ViewModel layer (UI state, business logic binding)
      model/
        database/       # Service and entity classes, JDBC access
        validation/     # Input validators with typed error codes
        reports/        # DTO classes for report data
        models/         # Business logic models
      logging/          # AppLog utility wrapper
      exceptions/       # Custom exception types
    resources/
      db/migration/     # Flyway SQL migration scripts (V1–V15)
      styles/           # CSS files (global, table, charts)
  test/
    java/org/example/
      model/validation/ # Unit tests for all validators
```
