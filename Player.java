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
    public GameState play(final GameState pState, final Deadline pDue) {

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */
        //Random random = new Random();
        //System.err.println("this is debug");

        //System.err.println();

        //System.err.println("this is debug");

        //return lNextStates.elementAt(random.nextInt(lNextStates.size()));
        GameState best = playMiniMax(pState, 3);
        return best;
    }

    public static int eval(GameState pState) {
        //return the difference between the chips of the current player and those of the opposite player
        int red_pieces = 0;
        int white_pieces = 0;

        // Count pieces
        for (int i = 0; i < pState.NUMBER_OF_SQUARES; i++) {
            if (0 != (pState.get(i) & Constants.CELL_RED)) {
                ++red_pieces;
            } else if (0 != (pState.get(i) & Constants.CELL_WHITE)) {
                ++white_pieces;
            }
        }

        //if the player is white
        if (pState.getNextPlayer()==Constants.CELL_RED)
            return white_pieces - red_pieces;
        else
            return red_pieces - white_pieces;

    }

    private GameState playMiniMax(GameState pState, int depth){

        Vector<GameState> nextStates = new Vector<GameState>();
        pState.findPossibleMoves(nextStates);
        int player = pState.getNextPlayer();
        int score = miniMax(nextStates.elementAt(0), player, depth -1, false);
        GameState state = nextStates.elementAt(0);

        for(int i = 1; i < nextStates.size(); i++){
                    int nextStateScore = miniMax(nextStates.elementAt(i), player, depth -1, false);
                    if(nextStateScore >= score) {
                        score = nextStateScore;
                        state = nextStates.elementAt(i);
                    }
        }

        return state;


    }

    private int miniMax(GameState pState, int player, int depth, boolean max){

        if(depth == 0 || pState.isEOG()) {
            return eval(pState);
        }
        else {
           
            Vector<GameState> nextStates = new Vector<GameState>();
            pState.findPossibleMoves(nextStates);

            if(max) {
                int score = Integer.MIN_VALUE;
                for(int i = 0; i < nextStates.size(); i++){
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
                    int nextStateScore = miniMax(nextStates.elementAt(i), player, depth -1, !max);
                    if(nextStateScore < score){
                        score = nextStateScore;
                    }
                }
                return score;
            }
        }




    }

    private int playMax(GameState pState, Vector<GameState> lNextStates, int depth) {

        if(depth == 0) {
            System.err.println("debug");
            System.err.println(eval(pState));
            return eval(pState);


        }
        else {
            --depth;
            // let's get the next possible state
            //init

            GameState bestState = lNextStates.elementAt(0);
            int eval = eval(bestState);
            for(int i = 0; i < lNextStates.size(); i++){
                GameState state = lNextStates.elementAt(i);

                GameState minState = state.reversed();
                Vector<GameState> nextMinStates = new Vector<GameState>();
                minState.findPossibleMoves(nextMinStates);

                int stateEval = playMin(minState, nextMinStates, depth);
                if (stateEval >= eval) {
                    eval = stateEval;
                    pState = bestState;
                }

            }    
        return eval;

        }

    } 

    private int playMin(GameState pState, Vector<GameState> lNextStates, int depth) {

        if(depth == 0) {
            System.err.println("debug");
            System.err.println(eval(pState));
            return eval(pState);
        }
        else {
            --depth;
            
            // let's get the next possible state
            //init
            GameState bestState = lNextStates.elementAt(0);
            int eval = eval(bestState);
            for(int i = 0; i < lNextStates.size(); i++){
                GameState state = lNextStates.elementAt(i);

                GameState minState = state.reversed();
                Vector<GameState> nextMinStates = new Vector<GameState>();
                minState.findPossibleMoves(nextMinStates);

                int stateEval = playMax(minState, nextMinStates, depth);
                if (stateEval <= eval) {
                    eval = stateEval;
                    pState = bestState;
                }

            }

        return eval;

        }
        
        
    }
     


}
