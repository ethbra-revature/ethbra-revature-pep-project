package Service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    MessageDAO dao = new MessageDAO();

    public Message createMessage(Message message) {
        if (message.getMessage_text().length() == 0 | message.getMessage_text().length() > 255) {
            logger.error("Message either has no characters is too long");
            return null;
        }

        try {
            return dao.createMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Message deleteMessageByID(int id) {
        try {
            return dao.deleteMessageByID(id);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getAllMessages() {
        try {
            return dao.getAllMessages();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Message>();
        }
    }

    public Message getMessageByID(int id) {
        try {
            return dao.getMessageByID(id);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getMessagesByID(int id) {
        try {
            return dao.getMessagesByID(id);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Message updateMessageByID(String message, int id) {
        if (message.length() > 255 | message.length() == 0) 
            return null;
        try {
            return dao.updateMessageByID(message, id);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
