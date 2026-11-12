package ui;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BoardRenderer {

    public static void drawBoard(
            ChessBoard board, ChessGame.TeamColor perspective) {
        System.out.println();
        System.out.print(EscapeSequences.ERASE_SCREEN);
        if (perspective == ChessGame.TeamColor.BLACK) {
            drawBlack(board);
        }
        else {
            drawWhite(board);
        }
    }

    private static void drawWhite(ChessBoard board) {
        printColumnHeadersWhite();
        for (int row = 8; row >= 1; row--) {
            System.out.print(
                    EscapeSequences.SET_BG_COLOR_BLACK +
                            EscapeSequences.SET_TEXT_COLOR_WHITE +
                            " " + row + " ");
            for (int col = 1; col <= 8; col ++) {
                drawSquare(
                        board.getPiece(
                                new ChessPosition(row, col)),
                        row,
                        col);
            }
            System.out.print(
                    EscapeSequences.SET_BG_COLOR_BLACK +
                            EscapeSequences.SET_TEXT_COLOR_WHITE +
                            " " + row + " ");
            System.out.println();
        }
        printColumnHeadersWhite();
    }

    private static void drawBlack(ChessBoard board) {
        printColumnHeadersBlack();
        for (int row = 1; row <= 8; row++) {
            System.out.print(
                    EscapeSequences.SET_BG_COLOR_BLACK +
                            EscapeSequences.SET_TEXT_COLOR_WHITE +
                            " " + row + " ");
            for (int col = 8; col >= 1; col--) {
                drawSquare(
                        board.getPiece(
                                new ChessPosition(row, col)),
                        row,
                        col);
            }
            System.out.print(
                    EscapeSequences.SET_BG_COLOR_BLACK +
                            EscapeSequences.SET_TEXT_COLOR_WHITE +
                            " " + row + " ");
            System.out.println();
        }
        printColumnHeadersBlack();
    }


}
