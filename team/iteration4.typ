#let TODO = {
  text(blue)[TODO]
}

#set heading(numbering: "1.1")
#show link: underline

#set text(lang: "fr")

#title[Document de fin d'itération 2]

#outline()

= Gestion de projet <sec:gestion_projet>

== Rythme ralenti par les congés <sec:rhytme_ralenti>

Durant la semaine juste avant les congés, le rythme a fortement ralenti, et n'a pas vraiment repris pendant les congés.
Lors de la reprise, nous avions donc une quantité de travail supérieure à celle attendue.

== Fin d'itération difficile <sec:fin_difficile>

Quelques jours avant la deadline initiale, nous arrivions progressivement à la fin du projet, mais non sans difficultés.
En effet, le code ressemblait de plus en plus à un "spaghetti géant" : le code n'était pas bien pensé pour accueillir les nouvelles fonctionnalités de l'itération 4,
mais nous n'avions plus le temps d'entamer un refactoring au moment où nous en avons pris conscience.

C'est pourquoi, lorsque nous avons appris que la deadline était repoussée, nous avons immédiatement entamé un refactoring complet (cf. @sec:qualite_de_code).

Avec le recul, nous réalisons à présent que nous aurions vraisemblablement dû demander un refactoring à l'itération 3.
Effectuer le refactoring plus tôt aurait probablement permis de le rendre plus court que celui que nous avons dû réaliser pendant la période supplémentaire.

== Mauvaise division des tâches

Nous avions divisé l'histoire 17 (Arbre de compétences) en les tâches suivantes :
- logique pour appliquer les effets
- controller skill tree
- model skill tree (touche au service)
- vue skill tree

La personne s'occupant de la vue n'a pas lu la partie de l'histoire disant que les possitions des noeuds étaient déjà présentes dans le fichier `skill_tree.json`, ou du moins ne s'est pas souvenu
de cette information, et a par conséquent réimplémenté un algorithme permettant d'organiser les positions des noeuds pour rien.
(Nous sommes revenus en arrière pour utiliser les coordonnées dans le fichier, afin de respecter la demande du client.)

= Qualité de code <sec:qualite_de_code>

Afin de rendre le refactoring le plus efficace possible, nous nous sommes fortement aidés de LLMs (claude).
(Majoritairement pour comprendre nos mauvais choix de conceptions, et les erreurs de compréhensions de certains concepts tels que les services).

== Mauvais choix initiaux

Dans cette section, nous décrivons brièvement les mauvaise choix de conception que nous avions fait _avant le refactoring final_.

=== Services stateful

Quatre jours avant la remise, nous avons réalisé que nos services étaient beaucoup trop _stateful_ pour des services.
Par exemple, `BugemonService` stockait les `LevelUp`s pas  encore choisi. Ceci compliquait fortement son utilisation.

=== Service trop interdépendants

À de nombreuses reprises, des services étaient injectés à l'intérieur d'autres services.
Nous avons par la suite réalisé que ceci était généralement une mauvaise idée du au risque d'apparition de dépendance cyclique entre ceux-ci.

=== Un seul type de bugemon

Nous avions un seul type `Bugemon`, qui était utilisée partout. Celui-ci stockait toute les informations d'un bugemon (incluant notamment des champs `hp`, `niveau`).

Les bugemons "par défaut" était stockés dans une liste (dans le BugemonService). (Cette liste était obtenue grâce au repo que gérait le BugemonService.)

