## Installation et Exécution

### Prérequis

- Java JDK 17 ou supérieur
- Maven 3.6 ou supérieur

### Installation de Maven

#### Sous Windows
1. Télécharger l'archive binaire depuis [le site officiel de Maven](https://maven.apache.org/download.cgi)
2. Extraire l'archive dans un dossier (ex: `C:\Program Files\Maven`)
3. Ajouter le chemin du dossier `bin` à la variable d'environnement PATH
4. Vérifier l'installation avec `mvn -version`

#### Sous Linux (Debian/Ubuntu)
```bash
sudo apt update
sudo apt install maven
mvn -version
```

#### Sous macOS
```bash
brew install maven
mvn -version
```

### Configuration du Projet

1. Cloner le dépôt
   ```bash
   git clone [URL_DU_REPO]
   cd wargame
   ```

2. Compiler le projet
   ```bash
   mvn clean package
   ```
ou plus simplement "make build-run"

3. Exécuter le jeu
   ```bash
   java -jar target/wargame-1.0-SNAPSHOT.jar
   ```
ou plus simplement "make build-run"

## Liste des Tâches à Faire (TODO)

### Priorité Haute
- Équilibrer les statistiques des unités
- Améliorer le feedback visuel des mouvements
- Compléter l'implémentation du système de sauvegarde/chargement
- Implémenter les bonus de terrain dans le combat
- Optimiser le placement des unités au départ

### Priorité Moyenne
- Améliorer la génération aléatoire de terrain
- Implémenter l'économie du jeu
- Ajouter des animations de combat
- Ajouter des tooltips pour les unités et terrains
- Implémenter l'auto-sauvegarde

### Priorité Basse
- Compléter la documentation Javadoc
- Ajouter des tests unitaires
- Créer un guide utilisateur
- Ajouter des effets sonores
- Implémenter un système d'expérience pour les unités