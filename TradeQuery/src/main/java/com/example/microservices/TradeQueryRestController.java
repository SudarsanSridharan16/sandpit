package com.example.microservices;


import com.example.projects.domain.Trade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by oliverbuckley-salmon on 17/08/2016.
 */
@RestController
public class TradeQueryRestController {

    //@RequestMapping(value = "/queryById", method = "GET")
    //@RequestParam(value="tradeId")
    public Trade queryTradebyId(String tradeId){
        return null;
    }
}
