package io.nkdtrdr.mrktmkr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class TerminateBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminateBean.class);

    @PreDestroy
    public void onTerminate() {
        LOGGER.info("Terminating MrktMkr");
    }
}
