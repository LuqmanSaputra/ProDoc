# Changelog

Semua perubahan penting pada proyek **ProDoc** akan didokumentasikan pada file ini.

Format changelog ini mengacu pada prinsip **Keep a Changelog**, dengan penyesuaian terhadap siklus pengembangan ProDoc.

---

# [v1.1.1] - Stable

**Release Date:** June 2026

## Added

### Hierarchy Engine

* Unlimited Nested Project
* Recursive Project Navigation
* Parent Child Relationship
* Hierarchy Validation
* Project Summary Card

### Project Management

* Cascade Delete
* Parent Child Summary
* Recursive History
* QA Audit Trail

### Dashboard

* Project Summary
* Dashboard Summary
* Summary Statistics

### Quality Assurance

* QA History Logging
* Automatic History Recording
* Approval History
* Rejection History

---

## Improved

### Architecture

* Repository Integration
* Hierarchy Service
* Offline First Stability
* StateFlow Synchronization

### UI

* HorizontalPager Stability
* ScrollableTabRow Stability
* State Preservation
* Project Detail Layout

### Performance

* Reduced unnecessary recomposition
* Better State Management
* Improved Recursive Navigation

---

## Fixed

### Navigation

* Fixed HorizontalPager synchronization issue
* Fixed ScrollableTabRow synchronization
* Fixed recursive navigation issue

### History

* Fixed history synchronization
* Fixed missing QA history record

### Stability

* Fixed project hierarchy inconsistency
* Fixed parent-child navigation issue
* Fixed summary calculation issue

---

# [v1.1.0] - Stable

## Added

* Dashboard Statistics
* HorizontalPager
* ScrollableTabRow
* Better Navigation
* State Preservation

---

## Improved

* Dashboard UI
* Navigation Flow
* Compose State Management

---

## Fixed

* Navigation bug
* Scroll state issue

---

# [v1.0.0] - Initial Release

## Added

### Authentication

* Login
* Register
* Logout

### Dashboard

* Dashboard Project
* Search Project
* Status Filter

### Project

* CRUD Project
* Project Category
* Project Status

### Material

* CRUD Material

### Logic

* CRUD Logic

### Diagram

* CRUD Diagram

### QA

* QA Status

### History

* Audit History

### Cloud

* Firebase Authentication
* Firebase Firestore
* WorkManager Synchronization

---

# Upcoming

## v1.2.0

### Planned

* Firebase Storage Integration
* Image Upload
* PDF Upload
* Document Upload
* Attachment Manager
* File Preview
* Background Upload
* Upload Progress

---

## v1.3.0

### Planned

* Export PDF
* Export Excel
* Reporting Engine
* Analytics Dashboard
* Advanced Search
* Advanced Filter

---

## v2.0.0

### Vision

* PostgreSQL Backend
* REST API
* Multi User Collaboration
* Web Platform
* iOS Platform
* Role & Permission
* Real-time Synchronization
* Cloud Storage
* Team Workspace

---

# Version Summary

| Version | Status         | Description                        |
| ------- | -------------- | ---------------------------------- |
| v1.0.0  | ✅ Stable       | Initial Release                    |
| v1.1.0  | ✅ Stable       | UI & Navigation Improvement        |
| v1.1.1  | ✅ Stable       | Hierarchy Engine & Project Summary |
| v1.2.0  | 🚧 Development | Attachment Management              |
| v1.3.0  | 📅 Planned     | Reporting & Productivity           |
| v2.0.0  | 🔮 Vision      | Enterprise Platform                |

---

# Notes

Mulai versi **v1.1.1**, ProDoc menerapkan strategi pengembangan berbasis **Stable Release**, sehingga setiap versi utama akan melalui tahapan:

1. Development
2. Internal Testing
3. Stable Release
4. Documentation Update
5. GitHub Release

Dengan pendekatan ini, setiap versi yang dirilis di branch **main** merupakan versi yang telah melalui proses validasi dan siap digunakan sebagai dasar pengembangan versi berikutnya.
