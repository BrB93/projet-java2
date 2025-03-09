**🌾 Projet de Ferme Virtuelle 🐄**


📝 Description

Ce projet est une simulation de gestion de ferme virtuelle développée en Java avec JavaFX. Les joueurs peuvent planter des cultures, élever des animaux, vendre des produits et gérer les finances de leur ferme.


✨ Fonctionnalités

🌱 Plantation et Récolte : Cultivez du blé, du maïs et des carottes
🐔 Élevage d'Animaux : Prenez soin de poules, vaches et moutons
🍽️ Système de Besoins : Nourrissez vos animaux selon leurs besoins spécifiques
⚠️ Cycle de Faim : Surveillez l'état de vos animaux (bien nourri, affamé, mourant)
🔔 Alertes Visuelles : Notifications colorées pour l'état des animaux
💰 Gestion des Finances : Suivez votre économie fermière
⏱️ Chronomètre de Jeu : Mesurez votre temps de jeu


🔧 Prérequis
Java 11 ou supérieur
Maven
IntelliJ IDEA (ou IDE Java compatible)

📥 Installation

Clonez le dépôt :

git clone https://github.com/BrB93/projet-java2.git

Accédez au répertoire du projet :

cd projet-java2/projet

Compilez avec Maven :

mvn clean install


🎮 Utilisation

Ouvrez le projet dans IntelliJ IDEA
Exécutez src/main/java/farm/Main.java

Dans l'interface de la ferme virtuelle :

Gestion de la ferme
🌾 Planter : Sélectionnez une culture puis cliquez sur une case vide
🐄 Placer des animaux : Sélectionnez un animal puis cliquez sur une case vide
🍞 Nourrir les animaux : Cliquez sur "Nourrir animal" puis sur l'animal concerné
🐔 Poules → maïs
🐄 Vaches → blé
🐑 Moutons → carottes
🧺 Récolter : Les cultures peuvent être récoltées une fois mûres
État des animaux
État
Indicateur
Description
🟢 Bien nourri
Vert
Animal en bonne santé
🟠 Affamé
Orange
À nourrir rapidement
🔴 Mourant
Rouge
Danger de mort imminente


📁 Structure du Projet

src/
├── main/
│   ├── java/farm/           # Classes principales
│   │   ├── Main.java        # Point d'entrée
│   │   ├── MainController   # Contrôleur UI principal
│   │   ├── FieldController  # Gestion de la grille de jeu
│   │   ├── Farm.java        # Logique de la ferme
│   │   ├── Animal.java      # Gestion des animaux
│   │   └── Crop.java        # Gestion des cultures
│   └── resources/
│       ├── fxml/            # Layouts UI
│       ├── css/             # Styles CSS
│       └── img/             # Images du jeu



👨‍💻 Auteurs

BrB93


📄 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.