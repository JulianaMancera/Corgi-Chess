package Main;

import piece.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{

    public static final int WIDTH= 1100;
    public static final int HEIGHT  = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //Pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList <Piece> promoPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;


    //Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //Booleans
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;

    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        //testIllegal();
        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces(){
        //white team
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        //Black team
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }

    public void testPromotion(){

        pieces.add(new Pawn(WHITE, 0, 3));
        pieces.add(new Pawn(BLACK, 5, 4));
    }

    public void testIllegal(){

        pieces.add(new Queen(WHITE, 3, 4));
        pieces.add(new Queen(WHITE, 4, 4));
        pieces.add(new Queen(WHITE, 6, 4));

        pieces.add(new Pawn(WHITE, 0, 3));
        pieces.add(new King(WHITE, 4, 4));
        pieces.add(new King(BLACK, 4, 0));
        pieces.add(new Pawn(BLACK, 5, 4));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if(delta >- 1){
                update();
                repaint();
                delta--;
            }

        }
    }

    private void update(){
        if (promotion) {
            promoting();
        } else if (gameover == false && stalemate == false){
            //Mouse button pressed
            if(mouse.pressed) {
                if (activeP == null) {
                    //if active is null, check if you can pick up a piece
                    for (Piece piece : simPieces) {
                        //if the mouse is pn ally, pict it up as activeP
                        if (piece.color == currentColor &&
                                piece.col == mouse.x / Board.SQUARE_SIZE &&
                                piece.row == mouse.y / Board.SQUARE_SIZE) {
                            activeP = piece;
                        }
                    }
                }
                else{
                    //of the player picks up a piece, simulate it
                    simulate();
                }
            }
            if(mouse.pressed == false){
                if(activeP != null){
                    if(validSquare){
                        //Move confirmed

                        //update the piece list in case a piece has been captured
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if(castlingP != null){
                            castlingP.updatePosition();
                        }

                        if (isKingInCheck() && isCheckmate()) {
                            gameover = true;

                        } else if(isStalemate() && isKingInCheck() == false){
                            stalemate = true;
                        }
                        else { //game is still going
                            if(canPromote()){
                                promotion = true;
                            } else {
                                changePlayer();
                            }
                        }
                    }
                    else {
                        //the move is not valid so reset
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }
        }
    }

    private void simulate(){
        canMove = false;
        validSquare = false;

        //reset the piece list in every loop
        copyPieces(pieces, simPieces);

        //Reset the castling piece's position
        if(castlingP != null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        //checks if piece is being held
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getCol(activeP.y);

        //check if piece is hovering reachable square
        if(activeP.canMove(activeP.col, activeP.row)){
            canMove = true;

            if(activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();

            if(isIllegal(activeP) == false && opponentCanCaptureKing() == false){
                validSquare = true;
            }
        }
    }

    private boolean isIllegal(Piece king){
        if (king.type == Type.KING){
            for(Piece piece : simPieces){
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean opponentCanCaptureKing(){

        Piece king = getKing(false);

        for (Piece piece : simPieces){
            if(piece.color != king.color && piece.canMove(king.col, king.row)){
                return true;
            }
        }

        return false;
    }
    private boolean isKingInCheck(){
        Piece king = getKing(true);
        if(activeP.canMove(king.col, king.row)){
            checkingP = activeP;
            return true;
        } else{
            checkingP = null;
        }
        return false;
    }
    private Piece getKing(boolean opponent){
        Piece king = null;
        for(Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }
        return king;
    }
    private boolean isCheckmate(){

        Piece king = getKing(true);

        if(kingCanMove(king)){
            return false;
        } else {
            //check if you can block with allied piece.

            //check the position of the checking piece and the king in check
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if(colDiff == 0){
                //vertically

                if(checkingP.row < king.row){
                    //above
                    for(int row = checkingP.row; row < king.row; row++){
                        for(Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){
                    //Below
                    for(int row = checkingP.row; row > king.row; row--){
                        for(Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
            }
            else if(rowDiff == 0){
                //horizontally
                if(checkingP.col < king.col){
                    //left
                    for(int col = checkingP.col; col < king.col; col++){
                        for(Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col){
                    //right
                    for(int col = checkingP.col; col > king.col; col--){
                        for(Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
            }
            else if(colDiff == rowDiff){
                //diagonally
                if(checkingP.row < king.row){
                    //above
                    if(checkingP.col < king.col){
                        //upper left
                        for(int col = checkingP.col, row  = checkingP.row; col < king.col; col++, row++){
                            for (Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        //upper right
                        for(int col = checkingP.col, row  = checkingP.row; col > king.col; col--, row++){
                            for (Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){
                    //below the king
                    if(checkingP.col < king.col){
                        //lower left
                        for(int col = checkingP.col, row  = checkingP.row; col < king.col; col++, row--){
                            for (Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){
                        //lower right
                        for(int col = checkingP.col, row  = checkingP.row; col > king.col; col--, row--){
                            for (Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean kingCanMove(Piece king){
        //simulate
        if(isValidMove(king, -1, -1)) {
            return true;
        }
        if(isValidMove(king, 0, -1)) {
            return true;
        }
        if(isValidMove(king, 1, -1)) {
            return true;
        }
        if(isValidMove(king, -1, -0)) {
            return true;
        }
        if(isValidMove(king, 1, 0)) {
            return true;
        }
        if(isValidMove(king, -1, 1)) {
            return true;
        }
        if(isValidMove(king, 0, 1)) {
            return true;
        }
        if(isValidMove(king, 1, 1)) {
            return true;
        }
        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus){
        boolean isValidMove = false;

        //update kings posiition momentarily
        king.col += colPlus;
        king.row += rowPlus;
        if (king.canMove(king.col, king.row)){
            if (king.hittingP != null){
                simPieces.remove(king.hittingP.getIndex());
            }
            if(isIllegal(king) == false){
                isValidMove = true;
            }
        }

        king.resetPosition();
        copyPieces(pieces, simPieces);
        return isValidMove;
    }

    private boolean isStalemate(){
        int count = 0;
        for(Piece piece : simPieces){
            if(piece.color != currentColor){
                count++;
            }
        }
        //if only one piece(king) is left
        if(count == 1){
            if(kingCanMove(getKing(true)) == false){
                return true;
            }

        }
        return false;
    }
    private void  checkCastling(){
        if(castlingP != null){
            if(castlingP.col == 0){
                castlingP.col += 3;
            }
            else if( castlingP.col == 7){
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    private void changePlayer(){
        if (currentColor == WHITE){
            currentColor = BLACK;
            //reset two stepped stats
            for(Piece piece : pieces){
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        }
        else {
            currentColor = WHITE;
            //reset two stepped stats
            for(Piece piece : pieces){
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }

    private boolean canPromote(){
        if (activeP.type == Type.PAWN){
            if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Knight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }

    private void promoting(){
        if(mouse.pressed){
            for(Piece piece : promoPieces){
                if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                    switch(piece.type){
                        case ROOK: simPieces.add(new Rook (currentColor, activeP.col, activeP.row)); break;
                        case KNIGHT: simPieces.add(new Knight (currentColor, activeP.col, activeP.row)); break;
                        case BISHOP: simPieces.add(new Bishop (currentColor, activeP.col, activeP.row)); break;
                        case QUEEN: simPieces.add(new Queen (currentColor, activeP.col, activeP.row)); break;
                        default: break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        //Board
        board.draw(g2);

        //Pieces
        for (Piece p : simPieces){
            p.draw(g2);
        }
        if (activeP != null){
            if(canMove){
                if(isIllegal(activeP) || opponentCanCaptureKing()){
                    g2.setColor(Color.gray);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                } else {
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                }
            }

            //Draw Active piece
            activeP.draw(g2);
        }
        //status message
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if (promotion) {
            g2.drawString("promote to:", 840, 150);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        }
        else{
            if(currentColor == WHITE){
                g2.drawString("White's turn", 840, 550);
                if(checkingP != null && checkingP.color == BLACK){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in Check!", 840, 700);
                }
            } else {
                g2.drawString("Black's turn", 840, 250);
                if(checkingP != null && checkingP.color == WHITE){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in Check!", 840, 700);
                }

            }
        }
        if(gameover) {
            BufferedImage image = null;
            try {
                if (currentColor == WHITE) {
                    image = ImageIO.read(new File("D:\\Projects\\Chess\\res\\piece\\w-win.png"));
                } else {
                    image = ImageIO.read(new File("D:\\Projects\\Chess\\res\\piece\\b-win.png"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            int x = (1100 - image.getWidth()) / 2;
            int y = (800 - image.getHeight()) / 2;

            g2.drawImage(image, x, y, null);
        }
        if(stalemate){
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File("D:\\Projects\\Chess\\res\\piece\\stalemate.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            int x = (1100 - image.getWidth()) / 2;
            int y = (800 - image.getHeight()) / 2;

            g2.drawImage(image, x, y, null);
        }
    }
}