# ANALYSE DU BESOIN #
## Cahier des charges initial ##

L’objectif du projet est de développer un **système expert** capable de deviner des entités en posant à l’utilisateur une série de questions. Ce concept est inspiré de l’application Akinator ou 20Q.

Réalisé sous forme de jeu, le logiciel devra sélectionner les questions pertinentes qui permettront au moteur d’intelligence artificielle de deviner le plus rapidement possible ce à quoi pense l’utilisateur. Pour cela, il s’appuiera sur une base de connaissance composée au démarrage de 100 entités, et d’en moyenne 200 questions, lesquelles seront au fur et à mesure des parties complétées et affinées.

Notre moteur **Jade** (pour Julien, Alexandre, Djeneba, Epsi) sera capable de découvrir le sport auquel vous pensez, ou une série télévisée. A chaque étape, des données statistiques seront enregistrées pour améliorer le système, et à la fin de la partie, en cas d’erreur, le logiciel proposera à l’utilisateur de saisir sa réponse et une question discriminative pour ne plus réitérer cette erreur dans les prochaines parties.

## Fonctionnalités supplémentaires ##
En plus du fonctionnement de base, nous avons décidé de rajouter plusieurs fonctionnalités au cahier des charges initial.

  * En plus des boutons « Oui » et « Non » permettant de répondre aux questions, un bouton « Je ne sais pas » sera présent. Son comportement utilisera les statistiques enregistrées tout au long des parties précédents pour choisir la réponse qui a mathématiquement le plus de chance d’être cette correcte.
  * En relation, un tableau affichera les statistiques des différentes entités : le nombre de fois où le moteur d’IA a trouvé la bonne réponse, et le nombre de fois où il s’est trompé.
  * Une dernière fonctionnalité a été implémenté, celle-ci prend part dans le formulaire d’apprentissage. Lorsque l’utilisateur renseigne l’entité à laquelle il pensait, le moteur va vérifier si elle n’est pas déjà enregistrée dans la base de connaissance.

On part du postulat que la base de connaissance est intègre, et donc que l’utilisateur a commis une erreur dans ses réponses.
On va donc rechercher et indiquer à l’utilisateur sur quelle question il s’est trompé.
Pour cela, il est nécessaire de réaliser un double parcours inversé de l’arbre de recherche.

## Outils de développement ##
Le choix du langage de programmation s’est naturellement orienté sur le langage libre et open-source **Java**.

