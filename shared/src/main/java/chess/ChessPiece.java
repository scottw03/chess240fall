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
        return null;
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
            case KING -> null;
            case QUEEN -> queenMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case PAWN -> null;
        };
    }
}
