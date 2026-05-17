import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:connect4_mentesek.db";

    public DatabaseManager() {
        initDatabase();
    }

    private void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS match_history (\n"
                     + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                     + " player_name TEXT NOT NULL,\n"
                     + " result TEXT NOT NULL,\n"
                     + " date_played DATETIME DEFAULT CURRENT_TIMESTAMP\n"
                     + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Adatbázis inicializálási hiba: " + e.getMessage());
        }
    }

    public void saveMatchResult(String playerName, String result) {
        String sql = "INSERT INTO match_history(player_name, result) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setString(2, result);
            pstmt.executeUpdate();
            System.out.println("\n[Rendszer] Az eredmény sikeresen elmentve az adatbázisba!");

        } catch (SQLException e) {
            System.out.println("\n[Rendszer] Mentési hiba: " + e.getMessage());
        }
    }

    public void printScoreBoard() {
        String sql = "SELECT result, COUNT(*) as count FROM match_history GROUP BY result";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n ÖSSZESÍTETT EREDMÉNYTÁBLA");
            int playerWins = 0, aiWins = 0, draws = 0;

            while (rs.next()) {
                String resultStr = rs.getString("result");
                int count = rs.getInt("count");

                if (resultStr.equals("Győzelem")) playerWins = count;
                else if (resultStr.equals("Vereség")) aiWins = count;
                else if (resultStr.equals("Döntetlen")) draws = count;
            }

            System.out.println(" Játékosok győzelmei: " + playerWins);
            System.out.println(" Gép (AI) győzelmei:  " + aiWins);
            System.out.println(" Döntetlenek száma:   " + draws);
            System.out.println("=====================================\n");

        } catch (SQLException e) {
            System.out.println("\n[Rendszer] Hiba az eredménytábla lekérésekor: " + e.getMessage());
        }
    }
}