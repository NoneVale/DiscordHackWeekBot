package studios.hawkeyegame.hackweekbot.games.uno;

import studios.hawkeyegame.hackweekbot.games.game.GameModel;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoCard;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoCardType;
import studios.hawkeyegame.hackweekbot.games.user.GameUserModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class UnoGameBoard {

    public static BufferedImage getGameBoard(List<UnoCard> deck, List<UnoCard> thrown) {
        BufferedImage image = null;
        BufferedImage card = null;
        BufferedImage board = null;
        try {
            image = ImageIO.read(new File("images/uno/board.png"));
            card = ImageIO.read(new File("images/uno/CARD-BACK.png"));
        } catch (Exception ignored) {}

        assert image != null;
        BufferedImage combined = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        if (image != null && card != null) {
            Graphics2D graphics = combined.createGraphics();
            graphics.drawImage(image, 0, 0, null);
            int i = 0;
            int x = (image.getWidth() / 2) - ((int)((float)card.getWidth() * 1.25)) - 40;
            int y = (image.getHeight() / 2) - ((int)((float)card.getHeight() * 1.25) / 2);
            for (UnoCard decked : deck) {
                if (i % 9 == 0 && i != 0) {
                    x--;
                    y--;
                }
                graphics.drawImage(card, x, y,  null);
                i++;
            }

            i = 0;
            x = (image.getWidth() / 2) +  40;
            y = (image.getHeight() / 2) - ((int)((float)card.getHeight() * 1.25) / 2);
            for (UnoCard discarded : thrown) {
                if (i % 9 == 0 && i != 0) {
                    x--;
                    y--;
                }

                BufferedImage face = null;
                try {
                    if (discarded.getFace().getCardType() != UnoCardType.WILD) {
                        face = ImageIO.read(new File("images/uno/" + discarded.getSuit().toString() + "-" + discarded.getFace().toString() + ".png"));
                    } else {
                        face = ImageIO.read(new File("images/uno/" + discarded.getFace().toString() + ".png"));
                    }
                } catch (Exception ignored) {}

                if (face != null) {
                    Random random = new Random();

                    graphics.drawImage(rotateImageByDegrees(face, random.nextInt(270)), x, y,  null);
                    i++;
                }
            }
            graphics.dispose();
            try {
                ImageIO.write(combined, "png", new File("images/temp/board.png"));
                board = ImageIO.read(new File("images/temp/board.png"));
            } catch (IOException ignored) {}
        }
        return board;
    }

    public static BufferedImage getGameBoard(List<UnoCard> deck, List<UnoCard> thrown, GameUserModel playerOne) {
        BufferedImage board = getGameBoard(deck, thrown);
        BufferedImage card = null;
        try {
            String fontName = "HelveticaNeueLT Std Blk Cn";
            card = ImageIO.read(new File("images/uno/CARD-BACK.png"));
        } catch (Exception ignored) {}

        try {
            BufferedImage combined = new BufferedImage(board.getWidth(), board.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics = combined.createGraphics();
            graphics.drawImage(board, 0, 0, null);
            graphics.setFont(new Font("Helvetica Neue LT Std", Font.BOLD + Font.ITALIC, 20));

            BufferedImage image = playerOne.getUser().getAvatar().asBufferedImage().get();
            if (image!= null) {
                image = userIcon(image);
                Rectangle rect = new Rectangle(board.getWidth() - 225, board.getHeight() - 225, image.getWidth() + 50, image.getHeight() * 2);

                // Player shit
                graphics.setColor(Color.GRAY);
                graphics.fill(rect);
                graphics.setStroke(new BasicStroke(10));
                graphics.setColor(Color.BLACK);
                graphics.draw(rect);
                graphics.drawImage(image, board.getWidth() - 200, board.getHeight() - 210, null);
                graphics.setColor(Color.WHITE);
                rect = new Rectangle(board.getWidth() - 225, board.getHeight() - 200, image.getWidth() + 50, image.getHeight() * 2 + 10);
                drawCenteredString(graphics, playerOne.getUser().getName(), rect, graphics.getFont());
                BufferedImage white = ImageIO.read(new File("images/uno/WHITE-CARD.png"));
                int drawX = (int)rect.getX() + (int)rect.getWidth() / 2 - (white.getWidth() /2 );
                graphics.drawImage(white, drawX - 5, board.getHeight() - 70, (int)((float) white.getWidth() * 1.10), (int)((float) white.getHeight() * 1.10),null);
                graphics.drawImage(white, drawX + 5, board.getHeight() - 75, (int)((float) white.getWidth() * 1.10), (int)((float) white.getHeight() * 1.10),null);
                System.out.println((board.getHeight() - 70) - (int)rect.getY());
                rect = new Rectangle(drawX, board.getHeight() - 73, white.getWidth(), white.getHeight());
                graphics.setColor(Color.BLACK);
                drawCenteredString(graphics, "" + playerOne.getHand().size(), rect, graphics.getFont());

                // Player's card
                int fromLeft = 255;
                int fromRight = board.getWidth() - 255 - card.getWidth();
                int between = fromRight - fromLeft;
                int betweenEach = between / (playerOne.getHand().size() + 1);
                if (playerOne.getHand().size() > 1) {
                    betweenEach = between / (playerOne.getHand().size() - 1);
                }
                for (int i = 0; i < playerOne.getHand().size(); i++) {
                    int drawCard = 255 + (i * betweenEach);
                    graphics.drawImage(card, drawCard, board.getHeight() - 210, null);
                }
            }
            graphics.dispose();

            ImageIO.write(combined, "png", new File("images/temp/board.png"));
            board = ImageIO.read(new File("images/temp/board.png"));
        } catch (Exception ignored) {}
        return board;
    }

    public static BufferedImage getGameBoard(List<UnoCard> deck, List<UnoCard> thrown, GameUserModel playerOne, GameUserModel playerTwo) {
        BufferedImage board = getGameBoard(deck, thrown, playerOne);
        BufferedImage card = null;
        try {
            String fontName = "HelveticaNeueLT Std Blk Cn";
            card = ImageIO.read(new File("images/uno/CARD-BACK.png"));
        } catch (Exception ignored) {}

        try {
            BufferedImage combined = new BufferedImage(board.getWidth(), board.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics = combined.createGraphics();
            graphics.drawImage(board, 0, 0, null);
            graphics.setFont(new Font("Helvetica Neue LT Std", Font.BOLD + Font.ITALIC, 20));

            BufferedImage image = playerTwo.getUser().getAvatar().asBufferedImage().get();
            if (image!= null) {
                image = userIcon(image);
                Rectangle rect = new Rectangle(73, 21, image.getWidth() + 50, image.getHeight() * 2);

                graphics.setColor(Color.GRAY);
                graphics.fill(rect);
                graphics.setStroke(new BasicStroke(10));
                graphics.setColor(Color.BLACK);
                graphics.draw(rect);
                graphics.drawImage(image, 98, 31, null);
                graphics.setColor(Color.WHITE);
                rect = new Rectangle(73, 46, image.getWidth() + 50, image.getHeight() * 2 + 10);
                drawCenteredString(graphics, playerTwo.getUser().getName(), rect, graphics.getFont());
                BufferedImage white = ImageIO.read(new File("images/uno/WHITE-CARD.png"));
                int drawX = (int)rect.getX() + (int)rect.getWidth() / 2 - (white.getWidth() /2 );
                graphics.drawImage(white, drawX - 5, (int)rect.getY() + 130, (int)((float) white.getWidth() * 1.10), (int)((float) white.getHeight() * 1.10),null);
                graphics.drawImage(white, drawX + 5, (int)rect.getY() + 125, (int)((float) white.getWidth() * 1.10), (int)((float) white.getHeight() * 1.10),null);
                rect = new Rectangle(drawX, (int)rect.getY() + 127, white.getWidth(), white.getHeight());
                graphics.setColor(Color.BLACK);
                drawCenteredString(graphics, "" + playerTwo.getHand().size(), rect, graphics.getFont());

                // Player's card
                int fromLeft = 255;
                int fromRight = board.getWidth() - 255 - card.getWidth();
                int between = fromRight - fromLeft;
                int betweenEach = between / (playerTwo.getHand().size() + 1);
                if (playerTwo.getHand().size() > 1) {
                    betweenEach = between / (playerTwo.getHand().size() - 1);
                }
                for (int i = 0; i < playerTwo.getHand().size(); i++) {
                    int drawCard = 255 + (i * betweenEach);
                    graphics.drawImage(rotateImageByDegrees(card, 180), drawCard, 210 - card.getHeight(), null);
                }
            }
            graphics.dispose();

            ImageIO.write(combined, "png", new File("images/temp/board.png"));
            board = ImageIO.read(new File("images/temp/board.png"));
        } catch (Exception ignored) {}
        return board;
    }

    public static BufferedImage getGameBoard(List<UnoCard> deck, List<UnoCard> thrown, GameUserModel playerOne, GameUserModel playerTwo, GameUserModel playerThree) {
        BufferedImage board = getGameBoard(deck, thrown, playerOne, playerTwo);
        BufferedImage card = null;
        try {
            String fontName = "HelveticaNeueLT Std Blk Cn";
            card = ImageIO.read(new File("images/uno/CARD-BACK.png"));
        } catch (Exception ignored) {}

        try {
            BufferedImage combined = new BufferedImage(board.getWidth(), board.getHeight(), BufferedImage.TYPE_INT_ARGB);
            BufferedImage white = ImageIO.read(new File("images/uno/WHITE-CARD.png"));

            Graphics2D graphics = combined.createGraphics();
            graphics.drawImage(board, 0, 0, null);
            graphics.setFont(new Font("Helvetica Neue LT Std", Font.BOLD + Font.ITALIC, 20));

            BufferedImage image = playerThree.getUser().getAvatar().asBufferedImage().get();
            if (image!= null) {
                image = userIcon(image);
                Rectangle rect = new Rectangle(21, board.getHeight() - 225, image.getWidth() * 2, image.getHeight() + 50);
                int drawY = (int)rect.getY() + ((int)rect.getHeight() / 2) - (white.getHeight() / 2);

                graphics.setColor(Color.GRAY);
                graphics.fill(rect);
                graphics.setStroke(new BasicStroke(10));
                graphics.setColor(Color.BLACK);
                graphics.draw(rect);
                graphics.drawImage(image, (int)rect.getX() + 25, (int)rect.getY() + 15, null);
                graphics.setColor(Color.WHITE);
                rect = new Rectangle(21, (int) rect.getY() + 25, image.getWidth() + 50, image.getHeight() * 2 + 10);
                drawCenteredString(graphics, playerThree.getUser().getName(), rect, graphics.getFont());
                int drawX = ((int)rect.getX() + (int)rect.getWidth()) - (white.getWidth() / 2) + 10;
                graphics.drawImage(white, drawX - 5, drawY + 5, (int)((float) white.getWidth() * 1.10), (int)((float) white.getHeight() * 1.10),null);
                graphics.drawImage(white, drawX + 5, drawY, (int)((float) white.getWidth() * 1.10), (int)((float) white.getHeight() * 1.10),null);
                rect = new Rectangle(drawX, drawY + 2, white.getWidth(), white.getHeight());
                graphics.setColor(Color.BLACK);
                drawCenteredString(graphics, "" + playerThree.getHand().size(), rect, graphics.getFont());

                int fromTop = 255;
                int fromBottom = board.getHeight() - 255 - card.getWidth();
                int between = fromBottom - fromTop;
                int betweenEach = between / (playerThree.getHand().size() + 1);
                if (playerThree.getHand().size() > 1) {
                    betweenEach = between / (playerThree.getHand().size() - 1);
                }
                for (int i = 0; i < playerThree.getHand().size(); i++) {
                    int drawCard = 255 + (i * betweenEach);
                    graphics.drawImage(rotateImageByDegrees(card, 90), 210 - card.getHeight(), drawCard, null);
                }
            }
            graphics.dispose();

            ImageIO.write(combined, "png", new File("images/temp/board.png"));
            board = ImageIO.read(new File("images/temp/board.png"));
        } catch (Exception ignored) {}
        return board;
    }

    public static BufferedImage getGameBoard(List<UnoCard> deck, List<UnoCard> thrown, GameUserModel playerOne, GameUserModel playerTwo, GameUserModel playerThree, GameUserModel playerFour) {
        BufferedImage board = getGameBoard(deck, thrown, playerOne, playerTwo, playerThree);
        BufferedImage card = null;
        try {
            String fontName = "HelveticaNeueLT Std Blk Cn";
            card = ImageIO.read(new File("images/uno/CARD-BACK.png"));
        } catch (Exception ignored) {}

        try {
            BufferedImage combined = new BufferedImage(board.getWidth(), board.getHeight(), BufferedImage.TYPE_INT_ARGB);
            BufferedImage white = ImageIO.read(new File("images/uno/WHITE-CARD.png"));

            Graphics2D graphics = combined.createGraphics();
            graphics.drawImage(board, 0, 0, null);
            graphics.setFont(new Font("Helvetica Neue LT Std", Font.BOLD + Font.ITALIC, 20));

            BufferedImage image = playerFour.getUser().getAvatar().asBufferedImage().get();
            if (image!= null) {
                image = userIcon(image);
                Rectangle rect = new Rectangle(board.getWidth() - 225, 73, image.getWidth() * 2, image.getHeight() + 50);
                int drawY = (int)rect.getY() + ((int)rect.getHeight() / 2) - (white.getHeight() / 2);

                graphics.setColor(Color.GRAY);
                graphics.fill(rect);
                graphics.setStroke(new BasicStroke(10));
                graphics.setColor(Color.BLACK);
                graphics.draw(rect);
                graphics.drawImage(image, (int)rect.getX() + 25, (int)rect.getY() + 15, null);
                graphics.setColor(Color.WHITE);
                rect = new Rectangle(board.getWidth() - 225, (int) rect.getY() + 25, image.getWidth() + 50, image.getHeight() * 2 + 10);
                drawCenteredString(graphics, playerFour.getUser().getName(), rect, graphics.getFont());
                int drawX = ((int)rect.getX() + (int)rect.getWidth()) - (white.getWidth() / 2) + 10;
                graphics.drawImage(white, drawX - 5, drawY + 5, (int)((float) white.getWidth() * 1.10), (int)((float) white.getHeight() * 1.10),null);
                graphics.drawImage(white, drawX + 5, drawY, (int)((float) white.getWidth() * 1.10), (int)((float) white.getHeight() * 1.10),null);
                rect = new Rectangle(drawX, drawY + 2, white.getWidth(), white.getHeight());
                graphics.setColor(Color.BLACK);
                drawCenteredString(graphics, "" + playerFour.getHand().size(), rect, graphics.getFont());

                // Player's card
                int fromTop = 255;
                int fromBottom = board.getHeight() - 255 - card.getWidth();
                int between = fromBottom - fromTop;
                int betweenEach = between / (playerFour.getHand().size() + 1);
                if (playerFour.getHand().size() > 1) {
                    betweenEach = between / (playerFour.getHand().size() - 1);
                }
                for (int i = 0; i < playerFour.getHand().size(); i++) {
                    int drawCard = 255 + (i * betweenEach);
                    graphics.drawImage(rotateImageByDegrees(card, 270), board.getWidth() - 210, drawCard, null);
                }
            }
            graphics.dispose();

            ImageIO.write(combined, "png", new File("images/temp/board.png"));
            board = ImageIO.read(new File("images/temp/board.png"));
        } catch (Exception ignored) {}
        return board;
    }

    public static BufferedImage getHand(GameUserModel player) {
        BufferedImage board = null;
        BufferedImage card = null;
        try {
            board = ImageIO.read(new File("images/uno/board.png"));
            card = ImageIO.read(new File("images/uno/CARD-BACK.png"));
        } catch (Exception ignored) {}

        try {
            BufferedImage combined = new BufferedImage(board.getWidth(), (int) ((float) card.getHeight() * 1.5), BufferedImage.TYPE_INT_ARGB);
            BufferedImage white = ImageIO.read(new File("images/uno/WHITE-CARD.png"));

            Graphics2D graphics = combined.createGraphics();
            graphics.drawImage(board, 0, 0, null);
            graphics.setFont(new Font("Helvetica Neue LT Std", Font.BOLD + Font.ITALIC, 20));


            int fromLeft = 50;
            int fromRight = board.getWidth() - 50 - card.getWidth();
            int between = fromRight - fromLeft;
            int betweenEach = between / (player.getHand().size() + 1);
            if (player.getHand().size() > 1) {
                betweenEach = between / (player.getHand().size() - 1);
            }
            for (int i = 0; i < player.getHand().size(); i++) {
                int drawCard = 50 + (i * betweenEach);

                BufferedImage face = null;
                UnoCard unoCard = (UnoCard) player.getHand().get(i);
                try {
                    if (unoCard.getFace().getCardType() != UnoCardType.WILD) {
                        face = ImageIO.read(new File("images/uno/" + unoCard.getSuit().toString() + "-" + unoCard.getFace().toString() + ".png"));
                    } else {
                        face = ImageIO.read(new File("images/uno/" + unoCard.getFace().toString() + ".png"));
                    }
                } catch (Exception ignored) {}

                if (face != null) {
                    graphics.drawImage(face, drawCard, 210 - card.getHeight(), null);
                }
            }
            graphics.dispose();

            ImageIO.write(combined, "png", new File("images/temp/board.png"));
            board = ImageIO.read(new File("images/temp/board.png"));
        } catch (Exception ignored) {}
        return board;
    }

    private static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0,null);
        g2d.dispose();

        return rotated;
    }

    private static BufferedImage userIcon(BufferedImage img) {
        int width = (int) ((float)img.getWidth() * .8);

        BufferedImage newImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImage.createGraphics();

        g2d.setClip(new Ellipse2D.Float(0, 0, width, width));
        g2d.drawImage(img, 0, 0, width, width, null);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawOval(0, 0, width, width);
        g2d.dispose();

        return newImage;

    }

    private static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }

    public static BufferedImage getBoard(GameModel model) {
        //model.refreshUsers();
        for (GameUserModel userModel : model.getGameUsers()) {
            System.out.println(userModel.getKey());
        }
        switch (model.getGameUsers().size()) {
            case 2:
                return getGameBoard(model.getUnoDeck().getDeck(), model.getUnoDeck().getDiscarded(), model.getGameUsers().get(0), model.getGameUsers().get(1));
            case 3:
                return getGameBoard(model.getUnoDeck().getDeck(), model.getUnoDeck().getDiscarded(), model.getGameUsers().get(0), model.getGameUsers().get(2), model.getGameUsers().get(1));
            case 4:
                return getGameBoard(model.getUnoDeck().getDeck(), model.getUnoDeck().getDiscarded(), model.getGameUsers().get(0), model.getGameUsers().get(2), model.getGameUsers().get(1), model.getGameUsers().get(3));
        }
        return null;
    }
}
