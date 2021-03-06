package magic.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import magic.MagicUtility;
import magic.ai.MagicAI;
import magic.data.DeckGenerators;
import magic.data.DeckUtils;
import magic.data.DuelConfig;
import magic.model.phase.MagicDefaultGameplay;
import magic.model.player.PlayerProfile;
import magic.ui.duel.viewer.DeckStrengthViewer;
import magic.utility.MagicFileSystem;
import magic.utility.MagicFileSystem.DataPath;

public class MagicDuel {

    private static final String OPPONENT="opponent";
    private static final String GAME="game";
    private static final String PLAYED="played";
    private static final String WON="won";
    private static final String START="start";

    private final DuelConfig duelConfig;
    private MagicPlayerDefinition[] playerDefinitions;
    private MagicAI[] ais;
    private int opponentIndex;
    private int gameNr;
    private int gamesPlayed;
    private int gamesWon;
    private int startPlayer;
    private final int[] difficulty = new int[2];

    public MagicDuel(final DuelConfig configuration) {
        this.duelConfig=configuration;
        ais=configuration.getPlayerAIs();
        restart();
    }

    public MagicDuel() {
        this(new DuelConfig());
    }

    public MagicDuel(final DuelConfig configuration,final MagicDuel duel) {
        this(configuration);
        playerDefinitions=duel.playerDefinitions;
    }

    public DuelConfig getConfiguration() {
        return duelConfig;
    }

