package com.captaindroid.gsmarena.scrapper.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static HashMap getHeaders(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "en-US,en;q=0.9,bn;q=0.8");
        headers.put("Cache-Control", "max-age=0");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", "__unid=cff369ee-cf76-a365-6ae6-e343841ed4e0; euconsent-v2=CPZrfwAPZrfwABEACBENCRCoAP_AAH_AAAIwIutf_X__b3_n-_7___t0eY1f9_7__-0zjhfdt-8N3f_X_L8X_2M7vF36tr4KuR4ku3bBIQdtHOncTUmx6olVrzPsbk2cr7NKJ7Pkmnsbe2dYGH9_n93T_ZKZ7______7________________________-_____9_____________________________4Iutf_X__b3_n-_7___t0eY1f9_7__-0zjhfdt-8N3f_X_L8X_2M7vF36tr4KuR4ku3bBIQdtHOncTUmx6olVrzPsbk2cr7NKJ7Pkmnsbe2dYGH9_n93T_ZKZ7______7________________________-_____9_____________________________4AA; _pbjs_userid_consent_data=6683316680106290; sharedid=f96fa6be-99ad-42ec-8c3e-6a9784471093; _lr_env_src_ats=false; networkVisible=1; lpe=271; keyw=Vodafone");
        headers.put("dnt", "1");
        headers.put("Host", "www.gsmarena.com");
        headers.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"102\", \"Google Chrome\";v=\"102\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("sec-ch-ua-platform", "Linux");
        headers.put("Sec-Fetch-Dest", "document");
        headers.put("Sec-Fetch-Mode", "navigate");
        headers.put("Sec-Fetch-Site", "none");
        headers.put("Sec-Fetch-User", "?1");
        headers.put("Upgrade-Insecure-Requests", "1");
        return headers;
    }
}
