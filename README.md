# ProDoc

ProDoc adalah aplikasi Android berbasis untuk dokumentasi dan manajemen proyek.

Aplikasi ini dirancang untuk membantu teknisi, engineer, maupun tim proyek dalam mengelola:

* Project
* Sub Project
* Material
* Logic / Configuration
* Diagram
* QA Review
* History Log

dengan fitur sinkronisasi cloud menggunakan Firebase.

---

# Features

## Authentication

* Login Email & Password
* Register Account
* Firebase Authentication
* Sign Out

## Dashboard

* Project Dashboard
* Search Project
* Status Filter
* Dashboard Statistics
* Sync Queue Monitoring

## Project Management

* Main Project
* Sub Project
* Project Detail
* Project Status Tracking

## Material Management

* Create Material
* Edit Material
* Delete Material

## Logic Management

* Create Logic
* Edit Logic
* Delete Logic

## Diagram Management

* Create Diagram
* Edit Diagram
* Delete Diagram

## QA Workflow

* Draft
* Pending QA
* Approved
* Rejected

## History Log

* Activity Tracking
* Audit Trail

## Offline First

* Room Database
* Local Data Storage
* StateFlow
* Repository Pattern

## Cloud Synchronization

* Firebase Firestore
* Sync Queue
* Push Sync
* Pull Sync
* WorkManager Background Sync

---

# Technology Stack

## Language

* Kotlin

## UI

* Jetpack Compose
* Material 3

## Architecture

* MVVM
* Repository Pattern
* Offline First Architecture

## Local Storage

* Room Database

## Cloud

* Firebase Authentication
* Firebase Firestore

## Background Processing

* WorkManager

## Image Loading

* Coil

---

# Architecture

UI

↓

ViewModel

↓

Repository

↓

Room Database

↓

Sync Queue

↓

Firebase Firestore

---

# Project Structure

```text
com.prodoc.app

├── data
│   ├── local
│   │   ├── dao
│   │   ├── entity
│   │   └── database
│   │
│   └── remote
│
├── repository
│
├── ui
│   ├── auth
│   ├── dashboard
│   ├── project
│   ├── material
│   ├── logic
│   ├── diagram
│   ├── qa
│   └── history
│
├── worker
│
└── model
```

# Current Version

## v1.0 Stable

Completed:

* Authentication
* Dashboard
* Project Management
* Material Management
* Logic Management
* Diagram Management
* QA Workflow
* History Log
* Offline First Storage
* Firestore Synchronization
* WorkManager Sync

---

# Roadmap

## v1.0.1

UI & UX Refinement

* Material Detail View
* Logic Detail View
* Diagram Detail View
* Scrollable Form
* Keyboard Handling

## v1.0.2

Sub Project Enhancement

* Open Sub Project
* Edit Sub Project
* Delete Sub Project
* Sub Project Detail
* Dashboard Summary Enhancement

## v1.1

Documentation & File Management

* Firebase Storage
* Image Upload
* PDF Upload
* Attachment Manager
* Pull To Refresh

## v1.2

Reporting & Export

* Export PDF
* Export Excel
* Project Summary Report

---

# Screenshots

Coming Soon

---

# Development Status

Active Development

Current Milestone:

v1.0 Stable Release

Next Milestone:

v1.0.1 UI & UX Refinement

---

# License

This project is developed for educational and portfolio purposes.
