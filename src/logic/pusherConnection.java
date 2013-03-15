package logic;



import com.justinschultz.pusherclient.ChannelListener;
import com.justinschultz.pusherclient.Pusher;
import com.justinschultz.pusherclient.PusherListener;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

/**
 * Created with IntelliJ IDEA.
 * User: Snorre
 * Date: 03.02.13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
public class pusherConnection {
    PusherListener eventListener;
    Pusher pusher;
    Pusher.Channel channel;
    String channelname = "private-iaeste";
    String appKey = "1ccea1ec8d86fc9a7bcc";
    boolean recieved=false;


    public pusherConnection(String channel) {
        this.channelname = "private-"+channel;
        System.out.println("Creating connection");
        createListener();
        connect();

    }

    public void createListener() {
        eventListener = new PusherListener() {

            @Override
            public void onConnect(String s) {
                System.out.println("Connected "+s);
                String secret = "000eb329d11de4bc39f0";
                String to_sign = s+":"+channelname;
                String mac = null;
                try {
                    mac = generateSignature(secret, to_sign);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvalidKeyException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                System.out.println("MY MAC "+mac);
                channel = pusher.subscribe(channelname,appKey+":"+mac);
                JSONObject json = new JSONObject();
                try {
                    json.put("test", "123456789");
                    channel.send("client-test", new JSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                channel.bind("client-number", new ChannelListener() {
                    @Override
                    public void onMessage(String message) {
                        System.out.println("Received bound channel message: " + message);
                    }
                });

            }

            @Override
            public void onMessage(String s) {
                System.out.println("Received message from Pusher: " + s);
            }

            @Override
            public void onDisconnect() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    private String generateSignature(String keySecret, String string) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKey key = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
        Mac m = Mac.getInstance("HmacSHA256");
        m.init(key);
        byte[] mac = m.doFinal(string.getBytes());

        String signature = encodeHexString(mac);
        return  signature;
    }

    public void connect() {
        pusher = new Pusher(appKey);
        pusher.setPusherListener(eventListener);
        pusher.connect();
    }

    public void sendMessage(String message){
        if(getRecieved()){
        }
        else {
            try {
                System.out.println("sending message");
                JSONObject json = new JSONObject();
                json.put("number",message);

                channel.send("client-number", json);
                setRecieved(true);
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    public void setRecieved(boolean msg) {
        recieved = msg;
        final java.util.Timer tmr = new java.util.Timer();
        tmr.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                System.out.println("JUST TURNED SENDING ON AGAIN!!");
                recieved = false;
                tmr.cancel();
            }
        },4000,1000);
    }

    public boolean getRecieved(){
        return recieved;
    }


}
