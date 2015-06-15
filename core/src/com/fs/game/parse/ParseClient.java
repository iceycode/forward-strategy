package com.fs.game.parse;

import com.badlogic.gdx.Net;
import com.fs.game.constants.Network;

/** Client for creating, updating, retrieving, deleting data from Parse.com
 *
 * @author Allen
 */
public class ParseClient implements Net.HttpResponseListener{

    private static ParseClient instance;

    public static int PUT_USER = 0;
    public static int PUT_UNIT = 1;
    public static int GET_UNIT = 2;

    
    private final String APP_ID = Network.Parse.APP_ID;
    private final String REST_KEY = Network.Parse.REST_API_KEY;


    public ParseClient(){

    }




    @Override
    public void handleHttpResponse(Net.HttpResponse httpResponse) {

    }

    @Override
    public void failed(Throwable t) {

    }

    @Override
    public void cancelled() {

    }
}
