package com.web.demo.service;


import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;
import java.util.Vector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;


/**
 * WebSocket business class
   * This type is used as a signaling server for RTC
 */
@Service
@ServerEndpoint("/websocketRTC")
public class WebSocketRTC {
    private static Vector<Session> sessions = new Vector<>();
    private static Vector<JSONObject> sessionProduce = new Vector<>();
    private static TreeMap<String,Session> sessionTreeMap = new TreeMap<>();
    private static int loginNumber = 0;
    private Session session ;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebSocketRTC.class);

    /**
           * Respond to a client WebSocket connection
     * @param session
     * @throws IOException
     */
    @OnOpen
    public void onopenproc(Session session) throws IOException {
        System.out.println("hava a client connected");
        this.session = session;

        JSONObject open = new JSONObject();

        open.put("status", "success");
        sendMessageToClient(open.toJSONString(), session);
    }

    /**
           * In response to a client's connection closed
     * @param session
     */
    @OnClose
    public void oncloseproc(Session session){
        System.out.println("had a client is disconneted");
//        sessionTreeMap.remove(data);

    }

    /**
           * Processing and response to client messages
           * @param message The message sent by the client
           * @param session WebSocket session object of the client
     * @throws IOException
     */
    @OnMessage
    public void onmessageproc(String message , Session session) throws IOException {
        /**
                   * The messaging between the signaling server and the client uses JSON
         */
        if(message!=null) {
            JSONObject msgJSON = JSON.parseObject(message);
/**
   * The type field in the message indicates the type of this message
   * The server handles targeted processing according to the type of the message
 */
            switch (msgJSON.getString("type")) {
                case "login" :{
                    /**
                                           * Process client login
                     */
                    log.info("session : "+session + "is login .. "+new Date());
                    log.info("user login in as "+msgJSON.getString("name"));
//                    if (sessionTreeMap.containsKey(msgJSON.getString("name"))) {
//                        JSONObject login = new JSONObject();
//                        login.put("type", "login");
//                        login.put("success", false);
//                        sendMessageToClient(login.toJSONString() , session);
//
//                    }else {
                        sessionTreeMap.put(msgJSON.getString("name"), session);
                        JSONObject login = new JSONObject();
                        login.put("type", "login");
                        login.put("success", true);
                        login.put("myName", msgJSON.getString("name"));
                        sendMessageToClient(login.toJSONString() , session);
                    //}

                }break;
                case "offer": {
                    /**
                                           * Processing offer messages
                                           * Offer is the first step in a peer to peer connection
                                           * This is a message in response to the call initiator
                                           * Here is mainly to find the conversation of the other party that the call initiator wants to talk to
                     */
//                    onOffer(data.offer, data.name);\
                    log.info("Sending offer to " + msgJSON.getString("name")+" from "+msgJSON.getString("myName"));

                    Session conn = sessionTreeMap.get(msgJSON.getString("name"));

                    if (conn != null) {
                        JSONObject offer = new JSONObject();
                        offer.put("type", "offer");
                        offer.put("offer", msgJSON.getString("offer"));
                        offer.put("name", msgJSON.getString("name"));
                        sendMessageToClient(offer.toJSONString(), conn);

                        /**
                                                   * Save session state
                         */
                        JSONObject offerAnswer = new JSONObject();
                        offerAnswer.put("offerName", msgJSON.getString("myName"));
                        offerAnswer.put("answerName", msgJSON.getString("name"));

                        JSONObject sessionTemp = new JSONObject();
                        sessionTemp.put("session", offerAnswer);
                        sessionTemp.put("type", "offer");

                        sessionProduce.add(sessionTemp);
                    }
                    else {
                    	log.info(msgJSON.getString("name")+" has not connected yet");
                    }

                }
                    break;
                case "answer": {
/**
   * In response to answer messages
   * answer is the client's reply to the person who initiated the call
 */
                    log.info("answer ..." + sessionProduce.size());

                    for (int i = 0; i < sessionProduce.size(); i++) {
                        log.info(sessionProduce.get(i).toJSONString());
                    }

                    if (true) {
                        Session conn = null;
                        /**
                                                   * Save session state
                                                   * Check who should receive Anser news
                         */

                        for (int ii = 0; ii < sessionProduce.size(); ii++) {
                            JSONObject i = sessionProduce.get(ii);
                            JSONObject sessionJson = i.getJSONObject("session");
                            log.info(msgJSON.toJSONString());
                            log.info(sessionJson.toJSONString());

                            log.info("myName is " + msgJSON.getString("myName") + "   , answer to name " + sessionJson.getString("answerName"));
                            if (/*i.getString("offerName").equals(msgJSON.getString("name")) && */sessionJson.getString("answerName").equals(msgJSON.getString("myName"))) {
                                conn = sessionTreeMap.get(sessionJson.getString("offerName"));
                                log.info("Sending answer to " + sessionJson.getString("offerName") + " from " + msgJSON.getString("myName"));

                                sessionProduce.remove(ii);
                            }
                        }

                        JSONObject answer = new JSONObject();
                        answer.put("type", "answer");
                        answer.put("answer", msgJSON.getString("answer"));
                        sendMessageToClient(answer.toJSONString(),conn);



                    }
                }
                    break;
                case "candidate": {
                    /**
                                           * This is the processing of candidate connections
                                           * This message processing may happen multiple times in a call
                     */
                    log.info("Sending candidate to "+msgJSON.getString("name"));
                    Session conn = sessionTreeMap.get(msgJSON.getString("name"));
                    if (conn != null) {
                        JSONObject candidate = new JSONObject();
                        candidate.put("type", "candidate");
                        candidate.put("candidate", msgJSON.getString("candidate"));
                        sendMessageToClient(candidate.toJSONString(),conn );
                    }
                }
                    break;
                case "leave":{
                    /**
                                           * This message is an event that handles ending the call
                     */
                    log.info("Disconnectiong user from " + msgJSON.getString(" name"));
                    Session conn = sessionTreeMap.get(msgJSON.getString("name"));

                    if (conn != null) {
                        JSONObject leave = new JSONObject();
                        leave.put("type", "leave");

                        sendMessageToClient(leave.toJSONString(),conn);
                    }
                }

                    break;
                default:
                    JSONObject defaultMsg = new JSONObject();
                    defaultMsg.put("type", "error");
                    defaultMsg.put("message", "Unreconfized command : "+ msgJSON.getString("type") );
                    sendMessageToClient(defaultMsg.toJSONString(),session);
                    break;
            }
            System.out.println(message);
        }
    }

    /**
           * Send a message 
     * @param msg
     * @throws IOException
     */
    public void sendMessage(String msg) throws IOException {
        if(this.session!=null)
        this.session.getBasicRemote().sendText("hello everyone!");
        this.session.getBasicRemote().sendText(msg);
    }

    public void sendMessageForAllClient(String msg){
        if(!sessions.isEmpty()){
            sessions.forEach(i->{
                try {
                    if(i.isOpen()) {
                        i.getBasicRemote().sendText(msg+" : "+new Date().toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
           * Send a message to the specified client
     * @param msg
     * @param session
     * @throws IOException
     */
    public void sendMessageToClient(String msg , Session session) throws IOException {
        if(session.isOpen())
        session.getBasicRemote().sendText(msg);
    }
}


