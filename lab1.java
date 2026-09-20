/// Daniel "Pablo" Popovich
/// September 16, 2026
/// This program performs A* search on a 2d grid that represents
/// a location with varying terrain]

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.swing.*;


//how the map scales horizontally and vertically, per pixel
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
    ROUGH_MEADOW(2),
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
    BufferedImage output = null;
    double[][] elevation = new double[500][395];
    Terrain[][] terr = new Terrain[500][395];
    LinkedList<int[]> locations = new LinkedList<>();


    //reads the image
    try {
        image = ImageIO.read(new File(imagePath));

    } catch (IOException e) {
        e.printStackTrace();
        System.err.println("Error reading image file");
    }


    //copies the image to edit
    BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            copy.setRGB(x, y, image.getRGB(x, y));
        }
    }


    //reads the path file
    try(BufferedReader br = new BufferedReader(new FileReader(pathFile))){

        String line;
        while((line = br.readLine()) != null){
            String[] coordinates = line.trim().split("\\s+");
            locations.add(new int[] {Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])});
        }
    } catch(IOException e) {
        e.printStackTrace();
        System.err.println("Error reading elevation file");
    }



    //reads the elevation file and puts the numbers into a 500x395 array
    //The assignment calls it a 400x500 file but ROWS COME FIRST. It should say 500x400
    //This confused me a bit which is why I'm pointing it out.
    String line;
    try(BufferedReader br = new BufferedReader(new FileReader(elevationFile))){
        for(int i=0; i<500; i++){
            int j = 0;
            line = br.readLine();
            if(line==null) {
                break;
            }
            String[] elevations = line.trim().split("\\s+");

            for (String el : elevations) {
                if(j<395) {
                    elevation[i][j] = Double.parseDouble(el);
                    j++;
                }
            }

        }
    } catch (IOException e){
        e.printStackTrace();
        System.err.println("Error reading elevation file");
    }



    //this loops through the image and fills each cell with the color value of the image
    for(int i=0; i<500; i++){
        for(int j=0; j<395; j++){
            int pixel = image.getRGB(j, i);
            Color pixelColor = new Color(pixel);

            //open land
            if(pixelColor.getRed()==248 &&  pixelColor.getGreen()==148 && pixelColor.getBlue()==18){
                terr[i][j]=Terrain.OPEN_LAND;
            }

            //rough meadow
            else if(pixelColor.getRed()==255 &&  pixelColor.getGreen()==192 && pixelColor.getBlue()==0){
                terr[i][j]=Terrain.ROUGH_MEADOW;
            }

            //easy moevement forest
            else if(pixelColor.getRed()==255 &&  pixelColor.getGreen()==255 && pixelColor.getBlue()==255){
                terr[i][j]=Terrain.EASY_MOVEMENT_FOREST;
            }

            //slow run forest
            else if(pixelColor.getRed()==2 &&  pixelColor.getGreen()==208 && pixelColor.getBlue()==60){
                terr[i][j]=Terrain.SLOW_RUN_FOREST;
            }

            //walk forest
            else if(pixelColor.getRed()==2 &&  pixelColor.getGreen()==136 && pixelColor.getBlue()==40){
                terr[i][j]=Terrain.WALK_FOREST;
            }

            //impassible vegetation
            else if(pixelColor.getRed()==5 &&  pixelColor.getGreen()==73 && pixelColor.getBlue()==24){
                terr[i][j]=Terrain.IMPASSIBLE_VEGETATION;
            }

            //water
            else if(pixelColor.getRed()==0 &&  pixelColor.getGreen()==0 && pixelColor.getBlue()==255){
                terr[i][j]=Terrain.WATER;
            }

            //paved road
            else if(pixelColor.getRed()==71 &&  pixelColor.getGreen()==51 && pixelColor.getBlue()==3){
                terr[i][j]=Terrain.PAVED_ROAD;
            }

            //foot path
            else if(pixelColor.getRed()==0 &&  pixelColor.getGreen()==0 && pixelColor.getBlue()==0){
                terr[i][j]=Terrain.FOOTPATH;
            }

            //out of bounds
            else{
                terr[i][j]=Terrain.OUT_OF_BOUNDS;
            }
        }
    }














    //REMOVE THIS. DONT NEED IT. JUST KEEP IT FOR TESTING
    ImageIcon icon = new ImageIcon(imagePath);
    JOptionPane.showMessageDialog(null, null, "Terrain", JOptionPane.INFORMATION_MESSAGE, icon);


















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




/// finds neighbors of a cell. Uses 4 cardinal directions
/// @param row: the row
/// @param col: the column
/// @return: an array of size 2 of the row and column
public ArrayList<int[]> getNeighbors(int row, int col){
    ArrayList<int[]> neighbors = new ArrayList<>();

    //if the spot is on the bottom
    if(row ==499){
        //bottom left corner
        if(col==0){
            neighbors.add(new int[] {row,col+1});
            neighbors.add(new int[] {row-1,col});
        }
        //bottom right corner
        else if(col==394){
            neighbors.add(new int[] {row,col-1});
            neighbors.add(new int[] {row-1,col});
        }

        //somwhere in middle
        else{
            neighbors.add(new int[] {row,col+1});
            neighbors.add(new int[] {row-1,col});
            neighbors.add(new int[] {row,col-1});
        }

    }

    //is spot is on the top
    else if(row == 0){
        //top left corner
        if(col==0){
            neighbors.add(new int[] {row,col+1});
            neighbors.add(new int[] {row+1,col});
        }
        //top right corner
        else if(col==394){
            neighbors.add(new int[] {row,col-1});
            neighbors.add(new int[] {row+1,col});
        }

        //somwhere in middle
        else{
            neighbors.add(new int[] {row,col-1});
            neighbors.add(new int[] {row+1,col});
            neighbors.add(new int[] {row,col+1});
        }




        //if spot is on the left side
    } else if(col ==0){

        //top left (it's redundant but whatever)
        if(row==0){
            neighbors.add(new int[] {row,col+1});
            neighbors.add(new int[] {row+1,col});

            //bottom left (it's redundant but whatever)
        } else if(row ==499){
            neighbors.add(new int[] {row,col+1});
            neighbors.add(new int[] {row-1,col});
        }

        //somwhere in the middle
        else {
            neighbors.add(new int[] {row,col+1});
            neighbors.add(new int[] {row-1,col});
            neighbors.add(new int[] {row+1,col});
        }
    }

    //the right side
    else if(col == 394){
        //top right (it's redundant but whatever)
        if(row==0){
            neighbors.add(new int[] {row,col-1});
            neighbors.add(new int[] {row+1,col});

            //bottom right (it's redundant but whatever)
        } else if(row ==499){
            neighbors.add(new int[] {row,col-1});
            neighbors.add(new int[] {row-1,col});
        }

        //somwhere in the middle
        else {
            neighbors.add(new int[] {row,col-1});
            neighbors.add(new int[] {row+1,col});
            neighbors.add(new int[] {row-1,col});
        }
    }

    //somwhere in the middle
    else{
        neighbors.add(new int[] {row,col-1});
        neighbors.add(new int[] {row,col+1});
        neighbors.add(new int[] {row+1,col});
        neighbors.add(new int[] {row-1,col});
    }



    return neighbors;
}

