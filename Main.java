import java.util.ArrayList;

class Main {
    public static void main(String[] args) throws Exception {
        while(true){ // game loop
            // game setup
            System.out.println("How many players are playing?");
            int numPlayers;
            while(true){
                numPlayers = Library.numscan.nextInt();
                if(numPlayers > 0){
                    break;
                }
                System.out.println(Library.RED + "Please enter a positive number." + Library.RESET);
            }
            
            ArrayList<Player> playerList = new ArrayList<>();
            for(int i = 0; i < numPlayers; i++){
                playerList.add(new Player());
            }

            System.out.println("Will you be using your own dice? (y/n)");
            char diceSelect = Library.wordscan.nextLine().charAt(0);
            boolean realDice = false;
            if(diceSelect == 'y'){
                realDice = true;
            } else {
                realDice = false;
            }

            // play game
            roundLoop:
            while(true){
                for(int i = 0; i < numPlayers; i++){ // cycles through each player
                    // makes sure player is not finished
                    if(playerList.get(i).scoresheetFilled) continue;

                    System.out.println(Library.GREEN + "Player " + (i + 1) + Library.RESET);
                    playerList.get(i).print();
                    int[] dice = rollDice(realDice);
                    playerList.get(i).scoreDice(dice);
                    System.out.println();

                    // detects whether player is finished the game
                    boolean scoresheetFilled = true;
                    for(int score : playerList.get(i).getScores()){
                        if(score == -1){
                            scoresheetFilled = false;
                        }
                    }
                    if(scoresheetFilled){
                        playerList.get(i).scoresheetFilled = true;
                    }
                }
                boolean allPlayersFinished = true;
                for(Player tempPlayer : playerList){
                    if(!tempPlayer.scoresheetFilled){
                        allPlayersFinished = false;
                    }
                }
                if(allPlayersFinished){
                    break roundLoop;
                }

            } // end round loop

            // end game
            if(playerList.size() > 1){
                ArrayList<Integer> winnerIndexes = new ArrayList<>();
                int highScore = 0;
                for(int i = 0; i < playerList.size(); i++){
                    if(playerList.get(i).getGrandTotal() == highScore){
                        winnerIndexes.add(i);

                    } else if(playerList.get(i).getGrandTotal() > highScore){
                        highScore = playerList.get(i).getGrandTotal();
                        winnerIndexes.clear();
                        winnerIndexes.add(i);
                    }
                }
                if(winnerIndexes.size() == 1){
                    System.out.println(Library.YELLOW + "Player " + (winnerIndexes.get(0) + 1) + " won with " + highScore + " points!" + Library.RESET);
                } else {
                    System.out.print(Library.YELLOW + "Players " + (winnerIndexes.get(0) + 1));
                    for(int i = 1; i < winnerIndexes.size(); i++){
                        System.out.print(", " + (winnerIndexes.get(i) + 1));
                    }
                    System.out.println(" tied with " + highScore + " points!" + Library.RESET);
                }
                
            } else {
                System.out.println(Library.YELLOW + "You finished with " + playerList.get(0).getGrandTotal() + " points!" + Library.RESET);
            }
            
            System.out.println("\nPlay again? (y/n)");
            if(Library.wordscan.nextLine().toLowerCase().charAt(0) != 'y'){
                return;
            }

        } // end game loop

    } // end main

    public static int[] rollDice(boolean realDice){
        int[] dice = new int[5];
        if(realDice){
            System.out.println("Roll 3 times. For each roll you may choose to keep a dice value, or reroll the dice. Then,");
            System.out.println("Enter dice values in one line (ex. 12345):");
            String diceString;
            while(true){
                diceString = Library.wordscan.nextLine();
                if(diceString.length() == 5){
                    break;
                } else {
                    System.out.println(Library.RED + "Please enter exactly 5 digits." + Library.RESET);
                }
            }
            for(int i = 0; i < 5; i++){
                dice[i] = diceString.charAt(i) - '0';
            }

        } else {
            System.out.println(Library.RED + "Rolling Dice..." + Library.RESET);
            for(int i = 0 ; i < 5; i++){
                dice[i] = Library.getRandom(1, 6);
                System.out.println(Library.PINK + "Die " + (i + 1) + ": " + Library.RESET + dice[i]);
            }

            for(int i = 0; i < 2; i++){
                System.out.println("Choose the dice you would like to keep: (ex. 145)");
                String diceToKeep = Library.wordscan.nextLine();
                ArrayList<Integer> diceToKeepArr = new ArrayList<>();
                for(int j = 0; j < diceToKeep.length(); j++){
                    if(diceToKeep.charAt(j) == '1') diceToKeepArr.add(1);
                    else if(diceToKeep.charAt(j) == '2') diceToKeepArr.add(2);
                    else if(diceToKeep.charAt(j) == '3') diceToKeepArr.add(3);
                    else if(diceToKeep.charAt(j) == '4') diceToKeepArr.add(4);
                    else if(diceToKeep.charAt(j) == '5') diceToKeepArr.add(5);
                    else if(diceToKeep.charAt(j) == '6') diceToKeepArr.add(6);
                }
                
                // roll dice
                boolean allDiceKept = true;
                for(int j = 0; j < 5; j++){
                    if(!diceToKeepArr.contains(j + 1)){
                        allDiceKept = false;
                        dice[j] = Library.getRandom(1, 6);
                    }
                }
                if(allDiceKept) break;
                // print dice
                System.out.println(Library.RED + "Rolling Dice..." + Library.RESET);
                for(int j = 0; j < 5; j++){
                    System.out.println(Library.PINK + "Die " + (j + 1) + ": " + Library.RESET + dice[j]);
                }
            }
        }
        return dice;
    } // end rollDice
} // end class