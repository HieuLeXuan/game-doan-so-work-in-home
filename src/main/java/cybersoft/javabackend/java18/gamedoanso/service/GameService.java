package cybersoft.javabackend.java18.gamedoanso.service;

import cybersoft.javabackend.java18.gamedoanso.model.GameSession;
import cybersoft.javabackend.java18.gamedoanso.model.Guess;
import cybersoft.javabackend.java18.gamedoanso.model.Player;
import cybersoft.javabackend.java18.gamedoanso.model.Ranking;
import cybersoft.javabackend.java18.gamedoanso.repository.GameSessionRepository;
import cybersoft.javabackend.java18.gamedoanso.repository.GuessRepository;
import cybersoft.javabackend.java18.gamedoanso.repository.PlayerRepository;
import cybersoft.javabackend.java18.gamedoanso.repository.RankRepository;

import java.util.List;

public class GameService {
    private static GameService INSTANCE = null;
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private final GuessRepository guessRepository;
    private final RankRepository rankRepository;

    private GameService() {
        gameSessionRepository = new GameSessionRepository();
        playerRepository = new PlayerRepository();
        guessRepository = new GuessRepository();
        rankRepository = new RankRepository();
    }

    public static GameService getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new GameService();
        return INSTANCE;
    }

    public GameSession createGame(String username) {
        var gameSession = new GameSession(username);
        gameSession.setActive(true);

        // deactivate other games
        gameSessionRepository.deactivateAllGames(username);

        gameSessionRepository.save(gameSession);

        return gameSession;
    }

    public Player dangNhap(String username, String password) {
        Player player = playerRepository.findByUsername(username);

        if (player == null)
            return null;

        if (player.getPassword().equals(password))
            return player;

        return null;
    }

    public Player dangKy(String username, String password, String name) {
        if (!isValidUser(username, password, name))
            return null;

        boolean userExisted = playerRepository.existedByUsername(username);

        if (userExisted)
            return null;

        Player newUser = new Player(username, password, name);
        playerRepository.save(newUser);

        return newUser;
    }

    private boolean isValidUser(String username, String password, String name) {
        if (username == null || "".equals(username.trim()))
            return false;

        if (password == null || "".equals(password.trim()))
            return false;

        return name != null && !"".equals(name.trim());
    }

    public GameSession getCurrentGame(String username) {
        List<GameSession> games = gameSessionRepository.findByUsername(username);
        // get current active game, if there's no game -> create new one

        var activeGame = games.isEmpty()
                ? createGame(username)
                : games.stream()
                .filter(GameSession::getIsActive)
                .findFirst()
                .orElseGet(() -> createGame(username));

        // get guess list and add to game
        activeGame.setGuess(guessRepository
                .findBySession(activeGame.getId()));

        return activeGame;
    }

    public List<Ranking> getAllRank() {
        return rankRepository.findAllRank();
    }

    public void saveGuess(Guess guess) {
        guessRepository.save(guess);
    }

    public void skipAndPlayNewGame(String username) {
        createGame(username);
    }

    public GameSession getGameSession(String id) {
        GameSession gameSession = gameSessionRepository.findById(id);
        gameSession.setGuess(guessRepository.findBySession(id));
        return gameSession;
    }

    public List<GameSession> getGameSessionByUsername(String username) {
        return gameSessionRepository.findByUsername(username);
    }

    public void completeGame(GameSession gameSession) {
        // update game session table
        gameSessionRepository.completeGame(gameSession.getId());

        // save rank
        this.saveRank(gameSession.getUsername());
    }

    public void saveRank(String username) {
        // get best rank from game session and guess table (db)
        Ranking bestRank = rankRepository.getBestRank(username);

        // get all rank from ranking table
        List<Ranking> ranks = rankRepository.findAllRank();

        Ranking currentRank = ranks.stream()
                .filter(rank -> rank.getUsername().equals(bestRank.getUsername()))
                .findFirst()
                .orElse(null);

        if (currentRank != null) {
            rankRepository.updateRank(bestRank);
        } else {
            rankRepository.save(bestRank);
        }
    }

    public void changeActiveAndSave(String username, String gameSessionId) {
        // update in db
        List<GameSession> gameSessionList =
                gameSessionRepository.findByUsername(username);
        GameSession activeGame = gameSessionList.stream()
                .filter(gameSession -> gameSession.getIsActive() == true).findFirst().orElse(null);

        if (activeGame != null) {
            activeGame.setActive(false);
            gameSessionRepository.save(activeGame);
        }

        GameSession gameSession = gameSessionRepository.findById(gameSessionId);
        gameSession.setActive(true);

        gameSessionRepository.save(gameSession);
    }
}
