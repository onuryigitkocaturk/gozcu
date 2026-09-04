# Gözcü

**Gözcü** is a database monitoring application. The name means "the watcher" in Turkish.

You connect it to a database that you want to watch. It reads the tables and the
columns inside that database. Then you build a check query by dragging column names
into a visual editor. You do not write any SQL. The application saves your query and
runs it again and again on a schedule. When the result meets the condition that you
set, the application sends an email to a group of people.

The project has a Spring Boot backend and a React frontend. It works with PostgreSQL,
MySQL and Microsoft SQL Server.

## Contents

1. What problem it solves
2. Main features
3. Technology
4. The big picture
5. Backend folder structure
6. Frontend folder structure
7. Data model
8. The life of a query
9. The query builder in detail
10. Alerts, the scheduler and email
11. Security
12. Permission model
13. REST API
14. Setup
15. Configuration
16. Design decisions and the reason behind them
17. Known limits and next steps

## 1. What problem it solves

The idea is simple. Many teams need to answer one question again and again:

> "Tell me when the number of rows that match this condition passes this limit."

Normally somebody writes an SQL query, runs it by hand every morning, looks at the
result and then writes an email to the team. This is slow, easy to forget and it needs
a person who knows SQL. Gözcü moves this job to a background service.

The application is not built for one fixed table. It does not know the table structure
when you compile it. It reads the structure at run time, so it can work with almost any
database that you give it.

Some real examples:

* **Car fleet (PostgreSQL).** Every day, find the cars where the warranty ends inside
  30 days **and** the model year is newer than 2020 **and** the last service was more
  than 6 months ago. Send the list to the fleet team.
* **Online shop orders (MySQL).** Every hour, find the orders where the payment is
  "successful" **and** the shipping status is still "preparing" **and** the order date
  is older than 3 days. Send them to the operations team.
* **Server logs (PostgreSQL).** If the number of rows with level "CRITICAL" in the last
  hour is more than 5, tell the on call team at once.

All of these use the same visual condition tree, the same safe SQL layer and the same
email flow. Only the connection details and the columns are different.

## 2. Main features

* **Connect to your own database.** Each project keeps its own host, port, database
  name, user name and password. You can test the connection before you save it.
* **Automatic table discovery.** The application lists the tables and the columns of
  the target database through `information_schema`. Nothing is written in the code by
  hand.
* **Visual query builder.** You drag a column into a group box, choose an operator and
  type a value. You can put groups inside other groups, so you can build deep AND and
  OR logic.
* **Relative dates.** A condition can say "30 days in the future" or "6 months in the
  past". The real date is calculated again on every run, so the query never becomes old.
* **Alerts.** An alert watches the row count of a query. You choose an operator and a
  limit value, for example "more than 0" or "less than 10".
* **Scheduler.** Active queries run every hour or every day. The result of every run is
  written to a log table.
* **Email notification.** When an alert is triggered, the application sends an HTML
  email that also contains a table of the matched rows.
* **Export.** You can download the result of a query as an Excel file or as a PDF file.
* **Two level permission system.** A global role for the whole system and a separate
  role for each project.
* **Login verification for new devices.** When you log in from a browser that the system
  does not know, it sends a six digit code to your email address first.
* **Encrypted connection passwords.** The password of a watched database is stored with
  AES encryption, never as plain text.

## 3. Technology

**Backend**

* Java 21
* Spring Boot 4.1.0 (Web MVC, Data JPA, Security, Validation, Mail)
* Spring Security with JWT (jjwt 0.12.6, HS256)
* Hibernate and Spring Data JPA for the application database
* Plain `JdbcTemplate` for the watched databases
* JDBC drivers: PostgreSQL, MySQL, Microsoft SQL Server
* Apache POI 5.3.0 for Excel export
* OpenPDF 2.0.3 for PDF export
* Maven, packaged as a JAR with an embedded Tomcat

**Frontend**

* React 19 with TypeScript
* Vite as the build tool and dev server
* React Router for the pages
* `@react-spring/web` for the animations
* Plain CSS with CSS variables. There is no UI library and no CSS framework.

