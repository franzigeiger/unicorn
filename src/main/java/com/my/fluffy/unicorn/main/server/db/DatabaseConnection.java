package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.*;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;

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

    protected Connection getConnection() {
        return connection;
    }

    public DatabaseInserter getInserter() {
        return new DatabaseInserter(this);
    }

    public <T> void insert(T t) throws SQLException {
        if (t instanceof Ballot) {
            insertBallot((Ballot) t);
        } else if (t instanceof Candidate) {
            insertCandidate((Candidate) t);
        } else if (t instanceof DirectCandidature) {
            throw new IllegalArgumentException("Please insert the candidate");
        } else if (t instanceof ListPlacement) {
            throw new IllegalArgumentException("Please insert the candidate");
        } else if (t instanceof ElectionDistrict) {
            insertElectionDistrict((ElectionDistrict) t);
        } else if (t instanceof Party) {
            insertParty((Party) t);
        } else if (t instanceof PartyResults) {
            insertPartyResults((PartyResults) t);
        } else if (t instanceof State) {
            insertState((State) t);
        } else {
            throw new IllegalArgumentException("Please insert the candidate");
        }
    }

    private void insertBothElections() throws SQLException {
        String query = "INSERT INTO \"Wahl\"(\"Jahr\",\"datum\") VALUES (?,?)";
        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, 2013);
        stmt.setDate(2, Date.valueOf(LocalDate.of(2013, 9,22)));
        stmt.executeUpdate();

        stmt.setInt(1, 2017);
        stmt.setDate(2, Date.valueOf(LocalDate.of(2017, 9,24)));
        stmt.executeUpdate();
        stmt.close();
    }

    private void insertBallot(Ballot t) throws SQLException {
        String query = "INSERT INTO \"Stimme\"(\"Erststimme\",\"Zweitstimme\",\"wahlkreis\") VALUES (?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        if (t.firstVote == -1) {
            stmt.setNull(1, Types.INTEGER);
        } else {
            stmt.setInt(1, t.firstVote);
        }
        if (t.secondVote == -1) {
            stmt.setNull(2, Types.INTEGER);
        } else {
            stmt.setInt(2, t.secondVote);
        }
        // FIXME: get district from ballot
        stmt.setNull(3, Types.VARCHAR);

        stmt.executeUpdate();
        stmt.close();
    }

    private void insertCandidate(Candidate t) throws SQLException {
        String query = "INSERT INTO " +
                "\"Kandidat\"(\"ID\",\"Vorname\",\"nachname\",\"beruf\",\"geburtsdatum\",\"titel\",\"geschlecht\") " +
                "VALUES (?,?,?,?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, t.id);
        stmt.setString(2, t.firstName);
        stmt.setString(3, t.name);
        stmt.setString(4, t.profession);
        stmt.setDate(5, Date.valueOf(LocalDate.of(t.birthYear, 1, 1)));
        stmt.setString(6, t.title);
        stmt.setString(7, t.gender);
        stmt.executeUpdate();
        stmt.close();

        if (t.directCandidature != null) {
            query = "INSERT INTO \"Direktkandidatur\"(\"Wahl\",\"Wahhlkreis\",\"Kandidat\",\"Partei\",\"Stimmen\") " +
                    "VALUES (?,?,?,?,?)";
            stmt = connection.prepareStatement(query);
            // FIXME: wahljahr
            stmt.setInt(1, 2017);
            // FIXME
            stmt.setString(2, t.directCandidature.electionDistrict + "");
            stmt.setInt(3, t.id);
            // FIXME
            stmt.setString(4, t.directCandidature.party + "");
            stmt.setInt(5, 0);

            stmt.executeUpdate();
            stmt.close();
        }
        if (t.listPlacement != null) {
            query = "INSERT INTO \"Landeslistenkandidatur\"(\"Kandidat\",\"Landesliste\",\"Listenplatz\") " +
                    "VALUES (?,?,?)";
            stmt = connection.prepareStatement(query);
            // FIXME this is the party id, not the list id
            stmt.setInt(1,t.id);
            stmt.setInt(1,t.listPlacement.state);
            stmt.setInt(1,t.listPlacement.place);

            stmt.executeUpdate();
            stmt.close();
        }
    }

    private void insertElectionDistrict(ElectionDistrict t) throws SQLException {
        String query = "INSERT INTO " +
                "\"Wahlkreis\"(\"Name\",\"Wahl\",\"Waehlerzahl\",\"ungueltige_1\",\"ungueltige_2\",\"bundesland\") " +
                "VALUES (?,?,?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setString(1, t.name);
        stmt.setInt(2, 2013);
        stmt.setInt(3, t.eligebleVoters_13);
        stmt.setInt(4, t.invalid_13_first);
        stmt.setInt(5, t.invalid_13_second);
        stmt.setString(6, t.state);

        stmt.executeUpdate();

        stmt.setString(1, t.name);
        stmt.setInt(2, 2017);
        stmt.setInt(3, t.eligibleVoters_17);
        stmt.setInt(4, t.invalid_17_first);
        stmt.setInt(5, t.invalid_17_second);
        stmt.setString(6, t.state);

        stmt.executeUpdate();
        stmt.close();
    }

    private void insertParty(Party t) throws SQLException {
        String query = "INSERT INTO " +
                "\"Partei\"(\"kuerzel\",\"Name\") " +
                "VALUES (?,?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        // FIXME get correct short name
        stmt.setString(1, cutAt(t.name, 10));
        stmt.setString(2, t.name);
    }

    private void insertPartyResults(PartyResults t) {
        // TODO implement
    }

    private void insertState(State t) throws SQLException {
        String query = "INSERT INTO \"Bundesland\"(\"Kuerzel\",\"Name\",\"SitzZahl\") " +
                "VALUES (?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(query);

        // FIXME get correct short name
        stmt.setString(1, cutAt(t.name, 10));
        stmt.setString(2, t.name);
        stmt.setInt(3, 0);

        stmt.executeUpdate();
        stmt.close();
    }

    public void updateAggregates() {
        // TODO implement
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
