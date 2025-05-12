package wargame.game;

import wargame.player.Player;
import wargame.unit.Unit;
import wargame.map.GameMap;
import wargame.map.HexTile;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/**
 * Manages game turns and player order.
 * Handles turn-based gameplay mechanics and player actions.
 */
public class TurnManager {
    private final List<Player> players;
    private final Queue<Player> turnOrder;
    private Player currentPlayer;
    private int turnNumber;
    private final GameMap gameMap;
    private Unit selectedUnit;
    private static final int MAX_TURNS = 100; // Optional turn limit
    private int currentPlayerIndex;

    /**
     * Creates a new turn manager.
     * @param players The list of players
     * @param gameMap The game map
     */
    public TurnManager(List<Player> players, GameMap gameMap) {
        this.players = players;
        this.turnOrder = new LinkedList<>(players);
        this.gameMap = gameMap;
        this.turnNumber = 1;
        this.currentPlayer = turnOrder.peek();
        this.currentPlayerIndex = 0;
        this.selectedUnit = null;
    }

    /**
     * Gets the current player.
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the current turn number.
     * @return The turn number
     */
    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     * Gets the currently selected unit.
     * @return The selected unit, or null if no unit is selected
     */
    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    /**
     * Selects a unit for the current player.
     * @param unit The unit to select
     * @return true if the unit was selected, false otherwise
     */
    public boolean selectUnit(Unit unit) {
        if (unit != null && unit.getOwner() == currentPlayer) {
            selectedUnit = unit;
            return true;
        }
        return false;
    }

    /**
     * Deselects the currently selected unit.
     */
    public void deselectUnit() {
        selectedUnit = null;
    }

    /**
     * Moves the selected unit to a new tile.
     * @param targetTile The tile to move to
     * @return true if the move was successful, false otherwise
     */
    public boolean moveSelectedUnit(HexTile targetTile) {
        if (selectedUnit == null || selectedUnit.getOwner() != currentPlayer) {
            System.out.println("Move failed: No selected unit or wrong player");
            return false;
        }
        
        if (targetTile == null || targetTile.getUnit() != null) {
            System.out.println("Move failed: Invalid target tile or tile occupied");
            return false;
        }
        
        HexTile currentTile = selectedUnit.getCurrentTile();
        if (currentTile == null) {
            System.out.println("Move failed: Unit has no current tile");
            return false;
        }
        
        // Original implementation with movement point restrictions:
        /*
        // Calculate the actual movement cost to reach the target tile
        int movementCost = calculateMovementCost(currentTile, targetTile, selectedUnit.getRemainingMovement());
        
        if (movementCost == -1) {
            System.out.println("Move failed: Target tile unreachable with current movement points");
            return false;
        }
        
        if (movementCost > selectedUnit.getRemainingMovement()) {
            System.out.println("Move failed: Not enough movement points (" + 
                             selectedUnit.getRemainingMovement() + " vs needed " + movementCost + ")");
            return false;
        }
        
        System.out.println("Moving unit " + selectedUnit.getType() + " with " + 
                         selectedUnit.getRemainingMovement() + " movement points, cost: " + movementCost);
        
        return selectedUnit.move(targetTile, movementCost);
        */
        
        // NEW IMPLEMENTATION: Allow movement to any empty tile
        // Use a constant low movement cost to preserve some movement history
        int artificialMovementCost = 1;
        
        System.out.println("Moving unit " + selectedUnit.getType() + " with unlimited movement");
        
        return selectedUnit.move(targetTile, artificialMovementCost);
    }
    
    /**
     * Calculates the movement cost to reach a target tile from a source tile.
     * Uses breadth-first search to find the cheapest path.
     * 
     * @param sourceTile The starting tile
     * @param targetTile The destination tile
     * @param maxMovement The maximum movement points available
     * @return The movement cost, or -1 if unreachable
     */
    private int calculateMovementCost(HexTile sourceTile, HexTile targetTile, int maxMovement) {
        if (sourceTile == targetTile) {
            return 0;
        }
        
        // Use breadth-first search to find the cheapest path
        Queue<HexTile> queue = new LinkedList<>();
        Map<HexTile, Integer> costToReach = new HashMap<>();
        Map<HexTile, HexTile> cameFrom = new HashMap<>();
        
        // Start from source tile
        queue.add(sourceTile);
        costToReach.put(sourceTile, 0);
        
        boolean foundPath = false;
        
        while (!queue.isEmpty() && !foundPath) {
            HexTile current = queue.poll();
            int currentCost = costToReach.get(current);
            
            if (current == targetTile) {
                foundPath = true;
                break;
            }
            
            // Get adjacent tiles
            List<HexTile> adjacentTiles = gameMap.getAdjacentTiles(current);
            
            for (HexTile adjacent : adjacentTiles) {
                // Skip tiles with units except for the target tile
                if (adjacent.getUnit() != null && adjacent != targetTile) {
                    continue;
                }
                
                int terrainCost = adjacent.getTerrainType().getMovementCost();
                int newCost = currentCost + terrainCost;
                
                // Only add if we can afford the movement and haven't found a cheaper path already
                if (newCost <= maxMovement && 
                    (!costToReach.containsKey(adjacent) || newCost < costToReach.get(adjacent))) {
                    queue.add(adjacent);
                    costToReach.put(adjacent, newCost);
                    cameFrom.put(adjacent, current);
                }
            }
        }
        
        // If we found a path to the target, return the cost
        if (costToReach.containsKey(targetTile)) {
            return costToReach.get(targetTile);
        }
        
        // No path found
        return -1;
    }

