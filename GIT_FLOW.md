# Git Flow & Branch Strategy

> Official Git Workflow for ProDoc Development

---

# Overview

ProDoc menggunakan strategi **Git Flow sederhana** yang dirancang untuk pengembangan oleh satu developer (single developer workflow).

Branch dipisahkan menjadi dua kategori utama agar proses pengembangan tetap terstruktur dan setiap versi stabil dapat dilacak dengan mudah.

---

# Branch Strategy

## main

Branch **main** hanya berisi source code yang telah dinyatakan **Stable Release**.

Semua commit pada branch ini harus:

* Sudah melalui pengujian
* Tidak memiliki bug kritis
* Siap digunakan
* Siap diberi Git Tag

Contoh:

```text
main

v1.0 Stable

↓

v1.1.0 Stable

↓

v1.1.1 Stable
```

---

## development

Branch **development** digunakan untuk seluruh proses pengembangan.

Seluruh Development Phase dilakukan pada branch ini.

Contoh:

```text
development

Implementasi Sprint 1

Implementasi Sprint 2

Implementasi Sprint 3

↓

Testing

↓

Release Candidate
```

Setelah versi stabil dirilis:

* branch development di-reset dari branch main
* kemudian digunakan kembali untuk pengembangan versi berikutnya

---

# Release Workflow

```text
development

↓

Feature Development

↓

Internal Testing

↓

Bug Fix

↓

Commit

↓

Push

↓

Merge → main

↓

Git Tag

↓

GitHub Release

↓

Reset development

↓

Start Next Version
```

---

# Versioning Strategy

ProDoc menggunakan Semantic Versioning sederhana.

Format:

```text
Major.Minor.Patch
```

Contoh:

| Version | Description         |
| ------- | ------------------- |
| 1.0     | Initial Release     |
| 1.1.0   | Feature Improvement |
| 1.1.1   | Stable Refinement   |
| 1.2.0   | New Feature         |
| 2.0.0   | Major Architecture  |

---

# Commit Convention

Gunakan format commit berikut.

## Feature

```text
feat(project): add cascade delete
```

---

## Fix

```text
fix(history): resolve QA history synchronization
```

---

## Refactor

```text
refactor(repository): simplify hierarchy calculation
```

---

## Documentation

```text
docs: update roadmap and changelog
```

---

## Release

```text
release(v1.1.1): stable release
```

---

# Release Checklist

Sebelum melakukan merge ke branch **main**, pastikan:

* Semua fitur roadmap selesai
* Tidak ada error
* Tidak ada warning penting
* Aplikasi berhasil di-build
* Pengujian manual selesai
* README diperbarui
* ROADMAP diperbarui
* CHANGELOG diperbarui

---

# Git Tag Convention

Gunakan Git Tag untuk setiap Stable Release.

Contoh:

```text
v1.0

v1.1.0

v1.1.1

v1.2.0
```

Tag dibuat setelah merge ke branch **main** berhasil.

---

# GitHub Release

Setiap Git Tag harus memiliki GitHub Release.

Isi Release Notes meliputi:

* Highlights
* Added
* Improved
* Fixed
* Known Limitations
* Next Development Target

---

# Current Workflow

Saat ini ProDoc menggunakan alur berikut.

```text
development
        │
        ▼
Implementasi Sprint
        │
        ▼
Testing Internal
        │
        ▼
Bug Fix
        │
        ▼
Commit
        │
        ▼
Push → development
        │
        ▼
Merge → main
        │
        ▼
Git Tag
        │
        ▼
GitHub Release
        │
        ▼
Update Documentation
        │
        ▼
Start Next Version
```

---

# Documentation Strategy

Setiap Stable Release wajib memperbarui dokumen berikut:

* README.md
* ROADMAP.md
* CHANGELOG.md
* Release Notes

Dengan strategi ini seluruh perkembangan ProDoc terdokumentasi secara konsisten dan mudah ditelusuri.

---

# Development Philosophy

ProDoc dikembangkan dengan prinsip:

* Stable First
* Incremental Development
* Offline First
* Single Source of Truth
* Clean Architecture
* Maintainable Code
* Documentation Driven Development

Setiap fitur baru harus tetap mempertahankan arsitektur MVVM, Repository Pattern, serta prinsip Offline First yang telah menjadi fondasi utama aplikasi.
