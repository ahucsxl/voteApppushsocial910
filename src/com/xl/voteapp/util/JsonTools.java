package com.xl.voteapp.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class JsonTools {
    
    /*
     * Function��:   ����JSON�ַ���
     * Param ����:��  value     ��Ҫת����JSON�ַ�����Object����
     * Retuen����:   JSON�ַ���
     * Author����:   ����԰-���ɵ�Ȼ
     */
    public static String createJsonString(Object value) {
        Gson gson = new Gson();
        String string = gson.toJson(value);
        return string;
    }
    

    
}