Dans un premier temps, une étude technique a été réalisée sur le moteur d’inférence Jess (http://www.jessrules.com/) afin de déterminer s’il était ou non adapté à notre cahier des charges.

Jess est un moteur de règle maintenu par le laboratoire Sandia (http://www.sandia.gov), offrant une implémentation en JAVA du langage Clips. Jess offre un système de programmation par règle permettant de développer des systèmes experts et des moteurs d’inférence. De ce fait, il répond à notre besoin sur la partie technique.

Cependant, plusieurs points nous ont amenés à choisir une solution alternative : **développer notre propre moteur d’inférence**.

Tout d’abord, Jess est un langage propriétaire, dont les sources sont verrouillées et qui n’offre qu’une documentation très partielle. Bien que le système soit lui-même très complet, le développeur ne peut que très difficilement exploiter tout le potentiel de ce moteur. De surcroît, il nous a semblé très intéressant de coder nous-même notre moteur afin d’en comprendre le fonctionnement interne, et de pouvoir l’adapter plus finement à notre besoin.

Concernant la base de connaissance, celle-ci est actuellement stockée dans une Système de gestion de base de données relationnel (SGBRD) dit « embarquée » : **DerbySQL**. Cette caractéristique nous permet de déployer facilement notre application, la base de données est directement incluse et ne demande pas l’installation ou la configuration d’un service extérieur.

De plus, nous avons utilisé la librairie JDBC de Java qui permet d’utiliser de manière transparente n’importe quel SGBDR. Il est alors tout à fait possible de transférer la base de connaissance sur une base de données hébergée en ligne, permettant ainsi à plusieurs utilisateurs de se connecter, de partager et d’enrichir une unique base de données.

Concernant le travail des experts, le logiciel libre **FreeMind** a été fort utile.  Sa principale fonction est de permettre l’élaboration de cartes heuristiques, mais il est aussi intéressant de l’utiliser pour concevoir un arbre qui, dans la période de réflexion, se veut très évolutif.

Enfin, afin de faciliter les opérations d’intégration continue, nous avons utilisé le logiciel de gestion  de version décentralisé **GIT**.

# Descriptions des principaux algorithmes #

Dans cette section, nous allons étudier les principaux algorithmes mis en place au sein de l’application.

Dans un premier temps, nous analyserons l’algorithme d’inférence principal, c’est-à-dire celui permettant d’avancer dans l’arbre de recherche pour trouver l’entité finale. Dans un second temps, nous verrons l’algorithme permettant de retrouver la question marquant la bifurcation entre deux branches de l’arbre.

## Analyse de l'algorithme principal d'inférence ##

**Objectif** : Trouver l’entité à laquelle pense l’utilisateur en lui posant un minimum de questions fermée.

**Stratégie** : Parcours avant de l’arbre de recherche préparé par l’expert et amélioré par les précédentes parties du jeu.

**Diagramme algorithmique**

![http://up.studio-dev.fr/_/algo1.png](http://up.studio-dev.fr/_/algo1.png)

**Programmation algorithmique**

```
Nœud <- récupérer question racine
Proposition <- Nul
Tant que Proposition est nul faire
	Poser question à l’utilisateur
	Réponse <- Récupérer réponse de l’utilisateur
	Nœud <- Avancer dans l’arbre à partir du nœud courant en fonction de la réponse
	Si nœud est de type question alors 	
		question <- Nœud
	Sinon
		Proposition <- Nœud
	Fin si
Fin boucle

Afficher Proposition
Résultat <- Demander à l’utilisateur si la proposition est correcte

Si Résultat est Vrai alors 
	Fin de partie gagnante
Sinon 
	Réponse <- Demander réponse à l’utilisateur
	Si réponse existe déjà alors
		Rechercher source de l’erreur
		Afficher à l’utilisateur la question où il s’est trompé
	Sinon
		Demander à l’utilisateur la question discriminative
		Insérer dans la base de connaissance la nouvelle réponse
	Fin si
Fin si
```


## Analyse de l'algorithme de recherche arrière de "fiburcation" ##


**Déclenchement** : Cet algorithme est utilisé à la fin d’une inférence infructueuse, lorsque l’utilisateur saisit la réponse à laquelle il pensait et que le moteur détecte que cette réponse est déjà présent dans la base de connaissance. On part du postulat que la base est intègre, on en déduit donc que l’utilisateur s’est trompé à une réponse.

**Objectif** : Trouver la question à laquelle l’utilisateur a entré la mauvaise réponse.

**Stratégie** : A partir de deux nœuds de l’arbre (la proposition du moteur et la réponse de l’utilisateur), on remonte récursivement jusqu’à trouver la première question commune.

**Optimisation** : On parcourt en parallèle les 2 branches de l’arbre et on vérifie à chaque étape si la question parcourue est présente parmi les ancêtres déjà connues de l’autre question de départ.

**Programmation algorithmique :**

```
ListeAncetresBrancheA <- { NoeudA }
ListeAncetresBrancheB <- { NoeudB }

Si NoeudA = NoeudB alors 
	=> Retourner Question du NoeudA
Fin si

Boucler
	Si NoeudA différent de NoeudRacine alors
		NoeudA <- Parent direct de la NoeudA
		ListeAncetresBrancheA <- Ajouter NoeudA	
		Si ListeAncetresBrancheB contient NoeudA alors
			=> Retourner NoeudA
		Fin si
	Fin si

	Si NoeudB différent de NoeudRacine alors
		NoeudB <- Parent direct de NoeudB
		ListeAncetresBrancheB <- Ajouter NoeudB
		Si ListeAncetresBrancheA contient NoeudB alors
			=> Retourner NoeudB
		Fin si
	Fin si 
Fin Boucle
```

# CONCLUSION ET PERSPECTIVES #

Au final, l’application Jade fournit un système de recherche parfaitement fonctionnel.

La base de connaissance initiale comporte environ 100 sports et séries, avec en moyenne 500 questions par arbre de recherche, et une longueur moyenne des branches de 10-15 questions.

Plusieurs améliorations peuvent être apportées au système pour le rendre plus performant, et le code du moteur et des contrôleurs ont été pensés pour qu’elles soient possibles et facilement intégrables.
> La première amélioration serait de transformer l’arbre de recherche en un graphe, où plusieurs branches peuvent se rejoindre dans des cas où la réponse à une question n’est pas formelle. Cela permettrait ainsi de rattraper des erreurs récurrentes des utilisateurs.

Une autre idée est de stocker les questions auxquelles le joueur a répondu « Je ne sais pas », et en cas d’erreur à la fin de la recherche, le moteur pourrait repartir sur ces questions en prenant l’autre réponse. Cela permettrait d’avoir un comportement proche de celui d’Akinator où le moteur a plusieurs chances de trouver la solution.