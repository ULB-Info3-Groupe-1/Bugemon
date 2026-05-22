# INFOF-307 - Génie Logiciel et gestion de projets

```bash
██████╗ ██╗   ██╗ ██████╗ ███████╗███╗   ███╗ ██████╗ ███╗   ██╗
██╔══██╗██║   ██║██╔════╝ ██╔════╝████╗ ████║██╔═══██╗████╗  ██║
██████╔╝██║   ██║██║  ███╗█████╗  ██╔████╔██║██║   ██║██╔██╗ ██║
██╔══██╗██║   ██║██║   ██║██╔══╝  ██║╚██╔╝██║██║   ██║██║╚██╗██║
██████╔╝╚██████╔╝╚██████╔╝███████╗██║ ╚═╝ ██║╚██████╔╝██║ ╚████║
╚═════╝  ╚═════╝  ╚═════╝ ╚══════╝╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝
```

## Table des matières

- [INFOF-307 - Génie Logiciel et gestion de projets](#infof-307---génie-logiciel-et-gestion-de-projets)
  - [Table des matières](#table-des-matières)
  - [Description générale](#description-générale)
  - [Fonctionnalités principales](#fonctionnalités-principales)
  - [Prérequis](#prérequis)
  - [Auteurs](#auteurs)
  - [Liste du nombre de commits de chaque membre](#liste-du-nombre-de-commits-de-chaque-membre)
  - [Lancer le projet](#lancer-le-projet)
  - [Lancer les tests](#lancer-les-tests)
  - [Générer la documentation](#générer-et-lire-la-documentation)
  - [Générer le jar](#générer-le-jar)

## Description générale

Bugemon est un jeu vidéo de type RPG développé en Java avec JavaFX dans le cadre du cours INFOF-307. Le joueur incarne un dresseur de "Bugemons" et doit affronter des adversaires à travers une tour. Le jeu propose un système de combat au tour par tour, une gestion d'équipe, un inventaire d'objets, un arbre de compétences, et un système de sauvegarde utilisant une base de données.

## Fonctionnalités principales

- **Combats stratégiques** : Affrontez d'autres Bugemons avec un système de combat au tour par tour.
- **Gestion d'équipe** : Créez et personnalisez votre équipe de Bugemons (ajout, suppression).
- **Compétences** : Gagnez de l'expérience, montez de niveau et débloquez de nouvelles capacités via un arbre de compétences.
- **Gestion de l'inventaire** : Gagnez des objets à travers la tour et utilisez les dans vos combats.
- **Exploration** : Progressez à travers les différentes salles et étages de la tour pour obtenir de nouvelles récompenses.

## Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé les outils suivants sur votre machine :
- [Java JDK](https://www.oracle.com/java/technologies/downloads/) (version 21)
- [Maven](https://maven.apache.org/) (pour la gestion des dépendances et de la compilation)
- [Docker](https://www.docker.com/) (pour démarrer la base de données requise via le fichier `docker-compose.yml`)

## Auteurs

| Prénom   | Nom               | Matricule |
|----------|-------------------|-----------|
| Yasmina  | Bou Khaled        | 000587781 |
| Philippe | Brisbois          | 000575939 |
| Romain   | Dejean            | 000587614 |
| Martin   | Gouverneur        | 000586541 |
| Romain   | Liefferinckx      | 000591790 |
| Matteo   | Morbée            | 000549684 |
| Manuel   | Rocca             | 000596086 |
| Rares    | Radu Loghin       | 000590079 |
| Ethan    | Van Ruyskensvelde | 000589640 |
| Lucas    | Verbeiren         | 000591223 |

## Liste du nombre de commits de chaque membre

Pour vérifier le nombre de commits de chaque membre du groupe :

```bash
git shortlog -se --all
```

## Lancer le projet

```bash
mvn clean javafx:run
```

## Lancer les tests

Pour exécuter la suite de tests unitaires, utilisez la commande suivante :

```bash
mvn test
```

## Générer et lire la documentation

```bash
mvn javadoc:javadoc
```

## Générer le jar

```bash
mvn package
```

