package Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    final static Logger logger = LoggerFactory.getLogger(AccountService.class);

    private AccountDAO dao = new AccountDAO();

    public Account getUserByLogin(Account acc) {
        try {
            return dao.getUserByLogin(acc.getUsername(), acc.getPassword());
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    };

    public Account registerAccount(Account acc) {
        if (acc.getPassword().length() < 4 | acc.getUsername().length() == 0)
            return null;    // make sure valid account strings

        try {
            logger.debug("Account service querying DAO ...");
            return dao.registerAccount(acc.getUsername(), acc.getPassword());
        } catch (Exception e) {
            logger.error("Dao returned exception");
            e.printStackTrace();
            return null;
        }
    }


}
