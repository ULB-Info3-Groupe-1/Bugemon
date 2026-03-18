# Code Review — Bugemon (groupe-01) — Iteration 1

**Date:** 2026-03-10
**Reviewer:** AI assistant (Claude Sonnet 4.6)
**Scope:** Full source tree — `src/main/java/ulb` + `src/test/java/ulb`
**Java version:** 21 · Build tool: Maven 3.x · UI framework: JavaFX 21
**Based on:** `code_review.md` + direct source inspection of every referenced file

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Critical Bugs — P0](#2-critical-bugs--p0)
3. [Exception & API Issues — P1](#3-exception--api-issues--p1)
4. [Design & Architecture Issues — P1](#4-design--architecture-issues--p1)
5. [Style & Encapsulation — P2](#5-style--encapsulation--p2)
6. [Test Coverage & Quality — P1–P2](#6-test-coverage--quality--p1p2)
7. [Packaging & Architecture](#7-packaging--architecture)
8. [CI / Build](#8-ci--build)
9. [Design-Pattern Opportunities *(new section)*](#9-design-pattern-opportunities)
10. [Positive Observations](#10-positive-observations)
11. [Prioritised Action Plan](#11-prioritised-action-plan)

---

## 1. Executive Summary

Since the previous review, the team has made notable progress: test utilities were
properly relocated to a dedicated `ulb.utils.test` package; a `TestManualTrainer`
class was added; the `LevelUp` / `Choice` / `LevelUpController` feature was
implemented end-to-end with tests; XP calculation was moved out of the controller
into `CombatHelper.calculateXP()`; and the `participation` tracking flag was
introduced and wired into both combat types.

However, **many critical bugs remain unfixed and new issues have been introduced**
alongside the new features. The most severe are listed below and detailed in the
sections that follow.

**Confirmed-still-broken (from previous review):**
- `EffectManager.update()` — `ConcurrentModificationException` on effect expiry.
- `ManualCombat.applyDamage()` — silent no-op when ally faints.
- String identity comparison (`!=`) in both combat controllers.
- `EffectManager` and `CombatHelper.calculateDamage()` never called in combat.
- `CombatController.isAttackEfficient()` duplicates `CombatHelper.compareBType()`.
- `ManualCombat` re-declares and shadows parent trainer fields.
- `new Random()` on every call in `AutoTrainer`.
- `CombatHelper.compareBType()` allocates a new `List` every call.
- `Bugemon.State` is a non-static inner class.
- `TeamFactory` has an implicit global dependency.
- `static` on `ManualTrainer.TAction` is redundant.
- `Combat` fields are `protected` instead of `private`.
- `BugemonDTO` references the nested `Bugemon.BType`.
- `Combat.isFinished()` uses an unnecessary `if/return`.
- `TestParser` relies on positional index lookups — **partially fixed** (name-based
  lookups added in `testBugemonParsing`/`testParseWithInputStreams`, but
  `testAttackParsing` still uses the stream `.filter()` pattern — verified to be
  ID-based now ✔).
- Mixed JUnit 4 and JUnit 5 without `junit-vintage-engine`.

**New issues introduced in this iteration:**
- `addXp()` XP thresholds inconsistent between explicit cases and `default` formula.
- `CombatVictoryController.cont()` is a dead stub that bypasses the level-up flow.
- `CombatHelper.calculateXP()` divides by zero when no Bugemon participated.
- `applyChoice()` updates `state.hp` but not `state.maxHp`.
- `Bugemon.reset()` wipes level-up bonuses.
- `LevelUpController` fields are package-private.
- `LevelUpDTO.getChoices()` leaks the model type `Choice` into the DTO layer.
- `LevelUp.generateRandomChoice()` instantiates `new Random()` per call.
- `CombatHelper.calculateXP()` has hardcoded TODO values; `loser` param is unused.
- `Choice` fields are package-private.
- Several new public methods lack Javadoc.
- Stale `// TODO: change that` comment in `showSwitchMenu()`.

---

## 2. Critical Bugs — P0

### BUG-01 — `EffectManager.update()` causes `ConcurrentModificationException` *(not fixed)*

**File:** `src/main/java/ulb/models/combat/EffectManager.java`

**Verified in source:** The `update()` method iterates over `effects.keySet()` while
calling `effects.remove()` inside the same loop body. Any call to `update()` where
at least one effect has expired will throw a `ConcurrentModificationException` at
runtime, silently crashing the entire effect system.

```
// CURRENT (broken)
public void update() {
    for (Bugemon key : effects.keySet()) {           // iterating the map
        ActiveEffect current = effects.get(key);
        if (current.isExpired()) {
            ...
            effects.remove(key, current);            // modifying during iteration → CME
        } else {
            current.decrementDuration();
        }
    }
}
```

**Fix:** collect the keys to remove first, then remove after the loop.

```
// FIXED
public void update() {
    List<Bugemon> toRemove = new ArrayList<>();
    for (Map.Entry<Bugemon, ActiveEffect> entry : effects.entrySet()) {
        ActiveEffect current = entry.getValue();
        if (current.isExpired()) {
            Effect e = current.getEffect();
            handleEffect(entry.getKey(), e.getStat(), -e.getModifier());
            toRemove.add(entry.getKey());
        } else {
            current.decrementDuration();
        }
    }
    toRemove.forEach(effects::remove);
}
```

---

### BUG-02 — `ManualCombat.applyDamage()` silent no-op when ally faints *(not fixed)*

**File:** `src/main/java/ulb/models/combat/ManualCombat.java`

**Verified in source:** The last `if` block in `applyDamage()` calls the getter and
discards its return value. The dead Bugemon remains on the field.

```
// CURRENT (broken)
if (!this.allyTrainer.isCurrentBugemonAlive()) {
    this.allyTrainer.getSelectedBugemon();   // pure getter — return value discarded
}
```

When the ally's current Bugemon faints from a counter-attack, the switch to the next
Bugemon is never performed.

**Fix:**

```
// FIXED
if (!this.allyTrainer.isCurrentBugemonAlive()) {
    Bugemon next = this.allyTrainer.getSelectedBugemon();
    if (next != null) {
        this.allyTrainer.setCurrentBugemon(next);
    }
}
```

---

### BUG-03 — String identity comparison (`!=`) instead of `.equals()` *(not fixed)*

**Files:**
- `src/main/java/ulb/controllers/combat/AutomaticCombatController.java` (inside the
  `KeyFrame` lambda)
- `src/main/java/ulb/controllers/combat/ManualCombatController.java` (`playerAttack`)

**Verified in source:**

```
// AutomaticCombatController — BROKEN
if (enemyBugemon.getId() != opponent.getCurrentBugemon().getId()) { ... }

// ManualCombatController — BROKEN
if (enemyBugemon.getId() != this.opponent.getCurrentBugemon().getId()) { ... }
```

`!=` on `String` compares object references, not values. Two logically identical ID
strings from different `String` instances compare as not-equal. The dialog-hiding
logic that depends on this check will behave non-deterministically.

**Fix:**

```
if (!enemyBugemon.getId().equals(opponent.getCurrentBugemon().getId())) { ... }
```

---

### BUG-04 — `EffectManager` and `CombatHelper.calculateDamage()` never called in combat *(not fixed)*

**Files:** `AutomaticCombat.java`, `ManualCombat.java`

**Verified in source:** Both combat classes use raw `attack.getPower()` as the damage
value.

```
// AutomaticCombat.applyDamage() — BROKEN
applyDamageHelper(allyTrainer, adversaryTrainer, allyAttack.getPower());
applyDamageHelper(adversaryTrainer, allyTrainer, adversaryAttack.getPower());

// ManualCombat.applyDamage() — BROKEN
int allyAttackPower = this.allyTrainer.getSelectedAttack().getPower();
this.adversaryTrainer.takeDamage(allyAttackPower);
...
this.allyTrainer.takeDamage(adversaryAttack.getPower());
```

Neither `CombatHelper.calculateDamage()` nor `EffectManager.applyEffect()` is ever
called. Attack stats, defense stats, type effectiveness, and critical hits are all
silently ignored during actual combat. All effects defined in JSON are discarded.

**Fix:** Replace raw `getPower()` calls with `CombatHelper.calculateDamage()` and
call `effectManager.applyEffect()` / `effectManager.update()` on each turn.

---

### BUG-05 — `addXp()` XP thresholds inconsistent between explicit cases and `default` formula *(NEW)*

**File:** `src/main/java/ulb/models/bugemon/Bugemon.java`

**Verified in source:**

```
// CURRENT (broken)
case 1 -> { if (this.state.xp >= 50)  { return Optional.of(this.levelUp()); } }
case 2 -> { if (this.state.xp >= 150) { return Optional.of(this.levelUp()); } }
case 3 -> { if (this.state.xp >= 250) { return Optional.of(this.levelUp()); } }
case 4 -> { if (this.state.xp >= 350) { return Optional.of(this.levelUp()); } }
default -> {
    int requiredXp = 50 + 50 * (this.state.level - 1); // level 2 → 100, level 3 → 150 ...
    ...
}
```

The `default` formula gives 100 for level 2, 150 for level 3, 200 for level 4.
The explicit cases hard-code 150 for level 2, 250 for level 3, 350 for level 4 —
significantly higher thresholds. The `TestLevelUp.testBugemonExperienceAndLevelProperties`
test only adds 150 XP in a single call for the level 2→3 transition, which incidentally
passes both the 100-XP formula and the 150-XP case. A player at level 2 who has
accumulated e.g. 120 XP should level up (formula says yes), but the explicit case
says no. The system is inconsistent across levels 1–4.

**Fix:** Drop the explicit cases and rely solely on a single formula:

```
// FIXED
public Optional<LevelUp> addXp(int xp) {
    this.state.xp += xp;
    int requiredXp = 50 * this.state.level;   // level 1 → 50, level 2 → 100, …
    if (this.state.xp >= requiredXp) {
        return Optional.of(this.levelUp());
    }
    return Optional.empty();
}
```

---

### BUG-06 — `CombatVictoryController.cont()` is a dead stub that bypasses the level-up flow *(NEW)*

**File:** `src/main/java/ulb/controllers/CombatVictoryController.java`

**Verified in source:**

```
// CURRENT (broken)
public void cont() {
    // TODO: impl
    // Optional<LevelUp> lvlup = bugemon.addxp(xpwonatfight)
    // if lvlup.isPresent() -> switch to level up screen
    // else -> back to main menu
    this.metaController.switchTo(Window.MAIN_MENU);
    this.metaController.resetTeam();
}
```

Despite the entire `LevelUpController` / `LevelUpView` / `LevelUpDTO` pipeline being
fully implemented and wired through `CombatController.handleCombatResult()` →
`MetaController.setLevelUp()` → `LevelUpController.setLevelUp()`, the victory screen
`cont()` method navigates directly to `MAIN_MENU` and then calls `resetTeam()`, which
wipes all stats including newly earned XP and level-up bonuses. The entire level-up
flow is dead-on-arrival from the victory screen.

Note also: `LevelUpController.cont()` has a commented-out `resetTeam()` call
(`// TODO: see with client, this.metaController.resetTeam()`), which is the correct
place for it — but `CombatVictoryController.cont()` calls it unconditionally and
prematurely.

**Fix:** `CombatVictoryController.cont()` should not call `resetTeam()`. The
navigation to `MAIN_MENU` after a level-up should be driven exclusively by
`LevelUpController.cont()` once all level-up choices have been made.

---

### BUG-07 — `CombatHelper.calculateXP()` divides by zero when no Bugemon participated *(NEW)*

**File:** `src/main/java/ulb/models/combat/CombatHelper.java`

**Verified in source:**

```
// CURRENT (broken)
long numParticipatingBugemon =
    winner.getTeam().stream().filter(b -> b.getParticipation()).count();
...
int xpPerBugemon = (int)(xpWon / numParticipatingBugemon);  // ArithmeticException if 0
```

If no Bugemon has `participation = true` (e.g. a forfeit before any turn is played),
this throws `ArithmeticException: / by zero` at runtime.

**Fix:**

```
// FIXED
if (numParticipatingBugemon == 0) return 0;
int xpPerBugemon = (int)(xpWon / numParticipatingBugemon);
```

---

### BUG-08 — `Parser.parseAttacks()` and `parseBugemons()` return `null` on failure *(not fixed)*

**File:** `src/main/java/ulb/utils/Parser.java`

**Verified in source:**

```
// CURRENT (broken)
} catch (Exception e) {
    System.out.println("Error when parsing attacks");
    e.printStackTrace();
}
return null;   // propagates silently as NullPointerException downstream
```

`Parser.parse()` directly calls `.stream()` on the returned list from `parseAttacks()`
with no null check. A parse failure silently produces a `NullPointerException` at the
`Collectors.toMap(...)` call with no meaningful message. The same applies to
`parseBugemons()`.

**Fix:** throw a checked or unchecked exception instead of returning null:

```
// FIXED
} catch (Exception e) {
    throw new IOException("Failed to parse attacks JSON", e);
}
```

---

## 3. Exception & API Issues — P1

### EXC-01 — `java.security.KeyException` used as a domain exception *(not fixed)*

**Files:** `Bugemon.java` (`editStat`), `EffectManager.java` (`applyEffect`, test
class `TestEffectManager`)

**Verified in source:** `EffectManager.applyEffect()` declares `throws KeyException`
and `TestEffectManager.testApplyInvalidEffect()` asserts that `KeyException` is thrown
for an invalid target.

`java.security.KeyException` is a cryptographic API exception. Using it to signal an
invalid stat key or unhandled effect target has zero semantic relationship to the Java
Security API. Any developer reading `throws KeyException` will assume cryptographic
operations are involved.

**Fix:** Replace with `IllegalArgumentException` (unchecked) or a custom
`InvalidStatException` in the `ulb.models` package. Update the test accordingly.

---

### EXC-02 — `e.getStackTrace()` passed to `System.err.println` prints garbage *(not fixed)*

**File:** `src/main/java/ulb/models/combat/EffectManager.java`

**Verified in source:** Two occurrences:

```
// In applyEffect():
System.err.println(exception.getStackTrace());  // prints [Ljava.lang.StackTraceElement;@...

// In handleEffect():
System.err.println(e.getStackTrace());           // same
```

`getStackTrace()` returns a `StackTraceElement[]`. Passing an array to `println`
invokes `Object.toString()`, producing something like
`[Ljava.lang.StackTraceElement;@5f4da5c3` — completely useless for debugging.

**Fix:** Use `e.printStackTrace(System.err)` or `e.getMessage()`.

---

### EXC-03 — `LevelUpController` fields are package-private *(NEW)*

**File:** `src/main/java/ulb/controllers/LevelUpController.java`

**Verified in source:**

```
// CURRENT (broken)
List<LevelUp> levelUps;   // no modifier → package-private
int currentIdx;           // no modifier → package-private
```

Any class in `ulb.controllers` can read or mutate these fields directly, bypassing
the controller's encapsulation. If another controller accidentally modifies `currentIdx`,
the level-up flow will skip or repeat choices silently.

**Fix:** Add `private` to both field declarations.

---

### EXC-04 — `LevelUpDTO.getChoices()` leaks model type `Choice` into the DTO layer *(NEW)*

**File:** `src/main/java/ulb/common/LevelUpDTO.java`

**Verified in source:**

```
// CURRENT (broken)
import ulb.models.level_up.Choice;

public interface LevelUpDTO {
    BugemonDTO getBugemon();
    List<Choice> getChoices();    // model class in a DTO interface
}
```

`Choice` is a model class in `ulb.models.level_up`. Referencing it in `ulb.common`
couples the DTO interface directly to the model package, defeating the purpose of the
DTO abstraction that `BugemonDTO` correctly establishes.

**Fix:** Either introduce a `ChoiceDTO` interface in `ulb.common` mirroring
`BugemonDTO`, or (simpler) move `Choice` itself to `ulb.common` since it is already
a plain data object with no model dependencies.

---

### EXC-05 — `applyChoice()` modifies `state.hp` but not `state.maxHp` *(NEW)*

**File:** `src/main/java/ulb/models/bugemon/Bugemon.java`

**Verified in source:**

```
// CURRENT (broken)
public void applyChoice(Choice choice){
    this.state.hp += choice.getBonusHP();       // current HP goes up
    this.state.attack += choice.getBonusAttack();
    this.state.defense += choice.getBonusDefense();
    this.state.initiative += choice.getBonusInitiative();
    // state.maxHp is NOT updated → HP bar will show e.g. 110/100
}
```

A level-up HP bonus increases `state.hp` but leaves `state.maxHp` unchanged,
producing an invalid state where current HP exceeds max HP. Any HP-bar rendering
based on `getHp() / getMaxHp()` will overflow.

Additionally, `initialState` is never updated, so the next `reset()` call will
discard all level-up bonuses permanently (see DES-06).

**Fix:** Update both `state.maxHp` and `initialState.maxHp` (and all other affected
stats in `initialState`) inside `applyChoice()`.

---

## 4. Design & Architecture Issues — P1

### DES-01 — Type-effectiveness logic duplicated between `CombatHelper` and `CombatController` *(not fixed)*

**Files:** `CombatHelper.java` (`compareBType`, `Efficiency` enum),
`CombatController.java` (`isAttackEfficient`, `STRONG_AGAINST` map, `AttackEfficiency`
enum)

**Verified in source:** `CombatController` declares a private `STRONG_AGAINST` map,
an `AttackEfficiency` enum, and an `isAttackEfficient()` method that reimplements the
same type-matchup logic already present in `CombatHelper.compareBType()`. Two parallel
type enums (`Efficiency` vs `AttackEfficiency`) must now be kept in sync. If a new
type is added, both implementations must be updated independently.

**Fix:** Remove `isAttackEfficient()`, `STRONG_AGAINST`, and `AttackEfficiency` from
`CombatController`. Delegate to `CombatHelper.compareBType()` and map the
`CombatHelper.Efficiency` result to a display string in the view layer only.

---

### DES-02 — `ManualCombat` shadows parent trainer fields *(not fixed)*

**File:** `src/main/java/ulb/models/combat/ManualCombat.java`

**Verified in source:**

```
// CURRENT (broken)
public class ManualCombat extends Combat {
    private ManualTrainer allyTrainer;       // duplicated from Combat (now private in parent)
    private AutoTrainer adversaryTrainer;    // duplicated from Combat (now private in parent)

    public ManualCombat(ManualTrainer allyTrainer, AutoTrainer adversaryTrainer) {
        super(allyTrainer, adversaryTrainer);     // stored in parent
        this.allyTrainer = allyTrainer;           // stored again locally
        this.adversaryTrainer = adversaryTrainer; // stored again locally
    }
}
```

Two copies of each trainer exist in memory. Any future reassignment of one copy would
not propagate to the other. Note: `Combat` fields are now correctly `private` (fixed
from previous review), which means `ManualCombat` cannot access them directly anyway —
the local duplicates exist purely to avoid casting the parent accessor.

**Fix:** Remove the duplicate local fields and use typed private accessor methods:

```
// FIXED
private ManualTrainer getAlly()      { return (ManualTrainer) super.getAllyTrainer(); }
private AutoTrainer   getAdversary() { return (AutoTrainer)   super.getAdversaryTrainer(); }
```

---

### DES-03 — `new Random()` instantiated on every call in `AutoTrainer` and `LevelUp` *(not fixed + regression)*

**Files:** `src/main/java/ulb/models/trainer/AutoTrainer.java`,
`src/main/java/ulb/models/level_up/LevelUp.java`

**Verified in source:**

```
// AutoTrainer.getRandomAttack() — BROKEN
public Attack getRandomAttack() {
    Random rand = new Random();   // new instance every call
    ...
}

// AutoTrainer.selectRandomBugemon() — BROKEN
public void selectRandomBugemon() {
    ...
    Random rand = new Random();   // new instance every call
}

// LevelUp.generateRandomChoice() — NEW REGRESSION
private Choice generateRandomChoice() {
    Random rand = new Random();   // new instance every call (3 times per LevelUp)
    ...
}
```

`TeamFactory` correctly declares `private static final Random RANDOM = new Random()`.
The other classes are inconsistent and wasteful.

**Fix:** Declare a single shared `private static final Random RAND = new Random()`
field in each class.

---

### DES-04 — `CombatHelper.compareBType()` allocates a new `List` on every call *(not fixed)*

**File:** `src/main/java/ulb/models/combat/CombatHelper.java`

**Verified in source:**

```
// CURRENT (wasteful)
public static Efficiency compareBType(BType offensiveType, BType defensiveType) {
    final List<BType> cycle = new ArrayList<BType>(List.of(BType.values())); // alloc every call
    ...
}
```

`BType.values()` returns a fixed-length array. Wrapping it in a new `ArrayList` on
every invocation is unnecessary allocation on the hot combat path.

**Fix:**

```
// FIXED
private static final List<BType> TYPE_CYCLE = List.of(BType.values());
```

---

### DES-05 — `Bugemon.State` is a non-static inner class *(not fixed)*

**File:** `src/main/java/ulb/models/bugemon/Bugemon.java`

**Verified in source:**

```
private class State { ... }  // non-static → holds an implicit reference to enclosing Bugemon
```

`State` does not access any member of its enclosing `Bugemon` instance. The implicit
outer reference wastes memory (one extra pointer per `State` instance, including
`initialState`) and prevents `State` from being instantiated independently in tests.

**Fix:** Declare as `private static class State`.

---

### DES-06 — `Bugemon.reset()` wipes level-up bonuses *(NEW)*

**File:** `src/main/java/ulb/models/bugemon/Bugemon.java`

**Verified in source:**

```
// CURRENT (broken)
public void reset() {
    this.state = new State(this.initialState);  // restores to pre-level-up snapshot
}
```

`initialState` is set once at construction and never updated. `reset()` is called by
`MetaController.resetTeam()`, which is called from `CombatVictoryController.cont()`
(BUG-06). Any level-up bonuses applied via `applyChoice()` are silently discarded.

The design requires a clear decision: either `reset()` should only restore HP/effects
(not XP and level), or `initialState` must be updated on each level-up to reflect the
new permanent baseline.

---

### DES-07 — `TeamFactory` is a static utility class with an implicit global dependency *(not fixed)*

**File:** `src/main/java/ulb/factory/TeamFactory.java`

`createRandomTeam()` requires the full Bugemon list at every call site, forcing both
`AutomaticCombatController` and `ManualCombatController` to call
`metaController.getAllBugemonsAvailable()` just to forward it to the factory.
The factory is also impossible to mock in tests, making combat controller tests
fragile.

**Fix:** Refactor `TeamFactory` into an injectable service (or pass the list via
constructor injection) so that tests can supply a controlled pool.

---

### DES-08 — `CombatHelper.calculateXP()` has hardcoded TODO values and an unused parameter *(NEW)*

**File:** `src/main/java/ulb/models/combat/CombatHelper.java`

**Verified in source:**

```
// CURRENT (stub)
public static int calculateXP(Trainer winner, Trainer loser) {
    int combatType = 1; // TODO: get actual combat type
    int floor = 1;      // TODO: get actual floor
    // long nAdversaries = loser.getTeam().stream()... // commented out
    long nAdversaries = 1;
    ...
    long xpWon = 30 * floor * combatType * nAdversaries;  // always 30
    ...
}
```

The `loser` parameter is received but completely unused (the only line that would have
used it is commented out). The formula always produces 30 XP regardless of difficulty
or team composition. The constant `30` appears with no named explanation.

**Fix:** Either remove the `loser` parameter until it is used, or implement the proper
formula. Extract the constant: `private static final int BASE_XP = 30;`.

---

## 5. Style & Encapsulation — P2

### STY-01 — `static` on `ManualTrainer.TAction` enum is redundant *(not fixed)*

**Verified in source:** `public static enum TAction { ... }`

`static` is implicitly applied to all nested enums in Java. The keyword is misleading
noise.

**Fix:** `public enum TAction { ... }`

---

### STY-02 — `Combat` fields now correctly `private` ✔ *(fixed)*

**Verified in source:** `Combat.java` now declares `private Trainer allyTrainer`,
`private Trainer adversaryTrainer`, and `private int turn` — all private with accessor
methods. This was flagged as not fixed in the previous review but has since been
corrected. *(No action required.)*

---

### STY-03 — Several new public methods lack Javadoc *(NEW)*

**Verified in source:** The following public methods have no Javadoc comment:

- `Bugemon.applyChoice(Choice)`
- `Bugemon.getParticipation()` / `setParticipation(boolean)`
- `CombatHelper.calculateXP(Trainer, Trainer)`
- `MetaController.setLevelUp(List<LevelUp>)`
- `LevelUpController.chooseOption(int)` — has a `@param` only, no method description
- `LevelUpController.setLevelUp(List<LevelUp>)` — no Javadoc at all
- `LevelUpController.cont()` — no Javadoc at all
- `LevelUpDTO` interface — no interface-level Javadoc

This is inconsistent with the rest of the codebase, which is generally well-documented.

**Fix:** Add Javadoc to all of the above.

---

### STY-04 — `Choice` fields are package-private *(NEW)*

**File:** `src/main/java/ulb/models/level_up/Choice.java`

**Verified in source:**

```
// CURRENT (broken)
int bonusHP, bonusAttack, bonusDefense, bonusInitiative;  // no 'private'
```

All four fields are accessible from any class in `ulb.models.level_up`. Public
getters already exist for all of them, making direct field access redundant and
dangerous.

**Fix:** Add `private` to all four field declarations.

---

### STY-05 — `BugemonDTO` references the nested `Bugemon.BType` *(not fixed)*

**File:** `src/main/java/ulb/common/BugemonDTO.java`

**Verified in source:** `Bugemon.BType getType();`

Exposing a nested type of the concrete implementation class in the DTO interface
creates a tight coupling between the interface and its implementor. Any code importing
`BugemonDTO` is forced to also import `Bugemon` just to use the return type of
`getType()`.

**Fix:** Promote `BType` to a top-level enum in `ulb.models.bugemon` (see PKG-01).

---

### STY-06 — `Combat.isFinished()` unnecessary `if/return` pattern *(not fixed)*

**File:** `src/main/java/ulb/models/combat/Combat.java`

**Verified in source:**

```
// CURRENT (verbose)
public boolean isFinished() {
    if (allyTrainer.isDefeated() || adversaryTrainer.isDefeated()) {
        return true;
    }
    return false;
}
```

**Fix:**

```
// FIXED
public boolean isFinished() {
    return allyTrainer.isDefeated() || adversaryTrainer.isDefeated();
}
```

---

### STY-07 — `showSwitchMenu()` has a stale `// TODO: change that` comment *(NEW)*

**File:** `src/main/java/ulb/controllers/combat/ManualCombatController.java`

**Verified in source:**

```
public void showSwitchMenu() {
    // TODO: change that
    List<BugemonDTO> bugemonList = ...
```

The implementation looks reasonable. If there is a known improvement, it should be
described specifically. If not, the comment should be removed to avoid confusion.

---

### STY-08 — `AutomaticCombatController` calls `combat.incrementTurn()` redundantly *(NEW, found during review)*

**File:** `src/main/java/ulb/controllers/combat/AutomaticCombatController.java`

**Verified in source:**

```
Attack allyAttack = combat.turn();
Trainer winner = combat.getWinner();
combat.incrementTurn();        // ← redundant: AutomaticCombat.turn() does NOT call incrementTurn
```

`AutomaticCombat.turn()` does **not** call `incrementTurn()` internally — unlike
`ManualCombat.turn()` which does call `incrementTurn()` at the end. The
`AutomaticCombatController` therefore correctly calls `incrementTurn()` after each
turn. However, this is an asymmetry between the two combat types: `ManualCombat`
self-increments while `AutomaticCombat` relies on the controller to do it. This
inconsistency is a maintenance hazard and should be unified: either both combat types
call `incrementTurn()` internally, or neither does.

---

## 6. Test Coverage & Quality — P1–P2

### TEST-01 — `TestEffectManager.testEffectDuration` will throw `ConcurrentModificationException`

**Verified in source:** `effectManager.update()` is called when the effect's duration
reaches 0. Since BUG-01 is not fixed, the second call to `effectManager.update()` in
the test (when the effect expires) will throw a `ConcurrentModificationException`.
The test is broken in production by the bug it was designed to test.

---

### TEST-02 — No `TestManualCombat` class *(not fixed)*

`ManualCombat` is the most complex and bug-prone class in the codebase (BUG-02 lives
there). There is no dedicated test class. `TestAutomaticCombat` covers only the
automatic path.

---

### TEST-03 — `TestAutomaticCombat.testApplyDamage` is fragile *(not fixed)*

**Verified in source:**

```
assertTrue(finalHp1 < initialHp1);
assertTrue(finalHp2 < initialHp2);
```

If by chance both combatants reduce each other to ≤ 0 HP in a single turn, the
trainer may automatically switch to a new Bugemon with full HP before the assertion
fires, producing a false failure. The test should assert that damage was dealt to the
*specific* initial Bugemon, not that the current Bugemon's HP decreased.

---

### TEST-04 — `TestLevelUp.testBugemonExperienceAndLevelProperties` masks BUG-05 *(NEW)*

**Verified in source:** The test adds 150 XP in a single call for the level 2→3
transition. This happens to satisfy both the 100-XP formula (`default` branch) and
the 150-XP explicit case. It does **not** detect the inconsistency.

A proper boundary-value test would add exactly 149 XP and assert the Bugemon is still
level 2, which would expose the discrepancy between the two branches.

---

### TEST-05 — No test for `applyChoice()` / `reset()` interaction *(NEW)*

`applyChoice()` is exercised indirectly by `TestLevelUp`, but there is no test
verifying that:
- HP bonuses from `applyChoice()` are reflected in `getMaxHp()` (they are not —
  EXC-05).
- Calling `reset()` after `applyChoice()` reverts to initial stats, exposing DES-06.
- `state.maxHp` remains consistent with `state.hp` after a choice is applied.

---

### TEST-06 — `TestParser` — improvement noted, old index-based tests replaced *(partially fixed)*

**Verified in source:** `testBugemonParsing` and `testParseWithInputStreams` now use
`.filter(b -> "Florachu".equals(b.getName())).findFirst()` lookups instead of index
positions. `testAttackParsing` also uses name-based filtering. This issue is now
largely resolved. ✔

---

### TEST-07 — Missing tests for new features *(NEW)*

The following scenarios have no test coverage:

- `LevelUpController.chooseOption()` — applying a choice and advancing to the next
  level-up in the queue.
- `CombatHelper.calculateXP()` — including the division-by-zero edge case (BUG-07)
  and the case where multiple Bugemons participated.
- `Bugemon.restoreHp()` — verifying that only HP is restored, not XP, level, or other
  stats.
- `ManualCombatController` interaction tests — the entire manual combat UI flow has no
  test coverage.
- `CombatVictoryController.cont()` / level-up flow integration — no end-to-end test
  verifying XP → level-up → choice → reset chain.
- `Bugemon.applyChoice()` with `maxHp` consistency check.

---

## 7. Packaging & Architecture

### PKG-01 — Promote `Bugemon.BType` to a top-level enum *(not fixed)*

`BType` is referenced in `BugemonDTO`, `Attack`, `Effect`, `CombatHelper`,
`CombatController`, and the views. Having it nested inside `Bugemon` means all these
classes have a compilation dependency on `Bugemon` just to use a type enum.

**Fix:** Move to `ulb/models/bugemon/BType.java` as a standalone top-level enum.

---

### PKG-02 — `ulb.common` package is growing inconsistently *(NEW)*

`ulb.common` now contains `BugemonDTO`, `LevelUpDTO`, and `package-info.java`.
`LevelUpDTO` imports `Choice` from the model layer (EXC-04), breaking the clean
read-only DTO contract that `BugemonDTO` establishes.

**Fix:** Either promote `Choice` to `ChoiceDTO` in `ulb.common`, or document
explicitly which model types are permitted in DTOs and enforce it via a package-level
`@NonNullApi` or a custom Checkstyle rule.

---

### PKG-03 — Test utilities correctly in `ulb.utils.test` ✔ *(fixed)*

`TestUtilsBugemons`, `TestUtilsBugemonTeam`, and `TestUtilsTrainer` were moved from
`ulb.utils` to `ulb.utils.test`. No action required.

---

## 8. CI / Build

### CI-01 — CI pipeline image is correct ✔ *(fixed)*

**Verified in `.gitlab-ci.yml`:** The pipeline now uses
`image: maven:3.9.9-eclipse-temurin-21`. The previous release-candidate image concern
is resolved.

---

### CI-02 — Mixed JUnit 4 and JUnit 5 without the vintage engine *(not fixed)*

**Verified in `pom.xml`:** The project declares both `junit:junit:4.13.1` (JUnit 4)
and `junit-bom:5.11.0` (JUnit 5) in `dependencyManagement`. All test files use
`org.junit.Test` (JUnit 4 annotation) with `org.junit.Assert` (JUnit 4 assertions),
but the BOM imports `junit-jupiter-api`. Without the `junit-vintage-engine` on the
classpath, JUnit 4 tests may silently not run under the JUnit Platform runner used by
Surefire ≥ 2.22.

**Fix (option A — add vintage engine):**

```
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <scope>test</scope>
</dependency>
```

**Fix (option B — migrate to pure JUnit 5):** Replace all `@Test` from
`org.junit.Test` with `@Test` from `org.junit.jupiter.api`, replace
`org.junit.Assert.*` with `org.junit.jupiter.api.Assertions.*`, and remove the
`junit:junit:4.13.1` dependency.

---

## 9. Design-Pattern Opportunities

This section identifies concrete design patterns that would directly resolve or
prevent the bugs and design issues identified above. Each entry links back to the
relevant finding.

---

### PAT-01 — Strategy pattern for XP level-up thresholds *(addresses BUG-05)*

**Problem:** `Bugemon.addXp()` uses a hard-coded `switch/case` with inconsistent
thresholds. Adding a new progression curve (e.g., quadratic, configurable from JSON)
requires modifying `Bugemon` itself.

**Pattern:** Extract a `LevelUpStrategy` interface with a single method
`int xpRequired(int level)`. Provide concrete implementations such as
`LinearLevelUpStrategy` and `QuadraticLevelUpStrategy`. Inject the strategy into
`Bugemon` at construction time (or via the `Builder`).

```
// Strategy interface
public interface LevelUpStrategy {
    int xpRequired(int level);
}

// Default linear implementation
public class LinearLevelUpStrategy implements LevelUpStrategy {
    @Override
    public int xpRequired(int level) {
        return 50 * level;   // level 1 → 50, level 2 → 100, …
    }
}

// Usage in Bugemon
public Optional<LevelUp> addXp(int xp) {
    this.state.xp += xp;
    if (this.state.xp >= this.levelUpStrategy.xpRequired(this.state.level)) {
        return Optional.of(this.levelUp());
    }
    return Optional.empty();
}
```

**Benefits:**
- Eliminates the inconsistent `switch/case` entirely (BUG-05).
- Makes threshold logic independently unit-testable without instantiating a `Bugemon`.
- Allows different Bugemon species to have different leveling curves.
- Trivially configurable from JSON by loading strategy parameters at parse time.

---

### PAT-02 — Template Method pattern for combat damage resolution *(addresses BUG-04)*

**Problem:** Both `AutomaticCombat` and `ManualCombat` hard-code `attack.getPower()`
for damage instead of calling `CombatHelper.calculateDamage()`. The base class
`Combat` could enforce a shared damage calculation contract.

**Pattern:** Define an abstract `applyDamageHelper(Trainer attacker, Trainer defender,
Attack attack)` in `Combat` that calls `CombatHelper.calculateDamage()` by default.
Subclasses only override it if they need custom pre/post-processing (e.g., logging
the damage to the view).

```
// In Combat (base class) — the "template method"
protected void applyDamageHelper(Trainer attacker, Trainer defender, Attack attack) {
    double damage = CombatHelper.calculateDamage(
        attack,
        attacker.getCurrentBugemon(),
        defender.getCurrentBugemon()
    );
    defender.takeDamage((int) damage);
    // call effectManager.update() here if held as a Combat field
    if (!defender.isDefeated() && !defender.isCurrentBugemonAlive()) {
        handleFaintedBugemon(defender);
    }
}

// handleFaintedBugemon can be abstract or have a default implementation
protected abstract void handleFaintedBugemon(Trainer trainer);
```

**Benefits:**
- Wires `CombatHelper.calculateDamage()` into combat automatically (fixes BUG-04).
- Removes duplicate damage-application logic from both subclasses.
- Makes it impossible for a new combat type to accidentally use raw `getPower()`.

---

### PAT-03 — Command pattern for player actions *(addresses DES-02 and improves testability)*

**Problem:** `ManualTrainer.TAction` combined with the `switch` in `ManualCombat.turn()`
is the classic anti-pattern that the Command pattern solves. Adding a new action
(e.g., use item) requires adding both a new enum constant and a new `case` branch.
`ManualCombatController` calls `player.selectAction(TAction.ATTACK)` as a two-step
side-effecting procedure rather than passing a self-contained action object.

**Pattern:** Define a `CombatAction` interface with a single `execute()` method.
Implement `AttackAction`, `SwitchAction`, and `ForfeitAction` as concrete commands.
The controller constructs the appropriate command and passes it to `ManualCombat.turn(CombatAction)`.

```
public interface CombatAction {
    /** Executes the action and returns the winner, or null if combat continues. */
    Trainer execute(ManualCombat combat);
}

public class AttackAction implements CombatAction {
    private final Attack attack;
    public AttackAction(Attack attack) { this.attack = attack; }

    @Override
    public Trainer execute(ManualCombat combat) {
        combat.applyDamage(attack);
        return combat.getWinner();
    }
}

public class SwitchAction implements CombatAction {
    private final Bugemon target;
    public SwitchAction(Bugemon target) { this.target = target; }

    @Override
    public Trainer execute(ManualCombat combat) {
        combat.switchBugemon(target);
        return null;
    }
}

// ManualCombat.turn() becomes:
public Trainer turn(CombatAction action) {
    return action.execute(this);
}
```

**Benefits:**
- Each action is independently testable without a full combat setup.
- Adding a new action (use item, run) requires only a new class, no `switch` modification.
- Eliminates the `getSelectedAction()` / `selectAction()` mutable state on `ManualTrainer`.
- The controller is simplified: it constructs the command and submits it.

---

### PAT-04 — Observer / JavaFX Property binding for the combat UI *(addresses STY-08 and manual update calls)*

**Problem:** The controller calls `updateCombatView()` manually after every action.
If a future refactor adds a new state-changing operation and forgets to call
`updateCombatView()`, the UI will silently fall out of sync. The same problem applies
to HP bars, level indicators, and XP bars.

**Pattern:** Expose `Bugemon` stats as JavaFX `IntegerProperty` / `StringProperty`
objects. Bind the view's labels and progress bars directly to these properties.
The controller no longer needs to call any `update*` method for stat changes.

```
// In Bugemon
private final IntegerProperty hpProperty = new SimpleIntegerProperty();

public IntegerProperty hpProperty() { return hpProperty; }

public void takeDamage(int dmg) {
    this.state.hp = Math.max(0, this.state.hp - dmg);
    this.hpProperty.set(this.state.hp);    // view updates automatically
}

// In the view (FXML controller)
hpBar.progressProperty().bind(
    bugemon.hpProperty().divide(bugemon.maxHpProperty())
);
```

**Benefits:**
- The view is always in sync with the model with zero manual update calls.
- Removes the asymmetry between `AutomaticCombat` (controller-incremented turn) and
  `ManualCombat` (self-incremented turn) — both just mutate the model and the view
  reacts automatically.
- Dramatically simplifies all `updateCombatView()` methods.
- Makes it easy to add animated HP transitions (bind to a `Timeline` interpolator).

---

### PAT-05 — Factory Method / Abstract Factory for `Combat` instantiation *(addresses DES-07)*

**Problem:** `TeamFactory.createRandomTeam()` is a static method that forces all
controllers to call `metaController.getAllBugemonsAvailable()` just to forward the
list. Controllers are tightly coupled to the factory's static API and cannot be tested
without a fully initialized `MetaController`.

**Pattern:** Introduce a `CombatFactory` interface with two factory methods:

```
public interface CombatFactory {
    AutomaticCombat createAutoCombat(BugemonTeam playerTeam);
    ManualCombat    createManualCombat(BugemonTeam playerTeam);
}

public class RandomOpponentCombatFactory implements CombatFactory {
    private final List<Bugemon> pool;
    public RandomOpponentCombatFactory(List<Bugemon> pool) { this.pool = pool; }

    @Override
    public AutomaticCombat createAutoCombat(BugemonTeam playerTeam) {
        BugemonTeam opponentTeam = TeamFactory.createRandomTeam(pool, playerTeam.size());
        return new AutomaticCombat(new AutoTrainer(playerTeam), new AutoTrainer(opponentTeam));
    }
    // ...
}
```

Inject `CombatFactory` into the combat controllers. In tests, inject a
`FixedOpponentCombatFactory` with a deterministic team.

**Benefits:**
- Controllers no longer need to call `metaController.getAllBugemonsAvailable()`.
- Combat controller unit tests can inject a mock factory without a `MetaController`.
- Adding new combat modes (e.g., story-driven fixed opponents) requires only a new
  factory implementation.

---

### PAT-06 — Memento pattern for `Bugemon` state persistence across resets *(addresses DES-06, EXC-05)*

**Problem:** `Bugemon.initialState` is a single snapshot taken at construction. After
`applyChoice()`, this snapshot is stale, and `reset()` silently rolls back all
level-up gains. There is no clean way to distinguish "restore HP for battle" from
"restore to truly initial state".

**Pattern:** The Memento pattern separates the concept of a restorable snapshot from
the current mutable state. Define two explicit memento types:

```
public record BattleMemento(int hp) {}                  // restore between fights
public record ProgressMemento(int level, int xp,
    int maxHp, int attack, int defense, int initiative) {} // permanent progress

// In Bugemon:
public BattleMemento saveBattleState()   { return new BattleMemento(state.hp); }
public void restoreBattleState(BattleMemento m) { state.hp = m.hp(); }

public ProgressMemento saveProgress()   { return new ProgressMemento(...); }
public void applyProgress(ProgressMemento m) { /* update state and initialState */ }
```

`MetaController.resetTeam()` calls `restoreBattleState()` (HP only). The level-up
system produces a `ProgressMemento` and calls `applyProgress()`, which updates both
`state` and `initialState`.

**Benefits:**
- Makes the distinction between "restore HP" and "restore to factory defaults"
  explicit at the API level.
- Eliminates the `reset()` / `applyChoice()` interaction bug (DES-06, EXC-05).
- The `Caretaker` role (e.g., `MetaController`) stores mementos without needing to
  know `Bugemon` internals.

---

## 10. Positive Observations

- **Test utilities refactored into `ulb.utils.test`** — clean separation between
  production and test code is now enforced.
- **`TestManualTrainer` added** — covers `selectBugemon` and the dead-Bugemon guard.
- **`LevelUp` / `Choice` / `LevelUpController` / `LevelUpView`** — the entire
  level-up feature is implemented end-to-end with a dedicated test class
  (`TestLevelUp`), a DTO interface (`LevelUpDTO`), and proper wiring through
  `MetaController.setLevelUp()`.
- **XP calculation moved to `CombatHelper.calculateXP()`** — business logic correctly
  placed in the model layer instead of the controller.
- **`participation` flag** — correctly tracks which Bugemons fought, wired into both
  `AutomaticCombat.turn()` and `ManualCombat.turn()`, used by the XP distribution
  logic.
- **`Bugemon.addXp()` returns `Optional<LevelUp>`** — excellent use of `Optional` to
  signal a level-up without null or exceptions.
- **`Bugemon.restoreHp()`** — clean separation between full `reset()` and HP-only
  restoration.
- **`Combat` fields are now `private`** — fixed since the previous review.
- **CI pipeline uses a stable Maven image** — `maven:3.9.9-eclipse-temurin-21`.
  Fixed since the previous review.
- **`TestParser` lookups are now ID/name-based** — index fragility resolved.
- **Builder pattern** — `Bugemon.Builder` is consistently used everywhere, including
  in all test utilities.
- **Consistent Javadoc** — the vast majority of the public API is well-documented.
- **`CombatHelper`** — the static utility is cleanly separated, well-tested
  (`TestCombatHelper`), and the new `calculateXP` fits naturally.

---

## 11. Prioritised Action Plan

### 🔴 P0 — Fix immediately (crash / completely broken feature)

| ID | Item | File(s) |
|----|------|---------|
| BUG-01 | `EffectManager.update()` → `ConcurrentModificationException` on effect expiry | `EffectManager.java` |
| BUG-02 | `ManualCombat.applyDamage()` → dead Bugemon never switched | `ManualCombat.java` |
| BUG-03 | String `!=` comparison in both combat controllers | `AutomaticCombatController.java`, `ManualCombatController.java` |
| BUG-04 | `EffectManager` and `CombatHelper.calculateDamage()` never wired into combat | `AutomaticCombat.java`, `ManualCombat.java` |
| BUG-05 | `addXp()` XP thresholds inconsistent between explicit cases and `default` formula | `Bugemon.java` |
| BUG-06 | `CombatVictoryController.cont()` bypasses level-up flow and wipes XP progress | `CombatVictoryController.java` |
| BUG-07 | `CombatHelper.calculateXP()` division by zero when no Bugemon participated | `CombatHelper.java` |
| BUG-08 | `Parser` returns `null` on failure; downstream `NullPointerException` | `Parser.java` |

### 🟠 P1 — Important (next sprint)

| ID | Item | File(s) |
|----|------|---------|
| EXC-01 | Replace `KeyException` with `IllegalArgumentException` | `Bugemon.java`, `EffectManager.java` |
| EXC-02 | Fix `e.getStackTrace()` passed to `println` | `EffectManager.java` |
| EXC-03 | Make `LevelUpController.levelUps` and `currentIdx` `private` | `LevelUpController.java` |
| EXC-04 | Remove model type `Choice` from `LevelUpDTO` interface | `LevelUpDTO.java` |
| EXC-05 | `applyChoice()` must update `state.maxHp` and `initialState` | `Bugemon.java` |
| DES-01 | Remove duplicated type-effectiveness logic from `CombatController` | `CombatController.java` |
| DES-02 | Remove duplicate trainer fields from `ManualCombat` | `ManualCombat.java` |
| DES-06 | Decide `reset()` semantics: preserve level/XP or reset to initial | `Bugemon.java`, `MetaController.java` |
| DES-08 | Remove unused `loser` param from `calculateXP()` or implement the formula | `CombatHelper.java` |
| TEST-01 | Fix `TestEffectManager.testEffectDuration` (blocked by BUG-01) | `TestEffectManager.java` |
| TEST-02 | Add `TestManualCombat` | *(new file)* |
| TEST-05 | Add tests for `applyChoice()` + `reset()` + `maxHp` interaction | *(new tests)* |
| TEST-07 | Add tests for `LevelUpController`, `calculateXP()`, `restoreHp()` | *(new tests)* |

### 🟡 P2 — Cleanup & improvements (ongoing)

| ID | Item | File(s) |
|----|------|---------|
| DES-03 | Share `Random` instance in `AutoTrainer` and `LevelUp` | `AutoTrainer.java`, `LevelUp.java` |
| DES-04 | Cache `TYPE_CYCLE` in `CombatHelper.compareBType()` | `CombatHelper.java` |
| DES-05 | Make `Bugemon.State` a `static` inner class | `Bugemon.java` |
| DES-07 | Refactor `TeamFactory` to an injectable service | `TeamFactory.java` |
| STY-01 | Remove redundant `static` from `TAction` enum | `ManualTrainer.java` |
| STY-03 | Add Javadoc to all new public methods | multiple files |
| STY-04 | Make `Choice` fields `private` | `Choice.java` |
| STY-05 | Promote `Bugemon.BType` to top-level enum | `Bugemon.java` → `BType.java` |
| STY-06 | Simplify `isFinished()` to a single `return` expression | `Combat.java` |
| STY-07 | Remove stale `// TODO: change that` from `showSwitchMenu()` | `ManualCombatController.java` |
| STY-08 | Unify `incrementTurn()` calling convention between combat types | `AutomaticCombat.java`, `ManualCombat.java`, `AutomaticCombatController.java` |
| PKG-01 | Promote `BType` to a top-level file | `Bugemon.java` |
| PKG-02 | Decide `ulb.common` DTO boundary policy for `Choice` | `LevelUpDTO.java` |
| TEST-03 | Fix fragile `testApplyDamage` assertion | `TestAutomaticCombat.java` |
| TEST-04 | Add boundary-value XP tests for levels 2, 3, 4 | `TestLevelUp.java` |
| CI-02 | Add `junit-vintage-engine` or migrate all tests to JUnit 5 | `pom.xml` |
| PAT-01 | Strategy for XP thresholds | `Bugemon.java` + new strategy classes |
| PAT-02 | Template Method for combat damage | `Combat.java` |
| PAT-03 | Command pattern for player actions | `ManualCombat.java`, `ManualCombatController.java` |
| PAT-04 | JavaFX Property binding for combat UI | `Bugemon.java`, combat views |
| PAT-05 | Factory Method for combat instantiation | new `CombatFactory` interface |
| PAT-06 | Memento for Bugemon state persistence | `Bugemon.java`, `MetaController.java` |
