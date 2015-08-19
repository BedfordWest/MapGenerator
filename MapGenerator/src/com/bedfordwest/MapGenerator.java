package com.bedfordwest;

/**
 * Created by Bedford on 3/28/2015.
 */

import java.util.*;
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

    public static void main (String[] arg) {
        Scanner reader = new Scanner(System.in);
        Random seedRand = new Random();
        MapGenerator mapGen = new MapGenerator();
        int x,y;
        System.out.println("Enter the number of x map tiles:");
        x = reader.nextInt();
        System.out.println("Enter the number of y map tiles:");
        y = reader.nextInt();
        System.out.println("Your map array is:");
        int map[][] = mapGen.getMap(seedRand.nextLong(), x, y);
        System.out.println(Arrays.deepToString(map));
    }

    private static Logger logger =
        Logger.getLogger(MapGenerator.class.getName());
    private static final String TAG = MapGenerator.class.getName();

    private int[][] levelTiles;
    private boolean[][] map;
    int birthLimit = 4;
    int deathLimit = 3;
    int numberOfSteps = 6;
    int level_x_tiles, level_y_tiles = 0;

    // The only way for another class to get
    //   a map should be through this method
    public int[][] getMap(long seed, int x_tiles, int y_tiles) {
        levelTiles = new int[x_tiles][y_tiles];
        level_x_tiles = x_tiles;
        level_y_tiles = y_tiles;
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
    }

    // Seed the map with a seed value provided
    public void seedMap(long seed) {

        logger.log(java.util.logging.Level.INFO,
                "Trying to seed the world with seed: " + seed);
        Random rand = new Random();
        float chanceToStartAlive = (float)(rand.nextInt(10) / 100) + 0.40f;

        // Iterate over the entire level and set each tile to true or false
        for (int x = 0; x < level_x_tiles; x++) {
            for (int y = 0; y < level_y_tiles; y++) {
                if (rand.nextFloat() < chanceToStartAlive) {
                    levelTiles[x][y] = 1;
                }
                else levelTiles[x][y] = 0;
            }
        }

    }

    // Use this method to further refine the map after initially generated
    private void doSimulationStep() {

        int[][] newMap = new int[level_x_tiles][level_y_tiles];
        // Loop over each row and column of the map
        for(int x = 0; x < levelTiles.length; x++) {
            for(int y = 0; y < levelTiles[0].length; y++) {
                int nbs = countAliveNeighbors(levelTiles, x, y);
                // The new value is based on our simulation rules
                // First, if a cell is alive but has too few neighbors, kill it
                if(levelTiles[x][y] == 1) {
                    if(nbs < deathLimit) {
                        newMap[x][y] = 0;
                    }
                    else {
                        newMap[x][y] = 1;
                    }
                }
                // Otherwise, if the cell is dead now, check if it has the
                //   right number of neighbors to be 'born'
                else {
                    if(nbs > birthLimit) {
                        newMap[x][y] = 1;
                    }
                    else {
                        newMap[x][y] = 0;
                    }
                }
            }
        }
        levelTiles = newMap;
    }

    // Returns the number of cells in a ring around (x,y) that are alive
    private int countAliveNeighbors (int[][] map, int x, int y) {
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
                else if(map[neighbor_x][neighbor_y] == 0) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    // Getters
    public boolean[][] getBoolMap() { return this.map; }

}
