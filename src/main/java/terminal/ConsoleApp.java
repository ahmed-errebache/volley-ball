package terminal;

import model.Player;
import model.Team;
import service.TournamentRegistration;
import exception.*;

import java.util.*;

public class ConsoleApp {
    public static void main(String[] args) {
        TournamentRegistration registration = new TournamentRegistration();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Afficher les dates d'inscription");
            System.out.println("2. Inscrire une équipe");
            System.out.println("3. Voir les équipes inscrites");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    afficherDates(scanner);
                    break;

                case "2":
                    inscrireEquipe(scanner, registration);
                    break;

                case "3":
                    afficherEquipes(registration);
                    break;

                case "0":
                    System.out.println("👋 Merci d'avoir utilisé le système d'inscription.");
                    scanner.close();
                    return;

                default:
                    System.out.println("❌ Choix invalide. Veuillez réessayer.");
            }
        }
    }

    private static void afficherDates(Scanner scanner) {
        System.out.println("\n📅 Inscriptions ouvertes du 9 mai au 16 mai 2025 inclus.");
        while (true) {
            System.out.println("\nQue souhaitez-vous faire ensuite ?");
            System.out.println("1. Revenir au menu principal");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) {
                return;
            } else if (input.equals("0")) {
                System.out.println("👋 Merci d'avoir utilisé le système d'inscription.");
                System.exit(0);
            } else {
                System.out.println("❌ Choix invalide. Veuillez réessayer.");
            }
        }
    }

    private static void inscrireEquipe(Scanner scanner, TournamentRegistration registration) {
        try {
            System.out.println("\n=== Inscription d'une nouvelle équipe ===");
            List<Player> players = new ArrayList<>();

            for (int i = 1; i <= 6; i++) {
                String firstName = "", lastName = "";
                while (firstName.isEmpty()) {
                    System.out.print("Joueur " + i + " - Prénom : ");
                    firstName = scanner.nextLine().trim();
                    if (firstName.isEmpty()) System.out.println("⚠️ Prénom vide, réessayez.");
                }
                while (lastName.isEmpty()) {
                    System.out.print("Joueur " + i + " - Nom    : ");
                    lastName = scanner.nextLine().trim();
                    if (lastName.isEmpty()) System.out.println("⚠️ Nom vide, réessayez.");
                }

                Player player = new Player(firstName, lastName);
                if (players.contains(player)) {
                    System.out.println("⚠️ Ce joueur est déjà présent dans cette équipe. Saisir un autre joueur.");
                    i--; continue;
                }

                players.add(player);
            }

            System.out.print("\nNom de l'équipe : ");
            String teamName = scanner.nextLine().trim();

            Team team = new Team(teamName);
            players.forEach(team::addPlayer);

            registration.registerTeam(team);
            System.out.println("✅ Équipe '" + team.getName() + "' inscrite avec succès !");

        } catch (RegistrationClosedException e) {
            System.out.println("❌ Inscriptions fermées : " + e.getMessage());
        } catch (TournamentFullException e) {
            System.out.println("❌ Tournoi complet : " + e.getMessage());
        } catch (InvalidTeamSizeException e) {
            System.out.println("❌ Taille d'équipe invalide : " + e.getMessage());
        } catch (DuplicatePlayerException e) {
            System.out.println("❌ Joueur déjà inscrit dans une autre équipe : " + e.getMessage());
        } catch (TeamAlreadyRegisteredException e) {
            System.out.println("❌ Nom d'équipe déjà pris : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Erreur inattendue : " + e.getMessage());
        }
    }

    private static void afficherEquipes(TournamentRegistration registration) {
        System.out.println("\n=== Équipes inscrites ===");
        List<Team> teams = registration.listTeams();
        if (teams.isEmpty()) {
            System.out.println("Aucune équipe inscrite pour le moment.");
            return;
        }
        for (Team t : teams) {
            System.out.println("- " + t.getName());
            t.getPlayers().forEach(p -> System.out.println("   • " + p));
        }
    }
}
