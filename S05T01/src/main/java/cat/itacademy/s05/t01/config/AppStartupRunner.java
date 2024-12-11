package cat.itacademy.s05.t01.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements ApplicationRunner {

    @Autowired
    private MySqlDatabaseConfig mySqlDatabaseConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mySqlDatabaseConfig.initializeDatabase().subscribe();
    }

}
