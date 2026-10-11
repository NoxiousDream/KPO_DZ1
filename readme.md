# ВышКат — учёт парка сервиса кикшеринга ВШЭ

Консольное приложение для учёта парка сервиса кикшеринга «ВышКат» (ДЗ №1 по КПО, ФКН ВШЭ).
Реализует приём техники и инвентарных вещей на баланс с обязательным техосмотром, отчётами и демонстрацией сценариев.

---

## Возможности

- Приём нового транспортного средства или вещи в парк только после успешного техосмотра.
- Валидация инвентарного номера в формате `XX-NNNN` (2–10 заглавных латинских букв, дефис, 3–6 цифр).
- Подсчёт общего количества единиц на балансе.
- Подсчёт суммарного суточного энергопотребления парка (только для потребителей энергии).
- Вывод списка прокатной техники, подходящей новичкам (простота ≥ 6).
- Вывод всех объектов на балансе с именами и инвентарными номерами.
- Консольная демонстрация всех сценариев (приём, отказ, отчёты).

---

## Технологии

- Java 21
- Gradle 9.6.0 (Groovy DSL, Gradle Wrapper)
- Google Guice 7.0.0 (DI-контейнер)
- JUnit 5.11.4 (BOM + Jupiter + Platform Launcher)

---

## Запуск

### Требования

- JDK 21 (проект собирается и более новыми версиями, минимально Java 17)
- Gradle — через Wrapper, системная установка не требуется

### Сборка и запуск приложения

```bash
./gradlew run
```

На Windows:

```bat
gradlew.bat run
```

Либо через IntelliJ IDEA: открыть класс `ru.hse.vyshkat.Main` и нажать «Run 'Main'».

### Запуск тестов

```bash
./gradlew test
```

Или через IDEA: ПКМ по `src/test/java` → «Run 'All Tests'».

Отчёт о тестах лежит в `build/reports/tests/test/index.html`.

---

## Архитектура

### Пакетная структура

```
ru.hse.vyshkat
├── Main                     — точка входа, собирает DI-контейнер
├── di
│   └── ProductionModule     — Guice-модуль продакшен-сборки
├── domain                   — доменная модель
│   ├── InventoryItem        — абстракция инвентаризируемого объекта
│   ├── EnergyConsumer       — абстракция потребителя энергии
│   ├── BeginnerFriendly     — абстракция устройства с оценкой «простоты»
│   ├── Asset                — базовый класс для объектов на балансе
│   ├── transport            — иерархия транспорта
│   │   ├── Transport
│   │   ├── KickScooter, Bicycle, ElectricBicycle
│   └── thing                — иерархия инвентарных вещей
│       ├── Thing
│       ├── Helmet, ChargingStation
├── service                  — прикладные сервисы
│   ├── FleetService         — приём объектов в парк (через техосмотр)
│   ├── FleetReportService   — построение отчётов
│   ├── InspectionService / StandardInspectionService
│   ├── FleetRepository / InMemoryFleetRepository
│   └── InspectionResult
└── ui
    └── ConsoleDemoRunner    — демонстрация сценариев в консоли
```

### Ключевые абстракции

- **`InventoryItem`** — объект, который можно поставить на баланс (имеет имя и инвентарный номер). Реализуется всеми сущностями на балансе: транспортом и вещами.
- **`EnergyConsumer`** — объект, потребляющий энергию. Реализуется `KickScooter`, `ElectricBicycle`, `ChargingStation`. Не реализуется `Bicycle` и `Helmet` — им эта характеристика не нужна.
- **`BeginnerFriendly`** — объект с оценкой простоты (1–10) и дефолтным методом `isSuitableForBeginners()`. Реализуется только прокатной техникой (`Transport`).
- **`Asset`** — абстрактный базовый класс, реализующий `InventoryItem`. От него наследуются `Transport` и `Thing`.

### Правила техосмотра (`StandardInspectionService`)

1. Объект не может быть `null`.
2. Инвентарный номер не может быть пустым.
3. Формат инвентарного номера: `^[A-Z]{2,10}-\d{3,6}$` (примеры: `SC-1001`, `HM-2001`).
4. Суточное энергопотребление (`EnergyConsumer`) не может быть отрицательным.
5. Оценка простоты для новичка (`BeginnerFriendly`) должна быть в диапазоне 1..10.

Объект попадает в парк только если техосмотр вернул `accept()` и репозиторий подтвердил уникальность инвентарного номера.

---

## SOLID

### 1. Single Responsibility Principle (SRP)

Каждый класс имеет ровно одну ответственность:

- `FleetService` — только приём объектов в парк с вызовом техосмотра.
- `FleetReportService` — только построение отчётов.
- `StandardInspectionService` — только правила техосмотра.
- `InMemoryFleetRepository` — только хранение объектов и контроль уникальности инвентарного номера.
- `ConsoleDemoRunner` — только вывод в консоль.
- `Asset`, `Transport`, `Thing` — только представление доменной сущности.

Ни один из классов не отвечает сразу и за хранение, и за валидацию, и за отчёты.

