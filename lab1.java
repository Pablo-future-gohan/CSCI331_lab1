/// Daniel "Pablo" Popovich
/// September 16, 2026
/// This program performs A* search on a 2d grid that represents
/// a location with varying terrain

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

float xScale = 10.29f;
float yScale = 7.55f;


/// takes 4 arguments. terrain image, elevation file, path file, and output image filename in that order
public static void main(String[] args) {
    String imagePath = args[0];
    String elevationFile = args[1];
    String pathFile = args[2];
    String outputFile = args[3];
    BufferedImage image = null;

    try {
        image = ImageIO.read(new File(imagePath));
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error reading image file");
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
