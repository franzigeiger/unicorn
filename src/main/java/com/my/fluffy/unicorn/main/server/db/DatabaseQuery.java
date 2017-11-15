package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.Candidate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseQuery {
    private DatabaseConnection db;

    DatabaseQuery(DatabaseConnection connection) {
        this.db = connection;
    }

    Candidate getCandidate(Candidate candidate) throws SQLException {
        String query = "SELECT * FROM candidates WHERE firstname=? AND lastname=? and yearofbirth=?;";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, candidate.getFirstName());
            stmt.setString(2, candidate.getLastName());
            stmt.setInt(3, candidate.getYearOfBirth());

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return Candidate.fullCreate(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getString(7), rs.getString(8), rs.getInt(9));
            }
        }
    }
}
