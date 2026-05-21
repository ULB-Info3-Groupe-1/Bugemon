#let TODO = {
  text(blue)[TODO]
}

#set heading(numbering: "1.1")
#show link: underline

#title[Document de fin d'itération 2]

#outline()

= Gestion de projet <sec:gestion_projet>

== Rythme ralenti par les congés <sec:rhytme_ralenti>

Durant la semaine juste avant les congés, le rythme a fortement ralenti, et n'a pas vraiment repris pendant les congés.
Lors de la reprise, nous avions donc une quantité de travail supérieure à celle attendue.

== Fin d'itération difficile <sec:fin_difficile>

Quelques jours avant la deadline initiale, nous avons arrivions progressivement à la fin du projet, mais non sans difficultés.
En effet, le code ressemblait de plus en plus à un spaghetti géant : le code n'était pas bien pensé pour accuillir les nouvelles fonctionalités de l'itération 4,
mais nous n'avions plus le temps d'entamer un refactoring au moment où nous en avons pris conscience.

C'est pourquoi, lorsque nous avons appris que la deadline était repoussée, nous avons immédiatement entamé un refactoring complet (cf. @sec:qualite_de_code).

Avec le recul, nous réalisons à présent que nous aurions vraissemblablement du demander un refactoring à l'itération 3.
Faire le refactoring plus tôt aurait probablement permis de le rendre plus court que celui que nous avons du effectuer pendant la période supplémentaire.

== Mauvaise compréhension

// TODO: mauvaise compréhensin du skill tree (algo de fou furieux pour rien)

= Qualité de code <sec:qualite_de_code>

Afin de rendre le refactoring le plus efficace possible, nous nous sommes fortement aidés de LLMs (claude).

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

// TODO: systeme d'actions async
// TODO: composition de Bugemons
// TODO: interface des repos -> permette de se concentrer sur les méthodes importantes de ces derniers
