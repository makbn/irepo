package io.github.makbn.irepo.service;

import io.github.makbn.irepo.model.IPA;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

@Service
public class PlistService {

    private final String plistTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "    <dict>\n" +
            "        <key>items</key>\n" +
            "        <array>\n" +
            "            <dict>\n" +
            "                <key>assets</key>\n" +
            "                <array>\n" +
            "                    <dict>\n" +
            "                        <key>kind</key>\n" +
            "                        <string>software-package</string>\n" +
            "                        <key>url</key>\n" +
            "                        <string>${{IPA_URL}}</string>\n" +
            "                    </dict>\n" +
            "                    <dict>\n" +
            "                        <key>kind</key>\n" +
            "                        <string>display-image</string>\n" +
            "                        <key>needs-shine</key>\n" +
            "                        <true/>\n" +
            "                        <key>url</key>\n" +
            "                        <string>${{IPA_ICON}}</string>\n" +
            "                    </dict>\n" +
            "                </array>\n" +
            "                <key>metadata</key>\n" +
            "                <dict>\n" +
            "                    <key>bundle-identifier</key>\n" +
            "                    <string>${{BUNDLE_ID}}</string>\n" +
            "                    <key>bundle-version</key>\n" +
            "                    <string>${{BUNDLE_VERSION}}</string>\n" +
            "                    <key>kind</key>\n" +
            "                    <string>${{IPA_KIND}}</string>\n" +
            "                    <key>title</key>\n" +
            "                    <string>${{IPA_TITLE}}</string>\n" +
            "                </dict>\n" +
            "            </dict>\n" +
            "        </array>\n" +
            "    </dict>\n" +
            "</plist>\n";

    private final String IPA_TITLE_KEY = "${{IPA_TITLE}}";
    private final String IPA_URL_KEY = "${{IPA_URL}}";
    private final String IPA_ICON_KEY = "${{IPA_ICON}}";
    private final String IPA_KIND_KEY = "${{IPA_KIND}}";
    private final String IPA_BI_KEY = "${{BUNDLE_ID}}";
    private final String IPA_BV_KEY = "${{BUNDLE_VERSION}}";
    private final String FILE_POSTFIX = "plist";

    @Value("${application.host}")
    private String host;

    public File getPlistFile(IPA ipa) throws IOException {
        File plist = File.createTempFile(ipa.getUuid().toLowerCase(), FILE_POSTFIX);

        String plistContent = plistTemplate.replace(IPA_TITLE_KEY, ipa.getTitle())
                .replace(IPA_URL_KEY, host+ "/v1/"+ipa.getUuid()+"/download")
                .replace(IPA_ICON_KEY,host+ "/v1/"+ipa.getUuid()+"/icon")
                .replace(IPA_KIND_KEY, ipa.getIpaInfo().getKind().name())
                .replace(IPA_BI_KEY, ipa.getIpaInfo().getBundleIdentifier())
                .replace(IPA_BV_KEY, String.valueOf(ipa.getIpaInfo().getBundleVersion()));

        FileUtils.writeStringToFile(plist, plistContent, Charset.defaultCharset());

        return plist;
    }

}
