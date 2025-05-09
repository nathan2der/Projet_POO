package wargame.ui;

import wargame.game.Game;
import wargame.map.GameMap;
import wargame.map.HexTile;
import wargame.terrain.TerrainType;
import wargame.unit.Unit;
import wargame.unit.UnitType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

/**
 * Panel that displays the game map with hexagonal tiles.
 */
public class MapPanel extends JPanel {
    private final Game game;
    private final GameMap map;
    private static final double HEX_ASPECT_RATIO = Math.sqrt(3) / 2; // Width/Height ratio of a hex
    private HexTile selectedTile;
    private List<HexTile> validMoves;
    private List<Unit> validTargets;
    private static final Map<String, BufferedImage> unitImages = new HashMap<>();
    private static BufferedImage questionImage;

    static {
        // Preload all unit images
        String[] types = {"guerrier", "archer", "chevalier", "mage"};
        String[] facings = {"1", "2"};
        for (String type : types) {
            for (String facing : facings) {
                String key = type + facing;
                String path = "/assets/units/" + type + facing + ".png";
                try {
                    unitImages.put(key, ImageIO.read(MapPanel.class.getResource(path)));
                } catch (Exception e) {
                    // Ignore, fallback will be used
                }
            }
        }
        try {
            questionImage = ImageIO.read(MapPanel.class.getResource("/assets/units/ptInterrogation.png"));
        } catch (Exception e) {
            questionImage = null;
        }
    }

    public MapPanel(Game game) {
        this.game = game;
        this.map = game.getGameMap();
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);

