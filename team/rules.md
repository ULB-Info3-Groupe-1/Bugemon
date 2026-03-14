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

Si nouvelle fonctionnalité : `feat/nom_fonctionnalite`
Si fix : `fix/fix_en_question`

### Merge branche

Avant de merge :

```bash
git switch ma-branche
git fetch origin
git rebase origin/branche-principale
```

Puis :

```bash
git switch branche-principale
git merge ma-branche
```

### Workflow Git

- Ne jamais commit directement sur main. On ne merge que sur main à la fin de chaque itération
- Branche principale en dehors de `main` : `development`
- Toujours passer par une branche

### Annuler son dernier commit local

```bash
git reset --soft HEAD~1
```

### Issues

#### Contexte

Description rapide du problème ou du besoin.

#### Objectif

Ce que l’on veut obtenir concrètement.

#### Tâches

- [ ] Étape 1
- [ ] Étape 2
- [ ] Étape 3

#### Critères d’acceptation

- Condition 1
- Condition 2

### Merge request

#### Choix des branches à merge et titre de la MR

Nom de la branche:
feature/nom-court
bugfix/nom-court
refactor/nom-court

Titre de la MR:
Même format que l’issue:
[TYPE] Description courte

#### Changements effectués

- Modification 1
- Modification 2

#### Type de modification

- [ ] Feature
- [ ] Bug fix
- [ ] Refactor
- [ ] Documentation

#### Vérifications

- [ ] Le code compile
- [ ] Les tests passent
- [ ] Pas de code mort ajouté
- [ ] Relecture effectuée

## Code

### Formatage

Toujours formater le code avant de commit :

```sh
find src -name "*.java" | xargs clang-format -i
```

### Langue

Tous les commentaires, nom de méthodes, etc se font en anglais.

### Documentation

On document toutes les méthodes avec la **Javadoc** :

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
- Nom des tests : shouldDoSomething_whenCondition
- Les tests doivent passer avant chaque commit

### Structure

- Architecture MVC (model / view / controller)
- Un fichier = une classe
- Packages en minuscules (ex: com.project.service)
- Nom de fichier java commance en MAJUSCULE (ex: Bugemon.java)

### Naming

- Classes : PascalCase → UserService
- Méthodes : camelCase → getUserById
- Variables : camelCase → userName
- Constantes : UPPER_CASE → MAX_SIZE
- Packages : lowercase → com.project.app

### Bonnes pratiques

- Pas de duplication de code
- Méthodes courtes (< 30 lignes si possible)
- Une seule responsabilité par classe
