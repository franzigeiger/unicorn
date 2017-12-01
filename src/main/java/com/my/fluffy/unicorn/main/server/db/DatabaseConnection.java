package com.my.fluffy.unicorn.main.server.db;

import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class DatabaseConnection implements AutoCloseable {
    private final Connection connection;

    private DatabaseConnection(Connection connection) {
        this.connection = connection;
    }

    @NotNull
    public static DatabaseConnection create() throws SQLException, ClassNotFoundException {
        Connection c = ConnectionFactory.create();
        return new DatabaseConnection(c);
    }

    Connection getConnection() {
        return connection;
    }

    public DatabaseInserter getInserter() {
        return new DatabaseInserter(this);
    }

    public DatabaseQuery getQuery() {
        return new DatabaseQuery(this);
    }

    /**
     * WARNING: will take some time.
     */
    public void updateAggregates() throws SQLException {
        String query = "DELETE FROM election.secondvote_aggregates";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();

        query = "INSERT INTO election.secondvote_aggregates(district,party,votes)" +
                "SELECT b.district,s.party,COUNT(*) AS votes " +
                "FROM election.ballots AS b, election.statelists AS s " +
                "WHERE b.secondvote = s.id " +
                "GROUP BY b.district,s.party";
        stmt = connection.prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();

        query = "WITH aggregated_first AS (SELECT b.firstvote,COUNT(*) AS votes " +
                "    FROM election.ballots AS b " +
                "    GROUP BY b.firstvote " +
                "    HAVING b.firstvote IS NOT NULL) " +
                "UPDATE election.direct_candidatures " +
                "    SET votes = (SELECT votes FROM aggregated_first WHERE firstvote = id) " +
                "    WHERE EXISTS (SELECT * FROM aggregated_first WHERE firstvote = id)";
        stmt = connection.prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();

        query = "WITH invalid_first AS (" +
                "       SELECT district,COUNT(*) AS votes FROM election.ballots WHERE firstvote  IS NULL GROUP BY district)," +
                "     invalid_second AS (" +
                "       SELECT district,COUNT(*) AS votes FROM election.ballots WHERE secondvote IS NULL GROUP BY district)" +
                "UPDATE election.districts" +
                "   SET invalidfirstvotes =  (SELECT votes FROM invalid_first  WHERE district = id)," +
                "       invalidsecondvotes = (SELECT votes FROM invalid_second WHERE district = id)";
        stmt = connection.prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();
    }

    public void close() {
        if (this.connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
