import java.util.Arrays;
import java.util.ArrayList;

public class Player {
    public boolean scoresheetFilled;
    private int grandTotal;
    private int[] scores = new int[13];
    
    // 0 - ones;
    // 1 - twos;
    // 2 - threes;
    // 3 - fours;
    // 4 - fives;
    // 5 - sixes;
    // 6 - threeOfAKind;
    // 7 - fourOfAKind;
    // 8 - fullHouse;
    // 9 - smStraight;
    // 10 - lgStraight;
    // 11 - yahtzee;
    // 12 - chance;

    // constructor
    public Player(){
        scoresheetFilled = false;
        grandTotal = 0;
        Arrays.fill(scores, -1);
    } // end constructor

    public void scoreDice(int[] dice){
        System.out.println("What would you like to use this roll for?");

        // find out what the player wants to score
        ArrayList<Integer> canScore = new ArrayList<>();
        int choiceNum = 1;
        for(int i = 0; i < scores.length; i++){
            if(scores[i] == -1 || (i == 11 && scores[i] != 0)){
                System.out.println(Library.GREEN + choiceNum + ". " + getScoreTypeWithScore(dice, i) + Library.RESET);
                canScore.add(i);
                choiceNum++;
            }
            // } else if(i == 11 && (scores[i] != 0 || scores[i] != -1)){
            //     System.out.println(Library.GREEN + choiceNum + ". Yahtzee(" + getScoreTypeWithScore(dice, i) + Library.RESET);
            //     canScore.add(i);
            //     choiceNum++;
            // }
        }
        int scoreChoice = Library.numscan.nextInt() - 1;
        int scoreTypeIndex = canScore.get(scoreChoice);
        scores[scoreTypeIndex] = calcScore(dice, scoreTypeIndex);
        calcGrandTotal();
        print();
    }

    public int calcScore(int[] dice, int scoreTypeIndex){
        int total = 0;
        switch(scoreTypeIndex){
            
            case(0): // ones
            case(1): // twos
            case(2): // threes
            case(3): // fours
            case(4): // fives
            case(5): // sixes
            for(int die : dice){
                if(die == (scoreTypeIndex + 1)){
                    total += (scoreTypeIndex + 1);
                }
            }
            break;

            case(6): // 3 of a kind
            case(7): // 4 of a kind
            boolean legal = false;
            for(int die : dice){
                int numSame = 0;
                for(int i = 0; i < 5; i++){
                    if(die == dice[i]){
                        numSame++;
                    }
                }
                int multNum;
                if(scoreTypeIndex == 6){
                    multNum = 3;
                } else {
                    multNum = 4;
                }
                if(numSame >= multNum) legal = true;
            }
            if(legal){
                for(int die : dice){
                    total += die;
                }
            } else {
                return 0;
            }
            break;
            
            case(8): // full house
            int[] diceValues = new int[]{0,0,0,0,0,0};
            for(int i = 0; i < 5; i++){
                switch(dice[i]){
                    case(1):
                    diceValues[0]++;
                    break;
                    case(2):
                    diceValues[1]++;
                    break;
                    case(3):
                    diceValues[2]++;
                    break;
                    case(4):
                    diceValues[3]++;
                    break;
                    case(5):
                    diceValues[4]++;
                    break;
                    case(6):
                    diceValues[5]++;
                    break;
                }
            }
            boolean threeOfAKind = false;
            boolean twoOfAKind = false;
            for(int val : diceValues){
                if(val == 3){
                    threeOfAKind = true;
                } else if(val == 2){
                    twoOfAKind = true;
                }
            }
            if(threeOfAKind && twoOfAKind) total = 25;
            break;

            case(9): // sm. straight
            outerFor:
            for(int i = 0; i < 5; i++){
                // reduce number of dice to 4
                ArrayList<Integer> tempDice = new ArrayList<>();
                for(int j = 0; j < 5; j++){
                    if(!(j == i)){
                        tempDice.add(dice[j]);
                    }
                }

                // use modified scanning method from large straight
                // find smallest value
                int smallestNum = 7;
                for(int die: tempDice){
                    if(die < smallestNum){
                        smallestNum = die;
                    }
                }

                //find largest value
                int largestNum = 0;
                for(int die : tempDice){
                    if(die > largestNum){
                        largestNum = die;
                    }
                }
                if(largestNum - smallestNum < 3) continue outerFor;
                
                for(int j = 1; j < 4; j++){
                    boolean containsNextNum = false;
                    for(int die : tempDice){
                        if(die == smallestNum + j){
                            containsNextNum = true;
                        }
                    }
                    if(!containsNextNum){
                        continue outerFor;
                    } else if(smallestNum + j == largestNum){
                        return 30;
                    }
                }
            }
            return 0;

            case(10): // lg. straight
            // find smallest value
            int smallestNum = 7;
            for(int die: dice){
                if(die < smallestNum){
                    smallestNum = die;
                }
            }
            for(int i = 1; i < 5; i++){
                boolean containsNextNum = false;
                scanFor:
                for(int die : dice){
                    if(die == smallestNum + i){
                        containsNextNum = true;
                        break scanFor;
                    }
                }
                if(!containsNextNum){
                    return 0;
                }
            }
            total = 40;
            break;

            case(11): // yahtzee
            for(int i = 1; i < 5; i++){
                if(dice[i - 1] != dice[i]){
                    return 0;
                }
            }
            if(scores[11] == 0){
                return 0;
            } else if(scores[11] == -1){
                total = 50;
            } else {
                total = scores[11] += 100;
            }
            break;

            case(12): // chance
            for(int die : dice){
                total += die;
            }
            break;

            default:
            total = -1;
            break;
        }
        return total;
    } // end calcScore

