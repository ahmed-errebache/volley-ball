package service;

import exception.*;
import model.Player;
import model.Team;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TournamentRegistration {
    private final LocalDate start = LocalDate.of(2025, 5, 9);
    private final LocalDate end   = LocalDate.of(2025, 5, 16);
    private final List<Team> teams = new ArrayList<>();

    public void registerTeam(Team team)
            throws RegistrationClosedException, TournamentFullException,
            InvalidTeamSizeException, DuplicatePlayerException,
            TeamAlreadyRegisteredException {
        LocalDate today = LocalDate.now();
        if (today.isBefore(start) || today.isAfter(end))
            throw new RegistrationClosedException("Inscriptions fermées");
        if (teams.size() >= 8)
            throw new TournamentFullException("Tournoi complet");
        if (team.getPlayers().size() != 6)
            throw new InvalidTeamSizeException("Une équipe doit avoir 6 joueurs");
        for (Team t : teams) {
            if (t.getName().equalsIgnoreCase(team.getName()))
                throw new TeamAlreadyRegisteredException("Nom d'équipe déjà pris");
            for (Player p : team.getPlayers()) {
                if (t.getPlayers().contains(p))
                    throw new DuplicatePlayerException("Le joueur " + p + " est déjà inscrit");
            }
        }
        teams.add(team);
    }

    public List<Team> listTeams() {
        return Collections.unmodifiableList(teams);
    }
}