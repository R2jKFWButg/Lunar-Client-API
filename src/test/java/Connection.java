import com.ghaph.LunarClient;
import com.ghaph.auth.LunarSession;
import com.ghaph.protocol.packets.client.CPacketPlayerInfoRequest;
import com.ghaph.protocol.user.ClientVersion;

import java.util.UUID;

public class Connection {

    public static void main(String[] args) {
        LunarClient client = new LunarClient(new LunarSession("email", "password", ClientVersion.V1_8));
        client.getLunarSettings().setDebug(true);
        //client.getLunarSettings().setWebsocketUrl("ws://127.0.0.1:8082/connect");
        client.connect();

        client.waitForConnect();

        client.getSocket().sendPacket(new CPacketPlayerInfoRequest(
                UUID.fromString("930b0bf6-71ba-4634-8f24-05904e14e263")
        ));
    }

}