**Infrastructure**

* PostgreSQL 16 in Docker for the application data
* MailHog in Docker as a fake SMTP server for development

## 4. The big picture

```
React frontend (Vite dev server, port 5173)
        │
        │  REST calls with a JWT in the Authorization header
        ▼
Spring Boot backend (port 8080)
        │
        ├──▶ Application database (PostgreSQL, port 5432)
        │       users, groups, projects, memberships, queries,
        │       alerts and logs. Managed by JPA and Hibernate.
        │
        ├──▶ Watched databases (PostgreSQL, MySQL or MSSQL)
        │       One connection for each project. Opened with plain
        │       JDBC. No JPA here, because these tables are not
        │       known when the code is compiled.
        │
        └──▶ SMTP server (MailHog in development, port 1025)
                alert emails and login verification emails
```

There are two database layers and they are kept apart on purpose.

The first layer is the data of the application itself: users, projects, query
definitions and logs. This layer has a fixed shape, so JPA entities fit very well.

The second layer is the data that the user wants to watch. Here the shape is unknown.
Every user can watch a different table with different columns. You cannot write an
entity class for a table that you have never seen, so this layer uses generic JDBC and
returns rows as `List<Map<String, Object>>`.

The flow between the layers goes in one direction only:

```
Controller  ▶  Service  ▶  Repository
```

A layer only calls the layer under it. A controller never talks to a repository
directly. Entities never leave the controller. The controller always returns a DTO, and
the conversion happens in a mapper class.

## 5. Backend folder structure

```
model/          JPA entity classes only (User, Group, Project,
                ProjectMembership, ProjectTable, Query, Alert,
                AlertLog, TrustedDevice, LoginVerification)
enums/          Role, ProjectRole, Frequency, LogStatus, DatabaseType,
                ConditionOperator, LogicOperator and other enums
dto/            request and response shapes of the REST API
dto/querydefinition/
                the JSON tree of the query builder (QueryNode,
                GroupNode, ConditionNode, LiteralValue,
                RelativeDateValue)
repository/     Spring Data JPA repositories
service/        business logic. Every service is an interface, and the
                class that implements it lives under service/impl/
mapper/         entity to DTO conversion, written by hand
controller/     REST endpoints
security/       JWT creation and reading, Spring Security setup,
                trusted device handling
connector/      connection to the watched database, table discovery,
                query execution, password encryption
querybuilder/   turns the JSON tree into safe parameterised SQL and
                validates the column names
alerting/       decides if an alert is triggered right now. It only
                calculates. It does not send any email.
scheduler/      @Scheduled jobs that run the queries again and again
notification/   builds and sends the HTML emails
export/         Excel and PDF export of query results
geocoding/      turns latitude and longitude into a city name, using a
                local CSV file and no external service
exception/      custom exceptions and the global exception handler
config/         CORS, data source, password encoder and Jackson beans
```

## 6. Frontend folder structure

```
src/api/                 a small fetch wrapper plus one client file for
                         every resource (auth, projects, queries,
                         alerts, groups, users, connector)
src/types/api.ts         TypeScript types that match the backend DTOs
src/context/             AuthContext (login state) and ToastContext
                         (small messages in the corner)
src/hooks/               useAsync for loading and error state,
                         useMyProjectRole for the project role
src/components/ui/       Button, Card, Input, Select, Badge, Modal,
                         DataTable, Spinner, EmptyState
src/components/layout/   AppShell, ProtectedRoute, AdminRoute
src/components/querybuilder/
                         FieldPalette, GroupBlock, ConditionRow,
                         ValueEditor and the tree model in
                         builderTypes.ts
src/pages/               one file for every route
src/styles/              theme.css, components.css, querybuilder.css
```

The pages are:

* `/login` and `/register`
* `/` the dashboard with the list of your projects
* `/about` a page that explains the application
* `/account` your own account and your trusted devices
* `/projects/:projectId` tables and members of one project
* `/projects/:projectId/tables/:tableId` data preview and query list
* `.../queries/new` and `.../queries/:queryId/edit` the query builder
* `.../queries/:queryId` run the query, export it and manage its alerts
* `/admin/users`, `/admin/groups`, `/admin/connector` for administrators

