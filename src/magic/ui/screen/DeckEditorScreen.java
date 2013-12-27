package magic.ui.screen;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import magic.MagicMain;
import magic.data.DeckUtils;
import magic.model.MagicDeck;
import magic.ui.ExplorerPanel;
import magic.ui.MagicFrame;
import magic.ui.ScreenOptionsOverlay;
import magic.ui.interfaces.IMagActionBar;
import magic.ui.interfaces.IMagScreenOptionsMenu;
import magic.ui.interfaces.IMagStatusBar;
import magic.ui.viewer.DeckDescriptionPreview;
import magic.ui.widget.FontsAndBorders;
import magic.ui.widget.MenuButton;
import magic.ui.widget.MenuPanel;

@SuppressWarnings("serial")
public class DeckEditorScreen
    extends AbstractScreen
    implements IMagStatusBar, IMagActionBar, IMagScreenOptionsMenu {

    private static ExplorerPanel content;
    private final MagicFrame frame;

    // CTR : opens Deck Editor ready to update passed in deck.
    public DeckEditorScreen(final MagicFrame frame0, final MagicDeck deck) {
        super(getScreenContent(deck), frame0);
        this.frame = frame0;
    }
    // CTR : open Deck Editor in standalone mode starting with an empty deck.
    public DeckEditorScreen(final MagicFrame frame0) {
        this(frame0, null);
    }

    private static JPanel getScreenContent(final MagicDeck deck) {
        content = new ExplorerPanel(deck);
        return content;
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagActionBar#getLeftAction()
     */
    @Override
    public MenuButton getLeftAction() {
        if (!content.isStandaloneDeckEditor()) {
            return new MenuButton("Cancel", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    frame.closeActiveScreen(false);
                }
            });
        } else {
            return new MenuButton("Close", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    frame.closeActiveScreen(false);
                }
            });
        }
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagActionBar#getRightAction()
     */
    @Override
    public MenuButton getRightAction() {
        if (!content.isStandaloneDeckEditor()) {
            return new MenuButton("Use this deck", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (content.validateDeck(true) && content.applyDeckUpdates()) {
                        frame.closeActiveScreen(false);
                    }
                }
            });
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagActionBar#getMiddleActions()
     */
    @Override
    public List<MenuButton> getMiddleActions() {
        final List<MenuButton> buttons = new ArrayList<MenuButton>();
        buttons.add(new MenuButton("Load deck", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                loadDeck();
            }
        }, "Load deck from file"));
        if (content.isStandaloneDeckEditor()) {
            buttons.add(new MenuButton("Save", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    saveDeck();
                }
            }, "Save deck to file"));
            buttons.add(new MenuButton("Clear", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(
                            MagicMain.rootFrame,
                            "Remove all cards from the deck?") == JOptionPane.YES_OPTION) {
                        createNewEmptyDeck();
                    }
                }
            }, "Clear all cards from deck"));
        }
        return buttons;
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagStatusBar#getScreenCaption()
     */
    @Override
    public String getScreenCaption() {
        return "Deck Editor";
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagScreenOptionsMenu#showOptionsMenuOverlay()
     */
    @Override
    public void showOptionsMenuOverlay() {
       new ScreenOptions(frame, this);
    }

    public void createNewEmptyDeck() {
        content.setDeck(new MagicDeck());
    }

    public void loadDeck() {
        final JFileChooser fileChooser=new JFileChooser(DeckUtils.getDeckFolder());
        fileChooser.setDialogTitle("Load deck");
        fileChooser.setFileFilter(DeckUtils.DECK_FILEFILTER);
        fileChooser.setAcceptAllFileFilterUsed(false);
        // Add the description preview pane
        fileChooser.setAccessory(new DeckDescriptionPreview(fileChooser));
        final int action=fileChooser.showOpenDialog(this);
        if (action==JFileChooser.APPROVE_OPTION) {
            final String filename=fileChooser.getSelectedFile().getAbsolutePath();
            content.setDeck(DeckUtils.loadDeckFromFile(filename));
        }
    }

    public void saveDeck() {

        final JFileChooser fileChooser = new JFileChooser(DeckUtils.getDeckFolder()) {
            @Override
            public void approveSelection() {
                // first ensure filename has "dec" extension
                String filename = getSelectedFile().getAbsolutePath();
                if (!filename.endsWith(DeckUtils.DECK_EXTENSION)) {
                    setSelectedFile(new File(filename + DeckUtils.DECK_EXTENSION));
                }
                final Path prebuiltDecksFolder = DeckUtils.getPrebuiltDecksFolder();
                final Path saveFolder = getSelectedFile().toPath().getParent();
                if (saveFolder.equals(prebuiltDecksFolder)) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "This directory is reserved for prebuilt decks.\nPlease choose a different directory.",
                            "Invalid directory",
                            JOptionPane.WARNING_MESSAGE);
                } else if (Files.exists(getSelectedFile().toPath())) {
                    int response = JOptionPane.showConfirmDialog(
                            frame,
                            "Overwrite existing deck file?",
                            "Overwrite file",
                            JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        super.approveSelection();
                    }
                } else {
                    super.approveSelection();
                }
            }
        };
        fileChooser.setDialogTitle("Save deck");
        fileChooser.setFileFilter(DeckUtils.DECK_FILEFILTER);
        fileChooser.setAcceptAllFileFilterUsed(false);
        final int action = fileChooser.showSaveDialog(this);
        if (action == JFileChooser.APPROVE_OPTION) {
            final String filename = fileChooser.getSelectedFile().getAbsolutePath();
            if (DeckUtils.saveDeck(filename, content.getDeck())) {
                final String shortFilename = fileChooser.getSelectedFile().getName();
                content.getDeck().setName(shortFilename);
                content.setDeck(content.getDeck());
            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "There was a problem saving the deck file!",
                        "Deck not saved",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see magic.ui.MagScreen#canScreenClose()
     */
    @Override
    public boolean isScreenReadyToClose(final AbstractScreen nextScreen) {
        if (content.isDeckEditor() && !content.isStandaloneDeckEditor() && nextScreen instanceof DuelDecksScreen) {
            ((DuelDecksScreen)nextScreen).updateDecksAfterEdit();
        }
        return true;
    }


    private class ScreenOptions extends ScreenOptionsOverlay {

        private final DeckEditorScreen screen;

        public ScreenOptions(final MagicFrame frame0, final DeckEditorScreen screen0) {
            super(frame0);
            this.screen = screen0;
        }

        /* (non-Javadoc)
         * @see magic.ui.ScreenOptionsOverlay#getScreenMenu()
         */
        @Override
        protected MenuPanel getScreenMenu() {

            final MenuPanel menu = new MenuPanel("Deck Options");

            menu.addMenuItem("Clear deck", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    screen.createNewEmptyDeck();
                    hideOverlay();
                }
            });
//            menu.addMenuItem("New random deck", new AbstractAction() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    setVisible(false);
//                }
//            });
            menu.addMenuItem("Load deck", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    hideAllMenuPanels();
                    screen.loadDeck();
                    hideOverlay();
                }
            });
            menu.addMenuItem("Save deck", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    hideAllMenuPanels();
                    screen.saveDeck();
                    hideOverlay();
                }
            });
            menu.addBlankItem();
            menu.addMenuItem("Close menu", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    hideOverlay();
                }
            });

            menu.refreshLayout();
            menu.setBackground(FontsAndBorders.IMENUOVERLAY_MENUPANEL_COLOR);
            return menu;
        }
    }

}