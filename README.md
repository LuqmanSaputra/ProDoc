# ProDoc

> **Professional Android Project Documentation & Management Application**

![Version](https://img.shields.io/badge/version-v1.1.1-blue)
![Status](https://img.shields.io/badge/status-stable-brightgreen)
![Platform](https://img.shields.io/badge/platform-Android-green)
![Language](https://img.shields.io/badge/Kotlin-100%25-purple)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange)
![Database](https://img.shields.io/badge/Database-Room-blue)
![Cloud](https://img.shields.io/badge/Cloud-Firebase-yellow)

---

# 📖 Overview

**ProDoc** adalah aplikasi Android yang dirancang untuk membantu proses dokumentasi, pengelolaan, serta pengawasan proyek teknis secara terstruktur.

Aplikasi menerapkan konsep **Offline First Architecture**, sehingga seluruh data disimpan terlebih dahulu pada database lokal menggunakan **Room Database**, kemudian disinkronkan ke cloud menggunakan **Firebase Firestore** melalui **WorkManager**.

ProDoc mendukung pengelolaan proyek secara bertingkat (hierarchical project management), dokumentasi material, konfigurasi logika, diagram teknis, proses Quality Assurance (QA), audit history, serta sinkronisasi cloud.

---

# ✨ Key Features

## Authentication

* Login
* Register
* Firebase Authentication
* Logout

---

## Dashboard

* Project Dashboard
* Search Project
* Status Filter
* Dashboard Statistics
* Sync Queue Counter
* Project Summary

---

## Project Management

* Main Project
* Unlimited Nested Sub Project
* Recursive Project Navigation
* Project Detail
* Category Management
* Project Status

---

## Material Management

* Create Material
* Edit Material
* Delete Material
* Material Detail
* QA Status

---

## Logic Management

* Create Logic
* Edit Logic
* Delete Logic
* Logic Detail
* QA Status

---

## Diagram Management

* Create Diagram
* Edit Diagram
* Delete Diagram
* Diagram Detail
* QA Status

---

## QA Workflow

* Draft
* Pending Review
* Approved
* Rejected
* QA Audit Trail

---

## History & Audit Trail

* Automatic History Logging
* Project History
* Material History
* Logic History
* Diagram History
* QA Approval History
* QA Rejection History
* Recursive History

---

## Hierarchy Engine (v1.1.1)

* Unlimited Nested Project
* Recursive Navigation
* Cascade Delete
* Parent Child Summary
* Hierarchy Validation
* Project Summary Card

---

## Offline First

* Room Database
* Repository Pattern
* StateFlow
* Sync Queue
* Local First Storage

---

## Cloud Synchronization

* Firebase Firestore
* Push Synchronization
* Pull Synchronization
* WorkManager
* Sync Queue Monitoring

---

# 🏗 Architecture

ProDoc menerapkan arsitektur modern Android.

```text
UI (Jetpack Compose)
        │
        ▼
ViewModel
        │
        ▼
Repository
        │
        ▼
Room Database
        │
        ▼
Sync Queue
        │
        ▼
Firebase Firestore
```

Prinsip utama:

* UI tidak mengakses Room secara langsung.
* UI tidak mengakses Firebase secara langsung.
* Seluruh operasi dilakukan melalui Repository.
* Seluruh perubahan data diproses secara Offline First.

---

# 🛠 Technology Stack

## Language

* Kotlin

## UI

* Jetpack Compose
* Material Design 3

## Architecture

* MVVM
* Repository Pattern
* Offline First Architecture

## Local Database

* Room Database

## Cloud

* Firebase Authentication
* Firebase Firestore

## Background Processing

* WorkManager

## Reactive Programming

* Kotlin Coroutines
* StateFlow

## Image Loading

* Coil

---

# 📂 Project Structure

```text
com.prodoc.app
├── data
│   ├── local
│   │   ├── dao
│   │   ├── entity
│   │   ├── converters
│   │   └── ProDocDatabase
│   └── remote
├── domain
│   └── hierarchy
├── repository
├── ui
│   ├── auth
│   ├── dashboard
│   ├── project
│   ├── material
│   ├── logic
│   ├── diagram
│   ├── history
│   └── qa
├── worker
└── MainActivity
```

---

# 🚀 Current Release

## v1.1.1 Stable

### Added

* Hierarchy Engine
* Unlimited Nested Project
* Recursive Navigation
* Cascade Delete
* Parent Child Summary
* Project Summary Card
* QA Audit Trail

### Improved

* HorizontalPager Stability
* ScrollableTabRow Stability
* Dashboard Summary
* State Preservation
* Audit Trail
* Repository Integration

### Fixed

* HorizontalPager State Issue
* Navigation State Issue
* Scroll Issue
* History Logging
* QA History Synchronization

---

# 📌 Current Status

✅ Stable Release

Completed:

* Authentication
* Dashboard
* Project Management
* Material Management
* Logic Management
* Diagram Management
* Hierarchy Engine
* Cascade Delete
* Parent Child Summary
* QA Workflow
* Audit History
* Offline First Architecture
* Firestore Synchronization

---

# 🗺 Roadmap

| Version | Status         |
| ------- | -------------- |
| v1.0.0  | ✅ Stable       |
| v1.1.0  | ✅ Stable       |
| v1.1.1  | ✅ Stable       |
| v1.2.0  | 🚧 Development |
| v1.3.0  | 📅 Planned     |
| v2.0.0  | 🔮 Vision      |

---

# ⚠ Known Limitations

Fitur berikut masih dalam tahap pengembangan:

* Firebase Storage
* Image Upload
* PDF Upload
* Attachment Manager
* Export PDF
* Export Excel
* Reporting Engine
* Advanced Search
* Advanced Filter

---

# 📸 Screenshots

Coming Soon

---

# 📄 License

MIT License

Copyright (c) 2026 ProDoc

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software.
