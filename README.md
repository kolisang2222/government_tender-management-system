# ProcureGov — Government e-Tender Management System

[![Java](https://img.shields.io/badge/Java-EE%208-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Tomcat](https://img.shields.io/badge/Apache%20Tomcat-9.x-yellow?style=flat-square)](https://tomcat.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![JSTL](https://img.shields.io/badge/JSTL-1.2-green?style=flat-square)](https://jstl.dev.java.net/)
[![License](https://img.shields.io/badge/License-Academic-lightgrey?style=flat-square)](#license)

> A full-stack Java EE web application that digitises the complete government procurement lifecycle for the **Ministry of Public Works, Kingdom of Lesotho** — from tender creation and publication through sealed bid submission, weighted evaluation, and formal contract award.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Step 1 — Database Setup](#step-1--database-setup)
  - [Step 2 — Configure Tomcat JNDI](#step-2--configure-tomcat-jndi)
  - [Step 3 — Create Upload Directory](#step-3--create-upload-directory)
  - [Step 4 — Deploy the WAR](#step-4--deploy-the-war)
  - [Step 5 — Log In](#step-5--log-in)
- [User Roles](#user-roles)
- [Tender Lifecycle](#tender-lifecycle)
- [Evaluation Scoring Model](#evaluation-scoring-model)
- [Security Implementation](#security-implementation)
- [Seed Credentials](#seed-credentials)
- [Dependencies](#dependencies)
- [Screenshots](#screenshots)
- [Academic Context](#academic-context)
- [License](#license)

---

## Overview

The Ministry of Public Works of the Kingdom of Lesotho previously managed its procurement processes through a combination of paper-based submissions, physical notice boards, and email correspondence. This resulted in delayed publication of tender notices, misplaced bid documents, lack of transparency in evaluation, and difficulty tracking the lifecycle of each tender.

**ProcureGov** solves these problems by providing a secure, role-controlled web portal that enforces a strict, auditable tender lifecycle. Every tender transition is validated server-side, every bid is sealed and timestamped, every evaluation score is recorded per evaluator, and every contract award is formally justified and notified.

---

## Features

### Module 1 — Authentication & Session Management
- Single login page for all three roles with automatic role-based dashboard redirect
- SHA-256 password hashing — plain text passwords are never stored or transmitted
- Account lockout after 3 consecutive failed login attempts (stored in database, not session)
- CSRF token protection on all forms
- Session invalidation on logout (`session.invalidate()`)
- Role guard on every protected page via `AuthenticationFilter` and `RoleAuthorizationFilter`

### Module 2 — Tender Management (Procurement Officer)
- Create tenders with auto-generated reference numbers in format `MPW-YYYY-NNNN`
- PDF tender notice upload via the Servlet Part API (max 5 MB)
- Secure document download through a dedicated `FileDownloadServlet` — file paths never exposed
- Full tender lifecycle management with enforced status transitions
- Tender editing locked once published to OPEN status
- Award notice generation with winning supplier, awarded value, and justification

### Module 3 — Supplier Bid Submission
- Supplier self-registration with auto-generated registration number (`SUP-YYYY-NNNN`)
- Browse open tenders and download tender notice documents
- Submit sealed bids with amount, technical statement, timeline, and supporting document
- Server-side closing date enforcement using `LocalDateTime` — browser state is irrelevant
- One bid per tender enforced at both application and database level
- POST-Redirect-GET pattern prevents duplicate submissions on page refresh

### Module 4 — Bid Evaluation & Scoring
- Weighted scoring model: Price (40%) + Technical (35%) + Timeline (25%)
- Price and Timeline scores calculated automatically by `ScoringService`
- Blind scoring — evaluators cannot see each other's scores before submitting their own
- Multi-evaluator averaging to produce final bid scores
- Automatic transition to EVALUATED status when all evaluators have scored all bids
- Ranked leaderboard sorted by final score for the Procurement Officer

### Module 5 — Data Persistence Layer
- MySQL database in Third Normal Form (8 tables)
- DAO interface + implementation pattern throughout — no JDBC in Servlets or JSPs
- Tomcat JNDI DataSource connection pool — no `DriverManager.getConnection()`
- Full `schema.sql` with DROP, CREATE, constraints, stored procedures, triggers, and seed data
- `SQLException` caught and logged in every DAO method — stack traces never shown to users

### Module 6 — Email Notifications (Bonus)
- JavaMail API sends award outcome emails to all bidding suppliers when a contract is awarded
- Notifications include tender reference, outcome (Won / Not Won), and a link to the Award Notice
- Database trigger (`after_tender_awarded`) populates in-app notification records automatically

---

## System Architecture

ProcureGov follows the **MVC (Model-View-Controller)** architectural pattern:

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT (Browser)                     │
│              HTTP Requests  ↕  HTTP Responses           │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│              VIEW LAYER  —  JSP Pages                   │
│     officer/  │  supplier/  │  evaluator/  │  shared/   │
│           JSTL only — zero scriptlets                   │
└────────────────────────┬────────────────────────────────┘
                         │ forward / redirect
┌────────────────────────▼────────────────────────────────┐
│           CONTROLLER LAYER  —  Servlets                 │
│  AuthenticationServlet │ TenderCreationServlet          │
│  SubmitBidServlet │ SubmitEvaluationScoreServlet        │
│  AwardTenderServlet │ FileDownloadServlet  │ ...        │
│  ─────────────────────────────────────────────────────  │
│  Filters: AuthenticationFilter │ RoleAuthorizationFilter│
│  Listeners: TenderClosingScheduler │ DBInitializer      │
└────────────────────────┬────────────────────────────────┘
                         │ calls
┌────────────────────────▼────────────────────────────────┐
│         BUSINESS LOGIC LAYER  —  Services               │
│  ScoringService │ TenderLifecycleService                │
│  EvaluationService │ EmailNotificationService           │
│  FileUploadHandler │ ValidationService                  │
└────────────────────────┬────────────────────────────────┘
                         │ calls
┌────────────────────────▼────────────────────────────────┐
│         DATA ACCESS LAYER  —  DAO Pattern               │
│  TenderDAO / TenderDAOImpl   │  BidDAO / BidDAOImpl     │
│  UserDAO / UserDAOImpl  │  EvaluationDAO / EvaluationDAOImpl │
│         JavaBeans: Tender │ Bid │ User │ EvaluationScore│
└────────────────────────┬────────────────────────────────┘
                         │ JNDI connection pool
┌────────────────────────▼────────────────────────────────┐
│          PERSISTENCE LAYER  —  MySQL Database           │
│  users │ tenders │ bids │ evaluation_scores             │
│  tender_evaluators │ documents │ notifications          │
│  audit_logs                                             │
└─────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java EE | 8 | Core platform |
| Apache Tomcat | 9.x | Servlet container |
| MySQL | 8.0 | Relational database |
| JSTL | 1.2 | JSP templating (no scriptlets) |
| JavaMail API | 1.6.2 | Email notifications |
| MySQL Connector/J | 9.4.0 | JDBC driver |
| Gson | 2.8.6 | JSON serialisation |
| Apache Ant | - | Build tool (`build.xml`) |

---

## Project Structure

```
procuregov/
├── src/java/
│   ├── controller/                 # Servlet controllers (MVC — Controller)
│   │   ├── AuthenticationServlet.java
│   │   ├── TenderCreationServlet.java
│   │   ├── PublishTenderServlet.java
│   │   ├── EditTenderServlet.java
│   │   ├── SubmitBidServlet.java
│   │   ├── SubmitEvaluationScoreServlet.java
│   │   ├── AwardTenderServlet.java
│   │   ├── FileDownloadServlet.java
│   │   ├── LogoutServlet.java
│   │   ├── SupplierRegistrationServlet.java
│   │   └── ...38 controllers total
│   ├── dao/
│   │   ├── interfaces/             # DAO contracts
│   │   │   ├── TenderDAO.java
│   │   │   ├── BidDAO.java
│   │   │   ├── UserDAO.java
│   │   │   └── EvaluationDAO.java
│   │   └── impl/                   # JDBC implementations
│   │       ├── TenderDAOImpl.java
│   │       ├── BidDAOImpl.java
│   │       ├── UserDAOImpl.java
│   │       └── EvaluationDAOImpl.java
│   ├── filter/
│   │   ├── AuthenticationFilter.java
│   │   └── RoleAuthorizationFilter.java
│   ├── listener/
│   │   ├── DatabaseInitializerListener.java
│   │   └── TenderClosingScheduler.java
│   ├── model/                      # JavaBeans (MVC — Model)
│   │   ├── Tender.java
│   │   ├── Bid.java
│   │   ├── User.java
│   │   ├── EvaluationScore.java
│   │   ├── BidEvaluationSummary.java
│   │   ├── BidStatistics.java
│   │   ├── TenderStatistics.java
│   │   ├── Supplier.java
│   │   └── enums/
│   │       ├── TenderStatus.java
│   │       ├── TenderCategory.java
│   │       └── UserRole.java
│   ├── service/
│   │   ├── ScoringService.java
│   │   ├── TenderLifecycleService.java
│   │   ├── EvaluationService.java
│   │   ├── EmailNotificationService.java
│   │   ├── FileUploadHandler.java
│   │   ├── SupplierRegistrationService.java
│   │   └── ValidationService.java
│   └── util/
│       ├── SessionValidator.java
│       ├── PasswordHasher.java
│       ├── DatabaseConnectionPool.java
│       ├── CSRFTokenUtil.java
│       ├── DateConverter.java
│       ├── LoggingUtil.java
│       └── ValidationUtils.java
│
├── web/
│   ├── WEB-INF/
│   │   ├── web.xml                 # Servlet mappings, filters, error pages, JNDI ref
│   │   ├── schema.sql              # Complete DB script with seed data
│   │   └── lib/                    # All required JARs
│   ├── META-INF/
│   │   └── context.xml             # Tomcat JNDI DataSource config
│   ├── officer/                    # JSP views — Procurement Officer
│   ├── supplier/                   # JSP views — Supplier
│   ├── evaluator/                  # JSP views — Evaluation Committee
│   ├── shared/                     # Shared fragments (header, nav, footer)
│   ├── errors/                     # Custom error pages (400, 403, 404, 500)
│   ├── css/                        # Stylesheets (one per view)
│   └── images/                     # Ministry branding assets
│
├── build.xml                       # Apache Ant build file
└── README.md
```

---

## Getting Started

### Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 8 or later |
| Apache Tomcat | 9.x |
| MySQL Server | 8.0 or later |
| Any modern browser | Chrome, Firefox, Edge |

---

### Step 1 — Database Setup

Open MySQL Workbench or your MySQL CLI and run:

```sql
-- Create the database
CREATE DATABASE kolisangphatela2334120
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Select and run the schema script
USE kolisangphatela2334120;
SOURCE /path/to/procuregov/web/WEB-INF/schema.sql;
```

The script will:
- Drop and recreate all 8 tables with correct constraints
- Insert seed data (2 Officers, 2 Committee Members, 4 Suppliers, 2 Tenders, 6 Bids)
- Create stored procedures (`CalculateBidRankings`, `CloseExpiredTenders`)
- Create a trigger (`after_tender_awarded`) for automatic notifications

---

### Step 2 — Configure Tomcat JNDI

Edit the `META-INF/context.xml` already included in the project and update your MySQL credentials:

```xml
<Resource
  name="jdbc/kolisangphatela2334120"
  auth="Container"
  type="javax.sql.DataSource"
  driverClassName="com.mysql.cj.jdbc.Driver"
  url="jdbc:mysql://localhost:3306/kolisangphatela2334120?useSSL=false&amp;serverTimezone=UTC&amp;allowPublicKeyRetrieval=true"
  username="root"
  password="YOUR_MYSQL_PASSWORD"
  maxTotal="20"
  maxIdle="10"
  maxWaitMillis="10000"
  validationQuery="SELECT 1"
  testOnBorrow="true"
/>
```

> The JNDI resource name `jdbc/kolisangphatela2334120` must match exactly — it is referenced in both `web.xml` and `DatabaseConnectionPool.java`.

---

### Step 3 — Create Upload Directory

The application stores uploaded files outside the WAR on the server filesystem.

**Linux / macOS:**
```bash
sudo mkdir -p /var/procuregov/uploads/tender-notices
sudo mkdir -p /var/procuregov/uploads/bid-documents
sudo chown -R tomcat:tomcat /var/procuregov/uploads
```

**Windows (run as Administrator):**
```cmd
mkdir C:\procuregov\uploads\tender-notices
mkdir C:\procuregov\uploads\bid-documents
```

To use a custom path, update `uploadDirectory` in `WEB-INF/web.xml`:
```xml
<context-param>
  <param-name>uploadDirectory</param-name>
  <param-value>/your/custom/path</param-value>
</context-param>
```

---

### Step 4 — Deploy the WAR

**Option A — Deploy compiled WAR:**
```bash
cp kolisangphatela2334120.war $TOMCAT_HOME/webapps/
$TOMCAT_HOME/bin/startup.sh      # Linux/macOS
$TOMCAT_HOME\bin\startup.bat     # Windows
```

**Option B — Build from source with Apache Ant:**
```bash
cd procuregov
ant clean build
# WAR is output to build/
cp build/kolisangphatela2334120.war $TOMCAT_HOME/webapps/
```

Then open your browser at:
```
http://localhost:8080/kolisangphatela2334120/
```

---

### Step 5 — Log In

All three roles share a single login page at `/login`.

See [Seed Credentials](#seed-credentials) below for pre-seeded accounts.

---

## User Roles

| Role | Access | Dashboard |
|---|---|---|
| **Procurement Officer** | Create tenders, manage lifecycle, assign evaluators, score bids, award contracts, generate reports | `/officer/dashboard` |
| **Evaluation Committee Member** | Score bids on closed tenders, view consolidated results | `/evaluator/dashboard` |
| **Supplier** | Register, browse open tenders, submit bids, track bid status, view award outcomes | `/supplier/dashboard` |

Staff accounts are created via the database seed script only — there is no self-registration for staff. Suppliers register through the public registration page.

---

## Tender Lifecycle

```
DRAFT ──► OPEN ──► CLOSED ──► UNDER_EVALUATION ──► EVALUATED ──► AWARDED
```

| Transition | Triggered By | Mechanism |
|---|---|---|
| DRAFT → OPEN | Procurement Officer | `PublishTenderServlet` → `TenderLifecycleService.publishTender()` |
| OPEN → CLOSED | **Automatic** | `TenderClosingScheduler` (every 5 min) + `SubmitBidServlet` (on bid attempt) |
| CLOSED → UNDER_EVALUATION | Procurement Officer | `OfficerStartEvaluationServlet` → `TenderLifecycleService.startEvaluation()` |
| UNDER_EVALUATION → EVALUATED | **Automatic** | `SubmitEvaluationScoreServlet` checks after every score submission |
| EVALUATED → AWARDED | Procurement Officer | `AwardTenderServlet` → `TenderLifecycleService.awardTender()` |

Status transitions are enforced server-side in `TenderLifecycleService` — no stage can be skipped or reversed.

---

## Evaluation Scoring Model

Each bid is scored across three weighted criteria:

| Criterion | Formula | Entered By | Weight |
|---|---|---|---|
| Price Score | `(Lowest Bid ÷ This Bid) × 100` | Calculated automatically | **40%** |
| Technical Compliance | Score 0–100 | Evaluator (manual entry) | **35%** |
| Delivery Timeline Score | `(Shortest Timeline ÷ This Timeline) × 100` | Calculated automatically | **25%** |

**Weighted Total** = `(Price × 0.40) + (Technical × 0.35) + (Timeline × 0.25)`

When multiple evaluators score the same bid, their weighted totals are averaged to produce a **Final Score**. Evaluators cannot see each other's scores until they have submitted their own (blind scoring).

All calculations are performed in `ScoringService.java` — no calculation logic in Servlets or JSPs.

---

## Security Implementation

| Feature | Implementation |
|---|---|
| Password hashing | SHA-256 via `PasswordHasher.java` (`MessageDigest`) |
| Account lockout | 3 failed attempts locks account in database (`failed_attempts`, `locked_until`) |
| Session management | `HttpSession` with `session.invalidate()` on logout |
| CSRF protection | Token generated per session, validated on every POST (`CSRFTokenUtil.java`) |
| SQL injection prevention | `PreparedStatement` with parameterised queries throughout all DAOs |
| Access control | `AuthenticationFilter` (authentication) + `RoleAuthorizationFilter` (authorisation) |
| Secure file serving | Files served through `FileDownloadServlet` — paths never exposed to browser |
| Error handling | Custom 400/403/404/500 pages configured in `web.xml` — no raw Tomcat errors |

---

## Seed Credentials

### Staff Accounts (pre-seeded — no self-registration)

| Role | Email | Password |
|---|---|---|
| Procurement Officer | `kolisang.phatela@mpw.gov.ls` | `Admin@1234` |
| Procurement Officer | `thabo.nkosi@mpw.gov.ls` | `Admin@1234` |
| Evaluation Committee | `seboko.faso@mpw.gov.ls` | `Admin@1234` |
| Evaluation Committee | `lineo.ramaema@mpw.gov.ls` | `Admin@1234` |

### Supplier Accounts (pre-seeded — suppliers can also self-register)

| Company | Email | Password |
|---|---|---|
| Maellen Finances (Pty) Ltd | `maellen.finances@gmail.com` | `Supplier@1234` |
| Phatela Infrastructure Solutions | `phatela.infrastructure@gmail.com` | `Supplier@1234` |
| Thulo Construction and Civil Works | `thulo.construction@gmail.com` | `Supplier@1234` |
| Parts Computer Systems | `parts.computer@gmail.com` | `Supplier@1234` |

> All passwords are stored as SHA-256 hashes. The plain text above is for setup purposes only.

---

## Dependencies

All JAR files are included in `web/WEB-INF/lib/` — no external package manager required.

| JAR | Version | Purpose |
|---|---|---|
| `mysql-connector-j` | 9.4.0 | MySQL JDBC driver |
| `jstl` | 1.2 | JSP Standard Tag Library |
| `javax.mail` | 1.6.2 | JavaMail API for email notifications |
| `activation` | 1.1.1 | JavaBeans Activation Framework (JavaMail dependency) |
| `gson` | 2.8.6 | JSON serialisation utility |

---

## Screenshots

> Add screenshots of your application here once it is deployed.

```
screenshots/
├── landing-page.png
├── officer-dashboard.png
├── create-tender.png
├── supplier-dashboard.png
├── bid-submission.png
├── evaluation-panel.png
├── score-form.png
├── award-notice.png
└── email-notification.png
```

To add screenshots, create a `screenshots/` folder in the repository root, add your images, and update the paths above with:
```markdown
![Officer Dashboard](screenshots/officer-dashboard.png)
```

---

## Troubleshooting

| Problem | Solution |
|---|---|
| `NameNotFoundException: jdbc/kolisangphatela2334120` | Ensure `context.xml` is in `META-INF/` at WAR root. Check `resource-ref` in `web.xml` matches JNDI name exactly. |
| `Access Denied` on login | Verify email and password. Check `failed_attempts` column in `users` table — may need resetting. |
| File upload fails silently | Ensure `/var/procuregov/uploads` exists and Tomcat process has write permission. |
| Tenders not auto-closing | `TenderClosingScheduler` runs every 5 minutes. You can also trigger a close by attempting a bid on an expired tender. |
| Emails not sending | Update `SENDER_EMAIL` and `SENDER_PASSWORD` in `EmailNotificationService.java` with a valid Gmail App Password. |
| 404 on all pages | Confirm WAR deployed correctly and context path matches URL. Check Tomcat logs at `$TOMCAT_HOME/logs/catalina.out`. |

---

## Academic Context

| Field | Value |
|---|---|
| Student | Kolisang Phatela |
| Student Number | 2334120 |
| Module | Advanced Java — C7-ADJ-11 |
| Assessment | End Assessment — CADJ/A26E-1 |
| Faculty | Faculty of Engineering and Technology |
| Institution | Jan –Jun 2026 Semester |

---

## License

This project was developed as an academic assessment submission. It is shared publicly for portfolio and educational purposes.

You are welcome to read the code, learn from it, and reference it. Please do not submit it as your own academic work.

---

<div align="center">
  <sub>Built with Java EE · Deployed on Apache Tomcat 9 · Powered by MySQL 8</sub>
  <br/>
  <sub>Ministry of Public Works · Kingdom of Lesotho</sub>
</div>
