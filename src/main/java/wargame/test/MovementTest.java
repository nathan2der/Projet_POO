package wargame.test;

import wargame.unit.Unit;
import wargame.unit.UnitType;
import wargame.player.Player;
import wargame.map.GameMap;
import wargame.map.HexTile;
import wargame.game.TurnManager;
import wargame.game.Game;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple test class to verify movement point reset functionality.
 */
public class MovementTest {
    
    public static void main(String[] args) {
        System.out.println("Starting movement point reset test...");
        
        // Create a test game map
        GameMap map = new GameMap(10, 10);
        
        // Create players
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        
        // Create game
        Game game = new Game(map, players);
        
        // Create units
        Unit infantry1 = new Unit(UnitType.INFANTRY, player1);
        Unit archer1 = new Unit(UnitType.ARCHER, player1);
        
        Unit infantry2 = new Unit(UnitType.INFANTRY, player2);
        Unit archer2 = new Unit(UnitType.ARCHER, player2);
        
        // Place units on map
        game.placeUnit(infantry1, 1, 1);
        game.placeUnit(archer1, 2, 2);
        game.placeUnit(infantry2, 8, 8);
        game.placeUnit(archer2, 7, 7);
        
        // Add units to players to make sure they're tracked
        player1.addUnit(infantry1);
        player1.addUnit(archer1);
        player2.addUnit(infantry2);
        player2.addUnit(archer2);
        
        // Print current player
        System.out.println("Current player: " + game.getCurrentPlayer().getName());
        
        // Verify initial movement points
        System.out.println("\n--- Initial state ---");
        printUnitMovement(infantry1, "Infantry 1");
        printUnitMovement(archer1, "Archer 1");
        printUnitMovement(infantry2, "Infantry 2");
        printUnitMovement(archer2, "Archer 2");
        
        // Move a unit to reduce movement points
        System.out.println("\n--- Moving unit ---");
        System.out.println("Moving infantry1...");
        infantry1.move(map.getTile(2, 1));
        printUnitMovement(infantry1, "Infantry 1 after move");
        
        // End player 1's turn and reset
        System.out.println("\n--- Ending Player 1's turn ---");
        game.endTurn();
        
        // Print current player after end turn
        System.out.println("Current player: " + game.getCurrentPlayer().getName());
        
        // Verify movement points after end turn
        System.out.println("\n--- After Player 1's turn ended ---");
        printUnitMovement(infantry1, "Infantry 1");
        printUnitMovement(archer1, "Archer 1");
        
        // Move a unit from player 2
        System.out.println("\n--- Moving Player 2's unit ---");
        infantry2.move(map.getTile(7, 8));
        printUnitMovement(infantry2, "Infantry 2 after move");
        
        // End player 2's turn
        System.out.println("\n--- Ending Player 2's turn ---");
        game.endTurn();
        
        // Print current player
        System.out.println("Current player: " + game.getCurrentPlayer().getName());
        
        // Verify movement points after full round
        System.out.println("\n--- After full round (back to Player 1) ---");
        printUnitMovement(infantry1, "Infantry 1");
        printUnitMovement(archer1, "Archer 1");
        printUnitMovement(infantry2, "Infantry 2");
        printUnitMovement(archer2, "Archer 2");
        
        // Print player's unit list sizes to check tracking
        System.out.println("\nPlayer 1 unit count: " + player1.getUnits().size());
        System.out.println("Player 2 unit count: " + player2.getUnits().size());
        
        // Manual test - try to use Player.resetTurn directly
        System.out.println("\n--- Testing Player.resetTurn directly ---");
        player1.resetTurn();
        printUnitMovement(infantry1, "Infantry 1 after player1.resetTurn");
        
        // Directly verify the reset functionality
        System.out.println("\n--- Directly testing Unit.resetTurn ---");
        infantry1.setRemainingMovement(1); // Manually set to low value
        printUnitMovement(infantry1, "Infantry 1 after manual change");
        
        // Directly call resetTurn
        infantry1.resetTurn();
        printUnitMovement(infantry1, "Infantry 1 after resetTurn");
        
        System.out.println("\nTest completed.");
    }
    
    private static void printUnitMovement(Unit unit, String label) {
        System.out.println(label + ": movement=" + unit.getRemainingMovement() + 
                          "/" + unit.getType().getMovement() + 
                          ", hasMoved=" + unit.hasMoved() +
                          ", owner=" + unit.getOwner().getName());
    }
} 