    /**
     * Makes the selected unit attack a target unit.
     * @param targetUnit The unit to attack
     * @return true if the attack was successful, false otherwise
     */
    public boolean attackWithSelectedUnit(Unit targetUnit) {
        if (selectedUnit == null || selectedUnit.getOwner() != currentPlayer) {
            return false;
        }

        if (targetUnit == null || targetUnit.getOwner() == currentPlayer) {
            return false;
        }

        boolean attackSuccess = selectedUnit.attack(targetUnit);
        if (attackSuccess) {
            // Mark unit as having attacked
            selectedUnit.setHasAttacked(true);
            
            // Check if target unit was destroyed
            if (targetUnit.getCurrentHealth() <= 0) {
                System.out.println("Unit destroyed in TurnManager.attackWithSelectedUnit");
                gameMap.removeUnit(targetUnit);
                targetUnit.getOwner().removeUnit(targetUnit);
                
                // Check if this caused a victory condition
                Player defenderPlayer = targetUnit.getOwner();
                if (!defenderPlayer.hasUnits()) {
                    System.out.println("VICTORY CONDITION DETECTED: " + defenderPlayer.getName() + " has no units left!");
                    System.out.println("Winner should be: " + currentPlayer.getName());
                    
                    // Debug output of all players' unit counts
                    for (Player player : players) {
                        System.out.println("  " + player.getName() + " has " + player.getUnits().size() + " units");
                    }
                }
            }
        }
        return attackSuccess;
    }

