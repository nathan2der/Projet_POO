package wargame;

import wargame.game.Game;
import wargame.map.GameMap;
import wargame.map.HexTile;
import wargame.terrain.TerrainType;
import wargame.player.Player;
import wargame.unit.Unit;
import wargame.unit.UnitType;
import wargame.ui.GameWindow;

import java.util.ArrayList;
import java.util.List;

public class GameTest {
    private static final int MIN_SPAWN_DISTANCE = 5; // Minimum distance between player units

    public static void main(String[] args) {
        // Create a larger test map (20x15 instead of 10x10)
        GameMap map = new GameMap(20, 15);
        
        // Create two players
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        
        // Create some units for each player
        Unit infantry1 = new Unit(UnitType.INFANTRY, player1);
        Unit archer1 = new Unit(UnitType.ARCHER, player1);
        Unit cavalry1 = new Unit(UnitType.CAVALRY, player1);
        
        Unit infantry2 = new Unit(UnitType.INFANTRY, player2);
        Unit heavyInfantry2 = new Unit(UnitType.HEAVY_INFANTRY, player2);
        Unit mage2 = new Unit(UnitType.MAGE, player2);
        
        // Place units on the map with proper distance
        System.out.println("Placing units on the map...");
        
        // Player 1 units on the left side
        map.placeUnit(infantry1, 0, 7);
        map.placeUnit(archer1, 1, 6);
        map.placeUnit(cavalry1, 2, 7);
        
        // Player 2 units on the right side
        map.placeUnit(infantry2, 17, 7);
        map.placeUnit(heavyInfantry2, 18, 6);
        map.placeUnit(mage2, 19, 7);
        
        // Generate random terrain
        System.out.println("\nGenerating random terrain...");
        TerrainType[] terrainTypes = TerrainType.values();
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                // Skip tiles with units
                if (map.getTile(x, y).getUnit() != null) {
                    continue;
                }
                
                // Generate clusters of terrain
                double random = Math.random();
                if (random < 0.3) {
                    map.getTile(x, y).setTerrainType(TerrainType.PLAINS);
                } else if (random < 0.5) {
                    map.getTile(x, y).setTerrainType(TerrainType.FOREST);
                } else if (random < 0.7) {
                    map.getTile(x, y).setTerrainType(TerrainType.MOUNTAIN);
                } else if (random < 0.85) {
                    map.getTile(x, y).setTerrainType(TerrainType.WATER);
                } else {
                    map.getTile(x, y).setTerrainType(TerrainType.ROAD);
                }
            }
        }
        
        // Verify minimum distance between units
        System.out.println("\nVerifying unit placement distances...");
        for (Unit unit1 : player1.getUnits()) {
            for (Unit unit2 : player2.getUnits()) {
                int distance = unit1.getTile().distanceTo(unit2.getTile());
                System.out.println("Distance between " + unit1.getType() + " and " + unit2.getType() + ": " + distance);
                if (distance < MIN_SPAWN_DISTANCE) {
                    System.out.println("Warning: Units are too close!");
                }
            }
        }
        
        // Create game
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        Game game = new Game(map, players);
        
        // Test movement
        System.out.println("\nTesting unit movement...");
        HexTile startTile = map.getTile(0, 0);
        HexTile endTile = map.getTile(1, 1);
        
        System.out.println("Moving infantry from (0,0) to (1,1)");
        game.selectUnit(infantry1);
        boolean moveSuccess = game.moveSelectedUnit(1, 1);
        System.out.println("Move successful: " + moveSuccess);
        
        // Test combat
        System.out.println("\nTesting combat...");
        System.out.println("Infantry1 health before combat: " + infantry1.getCurrentHealth());
        System.out.println("Infantry2 health before combat: " + infantry2.getCurrentHealth());
        
        game.selectUnit(infantry1);
        boolean attackSuccess = game.attackWithSelectedUnit(infantry2);
        System.out.println("Attack successful: " + attackSuccess);
        
        System.out.println("Infantry1 health after combat: " + infantry1.getCurrentHealth());
        System.out.println("Infantry2 health after combat: " + infantry2.getCurrentHealth());
        
        // Test turn management
        System.out.println("\nTesting turn management...");
        System.out.println("Current turn: " + game.getTurnNumber());
        System.out.println("Current player: " + game.getCurrentPlayer().getName());
        
        // Test unit selection
        System.out.println("\nTesting unit selection...");
        boolean selectSuccess = game.selectUnit(infantry1);
        System.out.println("Unit selection successful: " + selectSuccess);
        if (selectSuccess) {
            System.out.println("Selected unit: " + game.getSelectedUnit().getType());
        }
        
        // Test valid moves
        System.out.println("\nTesting valid moves...");
        List<HexTile> validMoves = game.getValidMoves();
        System.out.println("Number of valid moves: " + validMoves.size());
        
        // Test valid targets
        System.out.println("\nTesting valid targets...");
        List<Unit> validTargets = game.getValidTargets();
        System.out.println("Number of valid targets: " + validTargets.size());
        
        // Test turn end
        game.endTurn();
        System.out.println("\nAfter end turn:");
        System.out.println("Current turn: " + game.getTurnNumber());
        System.out.println("Current player: " + game.getCurrentPlayer().getName());
        
        // Test terrain effects
        System.out.println("\nTesting terrain effects...");
        HexTile forestTile = map.getTile(3, 3);
        forestTile.setTerrainType(TerrainType.FOREST);
        System.out.println("Forest movement cost: " + forestTile.getTerrainType().getMovementCost());
        System.out.println("Forest defense bonus: " + forestTile.getTerrainType().getDefenseBonus());

        // Test UI
        System.out.println("\nTesting UI...");
        GameWindow gameWindow = new GameWindow(game);
        gameWindow.setVisible(true);
    }
} 