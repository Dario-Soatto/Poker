import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
/**
 * This program sifts through data representing the rounds of poker played by Pluribus,
 * an AI trained to play poker through machine learning.
 * 
 * The data:
 * The data are publicly available at https://www.science.org/doi/10.1126/science.aay2400
 * and were generously reformated here: http://kevinwang.us/lets-analyze-pluribuss-hands/.
 * Each hand is represented as follows:
 * 
 * PokerStars Hand #117140: Hold'em No Limit (50/100) - 2019/07/12 08:32:20 ET
 * Table 'Pluribus Session 117' 6-max (Play Money) Seat #6 is the button
 * Seat 1: MrBlue (10000 in chips)
 * Seat 2: Joe (10000 in chips)
 * Seat 3: Bill (10000 in chips)
 * Seat 4: Pluribus (10000 in chips)
 * Seat 5: MrOrange (10000 in chips)
 * Seat 6: MrPink (10000 in chips)
 * MrBlue: posts small blind 50
 * Joe: posts big blind 100
 * *** HOLE CARDS ***
 * Dealt to MrBlue [6c 7s]
 * Dealt to Joe [Qd 5c]
 * Dealt to Bill [Qs Ks]
 * Dealt to Pluribus [4h Jh]
 * Dealt to MrOrange [Jc 2c]
 * Dealt to MrPink [Ts Th]
 * Bill: raises 125 to 225
 * Pluribus: folds
 * MrOrange: folds
 * MrPink: calls 225
 * MrBlue: folds
 * Joe: folds
 * *** FLOP *** [Ac Qh 9h]
 * Bill: checks
 * MrPink: bets 600
 * Bill: calls 600
 * *** TURN *** [Ac Qh 9h] [2h]
 * Bill: checks
 * MrPink: checks
 *  *** RIVER *** [Ac Qh 9h] [2h] [2d]
 * Bill: checks
 * MrPink: checks
 * *** SHOWDOWN ***
 * Bill: shows [Qs Ks]
 * Bill collected 1800.0 from pot
 * *** SUMMARY ***
 * Total pot 1800 | Rake 0
 * Board [Ac Qh 9h 2h 2d]
 * Seat 3: Bill showed [Qs Ks] and won (1800.0)
 * Seat 6: MrPink showed [Ts Th] and lost
 * 
 * The files:
 * pluribus_30.txt includes the vast majority of the data—10,949 hands. This file is used for training.
 * pluribus_118.txt includes the remainder of the data—100 hands. This file is used for validation.
 * 
 * The program:
 * The program sifts through either of the two files and then exports two 2-D arrays as .csv files.
 * In contrast to Poker.java, in this program, each function goes through the data for every player in the data
 * One .csv file represents inputs to the neural network. The inputs are formatted as a 2-D array as follows:
 *      Each row represents the information that a player in the data has before taking a certain action. Note that there may be multiple actions per hand
 *      The first number is the player's position at the table
 *      The second number is the number of players left in the round (i.e. the number who haven't folded)
 *      The third number is log base 5 of the highest bet. The logarithm is for feature scaling purposes
 *      The next 52 numbers are a one-hot vector encoding the first hole card.
 *          The cards are ordered from ace to king. The first 13 are spades, then clubs, diamonds, and hearts
 *      The next 52 numbers encode the second hole card
 *      The next 260 numbers are five one-hot vectors encoding the community cards.
 *          If there are not five community cards (as in most cases), the lack of community card is encoded as all zeros
 *      The number of rows is the number of inputs in the training set. Each row has 367 numbers.
 * 
 * The second .csv file represents the outputs of the neural network. The outputs are formatted as a 2-D array as follows:
 *      Each row represents the action taken by the player with the inputs corresponding to the row of the same index in the inputs
 *      Each row is a one-hot vector with four places.
 *          [1, 0, 0, 0] represents folding
 *          [0, 1, 0, 0] represents checking
 *          [0, 0, 1, 0] represents calling
 *          [0, 0, 0, 1] represents raising
 * 
 * The two .csv files can then be used to train a neural network in python.
 * 
 * To export the training data, make sure:
 *      String filepath goes to pluribus_30.txt
 *      String filename1 is input_data_2.csv
 *      String filename2 is output_data_2.csv
 * 
 *      
 * Uploaded to Git
 * 
 */

