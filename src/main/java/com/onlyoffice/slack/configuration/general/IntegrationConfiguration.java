package com.onlyoffice.slack.configuration.general;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "onlyoffice.application")
@Getter
@Setter
public class IntegrationConfiguration {
    @Value("${onlyoffice.application.file.sizeLimit:3.5}") private float fileSizeLimitMb;

    @Value("${onlyoffice.application.cache.bots:30}") private int botsSizeMb;
    @Value("${onlyoffice.application.cache.users:80}") private int usersSizeMb;
    @Value("${onlyoffice.application.cache.workspaces:30}") private int workspaceSizeMb;

    @Value("${onlyoffice.application.resources.icons.docx}") private String docxIcon;
    @Value("${onlyoffice.application.resources.icons.xlsx}") private String xlsxIcon;
    @Value("${onlyoffice.application.resources.icons.pptx}") private String pptxIcon;
    @Value("${onlyoffice.application.resources.icons.word}") private String wordIcon;
    @Value("${onlyoffice.application.resources.icons.cell}") private String cellIcon;
    @Value("${onlyoffice.application.resources.icons.slide}") private String slideIcon;
    @Value("${onlyoffice.application.resources.icons.generic}") private String genericIcon;

    private String aesSecret;
    private String editorSecret;
    private String callbackSecret;

    private String installUrl;
    private String editorUrl;
    private String callbackUrl;
}
