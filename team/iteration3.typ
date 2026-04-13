#let TODO = {
  text(blue)[TODO]
}

#set text(lang: "fr")
#set heading(numbering: "1.1")
#show link: underline

#title[Document de fin d'itération 3]

#outline()

= Uniformité du code

== Linter Checkstyle

Nous avons ajouté celui-ci au début de cet itération. Nous avons rapidement vu
les bénéfices, code beaucoup plus uniforme, même style.

== Nommage

Nous avons été confrontés au problème de convention de nommage.

Le principe de 1 concept #sym.arrow.l.r 1 nom n'était pas vraiment respectée.

Ce problème était déjà présent durant les itérations précédentes.

Au fur et à mesure de cette itération, nous avons ajouté des issues à chaque
fois que nous voyions un problème de ce nommage.

Vers la fin de l'itération, nous avons :
+ écrit des conventions de nommage dans un document pour respecter le principe 1 concept #sym.arrow.l.r 1 nom.
+ résolu toutes ces issues.

= Listeners

- ajout des interfaces `Listener` dans toutes les Views.
- suppression de toutes les références au Controllers dans les Views.

Au départ les méthodes de nos `Listeners` ne prenaient jamais de paramètre.
Lorsqu'une View signalait à son `Listener` (Controller) un évènement, et que
le Controller devait récupérer une information concernant l'event, il utilisait
un getter défini dans sa View.

Nous avons ensuite réalisé que ceci était mauvais. Notre solution consiste à
passer les informations nécessaires directement en paramètre des méthodes de
`Listener`. 

_Exemple :_

#figure(
  ```java
  // CreateBugemonView.java
  public interface Listener {
      void onTypeSelected(BugemonType selectedType);

      void onAdd(String bugemonName, double healthValue, double attackValue,
                  double defenseValue, double initiativeValue);
  }
  ```
)

= Logique dans les Controllers

Dans l'itération 2, nous avions de la logique dans les controllers, en
particulier pour les fins de combats.

#figure(
  ```java
  // CombatController
  protected void onCombatEnded(Trainer winner) {
      this.playerService.restoreHpActiveTeam();

      List<LevelUp> levelUps = LevelUpService.distributeXpAndGetLevelUps(winner,
                                  this.combat.getOpponentTrainer());

      this.playerService.saveActiveTeamState();

      boolean won = winner == this.playerTrainer;
      this.metaController.onCombatFinished(levelUps, won);
  }
  ```
)<on_combat_ended_before>

Clairement, la distribution d'XP à la fin du combat, ainsi que la création des
objets `LevelUp` (contenant les choix d'upgrade pour les montées de niveau)
n'a rien à faire dans un controller. 

La distribution de l'XP (et la création des LevelUp) est une étape du
déroulement d'un combat.

*Notre solution :*

L'objet `Combat` prend en paramètre un objet `ICombatXpDistributor` et
demande à celui-ci de distribuer l'xp (délégation).

`CombatXpDistributor` calcule la quantité d'xp à distribuer aux bugemons et demande au `BugemonService` de distribuer l'xp.

`BugemonService` gère ensuite :
+ l'ajout d'xp sur le bugemon reçu en paramètre
+ la sauvegarde de l'état du bugemon (pour sauvegarder les nouveaux xp + niveau)
+ la création et le stockage interne des levelUps

La méthode @on_combat_ended_before devient alors :

#figure(
  ```java
  // CombatController
  protected void onCombatEnded(Trainer winner) {
      boolean won = winner == this.playerTrainer;
      this.metaController.onCombatFinished(won);
  }
  ```
)<on_combat_ended_after>

= Basculement des écrans

Nous utilisions initialement une méthode _publique_
`MetaController#switchTo(Window window)` pour gérer les transitions
entre écrans.

Après avoir discuté Pr. Frédéric Pluquet, nous avons constaté que cette
approche manquait de flexibilité. Nous avons adopté une solution plus adaptée
qu'il nous a suggérée : ajouter au `MetaController` des méthodes lui
permettant de réagir aux événements qui lui sont transmis par les autres
contrôleurs.

Cette approche est notamment illustrée dans le `CombatController`, avec la
méthode `onCombatEnded` (voir @on_combat_ended_after).

_Note :_ nous n'avons pas encore fini de remplacer tous les appels à
`switchTo`. Ceci sera terminé au début de la prochaine itération.

= Double dispatch

Utilisation du double dispatch dans l'application des effets sur les bugemons.

`Bugemon` expose une méthode :

```java
// Bugemon.java
public void apply(Effect effect) {
    effect.applyTo(this);
}
```

`Effect`, `EffectStatModifier`, `EffectHeal`, `EffectResetMalus` ont tous la méthode suivante :

```java
public void applyTo(Bugemon bugemon) {
    bugemon.apply(this);
}
```

Ces méthodes permettent d'appeler le bon overload de `Bugemon` :

```java
// Bugemon.java
public void apply(EffectStatModifier e) { ... }
public void apply(EffectHeal e) { ... }
public void apply(EffectResetMalus e) { ... }
```

