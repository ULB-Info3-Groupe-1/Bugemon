#let TODO = {
  text(blue)[TODO]
}

#set heading(numbering: "1.1")
#show link: underline

#title[Document de fin d'itération 2]

#outline()

= Gestion de projet

== Rythme ralenti par les congés

Durant la semaine juste avant les congés, le rythme a fortement ralenti, et n'a pas vraiment repris pendant les congés.
Lors de la reprise, nous avions donc une quantité de travail à terminer supérieure à celle prévue.

= Code

== Services

Quatre jours avant la remise, nous avons réalisé que nos services étaient beaucoup trop _stateful_ pour des services.
Par exemple, `BugemonService` stockait les `LevelUp`s pas  encore choisi. Par conséquent le BugemonService était _stateful_, ce qui compliquait son utilisation.

== Mauvaise compréhension Skill-Tree


