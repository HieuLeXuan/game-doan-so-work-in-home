package cybersoft.javabackend.java18.gamedoanso.repository;

import cybersoft.javabackend.java18.gamedoanso.model.Ranking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RankRepository extends AbstractRepository<Ranking> {

    public List<Ranking> findAllRank() {
        final String query = """
                select username, num_play, time_play
                from ranking
                order by num_play, time_play
                """;
        return executeQuery(connection -> {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Ranking> ranks = new ArrayList<>();

            while (resultSet.next()) {
                ranks.add(new Ranking()
                        .username(resultSet.getString("username"))
                        .numPlay(resultSet.getInt("num_play"))
                        .timePlay(resultSet.getInt("time_play")));
            }
            return ranks;
        });
    }

    public void save(Ranking rank) {
        final String query = """
                insert into ranking
                (username, num_play, time_play)
                values(?, ?, ?)
                """;
        executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, rank.getUsername());
            statement.setInt(2, rank.getNumPlay());
            statement.setInt(3, rank.getTimePlay());

            return statement.executeUpdate();
        });
    }

    public void updateRank(Ranking rank) {
        final String query = """
                update ranking
                set num_play = ?, time_play = ?
                where username = ?
                """;
        executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, rank.getNumPlay());
            statement.setInt(2, rank.getTimePlay());
            statement.setString(3, rank.getUsername());
            return statement.executeUpdate();
        });
    }

    public Ranking getBestRank(String username) {
        final String query = """
                    select table_a.username, table_a.num_play, min(table_a.time_play) as time_play
                    from
                    (
                        select username, gs.id, count(*) as num_play, timestampdiff(SECOND, start_time, end_time) as time_play
                        from game_session as gs join guess as g
                        on gs.id = g.session_id
                        where gs.is_completed = 1 and username = ?
                        group by username, gs.id, timestampdiff(SECOND, start_time, end_time)
                    ) as table_a
                    join
                    (
                        select username, min(num_play) as num_play
                        from
                        (
                            select username, gs.id, count(*) as num_play
                            from game_session as gs join guess as g
                            on gs.id = g.session_id
                            where gs.is_completed = 1 and username = ?
                            group by username, gs.id
                            ) as table_c
                            group by username
                    ) as table_b
                    on table_a.username = table_b.username and table_a.num_play = table_b.num_play
                    group by username, num_play
                    order by num_play asc, min(table_a.time_play) asc
                """;
        return executeQuerySingle(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, username);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return null;

            return new Ranking()
                    .username(resultSet.getString("username"))
                    .numPlay(resultSet.getInt("num_play"))
                    .timePlay(resultSet.getInt("time_play"));
        });
    }
}
