package magic.ui.card;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import magic.data.CachedImagesProvider;
import magic.data.GeneralConfig;
import magic.data.IconImages;
import magic.model.MagicAbility;
import magic.model.MagicCard;
import magic.model.MagicCardDefinition;
import magic.model.MagicObject;
import magic.model.MagicPermanent;
import magic.ui.GameController;
import magic.utility.GraphicsUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

@SuppressWarnings("serial")
public class AnnotatedCardPanel extends JPanel {

    private static final Color BCOLOR = new Color(0, 0, 0, 0);
    private static final Dimension MAX_CARD_SIZE = new Dimension(480, 680);
    private static final Font PT_ADJ_FONT = new Font("Serif", Font.BOLD, 28);
    private static final Font PT_ORIG_FONT = new Font("Dialog", Font.PLAIN, 14);
    private static final Color ICON_BAR_COLOR = new Color(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), 240);
    private static final Color GRADIENT_FROM_COLOR = new Color(236, 224, 255, 254);
    private static final Color GRADIENT_TO_COLOR = new Color(236, 224, 255, 240);

    private final GeneralConfig CONFIG = GeneralConfig.getInstance();

    private MagicObject magicObject = null;
    private Timeline fadeInTimeline;
    private float opacity = 1.0f;
    private final GameController controller;
    private BufferedImage cardImage;
    private String modifiedPT;
    private String basePT;
    private Dimension imageOnlyPopupSize;
    private Dimension popupSize;
    private List<CardIcon> cardIcons = new ArrayList<>();
    private final List<Shape> iconShapes = new ArrayList<>();
    private final boolean isFadeInActive = false;
    private Timer timer;
    private BufferedImage popupImage;
    private final MagicInfoWindow infoWindow = new MagicInfoWindow();
    private final Rectangle containerRect;
    
    public AnnotatedCardPanel(final Rectangle containerRect, final GameController controller) {

        this.containerRect = containerRect;
        this.controller = controller;

        setOpaque(false);

        setDelayedVisibilityTimer();

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                if (event.getWheelRotation() > 0) { // rotate mousewheel back or towards you.
                    setVisible(false);
                }
            }
        });

        setMouseMovedListener();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                infoWindow.setVisible(false);
            }
        });
    }

    private void setDelayedVisibilityTimer() {
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showPopup();
            }
        });
        timer.setRepeats(false);
    }

    public void showDelayed(final int delay) {
        timer.setInitialDelay(delay);
        timer.restart();

    }

    private void showPopup() {
        if (isFadeInActive) {
            if (opacity == 0f) {
                setVisible(true);
                fadeInTimeline = new Timeline();
                fadeInTimeline.setDuration(300);
                fadeInTimeline.setEase(new Spline(0.8f));
                fadeInTimeline.addPropertyToInterpolate(
                    Timeline.property("opacity")
                    .on(this)
                    .from(0.0f)
                    .to(1.0f));
                fadeInTimeline.play();
            } else {
                opacity = 1.0f;
                setVisible(true);
            }
        } else {
            opacity = 1.0f;
            setVisible(true);
        }
    }

    public void hideDelayed() {
        timer.stop();
        setVisible(false);
        magicObject = null;
        opacity = 0f;
    }

    public void setCard(final MagicCardDefinition cardDef, final Dimension containerSize) {
        // <--- order important
        cardIcons = getCardIcons(cardDef);
        setPanelSize(containerSize);
        // --->
        this.magicObject = null;
        this.cardImage = getCardImage(cardDef); 
        this.modifiedPT = "";
        this.basePT = "";
        setPopupImage();
    }

    /**
     * Both MagicPermanent and MagicCard are instances of MagicObject.
     * MagicObject returns the correct abilities of a card including any
     * additional abilities added during game-play (via enchantments, etc).
     */
    public void setCard(final MagicObject magicObject, final Dimension containerSize) {
        // <--- order important
        cardIcons = getCardIcons(magicObject);
        setPanelSize(containerSize);
        // --->
        this.magicObject = magicObject;
        this.cardImage = getCardImage(magicObject);
        this.modifiedPT = getModifiedPT(magicObject);
        this.basePT = getBasePT(magicObject);
        setPopupImage();
    }

    private void setPopupImage() {
        // create a blank canvas of the appropriate size.
        popupImage = GraphicsUtilities.getCompatibleBufferedImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
        final Graphics g = popupImage.getGraphics();
        final Graphics2D g2d = (Graphics2D)g;
        // draw modified PT on original image so it is scaled properly.
        drawPowerToughnessOverlay(cardImage);
        // scale card image if required.
        final BufferedImage scaledImage = GraphicsUtilities.scale(cardImage, imageOnlyPopupSize.width, imageOnlyPopupSize.height);
        //
        // draw card image onto popup canvas, right-aligned.
        g.drawImage(scaledImage, popupSize.width - imageOnlyPopupSize.width, 0, this);
        //
        drawIcons(g2d);
    }

    private void drawPowerToughnessOverlay(final BufferedImage cardImage) {
        if (!modifiedPT.isEmpty()) {
            final int maskX = cardImage.getWidth() - (int)(cardImage.getWidth() * 0.22d);
            final int maskY = cardImage.getHeight() - (int)(cardImage.getHeight() * 0.084d);
            final int maskWidth = (int)(cardImage.getWidth() * 0.15d);
            final int maskHeight = (int)(cardImage.getHeight() * 0.038d);
            final Graphics g = cardImage.getGraphics();
            final Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(getPTOverlayBackgroundColor(maskX, maskY));
            g.fillRect(maskX, maskY, maskWidth, maskHeight);
            g.setColor(Color.BLACK);
            g.setFont(PT_ADJ_FONT);
            final FontMetrics metrics = g.getFontMetrics();
            final int ptWidth = metrics.stringWidth(modifiedPT);
            g.drawString(modifiedPT, maskX + 12, maskY + 24);
            //
            g.setFont(PT_ORIG_FONT);
            g.drawString(basePT, maskX + 14 + ptWidth, maskY + 16);
        }
    }

    private Color getPTOverlayBackgroundColor(final int x, final int y) {
        final int rgb = cardImage.getRGB(x, y);
        final int r = (rgb >> 16) & 0xFF;
        final int g = (rgb >> 8) & 0xFF;
        final int b = (rgb & 0xFF);
        return new Color(r, g, b);
    }

    private BufferedImage getCardImage(final MagicCardDefinition cardDef) {
        return CachedImagesProvider.getInstance().getImage(cardDef, 0, true);
    }
    private BufferedImage getCardImage(final MagicObject magicObject) {
        if (magicObject instanceof MagicCard) {
            final MagicCard card = (MagicCard)magicObject;
            return getCardImage(card.getCardDefinition());
        } else {
            return getCardImage(magicObject.getCardDefinition());
        }
    }

    private String getModifiedPT(final MagicObject magicObject) {
        if (magicObject instanceof MagicPermanent) {
            final MagicPermanent permanent = (MagicPermanent)magicObject;
            final String permanentPT = permanent.getPower() + "/" + permanent.getToughness();
            final String cardDefPT = permanent.getCardDefinition().getCardPower() + "/" + permanent.getCardDefinition().getCardToughness();
            if (!permanentPT.equals(cardDefPT)) {
                return permanent.getPower() + "/" + permanent.getToughness();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private String getBasePT(final MagicObject magicObject) {
        if (magicObject instanceof MagicPermanent) {
            final MagicPermanent permanent = (MagicPermanent)magicObject;
            return permanent.getCardDefinition().getCardPower() + "/" + permanent.getCardDefinition().getCardToughness();
        } else if (magicObject instanceof MagicCard) {
            final MagicCard card = (MagicCard)magicObject;
            return card.getCardDefinition().getCardPower() + "/" + card.getCardDefinition().getCardToughness();
        } else {
            return "";
        }
    }

    public MagicObject getMagicObject() {
        return magicObject;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (opacity < 1.0f && isFadeInActive) {
            final Graphics2D g2d = (Graphics2D)g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.setColor(BCOLOR);
            Rectangle r = g2d.getClipBounds();
            g2d.fillRect(r.x, r.y, r.width, r.height);
        }
        if (popupImage != null) {
            g.drawImage(popupImage, 0, 0, this);
        }
        super.paintComponent(g);
    }

    public void setOpacity(final float opacity) {
        this.opacity = opacity;
        repaint();
    }
    public float getOpacity() {
        return opacity;
    }

    private CardIcon getAbilityIcon(final MagicAbility ability) {
        final CardIcon icon = new CardIcon(IconImages.BACKDROP_ICON);
        icon.setName(ability.getAbilityIconName());
        icon.setDescription(ability.getAbilityIconTooltip());
        icon.setIcon(ability.getAbilityIcon());
        return icon;
    }

    private List<CardIcon> getCardIcons(final MagicObject magicObject) {
        final List<CardIcon> icons = new ArrayList<>();
        for (MagicAbility ability : MagicAbility.getIconAbilities()) {
            if (magicObject.hasAbility(ability)) {
                icons.add(getAbilityIcon(ability));
            }
        }
        return icons;
    }
    
    private List<CardIcon> getCardIcons(final MagicCardDefinition cardDef) {
        final List<CardIcon> icons = new ArrayList<>();
        for (MagicAbility ability : MagicAbility.getIconAbilities()) {
            if (cardDef.hasAbility(ability)) {
                icons.add(getAbilityIcon(ability));
            }
        }
        return icons;
    }

    @Override
    public void setVisible(final boolean isVisible) {
        super.setVisible(isVisible);
        if (controller != null) {
            controller.setGamePaused(isVisible);
        }
    }

    private void setPanelSize(final Dimension containerSize) {
        // keep scaled card in correct proportion.
        final double cardAspectRatio = (double)MAX_CARD_SIZE.height / MAX_CARD_SIZE.width;
        // Height
        final int maxHeight = CONFIG.isCardPopupScaledToScreen() ? containerSize.height : MAX_CARD_SIZE.height;
        final int scaledHeight = (int)(maxHeight * CONFIG.getCardPopupScale());
        final int actualHeight = Math.min(Math.min(scaledHeight, containerSize.height), MAX_CARD_SIZE.height);
        // Width
        final int actualWidth = (int)(actualHeight / cardAspectRatio);
        final Dimension actualCardImageSize = new Dimension(actualWidth, actualHeight);
        imageOnlyPopupSize = actualCardImageSize;
        final Dimension annotatedPopupSize = new Dimension(actualCardImageSize.width + 26, actualCardImageSize.height);
        popupSize = cardIcons.isEmpty() ? imageOnlyPopupSize : annotatedPopupSize;
        //
        setPreferredSize(popupSize);
        setMaximumSize(popupSize);
        setMinimumSize(popupSize);
        setSize(popupSize);
    }

    private void drawIcons(final Graphics2D g2d) {
        if (!cardIcons.isEmpty()) {
            final int BORDER_WIDTH = 1;
            final BasicStroke BORDER_STROKE = new BasicStroke(BORDER_WIDTH);
            // draw icon bar
            final int ICONS_PANEL_WIDTH = popupSize.width - imageOnlyPopupSize.width;
            final Rectangle barRect = new Rectangle(0, 0, ICONS_PANEL_WIDTH, popupSize.height);
            g2d.setPaint(ICON_BAR_COLOR);
            g2d.fill(barRect);
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(BORDER_STROKE);
            final Rectangle borderRect = new Rectangle(0, 0, ICONS_PANEL_WIDTH, popupSize.height - BORDER_WIDTH);
            g2d.draw(borderRect);
            // draw icons
            int y = 10;
            final int ICON_WIDTH = 36;
            iconShapes.clear();
            for (CardIcon cardIcon : cardIcons) {
                // icon bounds should be relative to CardPopupPanel.
                final Rectangle2D iconShapeRect = new Rectangle2D.Double(0d, (double)y, (double)ICON_WIDTH, 32d);
                iconShapes.add(iconShapeRect);
                //
                final Rectangle rect = new Rectangle(0, y, ICON_WIDTH, 32);
                final GradientPaint gradientPaint
                        = new GradientPaint(0, 0, GRADIENT_FROM_COLOR, ICON_WIDTH, 0, GRADIENT_TO_COLOR);
                g2d.setPaint(gradientPaint);
                g2d.fill(rect);
                g2d.setPaint(Color.BLACK);
                g2d.draw(rect);
                //
                final ImageIcon icon = cardIcon.getIcon();
                final int iconOffsetX = (ICON_WIDTH / 2) - (icon.getIconWidth() / 2);
                final int iconOffsetY = 16 - (icon.getIconHeight() / 2);
                icon.paintIcon(this, g2d, iconOffsetX, y + iconOffsetY);
                y += 36;
            }
        }
    }

    private void setMouseMovedListener() {
        addMouseMotionListener(new MouseMotionAdapter() {
            private CardIcon currentIcon;
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!cardIcons.isEmpty() && !iconShapes.isEmpty()) {
                    final Shape lastShape = iconShapes.get(iconShapes.size() - 1);
                    final Dimension lastShapeSize = new Dimension(lastShape.getBounds().width, lastShape.getBounds().y + lastShape.getBounds().height);
                    final Rectangle rect = new Rectangle(0, 0, lastShapeSize.width, lastShapeSize.height);
                    if (rect.contains(e.getPoint())) {
                        for (Shape iconShape : iconShapes) {
                            if (iconShape.contains(e.getPoint())) {
                                final CardIcon cardIcon = cardIcons.get(iconShapes.indexOf(iconShape));
                                if (currentIcon != cardIcon) {
                                    currentIcon = cardIcon;
                                    showInfoTip(cardIcon, new Point(e.getXOnScreen(), e.getYOnScreen()));
                                }
                                break;
                            }
                        }
                    } else if (infoWindow.isVisible()) {
                        boolean hideInfo = true;
                        currentIcon = null;
                        if (hideInfo) {
                            infoWindow.setVisible(false);
                        }
                    }
                }
            }
        });
    }

    private void showInfoTip(final CardIcon cardIcon, final Point position) {
        //
        infoWindow.setTitle(cardIcon.getName());
        infoWindow.setDescription("<html>" + cardIcon.getDescription().replaceAll("\r\n|\r|\n", "<br>") + "</html>");
        infoWindow.pack();
        //
        final int infoWidth = infoWindow.getWidth();
        int mx = position.x + 10;
        final int sx = containerRect.x;
        final int sw = containerRect.width - 10;
        if (mx + infoWidth > sx + sw) {
            mx = mx - ((mx + infoWidth) - (sx + sw));
        }
        final Point p = new Point(mx, position.y + 10);
        infoWindow.setLocation(p);
        infoWindow.setAlwaysOnTop(true);
        infoWindow.setVisible(true);
    }

}