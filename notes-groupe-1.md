### Membres du groupe :
1. Yasmina Bou Khaled
2. Philippe Brisbois
3. Romain Dejean
4. Martin Gouverneur
5. Romain Liefferinckx
6. Matteo Morbée
7. Manuel Rocca
8. Rares Radu Loghin
9. Ethan Van Ruyskensvelde
10. Lucas Verbeiren


## Notes 
|  																				| It1 	| It2	| It3	| It4	|
| ----------- | ----------- | ----------- | ----------- | ----------- |
| Architecture MVC (/10) 														| 7.5	| 8.5	| TBD	| TBD	|
| Orienté Objet (encapsulation, responsabilités) (/10) 							| 8		| 8.5	| TBD	| TBD	|
| Commentaires (/5)																| 5		| 5		| TBD	| TBD	|
| Qualité du code (exceptions, noms de variables) (/10) 						| 8		| 9		| TBD	| TBD	|
| Tests (/10)																	| 9		| 9		| TBD	| TBD	|
| Réévaluation des histoires, demo correcte, discussion avec le client (/10)	| 9.5	| 10	| TBD	| TBD	|
| Organisation du travail (/10)													| 10	| 10	| TBD	| TBD	|



# Itération 1

### Questions :
- Quel usage de l'IA avez-vous fait ?
- La classe Bugémon fait déjà près de 800 lignes. Qu'en pensez-vous ?
- Comment fonctionne le constructeur de la classe Bugémon ? (builder)
- Commentez Models.Bugemon.getXP()
- Vous utilisez des factories. Qu'est-ce que c'est et à quoi ça sert ?
- Models/Bugemon (l.162 et l.168), vous définissez initialState et state. Qu'est-ce que c'est et à quoi ça sert ?
- Comment est défini l'id d'un bugémon ?
- Les attaquse peuvent faire tomber les HP dans le négatif. Pas un problème ?
- Commentez Controllers.CombatController.STRONG_AGAINST.
- models.Bugemon.addXP (l.716) Vous utilisez un Optional<LevelUp>. Qu'est-ce que c'est et à quoi ça sert ?
	
	
	
### Commentaires :
- Il y a encore des magic values et strings hardcodés (cf CombatController)
- Il y a un problème avec la formule des dégats (voir exemple). Vous avez deux méthodes calculateDamage dans models.combat.CombatHelper ?
- Convention de nommage : je recommande de mettre tout le code en angais et avec uniquement l'interface en français.
	
	
# Itération 2


### Questions :
- Que pensez-vous de models.NOTower#goToNextFloor() d'un point de vue du MVC ?
- Imaginons que vous vouliez changer les règles du combat (chance de critiques, supériorité des types, etc). À quel point ça serait facile avec votre architecture ?
- Quelle est votre politique de test ?
- Comment fonctionnent les services ? Par exemple services.LevelUpService ?
- Qu'est-ce que la loi de Démeter ?
- Quels sont les 4 "contraintes" de la gestion de projet ? (Temps, Argent, Features --> Qualité)


# Itération 3
# Itération 4
