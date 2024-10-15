package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Model.Message;
import Util.ConnectionUtil;

// check if message.posted_by is a real person
public class MessageDAO {
    static final Logger logger = LoggerFactory.getLogger(MessageDAO.class);

    public Message createMessage(Message message) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Account WHERE account_id = ?");
        stmt.setInt(1, message.getPosted_by());

        if (!stmt.executeQuery().next()) { // .next() = false when account doesnt exist
            logger.error("An account doesn't exist to send this message.");
            return null;
        }

        // user exists, add message to table
        stmt = conn.prepareStatement(
                "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);");
        stmt.setInt(1, message.getPosted_by());
        stmt.setString(2, message.getMessage_text());
        stmt.setLong(3, message.getTime_posted_epoch());

        stmt.execute();

        // now return the value, including message_id
        stmt = conn.prepareStatement("SELECT * FROM Message WHERE posted_by = ? AND time_posted_epoch = ? AND message_text = ?");
        stmt.setInt(1, message.getPosted_by());
        stmt.setLong(2, message.getTime_posted_epoch());
        stmt.setString(3, message.getMessage_text());

        logger.debug("Created query, attempting to return the message object ...");

        ResultSet response = stmt.executeQuery();
        if (response.next()) {
            logger.debug("Found correct message object, returning object ...");
            return new Message(
                    response.getInt("message_id"),
                    response.getInt("posted_by"),
                    response.getString("message_text"),
                    response.getLong("time_posted_epoch"));
        }
        // if the posted_by/time_posted query doesn't work, it will return null
        logger.error("Message wasn't found: %s", response.toString());
        return null;
    }

    public List<Message> getAllMessages() throws SQLException {
        Connection conn = ConnectionUtil.getConnection();

        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("SELECT * FROM Message");

        List<Message> messages = new ArrayList<>();

        while (res.next()) {
            Message message = new Message(
                    res.getInt("message_id"),
                    res.getInt("posted_by"),
                    res.getString("message_text"),
                    res.getLong("time_posted_epoch"));

            messages.add(message);
        }

        return messages;
    }

    public Message getMessageByID(int id) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();

        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM Message WHERE message_id = ?");
        stmt.setInt(1, id);

        logger.debug("Looking for message with ID %d ...", id);
        ResultSet response = stmt.executeQuery();

        if (response.next()) {
            return new Message(
                    response.getInt("message_id"),
                    response.getInt("posted_by"),
                    response.getString("message_text"),
                    response.getLong("time_posted_epoch"));
        }

        logger.error("No records found from database.");

        return null;
    }

    public Message deleteMessageByID(int id) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();

        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM Message WHERE message_id = ?;");
        stmt.setInt(1, id);
        ResultSet sqlResponse = stmt.executeQuery();

        Message response = null; // <-- message set if exists, else null
        if (sqlResponse.next()) {
            response = new Message(
                    sqlResponse.getInt("message_id"),
                    sqlResponse.getInt("posted_by"),
                    sqlResponse.getString("message_text"),
                    sqlResponse.getLong("time_posted_epoch"));
        }

        stmt = conn.prepareStatement(
                "DELETE FROM Message WHERE message_id = ?;");
        stmt.setInt(1, id);

        if (stmt.executeUpdate() > 0) {
            // you can log in here; Message record deleted.
            // response will return the correct things without this
        }

        return response;
    }

    public Message updateMessageByID(String message, int id) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Message SET message_text = ? WHERE message_id = ?;");
        stmt.setString(1, message);
        stmt.setInt(2, id);

        if (stmt.executeUpdate() > 0) { // if update occurred, return Message
            stmt = conn.prepareStatement("SELECT * FROM Message WHERE message_id = ?;");
            stmt.setInt(1, id);

            ResultSet sqlResponse = stmt.executeQuery();
            if (sqlResponse.next()) {
                return new Message(
                        sqlResponse.getInt("message_id"),
                        sqlResponse.getInt("posted_by"),
                        sqlResponse.getString("message_text"),
                        sqlResponse.getLong("time_posted_epoch"));
            }
        }
        // there was no messsage to update
        return null;
    }

    public List<Message> getMessagesByID(int id) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();

        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM Message WHERE posted_by = ?;");
        stmt.setInt(1, id);
        ResultSet sqlResponse = stmt.executeQuery();

        List<Message> response = new ArrayList<>(); // <-- messages set if exists, else null
        while (sqlResponse.next()) {
            response.add(new Message(
                    sqlResponse.getInt("message_id"),
                    sqlResponse.getInt("posted_by"),
                    sqlResponse.getString("message_text"),
                    sqlResponse.getLong("time_posted_epoch")));
        }

        return response;
    }
}