### 2. Open/Closed Principle (OCP)

Система открыта для расширения: чтобы добавить новый вид транспорта (например, `Segway` или `Unicycle`), достаточно создать новый класс, унаследованный от `Transport` и при необходимости реализующий `EnergyConsumer`:

```java
public class Segway extends Transport implements EnergyConsumer { ... }
```

Ни `FleetService`, ни `FleetReportService`, ни `InMemoryFleetRepository` **не требуют изменений** — они работают через абстракции `InventoryItem`, `EnergyConsumer`, `BeginnerFriendly`.

### 3. Liskov Substitution Principle (LSP)

Любой наследник `Transport` или `Thing` может быть передан в `FleetService.acceptToPark(InventoryItem)` и в `FleetReportService`, не ломая поведение программы. Базовые классы (`Asset`, `Transport`) не вводят неожиданных контрактов, которых не может выполнить наследник — проверки параметров (диапазон простоты, неотрицательность энергии) выполняются в конструкторах наследников.

### 4. Interface Segregation Principle (ISP)

Вместо одного «большого» интерфейса `Device` с кучей методов введены три узкоспециализированных:

- `InventoryItem` — имя и инвентарный номер;
- `EnergyConsumer` — только энергопотребление;
- `BeginnerFriendly` — только оценка простоты.

`Bicycle` и `Helmet` **не реализуют** `EnergyConsumer`, потому что им не нужно энергопотребление. Клиенты, которым нужна только инвентаризация, зависят только от `InventoryItem`.

### 5. Dependency Inversion Principle (DIP)

Высокоуровневые модули (`FleetService`, `FleetReportService`) зависят от абстракций (`FleetRepository`, `InspectionService`), а не от конкретных реализаций. Конкретные реализации (`InMemoryFleetRepository`, `StandardInspectionService`) подставляются через Guice в `ProductionModule`. Это позволяет в тестах легко заменить `InspectionService` на заглушку без правки тестируемого класса.

---

## Обязательные вопросы

### Какой принцип SOLID применён наиболее явно и где?

Наиболее явно применён **Dependency Inversion Principle**.

Где это видно:

- `FleetService` принимает в конструктор `FleetRepository` и `InspectionService` (интерфейсы), а не `InMemoryFleetRepository` / `StandardInspectionService`.
- `FleetReportService` зависит только от `FleetRepository`.
- `Main` собирает граф зависимостей через Guice (`ProductionModule`), не создавая зависимости вручную.

В тестах это проявляется в классе `FleetServiceDiTest`: через тестовый Guice-модуль `InspectionService` подменяется на `RejectAllInspectionStub`, и `FleetService` отказывает в приёме без каких-либо правок в своём коде.

Вторым по явности идёт **Open/Closed Principle** — добавление `Unicycle`/`Segway` не требует правки существующих классов.

### Какой принцип сознательно ограничен / «почти нарушен» и почему это приемлемо?

Сознательно ограничен «чистый» полиморфизм в `FleetReportService` — там используются проверки `instanceof`:

```java
repository.findAll().stream()
    .filter(EnergyConsumer.class::isInstance)
    .map(EnergyConsumer.class::cast)
    ...
```

Альтернативой было бы заставить **все** объекты на балансе реализовывать `EnergyConsumer` (с дефолтным `getDailyEnergyConsumptionKwh() = 0`) и `BeginnerFriendly`. Но это нарушило бы **ISP**: `Helmet` получил бы неиспользуемый метод энергопотребления, а `ChargingStation` — фиктивную оценку «простоты для новичка», которая не имеет смысла для стационарного оборудования.

Почему это приемлемо:

- модель остаётся семантически честной (шлем — не потребитель энергии, станция — не прокатное устройство);
- нет overengineering ради «идеального» полиморфизма;
- при необходимости расширения (например, добавить Visitor для отчётов) `instanceof`-фильтры можно заменить на паттерн, не переделывая домен.

### Что сломается (или не сломается) при добавлении нового вида транспорта, сотрудника или склада запчастей?

#### Новый вид транспорта (например, `Segway`)

**Не сломается ничего.**

Достаточно создать новый класс `Segway extends Transport` (и, при необходимости, `implements EnergyConsumer`). Существующие `FleetService`, `FleetReportService`, `StandardInspectionService` и `InMemoryFleetRepository` продолжат работать без изменений, поскольку работают через абстракции `InventoryItem`, `Transport`, `EnergyConsumer`, `BeginnerFriendly`. Это прямое следствие **OCP**.

#### Сотрудник

**Не сломается ничего.**

Сотрудник не является объектом на балансе, поэтому он **не должен** реализовывать `InventoryItem` и не попадает в `FleetRepository`. Учёт сотрудников — это отдельный подмодуль (условные `Employee`, `EmployeeRepository`, `EmployeeService`), который никак не связан с текущим доменом парка.

#### Склад запчастей

**Не сломается ничего.**

Возможны два пути:

