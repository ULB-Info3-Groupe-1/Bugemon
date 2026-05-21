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

== Fin d'itération difficile <sec:sec_fin_difficile>

Quelques jours avant la deadline initiale, nous avons arrivions progressivement à la fin du projet, mais non sans difficultés.
En effet, le code ressemblait de plus en plus à un spaghetti géant : le code n'était pas bien pensé pour accuillir les nouvelles fonctionalités de l'itération 4,
mais nous n'avions plus le temps d'entamer un refactoring au moment où nous en avons pris conscience.

C'est pourquoi, lorsque nous avons appris que la deadline était repoussée, nous avons immédiatement entamé un refactoring complet (cf. @sec:qualite_de_code).

Avec le recul, nous réalisons à présent que nous aurions vraissemblablement du demander un refactoring à l'itération 3.
Faire le refactoring plus tôt aurait probablement permis de le rendre plus court que celui que nous avons du effectuer pendant la période supplémentaire.

= Qualité de code <sec:qualite_de_code>

== Mauvais choix initiaux

Dans cette section, nous décrivons brièvement les mauvaise choix de conception que nous avions fait avant le refactoring final.

=== Services Stateful

Quatre jours avant la remise, nous avons réalisé que nos services étaient beaucoup trop _stateful_ pour des services.
Par exemple, `BugemonService` stockait les `LevelUp`s pas  encore choisi. Par conséquent le BugemonService était _stateful_, ce qui compliquait son utilisation.

=== Service trop interdépendants

À de nombreuses reprises des services étaient injectés à l'intérieur d'autres services.
Nous avons par la suite réalisé que ceci était généralement une mauvaise idée du au risque d'apparition de dépendance cyclique entre ceux-ci.

=== Un seul type de bugemon



// TODO: mauvaise compréhension du skill tree
