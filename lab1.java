/// Daniel "Pablo" Popovich
/// September 16, 2026
/// This program performs A* search on a 2d grid that represents
/// a location with varying terrain

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

float xScale = 10.29f;
float yScale = 7.55f;

//enum with name of each terrain and how I chose their walkability
public enum Terrain{
    OPEN_LAND(1.0),
    PAVED_ROAD(1.0),
    FOOTPATH(1.0),
    EASY_MOVEMENT_FOREST(1.1),
    SLOW_RUN_FOREST(1.4),
    WALK_FOREST(1.5),
    IMPASSIBLE_VEGETATION(1000.0),
    WATER(3.0),
    OUT_OF_BOUNDS(1000.0);

    private final double cost;

    Terrain(double cost){
        this.cost = cost;
    }

    public double getCost() {
        return this.cost;
    }
}

/// takes 4 arguments. terrain image, elevation file, path file, and output image filename in that order
public static void main(String[] args) {
    String imagePath = args[0];
    String elevationFile = args[1];
    String pathFile = args[2];
    String outputFile = args[3];
    BufferedImage image = null;
    double[][] elevation = new double[395][500];

    //reads the image
    try {
        image = ImageIO.read(new File(imagePath));
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error reading image file");
    }

    //reads the elevation file and puts the numbers into a 395x500 array
    String line;
    try(BufferedReader br = new BufferedReader(new FileReader(elevationFile))){
        for(int i=0; i<395; i++){
            int j = 0;
            line = br.readLine();
            if(line==null) {
                break;
            }
            String[] elevations = line.trim().split("\\s+");

            for (String el : elevations) {
                elevation[i][j] = Double.parseDouble(el);
                j++;
            }

        }
    } catch (IOException e){
        System.out.println("Error reading elevation file");
    }


    for(int x=0; x<395; x++){
        for(int y=0; y<500; y++){

        }
    }
}


/// Calculates straight distance from current location to goal
/// @param x1, y1, z1: coordinates of current point
/// @param x2, y2, z2: coordinates of goal point
/// @return: euclidean distance
public float heuristic(float x1, float y1, float z1, float x2, float y2, float z2) {
    float xdist=(float) Math.pow(x1-x2,2);
    float ydist=(float) Math.pow(y1-y2,2);
    float zdist=(float) Math.pow(z1-z2,2);

    float dist = (float) Math.sqrt(xdist+ydist+zdist);

    return dist;

}


