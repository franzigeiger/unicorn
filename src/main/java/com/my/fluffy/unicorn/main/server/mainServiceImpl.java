package com.my.fluffy.unicorn.main.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.my.fluffy.unicorn.main.client.data.*;
import com.my.fluffy.unicorn.main.client.mainService;
import com.my.fluffy.unicorn.main.server.db.DatabaseConnection;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class mainServiceImpl extends RemoteServiceServlet implements mainService {
    @Override
    public Map<Party, Integer> getParlamentSeats(int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().calculatePerParty(year);
            //all other basic infos for 2017 and 2013!
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<Party, Double> getPartyPercent(int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().getPartyPercent(year);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<Candidate, Party> getParlamentMembers(int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().getParlamentMembers(year);
            //all other basic infos for 2017 and 2013!
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<District> getAllDistricts(int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getQuery().getDistrictsPerYear(year);
        }
    }

    @Override
    public District getDistrict(int districtId, int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getQuery().getDistrict(districtId, year);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Candidate getDistrictWinner(int districtId) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().getDirectWinner(districtId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PartyStateInfos> getAdditionalMandatsPerParty(int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().getAdditionalMandats(year);
            //all other basic infos for 2017 and 2013!
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<State, Integer> getAdditionalMandatsPerstate() {
        return null;
    }

    @Override
    public List<Party> getParties() {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getQuery().getAllParties();
        }
    }

    @Override
    public List<Top10Data> getTopTen(int parteiID, int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            Party result;
            try (DatabaseConnection conn1 = DatabaseConnection.create()) {
                result = conn1.getQuery().getPartyById(parteiID);
            }
            return conn.getStatements().getTopTen(result, year);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Party, DifferenceFirstSecondVotes> getDifferencesFirstSecondVotes(int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().getDifferencesFirstSecondVotes(year);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<Party, Integer> getFirstVotesTotal(int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().getFirstVotesPerParty(year);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Integer> getAmountPerGender() {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().getAmountPerGender();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DistrictResults> getDistrictResults(int districtIdOld, int districtIdNew) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().getDistrictResults(districtIdOld, districtIdNew);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Integer, District> getDistrictMap() {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getQuery().getAllDistricts();
        }
    }

    @Override
    public Map<District, List<String>> getWinningParties(int year) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return conn.getStatements().getWinnigParties(year);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateAggregates() {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            conn.updateAggregates();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return 0;
    }
}
