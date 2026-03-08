# Code Review — Bugemon (groupe-01)

**Date:** 2026-03-08  
**Reviewer:** AI assistant (Claude Sonnet 4.6)  
**Scope:** Full source tree — `src/main/java/ulb` + `src/test/java/ulb`  
**Java version:** 21 · Build tool: Maven 3.x · UI framework: JavaFX 21

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Project Structure Overview](#2-project-structure-overview)
3. [Critical Bugs (P0)](#3-critical-bugs-p0)
4. [Exception & API Issues (P1)](#4-exception--api-issues-p1)
5. [Design & Data-Structure Issues (P1)](#5-design--data-structure-issues-p1)
6. [Style & Encapsulation (P2)](#6-style--encapsulation-p2)
7. [Test Coverage & Quality (P1–P2)](#7-test-coverage--quality-p1p2)
8. [Design-Pattern Opportunities](#8-design-pattern-opportunities)
9. [Packaging & Architecture](#9-packaging--architecture)
10. [CI / Build](#10-ci--build)
11. [Positive Observations](#11-positive-observations)
12. [Prioritised Action Plan](#12-prioritised-action-plan)

---

## 1. Executive Summary

The project implements a turn-based monster-battling game ("Bugemon") in Java 21
with a JavaFX UI, Gson-based JSON parsing, and a JUnit 4/5 mixed test suite.
The architecture follows a clean MVC split (controllers, models, views) and the
codebase is well-documented with Javadoc.

**However, several issues range from crash-level bugs to important API and design
problems.** The most urgent ones are:

| Severity | Count | Summary |
|----------|-------|---------|
| 🔴 P0 — Crash / broken feature | 6 | CME in EffectManager, null action in ManualCombat, NPE from Parser returning null, Trainer empty-team crash, ManualCombat switch bug, offensive hardcoded string |
| 🟠 P1 — Important | 10 | Wrong exception types, Optional misuse, mutable value objects, raw array team, null slots, non-static state, no determinism in tests |
| 🟡 P2 — Cleanup | 9 | Non-static constants, non-static inner class, duplicated helpers, fragile tests, packaging |

---

## 2. Project Structure Overview

```
src/main/java/ulb/
├── AppLauncher.java            Entry-point shim (avoids JavaFX classpath check)
├── Main.java                   JavaFX Application subclass
├── common/
│   └── BugemonDTO.java         Read-only DTO interface
├── controllers/
│   ├── Controller.java         Abstract base controller
│   ├── MetaController.java     Screen navigation + shared state
│   ├── CreateTeamController, MainMenuController,
│   │   CombatVictory/DefeatController
│   └── combat/
│       ├── CombatController.java        Abstract combat controller
│       ├── AutomaticCombatController.java
│       └── ManualCombatController.java
├── models/
│   ├── bugemon/               Attack, Effect, ActiveEffect, Bugemon, enums
│   ├── bugemon_team/          BugemonTeam + exceptions/
│   ├── combat/                Combat, AutomaticCombat, ManualCombat,
│   │                          CombatHelper, EffectManager
│   └── trainer/               Trainer, AutoTrainer, ManualTrainer
├── utils/
│   ├── Parser.java
│   └── BugemonDeserializer.java
└── views/                     (excluded from detailed review)

src/test/java/ulb/
├── models/bugemon/            TestBugemon, TestAttack, TestEffect, TestActiveEffect
├── models/bugemon_team/       TestBugemonTeam
├── models/combat/             TestAutomaticCombat, TestCombatHelper, TestEffectManager
├── models/trainer/            TestTrainer, TestAutoTrainer, TestManualTrainer
└── utils/                     TestParser, TestUtils* (test fixtures)
```

---

## 3. Critical Bugs (P0)

### BUG-01 — `EffectManager.update()` causes `ConcurrentModificationException`

**File:** `src/main/java/ulb/models/combat/EffectManager.java` — `update()` method

```java
for (Bugemon key : effects.keySet()) {   // iterating the map
    ...
    effects.remove(key, current);         // mutating the same map
}
```

Calling `effects.remove(...)` inside a `for-each` over `effects.keySet()`
throws `ConcurrentModificationException` at runtime whenever any effect expires.

Additionally, the original logic had the decrement/expire check inverted: it
checked `isExpired()` *before* calling `decrementDuration()`, meaning an effect
with duration `1` would be reversed immediately on the same tick it was applied,
without ever decrementing.

**Fix — correct logic + no CME:**

```java
public void update() {
    List<Bugemon> toRemove = new ArrayList<>();
    for (Map.Entry<Bugemon, ActiveEffect> entry : effects.entrySet()) {
        ActiveEffect current = entry.getValue();
        current.decrementDuration();        // decrement first
        if (current.isExpired()) {          // then check
            Effect e = current.getEffect();
            handleEffect(entry.getKey(), e.getStat(), -e.getModifier());
            toRemove.add(entry.getKey());
        }
    }
    toRemove.forEach(effects::remove);      // remove after iteration
}
```

---

### BUG-02 — `ManualCombat.applyDamage()` discards the fainted-ally switch

**File:** `src/main/java/ulb/models/combat/ManualCombat.java` — `applyDamage()` method

```java
if (!this.allyTrainer.isCurrentBugemonAlive()) {
    this.allyTrainer.getSelectedBugemon();  // return value ignored!
}
```

`getSelectedBugemon()` is a plain getter. Calling it and discarding the result
does nothing. The ally's fainted Bugemon is never replaced, so `currentBugemon`
continues to point to a dead Bugemon for all subsequent turns — any call to
`isCurrentBugemonAlive()` will return `false` indefinitely.

**Fix:**

```java
if (!this.allyTrainer.isCurrentBugemonAlive()) {
    Bugemon replacement = this.allyTrainer.getSelectedBugemon();
    if (replacement != null) {
        this.allyTrainer.setCurrentBugemon(replacement);
    }
}
```

---

### BUG-03 — `Trainer` constructor crashes or produces null `currentBugemon` on empty team

**File:** `src/main/java/ulb/models/trainer/Trainer.java` — constructor

```java
public Trainer(BugemonTeam team) {
    this.team = team;
    this.currentBugemon = team.get(0);  // returns null if slot 0 is empty
}
```

`BugemonTeam.get(0)` returns `null` for an empty team (the backing array slot
is `null`). `MetaController` constructs a `Trainer` with a brand-new empty team:

```java
this.trainer = new Trainer(new BugemonTeam());  // MetaController.java
```

As a result `currentBugemon` is `null`, and any subsequent call such as
`isCurrentBugemonAlive()` will throw a `NullPointerException`.

**Fix option A — guard null in constructor (preferred for MetaController use):**

```java
public Trainer(BugemonTeam team) {
    this.team = team;
    this.currentBugemon = team.isEmpty() ? null : team.get(0);
}
```

Document that `currentBugemon` may be `null` and guard all usages, or change
`getCurrentBugemon()` to return `Optional<Bugemon>`.

**Fix option B — disallow empty-team Trainers (preferred for combat):**

```java
public Trainer(BugemonTeam team) {
    if (team.isEmpty()) throw new IllegalArgumentException("Team must not be empty");
    this.team = team;
    this.currentBugemon = team.get(0);
}
```

`MetaController` then holds only a `BugemonTeam` (not a `Trainer`) until
a combat is launched.

---

### BUG-04 — `ManualCombatController` passes `null` to `ManualCombat.turn()`

**File:** `src/main/java/ulb/controllers/combat/ManualCombatController.java` — `runManuelCombat()`

```java
while (winner == null) {
    winner = combat.turn(null);  // null hits the default: case → throws
}
```

`ManualCombat.turn(null)` reaches the `switch` statement's `default` branch and
throws `IllegalArgumentException("Illegal action: null")`. The entire manual
combat mode is non-functional.

**Fix:** Wire the view's action selection into the turn loop. This requires
moving the combat loop off the JavaFX Application Thread (see §8 PAT-02), e.g.
using a `Task<Trainer>` and callbacks triggered by button clicks in the view.

---

### BUG-05 — `Parser.parseAttacks()` and `parseBugemons()` return `null` on failure

**File:** `src/main/java/ulb/utils/Parser.java` — `parseAttacks()` and `parseBugemons()`

```java
} catch (Exception e) {
    System.out.println("Error when parsing attacks");
    e.printStackTrace();
}
return null;  // callers iterate this → NullPointerException
```

`Parser.parse()` immediately iterates the result:

```java
for (Attack a : attackList) { ... }   // NPE if attackList == null
```

If parsing fails, the stack trace is printed but the crash occurs later at an
unrelated-looking NPE site, making debugging extremely difficult.

**Fix:** Throw instead of returning `null`:

```java
static List<Attack> parseAttacks(Reader reader) throws IOException {
    try {
        // ... parse ...
        return attacks;
    } catch (Exception e) {
        throw new IOException("Failed to parse attacks: " + e.getMessage(), e);
    }
}
```

Update `Parser.parse()` and `MetaController.loadResources()` to propagate or
wrap the exception.

---

### BUG-06 — Hardcoded offensive string displayed to users

**File:** `src/main/java/ulb/controllers/combat/ManualCombatController.java` — constructor

```java
this.view.showDialog(
    "Oh nice a fucking hardcoded thing...",
    "fuck yeah"
);
```

This dialog is shown every time the manual combat controller is initialised —
i.e., at application startup. It must be removed before any user-facing build.

---

## 4. Exception & API Issues (P1)

### EXC-01 — `java.security.KeyException` used as a domain exception

**Files:** `Bugemon.java`, `EffectManager.java`, `TestEffectManager.java`

`KeyException` is part of the `java.security` cryptographic API. Using it to
signal an invalid game stat key is semantically wrong, confuses readers, and
forces callers to handle a checked exception from an unrelated API.

**Fix:** Replace with `IllegalArgumentException` (unchecked), or define a
custom `InvalidStatException extends RuntimeException` in the `bugemon` package.
Update all call sites and tests.

---

### EXC-02 — `e.getStackTrace()` passed to `System.err.println` prints garbage

**File:** `EffectManager.java`

```java
System.err.println(exception.getStackTrace());
// prints something like: [Ljava.lang.StackTraceElement;@7d4991ad
```

`getStackTrace()` returns a `StackTraceElement[]`. Passing an array to
`println` calls `Object.toString()`, printing the array's identity string
instead of the stack trace.

**Fix:** Use `exception.printStackTrace()`, or if you want structured logging,
use `java.util.logging` or SLF4J.

---

### EXC-03 — Missing domain exceptions in `BugemonTeam`

**File:** `src/main/java/ulb/models/bugemon_team/BugemonTeam.java`

The team throws `IllegalStateException` and `IllegalArgumentException` with
plain string messages. These are difficult to catch specifically.

**Suggestion:** Add custom exceptions alongside `BugemonAlreadyExistsException`:
- `TeamFullException extends RuntimeException`
- `BugemonNotFoundException extends RuntimeException`

---

### EXC-04 — `ManualTrainer.selectBugemon()` silently returns when defeated

**File:** `ManualTrainer.java`

```java
if (this.isDefeated()) {
    return;  // silent no-op
}
```

Callers have no way to know their selection was silently ignored. Document this
contract clearly in the Javadoc, or throw `IllegalStateException("Trainer is defeated")`.

---

### EXC-05 — Duration parse failure in `EffectManager` silently corrupts effect duration

**File:** `EffectManager.java`

```java
try {
    duration = e.extractDuration() - 1;
} catch (Exception exception) {
    // TODO: handle exception
    System.err.println(exception.getStackTrace());
}
```

A malformed duration string causes `duration` to remain `0`, so the effect
expires immediately on the next `update()` call with no diagnostic output
(only the useless array-toString message — see EXC-02).

**Fix:** Log a meaningful warning with the effect's data, and validate duration
format at parse time in `BugemonDeserializer` or `Parser`.

---

## 5. Design & Data-Structure Issues (P1)

### DES-01 — `Optional` used as field types in `Bugemon.Builder`

**File:** `Bugemon.java` — `Builder` inner class, fields at lines 230–257

```java
private Optional<String> id = Optional.empty();
private Optional<String> name = Optional.empty();
// ... etc.
```

`Optional` is designed as a *return type*, not a field type (Effective Java,
3rd ed., Item 55). Using it as a field:
- Allocates an extra wrapper object for each field
- Complicates serialisation
- Offers no benefit over a plain nullable field in a builder

**Fix:** Use plain nullable fields:

```java
private String id = null;           // mandatory — null means not set
private String name = DEFAULT_NAME; // already has a default
private BType type = DEFAULT_TYPE;
// ...
```

`build()` checks `id != null` directly, no `orElseThrow` needed.

---

### DES-02 — `Attack` and `Effect` are mutable but logically should be immutable

**Files:** `Attack.java`, `Effect.java`

Both classes expose full setter APIs. After being parsed from JSON, these
objects are shared across Bugemon instances (Flyweight, per `BugemonDeserializer`).
Mutating a shared `Attack` would corrupt all Bugemons that use it.

**Fix:** Mark all fields `final`, remove all setters. Gson can deserialise into
`final` fields via internal reflection. This also makes the Flyweight pattern
correct (see §8 PAT-06).

---

### DES-03 — `BugemonTeam.MAX_SIZE` is an instance constant

**File:** `BugemonTeam.java`

```java
private final int MAX_SIZE = 6;
```

This value does not depend on any instance state. It should be:

```java
private static final int MAX_SIZE = 6;
```

---

### DES-04 — `BugemonTeam.getTeam()` exposes `null` slots

**File:** `BugemonTeam.java`

```java
public List<Bugemon> getTeam() {
    return Arrays.asList(this.team);  // includes null entries for empty slots
}
```

Callers that do not filter nulls will get `NullPointerException`:
- `EffectManager.applyEffect()` iterates the result for `TEAM`-target effects
  and calls `bugemon.editStat(...)` on each element.
- `CreateTeamController.updateBugemonsTeamView()` adds the result directly to
  a `List<BugemonDTO>`.

**Fix:** Return only non-null members:

```java
public List<Bugemon> getTeam() {
    return Arrays.stream(this.team)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}
```

---

### DES-05 — `BugemonTeam.get(int)` silently returns `null` for empty slots

**File:** `BugemonTeam.java`

```java
public Bugemon get(int index) {
    if (index < 0 || index >= MAX_SIZE) throw new IndexOutOfBoundsException(...);
    return this.team[index];  // null if slot is empty
}
```

`Trainer` calls `team.get(0)` in its constructor. If slot 0 is empty (after a
remove), `currentBugemon` becomes silently `null`.

**Fix:** Either return `Optional<Bugemon>`, or throw when the slot is empty:

```java
public Optional<Bugemon> get(int index) {
    if (index < 0 || index >= MAX_SIZE) throw new IndexOutOfBoundsException(...);
    return Optional.ofNullable(this.team[index]);
}
```

---

### DES-06 — `Bugemon.State` is a non-static inner class

**File:** `Bugemon.java`

```java
private class State { ... }
```

`State` does not reference any instance of its enclosing `Bugemon`. Declaring
it non-static means every `State` object holds an invisible reference to its
`Bugemon`, preventing GC of detached states and complicating copy construction.

**Fix:**

```java
private static class State { ... }
```

---

### DES-07 — `new Random()` created on every call

**Files:** `AutoTrainer.java`, `BugemonTeam.createRandomTeam()`

```java
public Attack getRandomAttack() {
    Random rand = new Random();  // new instance per call
    ...
}
```

A `new Random()` per call is wasteful and degrades statistical quality (each
instance uses `System.nanoTime()` as seed; nearby calls in the same millisecond
may produce correlated sequences).

**Fix:** Use a shared static instance:

```java
private static final Random RNG = new Random();
```

---

### DES-08 — `CombatHelper.compareBType()` builds a new `List` on every call

**File:** `CombatHelper.java`

```java
final List<BType> cycle = new ArrayList<BType>(List.of(BType.values()));
```

This allocates two objects (the array from `values()` + the `ArrayList`) every
time a type comparison is made — i.e., once or more per turn.

**Fix:** Use `ordinal()` directly, no list needed:

```java
private static final int TYPE_COUNT = BType.values().length;

public static Efficiency compareBType(BType offensiveType, BType defensiveType) {
    final int delta = Math.floorMod(
        offensiveType.ordinal() - defensiveType.ordinal(),
        TYPE_COUNT
    );
    if (delta == 1) return Efficiency.LOW;
    if (delta == TYPE_COUNT - 1) return Efficiency.HIGH;
    return Efficiency.NEUTRAL;
}
```

---

### DES-09 — `ManualCombat` shadows parent trainer fields

**File:** `ManualCombat.java`

```java
private ManualTrainer allyTrainer;     // shadows Combat.allyTrainer
private AutoTrainer adversaryTrainer;  // shadows Combat.adversaryTrainer
```

The parent class `Combat` already stores these as `protected Trainer`. The
subclass declares new identically-named fields, which shadow (not override) the
parent's. Both sets of fields point to the same objects initially, but reading
`super.allyTrainer` vs `this.allyTrainer` could diverge in future refactors.

**Fix:** Remove the subclass fields and cast when the typed API is needed:

```java
private ManualTrainer getAlly() { return (ManualTrainer) super.allyTrainer; }
private AutoTrainer getAdversary() { return (AutoTrainer) super.adversaryTrainer; }
```

---

### DES-10 — `Combat.isFinished()` unnecessary `if/return` pattern

**File:** `Combat.java`

```java
if (allyTrainer.isDefeated() || adversaryTrainer.isDefeated()) {
    return true;
}
return false;
```

**Fix:**

```java
return allyTrainer.isDefeated() || adversaryTrainer.isDefeated();
```

---

### DES-11 — `BugemonDeserializer` silently drops unknown attack IDs

**File:** `BugemonDeserializer.java`

```java
if (attack != null) {
    attackList.add(attack);
}
// else: silently ignored
```

A Bugemon referencing an attack ID not in the map ends up with fewer attacks.
If all attacks are missing, `getRandomAttack()` calls `nextInt(0)` and throws
`IllegalArgumentException`.

**Fix:** Log a warning or throw `JsonParseException` for unknown IDs.

---

### DES-12 — `MetaController` stores a `Trainer` to hold a team

**File:** `MetaController.java`

```java
this.trainer = new Trainer(new BugemonTeam());
```

`Trainer` is a combat entity (tracks `currentBugemon`, `isDefeated()`, etc.).
Using it as a pre-combat team holder couples the UI state to a combat model.

**Suggestion:** Store `BugemonTeam` directly in `MetaController`. Construct the
appropriate `Trainer` subclass only when launching a combat.

---

## 6. Style & Encapsulation (P2)

### STY-01 — `static` on `ManualTrainer.TAction` enum is redundant

Nested enums are implicitly `static` in Java. The `public static enum TAction`
declaration should simply be `public enum TAction`.

---

### STY-02 — `Combat` fields are `protected` — prefer `private` with accessors

**File:** `Combat.java`

```java
protected Trainer allyTrainer;
protected Trainer adversaryTrainer;
protected int turn;
```

`protected` exposes these mutable fields to any subclass in any package.
Using `private` fields with `protected` getters (`getAllyTrainer()`,
`getAdversaryTrainer()`, `getTurn()` — already present) is safer and avoids
the field-shadowing problem in `ManualCombat` (DES-09).

---

### STY-03 — `Main.STAGE_TITLE` should be `static final`

**File:** `Main.java`

```java
private String STAGE_TITLE = "Bugemon";
```

This is an instance field written only once and never modified. Correct form:

```java
private static final String STAGE_TITLE = "Bugemon";
```

---

### STY-04 — `killBugemon` test helper duplicated

`TestTrainer` and `TestManualTrainer` both define an identical static helper:

```java
public static void killBugemon(BugemonTeam team, String id) {
    team.getBugemon(id).takeDamage(team.getBugemon(id).getHp());
}
```

`TestAutoTrainer` correctly delegates to `TestTrainer.killBugemon`. Move the
single canonical definition to `TestUtilsBugemonTeam` and delete the duplicate.

---

### STY-05 — `BugemonDTO` interface references the nested `Bugemon.BType`

**File:** `BugemonDTO.java`

```java
Bugemon.BType getType();
```

A DTO meant to decouple views from model internals should not directly reference
a type nested inside the main model class. Promoting `Bugemon.BType` to a
top-level `BType` enum (see §9 PKG-01) resolves this cleanly.

---

### STY-06 — Unnecessary `(AutoTrainer)` cast in `AutomaticCombatController`

**File:** `AutomaticCombatController.java`

```java
winner = (AutoTrainer) combat.getWinner();
```

`handleCombatResult` accepts `Trainer`. The cast is unused.

**Fix:** Change `winner` to `Trainer`:

```java
Trainer winner = null;
while (winner == null) {
    combat.turn();
    winner = combat.getWinner();
    combat.incrementTurn();
}
```

---

## 7. Test Coverage & Quality (P1–P2)

### TEST-01 — Test fixture utilities live in the production `ulb.utils` package name

**Files:** `src/test/java/ulb/utils/TestUtils*.java`

These are test-only helper classes, but they share the package name `ulb.utils`
with production code (`Parser`, `BugemonDeserializer`). Although they are
physically under `src/test/`, the identical package name causes confusion when
browsing imports.

**Fix:** Rename to `ulb.test.fixtures` or `ulb.utils.test`.

---

### TEST-02 — `killBugemon` helper duplicated across test files

See STY-04.

---

### TEST-03 — `TestEffectManager.testEffectDuration` will fail due to BUG-01

`testEffectDuration` calls `effectManager.update()`, which triggers the
`ConcurrentModificationException` from BUG-01 the moment any effect expires.
Fix BUG-01 first; this test should then pass.

---

### TEST-04 — No `TestManualCombat` class

`ManualCombat` is the core of the game's interactive mode and has no dedicated
tests at all. Once BUG-04 is fixed, add tests for:

- Normal `ATTACK` turn (damage applied, turn incremented)
- `SWITCH` turn (Bugemon replaced, no damage)
- `FORFEIT` turn (adversary returned as winner immediately)
- Fainted ally switch (regression for BUG-02)
- Combat end detection after all Bugemons faint

---

### TEST-05 — `TestAutomaticCombat.testApplyDamage` is fragile

**File:** `TestAutomaticCombat.java`

```java
assertTrue(finalHp1 < initialHp1);
assertTrue(finalHp2 < initialHp2);
```

If the ally's first attack one-shots the adversary's Bugemon, `applyDamageHelper`
returns early and the adversary never gets to attack the ally. The second
assertion then fails non-deterministically.

**Fix:** Use a weaker assertion (at least one trainer took damage), or configure
the test Bugemons so that one-shots are impossible.

---

### TEST-06 — `TestUtilsBugemons` creates effects with invalid duration format

**File:** `TestUtilsBugemons.java`

```java
Effect effect = new Effect(
    EffectType.STAT_MODIFIER,
    EffectTarget.ADVERSARY,
    EffectStat.ATTACK,
    10,
    "1 turn"   // space, not underscore
);
```

`Effect.extractDuration()` expects `"<n>_<unit>"` (underscore separator). The
string `"1 turn"` (space) would throw `IllegalArgumentException` if this effect
were ever fed through `EffectManager`. The real JSON data uses `"1_tour"`.

**Fix:** Change to `"1_turn"` (or any valid `<n>_<unit>` string).

---

### TEST-07 — `TestParser` relies on index positions in the JSON file

**File:** `TestParser.java`

```java
assertEquals(attackList.get(0).getId(), "fouet_liane");
assertEquals(attackList.get(1).getType(), Bugemon.BType.FLORA);
```

Reordering entries in the JSON file breaks these tests with no compiler warning.

**Fix:** Look up by ID:

```java
Attack fouetLiane = attackList.stream()
    .filter(a -> "fouet_liane".equals(a.getId()))
    .findFirst().orElseThrow();
```

---

### TEST-08 — Missing tests

| Behaviour | Why needed |
|---|---|
| `Parser.parseAttacks()` / `parseBugemons()` with malformed JSON | Verify BUG-05 fix propagates errors |
| `Bugemon.clone()` deep-copy semantics | Modifying clone must not affect original |
| `Bugemon.editStat()` default throw branch | Regression for EXC-01 fix |
| `EffectManager.update()` no-CME assertion | Regression for BUG-01 fix |
| `BugemonTeam.get(int)` on empty slot | Regression for DES-05 fix |
| `ManualTrainer.getAttackPower()` when no attack selected | Contract: returns 0 |
| `Trainer` constructor with empty team | Regression for BUG-03 fix |
| `CombatHelper.attackPriority()` tie-break | Random branch rarely tested |

---

## 8. Design-Pattern Opportunities

### PAT-01 — Strategy pattern for trainer action selection

**Problem:** `AutoTrainer` and `ManualTrainer` are concrete subclasses of
`Trainer`. Any new AI variant (e.g. smarter opponent) requires a new subclass.

**Proposed:** Extract a `CombatStrategy` interface:

```java
public interface CombatStrategy {
    Attack selectAttack(Bugemon current);
    Optional<Bugemon> selectReplacement(BugemonTeam team);
}
```

Implementations: `RandomCombatStrategy`, `PlayerCombatStrategy`.  
`Trainer` holds a `CombatStrategy` instead of being subclassed.

**Benefits:** Single `Trainer` class, strategy swappable at runtime, easier to
mock in tests.

---

### PAT-02 — Observer / JavaFX property binding for combat UI

**Problem:** Both `runAutoCombat` and `runManuelCombat` run the entire combat
loop synchronously on the JavaFX Application Thread, blocking the UI.

**Proposed:**
- Move combat loops to a `Task<Trainer>` (JavaFX background thread).
- Expose `ReadOnlyObjectProperty<Bugemon>` for ally/adversary current Bugemon
  and `ReadOnlyIntegerProperty` for HP.
- Views bind to these properties; updates are pushed via `Platform.runLater()`.

**Benefits:** Non-blocking UI, clean model/view separation, enables
turn-by-turn animations.

---

### PAT-03 — Command pattern for player actions

**Problem:** Adding a new `TAction` requires modifying both the enum and the
`switch` in `ManualCombat.turn()`.

**Proposed:**

```java
public interface CombatCommand {
    Trainer execute(ManualCombat combat);
}
class AttackCommand  implements CombatCommand { ... }
class SwitchCommand  implements CombatCommand { ... }
class ForfeitCommand implements CombatCommand { ... }
```

`ManualCombat.turn(CombatCommand cmd)` calls `cmd.execute(this)`.

**Benefits:** Open/Closed principle, commands are independently testable.

---

### PAT-04 — Template Method for the combat turn lifecycle

**Problem:** The pre/post turn logic (effect update, turn counter increment)
is scattered across subclasses and easy to forget.

**Proposed:** Add a `final playTurn()` template in `Combat`:

```java
public final void playTurn() {
    preTurn();      // hook
    resolveTurn();  // abstract — overridden by subclasses
    postTurn();     // hook — calls effectManager.update() and incrementTurn()
}
protected abstract void resolveTurn();
protected void preTurn()  {}
protected void postTurn() { effectManager.update(); incrementTurn(); }
```

**Benefits:** Ensures effect management and turn counting always run, regardless
of which subclass is used.

---

### PAT-05 — Factory for random team generation

**Problem:** `BugemonTeam.createRandomTeam(List, int)` is a static factory on
the data class itself — mixing creation logic with data logic.

**Proposed:** Extract a `TeamFactory`:

```java
public class TeamFactory {
    public static BugemonTeam createRandom(List<Bugemon> pool, int size) { ... }
    public static BugemonTeam createStarterTeam(List<Bugemon> pool)      { ... }
}
```

**Benefits:** Single Responsibility — `BugemonTeam` is a pure data structure.

---

### PAT-06 — Flyweight for shared `Attack` objects

`BugemonDeserializer` already implements Flyweight by reusing `Attack`
instances from the attacks map. However, `Attack` being mutable (DES-02)
undermines this — one Bugemon could silently corrupt the shared instance.

Making `Attack` immutable (fix for DES-02) is all that is needed to make this
Flyweight pattern correct and safe.

---

## 9. Packaging & Architecture

### PKG-01 — Promote `Bugemon.BType` to a top-level enum

`BType` is nested inside `Bugemon` but is referenced by at least 8 other
classes. Widely used cross-cutting types should not be nested inside a single
model class.

**Fix:** Create `src/main/java/ulb/models/bugemon/BType.java` and move the
enum there. Update all imports (automated refactor in any IDE).

---

### PKG-02 — `ulb.common` contains a single interface

`ulb.common.BugemonDTO` is the only class in the package. Either rename the
package to `ulb.interfaces` (or `ulb.api`) to better reflect its purpose, or
move `BugemonDTO` into `ulb.models.bugemon`.

---

### PKG-03 — Test utilities share the production `ulb.utils` package name

See TEST-01. Rename test fixtures to `ulb.test.fixtures`.

---

### PKG-04 — Missing `package-info.java` for root `ulb` package

All sub-packages have `package-info.java` files. `Main` and `AppLauncher` live
in `ulb` with none. Add one for consistency.

---

## 10. CI / Build

### CI-01 — Release-candidate Maven image in CI pipeline

**File:** `.gitlab-ci.yml`

```yaml
image: maven:4.0.0-rc-5-ibm-semeru-25-noble
```

RC Docker images may contain bugs or be removed without notice. Use a stable
tag:

```yaml
image: maven:3.9.9-eclipse-temurin-21
```

---

### CI-02 — No test report artifact published

The CI `test` job runs `mvn clean test` but publishes no artifact. Add:

```yaml
test:
  script:
    - mvn clean test
  artifacts:
    paths:
      - target/surefire-reports/
    expire_in: 7 days
    when: always
```

---

### CI-03 — Mixed JUnit 4 and JUnit 5 without the vintage engine

**File:** `pom.xml`

`junit:junit:4.13.1` (JUnit 4) and `junit-jupiter-api` (JUnit 5) are both on
the test classpath. Tests use annotations from both versions in the same files
(e.g. `TestAutomaticCombat` imports `org.junit.Test` and
`org.junit.jupiter.api.Assertions`). Surefire 2.22.x requires
`junit-vintage-engine` to run JUnit 4 `@Test` methods alongside JUnit 5.
Without it, JUnit 4 tests may be silently skipped.

**Fix:** Add the vintage engine, or standardise on JUnit 5:

```xml
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <scope>test</scope>
</dependency>
```

---

### CI-04 — Outdated Maven plugin versions

| Plugin | Current | Recommended |
|---|---|---|
| `maven-surefire-plugin` | 2.22.1 | 3.2.5 |
| `maven-compiler-plugin` | 3.8.0 | 3.13.0 |
| `maven-resources-plugin` | 3.0.2 | 3.3.1 |
| `maven-jar-plugin` | 3.1.0 | 3.4.1 |

Surefire 2.22.x in particular has known JUnit 5 discovery issues.

---

## 11. Positive Observations

- **Comprehensive Javadoc.** Every public class and method has a Javadoc
  comment with `@param`, `@return`, and `@throws`. This is well above average.

- **`Bugemon.Builder`.** Fluent builder with sensible defaults is idiomatic
  and test-friendly (aside from the `Optional` field issue).

- **`Parser.ParseResult` value object.** Returning a clean, structured result
  from the main parse call is good API design.

- **`BugemonDTO` interface.** The intent to decouple views from model internals
  is correct, even if the `Bugemon.BType` coupling slightly undermines it.

- **`CombatHelper` as a pure static utility.** All methods are stateless and
  side-effect free — easy to unit test in isolation.

- **`calculateDamage` with explicit `criticFactor` overload.** Allowing the
  critical-hit multiplier to be injected makes the damage formula deterministically
  testable — a solid design decision.

- **`BugemonTeam.iterator()` skips null slots cleanly.** The stream + filter
  approach in the `iterator()` method is correct and safe.

- **`ActiveEffect` lifecycle.** Wrapping an `Effect` with a turn counter in
  a dedicated class is clean and easy to reason about.

- **`BugemonAlreadyExistsException`.** Domain-specific exception for duplicate
  team members is good practice.

- **Sprite path normalisation in `BugemonDeserializer`.** Automatically
  prepending `"png/"` if missing shows defensive, user-friendly parsing.

---

## 12. Prioritised Action Plan

### 🔴 P0 — Fix immediately (crash / completely broken feature)

| ID | Action | File(s) |
|----|--------|---------|
| BUG-01 | Fix CME in `EffectManager.update()` — collect expiring keys, remove after loop; fix decrement-before-check order | `EffectManager.java` |
| BUG-02 | Fix `ManualCombat.applyDamage()` — call `setCurrentBugemon(getSelectedBugemon())` after ally faints | `ManualCombat.java` |
| BUG-03 | Guard `Trainer` constructor for empty team — null-safe `currentBugemon` init | `Trainer.java`, `MetaController.java` |
| BUG-04 | Remove `combat.turn(null)` loop — wire view-driven action selection via task/callback | `ManualCombatController.java` |
| BUG-05 | `Parser` methods must throw `IOException` instead of returning `null` | `Parser.java` |
| BUG-06 | Remove offensive hardcoded dialog string | `ManualCombatController.java` |

### 🟠 P1 — Important (next sprint)

| ID | Action | File(s) |
|----|--------|---------|
| EXC-01 | Replace `KeyException` with `IllegalArgumentException` or custom exception | `Bugemon.java`, `EffectManager.java` |
| EXC-02 | Fix `println(getStackTrace())` → `printStackTrace()` | `EffectManager.java` |
| EXC-05 | Log meaningful warning on duration parse failure | `EffectManager.java` |
| DES-01 | Remove `Optional` fields from `Bugemon.Builder` | `Bugemon.java` |
| DES-02 | Make `Attack` and `Effect` immutable (`final` fields, no setters) | `Attack.java`, `Effect.java` |
| DES-03 | `MAX_SIZE` → `private static final` | `BugemonTeam.java` |
| DES-04 | `getTeam()` — filter null slots before returning | `BugemonTeam.java` |
| DES-05 | `get(int)` — return `Optional<Bugemon>` or throw for empty slots | `BugemonTeam.java` |
| DES-06 | `Bugemon.State` → `private static class` | `Bugemon.java` |
| DES-07 | Replace per-call `new Random()` with `static final RNG` | `AutoTrainer.java`, `BugemonTeam.java` |
| DES-08 | Cache `BType` ordinal cycle in `CombatHelper` | `CombatHelper.java` |
| DES-09 | Remove duplicate trainer fields from `ManualCombat` | `ManualCombat.java` |
| DES-11 | Warn or throw on unknown attack ID in `BugemonDeserializer` | `BugemonDeserializer.java` |
| TEST-03 | Fix `testEffectDuration` (depends on BUG-01 fix) | `TestEffectManager.java` |
| TEST-06 | Fix duration string `"1 turn"` → `"1_turn"` | `TestUtilsBugemons.java` |
| CI-03 | Add `junit-vintage-engine` or unify on JUnit 5 | `pom.xml` |

### 🟡 P2 — Cleanup & improvements (ongoing)

| ID | Action | File(s) |
|----|--------|---------|
| STY-01 | Remove redundant `static` on `TAction` enum | `ManualTrainer.java` |
| STY-02 | `Combat` fields → `private` (accessors already exist) | `Combat.java` |
| STY-03 | `STAGE_TITLE` → `static final` | `Main.java` |
| STY-04 | Deduplicate `killBugemon` test helper | `TestManualTrainer.java` |
| STY-05 / PKG-01 | Promote `Bugemon.BType` to top-level enum | new `BType.java` |
| STY-06 | Remove unnecessary `(AutoTrainer)` cast in `runAutoCombat` | `AutomaticCombatController.java` |
| DES-10 | Simplify `Combat.isFinished()` | `Combat.java` |
| DES-12 | Replace `Trainer` holder in `MetaController` with `BugemonTeam` | `MetaController.java` |
| PKG-02 | Rename `ulb.common` to `ulb.interfaces` or move `BugemonDTO` | `BugemonDTO.java` |
| PKG-03 | Move `TestUtils*` to `ulb.test.fixtures` | test tree |
| PKG-04 | Add `package-info.java` for root `ulb` package | new file |
| TEST-01 | Rename test fixture package | test tree |
| TEST-04 | Add `TestManualCombat` test class | new test file |
| TEST-05 | Fix fragile `testApplyDamage` assertion | `TestAutomaticCombat.java` |
| TEST-07 | Replace index-based with ID-based lookups in `TestParser` | `TestParser.java` |
| TEST-08 | Add missing tests (clone, editStat, Parser error paths, etc.) | various |
| CI-01 | Use stable Maven CI image (`maven:3.9.9-eclipse-temurin-21`) | `.gitlab-ci.yml` |
| CI-02 | Publish Surefire HTML report as CI artifact | `.gitlab-ci.yml` |
| CI-04 | Update outdated Maven plugin versions | `pom.xml` |
| PAT-01–06 | Apply Strategy/Observer/Command/Template/Factory/Flyweight patterns | various |

---