        // Add mouse listener for tile selection
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTileClick(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate hex width and height to fill the panel
        double hexWidth = getWidth() / (map.getWidth() * 0.75 + 0.25);
        double hexHeight = getHeight() / (map.getHeight() + 0.5);

        // Draw each hex tile
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                HexTile tile = map.getTile(x, y);
                drawHexTile(g2d, tile, x, y, hexWidth, hexHeight);
            }
        }
    }

    private void drawHexTile(Graphics2D g2d, HexTile tile, int x, int y, double hexWidth, double hexHeight) {
        double centerX = x * hexWidth * 0.75 + hexWidth / 2;
        double centerY = y * hexHeight + (x % 2) * hexHeight / 2 + hexHeight / 2;
        Path2D hex = new Path2D.Double();
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 3 * i;
            double xPos = centerX + (hexWidth / 2) * Math.cos(angle);
            double yPos = centerY + (hexHeight / 2) * Math.sin(angle);
            if (i == 0) {
                hex.moveTo(xPos, yPos);
            } else {
                hex.lineTo(xPos, yPos);
            }
        }
        hex.closePath();
        g2d.setColor(getTerrainColor(tile.getTerrainType()));
        g2d.fill(hex);
        if (validMoves != null && validMoves.contains(tile)) {
            g2d.setColor(new Color(0, 255, 0, 80));
            g2d.fill(hex);
            if (tile.getUnit() == null) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, (int)(hexHeight / 4)));
                g2d.drawString(String.valueOf(tile.getTerrainType().getMovementCost()),
                    (int)centerX - (int)(hexWidth/12), (int)centerY + (int)(hexHeight/8));
            }
        }
        if (validTargets != null && tile.getUnit() != null && validTargets.contains(tile.getUnit())) {
            g2d.setColor(new Color(255, 0, 0, 80));
            g2d.fill(hex);
        }
        if (tile == selectedTile) {
            g2d.setColor(new Color(255, 255, 0, 100));
            g2d.fill(hex);
        }
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(hex);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, (int)(hexHeight / 5)));
        g2d.drawString(x + "," + y, (int)centerX - (int)(hexWidth/8), (int)centerY);
        // Only show terrain type if not a valid move
        if ((validMoves == null || !validMoves.contains(tile)) && tile.getUnit() == null) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, (int)(hexHeight / 5)));
            g2d.drawString(tile.getTerrainType().toString().substring(0, 1),
                (int)centerX - (int)(hexWidth/12), (int)centerY + (int)(hexHeight/4));
        }
        Unit unit = tile.getUnit();
        if (unit != null) {
            drawUnit(g2d, unit, centerX, centerY, hexWidth, hexHeight);
        }
    }

    private void drawUnit(Graphics2D g2d, Unit unit, double centerX, double centerY, double hexWidth, double hexHeight) {
        // Determine sprite key
        String typeKey = switch (unit.getType()) {
            case INFANTRY, HEAVY_INFANTRY -> "guerrier";
            case CAVALRY -> "chevalier";
            case ARCHER -> "archer";
            case MAGE -> "mage";
        };
        // Player 1 = left (1), Player 2 = right (2)
        int facing = (unit.getOwner().getName().equals("Player 1")) ? 1 : 2;
        String key = typeKey + facing;
        BufferedImage img = unitImages.getOrDefault(key, questionImage);

        // Draw the image centered in the hex
        if (img != null) {
            int imgW = (int)(hexWidth * 0.9);
            int imgH = (int)(hexHeight * 0.9);
            g2d.drawImage(img, (int)(centerX - imgW/2), (int)(centerY - imgH/2), imgW, imgH, null);
        } else {
            // Fallback: draw colored ellipse
            g2d.setColor(getUnitColor(unit));
            g2d.fillOval((int)(centerX - hexWidth/2.5), (int)(centerY - hexHeight/2.5), (int)(hexWidth/1.25), (int)(hexHeight/1.25));
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval((int)(centerX - hexWidth/2.5), (int)(centerY - hexHeight/2.5), (int)(hexWidth/1.25), (int)(hexHeight/1.25));
        }

        // Draw health bar
        double healthPercentage = (double) unit.getCurrentHealth() / unit.getType().getHealth();
        int healthBarWidth = (int)(hexWidth/1.25);
        int healthBarHeight = (int)(hexHeight / 8);
        g2d.setColor(Color.RED);
        g2d.fillRect((int)(centerX - healthBarWidth/2), (int)(centerY - hexHeight/2.5 - healthBarHeight - 2), healthBarWidth, healthBarHeight);
        g2d.setColor(Color.GREEN);
        g2d.fillRect((int)(centerX - healthBarWidth/2), (int)(centerY - hexHeight/2.5 - healthBarHeight - 2), (int)(healthBarWidth * healthPercentage), healthBarHeight);

        // Draw movement points
        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Arial", Font.PLAIN, (int)(hexHeight / 4)));
        String movementText = unit.getRemainingMovement() + "/" + unit.getType().getMovement();
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (int)(centerX - fm.stringWidth(movementText) / 2);
        int textY = (int)(centerY + hexHeight/2.5 + fm.getHeight());
        g2d.drawString(movementText, textX, textY);

        // Draw attack range indicator if unit is selected
        if (unit == game.getSelectedUnit()) {
            g2d.setColor(new Color(255, 0, 0, 30));
            int range = unit.getType().getRange();
            for (int x = 0; x < map.getWidth(); x++) {
                for (int y = 0; y < map.getHeight(); y++) {
                    HexTile targetTile = map.getTile(x, y);
                    if (targetTile != null && targetTile.distanceTo(unit.getTile()) <= range) {
                        double targetCenterX = x * hexWidth * 0.75 + hexWidth / 2;
                        double targetCenterY = y * hexHeight + (x % 2) * hexHeight / 2 + hexHeight / 2;
                        g2d.fillOval(
                            (int)(targetCenterX - hexWidth/2.5),
                            (int)(targetCenterY - hexHeight/2.5),
                            (int)(hexWidth/1.25),
                            (int)(hexHeight/1.25)
                        );
                    }
                }
            }
        }
    }

    private String getUnitTypeSymbol(UnitType type) {
        return switch (type) {
            case INFANTRY -> "I";
            case HEAVY_INFANTRY -> "H";
            case CAVALRY -> "C";
            case ARCHER -> "A";
            case MAGE -> "M";
        };
    }

    private Color getTerrainColor(TerrainType terrain) {
        return switch (terrain) {
            case PLAINS -> new Color(144, 238, 144);  // Light green
            case FOREST -> new Color(34, 139, 34);    // Forest green
            case MOUNTAIN -> new Color(139, 137, 137); // Gray
            case WATER -> new Color(0, 191, 255);     // Deep sky blue
            case ROAD -> new Color(210, 180, 140);    // Tan
        };
    }

    private Color getUnitColor(Unit unit) {
        return unit.getOwner() == game.getCurrentPlayer() ? Color.BLUE : Color.RED;
    }

    private void handleTileClick(int mouseX, int mouseY) {
        // Convert mouse coordinates to hex coordinates
        double hexWidth = getWidth() / (map.getWidth() * 0.75 + 0.25);
        double hexHeight = getHeight() / (map.getHeight() + 0.5);
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                HexTile tile = map.getTile(x, y);
                double centerX = x * hexWidth * 0.75 + hexWidth / 2;
                double centerY = y * hexHeight + (x % 2) * hexHeight / 2 + hexHeight / 2;
                double dx = mouseX - centerX;
                double dy = mouseY - centerY;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= Math.min(hexWidth, hexHeight) / 2) {
                    selectedTile = tile;
                    handleTileSelection(tile);
                    updateValidMovesAndTargets();
                    repaint();
                    return;
                }
            }
        }
    }

    private void updateValidMovesAndTargets() {
        Unit selectedUnit = game.getSelectedUnit();
        if (selectedUnit != null) {
            validMoves = game.getValidMoves();
            validTargets = game.getValidTargets();
        } else {
            validMoves = null;
            validTargets = null;
        }
    }

    private boolean isPointInHex(int mouseX, int mouseY, int hexX, int hexY) {
        double hexWidth = getWidth() / (map.getWidth() * 0.75 + 0.25);
        double hexHeight = getHeight() / (map.getHeight() + 0.5);
        double centerX = hexX * hexWidth * 0.75 + hexWidth / 2;
        double centerY = hexY * hexHeight + (hexX % 2) * hexHeight / 2 + hexHeight / 2;
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        // Use ellipse equation for hit detection
        return (dx*dx)/(hexWidth*hexWidth/4) + (dy*dy)/(hexHeight*hexHeight/4) <= 1;
    }

    private void handleTileSelection(HexTile tile) {
        Unit selectedUnit = game.getSelectedUnit();
        Unit tileUnit = tile.getUnit();

        if (selectedUnit == null) {
            // Select unit if it belongs to current player
            if (tileUnit != null && tileUnit.getOwner() == game.getCurrentPlayer()) {
                game.selectUnit(tileUnit);
            }
        } else {
            // Try to move or attack
            if (tileUnit == null && validMoves != null && validMoves.contains(tile)) {
                // Try to move
                game.moveSelectedUnit(tile.getX(), tile.getY());
            } else if (tileUnit != null && validTargets != null && validTargets.contains(tileUnit)) {
                // Try to attack
                game.attackWithSelectedUnit(tileUnit);
            }
            game.deselectUnit();
        }
    }
} 