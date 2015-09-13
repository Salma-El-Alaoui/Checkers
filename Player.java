import java.util.*;

public class Player {
    /**
     * Performs a move
     *
     * @param pState
     *            the current state of the board
     * @param pDue
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */

    private Deadline mPDue;
    private static final long SECOND = 1000000000L;
    private static final long HUNDRED_MILLIS = 100000000L;
    private static final long MILLIS = 1000000L;
    private int mNodes = -1;
    private int mPruned = -1;

    public GameState play(final GameState pState, final Deadline pDue) {

        mPDue = pDue;

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        GameState best = bestMoveAlphaBeta(pState, 9);
        return best;
    }

    public GameState playRandom(final GameState pState, final Deadline pDue) {

        mPDue = pDue;

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        Random random = new Random();
        GameState best = lNextStates.elementAt(random.nextInt(lNextStates.size()));

        return best;
    }

    public GameState playMiniMax(final GameState pState, final Deadline pDue) {

        mPDue = pDue;
        mNodes = 0;

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        GameState best = bestMoveMiniMax(pState, 7);

        System.err.println("Total nodes expanded: " + mNodes);
        printTimeLeft();
        return best;
    }

    public GameState playAlphaBeta(final GameState pState, final Deadline pDue) {

        mPDue = pDue;
        mNodes = 0;
        mPruned = 0;

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        GameState best = bestMoveAlphaBeta(pState, 9);

        System.err.println("Total nodes expanded: " + mNodes + " Pruned N-times: " + mPruned);
        printTimeLeft();
        return best;
    }

/*
=========================================================================================
=========================================Private=========================================
=========================================================================================
*/

    /**
     *  print timeleft for debug
     */
    private void printTimeLeft(){

        float timeLeft = mPDue.timeUntil()/ 1000000f;
        System.err.println("Time left in ms: " + timeLeft);

    }

    private boolean isTimeleft(long atLeast){

        if( mPDue.timeUntil() <= atLeast){
//            printTimeLeft();
            return false;
        }
        return true;

    }


    private GameState bestMoveAlphaBeta(GameState pState, int depth){

        Vector<GameState> nextStates = new Vector<GameState>();
        pState.findPossibleMoves(nextStates);


        int alpha = Integer.MAX_VALUE*-1;
        int beta = Integer.MAX_VALUE;

        int player = pState.getNextPlayer();
        GameState state = nextStates.elementAt(0);
        alpha = alphaBeta(nextStates.elementAt(0), player, depth -1, false, alpha, beta);
        mNodes++;

        for(int i = 1; i < nextStates.size(); i++){

                    mNodes++;
                    int nextStateScore = alphaBeta(nextStates.elementAt(i), player, depth -1, false, alpha, beta);
                    if(nextStateScore > alpha) {
                        alpha = nextStateScore;
                        state = nextStates.elementAt(i);
                    }
                    if(beta <= alpha) {
                        mPruned++;
                        break;
                    }
        }

        return state;
    }


    private int alphaBeta(GameState pState, int player, int depth, boolean max, int alpha, int beta) {

        if(depth == 0 || pState.isEOG()) {
            return eval(pState, player);
        }
        else {
           
            Vector<GameState> nextStates = new Vector<GameState>();
            pState.findPossibleMoves(nextStates);

            if(max) {

                for(int i = 0; i < nextStates.size(); i++){
                    mNodes++;
                    int nextStateScore = alphaBeta(nextStates.elementAt(i), player, depth -1, !max, alpha, beta);
                    if(nextStateScore >= alpha){
                        alpha = nextStateScore;
                    }
                    if(beta <= alpha) {
                        mPruned++;
                        break;
                    }

                }
                return alpha;
            }
            else {
                for(int i = 0; i < nextStates.size(); i++){
                    mNodes++;
                    int nextStateScore = alphaBeta(nextStates.elementAt(i), player, depth -1, !max, alpha, beta);
                    if(nextStateScore <= beta){
                        beta = nextStateScore;
                    }
                    if(beta <= alpha) {
                        mPruned++;
                        break;
                    }
                }
                return beta;
            }
        }

    }
    

    private GameState bestMoveMiniMax(GameState pState, int depth){

        Vector<GameState> nextStates = new Vector<GameState>();
        pState.findPossibleMoves(nextStates);
        int player = pState.getNextPlayer();
        int score = miniMax(nextStates.elementAt(0), player, depth -1, false);
        GameState state = nextStates.elementAt(0);
        mNodes++;

        for(int i = 1; i < nextStates.size(); i++){
//                    if(!isTimeleft(2*HUNDRED_MILLIS)){
//                        return state;
//                    }
                    mNodes++;
                    int nextStateScore = miniMax(nextStates.elementAt(i), player, depth -1, false);
                    if(nextStateScore >= score) {
                        score = nextStateScore;
                        state = nextStates.elementAt(i);
                    }
        }

        return state;
    }

    private int miniMax(GameState pState, int player, int depth, boolean max) {

        if(depth == 0 || pState.isEOG()  ) { // || !isTimeleft(HUNDRED_MILLIS)
            return eval(pState, player);
        }

        else {
           
            Vector<GameState> nextStates = new Vector<GameState>();
            pState.findPossibleMoves(nextStates);

            if(max) {
                int score = Integer.MIN_VALUE;
                for(int i = 0; i < nextStates.size(); i++){
                    mNodes++;
                    int nextStateScore = miniMax(nextStates.elementAt(i), player, depth -1, !max);
                    if(nextStateScore >= score){
                        score = nextStateScore;
                    }
                }
                return score;
            }
            else {
                int score = Integer.MAX_VALUE;
                for(int i = 0; i < nextStates.size(); i++){
                    mNodes++;
                    int nextStateScore = miniMax(nextStates.elementAt(i), player, depth -1, !max);
                    if(nextStateScore < score){
                        score = nextStateScore;
                    }
                }
                return score;
            }
        }

    }

    private static int eval(GameState pState, int player) {
        //return the difference between the chips of the current player and those of the opposite player
        int red_pieces = 0;
        int white_pieces = 0;
        int red_kings = 0;
        int white_kings = 0;
        int score = 0;

        // Count pieces
        for (int i = 0; i < pState.NUMBER_OF_SQUARES; i++) {
            if (0 != (pState.get(i) & Constants.CELL_RED)) {
                ++red_pieces;

                if (0 != (pState.get(i) & Constants.CELL_KING)){
                    ++red_kings;
                }

            } else if (0 != (pState.get(i) & Constants.CELL_WHITE)) {
                ++white_pieces;

                if (0 != (pState.get(i) & Constants.CELL_KING)) {
                    ++white_kings;
                }
            }
        }



        //if the player is white
        if (player == Constants.CELL_RED) {
            score = red_pieces - white_pieces;

            //penalty if opponent gets a king
            score -= white_kings * 100;
        }
        else {
            score = white_pieces - red_pieces;
            //penalty if opponent gets a king
            score -= red_kings * 100;
        }

        if (((pState.isRedWin()) && (player == Constants.CELL_RED)) || ((pState.isWhiteWin()) && (player == Constants.CELL_WHITE))){
            score += 2000;
        }
        else if (((pState.isWhiteWin()) && (player == Constants.CELL_RED) )|| ((pState.isRedWin() && player == Constants.CELL_WHITE)) ){
            score -= 2000;
        }
        else if (pState.isDraw()) {
            score -= 5000;
        }

        return score;

    }

     


}
