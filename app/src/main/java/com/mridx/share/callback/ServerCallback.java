package com.mridx.share.callback;

import java.io.IOException;

public interface ServerCallback {

    void setOnServerCallback(boolean success, IOException error);

}