    private int calcBonus(){
        int total = 0;
        for(int i = 0; i < 6; i++){
            if(scores[i] == -1) return -1;
            total += scores[i];
            
        }
        if(total >= 63){
            return 35;
        } else {
            return 0;
        }
    } // end calcBonus

    private void calcGrandTotal(){
        int tempTotal = 0;
        for(int score : scores){
            if(score != -1){
                tempTotal += score;
            }
        }
        if(!(calcBonus() == -1)){
            tempTotal += calcBonus();
        }
        grandTotal = tempTotal;
    }

    public void print(){
        System.out.println(Library.BLUE + "Scoresheet:" + Library.RESET);
        for(int i = 0; i < 6; i++){
            System.out.println(Library.CYAN + getScoreType(i) + ": " + Library.RESET + getScore(i));
        }
        // for bonus
        System.out.print(Library.YELLOW + "Bonus: " + Library.RESET);
        if(calcBonus() == -1) System.out.println("-");
        else System.out.println(calcBonus());

        for(int i = 6; i < 13; i++){
            System.out.println(Library.CYAN + getScoreType(i) + ": " + Library.RESET + getScore(i));
        }
        System.out.println(Library.YELLOW + "Grand Total: " + Library.RESET + grandTotal);
    } // end print

    private String getScore(int index){
        if(scores[index] == -1){
            return "-";
        } else {
            return Integer.toString(scores[index]);
        }
    }

    private String getScoreType(int index){
        switch(index){
            case(0):
            return "Ones";

            case(1):
            return "Twos";

            case(2):
            return "Threes";

            case(3):
            return "Fours";

            case(4):
            return "Fives";

            case(5):
            return "Sixes";

            case(6):
            return "3 of a Kind";

            case(7):
            return "4 of a kind";

            case(8):
            return "Full House";

            case(9):
            return "Sm. Straight";

            case(10):
            return "Lg. Straight";

            case(11):
            return "Yahtzee";

            case(12):
            return "Chance";

            default:
            return "";
        }
    } // end getScoreType

    private String getScoreTypeWithScore(int[] dice,int index){
        return getScoreType(index) + "(" + calcScore(dice, index) + ")";
    } // end getScoreTypeWithScore

    public int[] getScores(){
        return scores;
    } // end getScores

    public int getGrandTotal(){
        return grandTotal;
    } // end getGrandTotal
} // end class