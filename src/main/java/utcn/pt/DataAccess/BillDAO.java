package utcn.pt.DataAccess;

import utcn.pt.Connection.ConnectionFactory;
import utcn.pt.Model.Bill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillDAO {
    private static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());

    //insert new bill into Log table
    public Bill insert(Bill b) {
        String sql = """
            INSERT INTO Log
              (clientId, productId, quantity, billDate, totalPrice)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, b.getClientId());
            st.setInt(2, b.getProductId());
            st.setInt(3, b.getQuantity());
            st.setTimestamp(4, b.getBillDate());
            st.setDouble(5, b.getTotalPrice());

            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    return new Bill(
                            generatedId,
                            b.getClientId(),
                            b.getProductId(),
                            b.getQuantity(),
                            b.getBillDate(),
                            b.getTotalPrice()
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:insert " + e.getMessage());
        }
        return b;
    }

    //find bill after
    public Bill findById(int id) {
        String sql = "SELECT * FROM Log WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:findById " + e.getMessage());
        }
        return null;
    }

    //return all bills in insertion order
    public List<Bill> findAll() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM Log ORDER BY id";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:findAll " + e.getMessage());
        }
        return list;
    }

    // map the current ResultSet row into a bill
    private Bill mapRow(ResultSet rs) throws SQLException {
        return new Bill(
                rs.getInt("id"),
                rs.getInt("clientId"),
                rs.getInt("productId"),
                rs.getInt("quantity"),
                rs.getTimestamp("billDate"),
                rs.getDouble("totalPrice")
        );
    }
}
