package wargame.game;

import wargame.player.Player;
import java.util.List;

/**
 * Defines and checks victory conditions for the game.
 * Handles different types of victory conditions and their evaluation.
 */
public class VictoryCondition {
    private final List<Player> players;
    private int turnLimit;
    private boolean eliminationEnabled;
    private boolean turnLimitEnabled;

    /**
     * Creates a new victory condition checker.
     * @param players The list of players in the game
     */
    public VictoryCondition(List<Player> players) {
        this.players = players;
        this.eliminationEnabled = true;  // Default to elimination victory
        this.turnLimitEnabled = false;   // Turn limit disabled by default
        this.turnLimit = 100;            // Default turn limit if enabled
    }

    /**
     * Sets whether elimination victory is enabled.
     * @param enabled true to enable elimination victory
     */
    public void setEliminationEnabled(boolean enabled) {
        this.eliminationEnabled = enabled;
    }

    /**
     * Sets whether turn limit victory is enabled.
     * @param enabled true to enable turn limit victory
     */
    public void setTurnLimitEnabled(boolean enabled) {
        this.turnLimitEnabled = enabled;
    }

    /**
     * Sets the turn limit for the game.
     * @param turns The number of turns before the game ends
     */
    public void setTurnLimit(int turns) {
        this.turnLimit = turns;
    }

    /**
     * Checks if the game is over based on current victory conditions.
     * @param currentTurn The current turn number
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver(int currentTurn) {
        // Check turn limit if enabled
        if (turnLimitEnabled && currentTurn > turnLimit) {
            return true;
        }

        // Check elimination if enabled
        if (eliminationEnabled) {
            int activePlayers = 0;
            for (Player player : players) {
                if (player.hasUnits()) {
                    activePlayers++;
                }
            }
            return activePlayers <= 1;
        }

        return false;
    }

    /**
     * Gets the winner of the game.
     * @param currentTurn The current turn number
     * @return The winning player, or null if there is no winner
     */
    public Player getWinner(int currentTurn) {
        if (!isGameOver(currentTurn)) {
            return null;
        }

        // If turn limit reached, player with most units wins
        if (turnLimitEnabled && currentTurn > turnLimit) {
            return getPlayerWithMostUnits();
        }

        // If elimination enabled, last player standing wins
        if (eliminationEnabled) {
            for (Player player : players) {
                if (player.hasUnits()) {
                    return player;
                }
            }
        }

        return null;
    }

    /**
     * Gets the player with the most units.
     * @return The player with the most units
     */
    private Player getPlayerWithMostUnits() {
        Player winner = null;
        int maxUnits = 0;

        for (Player player : players) {
            int unitCount = player.getUnits().size();
            if (unitCount > maxUnits) {
                maxUnits = unitCount;
                winner = player;
            }
        }

        return winner;
    }

    /**
     * Gets the current victory condition status.
     * @param currentTurn The current turn number
     * @return A string describing the current victory condition status
     */
    public String getVictoryStatus(int currentTurn) {
        if (turnLimitEnabled) {
            return String.format("Turns remaining: %d", Math.max(0, turnLimit - currentTurn));
        }
        return "Elimination victory enabled";
    }
} 