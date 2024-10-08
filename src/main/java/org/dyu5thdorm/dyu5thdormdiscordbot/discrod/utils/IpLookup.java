package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils;

import lombok.RequiredArgsConstructor;
import org.dyu5thdorm.dyu5thdormdiscordbot.DormWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class IpLookup {
    final
    DormWebClient webClient;

    @Value("${ip-lookup-direct}")
    String directIpLink;
    @Value("${ip-lookup-description}")
    String descriptionIpLink;

    public String getIp() throws IOException {
        return webClient.getPage(
                new URL(directIpLink)
        ).getWebResponse().getContentAsString();
    }
}
