package com.bedfordwest;

/**
 * Created by Bedford on 3/28/2015.
 */

// import com.gletho.levels.LevelTile;
import com.gletho.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.*;


/**
 * This is a class to procedurally generate a map based on cellular autonoma and
 * store it as an array of integers. Regardless of map type, 0 will always stand
 * for a "dead" cell. Dead cells are meant to represent things like walls or
 * impassable terrain. As of the time of this writing, the only other cell type
 * is 1, which stands for a "living" cell. Currently only 2D arrays are
 * supported, but I hope to expand this functionality in the future.
 */
public class MapGenerator
{

    private static Logger logger =
        Logger.getLogger(MapGenerator.class.getName());
    private static final String TAG = MapGenerator.class.getName();

    private List<LevelTile> levelTiles = new ArrayList<LevelTile>();
    private boolean[][] map;
    int birthLimit = 4;
    int deathLimit = 3;
    int numberOfSteps = 6;

    // The only way for another class to get
    //   a map should be through this method
    public List<LevelTile> getMap(long seed) {
        generateMap(seed);
        return levelTiles;
    }

    // Create a new map with a long seed
    private void generateMap(long seed) {
        // Initialize the map with the seed
        seedMap(seed);
        // Refine the map for the number of steps
        for(int i = 0; i < numberOfSteps; i++) {
            doSimulationStep();
        }
        // Now copy the raw map into an array of level tiles
        createTileObjects();
    }

    // Seed the map with a seed value provided
    public void seedMap(long seed) {

        logger.log(java.util.logging.Level.INFO,
                "Trying to seed the world with seed: " + seed);
        Random rand = new Random();
        float chanceToStartAlive = (float)(rand.nextInt(10) / 100) + 0.40f;
        map = new boolean[Constants.LEVEL_X_TILES][Constants.LEVEL_Y_TILES];

        // Iterate over the entire level and set each tile to true or false
        for (int x = 0; x < Constants.LEVEL_X_TILES; x++) {
            for (int y = 0; y < Constants.LEVEL_Y_TILES; y++) {
                if (rand.nextFloat() < chanceToStartAlive) {
                    map[x][y] = true;
                }
            }
        }

    }

    // Use this method to further refine the map after initially generated
    private void doSimulationStep() {

        boolean[][] newMap =
                new boolean[Constants.LEVEL_X_TILES][Constants.LEVEL_Y_TILES];
        // Loop over each row and column of the map
        for(int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[0].length; y++) {
                int nbs = countAliveNeighbors(map, x, y);
                // The new value is based on our simulation rules
                // First, if a cell is alive but has too few neighbors, kill it
                if(map[x][y]) {
                    if(nbs < deathLimit) {
                        newMap[x][y] = false;
                    }
                    else {
                        newMap[x][y] = true;
                    }
                }
                // Otherwise, if the cell is dead now, check if it has the
                //   right number of neighbors to be 'born'
                else {
                    if(nbs > birthLimit) {
                        newMap[x][y] = true;
                    }
                    else {
                        newMap[x][y] = false;
                    }
                }
            }
        }
        map = newMap;
    }

    // Returns the number of cells in a ring around (x,y) that are alive
    private int countAliveNeighbors (boolean[][] map, int x, int y) {
        int count = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int neighbor_x = x+i;
                int neighbor_y = y+j;
                // If we're looking at the middle point
                if (i==0 && j==0) {
                    // Do nothing, we Don't want to add ourselves in!
                }
                // In case the index we're looking at is off the edge of map
                else if( neighbor_x < 0 || neighbor_y < 0 ||
                        neighbor_x >= map.length ||
                        neighbor_y >= map[0].length) {
                    count = count + 1;
                }
                // Otherwise, a normal check of the neighbor
                else if(map[neighbor_x][neighbor_y]) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    private void createTileObjects() {
        for (int x = 0; x < Constants.LEVEL_X_TILES; x++) {
            for (int y = 0; y < Constants.LEVEL_Y_TILES; y++) {
                LevelTile ltile = new LevelTile();
                ltile.setCellPosition(x,y);
                ltile.setPosition(
                        (x * Constants.TILE_WIDTH/Constants.WORLD_SCALE) +
                                ltile.getDimension().x/2,
                        y * Constants.TILE_HEIGHT/Constants.WORLD_SCALE +
                                ltile.getDimension().y/2
                );
                if (map[x][y]) {
                    ltile.setSolid(true);
                }
                else {
                    ltile.setSolid(false);
                }
                levelTiles.add(ltile);
            }
        }
    }

    // Getters
    public boolean[][] getBoolMap() { return this.map; }

}