    public int getGameNr() {
        return gameNr;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesTotal() {
        return (playerDefinitions.length-1)*duelConfig.getNrOfGames();
    }

    public int getGamesWon() {
        return gamesWon;
    }

    private void determineStartPlayer() {
        startPlayer=MagicRandom.nextRNGInt(2);
    }

    public void setStartPlayer(final int startPlayer) {
        this.startPlayer=startPlayer;
    }

    public void setAIs(final MagicAI[] aAIs) {
        this.ais = aAIs;
    }

    public MagicAI[] getAIs() {
        return ais;
    }

    public void setDifficulty(final int diff) {
        setDifficulty(0,diff);
        setDifficulty(1,diff);
    }

    public void setDifficulty(final int idx, final int diff) {
        difficulty[idx] = diff;
    }

    public int getDifficulty() {
        return getDifficulty(0);
    }

    int getDifficulty(final int idx) {
        return difficulty[idx];
    }

    public void updateDifficulty() {
        final int aiDifficulty = duelConfig.getAiDifficulty();
        difficulty[0] = aiDifficulty;
        difficulty[1] = aiDifficulty;
    }

    public boolean isEditable() {
        return gameNr==1;
    }

    public boolean isFinished() {
        // if a duel consists of a total of X games, then in an interactive
        // game it should be "best of" X games, not first to X.
        final int player1GamesWon = getGamesWon();
        final int player2GamesWon = getGamesPlayed() - player1GamesWon;
        final int gamesRequiredToWin = (int)Math.ceil(getGamesTotal()/2.0);
        return (player1GamesWon >= gamesRequiredToWin) || (player2GamesWon >= gamesRequiredToWin);
    }

    void advance(final boolean won, final MagicGame game) {
        gamesPlayed++;
        if (won) {
            gamesWon++;
            startPlayer=1;
        } else {
            startPlayer=0;
        }
        gameNr++;
        if (gameNr>duelConfig.getNrOfGames()) {
            gameNr=1;
            opponentIndex++;
            determineStartPlayer();
        }

        if (!DeckStrengthViewer.isRunning() && !MagicUtility.isTestGame()) {
            duelConfig.getPlayerProfile(0).getStats().update(won, game.getPlayer(0), game);
            duelConfig.getPlayerProfile(1).getStats().update(!won, game.getPlayer(1), game);
        }
    }

    private MagicPlayerDefinition[] createPlayers() {
        final MagicPlayerDefinition[] players = new MagicPlayerDefinition[DuelConfig.MAX_PLAYERS];
        players[0] =
                new MagicPlayerDefinition(
                        duelConfig.getPlayerProfile(0),
                        duelConfig.getPlayerDeckProfile(0));
        players[1] =
                new MagicPlayerDefinition(
                        duelConfig.getPlayerProfile(1),
                        duelConfig.getPlayerDeckProfile(1));
        return players;
    }

    public MagicGame nextGame(final boolean sound) {
        //create players
        final MagicPlayer player   = new MagicPlayer(duelConfig.getStartLife(),playerDefinitions[0],0);
        final MagicPlayer opponent = new MagicPlayer(duelConfig.getStartLife(),playerDefinitions[opponentIndex],1);

        //give the AI player extra life
        opponent.setLife(opponent.getLife() + duelConfig.getAiExtraLife());

        //determine who starts first
        final MagicPlayer start    = startPlayer == 0 ? player : opponent;

        //create game
        final MagicGame game = MagicGame.create(
                this,
                MagicDefaultGameplay.getInstance(),
                new MagicPlayer[]{player,opponent},
                start,
                sound);

        //create hand and library
        player.createHandAndLibrary(duelConfig.getHandSize());
        opponent.createHandAndLibrary(duelConfig.getHandSize());
        return game;
    }

    public int getNrOfPlayers() {
        return playerDefinitions.length;
    }

    public MagicPlayerDefinition getPlayer(final int index) {
        return playerDefinitions[index];
    }

    public MagicPlayerDefinition[] getPlayers() {
        return playerDefinitions;
    }

    // only used by magic.test classes.
    public void setPlayers(final MagicPlayerDefinition[] aPlayerDefinitions) {
        this.playerDefinitions=aPlayerDefinitions;
    }

    // Used by the "Generate Deck" button in DuelPanel.
    public void buildDeck(final MagicPlayerDefinition player) {
        DeckGenerators.setRandomDeck(player);
    }

    private void buildDecks() {
        for (final MagicPlayerDefinition player : playerDefinitions) {
            switch (player.getDeckProfile().getDeckType()) {
            case Random:
                DeckGenerators.setRandomDeck(player);
                break;
            case Preconstructed:
                setDeckFromFile(player, DeckUtils.getPrebuiltDecksFolder());
                break;
            case Custom:
                setDeckFromFile(player, Paths.get(DeckUtils.getDeckFolder()));
                break;
            case Firemind:
                setDeckFromFile(player, DeckUtils.getFiremindDecksFolder());
                break;            
            default:
                break;
            }
        }
    }

    private void setDeckFromFile(final MagicPlayerDefinition player, final Path deckFolder) {
        final String deckFilename = player.getDeckProfile().getDeckValue() + DeckUtils.DECK_EXTENSION;
        player.setDeck(loadDeck(deckFolder.resolve(deckFilename)));
    }

    private MagicDeck loadDeck(final Path deckFilePath) {
        try {
            return DeckUtils.loadDeckFromFile(deckFilePath);
        } catch (IOException ex) {
            throw new RuntimeException("Invalid deck: " + deckFilePath.toString(), ex);
        }
    }

    public void initialize() {
        playerDefinitions=createPlayers();
        buildDecks();
    }

    public static final File getDuelFile() {
        return MagicFileSystem.getDataPath(DataPath.DUELS).resolve("saved.duel").toFile();
    }

    private static String getPlayerPrefix(final int index) {
        return "p"+(index+1)+".";
    }

    private void save(final Properties properties) {
        duelConfig.save(properties);

        properties.setProperty(OPPONENT,Integer.toString(opponentIndex));
        properties.setProperty(GAME,Integer.toString(gameNr));
        properties.setProperty(PLAYED,Integer.toString(gamesPlayed));
        properties.setProperty(WON,Integer.toString(gamesWon));
        properties.setProperty(START,Integer.toString(startPlayer));

        for (int index=0;index<playerDefinitions.length;index++) {
            playerDefinitions[index].save(properties,getPlayerPrefix(index));
        }
    }

    public void save(final File file) {
        final Properties properties = getNewSortedProperties();
        save(properties);
        try {
            magic.data.FileIO.toFile(file, properties, "Duel");
        } catch (final IOException ex) {
            System.err.println("ERROR! Unable save duel to " + file);
        }
    }

    @SuppressWarnings("serial")
    private Properties getNewSortedProperties() {
       return new Properties() {
           @Override
           public synchronized Enumeration<Object> keys() {
               return Collections.enumeration(new TreeSet<Object>(super.keySet()));
           }
       };
    }

    private void load(final Properties properties) {
        duelConfig.load(properties);

        opponentIndex=Integer.parseInt(properties.getProperty(OPPONENT,"1"));
        gameNr=Integer.parseInt(properties.getProperty(GAME,"1"));
        gamesPlayed=Integer.parseInt(properties.getProperty(PLAYED,"0"));
        gamesWon=Integer.parseInt(properties.getProperty(WON,"0"));
        startPlayer=Integer.parseInt(properties.getProperty(START,"0"));

        playerDefinitions=new MagicPlayerDefinition[2];
        for (int index=0;index<playerDefinitions.length;index++) {
            playerDefinitions[index]=new MagicPlayerDefinition();
            playerDefinitions[index].load(properties,getPlayerPrefix(index));
        }
    }

    public void load(final File file) {
        load(magic.data.FileIO.toProp(file));
    }

    public void restart() {
        opponentIndex=1;
        gameNr=1;
        gamesPlayed=0;
        gamesWon=0;
        determineStartPlayer();
    }

    public PlayerProfile getWinningPlayerProfile() {
        if (!isFinished()) {
            return null;
        } else {
            final int playerOneGamesWon = getGamesWon();
            final int playerTwoGamesWon = getGamesPlayed() - playerOneGamesWon;
            if (playerOneGamesWon > playerTwoGamesWon) {
                return getConfiguration().getPlayerProfile(0);
            } else {
                return getConfiguration().getPlayerProfile(1);
            }
        }
    }
}
