# � Skypay Hotel Reservation System

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen?style=flat-square&logo=spring)
![Tests](https://img.shields.io/badge/Tests-53%20passed-success?style=flat-square)
![Build](https://img.shields.io/badge/Build-Success-success?style=flat-square)

> Système de réservation d'hôtel moderne développé pour le test technique Skypay

---

## 📋 Description

Application Java 21 de gestion de réservations hôtelières avec architecture **service orchestration**, exceptions personnalisées et stockage en mémoire. Démontre les bonnes pratiques de développement et l'utilisation des features Java 21.

### ✨ Fonctionnalités

- ✅ **Gestion des chambres** - 3 types (Standard, Junior, Suite)
- ✅ **Gestion des utilisateurs** - Soldes et comptes
- ✅ **Système de réservation** - Dates, disponibilité, calcul coûts
- ✅ **Validation complète** - Dates, soldes, disponibilité
- ✅ **Exceptions personnalisées** - Messages clairs et structurés
- ✅ **Thread-safe** - Production ready avec CopyOnWriteArrayList
- ✅ **Tests complets** - 53 tests unitaires (100% pass)

---

## 🚀 Quick Start

### Prérequis

```bash
Java 21+
Maven 3.9+
```

### Installation & Exécution

```bash
# Compiler
./mvnw clean install

# Lancer les tests
./mvnw test

# Exécuter l'application
./mvnw spring-boot:run
```

### Résultat Attendu

```
Tests run: 53, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS ✅
```

---

## 🏗️ Architecture

### Pattern Service Orchestration

```
HotelService (Orchestrateur)
    ├── RoomService      → Gestion des chambres
    ├── UserService      → Gestion des utilisateurs  
    └── BookingService   → Logique de réservation
```

### Entities

```java
User        → id, balance (avec audit)
Room        → id, type, pricePerNight (avec audit)
Booking     → id, userId, roomNumber, dates, totalCost (snapshot)
```

**Particularité** : Les bookings utilisent un **snapshot immutable** des prix pour éviter les modifications rétroactives.

---

## 🎯 Exceptions Personnalisées

### Hiérarchie (Sealed Classes - Java 21)

```java
BookingException
├── EntityNotFoundException          → Entité introuvable
├── InsufficientBalanceException     → Solde insuffisant
├── InvalidDateException             → Dates invalides
└── RoomNotAvailableException        → Chambre indisponible
```

### Exemples d'Utilisation

```java
// User introuvable
throw new EntityNotFoundException("User", 999);
// → "User avec l'ID 999 introuvable"

// Solde insuffisant
throw new InsufficientBalanceException(5000, 3000);
// → "Solde insuffisant. Requis: 5000, Disponible: 3000"

// Dates invalides
throw new InvalidDateException(checkIn, checkOut, "La date de check-out doit être après check-in");
// → "La date de check-out doit être après check-in (Check-in: 2025-12-10, Check-out: 2025-12-05)"

// Chambre indisponible
throw new RoomNotAvailableException(101, checkIn, checkOut);
// → "La chambre 101 n'est pas disponible du 2025-12-20 au 2025-12-25"
```

---

## 📦 Technologies

| Stack | Version | Usage |
|-------|---------|-------|
| Java | 21 | Records, Sealed Classes, Pattern Matching |
| Spring Boot | 3.5.8 | Framework principal |
| Lombok | 1.18.x | Réduction boilerplate |
| JUnit 5 | 5.10.x | Tests unitaires |
| Mockito | 5.14.x | Mocking |
| AssertJ | 3.24.x | Assertions fluides |
| Maven | 3.9+ | Build automation |

---

## 🧪 Tests

### Structure des Tests

```
53 tests répartis sur 4 classes :
├── HotelServiceImplTest       → 9 tests  (orchestration)
├── UserServiceImplTest        → 17 tests (gestion users)
├── RoomServiceImplTest        → 10 tests (gestion rooms)
└── BookingServiceImplTest     → 17 tests (logique booking)
```

### Scénarios Testés

- ✅ CRUD operations (Create, Read, Update)
- ✅ Validation des données (IDs, prices, dates)
- ✅ Exceptions personnalisées
- ✅ Calcul des coûts et nuitées
- ✅ Détection de chevauchement de réservations
- ✅ Snapshot immutable des prix
- ✅ Thread safety

### Lancer les Tests

```bash
# Tous les tests
./mvnw test

# Tests spécifiques
./mvnw test -Dtest=HotelServiceImplTest
./mvnw test -Dtest=BookingServiceImplTest#shouldDetectOverlappingBookings

# Avec mode quiet
./mvnw test -q
```

---

## 📊 Exemple d'Utilisation

```java
// Initialiser le service
HotelService hotelService = new HotelServiceImpl();

// Créer une chambre
hotelService.setRoom(Room.builder()
    .id(1)
    .type(RoomType.STANDARD)
    .roomPricePerNight(1000)
    .build());

// Créer un utilisateur
hotelService.setUser(User.builder()
    .id(1)
    .balance(5000)
    .build());

// Réserver une chambre
BookingRequest request = BookingRequest.builder()
    .userId(1)
    .roomNumber(1)
    .checkIn(LocalDate.of(2026, 7, 7))
    .checkOut(LocalDate.of(2026, 7, 9))
    .build();

hotelService.bookRoom(request);
// → Réservation créée, solde déduit automatiquement

// Afficher tout
hotelService.printAll();
```

---

## 🛠️ Commandes Utiles

```bash
# Build complet
./mvnw clean install

# Compilation seule
./mvnw compile

# Tests avec rapport
./mvnw test

# Package JAR
./mvnw package

# Vérifier dépendances obsolètes
./mvnw versions:display-dependency-updates

# Clean + Test
./mvnw clean test
```

---

## 📁 Structure du Projet

```
src/
├── main/java/com/skypay/
│   ├── SkypayApplication.java
│   └── hotel/
│       ├── dto/              → BookingRequest, BookingResponse
│       ├── entity/           → User, Room, Booking
│       │   └── domain/       → AbstractAuditable (base avec audit)
│       ├── exception/        → Exceptions personnalisées (sealed)
│       ├── model/            → BookingCreationData (record)
│       └── service/
│           ├── HotelService.java
│           └── impl/         → Implémentations
└── test/java/com/skypay/hotel/service/
    └── impl/                 → 53 tests unitaires
```

---

## ✨ Points Forts Techniques

### Java 21 Features

- **Records** : `BookingCreationData` pour données immutables
- **Sealed Classes** : Hiérarchie d'exceptions fermée
- **Pattern Matching** : Ready pour évolutions futures
- **Text Blocks** : Documentation claire

### Design Patterns

- **Service Orchestration** : Séparation claire des responsabilités
- **Data Snapshot** : Immutabilité des prix dans bookings
- **Optional Pattern** : Gestion élégante des absences
- **Builder Pattern** : Construction fluide des objets

### Thread Safety

- `CopyOnWriteArrayList` pour collections concurrentes
- `AtomicInteger` pour génération d'IDs thread-safe
- Pas de state partagé mutable

### Clean Code

- Nommage explicite
- Méthodes courtes et focalisées
- DRY (Don't Repeat Yourself)
- Logging structuré (SLF4J)

---

## 📄 Licence

Projet éducatif - Test Technique Skypay