public class Poker2 {
    public static void main(String [] args) {
        System.out.println("Testing push");

        /*
         * Importing the .txt file with the data
         */
        String filePath = "/Users/dariosoatto/Documents/Programming/Poker Project/Poker/pluribus_118.txt";
        File file = new File(filePath);

        /*
         * Calculating the number of plays, or actions taken by each player
         * int plays: the number of plays
         */

        String[] players = {"Pluribus", "Bill", "Budd", "Eddie", "Gogo", "Hattori", "Joe", "MrBlonde", "MrBlue", "MrBrown", "MrOrange", "MrPink", "MrWhite"};
        Scanner scanner0;
        try {
            scanner0 = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        int plays = getPlays(file, players);
        System.out.println(plays);

        


        /*
         * Getting the hole cards held by the player each play
         * double[][] hands: an array where each row represents the hole cards pluribus had in that play encoded as a one-hot vector
         */
        
        double[][] hands = getHands(file, plays, players);
        //System.out.println(Arrays.deepToString(hands));

        
        /*
         * Getting the community cards
         * double[][] tables: an array where each row represents the community cards in that play encoded as a one-hot vector
         */
        
        double[][] tables = getTables(file, plays, players);
        //System.out.println(Arrays.deepToString(tables));


        /*
         * Getting the highest bet in each round
         * double[] bets: an array containing the log base 5 of the bet in each play
         */
        
        double[] bets = getBets(file, plays, players);
        //System.out.println(Arrays.toString(bets));
        
        /*
         * Getting the seat that Pluribus has in each play
         * double[] seats: an array containing Pluribus's seat in each play
         */
       
        double[] seats = getSeats(file, plays, players);
        //System.out.println(Arrays.toString(seats));



        /*
         * Getting the number of players left when the player makes each play
         * double[] pl: an array containing the number of players left at each play
         */
        
        double [] pl = getPl(file, plays, players);
        //System.out.println(Arrays.toString(pl));

        /*
         * Compiling all of the above arrays into one 2-D array
         */
        double[][] inputs = new double[plays][hands[0].length + tables[0].length + 3];
        for(int i = 0; i < plays; i++) {
            inputs[i][0] = seats[i];
            inputs[i][1] = pl[i];
            inputs[i][2] = bets[i];
            for(int j = 3; j < hands[0].length + 3; j++) {
                inputs[i][j] = hands[i][j-3];
            }
            for(int j = hands[0].length + 3; j < hands[0].length + tables[0].length + 3; j++) {
                inputs[i][j] = tables[i][j - 3 - hands[0].length];
            }

        }
        //System.out.println(Arrays.deepToString(inputs));

        
        


        /*
         * Getting the action taken by the player each play
         * double[][] outputs: an array where each row represents the action taken by Pluribus encoded as a one-hot vector
         * Index 0 is folding, 1 is checking, 2 is calling, and 3 is raising
         */
        
        double [][] outputs = getOutputs(file, plays, players);
        //System.out.println(Arrays.deepToString(outputs));
        
        /*
         * Exporting inputs[][] and outputs[][] as .csv files
         */
        String filename1 = "input_validation_data_2.csv";
        exportToCSV(inputs, filename1);
        String filename2 = "output_validation_data_2.csv";
        exportToCSV(outputs, filename2);

    }

    /**
     * Function to return the number of plays in the data
     * @param scanner a scanner to sift through the data
     * @return an integer with the number of plays in the data
     */
    public static int getPlays(File file, String [] players) {
        int c = 0;
        for(String x : players) {
            Scanner scanner0;
            try {
                scanner0 = new Scanner(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return -1;
            }

            while (scanner0.hasNextLine()) {
                String line = scanner0.nextLine();
                if(line.indexOf(x + ": ") != -1 && line.indexOf("blind") == -1 && line.indexOf("shows") == -1) {
                    c++;
                }
            }
        }
        return c;
    }

    

    /**
     * Function to get the hole cards held by the player
     * @param scanner1 a scanner to sift through the data
     * @param plays the number of plays in the data
     * @return a 2-D array where each row represents the hole cards in one play encoded as two one-hot vectors
     */
    public static double[][] getHands(File file, int plays, String[] players) {
        double[][] hands = new double[plays][];
        int i = 0;
        for(String x : players) {
            Scanner scanner1;
            try {
                scanner1 = new Scanner(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return hands;
            }
        

            while(i<plays && scanner1.hasNextLine()) {
                double[] h = getHand(scanner1, x);          
                while(true && scanner1.hasNextLine()) {
                    String line = scanner1.nextLine();
                    if(line.indexOf("SUMMARY") != -1) {
                        break;
                    }
                    if(line.indexOf(x + ": ") != -1 && line.indexOf("blind") == -1 && line.indexOf("shows") == -1){
                        //System.out.println(Arrays.toString(h));
                        hands[i] = h;
                        i++;
                    }
                }
            }
        }
        return hands;
    }


    /**
     * Function to return one set of whole cards held by the player
     * @param scanner1 scanner to sift through the data. Same scanner as getHands to ensure same position
     * @return a one-hot vector encoding the two cards held
     */
    public static double[] getHand(Scanner scanner1, String x) {
        double [] hand = new double[104];
        while (scanner1.hasNextLine()) {
            String line = scanner1.nextLine();
            if(line.indexOf("Dealt to " + x) != -1) {
                int i = line.indexOf("Dealt to " + x);
                String s = line.substring(i + 11 + x.length(), i + 13 + x.length());
                double[] c1 = getCard(s);
                s = line.substring(i + 14 + x.length(), i + 16 + x.length());
                double[] c2 = getCard(s);
                for(int j = 0; j < 52; j++) {
                    hand[j] = c1[j];
                }
                for(int j = 52; j < 104; j++) {
                    hand[j] = c2[j-52];
                }
                break;
            }
        }
        return hand;
    }

    /**
     * Function to get the community cards at each action
     * @param scanner2 a scanner to sift through the data
     * @param plays the number of plays in the data
     * @return a 2-D array where each row represents the community cards in that play encoded as one-hot vectors
     */
    public static double[][] getTables(File file, int plays, String[] players) {
        double[][] tables = new double [plays][260];
        int i = 0;
        for(String x : players) {
            Scanner scanner2;
            try {
                scanner2 = new Scanner(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return tables;
            }
            double[] table = new double[260];
            while(scanner2.hasNextLine()) {
                String line = scanner2.nextLine();
                if(line.indexOf("FLOP") != -1) {
                    System.out.println(line);
                    int index = line.indexOf("FLOP");
                    String s = line.substring(index + 10, index + 12);
                    double [] c1 = getCard(s);  
                    s = line.substring(index + 13, index + 15);
                    double [] c2 = getCard(s);
                    s = line.substring(index + 16, index + 18);
                    double [] c3 = getCard(s);
                    for(int j = 0; j < 52; j++) {
                        table[j] = c1[j];
                    }
                    for(int j = 52; j < 104; j++) {
                        table[j] = c2[j-52];
                    }
                    for(int j = 104; j < 156; j++) {
                        table[j] = c3[j-104];
                    }
                } else if(line.indexOf("TURN") != -1) {
                    int index = line.indexOf("TURN");
                    String s = line.substring(index + 21, index + 23);
                    double [] c4 = getCard(s);
                    for(int j = 156; j < 208; j++) {
                        table[j] = c4[j-156];
                    }
                } else if(line.indexOf("RIVER") != -1) {
                    int index = line.indexOf("RIVER");
                    String s = line.substring(index + 27, index + 29);
                    double [] c5 = getCard(s);
                    for(int j = 208; j < 260; j++) {
                        table[j] = c5[j-208];
                    }
                } else if(line.indexOf(x + ": ") != -1 && line.indexOf("blind") == -1  && line.indexOf("shows") == -1){
                    for(int j = 0; j < 260; j++) {
                        tables[i][j] = table[j];
                    }
                    i++;
                    
                } else if(line.indexOf("PokerStars") != -1) {
                    Arrays.fill(table, 0);
                }
            }
        }
        
        return tables;
    }
    
    /**
     * Function to return the log base 5 of the highest bet at each play
     * @param scanner3 a scanner to sift through the data
     * @param plays the number of plays in the data
     * @return the log base 5 of the highest bet in each play
     */
    public static double[] getBets(File file, int plays, String[] players) {
        double[] bets = new double[plays];
        double bet = 0;
        int i = 0;
        for(String x : players) {
            Scanner scanner3;
            try {
                scanner3 = new Scanner(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return bets;
            }
            while(scanner3.hasNextLine()) {
                String line = scanner3.nextLine();
                if(line.indexOf("posts big") != -1) {
                    bet = 100;
                }
                if((line.indexOf("raises") != -1 || line.indexOf("bets") != -1 ) && line.indexOf(x) == -1) {
                    Pattern pattern = Pattern.compile("\\d+$");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        // Get the matched number as a string
                        String numberAsString = matcher.group();
            
                        // Convert the string number to an integer
                        int number = Integer.parseInt(numberAsString);
            
                        // Print the extracted number
                        if( (double) number >= bet) {
                            bet = (double) number;
                        }
                    } else if(line.indexOf("all-in") != -1) {
                        bet = 10000;
                    } else {
                        System.out.println("No number found at the end of the string.");
                        System.out.println(line);
                    }
                }
                if(line.indexOf(x + ": ") != -1 && line.indexOf("blind") == -1  && line.indexOf("shows") == -1){
                    bets[i] = Math.log(bet) / Math.log(5);
                    i++;
                }

            }
        }
        
        return bets;
    }

    /**
     * Function to return the player's seat at each play
     * @param scanner4 a scanner to sift through the data
     * @param plays the number of plays in the data
     * @return Pluribus's seat in each play
     */
    public static double[] getSeats(File file, int plays, String[] players) {
        double[] seats = new double[plays];
        int i = 0;
        double seat = 0;
        for(String x : players) {
            Scanner scanner4;
            try {
                scanner4 = new Scanner(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return seats;
            }
            while(scanner4.hasNextLine()) {
                String line = scanner4.nextLine();
                if(line.indexOf(": " + x) != -1 && line.indexOf("Seat") != -1){
                    seat = (double) Integer.parseInt(line.substring(5,6));
                }
                if(line.indexOf(x + ": ") != -1 && line.indexOf("blind") == -1  && line.indexOf("shows") == -1){
                    seats[i] = seat;
                    i++;
                }
            }
        }
        
        return seats;
    }

    /**
     * Function to return the number of players left at each of the player's plays
     * @param scanner5 a scanner to sift through the data
     * @param plays the number of plays in the data
     * @return the number of players who haven't folded at the time Pluribus plays each action
     */
    public static double[] getPl(File file, int plays, String[] players) {
        double[] pl = new double[plays];
        int i = 0;
        double p = 0;
        for(String x : players) {
            Scanner scanner5;
            try {
                scanner5 = new Scanner(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return pl;
            }
            while(scanner5.hasNextLine()) {
                String line = scanner5.nextLine();
                if(line.indexOf("folds") != -1 && line.indexOf(x + ": ") == -1){
                    p++;
                }
                if(line.indexOf(x + ": ") != -1 && line.indexOf("blind") == -1  && line.indexOf("shows") == -1){
                    pl[i] = (double) 6 - p;
                    i++;
                }
                if(line.indexOf("SUMMARY") != -1) {
                    p = 0;
                }
            }
        }
        return pl;
    }

    /**
     * Function to encode all of the player's actions
     * @param scanner6 a scanner to sift through the data
     * @param plays the number of plays in the data
     * @return a 2-D array where each row is a one-hot vector encoding Pluribus's action
     */
    public static double[][] getOutputs(File file, int plays, String[] players) {
        double [][] outputs = new double[plays][4];
        int i = 0;
        for(String x : players) {
            Scanner scanner6;
            try {
                scanner6 = new Scanner(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return outputs;
                
            }
            while(scanner6.hasNextLine()) {
                String line = scanner6.nextLine();
                if(line.indexOf(x + ": ") != -1 && line.indexOf("blind") == -1  && line.indexOf("shows") == -1){
                    double [] action = {0, 0, 0, 0};
                    if(line.indexOf("folds") != -1) {
                        action[0] = 1.0;
                    } else if(line.indexOf("checks") != -1) {
                        action[1] = 1.0;
                    } else 
                    if(line.indexOf("calls") != -1) {
                        action[2] = 1.0;
                    } else 
                    if(line.indexOf("raises") != -1 || line.indexOf("bets") != -1) {
                        action[3] = 1.0;
                    } else {
                        System.out.println(line);
                    }
                    for(int j = 0; j < 4; j++) {
                        outputs[i][j] = action[j];
                    }
                    i++;
                }
            }
            
        }
        return outputs;
    }





















    /**
     * Helper function to encode a card as a one-hot vector given its string format
     * @param st the card represented as a string
     * @return the card represented as a one-hot vector
     */
    public static double[] getCard(String st) {
        String s = st.substring(0,1);
        int c = 0;
        if(s.equals("A")) {
            c = 1;
        } else if(s.equals("2")) {
            c = 2;
        } else if(s.equals("3")) {
            c = 3;
        } else if(s.equals("4")) {
            c = 4;
        } else if(s.equals("5")) {
            c = 5;
        } else if(s.equals("6")) {
            c = 6;
        } else if(s.equals("7")) {
            c = 7;
        } else if(s.equals("8")) {
            c = 8;
        } else if(s.equals("9")) {
            c = 9;
        } else if(s.equals("T")) {
            c = 10;
        } else if(s.equals("J")) {
            c = 11;
        } else if(s.equals("Q")) {
            c = 12;
        } else if(s.equals("K")) {
            c = 13;
        } else {
            c = -1000;
        }

        String t = st.substring(1,2);
        if(t.equals("s")) {
            c = c;
        } else if(t.equals("c")) {
            c += 13;
        } else if(t.equals("d")) {
            c += 26;
        } else if(t.equals("h")) {
            c += 39;
        } else {
            c = 0;
        }
        double[] card = new double[52];
        Arrays.fill(card, 0);
        card[c-1] = 1;
        return card;

    }

    /**
     * Function to export an array as a .csv
     * Courtesy of ChatGPT :)
     * @param array the array to convert to a .csv
     * @param filename the name of the .csv to export to
     */
    public static void exportToCSV(double[][] array, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (double[] row : array) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    sb.append(row[i]);
                    if (i < row.length - 1) {
                        sb.append(",");
                    }
                }
                writer.write(sb.toString());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}