## 7. Data model

Entity classes and what they mean:

* **User.** Account with a user name, an email address, a BCrypt password hash and a
  global role.
* **Group.** A list of users. A group is only a target for email notifications.
* **Project.** A working area. It holds the connection details of one watched database:
  type, host, port, database name, user name and the encrypted password.
* **ProjectMembership.** It says which user works in which project and with which
  project role. It started as a simple join table, but it became a real entity when it
  needed extra fields (`role` and `joinedAt`).
* **ProjectTable.** One table of the watched database that a project follows. A project
  can follow many tables.
* **Query.** A saved check. It has a name, the JSON definition of the condition tree, a
  frequency (hourly or daily), the project, the project table and the user who created
  it.
* **Alert.** A rule on top of a query. It holds the condition (operator plus a limit
  value) as JSON and the groups that receive the email.
* **AlertLog.** One line for every evaluation, with the status `TRIGGERED`,
  `NOT_TRIGGERED` or `ERROR` and a short message.
* **TrustedDevice.** A browser that already passed the email verification. It stores
  only hashes, never the raw token.
* **LoginVerification.** A pending six digit code with an expiry time and an attempt
  counter.

The relations:

```
User          ▶ ProjectMembership ◀ Project     (many to many with extra fields)
User          ▶ Group                           (many to many, join table user_group)
Project       ▶ ProjectTable                    (one to many)
Project       ▶ Query                           (one to many)
ProjectTable  ▶ Query                           (one to many)
Query         ▶ Alert                           (one to many)
Alert         ▶ Group                           (many to many)
Alert         ▶ AlertLog                        (one to many)
User          ▶ TrustedDevice                   (one to many)
```

`Alert` also has a direct link to `Project`. This value is a copy of
`Query.getProject()`. It is stored twice on purpose, so the scheduler can reach the
connection details of the project without an extra join. A query never moves to another
project, so the two values can never become different.

## 8. The life of a query

1. **A project is created.** You type the connection details of the database that you
   want to watch. Before you save, you can press "test connection" and see if the host,
   the port and the password really work. When you save, the password is encrypted with
   AES and only the encrypted text goes into the database.
2. **Tables are discovered.** The application asks the target database for its table
   list and shows it to you. You choose the tables that you want to follow and add them
   to the project. From this moment those table names are a white list.
3. **A query is built.** You open the query builder. The screen shows the real columns
   of the chosen table. You drag a column into a group, pick an operator and give a
   value. When you press save, the backend checks every column name against the real
   schema, and then stores the tree as JSON.
4. **An alert is added.** You say when the query should wake somebody up, for example
   "row count is greater than 0". You also choose one or more groups that will receive
   the email.
5. **The scheduler runs.** Every active query runs on its frequency. For every alert of
   that query, the backend counts the matching rows and compares the count with the
   limit.
6. **The email goes out.** If the condition is true, all members of the chosen groups
   receive an HTML email with a summary table of the matched rows. Every run writes one
   row into `alert_logs`, also when nothing was triggered and also when an error
   happened.

## 9. The query builder in detail

### The JSON tree

The condition of a query is stored as a tree of nodes. There are two node types.

A **group node** holds a logic operator (`AND` or `OR`) and a list of children. A child
can be a condition or another group. This is how nested logic is possible.

A **condition node** holds a field name, an operator and a value.

Jackson creates the correct Java class from the `type` field of the JSON, so the tree
can be as deep as you want.

A small example:

```json
{
  "type": "GROUP",
  "logic": "AND",
  "children": [
    {
      "type": "CONDITION",
      "field": "warranty_end_date",
      "operator": "LESS_THAN",
      "value": {
        "kind": "RELATIVE_DATE",
        "direction": "FUTURE",
        "amount": 30,
        "unit": "DAYS"
      }
    },
    {
      "type": "CONDITION",
      "field": "model_year",
      "operator": "GREATER_THAN",
      "value": { "kind": "LITERAL", "value": 2020 }
    }
  ]
}
```

