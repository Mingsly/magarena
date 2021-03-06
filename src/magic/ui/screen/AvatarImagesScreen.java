package magic.ui.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import magic.data.IconImages;
import magic.data.URLUtils;
import magic.ui.avatar.AvatarImageSet;
import magic.ui.avatar.WrapLayout;
import magic.ui.screen.interfaces.IActionBar;
import magic.ui.screen.interfaces.IAvatarImageConsumer;
import magic.ui.screen.interfaces.IStatusBar;
import magic.ui.screen.interfaces.IThemeStyle;
import magic.ui.screen.widget.ActionBarButton;
import magic.ui.screen.widget.MenuButton;
import magic.ui.theme.PlayerAvatar;
import magic.ui.theme.Theme;
import magic.ui.theme.ThemeFactory;
import magic.utility.GraphicsUtilities;
import magic.ui.widget.FontsAndBorders;
import magic.ui.widget.TexturedPanel;
import magic.utility.MagicFileSystem;
import magic.utility.MagicFileSystem.DataPath;
import magic.utility.MagicStyle;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class AvatarImagesScreen
    extends AbstractScreen
    implements IStatusBar, IActionBar {

    private JPanel viewer;
    private MenuButton rightActionButton = null;
    private JLabel selectedImageLabel = null;
    private final IAvatarImageConsumer consumer;
    private final Map<JLabel, Path> imagePathMap = new HashMap<>();

    public AvatarImagesScreen(final IAvatarImageConsumer consumer) {
        this.consumer = consumer;
        setContent(getScreenContent());
    }
    public AvatarImagesScreen() {
        this(null);
    }

    private JPanel getScreenContent() {
        // Layout content.
        final JPanel content = new JPanel(new MigLayout("insets 0, gap 0"));
        content.setOpaque(false);
        content.add(new AvatarImageSetsPanel(), "w 240!, h 100%");
        content.add(getAvatarImageSetViewer(), "w 100%, h 100%");
        return content;
    }

    private JScrollPane getAvatarImageSetViewer() {

        viewer = new TexturedPanel();
        viewer.setLayout(new WrapLayout());
        viewer.setBorder(FontsAndBorders.BLACK_BORDER);
        viewer.setBackground(FontsAndBorders.MAGSCREEN_FADE_COLOR);

        final JScrollPane scroller = new JScrollPane(viewer);
        scroller.getViewport().setOpaque(false);
        scroller.setBorder(BorderFactory.createEmptyBorder());
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.getVerticalScrollBar().setUnitIncrement(20);
        return scroller;
    }

    private void displayImageSetIcons(final AvatarImageSet imageSet) {
        viewer.removeAll();
        imagePathMap.clear();
        for (Path imagePath : imageSet.getImagePaths()) {
            final String filePath = imagePath.toAbsolutePath().toString();
            try (final InputStream ins = new FileInputStream(new File(filePath))) {
                final BufferedImage image = magic.data.FileIO.toImg(ins, IconImages.MISSING);
                final ImageIcon icon = new ImageIcon(GraphicsUtilities.scale(image, PlayerAvatar.LARGE_SIZE, PlayerAvatar.LARGE_SIZE));
                final JLabel iconLabel = new JLabel(icon);
                imagePathMap.put(iconLabel, imagePath);
                iconLabel.setBorder(FontsAndBorders.EMPTY_BORDER);
                viewer.add(iconLabel);
                iconLabel.addMouseListener(new MouseAdapter() {
                    private final Border defaultBorder = iconLabel.getBorder();
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        iconLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        iconLabel.setBorder(defaultBorder);
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.getButton() == 1) {
                            final JLabel imageLabel = (JLabel)e.getSource();
                            if (imageLabel != selectedImageLabel) {
                                setSelectedAvatar((JLabel)e.getSource());
                            } else {
                                notifyConsumer(imageLabel);
                                getFrame().closeActiveScreen(false);
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        viewer.revalidate();
        viewer.repaint();
    }

    private void setSelectedAvatar(final JLabel iconLabel) {
        final Icon icon = iconLabel.getIcon();
        final BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics g = bi.createGraphics();
        // paint the Icon to the BufferedImage.
        icon.paintIcon(null, g, 0,0);
        g.dispose();
        rightActionButton =
                new ActionBarButton(
                        new ImageIcon(GraphicsUtilities.scale(bi, 46, 46)),
                        "Select Avatar", "Click to select this avatar image.",
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                notifyConsumer(iconLabel);
                                getFrame().closeActiveScreen(false);
                            }
                        });

        refreshActionBar();
        this.selectedImageLabel = iconLabel;
    }

    private void notifyConsumer(final JLabel selectedLabel) {
        if (consumer != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    consumer.setSelectedAvatarPath(imagePathMap.get(selectedLabel));
                }
            });
        }
    }

    private class AvatarImageSetsPanel extends TexturedPanel implements IThemeStyle {
        
        public AvatarImageSetsPanel() {

            // List of avatar image sets.
            final JList<AvatarImageSet> imageSetsList = new JList<>(getAvatarImageSetsArray());
            imageSetsList.setOpaque(false);
            imageSetsList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                displayImageSetIcons(imageSetsList.getSelectedValue());
                                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            }
                        });
                    }
                }
            });
            imageSetsList.setSelectedIndex(0);

            final ImageSetsListRenderer renderer = new ImageSetsListRenderer();
            imageSetsList.setCellRenderer(renderer);

            final JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(imageSetsList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);

            setLayout(new MigLayout("insets 0, gap 0, flowy"));
            setBorder(FontsAndBorders.BLACK_BORDER);
            add(scrollPane, "w 100%, h 100%");

            refreshStyle();

        }

        @Override
        public final void refreshStyle() {
            final Theme THEME = ThemeFactory.getInstance().getCurrentTheme();
            final Color refBG = THEME.getColor(Theme.COLOR_TITLE_BACKGROUND);
            final Color thisBG = new Color(refBG.getRed(), refBG.getGreen(), refBG.getBlue(), 200);
            setBackground(thisBG);
        }

    }

    private AvatarImageSet[] getAvatarImageSetsArray() {
        final List<AvatarImageSet> imageSetsList = getAvatarImageSetsList();
        return imageSetsList.toArray(new AvatarImageSet[imageSetsList.size()]);
    }

    private List<AvatarImageSet> getAvatarImageSetsList() {
        final List<AvatarImageSet> imageSets = new ArrayList<>();
        List<Path> directoryPaths = getDirectoryPaths(MagicFileSystem.getDataPath(DataPath.AVATARS));
        for (Path path : directoryPaths) {
            imageSets.add(loadImageSet(path));
        }
        return imageSets;
    }

    private AvatarImageSet loadImageSet(final Path imageSetDirectory) {
        final AvatarImageSet imageSet = new AvatarImageSet(imageSetDirectory);
        return imageSet;
    }

    private List<Path> getDirectoryPaths(final Path rootDirectory) {
        final List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> ds =
                Files.newDirectoryStream(
                        rootDirectory,
                        new DirectoriesOnlyFilter())) {
            for (Path p : ds) {
                paths.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    private static class DirectoriesOnlyFilter implements Filter<Path> {
        @Override
        public boolean accept(Path entry) throws IOException {
            return Files.isDirectory(entry);
        }
    }

    private class ImageSetsListRenderer extends JLabel implements ListCellRenderer<AvatarImageSet> {

        public ImageSetsListRenderer() {
            setOpaque(false);
        }

        /* (non-Javadoc)
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(
                JList<? extends AvatarImageSet> list, AvatarImageSet value, int index,
                boolean isSelected, boolean cellHasFocus) {

            final Color foreColor = isSelected ? MagicStyle.HIGHLIGHT_COLOR : Color.WHITE;

            final JLabel setNameLabel = new JLabel(value.getName());
            setNameLabel.setFont(FontsAndBorders.FONT2);
            setNameLabel.setForeground(foreColor);
            setNameLabel.setVerticalAlignment(SwingConstants.TOP);

            final JPanel infoPanel = new JPanel(new MigLayout("insets 0, gap 0, flowy"));
            infoPanel.setOpaque(false);
            infoPanel.setForeground(foreColor);
            infoPanel.add(setNameLabel, "w 100%, gapbottom 4");

            final JPanel itemPanel = new JPanel(new MigLayout("insets 0 0 0 6, gap 0"));
            itemPanel.setPreferredSize(new Dimension(0, 70));
            itemPanel.setOpaque(false);
            itemPanel.setForeground(foreColor);
            itemPanel.setBorder(isSelected ? BorderFactory.createLineBorder(MagicStyle.HIGHLIGHT_COLOR, 1) : null);
            itemPanel.add(new JLabel(value.getSampleImage()), "w 70!, h 70!");
            itemPanel.add(infoPanel, "w 100%");
            return itemPanel;

        }

    }

    /* (non-Javadoc)
     * @see magic.ui.IMagStatusBar#getScreenCaption()
     */
    @Override
    public String getScreenCaption() {
        return "Avatars";
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagActionBar#getLeftAction()
     */
    @Override
    public MenuButton getLeftAction() {
        return MenuButton.getCloseScreenButton("Cancel");
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagActionBar#getRightAction()
     */
    @Override
    public MenuButton getRightAction() {
        return rightActionButton;
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagActionBar#getMiddleActions()
     */
    @Override
    public List<MenuButton> getMiddleActions() {
        final List<MenuButton> buttons = new ArrayList<MenuButton>();
        buttons.add(new MenuButton("Avatars online...", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                URLUtils.openURL(URLUtils.URL_AVATARS);
            }
        }, "Get more avatars from the Magarena forum."));
        return buttons;
    }

    /* (non-Javadoc)
     * @see magic.ui.MagScreen#canScreenClose()
     */
    @Override
    public boolean isScreenReadyToClose(final AbstractScreen nextScreen) {
        return true;
    }

    /* (non-Javadoc)
     * @see magic.ui.interfaces.IStatusBar#getStatusPanel()
     */
    @Override
    public JPanel getStatusPanel() {
        return null;
    }

}
