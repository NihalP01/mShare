package com.mridx.share.callback;

import java.io.IOException;

public interface SenderCallback {

    void setOnSenderCallback(boolean connected, IOException error);

    void setOnFileTransferCallback();
}
