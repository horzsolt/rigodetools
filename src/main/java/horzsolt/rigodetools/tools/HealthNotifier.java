package horzsolt.rigodetools.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by horzsolt on 2017. 08. 19..
 */
@Component
public class HealthNotifier {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MailSender mailSender;

    @Autowired
    private StatusBean statusBean;

    @Scheduled(cron = "0 0 12 * * ?")
    public void sendNotificationEmail() {
        logger.debug("Email notification triggered...");
        mailSender.sendMail("Status notification", "Invocation count: " + statusBean.getInvokationCount() + ", Errors: " + statusBean.getNumberOfErrors() + ", Last run: " + statusBean.getLastRun());
    }
}
