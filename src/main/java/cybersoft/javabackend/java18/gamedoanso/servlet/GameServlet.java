package cybersoft.javabackend.java18.gamedoanso.servlet;

import cybersoft.javabackend.java18.gamedoanso.model.GameSession;
import cybersoft.javabackend.java18.gamedoanso.model.Guess;
import cybersoft.javabackend.java18.gamedoanso.model.Player;
import cybersoft.javabackend.java18.gamedoanso.model.Ranking;
import cybersoft.javabackend.java18.gamedoanso.service.GameService;
import cybersoft.javabackend.java18.gamedoanso.utils.JspUtils;
import cybersoft.javabackend.java18.gamedoanso.utils.UrlUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "gameServlet", urlPatterns = {
        UrlUtils.HOME,
        UrlUtils.GAME,
        UrlUtils.NEW_GAME,
        UrlUtils.XEP_HANG
})
public class GameServlet extends HttpServlet {
    private GameService gameService;

    // init -> service -> destroy
    @Override
    public void init() throws ServletException {
        super.init();
        gameService = GameService.getINSTANCE();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case UrlUtils.HOME -> currentGame(req, resp);
            case UrlUtils.GAME, UrlUtils.NEW_GAME -> loadGame(req, resp);
            case UrlUtils.XEP_HANG -> ranking(req, resp);
            default -> resp.sendRedirect(req.getContextPath() + UrlUtils.NOT_FOUND);
        }
    }

    private void currentGame(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var currentUser = (Player) req.getSession().getAttribute("currentUser");
        List<GameSession> currentGameSessions =
                gameService.getGameSessionByUsername(currentUser.getUsername());

        req.setAttribute("games", currentGameSessions);
        req.getRequestDispatcher(JspUtils.HOME).forward(req, resp);
    }

    private void ranking(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Ranking> ranks = gameService.getAllRank();
        // put in req
        req.setAttribute("ranks", ranks);
        req.getRequestDispatcher(JspUtils.XEP_HANG).forward(req, resp);
    }

    private void loadGame(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var currentUser = (Player) req.getSession().getAttribute(
                "currentUser");

        String typeFunction = req.getParameter("typeFunction");
        String gameSessionId = req.getParameter("gameSessionId");

        GameSession game;
        if (typeFunction != null && gameSessionId != null) {
//            gameService.changeActiveAndSave(currentUser.getUsername(), gameSessionId);
            game = gameService.getGameSession(gameSessionId);
        } else {
            game = gameService.getCurrentGame(currentUser.getUsername());
        }
        // put in req
        req.setAttribute("game", game);
        req.getRequestDispatcher(JspUtils.GAME)
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case UrlUtils.GAME -> processGame(req, resp);
            case UrlUtils.NEW_GAME -> processNewGame(req, resp);
            case UrlUtils.XEP_HANG -> req.getRequestDispatcher(JspUtils.XEP_HANG)
                    .forward(req, resp);
            default -> resp.sendRedirect(req.getContextPath() + UrlUtils.NOT_FOUND);
        }
    }

    private void processNewGame(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var currentUser = (Player) req.getSession().getAttribute("currentUser");
        // create new game/get existed game
        gameService.skipAndPlayNewGame(currentUser.getUsername());

        resp.sendRedirect(req.getContextPath() + UrlUtils.GAME);
    }

    private void processGame(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String gameSessionId = req.getParameter("game-session");
        int guessNumber = Integer.parseInt(req.getParameter("guess"));

        var gameSession = gameService.getGameSession(gameSessionId);

        if (gameSession == null) { // if the session is not existed, ask the player to sign in again
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + UrlUtils.DANG_NHAP);
            return;
        }

        gameSession.getGuess().add(createGuess(gameSession, guessNumber));

        if (guessNumber == gameSession.getTargetNumber()) {
            gameService.completeGame(gameSession);
        }

        resp.sendRedirect(req.getContextPath() + UrlUtils.GAME);
    }

    private Guess createGuess(GameSession gameSession, int guessNumber) {
        int result = Integer.compare(guessNumber, gameSession.getTargetNumber());
        Guess newGuess = new Guess(guessNumber, gameSession.getId(), result);
        gameService.saveGuess(newGuess);
        return newGuess;
    }
}
