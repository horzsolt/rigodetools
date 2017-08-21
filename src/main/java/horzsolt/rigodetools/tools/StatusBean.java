package horzsolt.rigodetools.tools;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by horzsolt on 2017. 08. 19..
 */
@Component
public class StatusBean {

    private int invokationCount;
    private int numberOfErrors;
    private LocalDateTime lastRun;
    private LocalDateTime lastMp3Run;

    public int getInvokationCount() {
        return invokationCount;
    }

    public synchronized void incInvokationCount() {
        this.invokationCount = ++invokationCount;
    }
    public synchronized void incNumberOfErrors() {
        this.numberOfErrors = ++numberOfErrors;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }


    public LocalDateTime getLastRun() {
        return lastRun;
    }

    public synchronized void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }

    public LocalDateTime getLastMp3Run() {
        return lastMp3Run;
    }

    public void setLastMp3Run(LocalDateTime lastMp3Run) {
        this.lastMp3Run = lastMp3Run;
    }
}