### The operators

`EQUALS`, `NOT_EQUALS`, `GREATER_THAN`, `GREATER_THAN_OR_EQUAL`, `LESS_THAN`,
`LESS_THAN_OR_EQUAL`, `CONTAINS`, `IS_NULL`, `IS_NOT_NULL`.

### The two value types

* **Literal value.** A fixed number, text or date that you type yourself.
* **Relative date.** A direction (`FUTURE` or `PAST`), an amount and a unit (`HOURS`,
  `DAYS`, `MONTHS`, `YEARS`). The real date is calculated at the moment of every run.
  A query that says "in the next 30 days" stays correct for ever. It never freezes on
  the day when you created it.

### How the tree becomes SQL

Two classes do this work:

* `QueryDefinitionValidator` walks the whole tree. For every condition it asks the real
  database for the column list of the table and checks that the field name is really
  there. It also checks that a value exists when the operator needs one.
* `QuerySqlBuilder` walks the tree a second time and returns a `SqlFragment`, which is a
  record with an SQL string and a list of parameters. Groups are joined with `AND` or
  `OR` and wrapped in brackets. Every condition becomes something like `column > ?`.

### Why this is safe against SQL injection

This part is the most important design point of the project.

* **Values never touch the SQL string.** They are always bound with a question mark and
  sent to a `PreparedStatement`.
* **Operators never come from the JSON as raw text.** The JSON only carries an enum
  name. A fixed Java `switch` turns that enum into the SQL symbol. A value that is not
  in the enum cannot pass the JSON parsing step.
* **Column names cannot be bound with a question mark**, because they are part of the
  structure of the SQL and not a value. So they are checked against a white list. The
  list comes from `information_schema`, which means it comes from the database itself.
* **Table names use the same idea.** A table name is only accepted when it is already
  saved in `project_tables` for that project.

Because of these four rules, no text that a user types can ever change the shape of the
SQL.

### Why not Criteria API or QueryDSL

Those tools build queries from JPA entity classes. Here there are no entity classes for
the watched tables, and there cannot be any, because the structure is only known at run
time. So a white list plus parameter binding is the correct answer for this project.

## 10. Alerts, the scheduler and email

An alert stores its condition as a small JSON object with an operator and a number:

```json
{ "operator": "GREATER_THAN", "value": 0 }
```

`AlertEvaluationService` runs the query with `SELECT COUNT(*)`, compares the count with
the limit in Java and returns a small record with two fields: `triggered` and
`matchCount`. This service does not send any email. It only calculates. Because of that,
the same service can be used by the scheduler and also by the manual "evaluate" button
in the user interface.

`AlertScheduler` has two jobs. One job handles the queries with the frequency `HOURLY`
and the other one handles `DAILY`. Both jobs read the active queries, read the active
alerts of every query, evaluate them one by one and write an `AlertLog` row for each
result. The jobs are annotated with `@Transactional`, because there is no HTTP request
here, so the lazy relations need an open session.

When an alert is triggered, the scheduler collects the email addresses of all users in
all groups of that alert, removes the duplicates and asks `NotificationService` to send
the email. The email is HTML and it contains a table with the matched rows.

In development the emails do not go to the real internet. MailHog catches them and shows
them in a web page at `http://localhost:8025`.

## 11. Security

**Passwords.** User passwords are hashed with BCrypt. The system never stores a plain
password.

**JWT.** After a successful login the backend returns a signed token. The token lives
for one hour. There is no refresh token, and this is a conscious choice for the size of
this project. The frontend keeps the token in React state only. It does not use
`localStorage` or `sessionStorage`, so the session ends when you refresh the page.

**Login from a new device.** The login has two steps.

1. The user name and the password are checked first.
2. Then the backend looks at the `device_token` cookie and at the browser signature. If
   the browser is already trusted, the JWT is returned at once.
3. If the browser is not known, no JWT is returned. Instead the backend creates a six
   digit code, hashes it with BCrypt, saves it with a ten minute expiry time and sends
   it to the email address of the user.
