package action;

import java.io.IOException;

public interface Action {
    void execute(String args) throws IOException;
}
