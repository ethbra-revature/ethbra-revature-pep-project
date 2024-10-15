package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {

    final static Logger logger = LoggerFactory.getLogger(AccountDAO.class);


    public Account registerAccount(String username, String password) throws SQLException {

        Connection conn = ConnectionUtil.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT account_id FROM Account where username = ?");
        stmt.setString(1, username);
        ResultSet duplicateCheck = stmt.executeQuery();
        if (duplicateCheck.next()) // check if result has entries
            return null;


        logger.debug("Username and password passed safety checks ...");
        // no duplicates 
            
        PreparedStatement preppedStatement = conn
                .prepareStatement("INSERT INTO Account (username, password) VALUES (?, ?);");
        preppedStatement.setString(1, username);
        preppedStatement.setString(2, password);

        logger.debug("Attempting account creation ...");

        if (preppedStatement.executeUpdate() != 0)
            return getUserByLogin(username, password);

        logger.error("Account could not be created");
        return null;

    }

    public Account getUserByLogin(String username, String password) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();

        PreparedStatement preppedStatement = conn
                .prepareStatement("SELECT * FROM Account WHERE username = ? AND password = ?;");

        preppedStatement.setString(1, username);
        preppedStatement.setString(2, password);

        ResultSet res = preppedStatement.executeQuery();

        if (res.next()) {
            Account acc = new Account();

            acc.setAccount_id(res.getInt("account_id"));
            acc.setUsername(res.getString("username"));
            acc.setPassword(res.getString("password"));

            if (res.next()) {
                throw new SQLException("More than 1 account with this username & password");
            }

            return acc;
        }

        return null;
    }

}