    /**
     * Ends the current player's turn.
     * @return true if the game should continue, false if the game is over
     */
    public boolean endTurn() {
        System.out.println("DEBUG: TurnManager.endTurn() called for " + currentPlayer.getName());
        System.out.println("  Units count: " + currentPlayer.getUnits().size());
        
        // Store a reference to the current player before changing
        Player previousPlayer = currentPlayer;
        
        // Create a snapshot of the units to ensure we work with consistent references
        List<Unit> unitsToReset = new ArrayList<>(previousPlayer.getUnits());
        
        // Reset movement points for each unit using resetTurn() method directly
        // This approach doesn't use indirect methods (like player.resetTurn) which might not access the correct unit instances
        for (Unit unit : unitsToReset) {
            System.out.println("Resetting movement for " + unit.getType() + " from " + 
                               unit.getRemainingMovement() + " to " + unit.getType().getMovement());
            
            // Use the unit's own reset method for proper encapsulation
            unit.resetTurn();
            
            // Verify the unit was reset properly
            System.out.println("After reset, unit " + unit.getType() + " has " + 
                               unit.getRemainingMovement() + "/" + unit.getType().getMovement() +
                               " movement points, hasMoved=" + unit.hasMoved());
            
            // Double-check that the unit was actually reset correctly
            if (unit.getRemainingMovement() < unit.getType().getMovement() || 
                unit.hasMoved() || unit.hasAttacked()) {
                System.out.println("WARNING: Unit not properly reset, forcing reset again");
                unit.resetMovementPoints();
                unit.setHasMoved(false);
                unit.setHasAttacked(false);
            }
        }
        
        // Move to next player
        turnOrder.add(turnOrder.poll());
        currentPlayer = turnOrder.peek();
        System.out.println("Turn changed from " + previousPlayer.getName() + 
                          " to " + currentPlayer.getName());
        
        // If we've gone through all players, increment turn number
        if (currentPlayer == players.get(0)) {
            turnNumber++;
            System.out.println("New turn: " + turnNumber);
        }
        
        // Check for game end conditions
        return !isGameOver();
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise
     */
    private boolean isGameOver() {
        // Check for turn limit
        if (turnNumber > MAX_TURNS) {
            return true;
        }

        // Check for player elimination
        int activePlayers = 0;
        for (Player player : players) {
            if (player.hasUnits()) {
                activePlayers++;
            }
        }
        return activePlayers <= 1;
    }

    /**
     * Gets the winner of the game.
     * @return The winning player, or null if the game is not over
     */
    public Player getWinner() {
        // Debug the current state of all players' units
        debugPlayerUnits();
        
        // Count active players (those with units)
        int activePlayers = 0;
        Player lastActivePlayer = null;
        
        for (Player player : players) {
            if (player.hasUnits()) {
                activePlayers++;
                lastActivePlayer = player;
            }
        }
        
        // If only one player has units, they win
        if (activePlayers == 1) {
            return lastActivePlayer;
        }
        
        // If no players have units (which shouldn't normally happen),
        // determine winner based on last action
        if (activePlayers == 0) {
            System.out.println("WARNING: No players have units. This is likely a bug.");
            
            // As a fallback, return the player who is NOT the current player
            // (since the current player likely just lost their last unit)
            for (Player player : players) {
                if (player != currentPlayer) {
                    System.out.println("Using fallback winner determination: " + player.getName());
                    return player;
                }
            }
        }
        
        // Multiple players still have units or no winner could be determined
        return null;
    }
    
    /**
     * Debug method to print the current state of all players' units.
     * This helps diagnose issues with unit tracking.
     */
    private void debugPlayerUnits() {
        System.out.println("DEBUG: Current player unit counts:");
        for (Player player : players) {
            System.out.println("  " + player.getName() + ": " + player.getUnits().size() + " units");
            
            // Print details of each unit
            for (Unit unit : player.getUnits()) {
                System.out.println("    - " + unit.getType() + 
                                  " (health: " + unit.getCurrentHealth() + "/" + unit.getType().getHealth() + 
                                  ", tile: " + (unit.getCurrentTile() != null ? 
                                              "(" + unit.getCurrentTile().getX() + "," + unit.getCurrentTile().getY() + ")" 
                                              : "null") + ")");
            }
        }
    }

    /**
     * Gets all valid moves for the selected unit.
     * @return List of valid destination tiles
     */
    public List<HexTile> getValidMoves() {
        List<HexTile> validMoves = new ArrayList<>();
        if (selectedUnit == null || selectedUnit.getOwner() != getCurrentPlayer()) {
            return validMoves;
        }

        // Original implementation with movement point restrictions:
        /*
        int movementPoints = selectedUnit.getMovementPoints();
        HexTile currentTile = selectedUnit.getTile();
        
        if (currentTile == null) {
            return validMoves;
        }
        
        // Use breadth-first search to find all reachable tiles
        Queue<HexTile> queue = new LinkedList<>();
        Map<HexTile, Integer> costToReach = new HashMap<>();
        
        // Start from current tile
        queue.add(currentTile);
        costToReach.put(currentTile, 0);
        
        while (!queue.isEmpty()) {
            HexTile tile = queue.poll();
            int currentCost = costToReach.get(tile);
            
            // If this is not the current tile and it's empty, it's a valid move
            if (tile != currentTile && tile.getUnit() == null) {
                validMoves.add(tile);
            }
            
            // Get adjacent tiles
            List<HexTile> adjacentTiles = gameMap.getAdjacentTiles(tile);
            
            for (HexTile adjacent : adjacentTiles) {
                if (adjacent.getUnit() == null) {
                    int terrainCost = adjacent.getTerrainType().getMovementCost();
                    int newCost = currentCost + terrainCost;
                    
                    // Only add if we can afford the movement and haven't found a cheaper path already
                    if (newCost <= movementPoints && 
                        (!costToReach.containsKey(adjacent) || newCost < costToReach.get(adjacent))) {
                        queue.add(adjacent);
                        costToReach.put(adjacent, newCost);
                    }
                }
            }
        }
        */
        
        // NEW IMPLEMENTATION: Allow movement to any empty tile on the map
        HexTile currentTile = selectedUnit.getTile();
        
        // Add all empty tiles except the current one
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                HexTile tile = gameMap.getTile(x, y);
                if (tile != null && tile != currentTile && tile.getUnit() == null) {
                    validMoves.add(tile);
                }
            }
        }
        
        System.out.println("Valid moves for " + selectedUnit.getType() + ": " + validMoves.size() + 
                          " (unlimited movement)");
        
        return validMoves;
    }

    /**
     * Gets all valid attack targets for the selected unit.
     * @return List of valid target units
     */
    public List<Unit> getValidTargets() {
        List<Unit> validTargets = new ArrayList<>();
        if (selectedUnit == null || selectedUnit.getOwner() != getCurrentPlayer()) {
            return validTargets;
        }

        int range = selectedUnit.getAttackRange();
        HexTile currentTile = selectedUnit.getTile();
        
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                HexTile tile = gameMap.getTile(x, y);
                if (tile != null && tile != currentTile) {
                    Unit targetUnit = tile.getUnit();
                    if (targetUnit != null && targetUnit.getOwner() != getCurrentPlayer()) {
                        int distance = gameMap.getDistance(currentTile, tile);
                        if (distance <= range) {
                            validTargets.add(targetUnit);
                        }
                    }
                }
            }
        }
        
        return validTargets;
    }

    public List<Player> getPlayers() {
        return players;
    }
} 
