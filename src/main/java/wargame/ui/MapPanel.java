package wargame.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import wargame.game.AssetManager;
import wargame.game.Game;
import wargame.map.GameMap;
import wargame.map.HexTile;
import wargame.terrain.TerrainType;
import wargame.unit.Unit;
import wargame.unit.UnitType;

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
    private GameWindow parentWindow;
    
    // Panning variables
    private int offsetX = 0;
    private int offsetY = 0;
    private Point lastMousePosition;
    private boolean isPanning = false;
    private static final int PAN_SPEED = 1;
    private static final int ZOOM_SPEED = 1;
    private double zoomFactor = 1.0;
    private boolean isNavigating = false;
    private static final double MIN_ZOOM = 1.0; // Minimum zoom (current scale)
    private static final double MAX_ZOOM = 2.0; // Maximum zoom

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
        this.selectedTile = null;
        
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);

        // Add mouse listeners for tile selection and navigation
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Left click for navigation or tile selection
                    lastMousePosition = e.getPoint();
                    isNavigating = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (isNavigating && !isPanning) {
                        // If we didn't pan, it was a click for tile selection
                        handleTileClick(e.getX(), e.getY());
                    }
                    isNavigating = false;
                    isPanning = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isNavigating && lastMousePosition != null) {
                    int dx = e.getX() - lastMousePosition.x;
                    int dy = e.getY() - lastMousePosition.y;
                    
                    // Only start panning if we've moved more than a small threshold
                    if (!isPanning && (Math.abs(dx) > 5 || Math.abs(dy) > 5)) {
                        isPanning = true;
                    }
                    
                    if (isPanning) {
                        // Calculate new offsets
                        int newOffsetX = offsetX + dx * PAN_SPEED;
                        int newOffsetY = offsetY + dy * PAN_SPEED;
                        
                        // Calculate map bounds
                        double hexWidth = getWidth() / (map.getWidth() * 0.75 + 0.25) * zoomFactor;
                        double hexHeight = getHeight() / (map.getHeight() + 0.5) * zoomFactor;
                        double mapWidth = map.getWidth() * hexWidth * 0.75;
                        double mapHeight = map.getHeight() * hexHeight;
                        
                        // Calculate bounds for panning
                        int minOffsetX = (int)(getWidth() - mapWidth);
                        int minOffsetY = (int)(getHeight() - mapHeight);
                        
                        // Apply bounds checking
                        offsetX = Math.min(0, Math.max(minOffsetX, newOffsetX));
                        offsetY = Math.min(0, Math.max(minOffsetY, newOffsetY));
                        
                        // Update last position
                        lastMousePosition = e.getPoint();
                        
                        // Repaint the panel
                        repaint();
                    }
                }
            }
        };

        // Add mouse wheel listener for zooming
        addMouseWheelListener(e -> {
            double oldZoom = zoomFactor;
            zoomFactor += e.getWheelRotation() * 0.1 * ZOOM_SPEED;
            zoomFactor = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoomFactor)); // Limit zoom range
            
            if (zoomFactor != oldZoom) {
                // Recalculate offsets to keep the center point stable
                double zoomRatio = zoomFactor / oldZoom;
                offsetX = (int)((offsetX - getWidth()/2) * zoomRatio + getWidth()/2);
                offsetY = (int)((offsetY - getHeight()/2) * zoomRatio + getHeight()/2);
                
                // Ensure we don't pan outside the map after zooming
                double hexWidth = getWidth() / (map.getWidth() * 0.75 + 0.25) * zoomFactor;
                double hexHeight = getHeight() / (map.getHeight() + 0.5) * zoomFactor;
                double mapWidth = map.getWidth() * hexWidth * 0.75;
                double mapHeight = map.getHeight() * hexHeight;
                
                int minOffsetX = (int)(getWidth() - mapWidth);
                int minOffsetY = (int)(getHeight() - mapHeight);
                
                offsetX = Math.min(0, Math.max(minOffsetX, offsetX));
                offsetY = Math.min(0, Math.max(minOffsetY, offsetY));
                
                repaint();
            }
        });

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate hex width and height to fill the panel
        double hexWidth = getWidth() / (map.getWidth() * 0.75 + 0.25) * zoomFactor;
        double hexHeight = getHeight() / (map.getHeight() + 0.5) * zoomFactor;

        // Apply offsets
        g2d.translate(offsetX, offsetY);

        // Draw each hex tile
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                HexTile tile = map.getTile(x, y);
                drawHexTile(g2d, tile, x, y, hexWidth, hexHeight);
            }
        }

        // Reset transform
        g2d.translate(-offsetX, -offsetY);
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
        
        // Draw terrain texture
        Image terrainImage = AssetManager.getTerrainImage(tile.getTerrainType());
        if (terrainImage != null) {
            // Fill with base color first to ensure no transparent areas
            g2d.setColor(getTerrainColor(tile.getTerrainType()));
            g2d.fill(hex);
            
            // Use clip to ensure the terrain image stays within the hex boundaries
            Shape oldClip = g2d.getClip();
            g2d.setClip(hex);
            
            // Size the image to fit the hex while maintaining aspect ratio
            double scale = 1.1; // Slightly larger scale to eliminate gaps
            int imgW = (int)(hexWidth * scale);
            int imgH = (int)(hexHeight * scale);
            g2d.drawImage(terrainImage, 
                (int)(centerX - imgW/2), 
                (int)(centerY - imgH/2), 
                imgW, imgH, null);
            
            // Restore the original clip
            g2d.setClip(oldClip);
        } else {
            // Fallback: use color
            g2d.setColor(getTerrainColor(tile.getTerrainType()));
            g2d.fill(hex);
        }
        
        // Draw hex border with slightly thicker stroke for clarity
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(hex);
        
        // Draw color overlays for movement and selection
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
        
        // Comment out the coordinate display
        /*
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, (int)(hexHeight / 5)));
        g2d.drawString(x + "," + y, (int)centerX - (int)(hexWidth/8), (int)centerY);
        */
        
        // Comment out terrain type letter display
        /*
        // Only show terrain type if not a valid move
        if ((validMoves == null || !validMoves.contains(tile)) && tile.getUnit() == null) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, (int)(hexHeight / 5)));
            g2d.drawString(tile.getTerrainType().toString().substring(0, 1),
                (int)centerX - (int)(hexWidth/12), (int)centerY + (int)(hexHeight/4));
        }
        */
        
        // Draw unit if present
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
        // First player = right (2), Second player = left (1)
        int facing = (unit.getOwner() == game.getPlayers().get(0)) ? 1 : 2;
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
        
        // Gray background for all health bars
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect((int)(centerX - healthBarWidth/2), (int)(centerY - hexHeight/2.5 - healthBarHeight - 2), healthBarWidth, healthBarHeight);
        
        // Choose health bar color based on unit ownership
        boolean isCurrentPlayerUnit = unit.getOwner() == game.getCurrentPlayer();
        
        // Use green for current player's units, red for enemies
        Color healthBarColor = isCurrentPlayerUnit ? Color.GREEN : Color.RED;
        g2d.setColor(healthBarColor);
        g2d.fillRect((int)(centerX - healthBarWidth/2), (int)(centerY - hexHeight/2.5 - healthBarHeight - 2), 
                    (int)(healthBarWidth * healthPercentage), healthBarHeight);
        
        // Add a border around the health bar for better visibility
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect((int)(centerX - healthBarWidth/2), (int)(centerY - hexHeight/2.5 - healthBarHeight - 2), 
                    healthBarWidth, healthBarHeight);

        // Draw movement points
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, (int)(hexHeight / 4)));
        String movementText = unit.getMovementPoints() + "/" + unit.getType().getMovement();
        
        // Create background for better visibility
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(movementText);
        int textHeight = fm.getHeight();
        int textX = (int)(centerX - textWidth / 2);
        int textY = (int)(centerY + hexHeight/2.5);
        
        // Draw text background for better visibility
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillRect(textX - 2, textY - textHeight + 4, textWidth + 4, textHeight);
        
        // Draw movement text
        g2d.setColor(Color.BLUE);
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
            case VILLAGE -> new Color(165, 42, 42);   // Brown
        };
    }

    private Color getUnitColor(Unit unit) {
        return unit.getOwner() == game.getCurrentPlayer() ? Color.BLUE : Color.RED;
    }

    private void handleTileClick(int mouseX, int mouseY) {
        // Convert mouse coordinates to hex coordinates, accounting for offset and zoom
        double hexWidth = getWidth() / (map.getWidth() * 0.75 + 0.25) * zoomFactor;
        double hexHeight = getHeight() / (map.getHeight() + 0.5) * zoomFactor;
        
        // Adjust mouse coordinates for offset and zoom
        double adjustedX = (mouseX - offsetX) / zoomFactor;
        double adjustedY = (mouseY - offsetY) / zoomFactor;
        
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                HexTile tile = map.getTile(x, y);
                double centerX = x * hexWidth * 0.75 + hexWidth / 2;
                double centerY = y * hexHeight + (x % 2) * hexHeight / 2 + hexHeight / 2;
                double dx = adjustedX - centerX;
                double dy = adjustedY - centerY;
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

    public void setParentWindow(GameWindow window) {
        this.parentWindow = window;
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
                int fromX = selectedUnit.getTile().getX();
                int fromY = selectedUnit.getTile().getY();
                boolean moved = game.moveSelectedUnit(tile.getX(), tile.getY());
                if (moved && parentWindow != null) {
                    parentWindow.logUnitMovement(selectedUnit, fromX, fromY, tile.getX(), tile.getY());
                }
            } else if (tileUnit != null && validTargets != null && validTargets.contains(tileUnit)) {
                // Try to attack
                int initialHealth = tileUnit.getCurrentHealth();
                boolean attacked = game.attackWithSelectedUnit(tileUnit);
                if (attacked && parentWindow != null) {
                    int damageDone = initialHealth - tileUnit.getCurrentHealth();
                    parentWindow.logUnitAttack(selectedUnit, tileUnit, damageDone);
                    
                    // Check if unit was destroyed
                    if (tileUnit.getCurrentHealth() <= 0) {
                        System.out.println("Unit destroyed in MapPanel.handleTileSelection");
                        // Remove duplicate log message
                        // parentWindow.infoPanel.addToGameLog(tileUnit.getOwner().getName() + "'s " 
                        //     + tileUnit.getType() + " was destroyed!");
                            
                        // Explicitly check victory condition here
                        System.out.println("Explicitly checking victory condition after unit destroyed...");
                        parentWindow.checkVictoryCondition();
                    }
                }
            }
            game.deselectUnit();
        }
    }
} 