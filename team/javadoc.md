# Guide Javadoc

La Javadoc documente l'**intention**, pas l'implémentation.
Ce guide précise quand écrire de la Javadoc, comment la structurer, et quels tags utiliser.

---

## Principe fondamental

Une Javadoc utile répond à la question : *« Qu'est-ce que ce code garantit ou exige ? »*
Elle ne décrit pas ce que le code fait ligne par ligne — le code lui-même le fait déjà.

```java
// Mauvais — paraphrase le code
/**
 * Iterates over the list and returns the first bugemon whose id matches.
 */
public Optional<Bugemon> findById(String id) { ... }

// Bon — exprime le contrat
/**
 * Returns the first bugemon matching {@code id}, or empty if none exists.
 */
public Optional<Bugemon> findById(String id) { ... }
```

---

## Quand écrire de la Javadoc

### Obligatoire

- Méthodes `public` des **services** et **repositories**
- Toute méthode dont le comportement n'est pas évident à la lecture de son nom et de ses paramètres
- Contrainte implicite sur un paramètre (`@param` si non trivial)
- Exception métier lancée (`@throws`)
- Classe non triviale (une phrase d'intention suffit)

### Inutile

- Getters, setters, constructeurs triviaux
- `@Override` dont le comportement est identique au contrat du parent
- Méthodes privées sauf logique métier dense
- Classes de tests
- Records et classes de données simples

```java
// Inutile — le nom suffit
public String getName() { ... }

// Inutile — comportement identique au parent
@Override
public String toString() { return this.name; }

// Utile — contrainte implicite sur le retour
/**
 * Calculates damage dealt, applying type effectiveness and defense reduction.
 *
 * @return damage value, always >= 1
 */
public int calculateDamage(Bugemon attacker, Bugemon defender) { ... }
```

---

## Structure d'une Javadoc

### Classe / interface / enum

Une seule phrase décrivant le rôle. Pas de `<p>`, pas de `@see` en cascade, pas d'en-tête d'auteur.

```java
/** Represents a single turn's outcome, including damage dealt and effects applied. */
public record TurnResult(...) { }
```

Pour une classe plus complexe, deux phrases maximum :

```java
/**
 * Manages team persistence and retrieval.
 * Calls must be made on the JavaFX application thread.
 */
public class TeamRepository { ... }
```

### Méthode

Format standard :

```
/**
 * <phrase d'intention en une ligne>.
 *
 * <détail optionnel si nécessaire>.
 *
 * @param  nom   <contrainte ou rôle non trivial>
 * @return       <ce qui est garanti sur la valeur retournée>
 * @throws TypeException  <condition précise qui déclenche l'exception>
 */
```

La ligne de résumé (première phrase) doit tenir sur une ligne et se terminer par un point.
Elle doit être au présent à la troisième personne : *« Returns… »*, *« Computes… »*, *« Throws if… »*.

---

## Tags

### `@param`

À n'utiliser que si le paramètre a une **contrainte ou un rôle non évident**.
Ne pas documenter un paramètre dont le nom est déjà explicite.

```java
// Inutile
@param name the name

// Utile — contrainte non évidente
@param attacker must have initiative > 0
@param slots    must be between 1 and 6 inclusive
```

### `@return`

À utiliser si la valeur retournée a une **garantie non triviale** (borne, invariant, cas spécial).

```java
// Inutile
@return the player id

// Utile
@return damage dealt, always >= 1
@return empty if the team has no active members
```

### `@throws`

À utiliser pour les **exceptions métier** ou les préconditions non évidentes.
Ne pas documenter `RuntimeException` lancées par le JDK sur usage incorrect standard.

```java
// Utile
@throws IllegalStateException if no active run exists
@throws IllegalArgumentException if {@code level} is negative
```

### `@see`

À éviter sauf lien fort et non évident vers une autre classe.
Ne pas créer de chaînes de `@see` descriptives.

### `{@link}` et `{@code}`

- `{@link ClassName}` ou `{@link ClassName#method}` pour référencer un type ou une méthode depuis le texte.
- `{@code expression}` pour toute valeur littérale, constante, ou fragment de code dans le texte.

```java
/**
 * Resolves the next {@link Room} in the current {@link Floor}.
 *
 * @return {@code null} if the floor is already complete
 */
```

---

## `package-info.java`

Chaque package peut avoir un `package-info.java` qui documente le package dans son ensemble.
C'est le seul endroit pour documenter des décisions qui s'appliquent à tout le package et qui ne
appartiennent à aucune classe en particulier.

### Ce qui mérite d'y figurer

- La responsabilité globale du package en une phrase
- Les décisions architecturales transversales (ex : instanciation unique, découplage via DTO)
- Les invariants ou conventions qui s'appliquent à toutes les classes du package
- Les contraintes non-évidentes (ex : thread-safety, cycle de types, comportement non-déterministe)

### Ce qui n'y a pas sa place

- **La liste des classes du package** — elle duplique les noms de fichiers et se désynchronise à chaque renommage
- **La hiérarchie de classes** — chaque classe documente son propre rôle
- **Les `@see` en cascade** vers toutes les classes du package

```java
// Mauvais — liste de classes qui va se désynchroniser
/**
 * <h2>Class hierarchy</h2>
 * <ul>
 * <li>{@link Foo} — does foo.</li>
 * <li>{@link Bar} — does bar.</li>
 * </ul>
 */
package ulb.models.combat;

// Bon — décision architecturale non-évidente
/**
 * Turn-based combat system. Damage includes a 10% critical-hit chance (x1.5);
 * use the {@code criticFactor} overload in tests for reproducibility.
 *
 * Type cycle: FLORA → AQUA → PYRO → LITHO → FLORA.
 */
package ulb.models.combat;
```

---

## Ce qu'il ne faut jamais écrire

| À éviter | Raison |
|---|---|
| En-têtes `@author`, `@version`, `@date` | Git le fait mieux |
| `<p>` et HTML complexe | Alourdit sans apporter de valeur |
| Répéter le nom de la méthode dans le texte | Redondant |
| `@param` et `@return` vides ou évidents | Bruit sans information |
| Javadoc sur des méthodes privées simples | Audience = personne |
| Blocs multi-paragraphes sur des getters | Disproportionné |

---

## Exemples complets

### Bon exemple — service

```java
/**
 * Applies the selected upgrade to the given bugemon and persists the result.
 *
 * @throws IllegalStateException if {@code bugemon} has no pending level-up
 */
public void applyUpgrade(Bugemon bugemon, Upgrade upgrade) { ... }
```

### Bon exemple — repository

```java
/**
 * Returns all teams belonging to the current player, ordered by creation date.
 *
 * @return empty list if the player has no teams
 */
public List<BugemonTeam> findAllTeams() { ... }
```

### Bon exemple — classe utilitaire

```java
/** Deserializes {@link Bugemon} instances from the JSON resource files. */
public class BugemonDeserializer { ... }
```

### Mauvais exemple — sur-documentation

```java
/**
 * This class represents a bugemon.
 * It has a name, a type, and a list of attacks.
 * It can level up and gain experience points.
 *
 * @author Jean
 * @version 1.0
 * @see Attack
 * @see BugemonType
 */
public class Bugemon { ... }
```
