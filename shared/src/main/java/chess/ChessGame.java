package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private boolean gameOver = false;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return gameOver == chessGame.gameOver && Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, gameOver);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    //function that copies a board by replicating every square
    private ChessBoard copyBoard(ChessBoard original) {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = original.getPiece(pos);
                if (piece != null) {
                    ChessPiece copiedPiece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                    newBoard.addPiece(pos, copiedPiece);
                }
            }
        }
        return newBoard;
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //get the piece at the position
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        //create a collection of every possible move your piece can take
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        //create a separate collection of moves to keep
        Collection<ChessMove> validMoves = new ArrayList<>();
        //for each move in the potential moves, check to see if that
        //move will place you in check and if it is a viable destination
        for (ChessMove move : possibleMoves) {
            //make a temporary duplicate board we can do whatever we want with
            ChessBoard tempBoard = copyBoard(board);
            //get the piece you want to move
            ChessPiece movingPiece = tempBoard.getPiece(move.getStartPosition());
            //move the piece to the position you wish to try
            tempBoard.addPiece(move.getEndPosition(), movingPiece);
            //delete the original
            tempBoard.addPiece(move.getStartPosition(), null);
            //make this the state of the game
            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(tempBoard);
            //if the temporary game does not put you in check, add it to the collection
            if (!tempGame.isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }
        //if it is, add it to our keepers list
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    //helper function that finds the king piece
    private ChessPosition findKing(TeamColor color) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition kingPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(kingPos);
                if (piece != null && piece.getTeamColor()
                        == color && piece.getPieceType()
                        == ChessPiece.PieceType.KING) {
                    return kingPos;
                }
            }
        }
        return null;
    }

    //helper function that finds any pieces attacking the king
    private boolean attacksKing(
            ChessPiece piece,
            ChessPosition position,
            ChessPosition kingPosition) {
        Collection<ChessMove> moves =
                piece.pieceMoves(board, position);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find the position of the king
        ChessPosition kingPosition = findKing(teamColor);
        //establish the opposite color
        TeamColor enemy =
                (teamColor == TeamColor.WHITE)
                        ? TeamColor.BLACK
                        : TeamColor.WHITE;
        //for each piece on the board, it if is the enemy color
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position =
                        new ChessPosition(row, col);
                ChessPiece piece =
                        board.getPiece(position);
                if (piece == null ||
                        piece.getTeamColor() != enemy) {
                    continue;
                }
                //if the piece is in a position to attack the king, return true
                if (attacksKing(
                        piece,
                        position,
                        kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    //helper function that checks to see if there are any possible moves
    private boolean hasLegalMove(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> moves = validMoves(pos);
                if (moves != null && !moves.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return !hasLegalMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return !hasLegalMove(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
