/// Daniel "Pablo" Popovich
/// September 16, 2026
/// This program performs A* search on a 2d grid that represents
/// a location with varying terrain

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/// takes 4 arguments. terrain image, elevation file, path file, and output image filename ni that order
public static void main(String[] args) {
    String imagePath = args[0];
    String elevationFile = args[1];
    String pathFile = args[2];
    String outputFile = args[3];

    try {
        BufferedImage image = ImageIO.read(new File(imagePath));
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error reading image file");
    }
}


/// Calculates straight distance from current location to goal
/// @param x1, y1, z1: coordinates of current point
/// @param x2, y2, z2: coordinates of goal point
/// @return: euclidean distance
public double heuristic(float x1, float y1, float z1, float x2, float y2, float z2) {
    double xdist=Math.pow(x1-x2,2);
    double ydist=Math.pow(y1-y2,2);
    double zdist=Math.pow(z1-z2,2);

    double dist = Math.sqrt(xdist+ydist+zdist);

    return dist;

}
