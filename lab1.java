/// Daniel "Pablo" Popovich
/// September 16, 2026
/// This program performs A* search on a 2d grid that represents
/// a location with varying terrain]

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.swing.*;


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
    BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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


    //does search between each point in the path file and colors each cell
    double totalCost=0;
    for(int i=0; i<locations.size()-1; i++){
        int[] current= locations.get(i);
        int[]next=locations.get(i+1);
        LinkedList<Node> seg = search(current[1], current[0], next[1], next[0], elevation, terr);
        totalCost+=seg.getLast().g;

        for( Node n : seg) {
            copy.setRGB(n.col, n.row, new Color(140, 39, 130).getRGB());
        }
    }

    //copies image
    output=copy;
    try{
        ImageIO.write(output, "png", new File(outputFile));
    } catch(IOException e){
        e.printStackTrace();
        System.err.println("Error writing output image");
    }

    System.out.println(totalCost);

}


/// Calculates straight distance from current location to goal
/// @param x1, y1, z1: coordinates of current point
/// @param x2, y2, z2: coordinates of goal point
/// @return: euclidean distance
public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
    //how the map scales horizontally and vertically, per pixel
    double yScale = 10.29;
    double xScale = 7.55;

    x1=x1*xScale;
    y1=y1*yScale;
    x2=x2*xScale;
    y2=y2*yScale;


    double xdist=(double) Math.pow(x1-x2,2);
    double ydist=(double) Math.pow(y1-y2,2);
    double zdist=(double) Math.pow(z1-z2,2);

    double dist = (double) Math.sqrt(xdist+ydist+zdist);

    return dist;

}




/// finds neighbors of a cell. Uses 4 cardinal directions
/// @param row: the row
/// @param col: the column
/// @return: an array of size 2 of the row and column
public static ArrayList<int[]> getNeighbors(int row, int col){
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



/// A* search algorithm
/// @param startRow: starting row
/// @param startCol: starting column
/// @param endRow: ending row
/// @param endCol: ending column
/// @param elevation: elevation of goal spot
/// @param terrain: terrain of goal spot
/// @return: the optimal path between two cells
public static LinkedList<Node> search(int startRow, int startCol, int endRow, int endCol, double[][]elevation, Terrain[][] terrain){
    Map<String, String> predecessor = new HashMap<>();
    predecessor.put(startRow+ ","+startCol, null);
    Set<String> visited = new HashSet<>();
    PriorityQueue<Node> toVisit = new PriorityQueue<>((a, b) -> Double.compare(a.f, b.f));

    //adds the first node to the queue
    toVisit.offer(new Node(startRow, startCol, 0, distance(startRow, startCol, elevation[startRow][startCol],endRow, endCol, elevation[endRow][endCol])));


    while (!toVisit.isEmpty()) {
        Node current = toVisit.remove();

        //this is to check if the node has been checked already
        if( visited.contains(current.row + "," + current.col) ){
            continue;
        }

        visited.add(current.row+","+current.col);
        ArrayList<int[]> neighbors = getNeighbors(current.row, current.col);
        String key;

        //this is if the current cell matches the goal cell
        if (current.row == endRow && current.col == endCol) {
            LinkedList<Node> path = new LinkedList<>();
            key = current.row +"," + current.col;

            //reconstructs the path. Each node has the g value of current just because I only care about the final node
            while(key!=null) {
                String[] split = key.split(",");
                path.addFirst(new Node(Integer.parseInt(split[0]), Integer.parseInt(split[1]), current.g, 0));
                key = predecessor.get(key);
            }

            return path;
        }


        //looks throughh each neighbor and adds them to the toVisit list
        for (int[] neighbor : neighbors) {

            if(visited.contains(neighbor[0] + "," + neighbor[1])){
                continue;
            }
            double travelCost = distance(current.row, current.col, elevation[current.row][current.col], neighbor[0], neighbor[1], elevation[neighbor[0]][neighbor[1]])*terrain[neighbor[0]][neighbor[1]].getCost();
            toVisit.offer(new Node(neighbor[0], neighbor[1], current.g+travelCost, current.g+travelCost+distance(neighbor[0],neighbor[1], elevation[neighbor[0]][neighbor[1]], endRow, endCol, elevation[endRow][endCol])));
            predecessor.put(neighbor[0] + "," + neighbor[1], current.row + "," + current.col);



        }

    }

    return null;
}


//I use this because I need to save the g score and f score of each point I visit
static class Node {
    int row; //row of the spot
    int col; //column of the spot
    double g; //g value
    double f; //f value

    Node (int row, int col, double g, double f) {
        this.row = row;
        this.col = col;
        this.g = g;
        this.f = f;
    }
}