4. The user types the code. After a correct code the backend returns the JWT and sets a
   new `device_token` cookie. The cookie is `HttpOnly`, so JavaScript cannot read it,
   and it is valid for 90 days.

A code can be used only once. After five wrong tries it stops working. The trusted
device table stores only SHA 256 hashes of the token and of the browser signature.

**Approximate location in the email.** The verification email also shows a city name, so
the user can see where the login attempt came from. The browser sends latitude and
longitude, and `GeocodingService` finds the nearest district in a local CSV file of
Turkish districts. No request goes to any external service, so no personal data leaves
the machine. If the nearest point is more than 150 km away, the service reports that the
attempt came from outside the country.

**Connection passwords.** The password of a watched database belongs to the user, not to
the application, so it must be readable again later. It is encrypted with AES in GCM
mode. GCM also detects if somebody changed the stored text. The key comes from the
`CONNECTION_ENCRYPTION_KEY` environment variable and is never written in the code.

**Secrets.** `JWT_SECRET` and `CONNECTION_ENCRYPTION_KEY` have no default value in
`application.properties`. If you do not set them, the application does not start. This
is on purpose: a missing secret should be a loud error, not a silent weak default.

**CORS.** Only `localhost:5173` and `127.0.0.1:5173` are allowed, so a random web page
cannot call the API from your browser.

## 12. Permission model

There are two role systems and they work together.

**Global role** (`Role`) is about the whole system:

* `USER` is the normal account. Everybody starts here.
* `ADMIN` can manage users and groups, can create and list projects and can reach every
  project, even without a membership.

**Project role** (`ProjectRole`) is about one single project. The same person can be an
owner in one project and a reporter in another one. The order of the roles matters,
because the check is hierarchical: an endpoint that needs `MAINTAINER` also accepts
`OWNER`, but not `DEVELOPER`.

```
REPORTER  <  DEVELOPER  <  MAINTAINER  <  OWNER
```

What each project role can do:

* **REPORTER.** Read only. See the project, the table list, the column list and a
  preview of the table data. Run a query, evaluate an alert, read the alert logs and
  download the Excel or PDF export. This role cannot create or change anything.
* **DEVELOPER.** Everything above, plus create and update queries and create alerts.
  This is the first role that can write rules.
* **MAINTAINER.** Everything above, plus add and remove members, change the project role
  of a member, connect a new table to the project and delete queries and alerts. This is
  the role that runs the daily life of the project.
* **OWNER.** Everything above, plus remove a table from the project and delete the whole
  project. The actions that cannot be undone stay in this role.

In the code the check is a Spring Security expression, for example:

```java
"hasRole('ADMIN') or @projectAuthorizationService.isAtLeastMaintainer(principal.id, #projectId)"
```

`ProjectAuthorizationService` builds the set of roles that are equal or higher than the
required one and asks the repository if the user has one of them in that project.

## 13. REST API

All endpoints need a JWT in the `Authorization: Bearer <token>` header, except the three
endpoints under `/api/auth`.

**Authentication**

```
POST   /api/auth/register            create a new account
POST   /api/auth/login               step one of the login
POST   /api/auth/verify-login-code   step two, send the six digit code
```

**Users**

```
GET    /api/users/me                     your own profile
GET    /api/users/me/account             your profile and your trusted devices
DELETE /api/users/me/devices/{deviceId}  remove one trusted device
GET    /api/users                        list all users (ADMIN)
PUT    /api/users/{id}                   update a user (ADMIN)
DELETE /api/users/{id}                   delete a user (ADMIN, or your own account)
PUT    /api/users/{id}/role              change the global role (ADMIN)
```

**Groups**

```
POST   /api/groups                          create a group (ADMIN)
GET    /api/groups                          list groups (any logged in user)
DELETE /api/groups/{id}                     delete a group (ADMIN)
GET    /api/groups/{groupId}/users          list the members (ADMIN)
POST   /api/groups/{groupId}/users/{userId} add a member (ADMIN)
DELETE /api/groups/{groupId}/users/{userId} remove a member (ADMIN)
```

**Projects**

