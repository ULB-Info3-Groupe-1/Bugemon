# Règles

## Git

### Commit de paire

```git
Titre du commit

Message du commit

Co-authored-by: Frédéric <prenom.nom@ulb.be>
```

Pour le faire :

```bash
git commit -m "Titre du commit" -m "Message du commit" -m "Co-authored-by: Frédéric <prenom.nom@ulb.be>"
```

### Nom de branche

Si nouvelle fonctionnalité : `feat/nom-fonctionnalite`
Si fix : `fix/fix-en-question`
Si refactor : `refactor/nom-court`

Si plusieurs mots dans la fonctionnalité ou le fix, séparer les mots par des tirets : `feat/ajout-fonctionnalite-x`

### Merge branche

Avant de merge :

```bash
git switch ma-branche
git fetch [origin]
git rebase origin/branche-principale
```

Ensuite :

```bash
git push origin ma-branche [--force]
```

Et créer une MR sur GitLab.

### Workflow Git

- Ne jamais commit directement sur main. On ne merge que sur main à la fin de chaque itération
- Branche principale en dehors de `main` : `development`
- Toujours passer par une branche

### Annuler son dernier commit local

```bash
git reset --soft HEAD~1
```

## Base de données & Requêtes SQL

### Structure Singleton pour la DB

Toute interaction avec la base de données doit passer par le Repository pattern.

### Formatage des requêtes SQL

Toutes les requêtes SQL complexes ou de base doivent être stockées **dans des fichiers `.sql` dédiés** dans le dossier `src/main/resources/sql/`.

Chaque requête **doit obligatoirement** être précédée de sa documentation sous le format exact :

```sql
-- Query to create an object x
-- NameOfRequest
SELECT ...
```

*Exemple d'un fichier `bugemon_queries.sql` :*

```sql
-- Query to create a team member
-- InsertTeamMember
INSERT INTO team_members (user_id, team_name, bugemon_id, slot_position) VALUES (?, ?, ?, ?);
```

## Issues

### Contexte

Description rapide du problème ou du besoin.

### Objectif

Ce que l'on veut obtenir concrètement.

### Tâches

- [ ] Étape 1
- [ ] Étape 2
- [ ] Étape 3

### Critères d'acceptation

- Condition 1
- Condition 2

## Merge request

### Choix des branches à merge et titre de la MR

Nom de la branche :

```
feat/nom-court
fix/nom-court
refactor/nom-court
```

Titre de la MR : même format que l'issue : `[TYPE] Description courte`

### Changements effectués

- Modification 1
- Modification 2

### Type de modification

- [ ] Feature
- [ ] Bug fix
- [ ] Refactor
- [ ] Documentation

### Vérifications

- [ ] Le code compile
- [ ] Les tests passent (`mvn test`)
- [ ] Le formatage est appliqué (`mvn spotless:apply`)
- [ ] Checkstyle passe sans erreur (`mvn checkstyle:check`)
- [ ] Pas de code mort ajouté
- [ ] Relecture effectuée

## Code

### Formatage

Toujours formater le code avant de commit avec Spotless :

```sh
mvn spotless:apply
```

Pour vérifier sans modifier :

```sh
mvn spotless:check
```

Spotless applique automatiquement :
- Le formatter Eclipse (`.eclipse-formatter.xml`, profil `projet-ulb`)
- La suppression des imports inutilisés
- L'ordre des imports : `java`, `javax`, `org`, `com`, `ulb`
- Une newline en fin de fichier
- La suppression des espaces en fin de ligne


### Checkstyle

Les règles Checkstyle sont définies dans `checkstyle.xml`. Les principales contraintes :

- Longueur de ligne max : **120 caractères**
- Pas de tabulations (espaces uniquement)
- Pas d'imports `*`
- Accolades obligatoires sur toutes les structures de contrôle
- Une instruction par ligne

Pour vérifier manuellement :

```sh
mvn checkstyle:check
```

### Langue

Tous les commentaires, noms de méthodes, etc. se font en anglais.

### Documentation

On documente toutes les méthodes avec la **Javadoc** :

```java
/**
 *
 * @param args
 */
```

Pas besoin de commenter les tests.

### Tests

- Chaque fonctionnalité doit être testée
- Utiliser des tests unitaires (JUnit)
- Nom des tests : `shouldDoSomething_whenCondition`
- Les tests doivent passer avant chaque commit

### Structure

- Architecture MVC (model / view / controller)
- Un fichier = une classe
- Packages en minuscules (ex: `com.project.service`)
- Nom de fichier Java commence en MAJUSCULE (ex: `Bugemon.java`)

### Naming

- Classes : PascalCase → `UserService`
- Méthodes : camelCase → `getUserById`
- Variables : camelCase → `userName`
- Constantes : UPPER_CASE → `MAX_SIZE`
- Packages : lowercase → `com.project.app`

### Bonnes pratiques

- Pas de duplication de code
- Méthodes courtes (< 80 lignes)
- Une seule responsabilité par classe
