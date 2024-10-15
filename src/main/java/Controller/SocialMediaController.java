package Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {

    AccountService accService = new AccountService();
    MessageService mService = new MessageService();
    final static Logger logger = LoggerFactory.getLogger(SocialMediaController.class);

    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("register", this::registerPostHandler);
        app.post("login", this::loginPostHandler);
        app.post("messages", this::messagePostHandler);
        app.get("messages", this::messagesGetHandler);
        app.get("messages/{message_id}", this::messageGetByIdHandler);
        app.patch("messages/{message_id}", this::messageUpdateByIdHandler);
        app.delete("messages/{message_id}", this::messageDeleteByIdHandler);
        app.get("accounts/{account_id}/messages", this::messageGetByAccountId);

        return app;
    }

    private void registerPostHandler(Context ctx) throws JsonProcessingException {
        logger.debug("Registering account ...");

        ObjectMapper mapper = new ObjectMapper();
        Account acc = mapper.readValue(ctx.body(), Account.class);

        Account responseAccount = accService.registerAccount(acc);
        if (responseAccount == null) {
            ctx.status(HttpStatus.BAD_REQUEST);
            return;
        }
        ctx.json(mapper.writeValueAsString(responseAccount));

    }

    private void loginPostHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account acc = mapper.readValue(ctx.body(), Account.class);

        Account loginResponse = accService.getUserByLogin(acc);

        if (loginResponse == null) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            return;
        }

        ctx.json(mapper.writeValueAsString(loginResponse))
                .status(HttpStatus.OK);

    }

    private void messagePostHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        logger.debug("Message has text: \"%s\". Attempting to create ...", message.getMessage_text());
        Message loginResponse = mService.createMessage(message);

        if (loginResponse != null) {
            logger.debug("Message text: %s, user id: %d, time posted: %d %n",
                    loginResponse.getMessage_text(), loginResponse.getPosted_by(),
                    loginResponse.getTime_posted_epoch());

            ctx.json(mapper.writeValueAsString(loginResponse))
                    .status(HttpStatus.OK);
        }else
            ctx.status(HttpStatus.BAD_REQUEST);
        
    }

    private void messagesGetHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ctx.json(
                mapper.writeValueAsString(mService.getAllMessages()));
    }

    private void messageGetByIdHandler(Context ctx) {
        ObjectMapper mapper = new ObjectMapper();
        String id_string = ctx.pathParam("message_id");

        logger.debug("Getting messages from id %s %n", id_string);

        try {
            Message msg = mService.getMessageByID(Integer.parseInt(id_string));

            if (msg != null)
                ctx.json(mapper.writeValueAsString(msg)).status(200);
        } catch (Exception e) { // value is not an integer
            logger.error("Path parameter isn't an integer, " +
                    "so it can't be parsed. I send 400 to be more informative");

            ctx.status(HttpStatus.BAD_REQUEST);
        }

    }

    private void messageDeleteByIdHandler(Context ctx) {
        ObjectMapper mapper = new ObjectMapper();
        String id_string = ctx.pathParam("message_id");

        try {
            Message msg = mService.deleteMessageByID(Integer.parseInt(id_string));
            if (msg != null)
                ctx.json(mapper.writeValueAsString(msg)).status(200);
            else
                ctx.status(200);
        } catch (Exception e) { // value is not an integer
            logger.error("Path parameter isn't an integer, " +
                    "so it can't be parsed. I send 400 to be more informative");

            ctx.status(HttpStatus.BAD_REQUEST);
        }

    }

    private void messageUpdateByIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String id_string = ctx.pathParam("message_id");
        Message body = mapper.readValue(ctx.body(), Message.class);

        logger.info("Updating message to say \"%s\". %n", body);

        try {
            Message msg = mService.updateMessageByID(body.getMessage_text(), Integer.parseInt(id_string));
            if (msg != null)
                ctx.json(mapper.writeValueAsString(msg)).status(200);
            else
                ctx.status(400);
        } catch (Exception e) { // value is not an integer
            logger.error("Path parameter isn't an integer, " +
                    "so it can't be parsed. I send 400 to be more informative");

            ctx.status(HttpStatus.BAD_REQUEST);
        }

    }

    private void messageGetByAccountId(Context ctx) {
        ObjectMapper mapper = new ObjectMapper();
        String id_string = ctx.pathParam("account_id");
        try {
            List<Message> msg = mService.getMessagesByID(Integer.parseInt(id_string));
            if (msg != null)
                ctx.json(mapper.writeValueAsString(msg)).status(200);
            else 
                ctx.status(200);
        } catch (Exception e) { // value is not an integer
            logger.error("Path parameter isn't an integer, " +
                    "so it can't be parsed. I send 400 to be more informative");

            ctx.status(HttpStatus.BAD_REQUEST);
        }

    }

}