```
POST   /api/projects                                    create (ADMIN)
GET    /api/projects                                    list all (ADMIN)
GET    /api/projects/my                                 your own projects
GET    /api/projects/{projectId}                        details (REPORTER)
DELETE /api/projects/{projectId}                        delete (OWNER)
GET    /api/projects/{projectId}/users                  members (REPORTER)
POST   /api/projects/{projectId}/users/{userId}         add a member (MAINTAINER)
PUT    /api/projects/{projectId}/users/{userId}/role    change a role (MAINTAINER)
DELETE /api/projects/{projectId}/users/{userId}         remove a member (MAINTAINER)
GET    /api/projects/{projectId}/discover-tables        read the tables of the
                                                        watched database (MAINTAINER)
POST   /api/projects/{projectId}/tables                 follow a table (MAINTAINER)
DELETE /api/projects/{projectId}/tables/{tableId}       stop following it (OWNER)
GET    /api/projects/{projectId}/tables                 followed tables (REPORTER)
GET    /api/projects/{projectId}/tables/{name}/columns  column list (REPORTER)
GET    /api/projects/{projectId}/tables/{name}/data     data preview (REPORTER)
GET    /api/projects/{projectId}/dashboard-stats        small summary (REPORTER)
```

**Queries** (under `/api/projects/{projectId}/tables/{tableId}/queries`)

```
POST   /                            create a query (DEVELOPER)
PUT    /{queryId}                   update a query (DEVELOPER)
GET    /                            list the queries (REPORTER)
DELETE /{queryId}                   delete a query (MAINTAINER)
GET    /{queryId}/run               run it and return the rows (REPORTER)
GET    /{queryId}/count             return only the row count (REPORTER)
GET    /{queryId}/export/excel      download the result as .xlsx (REPORTER)
GET    /{queryId}/export/pdf        download the result as .pdf (REPORTER)
```

**Alerts** (under the query path, plus `/alerts`)

```
POST   /                        create an alert (DEVELOPER)
GET    /                        list the alerts (REPORTER)
DELETE /{alertId}               delete an alert (MAINTAINER)
GET    /{alertId}/evaluate      check the alert right now (REPORTER)
GET    /{alertId}/logs          read the run history (REPORTER)
```

**Connector**

```
POST   /api/connector/test-connection   try connection details (ADMIN)
```

An administrator can call every endpoint above, also without a project membership.

## 14. Setup

### Step 1: start the Docker services

Run this in the root folder of the repository. It starts the PostgreSQL database of the
application and the MailHog fake mail server.

```bash
docker compose up -d
```

Check the result with `docker ps`. You should see two containers:
`query-monitor-postgres` and `query-monitor-mailhog`.

### Step 2: start the backend

The application does not start without the two secrets, so create them first.

```bash
cd backend
export JWT_SECRET=$(openssl rand -base64 48)
export CONNECTION_ENCRYPTION_KEY=$(openssl rand -base64 32)
./mvnw spring-boot:run
```

The backend listens on `http://localhost:8080`.

Keep the same `CONNECTION_ENCRYPTION_KEY` between two runs. If you create a new key, the
old connection passwords in the database cannot be read any more.

### Step 3: start the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:5173`.

### Step 4: create the first administrator

Everybody who registers in the user interface starts with the role `USER`. The first
administrator must be set by hand in the database:

```sql
UPDATE users SET role='ADMIN' WHERE username='your_user_name';
```

After that, an administrator can promote other people from the user interface.

### Step 5: prepare a database to watch

You need a second database with some data inside it. It can live in the same PostgreSQL
container:

```bash
docker exec -it query-monitor-postgres psql -U query_monitor -c "CREATE DATABASE monitored_db;"
```

Then create a table with a few rows, log in as an administrator and create a project
that points to this database.

### Useful addresses

* Frontend: `http://localhost:5173`
* Backend: `http://localhost:8080`
* MailHog web page, where you can read every email that the system sends:
  `http://localhost:8025`

### Other commands

```bash
./mvnw test        # backend tests
npm run build      # type check and production build of the frontend
npm run lint       # frontend linter
```

## 15. Configuration

Environment variables:

