package magic.ui.widget.downloader;

import magic.ui.widget.downloader.DownloaderPanel;
import java.util.Collection;
import javax.swing.SwingUtilities;
import magic.data.CardDefinitions;
import magic.model.MagicCardDefinition;

@SuppressWarnings("serial")
public class UnimplementedDownloaderPanel extends DownloaderPanel {

    @Override
    protected String getProgressCaption() {
        return "Unimplemented cards, missing images = ";
    }

    @Override
    protected Collection<MagicCardDefinition> getCards() {
        assert !SwingUtilities.isEventDispatchThread();
        return CardDefinitions.getMissingCards();
    }

    @Override
    protected String getLogFilename() {
        return "unimplemented.log";
    }

}