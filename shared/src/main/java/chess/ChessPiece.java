package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    //checks if the desired end position is on the board, then checks to see if there's something there
    public boolean viableDestination(ChessBoard board, ChessPosition endPosition) {
        int row = endPosition.getRow();
        int col = endPosition.getColumn();
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }
        ChessPiece target = board.getPiece(endPosition);
        if (target == null) {
            return true;
        }
        else {
            return target.getTeamColor() != this.pieceColor;
        }
    }

    //creates array list and calls addDirectionalMoves to add each direction one by one
    private Collection<ChessMove> directionalMoves(
            ChessBoard board,
            ChessPosition position,
            int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int[] dir : directions) {
            addDirectionalMoves(
                    moves,
                    board,
                    position,
                    dir[0],
                    dir[1]);
        }
        return moves;
    }

    //increments the direction and checks to see if it is still valid: if yes, add the move
    private void addDirectionalMoves(
            Collection<ChessMove> moves,
            ChessBoard board,
            ChessPosition start,
            int rowA,
            int colA) {
        int row = start.getRow();
        int col = start.getColumn();
        while (true) {
            row += rowA;
            col += colA;
            ChessPosition newPos =
                    new ChessPosition(row, col);
            if (!viableDestination(board, newPos)) {
                break;
            }
            moves.add(
                    new ChessMove(start, newPos, null));
            if (board.getPiece(newPos) != null) {
                break;
            }
        }
    }

    //
    private void addSingleMoves(
            Collection<ChessMove> moves,
            ChessBoard board,
            ChessPosition start,
            int[][] directions) {
        for (int[] dir : directions) {
            int row = start.getRow() + dir[0];
            int col = start.getColumn() + dir[1];
            ChessPosition newPos = new ChessPosition(row, col);
            if (viableDestination(board, newPos)) {
                moves.add(
                        new ChessMove(
                                start,
                                newPos,
                                null));
            }
        }
    }

    private Collection<ChessMove> rookMoves(
            ChessBoard board,
            ChessPosition position) {
        return directionalMoves(
                board,
                position,
                new int[][]{
                        {1, 0},
                        {-1, 0},
                        {0, 1},
                        {0,-1}
                });
    }

    private Collection<ChessMove> bishopMoves(
            ChessBoard board,
            ChessPosition position) {
        return directionalMoves(
                board,
                position,
                new int[][]{
                        {1, 1},
                        {-1, 1},
                        {-1, -1},
                        {1,-1}
                });
    }

    private Collection<ChessMove> queenMoves(
            ChessBoard board,
            ChessPosition position) {
        return directionalMoves(
                board,
                position,
                new int[][]{
                        {1, 0},
                        {-1, 0},
                        {0, 1},
                        {0,-1},
                        {1, 1},
                        {-1, 1},
                        {-1, -1},
                        {1,-1}
                });
    }

    private Collection<ChessMove> knightMoves(
            ChessBoard board,
            ChessPosition position) {
        Collection<ChessMove> moves =
                new ArrayList<>();
        int[][] directions ={
                {2, 1},
                {2, -1},
                {-2, 1},
                {-2, -1},
                {1, 2},
                {1, -2},
                {-1, 2},
                {-1, -2}
        };
        addSingleMoves(
                moves,
                board,
                position,
                directions);
        return moves;
    }

    private Collection<ChessMove> kingMoves(
            ChessBoard board,
            ChessPosition position) {
        Collection<ChessMove> moves =
                new ArrayList<>();
        int[][] directions ={
                {1, 0},
                {-1, 0},
                {0, 1},
                {0,-1},
                {1, 1},
                {-1, 1},
                {-1, -1},
                {1,-1}
        };
        addSingleMoves(
                moves,
                board,
                position,
                directions);
        return moves;
    }

    private Collection<ChessMove> pawnMoves (
            ChessBoard board,
            ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>(); // create collection
        int direction =
                (pieceColor == ChessGame.TeamColor.WHITE) // get direction for both colors
                        ? 1 : -1;
        int startRow =
                (pieceColor == ChessGame.TeamColor.WHITE) // get start position for both colors
                    ? 2 : 7;
        int promotionRow =
                (pieceColor == ChessGame.TeamColor.WHITE) // get promotion row for both colors
                ? 8 : 1;
        addForwardPawnMoves( // calls addForwardPawnMoves
                moves,
                board,
                position,
                direction,
                startRow,
                promotionRow);
        addCapturePawnMoves( // calls capturePawnMoves
                moves,
                board,
                position,
                direction,
                promotionRow);
        return moves; //return collection

    }

    // only checks to see if the desired position is on the board
    private boolean viabilityCheck(
            int row,
            int col)
    {
        return row >= 1
                && row <= 8
                && col >= 1
                && col <= 8;
    }

    private void addForwardPawnMoves(
            Collection<ChessMove> moves,
            ChessBoard board,
            ChessPosition start,
            int direction,
            int startRow,
            int promotionRow) {
        int row = start.getRow();
        int col = start.getColumn();
        int newRow = row + direction;
        if (!viabilityCheck(newRow, col)) { // checks to see if the new move is allowed
            return;
        }
        ChessPosition oneForward = new ChessPosition(newRow, col);
        if (board.getPiece(oneForward) != null) { // checks to see if there is anything in the way
            return;
        }
        addPawnMove( // add any moves that have made it this far
                moves,
                start,
                oneForward,
                promotionRow);
        if (row != startRow) { // if it isn't in its start row, you're done
            return;
        }
        ChessPosition twoForward = new ChessPosition(row + (direction * 2), col);
        if (board.getPiece(twoForward) == null) {
            moves.add( // if nothing is in the way and it is on the starting position, add that too
                    new ChessMove(
                            start,
                            twoForward,
                            null));
        }
    }

    private void addCapturePawnMoves(
            Collection<ChessMove> moves,
            ChessBoard board,
            ChessPosition start,
            int direction,
            int promotionRow) {
        int row = start.getRow();
        int col = start.getColumn();
        int[] captureColumns = {
                col - 1,
                col + 1
        };
        for (int captureCol : captureColumns) {
            int captureRow = row + direction;
            if (!viabilityCheck(captureRow, captureCol)) {
                continue; // for each possible capture square, if not on board, ignore
            }
            ChessPosition diagonal = new ChessPosition(captureRow, captureCol);
            ChessPiece target = board.getPiece(diagonal);
            if (target == null) {
                continue; // for each possible target square, if it is empty, ignore
            }
            if (target.getTeamColor() == pieceColor) {
                continue; // if there is something there, but it's same color, ignore
            }
            addPawnMove( // add whatever made it this far
                    moves,
                    start,
                    diagonal,
                    promotionRow);
        }
    }

    private void addPawnMove(
            Collection<ChessMove> moves,
            ChessPosition start,
            ChessPosition end,
            int promotionRow
    ) {
        if (end.getRow() == promotionRow) { // if it has made it to the promotion row, call addPromotionMoves
            addPromotionMoves(
                    moves,
                    start,
                    end);
            return;
        }
        moves.add( // make the calculated move a real move
                new ChessMove(
                        start,
                        end,
                        null));
    }

    private void addPromotionMoves(
            Collection<ChessMove> moves,
            ChessPosition start,
            ChessPosition end) { // adds all possible promotion moves
        moves.add(new ChessMove(start, end, PieceType.QUEEN));
        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
        moves.add(new ChessMove(start, end, PieceType.BISHOP));
        moves.add(new ChessMove(start, end, PieceType.ROOK));
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> kingMoves(board, myPosition); // for each piece type case, return that pieces' moves
            case QUEEN -> queenMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }
}