* `JWT_SECRET` (required). The key that signs the tokens.
* `CONNECTION_ENCRYPTION_KEY` (required). A Base64 AES key for the connection passwords.
* `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (optional). The application database. The
  defaults point to the local Docker container.
* `MAIL_HOST`, `MAIL_PORT` (optional). The default is MailHog on `localhost:1025`.

Properties in `backend/src/main/resources/application.properties`:

* `jwt.expiration-ms` is the life time of a token. The default is one hour.
* `scheduler.hourly-rate-ms` and `scheduler.daily-rate-ms` control how often the
  scheduler runs. The real values are 3600000 and 86400000.
* `notification.mail.from` is the sender address of the emails.

> **Please note.** The file still contains two test values for the scheduler (60000 and
> 90000 milliseconds). They exist so that you can watch the flow without waiting one
> hour. Delete these two lines before any serious use, so the scheduler goes back to one
> hour and one day.

## 16. Design decisions and the reason behind them

**No refresh token, only an access token of one hour.** A refresh token needs storage,
rotation and a revoke list. For the size of this project that is extra complexity
without a real benefit.

**No MapStruct. The mappers are written by hand.** This project is also a learning
project. A generated mapper hides the conversion. A hand written mapper shows every
step, and every mapper stays small, with one `toResponse` method.

**No Criteria API and no QueryDSL for the watched tables.** Those tools need entity
classes. The watched tables have none, because their structure is unknown at compile
time. A column white list plus bound parameters gives the same safety here.

**No `DbConnection` entity.** The connection details are a part of the project, so they
live in the `Project` entity. A separate table would only add one more join without any
gain.

**A factory for `JdbcTemplate` instead of a fixed bean.** Spring normally creates one
`DataSource` bean when the application starts. That does not work here, because every
project can point to a different database. `JdbcTemplateFactory` builds a
`JdbcTemplate` at the moment when it is needed, from the connection details of the
project that is running.

**A separate endpoint for the connection test.** A user should be able to see that the
host, the port and the password are correct before the project is saved. A failed save
with a long error message is a bad first experience.

**The project link on `Alert` is stored twice.** It is a copy of the project of the
query. The scheduler needs the connection details of the project for every alert, and
this copy removes one join from a job that runs all the time. A query never changes its
project, so the copy can never be wrong.

**`@Column(columnDefinition = "TEXT")` instead of `@Lob`.** In PostgreSQL, `@Lob` maps
to a large object with an `oid`, and reading it needs a transaction. A method without
`@Transactional` failed with an "auto commit" error. In PostgreSQL a normal `text`
column has no length limit anyway, so `@Lob` was not needed at all.

**`ProjectMembership` is a real entity, not a plain join table.** A simple many to many
relation was enough at the beginning. When the project role and the join date became
necessary, the join table needed columns of its own, and an entity is the correct tool
for that.

**Local CSV for the reverse geocoding.** An external geocoding API would send the
position of the user to a third company on every login attempt. A local list of
districts is smaller, faster and more private.

## 17. Known limits and next steps

These points are known and open on purpose.

* **No overlap protection in the scheduler.** If a query runs longer than its own
  period, the next run can start while the first one is still working. On a large
  database this can be a problem. A simple "is running" flag or a library like ShedLock
  would solve it.
* **No paging in the query result.** `run` returns every matching row. On a very large
  table this answer can become heavy.
* **The operators `BETWEEN` and `IN` are missing.** The visual editor covers the nine
  operators in the list above.
* **`CONTAINS` is case sensitive.** It uses `LIKE`. `ILIKE` would be case insensitive,
  but it does not exist in every database type.
* **No column to column comparison.** A condition always compares a column with a fixed
  value or with a relative date.
* **No type check before the run.** If you compare a text column with a number, the
  error appears when the SQL runs, not when you save the query.
* **A normal user cannot list all projects.** `GET /api/projects` is for administrators
  only. Normal users see their own projects through `GET /api/projects/my`.
* **The device cookie is not `Secure` yet.** It is set to `false` for local development
  over HTTP. It must become `true` behind HTTPS.