On utilisait ensuite le constructeur de copie de Bugemon pour créer les Bugemons du joueur/adversaire (à l'aide la sorte de cache décrit plus haut).

Nous avons réalisé que ce design est mauvais pour plusieurs raisons.
Notamment car, le niveau dépend du joueur, stocker ce dernier dans les Bugemons qui n'appartiennent à aucun joueur est donc mauvais.

Lorsque le joueur lancait un combat _standalone_ (pas tower), nous devions restaurer l'attribut hp des bugemons que le joueur jouait.

=== Gestion des actions en combat

Afin de récupérer les actions effectuées en combat, nous utilisions un objet `Trainer` par joueur.
Ceux-ci étaient concus pour être utilisé pendant un seul combat et possédaient notamment un field stockant une action et un autre pour l'équipe, et étaient passé en paramètre de `Combat`.

Le controller de combat passait les `Trainer`s au `Combat`.
A chaque tour, celui-ci mettait à jour l'action stockée dans le `Trainer` correspondant au joueur.
Puis, demandait aucombat d'effectuer le tour.
Le `Combat` demandait ensuite au `Trainer` les actions qu'ils stockaient.

Ce système est fragile, l'ownership n'est pas claire car le Combat et le Controller partage clairement le Trainer du joueur.

=== Création précoce de certains objets <sec:creation_precoce>

Plusieurs fois où on créait des objets plus tôt que nécessaire, rendant la gestion de ceux-ci inutilement complexe.

*Exemple :* dans le mode Tour NO, les `Room`s pouvaient contenir des `Combat`s. Nous créions ces combats directement lorsque nous créions ces `Room`s,

Créer le `Combat` à la création de la `Room` était difficile pusique cela nécessitait également de déjà créer un Trainer pour le joueur, ainsi qu'un autre pour son adversaire.

== Améliorations

=== Services

Nos services sont pratiquement tous stateless.

Nous avons limité le plus possible le nombre de services injectés à l'intérieur d'autres services.

Notre `SaveService` sert de _facade pattern_ pour la sauvegarde du jeu, et prend donc en paramètre d'autres services.

=== Interface pour les repositories

Nous avons ajouté des interface à nos repositories. Ceci a permis de grandement simplifier le code de ceux-ci,
en limitant le nombre de méthodes à implémenter.

=== Différents types de bugemons (composition)

Nous utilisons différents types de Bugemons, en tirant parti de la composition.
Ce design est inspiré du schéma dans la base de donnée.
Ceci nous évite de devoir restaurer la vie des bugemons à la fin des combat standalone,
puisqu'il nous suffit de recréer des RunBugemons (la run étant finie après un combat standalone).

#grid(
  columns: 2,
  gutter: 1.5em,

  figure(
    caption: [Bugemon "atomique"],
    table(
      [*Bugemon*],
      [ id ],
      [ name ],
      [ hp (default hp) ],
      [ attack (default attack) ],
      [ defense (default defense) ],
      [ initiative (default initiative) ],
      [ type ],
      [ attacks (default attacks) ],
      [ spritePath ],
      [ isStarter ],
      [ isBoss ],
    ),
  ),

  figure(
    caption: [Bugemon d'un joueur],
    table(
      [*PlayerBugemon*],
      [ Bugemon ],
      [ level ],
      [ xp ],
      [ bonusHp ],
      [ bonusAttack ],
      [ bonusDefense ],
      [ bonusInitiative ],
      [ attacks ],
    ),
  ),

  figure(
    caption: [Bugemon d'une run],
    table(
      [*RunBugemon*],
      [ BaseBugemon ],
      [currentHp],
    ),
  ),

  figure(
    caption: [Bugemon durant un combat],
    table(
      [*CombatBugemon*],
      [ RunBugemon ],
      [ participated ],
      [ currentHp ],
      [ effets ],
    ),
  ),
)

=== Systèmes d'actions en combat

Pour les combats, nous utilisons le strategy pattern, avec le type `CombatStrategy`.

```
public interface CombatStrategy {
    void chooseAction(CombatContext ctx, ActionCallback callback);

    void chooseSwitch(CombatContext ctx, ActionCallback callback);
}
```

Grâce à ce pattern, nous n'avons plus besoin de l'objet `Trainer`. Du point de vue du combat, il y a simplement deux stratégies
auquel il peut demander de chiosir une action, ou bien forcer de switch (ce qu'il se passe lorsque le bugemon courant est ko).

Lorsqu'il demande une action (ou un switch), il passe deux arguments :
- `CombatContext`: un contexte, contenant les informations du joueur et de son adversaire
- `ActionCallback`: le callback que la stratégie doit appeler pour notifier le combat de l'action prise par celle-ci

=== Reporter la création des objets

Notre refactoring tente de repousser la création des objets le plus tard possible (pour contrer les problèmes décrits dans @sec:creation_precoce).

La `Room` contient simplement un `RoomType`, au lieu de stocker les combats dans les CombatRooms.
Le combat est créé lorsque le joueur visite la room si le RoomType indique qu'il s'agit d'une combat room.

// TODO: refactoring process: refac model -> ajuster controller/vue pour nouveau model -> refac repository -> refac service -> refac controller -> refac views