1. Если запчасти должны быть на общем балансе — они реализуют `InventoryItem` (наследуют `Asset`/`Thing`) и хранятся в том же `FleetRepository`. Отчёты автоматически подхватят их.
2. Если запчасти — отдельный домен — создаётся свой `WarehouseRepository` и `SparePart`, не затрагивая `FleetService`.

В обоих случаях ни одна из существующих строк кода не требует правки.

### Какой тест фиксирует решение техосмотра и/или правило «для новичков»? Как подменяется зависимость?

**Техосмотр фиксируют тесты:**

- `StandardInspectionServiceTest#acceptsValidDevice` — проверяет, что валидный `KickScooter` проходит техосмотр.
- `StandardInspectionServiceTest#rejectsInvalidInventoryNumberFormat` — проверяет отказ при некорректном формате инвентарного номера (`BAD-01`).
- `StandardInspectionServiceTest#rejectsNull` — проверяет отказ при `null`.
- `FleetServiceTest#addsDeviceWhenInspectionAccepts` — проверяет, что устройство добавляется в парк, если техосмотр принял его.
- `FleetServiceTest#doesNotAddDeviceWhenInspectionRejects` — проверяет, что устройство **не** попадает в парк при отказе.
- `FleetServiceDiTest#replacesInspectionServiceThroughDiContainer` — проверяет работу подмены зависимости через Guice.

**Правило «для новичков» фиксируют тесты:**

- `BeginnerRuleTest#scoreSixIsSuitableForBeginners` — оценка 6 подходит.
- `BeginnerRuleTest#scoreFiveIsNotSuitableForBeginners` — оценка 5 не подходит.
- `BeginnerRuleTest#invalidScoreThrowsException` — оценка 11 запрещена.
- `FleetReportServiceTest#returnsOnlyTransportSuitableForBeginners` — проверяет фильтрацию в отчёте (среди `KickScooter(8)`, `ElectricBicycle(5)`, `Bicycle(4)` возвращает только первый).

**Подмена зависимости.** Реализована двумя способами:

1. **Через конструктор** (`FleetServiceTest`):
   ```java
   var service = new FleetService(repository, new RejectAllInspectionStub());
   ```
   Это ручная подмена — быстро, явно, без DI.

2. **Через Guice** (`FleetServiceDiTest`):
   ```java
   bind(InspectionService.class).toInstance(new RejectAllInspectionStub());
   ```
   Это демонстрирует, что подмена работает и на уровне продакшен-графа, собранного DI-контейнером.

Заглушки (`AcceptAllInspectionStub`, `RejectAllInspectionStub`) лежат в пакете `testdoubles` и реализуют `InspectionService`.

### Что сделал ИИ?

ИИ использовался как вспомогательный инструмент. Он помог:

- проработать структуру пакета и иерархию классов по описанию предметной области;
- сформулировать контракт интерфейсов `InventoryItem`, `EnergyConsumer`, `BeginnerFriendly`;
- написать первый вариант реализации сервисов и тестов;
- оформить `README.md`.

---

## Запуск демонстрационного сценария

`ConsoleDemoRunner` последовательно:

1. Принимает в парк: `KickScooter (SC-1001)`, `Bicycle (BI-1002)`, `ElectricBicycle (EB-1003)`, `Helmet (HM-2001)`, `ChargingStation (CH-3001)`.
2. Пытается принять устройство с некорректным инвентарным номером (`BAD-01`) — получает отказ.
3. Выводит отчёты:
    - общее количество единиц на балансе;
    - суммарное суточное энергопотребление;
    - список техники, доступной новичкам;
    - полный список объектов с типом, именем, номером и энергопотреблением.

Примерный вывод (формат может слегка отличаться в зависимости от локали):

```
=== ВышКат: приём техники и вещей ===
[ПРИНЯТО] Самокат ВышКат S1 (SC-1001)
[ПРИНЯТО] Велосипед ВышКат City (BI-1002)
[ПРИНЯТО] Электровелосипед ВышКат E1 (EB-1003)
[ПРИНЯТО] Шлем ВышКат (HM-2001)
[ПРИНЯТО] Зарядная станция ВышКат (CH-3001)
[ОТКАЗ] Самокат с некорректным инвентарным номером (BAD-01)
    Причина: Неверный формат инвентарного номера: BAD-01. Ожидается формат вида SC-1001

=== Отчёты парка ===
Всего единиц на балансе: 5
Суммарное суточное энергопотребление: 11,20 кВт·ч

Прокатная техника, доступная новичкам (простота >= 6):
 - Велосипед ВышКат City (BI-1002), простота = 9
 - Самокат ВышКат S1 (SC-1001), простота = 8

Все предметы на балансе:
 - [Транспорт] Велосипед ВышКат City (BI-1002)
 - [Вещь] Зарядная станция ВышКат (CH-3001), 7,50 кВт·ч/сут
 - [Транспорт] Электровелосипед ВышКат E1 (EB-1003), 2,50 кВт·ч/сут
 - [Вещь] Шлем ВышКат (HM-2001)
 - [Транспорт] Самокат ВышКат S1 (SC-1001), 1,20 кВт·ч/сут
```