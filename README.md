# ProDoc

ProDoc adalah aplikasi Android berbasis untuk dokumentasi dan manajemen proyek.

Aplikasi mendukung pengelolaan:

Main Project
Sub Project
Material
Logic / Configuration
Diagram
QA Review
Audit History
Cloud Synchronization

dengan penyimpanan database Local First + Cloud Sync menggunakan Room Database dan Firebase.

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
* Sync Queue Counter
* Project Summary

## Project Management

* Main Project
* Sub Project
* Project Detail
* Project Status Tracking

## Sub Project

* Open Sub Project
* Edit Sub Project
* Delete Sub Project
* Recursive Navigation
* Parent Child Relation

## Material Management

* Create Material
* Edit Material
* Delete Material
* Material Detail Screen

## Logic Management

* Create Logic
* Edit Logic
* Delete Logic
* Logic Detail Screen

## Diagram Management

* Create Diagram
* Edit Diagram
* Delete Diagram
* Diagram Detail Screen

## QA Workflow

* Draft
* Pending QA
* Approved
* Rejected

## History

* Automatic History Logging
* Project History
* Material History
* Logic History
* Diagram History
* Sub Project History

## UI & UX Refinement

* HorizontalPager Navigation
* Swipe Navigation
* Dialog Confirmation Barrier
* Scrollable Detail Screen
* State Preservation
* Loading Overlay

## Offline First

* Room Database
* StateFlow
* Repository Pattern
* Sync Queue

## Synchronization

* Firebase Firestore
* Push Sync
* Pull Sync
* WorkManager Sync
* Sync Queue Monitoring

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

## Async & Reactive

* Kotlin Coroutines
* StateFlow

## Background Processing

* WorkManager

## Media

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

Alur:

UI tidak mengakses database secara langsung
UI tidak mengakses Firestore secara langsung
Semua operasi melalui Repository Layer
Semua data disimpan lokal terlebih dahulu sebelum sinkronisasi cloud

---

# Project Structure

```text
com.prodoc.app
├── data
│ ├── local
│ │ ├── dao
│ │ ├── entity
│ │ ├── converters
│ │ └── ProDocDatabase
│ │
│ └── remote
│
├── model
│
├── repository
│
├── worker
│
├── ui
│ ├── auth
│ ├── dashboard
│ ├── project
│ ├── material
│ ├── logic
│ ├── diagram
│ ├── qa
│ └── history
│
└── MainActivity

```

# Current Status

Completed :
## v1.0.0 Stable Release

* Authentication
* Dashboard
* Project Management
* Material Management
* Logic Management
* Diagram Management
* QA Workflow
* Audit History
* Offline First Storage
* Firestore Synchronization

## v1.1.0 Stable Refinement

* Material Detail Screen
* Logic Detail Screen
* Diagram Detail Screen
* HorizontalPager Navigation
* Dialog Barrier
* Scroll Fix
* State Preservation
* Dashboard Summary Enhancement
* Audit Trail Enhancement
* Sub Project Navigation Improvement

---

# Screenshots

Coming Soon

---

## Known Limitations

Saat ini beberapa fitur masih dalam pengembangan:

* Cascade Delete belum diterapkan
* Parent Child Summary belum tersedia
* Firebase Storage belum tersedia
* Upload Attachment belum tersedia
* Export PDF belum tersedia
* Export Excel belum tersedia

---

# Development Status

* MVVM Architecture
* Repository Pattern
* Offline First Architecture
* Room First
* Sync Queue First
* Firestore sebagai sinkronisasi cloud
* Tidak ada akses database langsung dari UI
* Tidak ada akses Firestore langsung dari UI

---

# License

MIT License

Copyright (c) 2026 ProDoc

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files to deal in the Software without restriction.
