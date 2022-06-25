package myefe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class mainMyefe {
    mainMyefe() throws SQLException {
        Separation separation = new Separation(200);
        List<ThreadMyefe> threadMyefeList = new ArrayList<>();
        ThreadMyefe threadMyefe;
        for (int i = 0; i < 5; i++) {   // Количество потоков
            threadMyefeList.add(new ThreadMyefe("test" + i, separation));
        }
        for (int i = 0; i < threadMyefeList.size(); i++) {
            threadMyefeList.get(i).start();
        }
    }
}
